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

package com.antgroup.openspg.reasoner.graphstate.impl;

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.IVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.common.utils.PropertyUtil;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.graphstate.model.MergeTypeEnum;
import com.antgroup.openspg.reasoner.lube.common.rule.Rule;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple3;
import scala.Tuple5;

/**
 * Graph State based on memory The multi-version vertex is implemented through the multi-version
 * property The multi-version edge is implemented through multiple edges
 */
public class MemGraphState implements GraphState<IVertexId> {
  private static final Logger log = LoggerFactory.getLogger(MemGraphState.class);

  /**
   * vertex map key is s, value is vertex property vertex property is Map<key, Map<longVersion,
   * value>>
   */
  protected Map<IVertexId, IProperty> vertexMap = new HashMap<>();

  /** in edges map key is s, value is p_o_t */
  Map<IVertexId, Set<Tuple3<String, IVertexId, Long>>> inEdgeMap = new HashMap<>();

  /** out edges map key is s, value is p_o_t */
  Map<IVertexId, Set<Tuple3<String, IVertexId, Long>>> outEdgeMap = new HashMap<>();

  /** edge property map key is spot, value is edge property */
  Map<Tuple5<IVertexId, String, IVertexId, Direction, Long>, IProperty> edgePropertyMap =
      new HashMap<>();

  public static final String SEPARATOR = "\u0001";

  @Override
  public void init(Map<String, String> param) {}

  @Override
  public void addVertex(IVertex<IVertexId, IProperty> vertex) {
    vertexMap.put(vertex.getId(), vertex.getValue());
  }

  @Override
  public void addVertex(IVertex<IVertexId, IProperty> vertex, Long version) {
    this.addVertex(vertex);
  }

  @Override
  public void mergeVertexProperty(
      IVertexId id, Map<String, Object> property, MergeTypeEnum mergeType, Long version) {
    IVersionProperty iProperty = (IVersionProperty) vertexMap.get(id);
    if (iProperty == null) {
      log.info("MemGraphState get vertex is null " + id.getInternalId() + " " + id.getType());
      return;
    }
    for (String key : property.keySet()) {
      if (MergeTypeEnum.REPLACE.equals(mergeType)) {
        iProperty.put(key, property.get(key), version);
        continue;
      }
      String oldValue = ((String) iProperty.get(key, version));
      String newValue = oldValue == null ? "" : oldValue + SEPARATOR;
      iProperty.put(key, (newValue + property.get(key)), version);
    }
  }

  @Override
  public void setVertexCacheProperty(Map<String, Set<String>> properties) {}

  @Override
  public IVertex<IVertexId, IProperty> getVertex(IVertexId id, Long version) {
    IVersionProperty iProperty = (IVersionProperty) vertexMap.get(id);
    if (iProperty == null) {
      log.info("MemGraphState get vertex is null " + id.getInternalId() + " " + id.getType());
      return null;
    }
    IVersionProperty resultValue = PropertyUtil.buildVertexProperty(id, null);
    for (String key : iProperty.getKeySet()) {
      Object value = iProperty.get(key, version);
      if (value != null) {
        resultValue.put(key, value, version);
      }
    }
    return new Vertex<>(id, resultValue);
  }

  @Override
  public IVertex<IVertexId, IProperty> getVertex(IVertexId id, Long version, Rule rule) {
    return getVertex(id, version);
  }

  private void addEdgeToMap(
      IVertexId vertexId,
      List<IEdge<IVertexId, IProperty>> edges,
      Map<IVertexId, Set<Tuple3<String, IVertexId, Long>>> edgeMap) {
    Set<Tuple3<String, IVertexId, Long>> edgeNameSet =
        edgeMap.computeIfAbsent(vertexId, k -> new HashSet<>());
    for (IEdge<IVertexId, IProperty> edge : edges) {
      edgeNameSet.add(new Tuple3<>(edge.getType(), edge.getTargetId(), edge.getVersion()));
      Tuple5<IVertexId, String, IVertexId, Direction, Long> spot =
          new Tuple5<>(
              edge.getSourceId(),
              edge.getType(),
              edge.getTargetId(),
              edge.getDirection(),
              edge.getVersion());
      if (edgePropertyMap.containsKey(spot)) {
        continue;
      }
      edgePropertyMap.put(spot, edge.getValue());
    }
  }

  @Override
  public void addEdges(
      IVertexId vertexId,
      List<IEdge<IVertexId, IProperty>> inEdges,
      List<IEdge<IVertexId, IProperty>> outEdges) {
    if (CollectionUtils.isNotEmpty(inEdges)) {
      addEdgeToMap(vertexId, inEdges, inEdgeMap);
    }
    if (CollectionUtils.isNotEmpty(outEdges)) {
      addEdgeToMap(vertexId, outEdges, outEdgeMap);
    }
  }

  @Override
  public void updateEdgeProperty(
      IVertexId s, String p, IVertexId o, Long version, IProperty property) {
    List<Direction> directions = Lists.newArrayList(Direction.IN, Direction.OUT);
    Tuple5<IVertexId, String, IVertexId, Direction, Long> spot = null;
    for (Direction d : directions) {
      spot = new Tuple5<>(s, p, o, d, version);
      if (edgePropertyMap.containsKey(spot)) {
        break;
      }
    }
    if (spot == null || !edgePropertyMap.containsKey(spot)) {
      throw new RuntimeException("edge not found");
    }
    edgePropertyMap.put(spot, property);
  }

  @Override
  public void mergeEdgeProperty(
      IVertexId s,
      String p,
      IVertexId o,
      Long version,
      Direction direction,
      Map<String, Object> property,
      MergeTypeEnum mergeType) {
    IProperty originProp = null;
    Direction finalDirection = direction;
    if (finalDirection == null) {
      finalDirection = Direction.BOTH;
    }
    List<Direction> directions = Lists.newArrayList(finalDirection);
    if (Direction.BOTH == finalDirection) {
      directions = Lists.newArrayList(Direction.IN, Direction.OUT);
    }
    for (Direction d : directions) {
      Tuple5<IVertexId, String, IVertexId, Direction, Long> spot =
          new Tuple5<>(s, p, o, d, version);
      originProp = edgePropertyMap.get(spot);
      if (null != originProp) {
        break;
      }
    }

    if (null == originProp) {
      throw new RuntimeException("edge property not exist");
    }
    for (String key : property.keySet()) {
      if (MergeTypeEnum.REPLACE.equals(mergeType)) {
        originProp.put(key, property.get(key));
        continue;
      }
      String oldValue = ((String) originProp.get(key));
      String newValue = oldValue == null ? "" : oldValue + SEPARATOR;
      originProp.put(key, (newValue + property.get(key)));
    }
  }

  @Override
  public void setEdgeCacheProperty(Map<String, Set<String>> properties) {}

  private ArrayList<IEdge<IVertexId, IProperty>> filterEdge(
      IVertexId vertexId,
      Set<Tuple3<String, IVertexId, Long>> edgeSet,
      Long startVersion,
      Long endVersion,
      Set<String> types,
      Direction direction) {
    ArrayList<IEdge<IVertexId, IProperty>> result = new ArrayList<>();
    if (null == edgeSet) {
      return result;
    }
    for (Tuple3<String, IVertexId, Long> pot : edgeSet) {
      String type = pot._1();
      IVertexId o = pot._2();
      Long version = pot._3();
      if (endVersion != null && endVersion != 0) {
        // 存在多版本场景下才做判断
        if (!RunnerUtil.between(startVersion, endVersion, version)) {
          continue;
        }
      }

      if (null == types || types.contains(type)) {
        result.add(new Edge<>(vertexId, o, null, version, direction, type));
      }
    }
    return result;
  }

  @Override
  public List<IEdge<IVertexId, IProperty>> getEdgesWithoutProperty(
      IVertexId vertexId,
      Long startVersion,
      Long endVersion,
      Set<String> types,
      Direction direction) {
    if (null == direction) {
      throw new RuntimeException("direction cant be null!");
    }

    long defaultVersion = 0L;
    if (startVersion == null || endVersion == null) {
      startVersion = defaultVersion;
      endVersion = defaultVersion;
    }

    ArrayList<IEdge<IVertexId, IProperty>> result = new ArrayList<>();
    if (Direction.IN.equals(direction) || Direction.BOTH.equals(direction)) {
      result =
          filterEdge(
              vertexId, inEdgeMap.get(vertexId), startVersion, endVersion, types, Direction.IN);
    }

    if (Direction.OUT.equals(direction) || Direction.BOTH.equals(direction)) {
      ArrayList<IEdge<IVertexId, IProperty>> tmp =
          filterEdge(
              vertexId, outEdgeMap.get(vertexId), startVersion, endVersion, types, Direction.OUT);
      result.addAll(tmp);
    }
    result.trimToSize();
    return result;
  }

  @Override
  public List<IEdge<IVertexId, IProperty>> getEdges(
      IVertexId vertexId,
      Long startVersion,
      Long endVersion,
      Set<String> types,
      Direction direction) {
    List<IEdge<IVertexId, IProperty>> result =
        getEdgesWithoutProperty(vertexId, startVersion, endVersion, types, direction);
    result.forEach(
        edge -> {
          List<Direction> directions = Lists.newArrayList(direction);
          if (Direction.BOTH == direction) {
            directions = Lists.newArrayList(Direction.IN, Direction.OUT);
          }
          for (Direction d : directions) {
            Tuple5<IVertexId, String, IVertexId, Direction, Long> spot =
                new Tuple5<>(vertexId, edge.getType(), edge.getTargetId(), d, edge.getVersion());
            IProperty edgeProperty = edgePropertyMap.get(spot);
            if (edgeProperty == null) {
              edge.setValue(null);
            } else {
              edge.setValue(edgeProperty.clone());
              break;
            }
          }
        });
    return result;
  }

  @Override
  public List<IEdge<IVertexId, IProperty>> getEdges(
      IVertexId vertexId,
      Long startVersion,
      Long endVersion,
      Set<String> types,
      Direction direction,
      Map<String, List<Rule>> typeAndRuleMap) {
    return getEdges(vertexId, startVersion, endVersion, types, direction);
  }

  @Override
  public Iterator<IVertex<IVertexId, IProperty>> getVertexIterator(Set<String> vertexType) {
    return getVertexIterator(
        vertex -> {
          String type = RunnerUtil.getVertexType(vertex);
          if (null == vertexType) {
            return true;
          }
          return vertexType.contains(type);
        });
  }

  @Override
  public Iterator<IVertex<IVertexId, IProperty>> getVertexIterator(
      Predicate<IVertex<IVertexId, IProperty>> filter) {
    List<IVertex<IVertexId, IProperty>> vertexList = new ArrayList<>();
    for (IVertexId vertexId : vertexMap.keySet()) {
      IProperty property = vertexMap.get(vertexId);
      Vertex<IVertexId, IProperty> vertex = new Vertex<>(vertexId, property);
      if (filter.test(vertex)) {
        vertexList.add(vertex);
      }
    }
    return vertexList.iterator();
  }

  @Override
  public Iterator<IEdge<IVertexId, IProperty>> getEdgeIterator(Set<String> edgeType) {
    return getEdgeIterator(
        edge -> {
          if (null == edgeType) {
            return true;
          }
          return edgeType.contains(edge.getType());
        });
  }

  @Override
  public Iterator<IEdge<IVertexId, IProperty>> getEdgeIterator(
      Predicate<IEdge<IVertexId, IProperty>> filter) {
    List<IEdge<IVertexId, IProperty>> edgeList = new ArrayList<>();
    for (IVertexId vertexId : vertexMap.keySet()) {
      List<IEdge<IVertexId, IProperty>> edges =
          getEdges(vertexId, null, null, null, Direction.BOTH);
      edgeList.addAll(edges.stream().filter(filter).collect(Collectors.toList()));
    }
    return edgeList.iterator();
  }

  @Override
  public void checkPoint() {}

  @Override
  public void close() {}
}
