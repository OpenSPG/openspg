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

import com.antgroup.openspg.reasoner.lube.logical.{SolvedModel, Var}

final case class SubQuery(lhs: LogicalOperator, rhs: LogicalOperator)
    extends BinaryLogicalOperator {

  /**
   * the nodes, edges, attributes has been solved in currently
   *
   * @return
   */
  override def solved: SolvedModel = lhs.solved.merge(rhs.solved)

  /**
   * the reference fields in current operator
   *
   * @return
   */
  override def refFields: List[Var] = fieldsMerge(lhs.refFields, rhs.refFields)

  /**
   * the output fields of current operator
   *
   * @return
   */
  override def fields: List[Var] = fieldsMerge(lhs.fields, rhs.refFields)

  private def fieldsMerge(fields: List[Var], other: List[Var]): List[Var] = {
    val varMap = new mutable.HashMap[String, Var]()
    for (field <- fields) {
      varMap.put(field.name, field)
    }
    for (field <- other) {
      if (varMap.contains(field.name)) {
        varMap.put(field.name, varMap(field.name).merge(Option.apply(field)))
      }
    }
    varMap.values.toList
  }

  override def withNewChildren(newChildren: Array[LogicalOperator]): LogicalOperator = {
    this.copy(lhs = newChildren.apply(0), rhs = newChildren.apply(1))
  }

}

