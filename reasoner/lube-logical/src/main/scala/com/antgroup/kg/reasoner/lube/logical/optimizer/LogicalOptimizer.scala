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

package com.antgroup.openspg.reasoner.lube.logical.optimizer

import com.antgroup.openspg.reasoner.lube.logical.operators.LogicalOperator
import com.antgroup.openspg.reasoner.lube.logical.optimizer.rules._
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext

/**
 * LogicalOptimizer, a rule based optimizer for logical plan
 */
object LogicalOptimizer {

  var LOGICAL_OPT_RULES: Seq[Rule] =
    Seq(
      IdFilterPushDown,
      FilterPushDown,
      ExpandIntoPure,
      FilterMerge,
      SolvedModelPure,
      DistinctGet,
      AggregatePushDown,
      Pure,
      ProjectMerge)

  def optimize(input: LogicalOperator, optRuleList: Seq[Rule])(implicit
      context: LogicalPlannerContext): LogicalOperator = {
    var root: LogicalOperator = input
    for (rule <- optRuleList) {
      for (i <- 0 until (rule.maxIterations)) {
        if (rule.direction.equals(Up)) {
          root = root.rewrite(rule.rule)
        } else {
          root = root.rewriteTopDown(rule.rule)
        }
      }
    }
    root
  }

  def optimize(input: LogicalOperator)(implicit
      context: LogicalPlannerContext): LogicalOperator = {
    optimize(input, LOGICAL_OPT_RULES)
  }

}
