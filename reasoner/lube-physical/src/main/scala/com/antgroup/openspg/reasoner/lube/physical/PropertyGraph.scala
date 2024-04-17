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

package com.antgroup.openspg.reasoner.lube.physical

import com.antgroup.openspg.reasoner.lube.common.expr.Expr
import com.antgroup.openspg.reasoner.lube.logical.RepeatPathVar
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG

trait PropertyGraph[T <: RDG[T]] {

  /**
   * Start with ids according to the types of start nodes.
   *
   * @param alias
   * @param types
   * @return
   */
  def createRDG(alias: String, types: Set[String]): T

  /**
   * Start with specific rdg with specific alias.
   * @param rdg
   * @param alias
   * @return
   */
  def createRDG(alias: String, rdg: T): T

  /**
   * Start with specific vertex.
   *
   * @param alias
   * @param id
   * @param types
   * @return
   */
  def createRDG(alias: String, id: Expr, types: Set[String]): T

  /**
   * Start with specific rdg with specific alias which in [[RepeatPathVar]]
   * @param repeatVar
   * @param alias
   * @param rdg
   * @return
   */
  def createRDGFromPath(repeatVar: RepeatPathVar, alias: String, rdg: T): T
}
