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

package com.antgroup.openspg.reasoner.lube.physical.operators

import scala.collection.mutable
import scala.reflect.runtime.universe.TypeTag

import com.antgroup.openspg.reasoner.lube.logical.Var
import com.antgroup.openspg.reasoner.lube.logical.planning.JoinType
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG

final case class Join[T <: RDG[T]: TypeTag](
    lhs: PhysicalOperator[T],
    rhs: PhysicalOperator[T],
    onAlias: List[(String, String)],
    joinType: JoinType,
    lhsSchemaMapping: Map[Var, Var],
    rhsSchemaMapping: Map[Var, Var])
    extends PhysicalOperator[T] {

  /**
   * The output of the current operator
   *
   * @return
   */
  override def rdg: T = {
    lhs.rdg.join(rhs.rdg, joinType, onAlias, lhsSchemaMapping, rhsSchemaMapping)
  }

  /**
   * The meta of the output of the current output
   *
   * @return
   */
  override def meta: List[Var] = {
    val varMap = new mutable.HashMap[String, Var]()
    for (field <- lhs.meta) {
      varMap.put(field.name, field)
    }
    for (field <- rhs.meta) {
      if (varMap.contains(field.name)) {
        varMap.put(field.name, varMap(field.name).merge(Option.apply(field)))
      } else {
        varMap.put(field.name, field)
      }
    }
    varMap.values.toList
  }
}
