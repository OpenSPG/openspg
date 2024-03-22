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
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG

final case class Select[T <: RDG[T]: TypeTag](
    in: PhysicalOperator[T],
    orderedFields: List[Var],
    as: List[String],
    isDistinctGet: Boolean)
    extends PhysicalOperator[T] {
  def row: T#Records = in.rdg.select(orderedFields, as, isDistinctGet)
  override def meta: List[Var] = orderedFields
}
