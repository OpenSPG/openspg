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

import com.antgroup.openspg.reasoner.common.trees.BottomUp
import com.antgroup.openspg.reasoner.lube.common.graph.{IREdge, IRNode}
import com.antgroup.openspg.reasoner.lube.common.pattern.{
  EdgePattern,
  NodePattern,
  PartialGraphPattern,
  Pattern
}
import com.antgroup.openspg.reasoner.lube.logical.{EdgeVar, NodeVar, Var}
import com.antgroup.openspg.reasoner.lube.logical.PatternOps.PatternOps
import com.antgroup.openspg.reasoner.lube.logical.operators._
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, Rule, Up}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext
import com.antgroup.openspg.reasoner.lube.utils.RuleUtils
import org.apache.commons.lang3.StringUtils

/**
 * Predicate push down
 */
object FilterPushDown extends Rule {

  override def rule(implicit
      context: LogicalPlannerContext): PartialFunction[LogicalOperator, LogicalOperator] = {
    case filter: Filter =>
      val fields = RuleUtils.getAllInputFieldInRule(filter.rule, null, null)
      val propertyMap = new mutable.HashMap[String, Set[String]]
      for (field <- fields) {
        field match {
          case IREdge(name, fields) => propertyMap.put(name, fields.toSet)
          case IRNode(name, fields) => propertyMap.put(name, fields.toSet)
          case _ =>
        }
      }
      val res = pushDown2Pattern(filter, propertyMap.toMap)
      if (res._1) {
        res._2
      } else {
        pushDown(filter, propertyMap.toMap)
      }
  }

  private def pushDown(filter: Filter, aliasMap: Map[String, Set[String]]): LogicalOperator = {
    var hasPushDown = false
    def rewriter: PartialFunction[LogicalOperator, LogicalOperator] = {
      case logicalOp: StackingLogicalOperator =>
        val outputFields = logicalOp.fields.filter(!_.isEmpty)
        if (diff(aliasMap, varToMap(outputFields)) && !hasPushDown) {
          hasPushDown = true
          filter.copy(in = logicalOp)
        } else {
          logicalOp
        }
    }

    val newRoot = BottomUp[LogicalOperator](rewriter).transform(filter).asInstanceOf[Filter]
    if (hasPushDown) {
      newRoot.in
    } else {
      newRoot
    }
  }

  private def pushDown2Pattern(
      filter: Filter,
      aliasMap: Map[String, Set[String]]): (Boolean, LogicalOperator) = {
    var hasPushDown: Boolean = false

    def rewriter: PartialFunction[LogicalOperator, LogicalOperator] = {
      case expandInto: ExpandInto =>
        val res = fillInRule(filter.rule, aliasMap, expandInto.pattern, expandInto.refFields)
        if (res._1) {
          hasPushDown = true
          expandInto.copy(pattern = res._2)
        } else {
          expandInto
        }
      case patternScan: PatternScan =>
        val res = fillInRule(filter.rule, aliasMap, patternScan.pattern, patternScan.refFields)
        if (res._1) {
          hasPushDown = true
          patternScan.copy(pattern = res._2)
        } else {
          patternScan
        }
    }

    val newRoot = BottomUp[LogicalOperator](rewriter).transform(filter).asInstanceOf[Filter]
    if (hasPushDown) {
      (hasPushDown, newRoot.in)
    } else {
      (hasPushDown, newRoot)
    }
  }

  private def fillInRule(
      filterRule: com.antgroup.openspg.reasoner.lube.common.rule.Rule,
      aliasMap: Map[String, Set[String]],
      pattern: Pattern,
      refFields: List[Var]): (Boolean, Pattern) = {
    var pushToAlias = ""
    pattern match {
      case NodePattern(node) =>
        if (diff(aliasMap, varToMap(refFields))) {
          pushToAlias = node.alias
        }
      case EdgePattern(_, _, edge) =>
        if (diff(aliasMap, varToMap(refFields))) {
          pushToAlias = edge.alias
        }
      case PartialGraphPattern(rootAlias, _, edges) =>
        if (diff(aliasMap, varToMap(filterVars(refFields, Set.apply(rootAlias))))) {
          pushToAlias = rootAlias
        }
        val edgeSet = edges.values.flatten
        for (e <- edgeSet) {
          if (StringUtils.isBlank(pushToAlias) && diff(
              aliasMap,
              varToMap(filterVars(refFields, Set.apply(e.source, e.alias))))) {
            pushToAlias = e.alias
          }
        }
      case _ =>
    }
    if (StringUtils.isNotBlank(pushToAlias)) {
      (true, pattern.fillInRule(filterRule, pushToAlias))
    } else {
      (false, pattern)
    }
  }

  private def diff(left: Map[String, Set[String]], right: Map[String, Set[String]]): Boolean = {
    for (pair <- left) {
      if (!right.contains(pair._1)) {
        return false
      } else if (!pair._2.diff(right(pair._1)).isEmpty) {
        return false
      }
    }
    true
  }

  private def varToMap(fields: List[Var]): Map[String, Set[String]] = {
    val map = new mutable.HashMap[String, Set[String]]
    for (field <- fields) {
      field match {
        case NodeVar(name, props) => map.put(name, props.filter(_.resolved).map(_.name))
        case EdgeVar(name, props) => map.put(name, props.filter(_.resolved).map(_.name))
        case _ =>
      }
    }
    map.toMap
  }

  private def filterVars(fields: List[Var], choose: Set[String]): List[Var] = {
    fields.filter(v => choose.contains(v.name))
  }

  override def direction: Direction = Up

  override def maxIterations: Int = 1
}
