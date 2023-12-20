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

package com.antgroup.openspg.reasoner.lube.logical

import com.antgroup.openspg.reasoner.lube.catalog.{AbstractConnection, Catalog, SemanticPropertyGraph}
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field

class EmptyCatalog extends Catalog {

  /**
   * Get schema from knowledge graph
   */
  override def getKnowledgeGraph(): SemanticPropertyGraph =
    new SemanticPropertyGraph(Catalog.defaultGraphName, null, null, null)

  override def getConnections(): Map[AbstractConnection, Set[String]] = Map.empty

  override def getConnection(typeName: String): AbstractConnection = null

  /**
   * get default node properties
   * @return
   */
  override def getDefaultNodeProperties()
      : Set[Field] = Set.empty

  /**
   * get default edge properties
   */
  override def getDefaultEdgeProperties()
      : Set[Field] = Set.empty

}
