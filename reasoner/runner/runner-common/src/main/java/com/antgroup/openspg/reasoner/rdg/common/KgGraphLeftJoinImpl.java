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
import com.antgroup.openspg.reasoner.common.graph.edge.impl.OptionalEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.NoneVertex;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.lube.common.pattern.Connection;
import com.antgroup.openspg.reasoner.lube.common.pattern.PartialGraphPattern;
import com.antgroup.openspg.reasoner.lube.common.pattern.PatternElement;
import com.antgroup.openspg.reasoner.lube.logical.planning.FullOuterJoin$;
import com.antgroup.openspg.reasoner.lube.logical.planning.JoinType;
import com.antgroup.openspg.reasoner.lube.logical.planning.LeftOuterJoin$;
import com.antgroup.openspg.reasoner.util.KgGraphSchema;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import scala.Tuple2;
import scala.collection.JavaConversions;

public class KgGraphLeftJoinImpl implements Serializable {
  private final JoinType joinType;
  private final String joinRootAlias;
  private final Set<String> joinAliasSet;
  private final Long pathLimit;

  private final List<Connection> joinNoneEdgeOrder;

  private final List<String> overlapVertexAlias = new ArrayList<>();
  private final List<String> overlapEdgeAlias = new ArrayList<>();

  public KgGraphLeftJoinImpl(
      JoinType joinType,
      List<String> onAlias,
      PartialGraphPattern leftSchema,
      PartialGraphPattern rightSchema,
      Long pathLimit) {
    this.joinType = joinType;
    this.joinRootAlias = onAlias.get(0);
    this.joinAliasSet = Sets.newHashSet(onAlias);
    this.pathLimit = pathLimit;
    this.joinNoneEdgeOrder = RunnerUtil.getJoinNoneEdgeOrder(this.joinRootAlias, rightSchema);

    Tuple2<
            scala.collection.immutable.Set<PatternElement>,
            scala.collection.immutable.Set<Connection>>
        tuple2 = KgGraphSchema.getOverlapSchema(leftSchema, rightSchema);
    for (PatternElement patternElement : JavaConversions.setAsJavaSet(tuple2._1())) {
      this.overlapVertexAlias.add(patternElement.alias());
    }
    for (Connection connection : JavaConversions.setAsJavaSet(tuple2._2())) {
      this.overlapEdgeAlias.add(connection.alias());
    }
  }

  public List<KgGraph<IVertexId>> join(
      Collection<KgGraph<IVertexId>> left, Collection<KgGraph<IVertexId>> right) {
    long count = 0;
    List<KgGraph<IVertexId>> result = new ArrayList<>();
    for (KgGraph<IVertexId> tmpLeftKgGraph : left) {
      KgGraphImpl leftKgGraph = (KgGraphImpl) tmpLeftKgGraph;
      if (null != this.pathLimit && count > this.pathLimit) {
        continue;
      }
      boolean leftOuterJoinDone =
          !(LeftOuterJoin$.MODULE$.equals(joinType) || FullOuterJoin$.MODULE$.equals(joinType));
      if (CollectionUtils.isNotEmpty(right)) {
        for (KgGraph<IVertexId> tmpRightKgGraph : right) {
          if (null != this.pathLimit && count > this.pathLimit) {
            break;
          }
          KgGraphImpl rightKgGraph = (KgGraphImpl) tmpRightKgGraph;

          boolean otherVertexIdEquals = true;
          for (String alias : this.joinAliasSet) {
            IVertex<IVertexId, IProperty> leftJoinVertex = leftKgGraph.getVertex(alias).get(0);
            IVertex<IVertexId, IProperty> rightJoinVertex = rightKgGraph.getVertex(alias).get(0);
            if (!leftJoinVertex.getId().equals(rightJoinVertex.getId())) {
              // id not equals
              otherVertexIdEquals = false;
              break;
            }
          }
          if (!otherVertexIdEquals) {
            continue;
          }

          KgGraphImpl newKgGraph = selectValidGraphItem(leftKgGraph, rightKgGraph);
          if (newKgGraph.checkDuplicateVertex()) {
            continue;
          }
          leftOuterJoinDone = true;
          result.add(newKgGraph);
          count++;
        }
      }
      if (!leftOuterJoinDone) {
        RunnerUtil.kgGraphJoinNone(leftKgGraph, joinNoneEdgeOrder);
        result.add(leftKgGraph);
        count++;
      }
    }
    return result;
  }

  private KgGraphImpl selectValidGraphItem(KgGraphImpl left, KgGraphImpl right) {
    KgGraphImpl newKgGraph = new KgGraphImpl();
    for (String vertexAlais : this.overlapVertexAlias) {
      Set<IVertex<IVertexId, IProperty>> leftSet = left.getAlias2VertexMap().get(vertexAlais);
      Set<IVertex<IVertexId, IProperty>> rightSet = right.getAlias2VertexMap().get(vertexAlais);
      if (this.joinAliasSet.contains(vertexAlais)) {
        IVertex<IVertexId, IProperty> leftVertex = leftSet.iterator().next();
        if (leftVertex instanceof NoneVertex) {
          newKgGraph.getAlias2VertexMap().put(vertexAlais, leftSet);
        } else {
          if (null == leftSet.iterator().next().getValue()) {
            newKgGraph.getAlias2VertexMap().put(vertexAlais, rightSet);
          } else {
            newKgGraph.getAlias2VertexMap().put(vertexAlais, leftSet);
          }
        }
      } else {
        if (CollectionUtils.isEmpty(leftSet)) {
          newKgGraph.getAlias2VertexMap().put(vertexAlais, rightSet);
          continue;
        } else if (CollectionUtils.isEmpty(rightSet)) {
          newKgGraph.getAlias2VertexMap().put(vertexAlais, leftSet);
          continue;
        }
        Map<IVertexId, IVertex<IVertexId, IProperty>> selectMap = new HashMap<>();
        for (IVertex<IVertexId, IProperty> vertex : leftSet) {
          if (vertex instanceof NoneVertex) {
            continue;
          }
          IVertex<IVertexId, IProperty> existVertex = selectMap.getOrDefault(vertex.getId(), null);
          if (null == existVertex || null == existVertex.getValue()) {
            selectMap.put(vertex.getId(), vertex);
          }
        }
        for (IVertex<IVertexId, IProperty> vertex : rightSet) {
          if (vertex instanceof NoneVertex) {
            continue;
          }
          IVertex<IVertexId, IProperty> existVertex = selectMap.getOrDefault(vertex.getId(), null);
          if (null == existVertex || null == existVertex.getValue()) {
            selectMap.put(vertex.getId(), vertex);
          }
        }
        newKgGraph.getAlias2VertexMap().put(vertexAlais, new HashSet<>(selectMap.values()));
      }
    }

    for (String edgeAlias : this.overlapEdgeAlias) {
      Set<IEdge<IVertexId, IProperty>> leftSet = left.getAlias2EdgeMap().get(edgeAlias);
      Set<IEdge<IVertexId, IProperty>> rightSet = right.getAlias2EdgeMap().get(edgeAlias);

      if (CollectionUtils.isEmpty(leftSet)) {
        newKgGraph.getAlias2EdgeMap().put(edgeAlias, rightSet);
        continue;
      } else if (CollectionUtils.isEmpty(rightSet)) {
        newKgGraph.getAlias2EdgeMap().put(edgeAlias, leftSet);
        continue;
      }

      Map<String, IEdge<IVertexId, IProperty>> selectMap = new HashMap<>();
      for (IEdge<IVertexId, IProperty> edge : leftSet) {
        if (edge instanceof OptionalEdge) {
          continue;
        }
        String edgeKey = RunnerUtil.getEdgeIdentifier(edge, null);
        IEdge<IVertexId, IProperty> existEdge = selectMap.getOrDefault(edgeKey, null);
        if (null == existEdge || null == existEdge.getValue()) {
          selectMap.put(edgeKey, edge);
        }
      }
      for (IEdge<IVertexId, IProperty> edge : rightSet) {
        if (edge instanceof OptionalEdge) {
          continue;
        }
        String edgeKey = RunnerUtil.getEdgeIdentifier(edge, null);
        IEdge<IVertexId, IProperty> existEdge = selectMap.getOrDefault(edgeKey, null);
        if (null == existEdge || null == existEdge.getValue()) {
          selectMap.put(edgeKey, edge);
        }
      }
      newKgGraph.getAlias2EdgeMap().put(edgeAlias, new HashSet<>(selectMap.values()));
    }

    for (Map.Entry<String, Set<IVertex<IVertexId, IProperty>>> entry :
        left.getAlias2VertexMap().entrySet()) {
      if (this.overlapVertexAlias.contains(entry.getKey())) {
        continue;
      }
      newKgGraph.getAlias2VertexMap().put(entry.getKey(), entry.getValue());
    }
    for (Map.Entry<String, Set<IVertex<IVertexId, IProperty>>> entry :
        right.getAlias2VertexMap().entrySet()) {
      if (this.overlapVertexAlias.contains(entry.getKey())) {
        continue;
      }
      newKgGraph.getAlias2VertexMap().put(entry.getKey(), entry.getValue());
    }

    for (Map.Entry<String, Set<IEdge<IVertexId, IProperty>>> entry :
        left.getAlias2EdgeMap().entrySet()) {
      if (this.overlapEdgeAlias.contains(entry.getKey())) {
        continue;
      }
      newKgGraph.getAlias2EdgeMap().put(entry.getKey(), entry.getValue());
    }
    for (Map.Entry<String, Set<IEdge<IVertexId, IProperty>>> entry :
        right.getAlias2EdgeMap().entrySet()) {
      if (this.overlapEdgeAlias.contains(entry.getKey())) {
        continue;
      }
      newKgGraph.getAlias2EdgeMap().put(entry.getKey(), entry.getValue());
    }
    return newKgGraph;
  }
}
