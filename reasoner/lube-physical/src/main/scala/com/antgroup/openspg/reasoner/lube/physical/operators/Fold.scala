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

final case class Fold[T <: RDG[T]: TypeTag](
    in: PhysicalOperator[T],
    foldMapping: List[(List[Var], RichVar)])
    extends StackingPhysicalOperator[T] {

  override def rdg: T = in.rdg.fold(foldMapping)

  /**
   * The meta of the output of the current output
   *
   * @return
   */
  override def meta: List[Var] = {
    val inMeta = in.meta
    val uselessMeta = foldMapping.map(_._1).flatten
    val outMeta = new mutable.ListBuffer[Var]
    outMeta.appendAll(inMeta.diff(uselessMeta))
    outMeta.appendAll(foldMapping.map(_._2))
    outMeta.toList
  }

  override def withNewChildren(newChildren: Array[PhysicalOperator[T]]): PhysicalOperator[T] = {
    this.copy(in = newChildren.head)
  }
}
