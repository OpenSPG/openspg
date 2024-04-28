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

package com.antgroup.openspg.reasoner.lube.logical.operators

import scala.collection.mutable

import com.antgroup.openspg.reasoner.lube.common.pattern.{EdgePattern, VariablePatternConnection}
import com.antgroup.openspg.reasoner.lube.logical.{SolvedModel, Var}
import com.antgroup.openspg.reasoner.lube.logical.PatternOps.PatternOps

case class BoundedVarLenExpand(
    lhs: LogicalOperator,
    rhs: LogicalOperator,
    edgePattern: EdgePattern[VariablePatternConnection],
    index: Int)
    extends BinaryLogicalOperator {

  /**
   * the nodes, edges, attributes has been solved in currently
   *
   * @return
   */
  override def solved: SolvedModel = lhs.solved

  /**
   * the reference fields in current operator
   *
   * @return
   */
  override def refFields: List[Var] = edgePattern.toVar(solved, graph)

  /**
   * the output fields of current operator
   *
   * @return
   */
  override def fields: List[Var] = {
    val varMap = new mutable.HashMap[String, Var]()
    for (field <- lhs.fields) {
      varMap.put(field.name, field)
    }
    for (field <- rhs.fields) {
      if (!varMap.contains(field.name)) {
        varMap.put(field.name, field)
      }
    }
    varMap.values.toList
  }

  override def withNewChildren(newChildren: Array[LogicalOperator]): LogicalOperator = {
    this.copy(lhs = newChildren.apply(0), rhs = newChildren.apply(1))
  }
}
