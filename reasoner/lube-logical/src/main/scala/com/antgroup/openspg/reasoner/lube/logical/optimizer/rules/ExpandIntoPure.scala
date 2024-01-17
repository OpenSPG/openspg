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
import com.antgroup.openspg.reasoner.lube.common.pattern.NodePattern
import com.antgroup.openspg.reasoner.lube.logical.NodeVar
import com.antgroup.openspg.reasoner.lube.logical.operators.{BoundedVarLenExpand, ExpandInto, LogicalOperator}
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, Down, Rule}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext

/**
 * Prue useless expandInto
 */
object ExpandIntoPure extends Rule {

  override def rule(implicit
      context: LogicalPlannerContext): PartialFunction[LogicalOperator, LogicalOperator] = {
    case expandInto @ ExpandInto(in, target, _) =>
      val needPure = canPure(expandInto)
      if (needPure) {
        in
      } else {
        expandInto
      }
    case boundedVarLenExpand @ BoundedVarLenExpand(
          lhs,
          expandInto: ExpandInto,
          edgePattern,
          index) =>
      if (expandInto.pattern.isInstanceOf[NodePattern] && edgePattern.edge.upper == index) {
        val refFields = expandInto.refFields
        if (refFields.head.isEmpty || (refFields.head
            .asInstanceOf[NodeVar]
            .fields
            .size == 1 && refFields.head
            .asInstanceOf[NodeVar]
            .fields
            .head
            .name
            .equals(Constants.NODE_ID_KEY))) {
          BoundedVarLenExpand(lhs, expandInto.in, edgePattern, index)
        } else {
          boundedVarLenExpand
        }
      } else {
        boundedVarLenExpand
      }

  }

  private def canPure(expandInto: ExpandInto): Boolean = {
    if (!expandInto.pattern.isInstanceOf[NodePattern]) {
      false
    } else {
      val refFields = expandInto.refFields
      if (refFields.head.isEmpty) {
        true
      } else {
        false
      }
    }
  }

  override def direction: Direction = Down

  override def maxIterations: Int = 1
}
