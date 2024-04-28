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

import java.util.UUID

import scala.reflect.runtime.universe.TypeTag

import com.antgroup.openspg.reasoner.lube.logical.Var
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG

final case class Cache[T <: RDG[T]: TypeTag](in: PhysicalOperator[T])
    extends StackingPhysicalOperator[T] {

  /**
   * The output of the current operator
   *
   * @return
   */
  override def rdg: T = {
    val uid = cacheName
    val cache = in.rdg.cache()
    context.graphSession.register(uid, cache)
    cache
  }

  /**
   * The meta of the output of the current output
   *
   * @return
   */
  override def meta: List[Var] = in.meta

  lazy val cacheName: String = {
    UUID.randomUUID().toString().replace("-", "")
  }

  override def toString: String = {
    s"Cache(RdgId=${cacheName})"
  }

  override def withNewChildren(newChildren: Array[PhysicalOperator[T]]): PhysicalOperator[T] = {
    this.copy(in = newChildren.head)
  }
}
