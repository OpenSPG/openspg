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
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks.break

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.lube.logical.{NodeVar, PropertyVar, Var}
import com.antgroup.openspg.reasoner.lube.logical.operators._
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, Rule, Up}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext

/**
 * Convert group node property to group node, when properties contains node id.
 */
object GroupNode extends Rule {

  override def rule(implicit
      context: LogicalPlannerContext): PartialFunction[LogicalOperator, LogicalOperator] = {
    case aggregate: Aggregate =>
      val varMap = new mutable.HashMap[String, mutable.Set[Var]]()
      for (field <- aggregate.group) {
        if (!varMap.contains(field.name)) {
          varMap.put(field.name, new mutable.HashSet[Var]())
        }
        varMap(field.name).add(field)
      }
      val groups = new ListBuffer[Var]()
      val nodes = aggregate.solved.getNodeAliasSet
      for (kv <- varMap) {
        if (nodes.contains(kv._1)) {
          var hasId = false
          for (field <- kv._2) {
            field match {
              case PropertyVar(name, field) =>
                if (field.name.equals(Constants.NODE_ID_KEY)) {
                  groups.append(NodeVar(name, Set.empty))
                  hasId = true
                }
              case _ =>
            }
            if (!hasId) {
              groups.++=(kv._2)
            }
          }
        } else {
          groups.++=(kv._2)
        }
      }
      aggregate.copy(group = groups.toList)
  }

  override def direction: Direction = Up

  override def maxIterations: Int = 1
}
