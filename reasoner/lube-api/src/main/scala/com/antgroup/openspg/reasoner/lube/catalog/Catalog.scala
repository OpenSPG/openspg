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

package com.antgroup.openspg.reasoner.lube.catalog

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.exception.{ConnectionNotFoundException, GraphAlreadyExistsException, GraphNotFoundException}
import com.antgroup.openspg.reasoner.common.utils.LabelTypeUtils
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.common.graph.IRGraph
import com.antgroup.openspg.reasoner.udf.{UdfMng, UdfMngFactory}


/**
 * Base class for catalog.
 * Catalog manages the meta-information necessary for KGReasoner to run,
 * including the Schema of the Knowledge Graph, the concept graph, the hypergraph,
 * and the property graph at runtime
 */
abstract class Catalog() extends Serializable {
  protected val graphRepository = new mutable.HashMap[String, SemanticPropertyGraph]()
  @transient private lazy val udfRepo = UdfMngFactory.getUdfMng
  private val connections = new mutable.HashMap[String, mutable.HashSet[AbstractConnection]]()

  /**
   * Init Catalog from Knowledge Graph
   */
  def init(): Unit = {
    val graph = getKnowledgeGraph()
    if (!graphRepository.contains("KG")) {
      registerSchema("KG", graph)
    }
    val connections = getConnections()
    if (connections != null) {
      for (connection <- connections) {
        registerConnection(connection._2, connection._1)
      }
    }
  }

  /**
   * Register the given [[SemanticPropertyGraph]] to catalog.
   *
   * @param graphName
   * @param propertyGraph
   */
  def registerSchema(graphName: String, propertyGraph: SemanticPropertyGraph): Unit = {
    if (graphRepository.contains(graphName)) {
      throw GraphAlreadyExistsException(graphName + " has exists.", null)
    }
    graphRepository.put(graphName, propertyGraph)
  }

  /**
   * Register [[AbstractConnection]] for specific types
   * @param types
   * @param connection
   */
  def registerConnection(types: Set[String], connection: AbstractConnection): Unit = {
    for (t <- types) {
      val innerConnSet: mutable.HashSet[AbstractConnection] =
        connections.getOrElseUpdate(t, new mutable.HashSet[AbstractConnection]())
      innerConnSet.add(connection)
    }
  }

  /**
   * Get connection by typeName.
   * @param typeName
   * @return
   */
  def getConnection(typeName: String): Set[AbstractConnection] = {
    val finalType = LabelTypeUtils.getMetaType(typeName)
    if (!connections.contains(finalType)) {
      throw ConnectionNotFoundException(s"$finalType not found.", null)
    }
    connections.getOrElse(finalType, mutable.Set.empty).toSet
  }

  /**
   * Returns the [[SemanticPropertyGraph]] which is stored at given graph name.
   *
   * @param graphName graph name
   * @return property graph
   */
  def getGraph(graphName: String): SemanticPropertyGraph = {
    if (!graphRepository.contains(graphName)) {
      throw GraphNotFoundException(graphName + " not found.", null)
    }

    graphRepository.get(graphName).orNull
  }

  def getUdfRepo: UdfMng = udfRepo

  /**
   * Get schema from knowledge graph
   */
  def getKnowledgeGraph(): SemanticPropertyGraph

  /**
   * Get connections of knowledge graph
   *
   * @return
   */
  def getConnections(): Map[AbstractConnection, Set[String]]

  /**
   * get default node properties
   * @return
   */
  def getDefaultNodeProperties(): Set[Field]

  /**
   * get default edge properties
   */
  def getDefaultEdgeProperties(): Set[Field]
}

object Catalog {
  val defaultGraphName = IRGraph.defaultGraphName;
}
