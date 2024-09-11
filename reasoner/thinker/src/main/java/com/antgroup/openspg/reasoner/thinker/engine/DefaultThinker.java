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

package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.thinker.Thinker;
import com.antgroup.openspg.reasoner.thinker.catalog.LogicCatalog;
import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.*;

import java.util.*;

public class DefaultThinker implements Thinker {
  private GraphStore graphStore;
  private InfGraph infGraph;

  public DefaultThinker(GraphState<IVertexId> graphState, LogicCatalog logicCatalog) {
    this.graphStore = new GraphStore(graphState);
    this.infGraph = new InfGraph(logicCatalog.getLogicNetwork(), graphStore);
  }

  @Override
  public void init(Map<String, String> params) {
    this.infGraph.init(params);
  }

  @Override
  public List<Result> find(Element s, Element p, Element o) {
    return find(s, p, o, new HashMap<>());
  }

  @Override
  public List<Result> find(Element s, Element p, Element o, Map<String, Object> context) {
    this.infGraph.clear();
    Triple pattern = Triple.create(s, p, o);
    this.infGraph.prepare(context);
    List<Result> result = this.infGraph.find(pattern, context == null ? new HashMap<>() : context);
    return result;
  }

  @Override
  public List<Result> find(Node s, Map<String, Object> context) {
    this.infGraph.clear();
    this.infGraph.prepare(context);
    List<Result> triples = this.infGraph.find(Triple.create(s), context == null ? new HashMap<>() : context);
    List<Result> results = new LinkedList<>();
    for (Result triple : triples) {
      results.add(new Result(((Triple) triple.getData()).getObject(), triple.getTraceLog()));
    }
    return results;
  }
}
