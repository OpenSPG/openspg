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

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.trees.TopDown
import com.antgroup.openspg.reasoner.lube.common.expr.{Filter => _, _}
import com.antgroup.openspg.reasoner.lube.common.pattern._
import com.antgroup.openspg.reasoner.lube.common.rule.LogicRule
import com.antgroup.openspg.reasoner.lube.logical.PatternOps.PatternOps
import com.antgroup.openspg.reasoner.lube.logical.operators._
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, Rule, Up}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext
import org.apache.commons.lang3.StringUtils



/**
 * Predicate push down
 */
object IdFilterPushDown extends Rule {

  override def rule(implicit
      context: LogicalPlannerContext): PartialFunction[LogicalOperator, LogicalOperator] = {
    case filter: Filter => idFilterPushDown(filter)
  }

  private def idFilterPushDown(filter: Filter): LogicalOperator = {
    var hasPushDown: Boolean = false
    val updatedRule = filter.rule match {
      case rule: LogicRule =>
        rule.getExpr match {
          case BinaryOpExpr(opName,
                UnaryOpExpr(GetField(Constants.NODE_ID_KEY), Ref(refName)),
                r) =>
            opName match {
              case BIn => r match {
                case _ => (refName, r, BIn)
              }
              case BEqual => r match {
                case _ => (refName, r, BEqual)
              }
              case _ => null
            }
          case _ => null
        }
      case _ => null
    }
    if (updatedRule == null) {
      return filter
    }

    var boundedVarLenExpandEdgeNameSet: Set[String] = Set.empty
    def rewriter: PartialFunction[LogicalOperator, LogicalOperator] = {
      case boundedVarLenExpand: BoundedVarLenExpand =>
        boundedVarLenExpandEdgeNameSet += boundedVarLenExpand.edgePattern.edge.alias
        boundedVarLenExpandEdgeNameSet += boundedVarLenExpand.edgePattern.dst.alias
        boundedVarLenExpandEdgeNameSet += boundedVarLenExpand.edgePattern.src.alias
        boundedVarLenExpand
      case expandInto: ExpandInto =>
        val res = updatePatternFilterRule(expandInto.pattern, updatedRule,
          null, boundedVarLenExpandEdgeNameSet)
        if (res._1) {
          hasPushDown = true
          expandInto.copy(pattern = res._2)
        } else {
          expandInto
        }
      case patternScan: PatternScan =>
        val res = updatePatternFilterRule(patternScan.pattern, updatedRule,
          patternScan.pattern.root.alias, boundedVarLenExpandEdgeNameSet)
        if (res._1) {
          hasPushDown = true
          patternScan.copy(pattern = res._2)
        } else {
          patternScan
        }
    }
    val newFilter = TopDown[LogicalOperator](rewriter).transform(filter).asInstanceOf[Filter]
    if (hasPushDown) {
      newFilter.in
    } else {
      filter
    }
  }

  private def fillInRule(
      filterRule: com.antgroup.openspg.reasoner.lube.common.rule.Rule,
      alias: String,
      pattern: Pattern, boundedVarLenExpandEdgeNameSet: Set[String]): (Boolean, Pattern) = {
    var pushToAlias = ""
    if (!boundedVarLenExpandEdgeNameSet.contains(alias)) {
      pattern match {
        case NodePattern(node) =>
          if (node.alias.equals(alias)) {
            pushToAlias = node.alias
          }
        case EdgePattern(_, _, edge) =>
          if (edge.alias.equals(alias)) {
            pushToAlias = edge.alias
          }
        case PartialGraphPattern(_, nodes, edges) =>
          if (nodes.contains(alias)) {
            pushToAlias = alias;
          }
          val edgeSet = edges.values.flatten
          for (e <- edgeSet) {
            if (StringUtils.isBlank(pushToAlias) &&
              e.alias.equals(alias)) {
              pushToAlias = e.alias
            }
          }
        case _ =>
      }
    }

    if (StringUtils.isNotBlank(pushToAlias)) {
      (true, pattern.fillInRule(filterRule, pushToAlias))
    } else {
      (false, pattern)
    }
  }

  private def updatePatternFilterRule(
      pattern: Pattern,
      updateExpr: (String, Expr, BinaryOpSet),
      startAlias: String,
      boundedVarLenExpandEdgeNameSet: Set[String]): (Boolean, Pattern) = {
    var updatedPattern = pattern

    val alias = updateExpr._1
    val expr = updateExpr._2
    val opName = updateExpr._3
    var isChange = false
    // node rule
    if (alias.equals(startAlias)) {
      val filterRule = LogicRule(
        "generate_id_filter_" + alias,
        "",
        BinaryOpExpr(opName, UnaryOpExpr(GetField(Constants.NODE_ID_KEY), Ref(alias)), expr))
      val res = fillInRule(filterRule, alias, updatedPattern, boundedVarLenExpandEdgeNameSet)
      isChange = res._1 || isChange
      updatedPattern = res._2
    }

    // edge filter
    val inEdges = getInConnection(alias, updatedPattern)
    inEdges.foreach(x => {
      val filterInEdgeRule = LogicRule(
        "generate_in_edge_id_filter_" + x.alias,
        "",
        BinaryOpExpr(opName, UnaryOpExpr(GetField(Constants.EDGE_TO_ID_KEY), Ref(x.alias)), expr))
      val res = fillInRule(filterInEdgeRule, x.alias, updatedPattern,
        boundedVarLenExpandEdgeNameSet)

      isChange = res._1 || isChange
      updatedPattern = res._2
    })

    val outEdges = getOutConnection(alias, updatedPattern)
    outEdges.foreach(x => {
      val filterOutEdgeRule = LogicRule(
        "generate_out_edge_id_filter_" + x.alias,
        "",
        BinaryOpExpr(opName, UnaryOpExpr(GetField(Constants.EDGE_FROM_ID_KEY), Ref(x.alias)), expr))
      val res = fillInRule(filterOutEdgeRule, x.alias,
        updatedPattern, boundedVarLenExpandEdgeNameSet)
      isChange = res._1 || isChange
      updatedPattern = res._2
    })

    (isChange, updatedPattern)
  }

  private def getConnection(
      alias: String,
      pattern: Pattern,
      direction: com.antgroup.openspg.reasoner.common.graph.edge.Direction): Set[Connection] = {
    pattern.topology
      .flatMap(edgeSet => {
        edgeSet._2
          .map { case c: PatternConnection =>
            val compareAlias =
              if (c.direction.equals(direction)) c.source else c.target
            if (compareAlias.equals(alias)) {
              c
            } else {
              null
            }
          }
          .filter(_ != null)
      })
      .toSet
  }

  private def getInConnection(alias: String, pattern: Pattern): Set[Connection] = {
    getConnection(alias, pattern, com.antgroup.openspg.reasoner.common.graph.edge.Direction.IN)
  }

  private def getOutConnection(alias: String, pattern: Pattern): Set[Connection] = {
    getConnection(alias, pattern, com.antgroup.openspg.reasoner.common.graph.edge.Direction.OUT)
  }

  override def direction: Direction = Up

  override def maxIterations: Int = 1
}
