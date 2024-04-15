/*
 * Copyright 2023 OpenSPG Authors
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
import com.antgroup.openspg.reasoner.common.trees.BottomUp
import com.antgroup.openspg.reasoner.lube.common.expr.{BEqual, BinaryOpExpr, Expr}
import com.antgroup.openspg.reasoner.lube.common.graph.IRNode
import com.antgroup.openspg.reasoner.lube.logical.operators.{Filter, LogicalOperator, Start, StartFromVertex}
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, SimpleRule, Up}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext
import com.antgroup.openspg.reasoner.lube.utils.ExprUtils

case object IdEqualPushDown extends SimpleRule {

  override def rule(implicit
      context: LogicalPlannerContext): PartialFunction[LogicalOperator, LogicalOperator] = {
    case filter: Filter =>
      val idExpr = getIdExpr(filter)
      if (idExpr == null) {
        filter
      } else {
        def rewriter: PartialFunction[LogicalOperator, LogicalOperator] = {
          case start: Start =>
            StartFromVertex(start.graph, idExpr, start.types, start.alias, start.solved)
        }
        val newFilter = BottomUp[LogicalOperator](rewriter).transform(filter).asInstanceOf[Filter]
        newFilter.in
      }
  }

  private def getIdExpr(filter: Filter): Expr = {
    filter.rule.getExpr match {
      case BinaryOpExpr(BEqual, left, right) =>
        val irFields = ExprUtils.getAllInputFieldInRule(
          filter.rule.getExpr,
          filter.solved.getNodeAliasSet,
          filter.solved.getEdgeAliasSet)
        if (irFields.size != 1 || !irFields.head.isInstanceOf[IRNode] || !irFields.head
            .asInstanceOf[IRNode]
            .fields
            .equals(Set.apply(Constants.NODE_ID_KEY))) {
          null
        } else {
          right
        }
      case _ => null
    }
  }

  override def direction: Direction = Up

  override def maxIterations: Int = 1
}
