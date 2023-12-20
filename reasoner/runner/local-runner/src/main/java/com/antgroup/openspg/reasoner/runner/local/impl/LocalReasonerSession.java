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

package com.antgroup.openspg.reasoner.runner.local.impl;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.parser.ParserInterface;
import com.antgroup.openspg.reasoner.lube.physical.PropertyGraph;
import com.antgroup.openspg.reasoner.runner.local.rdg.LocalRDG;
import com.antgroup.openspg.reasoner.session.KGReasonerSession;
import com.antgroup.openspg.reasoner.warehouse.common.config.GraphLoaderConfig;
import scala.reflect.api.TypeTags.TypeTag;

public class LocalReasonerSession extends KGReasonerSession<LocalRDG> {

  private final GraphState<IVertexId> graphState;

  /** session implement */
  public LocalReasonerSession(ParserInterface parser, Catalog catalog, TypeTag<LocalRDG> typeTag) {
    super(parser, catalog, typeTag);
    this.graphState = new MemGraphState();
  }

  /** session implement */
  public LocalReasonerSession(
      ParserInterface parser,
      Catalog catalog,
      TypeTag<LocalRDG> typeTag,
      GraphState<IVertexId> graphState) {
    super(parser, catalog, typeTag);
    this.graphState = graphState;
  }

  @Override
  public PropertyGraph<LocalRDG> loadGraph(GraphLoaderConfig graphLoaderConfig) {
    return new LocalPropertyGraph(this.graphState);
  }
}
