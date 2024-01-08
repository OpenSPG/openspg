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

package com.antgroup.openspg.reasoner.lube.physical

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.exception.{
  GraphAlreadyExistsException,
  GraphNotFoundException
}
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG

/**
 * Runtime session, manager the graphs during runtime.
 */
class GraphSession[T <: RDG[T]] {

  @transient private val graphRepository: mutable.Map[String, PropertyGraph[T]] =
    new mutable.HashMap[String, PropertyGraph[T]]()

  @transient private val workingRDG: mutable.Map[String, T] =
    new mutable.HashMap[String, T]()


  /**
   * Register the given graph to catalog.
   *
   * @param graphName
   * @param graph
   */
  def register(graphName: String, graph: PropertyGraph[T]): Unit = {
    if (graphRepository.contains(graphName)) {
      throw GraphAlreadyExistsException(graphName + " has exists.", null)
    }
    graphRepository.put(graphName, graph)
  }

  def register(name: String, rdg: T): Unit = {
    workingRDG.put(name, rdg)
  }


  /**
   * Returns graph which is stored at given graph name.
   *
   * @param graphName graph name
   * @return property graph
   */
  def getGraph(graphName: String): PropertyGraph[T] = {
    if (!graphRepository.contains(graphName)) {
      throw GraphNotFoundException(graphName + " not found.", null)
    }
    graphRepository.get(graphName).get
  }

  def getWorkingRDG(name: String): T = {
    if (!workingRDG.contains(name)) {
      throw GraphNotFoundException(name + " not found.", null)
    }
    workingRDG.apply(name)
  }

  /**
   * Tests whether this GraphSession contains a binding for a graphName.
   * @param graphName
   * @return
   */
  def hasGraph(graphName: String): Boolean = {
    graphRepository.contains(graphName)
  }

}
