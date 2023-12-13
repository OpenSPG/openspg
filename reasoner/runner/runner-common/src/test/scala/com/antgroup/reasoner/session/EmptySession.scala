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

package com.antgroup.reasoner.session

import com.antgroup.openspg.reasoner.lube.catalog.Catalog
import com.antgroup.openspg.reasoner.lube.logical.RepeatPathVar
import com.antgroup.openspg.reasoner.lube.parser.ParserInterface
import com.antgroup.openspg.reasoner.lube.physical.PropertyGraph
import com.antgroup.openspg.reasoner.warehouse.common.config.GraphLoaderConfig
import com.antgroup.openspg.reasoner.session.KGReasonerSession

class EmptyPropertyGraph extends PropertyGraph[EmptyRDG] {

  /**
   * Start with ids according to the types of start nodes.
   *
   * @param types
   * @return
   */
  override def createRDG(alias: String, types: Set[String]): EmptyRDG = new EmptyRDG()

  /**
   * Start with specific rdg with specific alias.
   *
   * @param rdg
   * @param alias
   * @return
   */
  override def createRDG(alias: String, rdg: EmptyRDG): EmptyRDG = rdg

  /**
   * Start with specific rdg with specific alias which in [[RepeatPathVar]]
   *
   * @param repeatVar
   * @param alias
   * @param rdg
   * @return
   */
  override def createRDGFromPath(
      repeatVar: RepeatPathVar,
      alias: String,
      rdg: EmptyRDG): EmptyRDG = rdg

}

class EmptySession(parser: ParserInterface, catalog: Catalog)
    extends KGReasonerSession[EmptyRDG](parser, catalog) {

  /**
   * Load a graph from Knowledge Graph to [[KGReasonerSession]]
   *
   * @param graphLoaderConfig
   * @return
   */
  override def loadGraph(graphLoaderConfig: GraphLoaderConfig): PropertyGraph[EmptyRDG] =
    new EmptyPropertyGraph()

}
