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

package com.antgroup.openspg.reasoner.lube.logical.optimizer

import com.antgroup.openspg.reasoner.lube.logical.operators.LogicalOperator
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext

sealed trait Direction

case object Up extends Direction
case object Down extends Direction

trait Rule {

  def ruleWithContext(implicit context: LogicalPlannerContext): PartialFunction[
    (LogicalOperator, Map[String, Object]),
    (LogicalOperator, Map[String, Object])]

  def direction: Direction

  def maxIterations: Int
}

abstract class SimpleRule extends Rule {

  override def ruleWithContext(implicit context: LogicalPlannerContext): PartialFunction[
    (LogicalOperator, Map[String, Object]),
    (LogicalOperator, Map[String, Object])] = {
    case (operator, c) if rule.isDefinedAt(operator) =>
      val transformedOperator = rule(context)(operator)
      (transformedOperator, c)
  }

  def rule(implicit
      context: LogicalPlannerContext): PartialFunction[LogicalOperator, LogicalOperator]

}
