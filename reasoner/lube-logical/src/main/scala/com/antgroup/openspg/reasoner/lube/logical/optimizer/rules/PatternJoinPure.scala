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

import com.antgroup.openspg.reasoner.lube.common.pattern.NodePattern
import com.antgroup.openspg.reasoner.lube.logical.operators.{BoundedVarLenExpand, LogicalOperator, PatternJoin, PatternScan}
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, SimpleRule, Up}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext

object PatternJoinPure extends SimpleRule {
  override def rule(implicit
      context: LogicalPlannerContext): PartialFunction[LogicalOperator, LogicalOperator] = {
    case patternJoin @ PatternJoin(
          boundedVarLenExpand: BoundedVarLenExpand,
          scan: PatternScan,
          _) =>
      if (scan.pattern.isInstanceOf[NodePattern]) {
        boundedVarLenExpand
      } else {
        patternJoin
      }
  }

  override def direction: Direction = Up

  override def maxIterations: Int = 1
}
