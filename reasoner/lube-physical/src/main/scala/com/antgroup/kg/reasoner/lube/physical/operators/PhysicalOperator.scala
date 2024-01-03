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

package com.antgroup.openspg.reasoner.lube.physical.operators

import scala.reflect.runtime.universe.TypeTag

import com.antgroup.openspg.reasoner.common.trees.AbstractTreeNode
import com.antgroup.openspg.reasoner.lube.logical.Var
import com.antgroup.openspg.reasoner.lube.physical.planning.PhysicalPlannerContext
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG

abstract class PhysicalOperator[T <: RDG[T]: TypeTag]
    extends AbstractTreeNode[PhysicalOperator[T]] {

  /**
   * The context during physical planner executing
   * @return
   */
  implicit def context: PhysicalPlannerContext[T] = children.head.context

  /**
   * The output of the current operator
   * @return
   */
  def rdg: T = children.head.rdg

  /**
   * The meta of the output of the current output
   * @return
   */
  def meta: List[Var]
}
