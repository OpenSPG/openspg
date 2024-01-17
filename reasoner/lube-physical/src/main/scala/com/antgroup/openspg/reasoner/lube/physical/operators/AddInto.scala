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

import scala.collection.mutable
import scala.reflect.runtime.universe.TypeTag

import com.antgroup.openspg.reasoner.lube.common.expr.{Directly, Expr}
import com.antgroup.openspg.reasoner.lube.logical.Var
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG

final case class AddInto[T <: RDG[T]: TypeTag](in: PhysicalOperator[T], fields: Map[Var, Expr])
    extends PhysicalOperator[T] {
  override def rdg: T = in.rdg.addFields(fields)

  override def meta: List[Var] = {
    val fieldMap = new mutable.HashMap[String, Var]()
    for (v <- in.meta) {
      fieldMap.put(v.name, v)
    }
    for (varPair <- fields) {
      if (!varPair._2.isInstanceOf[Directly.type]) {
        fieldMap.put(varPair._1.name, fieldMap(varPair._1.name).merge(Option.apply(varPair._1)))
      }
    }
    fieldMap.values.toList
  }

}
