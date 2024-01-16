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

import com.antgroup.openspg.reasoner.lube.logical.{RichVar, Var}
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG

final case class Unfold[T <: RDG[T]: TypeTag](
    in: PhysicalOperator[T],
    foldMapping: List[(RichVar, List[Var])])
    extends PhysicalOperator[T] {

  override def rdg: T = in.rdg.unfold(foldMapping)

  /**
   * The meta of the output of the current output
   *
   * @return
   */
  override def meta: List[Var] = {
    val outMeta = new mutable.ListBuffer[Var]
    for (m <- in.meta) {
      m match {
        case richVar: RichVar =>
          for (pair <- foldMapping) {
            if (pair._1.name.equals(richVar.name)) {
              outMeta.appendAll(pair._2)
            }
          }
        case simpleVar: Var =>
          outMeta.append(simpleVar)
      }
    }
    outMeta.toList
  }
}
