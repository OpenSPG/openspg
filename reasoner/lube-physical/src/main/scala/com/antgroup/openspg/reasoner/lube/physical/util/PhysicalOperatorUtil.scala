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

package com.antgroup.openspg.reasoner.lube.physical.util

import scala.reflect.runtime.universe.TypeTag

import com.antgroup.openspg.reasoner.lube.physical.operators.{PhysicalLeafOperator, PhysicalOperator, Start, StartFromVertex}
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG
import com.antgroup.openspg.reasoner.lube.physical.util.PhysicalOperatorOps.RichPhysicalOperator

object PhysicalOperatorUtil {

  def getStartOp[T <: RDG[T]: TypeTag](
      physicalOp: PhysicalOperator[T]): PhysicalLeafOperator[T] = {
    val op = physicalOp.findExactlyOne {
      case start: Start[T] =>
      case start: StartFromVertex[T] =>
    }
    op.asInstanceOf[PhysicalLeafOperator[T]]
  }

}
