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

package com.antgroup.openspg.reasoner.rdg.common;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.lube.logical.EdgeVar;
import com.antgroup.openspg.reasoner.lube.logical.NodeVar;
import com.antgroup.openspg.reasoner.lube.logical.Var;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
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

  public KgGraph<IVertexId> rename(KgGraph<IVertexId> value) {
    if (this.vertexAliasMap.isEmpty() && this.edgeAliasMap.isEmpty()) {
      return value;
    }
    KgGraphImpl kgGraph = (KgGraphImpl) value;
    for (Map.Entry<String, String> entry : this.vertexAliasMap.entrySet()) {
      kgGraph
          .getAlias2VertexMap()
          .put(entry.getValue(), kgGraph.getAlias2VertexMap().remove(entry.getKey()));
    }
    for (Map.Entry<String, String> entry : this.edgeAliasMap.entrySet()) {
      kgGraph
          .getAlias2EdgeMap()
          .put(entry.getValue(), kgGraph.getAlias2EdgeMap().remove(entry.getKey()));
    }
    return value;
  }

  public KgGraph<IVertexId> renameAndRemoveRoot(KgGraph<IVertexId> value, String rootVertexAlias) {
    KgGraphImpl kgGraph = (KgGraphImpl) rename(value);
    kgGraph.getAlias2VertexMap().remove(this.vertexAliasMap.get(rootVertexAlias));
    return kgGraph;
  }
}
