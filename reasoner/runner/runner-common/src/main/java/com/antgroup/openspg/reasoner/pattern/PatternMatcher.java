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

package com.antgroup.openspg.reasoner.pattern;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.SPO;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.type.MapType2IdFactory;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.lube.common.pattern.Connection;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.antgroup.openspg.reasoner.lube.common.pattern.PatternElement;
import com.antgroup.openspg.reasoner.lube.common.rule.Rule;
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.antgroup.openspg.reasoner.warehouse.utils.DebugVertexIdSet;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.collection.JavaConversions;

public class PatternMatcher implements Serializable {
  private static final Logger log = LoggerFactory.getLogger(PatternMatcher.class);
  private static final long serialVersionUID = -484913653122295682L;
  private final String taskId;
  private final GraphState<IVertexId> graphState;
  private long initTime;
  private boolean debugEnable = false;

  public PatternMatcher(String taskId, GraphState<IVertexId> graphState) {
    this.taskId = taskId;
    this.graphState = graphState;
    this.initTime = System.currentTimeMillis();
  }

  public void resetInitTime() {
    this.initTime = System.currentTimeMillis();
  }

  /**
   * set enable flag
   *
   * @param debugEnable
   */
  public void setDebugEnable(boolean debugEnable) {
    this.debugEnable = debugEnable;
  }

  /**
   * vertex center pattern match
   *
   * @param id vertex id
   * @param pattern pattern
   * @param limit, max result limit, null means no constraints are applied
   * @return
   */
  public KgGraph<IVertexId> patternMatch(
      IVertexId id,
      Long startVersion,
      Long endVersion,
      Pattern pattern,
      List<String> rootVertexRuleList,
      Map<String, List<String>> dstVertexRuleMap,
      Map<String, List<String>> edgeRuleMap,
      Map<String, Set<IVertexId>> edgeValidTargetIdSet,
      Rule vertexRule,
      Map<String, List<Rule>> edgeTypeRuleMap,
      Long limit,
      boolean enableStarPathLimit,
      long timeoutMillis) {

    if (timeoutMillis != 0 && (System.currentTimeMillis() - this.initTime) > timeoutMillis) {
      log.warn("PatternMatcher patternMatch timeout id=" + id.toString());
      return null;
    }
    IVertex<IVertexId, IProperty> vertex = graphState.getVertex(id, endVersion, vertexRule);
    if (null == vertex) {
      if (this.debugEnable) {
        log.warn("PatternMatcher patternMatch get id=" + id.toString() + " is null");
      }
      return null;
    }

    KgGraph<IVertexId> kgGraph = new KgGraphImpl();
    Map<String, List<IEdge<IVertexId, IProperty>>> adjEdges = new HashMap<>();

    // check root types
    PatternElement patternElement = pattern.root();
    if (!patternElement.getMetaTypeNames().contains(RunnerUtil.getVertexType(vertex))) {
      if (this.debugEnable) {
        log.warn(
            "PatternMatcher patternMatch meta type match failed id="
                + id.toString()
                + " need="
                + patternElement.getMetaTypeNames()
                + ", value is="
                + RunnerUtil.getVertexType(vertex));
      }
      return null;
    }

    // check root vertex rules
    Map<String, Object> vertexContext = RunnerUtil.vertexContext(vertex, pattern.root().alias());
    if (!rootVertexRuleList.isEmpty()
        && !RuleRunner.getInstance().check(vertexContext, rootVertexRuleList, this.taskId)) {
      if (DebugVertexIdSet.DEBUG_VERTEX_ID_SET.contains(vertex.getId()) || this.debugEnable) {
        log.info(
            "PatternMatch check vertex rule false, vertexContext="
                + JSON.toJSONString(vertexContext)
                + "rules = "
                + JSON.toJSONString(rootVertexRuleList));
      }
      return null;
    }

    // start match edges
    Option<scala.collection.immutable.Set<Connection>> patternConnectionSet =
        pattern.topology().get(pattern.root().alias());
    if (patternConnectionSet.isEmpty()) {
      // no edges need match
      kgGraph.init(vertex, adjEdges, pattern);
      return kgGraph;
    }
    Set<Connection> patternConnections = JavaConversions.setAsJavaSet(patternConnectionSet.get());

    Map<String, Map<Direction, ArrayList<IEdge<IVertexId, IProperty>>>> edgeTypeDirectionMap =
        new TreeMap<>();
    for (Connection patternConnection : patternConnections) {
      Direction direction = patternConnection.direction();
      List<String> edgeSpoTypeList = getEdgeSpoTypeList(patternConnection, pattern, id.getType());
      for (String edgeType : edgeSpoTypeList) {
        Map<Direction, ArrayList<IEdge<IVertexId, IProperty>>> directionArrayListMap =
            edgeTypeDirectionMap.computeIfAbsent(edgeType, k -> new TreeMap<>());
        if (Direction.BOTH.equals(direction)) {
          directionArrayListMap.computeIfAbsent(Direction.OUT, k -> new ArrayList<>());
          directionArrayListMap.computeIfAbsent(Direction.IN, k -> new ArrayList<>());
        } else {
          directionArrayListMap.computeIfAbsent(direction, k -> new ArrayList<>());
        }
      }
    }

    // query edges from graph state
    for (String edgeType : edgeTypeDirectionMap.keySet()) {
      Map<Direction, ArrayList<IEdge<IVertexId, IProperty>>> directionArrayListMap =
          edgeTypeDirectionMap.get(edgeType);
      for (Direction direction : directionArrayListMap.keySet()) {
        ArrayList<IEdge<IVertexId, IProperty>> edgeList = directionArrayListMap.get(direction);
        List<IEdge<IVertexId, IProperty>> edges =
            graphState.getEdges(
                id,
                startVersion,
                endVersion,
                Sets.newHashSet(edgeType),
                direction,
                edgeTypeRuleMap);
        if (this.debugEnable) {
          log.warn(
              "PatternMatcher patternMatch get edge id="
                  + id.toString()
                  + " edgeType="
                  + edgeType
                  + ", direction="
                  + direction
                  + " edgeTypeRuleMap="
                  + JSON.toJSONString(edgeTypeRuleMap)
                  + " edges="
                  + JSON.toJSONString(edges));
        }
        edgeList.addAll(edges);
      }
    }

    // do edge match
    for (Connection patternConnection : patternConnections) {
      String edgeAlias = patternConnection.alias();
      Direction direction = patternConnection.direction();
      ArrayList<IEdge<IVertexId, IProperty>> willMatchEdgeList = new ArrayList<>();
      for (String edgeType : getEdgeSpoTypeList(patternConnection, pattern, id.getType())) {
        Map<Direction, ArrayList<IEdge<IVertexId, IProperty>>> directionArrayListMap =
            edgeTypeDirectionMap.get(edgeType);
        if (Direction.BOTH.equals(direction)) {
          directionArrayListMap.values().forEach(willMatchEdgeList::addAll);
        } else {
          willMatchEdgeList.addAll(directionArrayListMap.get(direction));
        }
      }

      Set<IVertexId> validVertexIdSet = edgeValidTargetIdSet.get(edgeAlias);
      if (null != validVertexIdSet) {
        willMatchEdgeList.removeIf(
            new Predicate<IEdge<IVertexId, IProperty>>() {
              @Override
              public boolean test(IEdge<IVertexId, IProperty> edge) {
                return !validVertexIdSet.contains(edge.getTargetId());
              }
            });
        if (this.debugEnable) {
          log.warn(
              "PatternMatcher patternMatch validVertexIdSet id="
                  + id.toString()
                  + " willMatchEdgeList="
                  + JSON.toJSONString(willMatchEdgeList)
                  + " validVertexIdSet="
                  + JSON.toJSONString(validVertexIdSet));
        }
      }

      // check dst vertex rule list
      List<String> dstVertexRuleList = dstVertexRuleMap.get(patternConnection.target());
      if (CollectionUtils.isNotEmpty(dstVertexRuleList)) {
        willMatchEdgeList.removeIf(
            new Predicate<IEdge<IVertexId, IProperty>>() {
              @Override
              public boolean test(IEdge<IVertexId, IProperty> e) {
                Map<String, Object> vertexPropertyMap = RunnerUtil.dstVertexContext(e);
                Map<String, Object> context = new HashMap<>();
                context.put(patternConnection.target(), vertexPropertyMap);
                return !RuleRunner.getInstance().check(context, dstVertexRuleList, taskId);
              }
            });
        if (this.debugEnable) {
          log.warn(
              "PatternMatcher patternMatch dstVertexRuleList id="
                  + id.toString()
                  + " willMatchEdgeList="
                  + JSON.toJSONString(willMatchEdgeList)
                  + " dstVertexRuleList="
                  + JSON.toJSONString(dstVertexRuleList));
        }
      }
      List<IEdge<IVertexId, IProperty>> validEdges =
          matchEdges(
              vertexContext, willMatchEdgeList, patternConnection, pattern, edgeRuleMap, limit);
      if (CollectionUtils.isEmpty(validEdges)) {
        // one edge pattern connection no match
        if (DebugVertexIdSet.DEBUG_VERTEX_ID_SET.contains(vertex.getId()) || this.debugEnable) {
          log.info(
              "PatternMatch edge not match, vertexContext="
                  + JSON.toJSONString(vertexContext)
                  + ", willMatchEdgeList="
                  + JSON.toJSONString(willMatchEdgeList)
                  + ", patternConnection="
                  + patternConnections
                  + ", pattern="
                  + pattern
                  + ", edgeRuleMap="
                  + JSON.toJSONString(edgeRuleMap)
                  + ", limit="
                  + limit);
        }
        return null;
      }
      adjEdges.put(edgeAlias, validEdges);
    }

    // do path limit
    if (null != limit && enableStarPathLimit && adjEdges.size() > 1) {
      RunnerUtil.doStarPathLimit(adjEdges, limit, 10);
    }

    kgGraph.init(vertex, adjEdges, pattern);
    return kgGraph;
  }

  private List<IEdge<IVertexId, IProperty>> matchEdges(
      Map<String, Object> vertexContext,
      ArrayList<IEdge<IVertexId, IProperty>> edgeList,
      Connection patternConnection,
      Pattern pattern,
      Map<String, List<String>> edgeRuleMap,
      Long totalLimit) {
    ArrayList<IEdge<IVertexId, IProperty>> result = new ArrayList<>();
    Map<String, Long> edgeTypeCountMap = new HashMap<>();
    Long totalCount = 0L;
    for (IEdge<IVertexId, IProperty> edge : edgeList) {
      String edgeType = edge.getType();
      if (!edgeTypeCountMap.containsKey(edgeType)) {
        edgeTypeCountMap.put(edgeType, 0L);
      }
      if (!isEdgeMatch(
          vertexContext,
          edge,
          patternConnection,
          pattern,
          edgeRuleMap.get(patternConnection.alias()))) {
        continue;
      }
      totalCount = totalCount + 1;
      if (null != totalLimit && totalCount > totalLimit) {
        break;
      }
      long currentEdgeTypeCount = edgeTypeCountMap.get(edgeType) + 1;
      edgeTypeCountMap.put(edgeType, currentEdgeTypeCount);
      if (null != patternConnection.limit() && patternConnection.limit() > 0 && currentEdgeTypeCount > patternConnection.limit()) {
        continue;
      }
      result.add(edge);
    }
    result.trimToSize();
    return result;
  }

  private boolean isEdgeMatch(
      Map<String, Object> vertexContext,
      IEdge<IVertexId, IProperty> edge,
      Connection patternConnection,
      Pattern pattern,
      List<String> edgeRuleList) {
    SPO spo = new SPO(edge.getType());
    // edge type match
    if (!patternConnection.relTypes().contains(spo.getP())) {
      return false;
    }

    // edge target type match
    if (!pattern
        .getNode(patternConnection.target())
        .getMetaTypeNames()
        .contains(RunnerUtil.getEdgeTargetType(edge))) {
      return false;
    }

    // edge source type match
    if (!pattern
        .getNode(patternConnection.source())
        .getMetaTypeNames()
        .contains(RunnerUtil.getEdgeSourceType(edge))) {
      return false;
    }

    // edge rule match
    if (null != edgeRuleList && !edgeRuleList.isEmpty()) {
      Map<String, Object> edgeContext =
          RunnerUtil.edgeContext(edge, spo, patternConnection.alias());
      edgeContext.putAll(vertexContext);
      if (!RuleRunner.getInstance().check(edgeContext, edgeRuleList, this.taskId)) {
        return false;
      }
    }

    return true;
  }

  private List<String> getEdgeSpoTypeList(
      Connection patternConnection, Pattern pattern, String sType) {
    List<String> edgeSpoTypeList = new ArrayList<>();
    Set<String> sSet = Sets.newHashSet(sType);
    Set<String> pSet =
        Sets.newHashSet(JavaConversions.asJavaCollection(patternConnection.relTypes()));
    Set<String> oSet =
        Sets.newHashSet(
            JavaConversions.asJavaCollection(
                pattern.getNode(patternConnection.target()).typeNames()));
    if (Direction.IN.equals(patternConnection.direction())) {
      Set<String> tmp = sSet;
      sSet = oSet;
      oSet = tmp;
    } else if (Direction.BOTH.equals(patternConnection.direction())) {
      oSet.add(sType);
      sSet = oSet;
    }

    for (String s : sSet) {
      for (String p : pSet) {
        for (String o : oSet) {
          SPO spo = new SPO(s, p, o);
          String spoStr = spo.toString();
          Long id = MapType2IdFactory.getMapType2Id().getIdByType(spoStr);
          if (null != id) {
            edgeSpoTypeList.add(spoStr);
          }
        }
      }
    }
    edgeSpoTypeList.sort(String::compareTo);
    return edgeSpoTypeList;
  }
}
