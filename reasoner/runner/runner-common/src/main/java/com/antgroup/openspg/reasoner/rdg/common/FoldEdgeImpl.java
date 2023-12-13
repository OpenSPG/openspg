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
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.OptionalEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.PathEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.MirrorVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.lube.common.pattern.Connection;
import com.antgroup.openspg.reasoner.lube.common.pattern.PartialGraphPattern;
import com.antgroup.openspg.reasoner.util.PathConnection;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FoldEdgeImpl implements Serializable {

  private static final long serialVersionUID = -3632032496948942544L;
  private final FoldRepeatEdgeInfo foldRepeatEdgeInfo;

  private FoldRepeatEdgeInfo noneMatchRepeatFoldInfo = null;

  public FoldEdgeImpl(PartialGraphPattern kgGraphSchema, FoldRepeatEdgeInfo foldRepeatEdgeInfo) {
    for (Connection connection : RunnerUtil.getConnectionSet(kgGraphSchema)) {
      if (!(connection instanceof PathConnection)) {
        continue;
      }
      PathConnection pathConnection = (PathConnection) connection;
      noneMatchRepeatFoldInfo =
          new FoldRepeatEdgeInfo(
              pathConnection.alias(),
              pathConnection.alias(),
              pathConnection.source(),
              pathConnection.target());
    }
    this.foldRepeatEdgeInfo = foldRepeatEdgeInfo;
  }

  private void foldNone(KgGraphImpl kgGraph) {
    Set<IVertex<IVertexId, IProperty>> vertexSet =
        kgGraph.getAlias2VertexMap().get(this.noneMatchRepeatFoldInfo.getFromVertexAlias());
    Set<IVertex<IVertexId, IProperty>> newVertexSet = new HashSet<>();
    Set<IEdge<IVertexId, IProperty>> newEdgeSet = new HashSet<>();
    for (IVertex<IVertexId, IProperty> vertex : vertexSet) {
      IVertex<IVertexId, IProperty> newVertex = new MirrorVertex<>(vertex);
      newVertexSet.add(newVertex);

      IEdge<IVertexId, IProperty> newEdge = new OptionalEdge<>(vertex.getId(), vertex.getId());
      newEdgeSet.add(newEdge);
    }
    kgGraph.getAlias2VertexMap().put(this.noneMatchRepeatFoldInfo.getToVertexAlias(), newVertexSet);
    kgGraph.getAlias2EdgeMap().put(this.noneMatchRepeatFoldInfo.getToEdgeAlias(), newEdgeSet);
  }

  public List<KgGraph<IVertexId>> fold(KgGraphImpl kgGraph) {
    List<KgGraph<IVertexId>> result = new ArrayList<>();
    Set<IEdge<IVertexId, IProperty>> pathEdgeSet =
        kgGraph.getAlias2EdgeMap().get(this.foldRepeatEdgeInfo.getToEdgeAlias());

    Set<IEdge<IVertexId, IProperty>> fromEdgeSet =
        kgGraph.getAlias2EdgeMap().remove(this.foldRepeatEdgeInfo.getFromEdgeAlias());
    if (null == fromEdgeSet || fromEdgeSet.isEmpty()) {
      // repeat match nothing
      Set<IVertex<IVertexId, IProperty>> toVertexSet =
          kgGraph.getAlias2VertexMap().get(this.foldRepeatEdgeInfo.getToVertexAlias());
      if (null == toVertexSet) {
        foldNone(kgGraph);
      }
      result.add(kgGraph);
      return result;
    } else {
      IEdge<IVertexId, IProperty> edge = fromEdgeSet.iterator().next();
      if (edge instanceof OptionalEdge) {
        if (null != pathEdgeSet) {
          kgGraph.getAlias2VertexMap().remove(this.foldRepeatEdgeInfo.getFromVertexAlias());
          result.add(kgGraph);
          return result;
        }
        Set<IVertex<IVertexId, IProperty>> fromVertexSet =
            kgGraph.getAlias2VertexMap().remove(this.foldRepeatEdgeInfo.getFromVertexAlias());

        Set<IVertex<IVertexId, IProperty>> newVertexSet = new HashSet<>();
        for (IVertex<IVertexId, IProperty> vertex : fromVertexSet) {
          newVertexSet.add(new MirrorVertex<>(vertex));
        }
        kgGraph.getAlias2VertexMap().put(this.foldRepeatEdgeInfo.getToVertexAlias(), newVertexSet);
        kgGraph.getAlias2EdgeMap().put(this.foldRepeatEdgeInfo.getToEdgeAlias(), fromEdgeSet);
        result.add(kgGraph);
        return result;
      }
    }

    if (null == pathEdgeSet) {
      // 第一个wind，构造PathEdge
      Set<IEdge<IVertexId, IProperty>> toEdgeSet = new HashSet<>();
      for (IEdge<IVertexId, IProperty> edge : fromEdgeSet) {
        PathEdge<IVertexId, IProperty, IProperty> pathEdge =
            new PathEdge<>((Edge<IVertexId, IProperty>) edge);
        toEdgeSet.add(pathEdge);
      }
      kgGraph.getAlias2EdgeMap().put(this.foldRepeatEdgeInfo.getToEdgeAlias(), toEdgeSet);

      // 重命名点
      kgGraph
          .getAlias2VertexMap()
          .put(
              this.foldRepeatEdgeInfo.getToVertexAlias(),
              kgGraph.getAlias2VertexMap().remove(this.foldRepeatEdgeInfo.getFromVertexAlias()));
      result.add(kgGraph);
      return result;
    }

    // 已经存在PathEdge，增加PathEdge的长度

    // 一般这个map只有一个value
    Set<IVertex<IVertexId, IProperty>> toVertexSet =
        kgGraph.getAlias2VertexMap().get(this.foldRepeatEdgeInfo.getToVertexAlias());
    Map<IVertexId, IVertex<IVertexId, IProperty>> toVertexMap = new HashMap<>();
    for (IVertex<IVertexId, IProperty> v : toVertexSet) {
      toVertexMap.put(v.getId(), v);
    }

    Map<IVertexId, List<IEdge<IVertexId, IProperty>>> fromEdgeMap = new HashMap<>();
    for (IEdge<IVertexId, IProperty> edge : fromEdgeSet) {
      fromEdgeMap.computeIfAbsent(edge.getSourceId(), k -> new ArrayList<>()).add(edge);
    }

    // 开始构造新的PathEdge
    Set<IEdge<IVertexId, IProperty>> newPathEdgeSet = new HashSet<>();
    for (IEdge<IVertexId, IProperty> edge : pathEdgeSet) {
      PathEdge<IVertexId, IProperty, IProperty> pathEdge =
          (PathEdge<IVertexId, IProperty, IProperty>) edge;
      IVertexId pathEdgeSearchId = pathEdge.getTargetId();
      Vertex<IVertexId, IProperty> nextVertex =
          (Vertex<IVertexId, IProperty>) toVertexMap.get(pathEdgeSearchId);
      List<IEdge<IVertexId, IProperty>> nextEdgeList = fromEdgeMap.get(pathEdgeSearchId);
      for (IEdge<IVertexId, IProperty> nextIEdge : nextEdgeList) {
        Edge<IVertexId, IProperty> nextEdge = (Edge<IVertexId, IProperty>) nextIEdge;
        PathEdge<IVertexId, IProperty, IProperty> newPathEdge =
            new PathEdge<>(pathEdge, nextVertex, nextEdge);
        if (newPathEdge.haveLoop()) {
          continue;
        }
        newPathEdgeSet.add(newPathEdge);
      }
    }

    kgGraph.getAlias2EdgeMap().put(this.foldRepeatEdgeInfo.getToEdgeAlias(), newPathEdgeSet);
    kgGraph
        .getAlias2VertexMap()
        .put(
            this.foldRepeatEdgeInfo.getToVertexAlias(),
            kgGraph.getAlias2VertexMap().remove(this.foldRepeatEdgeInfo.getFromVertexAlias()));
    result.add(kgGraph);
    return result;
  }
}
