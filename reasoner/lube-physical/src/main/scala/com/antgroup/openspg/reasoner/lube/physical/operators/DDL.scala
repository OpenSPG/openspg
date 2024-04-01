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

import com.antgroup.openspg.reasoner.lube.block.{AddPredicate, AddProperty, AddVertex, DDLOp}
import com.antgroup.openspg.reasoner.lube.logical.Var
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG

final case class DDL[T <: RDG[T]: TypeTag](in: PhysicalOperator[T], ddlOp: Set[DDLOp])
    extends StackingPhysicalOperator[T] {

  override def rdg: T = {
    val list = new mutable.ListBuffer[DDLOp]()
    for (ddl <- ddlOp) {
      ddl match {
        case addVertex: AddVertex => list.append(addVertex)
        case _ =>
      }
    }

    for (ddl <- ddlOp) {
      ddl match {
        case addProp: AddProperty => list.append(addProp)
        case _ =>
      }
    }

    for (ddl <- ddlOp) {
      ddl match {
        case addPredicate: AddPredicate => list.append(addPredicate)
        case _ =>
      }
    }
    in.rdg.ddl(list.toList)
  }

  override def meta: List[Var] = List.empty

  override def withNewChildren(newChildren: Array[PhysicalOperator[T]]): PhysicalOperator[T] = {
    this.copy(in = newChildren.head)
  }
}
