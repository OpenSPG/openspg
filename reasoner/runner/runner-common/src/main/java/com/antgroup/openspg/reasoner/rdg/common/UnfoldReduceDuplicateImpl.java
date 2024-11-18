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
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.lube.common.pattern.Connection;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnfoldReduceDuplicateImpl implements KgGraphListProcess {
  private final String edgeAlias;
  private final List<String> edgeAliasList = new ArrayList<>();

  public UnfoldReduceDuplicateImpl(Pattern schema, String edgeAlias) {
    this.edgeAlias = edgeAlias;
    for (Connection connection : RunnerUtil.getConnectionSet(schema)) {
      if (connection.alias().equals(this.edgeAlias)) {
        continue;
      }
      this.edgeAliasList.add(connection.alias());
    }
  }

  @Override
  public List<KgGraph<IVertexId>> reduce(Collection<KgGraph<IVertexId>> kgGraphs) {
    Set<PathDuplicateKey> pathEdgeKeySet = new HashSet<>();
    List<KgGraph<IVertexId>> result = new ArrayList<>();
    for (KgGraph<IVertexId> kgGraph : kgGraphs) {
      PathDuplicateKey key = new PathDuplicateKey(kgGraph);
      if (pathEdgeKeySet.contains(key)) {
        continue;
      }
      pathEdgeKeySet.add(key);
      result.add(kgGraph);
    }
    return result;
  }

  private class PathDuplicateKey {
    private final List<IEdge<IVertexId, IProperty>> edgeList = new ArrayList<>();

    public PathDuplicateKey(KgGraph<IVertexId> kgGraph) {
      addEdges(kgGraph);
    }

    public void addEdges(KgGraph<IVertexId> kgGraph) {
      this.edgeList.add(kgGraph.getEdge(edgeAlias).get(0));
      for (String edgeAlias : edgeAliasList) {
        IEdge<IVertexId, IProperty> edge = kgGraph.getEdge(edgeAlias).get(0);
        this.edgeList.add(edge);
      }
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      PathDuplicateKey that = (PathDuplicateKey) o;
      return Arrays.equals(this.edgeList.toArray(), that.edgeList.toArray());
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(this.edgeList.toArray());
    }
  }
}
