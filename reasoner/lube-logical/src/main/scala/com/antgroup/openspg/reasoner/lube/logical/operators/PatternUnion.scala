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

case class PatternUnion(
    lhs: LogicalOperator,
    rhs: LogicalOperator)
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
  override def refFields: List[Var] = List.empty

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
      if (varMap.contains(field.name)) {
        varMap.put(field.name, varMap(field.name).merge(Option.apply(field)))
      } else {
        varMap.put(field.name, field)
      }
    }
    varMap.values.toList
  }

}
