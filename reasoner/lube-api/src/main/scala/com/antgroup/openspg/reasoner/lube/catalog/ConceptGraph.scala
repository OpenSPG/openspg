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

package com.antgroup.openspg.reasoner.lube.catalog

import com.antgroup.openspg.reasoner.common.concept.Predicate
import com.antgroup.openspg.reasoner.lube.catalog.struct.Node

class ConceptGraph extends Serializable {
  private var nodes: Map[String, Node] = null
  private var adjacencyList: Map[String, Map[Predicate, Set[String]]] = null
  def this(nodes: Map[String, Node], adjacencyList: Map[String, Map[Predicate, Set[String]]]) {
    this()
    this.nodes = nodes
    this.adjacencyList = adjacencyList
  }

  def getConceptNode(conceptName: String): Option[Node] = {
    nodes.get(conceptName)
  }

  def getChildren(conceptName: String, predicate: Predicate): Set[Node] = {
    val edge = adjacencyList.get(conceptName)
    if (edge.isEmpty) {
      null
    } else {
      val children = edge.get.get(predicate).orNull
      if (children == null) {
        null
      } else {
        children.map(name => nodes.get(name).orNull)
      }
    }
  }

}
