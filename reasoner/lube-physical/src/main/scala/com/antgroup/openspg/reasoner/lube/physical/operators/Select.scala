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

package com.antgroup.openspg.reasoner.lube.physical.operators

import scala.reflect.runtime.universe.TypeTag

import com.antgroup.openspg.reasoner.lube.logical.Var
import com.antgroup.openspg.reasoner.lube.physical.rdg.{RDG, Row}

final case class Select[T <: RDG[T]: TypeTag](
    in: PhysicalOperator[T],
    orderedFields: List[Var],
    as: List[String],
    distinct: Boolean)
    extends StackingPhysicalOperator[T] {

  def row: Row[T] = {
    var row = in.rdg.select(orderedFields, as)
    if (distinct) {
      row = row.distinct()
    }
    row
  }

  override def meta: List[Var] = orderedFields

  override def withNewChildren(newChildren: Array[PhysicalOperator[T]]): PhysicalOperator[T] = {
    this.copy(in = newChildren.head)
  }
}
