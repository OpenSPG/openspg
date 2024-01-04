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

package com.antgroup.openspg.reasoner.lube.physical.util

import scala.reflect.runtime.universe.TypeTag

import com.antgroup.openspg.reasoner.common.exception.SystemError
import com.antgroup.openspg.reasoner.lube.physical.operators.PhysicalOperator
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG

object PhysicalOperatorOps {

  implicit class RichPhysicalOperator[T <: RDG[T]: TypeTag](op: PhysicalOperator[T]) {

    def findExactlyOne(f: PartialFunction[PhysicalOperator[T], Unit]): PhysicalOperator[T] = {
      val results = op.collect {
        case op if f.isDefinedAt(op) =>
          f(op)
          op
      }
      if (results.size != 1) {
        throw SystemError(s"Failed to extract single matching physicalOp from $op")
      } else {
        results.head
      }
    }

  }

}
