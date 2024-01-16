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
import com.antgroup.openspg.reasoner.lube.physical.planning.PhysicalPlannerContext
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG

final case class Start[T <: RDG[T]: TypeTag](
    graphName: String,
    alias: String,
    meta: List[Var],
    types: Set[String])(implicit override val context: PhysicalPlannerContext[T])
    extends PhysicalOperator[T] {
  override def rdg: T = context.graphSession.getGraph(graphName).createRDG(alias, types)
}

final case class DrivingRDG[T <: RDG[T]: TypeTag](
    graphName: String,
    meta: List[Var],
    alias: String,
    workingRdgName: String)(implicit override val context: PhysicalPlannerContext[T])
    extends PhysicalOperator[T] {
  override def rdg: T = {
    val workingRdg = context.graphSession.getWorkingRDG(workingRdgName)
    context.graphSession.getGraph(graphName).createRDG(alias, workingRdg)
  }
}
