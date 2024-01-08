/*
 * Copyright 2023 Ant Group CO., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.reasoner.lube.logical.optimizer.rules

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.trees.{BottomUp, TopDown}
import com.antgroup.openspg.reasoner.lube.common.expr._
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern
import com.antgroup.openspg.reasoner.lube.logical._
import com.antgroup.openspg.reasoner.lube.logical.operators.{Filter, _}
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, Rule, Up}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext
import com.antgroup.openspg.reasoner.lube.utils.{ExprUtils, RuleUtils}

/**
 * Aggregation push down
 */
object AggregatePushDown extends Rule {

  override def rule(implicit
      context: LogicalPlannerContext): PartialFunction[LogicalOperator, LogicalOperator] = {
    case aggregate: Aggregate =>
      val multiVersionEnable = {
        val config = context.params
          .getOrElse(Constants.SPG_REASONER_MULTI_VERSION_ENABLE, "false")
        "true".equalsIgnoreCase(String.valueOf(config))
      }
      if (!canPushDown(aggregate)) {
        aggregate
      } else {
        pushDown(aggregate, multiVersionEnable)
      }
  }

  /**
   * only push the first aggregate down to graph pattern matching
   * @param aggregate
   * @return
   */
  private def canPushDown(logicalOperator: LogicalOperator): Boolean = {
    val aggCnt = getAggregateNum(logicalOperator)
    if (aggCnt == 1) {
      true
    } else {
      false
    }
  }

  private def getAggregateNum(logicalOperator: LogicalOperator): Int = {
    val aggCnt = logicalOperator.transform[Int] {
      case (aggregate: Aggregate, list) => list.sum + 1
      case (_, list) =>
        if (list.isEmpty) {
          0
        } else {
          list.head
        }
    }
    aggCnt
  }

  /**
   * aggregate push down
   *
   * @param aggregate
   * @return
   */
  private def pushDown(aggregate: Aggregate, multiVersionEnable: Boolean): LogicalOperator = {
    val allAgg =
      extendAgg(aggregate.group, aggregate.aggregations, multiVersionEnable, aggregate.solved)
    val useFields = getUsedField(aggregate)
    var preRoot: String = null

    def rewriter: PartialFunction[LogicalOperator, LogicalOperator] = {
      case expandInto: ExpandInto =>
        val root = expandInto.pattern.root.alias
        val inOutput = expandInto.in.fields.map(_.name).toSet
        val useless = inOutput
          .filter(allAgg.keySet.contains(_))
          .diff(useFields(root))
          .diff(Set.apply(preRoot))
        preRoot = root
        if (!useless.isEmpty) {
          // agg
          val aggregations = new mutable.HashMap[Var, Aggregator]()
          for (alias <- useless) {
            if (allAgg.get(alias).isDefined) {
              aggregations.++=(allAgg(alias))
              allAgg.remove(alias)
            }
          }
          // partial aggregate, eg. first(E)
          val partialAggregations = aggregations
            .filter(p =>
              p._1.isInstanceOf[EdgeVar] &&
                p._2.isInstanceOf[AggOpExpr] && p._2
                  .asInstanceOf[AggOpExpr]
                  .name
                  .isInstanceOf[First.type])
          val fullAggregations = aggregations.filter(p => !partialAggregations.contains(p._1))
          var newRoot = expandInto
          if (!partialAggregations.isEmpty) {
            val groups = getPartialGroupVar(expandInto.in)
            val aggOp =
              Aggregate(expandInto.in, groups, partialAggregations.toMap, expandInto.in.solved)
            newRoot = expandInto.copy(in = aggOp)
          }
          if (!fullAggregations.isEmpty) {
            val groups = getGroupVar(expandInto.in, aggregate.group)
            val aggOp =
              Aggregate(newRoot.in, groups, fullAggregations.toMap, newRoot.in.solved)
            newRoot = newRoot.copy(in = aggOp)
          }
          newRoot
        } else {
          expandInto
        }
      case patternScan: PatternScan =>
        preRoot = patternScan.pattern.root.alias
        patternScan
    }

    var newRoot = BottomUp[LogicalOperator](rewriter).transform(aggregate).asInstanceOf[Aggregate]
    if (!allAgg.isEmpty) {
      val edgeAggregations = allAgg.values.flatten.toMap.filter(e =>
        e._1.isInstanceOf[EdgeVar] || e._1.isInstanceOf[RepeatPathVar] || e._1
          .isInstanceOf[PathVar])
      val nodeAggregations =
        allAgg.values.flatten.toMap.filter(e => !edgeAggregations.contains(e._1))
      if (!edgeAggregations.isEmpty && !nodeAggregations.isEmpty) {
        newRoot = newRoot.copy(in = newRoot.in, aggregations = edgeAggregations)
        newRoot = newRoot.copy(in = newRoot, aggregations = nodeAggregations)
      } else if (nodeAggregations.isEmpty) {
        newRoot = newRoot.copy(in = newRoot.in, aggregations = edgeAggregations)
      } else {
        newRoot = newRoot.copy(in = newRoot.in, aggregations = nodeAggregations)
      }
      newRoot
    } else {
      newRoot.in
    }
  }

  private def getUsedField(aggregate: Aggregate): Map[String, Set[String]] = {
    val usedFieldMap = new mutable.HashMap[String, Set[String]]()
    val useFields = new mutable.HashSet[String]()
    def rewriter: PartialFunction[LogicalOperator, LogicalOperator] = {
      case expandInto: ExpandInto =>
        val root = expandInto.pattern.root.alias
        useFields.add(root)
        val refFields = expandInto.refFields.map(_.name).toSet
        val aggUsedFields = getAggUsedFields(aggregate)
        useFields.++=(refFields.union(aggUsedFields))
        usedFieldMap.put(root, useFields.toSet)
        expandInto
      case filter: Filter =>
        useFields.++=(
          RuleUtils.getAllInputFieldInRule(filter.rule, Set.empty, Set.empty).map(_.name))
        filter
      case project: Project =>
        for (expr <- project.expr.values) {
          useFields.++=(ExprUtils.getRefVariableByExpr(expr))
        }
        project
    }
    TopDown[LogicalOperator](rewriter).transform(aggregate).asInstanceOf[Aggregate]
    usedFieldMap.toMap
  }

  private def getAggUsedFields(aggregate: Aggregate) = {
    val useFields = new mutable.HashSet[String]()
    useFields.++=(aggregate.group.map(_.name))
    for (pair <- aggregate.aggregations) {
      pair._2 match {
        case AggOpExpr(name, expr) =>
          if (!name.isInstanceOf[First.type]) {
            useFields.++=(ExprUtils.getAllInputFieldInRule(expr, null, null).map(_.name))
          }
        case AggIfOpExpr(aggOpExpr, condition) =>
          useFields.++=(
            ExprUtils.getAllInputFieldInRule(aggOpExpr.aggEleExpr, null, null).map(_.name))
          useFields.++=(ExprUtils.getAllInputFieldInRule(condition, null, null).map(_.name))
        case _ =>
      }
    }
    useFields.toSet
  }

  private def getGroupVar(logicalOperator: LogicalOperator, groups: List[Var]): List[Var] = {
    var curRoot: String = null
    val roots = logicalOperator
      .transform[Set[Var]] {
        case (expandInto: ExpandInto, list) =>
          curRoot = expandInto.pattern.root.alias
          list.head ++ getVar(expandInto.pattern)
        case (scan: PatternScan, _) =>
          curRoot = scan.pattern.root.alias
          getVar(scan.pattern)
        case (_, list) =>
          if (list.isEmpty) {
            Set.empty
          } else {
            list.head
          }
      }
    val groupAlias = groups.map(_.name)
    if (groupAlias.contains(curRoot)) {
      val groupVar = roots.filter(r => groupAlias.contains(r.name)).toList
      groupVar
    } else {
      val groupVar = roots.filter(r => groupAlias.contains(r.name)).toList
      groupVar :+ NodeVar(curRoot, Set.empty)
    }
  }

  private def getPartialGroupVar(logicalOperator: LogicalOperator): List[Var] = {
    logicalOperator
      .transform[Set[Var]] {
        case (expandInto: ExpandInto, list) =>
          list.head ++ getVar(expandInto.pattern)
        case (scan: PatternScan, _) =>
          getVar(scan.pattern)
        case (_, list) =>
          if (list.isEmpty) {
            Set.empty
          } else {
            list.head
          }
      }
      .toList
  }

  private def getVar(pattern: Pattern): Set[Var] = {
    val list = new mutable.HashSet[String]
    list.add(pattern.root.alias)
    pattern.topology.values.flatten.foreach(conn => {
      list.add(conn.source)
      list.add(conn.target)
    })
    list.map(NodeVar(_, Set.empty)).toSet
  }

  private def extendAgg(
      groups: List[Var],
      aggregations: Map[Var, Aggregator],
      multiVersionEnable: Boolean,
      solved: SolvedModel) = {
    val allAggregations = new mutable.HashMap[String, mutable.HashMap[Var, Aggregator]]
    for (aggOp <- aggregations) {
      val alias = ExprUtils.getRefVariableByExpr(aggOp._2).head
      if (!allAggregations.contains(alias)) {
        allAggregations.put(alias, new mutable.HashMap[Var, Aggregator]())
      }
      allAggregations(alias).put(aggOp._1, aggOp._2)
    }
    val groupAlias = groups.map(_.name).toSet
    val aggAlias = allAggregations.keySet
    for (tuple <- solved.fields) {
      if (!aggAlias.contains(tuple._1) && !groupAlias.contains(tuple._1)) {
        if (!allAggregations.contains(tuple._1)) {
          allAggregations.put(tuple._1, new mutable.HashMap[Var, Aggregator]())
        }
        tuple._2 match {
          case NodeVar(name, _) =>
            allAggregations(tuple._1)
              .put(NodeVar(name, Set.empty), AggOpExpr(First, Ref(tuple._1)))
          case EdgeVar(name, _) =>
            // if (multiVersionEnable || solved.getTypes(name).size > 1) {
            // multiple type of edge not aggregate by default
            if (multiVersionEnable) {
              allAggregations(tuple._1)
                .put(EdgeVar(name, Set.empty), AggOpExpr(First, Ref(tuple._1)))
            }
          case _ =>
        }
      }
    }
    allAggregations.filter(!_._2.isEmpty)
  }

  override def direction: Direction = Up

  override def maxIterations: Int = 1
}
