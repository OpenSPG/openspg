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

import com.antgroup.openspg.reasoner.lube.logical.NodeVar
import com.antgroup.openspg.reasoner.lube.logical.operators.{Aggregate, LogicalOperator, Select}
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, SimpleRule, Up}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext

case object DistinctGet extends SimpleRule {

  override def rule(implicit
      context: LogicalPlannerContext): PartialFunction[LogicalOperator, LogicalOperator] = {
    case select: Select =>
      val aggNum = getAggregateNum(select)
      if (aggNum == 0 && getVertexOnly(select)) {
        val group = select.fields.map(x => NodeVar(x.name, Set.empty)).toSet.toList
        val agg = Aggregate(select.in, group, Map.empty, select.solved)
        select.copy(in = agg)
      } else {
        select
      }
  }

  /**
   * if select vertex only, equals to `distinct select`
   */
  private def getVertexOnly(select: Select): Boolean = {
    val nodes = select.solved.getNodeAliasSet
    val getFields = select.fields.map(_.name).toSet
    if (getFields.diff(nodes).isEmpty) {
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

  override def direction: Direction = Up

  override def maxIterations: Int = 1
}
