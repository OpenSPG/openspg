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

import com.antgroup.openspg.reasoner.lube.logical.operators.{Filter, LogicalOperator}
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, Down, Rule, SimpleRule}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext

object FilterMerge extends SimpleRule {

  override def rule(implicit
      context: LogicalPlannerContext): PartialFunction[LogicalOperator, LogicalOperator] = {
    case filter @ Filter(_: Filter, _) => mergeTwoFilter(filter)
  }

  private def mergeTwoFilter(filer: Filter): LogicalOperator = {
    val inFilter = filer.in.asInstanceOf[Filter]
    val newFilter = Filter(inFilter.in, filer.rule.andRule(inFilter.rule))
    newFilter
  }

  override def direction: Direction = Down

  override def maxIterations: Int = 10
}
