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

package com.antgroup.openspg.reasoner.rdg.common;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.lube.logical.EdgeVar;
import com.antgroup.openspg.reasoner.lube.logical.NodeVar;
import com.antgroup.openspg.reasoner.lube.logical.Var;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import scala.collection.JavaConversions;

public class KgGraphRenameImpl implements Serializable {
  private final Map<String, String> vertexAliasMap;
  private final Map<String, String> edgeAliasMap;

  public KgGraphRenameImpl(scala.collection.immutable.Map<Var, Var> schemaMapping) {
    this.vertexAliasMap = new HashMap<>();
    this.edgeAliasMap = new HashMap<>();
    for (Map.Entry<Var, Var> entry : JavaConversions.mapAsJavaMap(schemaMapping).entrySet()) {
      if (entry.getKey() instanceof NodeVar
          && entry.getValue() instanceof NodeVar
          && !entry.getKey().name().equals(entry.getValue().name())) {
        this.vertexAliasMap.put(entry.getKey().name(), entry.getValue().name());
      } else if (entry.getKey() instanceof EdgeVar
          && entry.getValue() instanceof EdgeVar
          && !entry.getKey().name().equals(entry.getValue().name())) {
        this.edgeAliasMap.put(entry.getKey().name(), entry.getValue().name());
      }
    }
  }

  public boolean needAction() {
    return !vertexAliasMap.isEmpty() || !edgeAliasMap.isEmpty();
  }

  public KgGraph<IVertexId> rename(KgGraph<IVertexId> value) {
    if (this.vertexAliasMap.isEmpty() && this.edgeAliasMap.isEmpty()) {
      return value;
    }
    KgGraphImpl kgGraph = (KgGraphImpl) value;
    Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = new HashMap<>();
    for (Map.Entry<String, String> entry : this.vertexAliasMap.entrySet()) {
      alias2VertexMap.put(entry.getValue(), kgGraph.getAlias2VertexMap().remove(entry.getKey()));
    }
    kgGraph.getAlias2VertexMap().putAll(alias2VertexMap);

    Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap = new HashMap<>();
    for (Map.Entry<String, String> entry : this.edgeAliasMap.entrySet()) {
      alias2EdgeMap.put(entry.getValue(), kgGraph.getAlias2EdgeMap().remove(entry.getKey()));
    }
    kgGraph.getAlias2EdgeMap().putAll(alias2EdgeMap);

    return value;
  }
}
