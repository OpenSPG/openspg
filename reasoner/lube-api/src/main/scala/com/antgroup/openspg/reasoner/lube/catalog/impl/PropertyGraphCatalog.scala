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

/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.lube.catalog.impl

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.graph.edge.SPO
import com.antgroup.openspg.reasoner.common.types.{KTLong, KTString}
import com.antgroup.openspg.reasoner.lube.catalog._
import com.antgroup.openspg.reasoner.lube.catalog.struct.{Edge, Field, Node, NodeType}

class PropertyGraphCatalog(val propertyGraphSchema: Map[String, Set[String]]) extends Catalog {

  /**
   * Get schema from knowledge graph
   */
  override def getKnowledgeGraph(): SemanticPropertyGraph =
    new SemanticPropertyGraph(
      Catalog.defaultGraphName,
      buildPropertyGraphSchema(),
      mutable.Map.empty,
      null)

  private def buildPropertyGraphSchema(): PropertyGraphSchema = {
    val nodes = new mutable.HashMap[String, Node]
    val edges = new mutable.HashMap[SPO, Edge]
    val edgeDefaultProperties = getDefaultEdgeProperties()
    val nodeDefaultProperties = getDefaultNodeProperties()
    for (kv <- propertyGraphSchema) {
      if (kv._1.contains("_")) {
        // edge
        val spoArray = kv._1.split("_")
        val spo = new SPO(spoArray.apply(0), spoArray.apply(1), spoArray.apply(2))
        val edge = Edge(
          spoArray.apply(0),
          spoArray.apply(1),
          spoArray.apply(2),
          kv._2.map(new Field(_, KTString, true)) ++ edgeDefaultProperties,
          true)
        edges.put(spo, edge)
      } else {
        // vertex
        val node = Node(kv._1,
          NodeType.ADVANCED,
          kv._2.map(new Field(_, KTString, true)) ++ nodeDefaultProperties,
          true)
        nodes.put(kv._1, node)
      }
    }
    new PropertyGraphSchema(nodes, edges)
  }

  override def getConnections(): Map[AbstractConnection, Set[String]] = Map.empty

  override def getConnection(typeName: String): AbstractConnection = null
  /**
   * get default node properties
   * @return
   */
  override def getDefaultNodeProperties()
  : Set[Field] = {
    Set.apply(
      new Field(Constants.NODE_ID_KEY, KTString, true),
      new Field(Constants.CONTEXT_LABEL, KTString, true))
  }

  /**
   * get default edge properties
   */
  override def getDefaultEdgeProperties()
  : Set[Field] = {
    Set.apply(
      new Field(Constants.EDGE_FROM_ID_KEY, KTString, true),
      new Field(Constants.EDGE_TO_ID_KEY, KTString, true)
    )
  }
}
