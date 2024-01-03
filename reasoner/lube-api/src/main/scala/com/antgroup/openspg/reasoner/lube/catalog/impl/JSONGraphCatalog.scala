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

package com.antgroup.openspg.reasoner.lube.catalog.impl

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.types.{KgType, KTLong, KTString}
import com.antgroup.openspg.reasoner.lube.catalog._
import com.antgroup.openspg.reasoner.lube.catalog.struct.{Field, NodeType}
import org.json4s._
import org.json4s.ext.EnumNameSerializer
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.read

class JSONGraphCatalog(val propertyGraph: String) extends Catalog {

  /**
   * Get schema from knowledge graph
   */
  override def getKnowledgeGraph(): SemanticPropertyGraph = {
    implicit val formats =
      Serialization.formats(
        FullTypeHints(
          List.apply(classOf[KgType]))) + new SPOKeySerializer + new EnumNameSerializer(NodeType)
    val graphSchema = read[PropertyGraphSchema](propertyGraph)
    val nodesName = graphSchema.nodes.keySet
    getDefaultEdgeProperties().foreach(defaultField => {
      nodesName.foreach(nodeName => {
        graphSchema.addVertexField(nodeName, defaultField)
      })
    })
    val edgesName = graphSchema.edges.keySet
    edgesName.foreach(edgeName => {
      graphSchema.addEdgeField(edgeName, getDefaultEdgeProperties())
    })

    new SemanticPropertyGraph(Catalog.defaultGraphName, graphSchema, mutable.Map.empty, null)
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
      new Field(Constants.VERTEX_INTERNAL_ID_KEY, KTString, true),
      new Field(Constants.CONTEXT_LABEL, KTString, true))
  }

  /**
   * get default edge properties
   */
  override def getDefaultEdgeProperties()
      : Set[Field] = {
    Set.apply(
      new Field(Constants.CONTEXT_LABEL, KTString, true),
      new Field(Constants.EDGE_FROM_ID_KEY, KTString, true),
      new Field(Constants.EDGE_TO_ID_KEY, KTString, true),
      new Field(Constants.EDGE_FROM_INTERNAL_ID_KEY, KTString, true),
      new Field(Constants.EDGE_TO_INTERNAL_ID_KEY, KTString, true),
      new Field(Constants.EDGE_FROM_ID_TYPE_KEY, KTString, true),
      new Field(Constants.EDGE_TO_ID_TYPE_KEY, KTString, true)
    )
  }

}
