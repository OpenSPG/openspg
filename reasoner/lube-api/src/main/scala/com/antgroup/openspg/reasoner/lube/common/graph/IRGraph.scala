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

package com.antgroup.openspg.reasoner.lube.common.graph

/**
 * A graph defined in query, which has a graph name only. Usually the name is KG
 */
trait IRGraph {
  def graphName: String
  def nodes: Map[String, IRNode]
  def edges: Map[String, IREdge]
}

final case class KG(nodes: Map[String, IRNode], edges: Map[String, IREdge]) extends IRGraph {
  override def graphName: String = IRGraph.defaultGraphName
}

final case class View(graphName: String, nodes: Map[String, IRNode], edges: Map[String, IREdge])
    extends IRGraph

object IRGraph {
  val defaultGraphName = "KG"
}
