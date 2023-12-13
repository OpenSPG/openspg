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

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KgGraphFirstEdgeAggImpl implements Serializable {

  private final List<String> firstEdgeAliasList;

  /** init */
  public KgGraphFirstEdgeAggImpl(List<String> firstEdgeAliasList) {
    this.firstEdgeAliasList = firstEdgeAliasList;
  }

  /** first edge */
  public KgGraph<IVertexId> map(KgGraph<IVertexId> value) {
    for (String edgeAlias : firstEdgeAliasList) {
      KgGraphImpl kgGraph = (KgGraphImpl) value;
      Set<IEdge<IVertexId, IProperty>> edgeSet = kgGraph.getAlias2EdgeMap().get(edgeAlias);
      Set<FirstEdgeKey> keySet = new HashSet<>();
      Set<IEdge<IVertexId, IProperty>> newEdgeSet = new HashSet<>();
      for (IEdge<IVertexId, IProperty> edge : edgeSet) {
        FirstEdgeKey key = new FirstEdgeKey(edge.getSourceId(), edge.getTargetId());
        if (keySet.contains(key)) {
          continue;
        }
        keySet.add(key);
        newEdgeSet.add(edge);
      }
      kgGraph.getAlias2EdgeMap().put(edgeAlias, newEdgeSet);
    }
    return value;
  }

  protected static class FirstEdgeKey {
    private final byte[] keyBytes = new byte[Long.BYTES * 4];

    /** init first edge key */
    public FirstEdgeKey(IVertexId id1, IVertexId id2) {
      System.arraycopy(id1.getBytes(), 0, keyBytes, 0, id1.getBytes().length);
      System.arraycopy(id2.getBytes(), 0, keyBytes, Long.BYTES * 2, id2.getBytes().length);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(this.keyBytes);
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof FirstEdgeKey)) {
        return false;
      }
      FirstEdgeKey that = (FirstEdgeKey) obj;
      return Arrays.equals(this.keyBytes, that.keyBytes);
    }
  }
}
