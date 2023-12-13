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

package com.antgroup.openspg.reasoner.kggraph.impl;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.OptionalEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.lube.common.pattern.Connection;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import scala.Tuple2;

/**
 * This class implements an iterator that combines edges into a path. taking as input a map of
 * schema and edges.
 *
 * <p>example1: schema is [A -E1-> B, B -E2-> C], edgeMap is {E1:[a1_b1, a1_b2], E2:[b1_c1, b1_c2]}
 * output is {E1:[a1_b1],E2:[b1_c1]}, {E1:[a1_b1],E2:[b1_c2]}
 *
 * @author donghai.ydh
 * @version EdgeCombinationIterator.java, v 0.1 2023年04月24日 14:18 donghai.ydh
 */
@Slf4j
public class EdgeCombinationIterator implements Iterator<KgGraph<IVertexId>> {

  private final Map<String, Map<IVertexId, IVertex<IVertexId, IProperty>>> alias2VertexMap;
  private final Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap;
  private final Map<String, Map<IVertexId, List<IEdge<IVertexId, IProperty>>>> aliasEdgeQueryMap;

  private final List<EdgeIterateInfo> edgeIterateInfoList;
  private final Map<String, Integer> edgeIterateOrderMap;

  private final Stack<Iterator<IEdge<IVertexId, IProperty>>> iteratorStack = new Stack<>();

  private final Stack<IEdge<IVertexId, IProperty>> resultEdgeStack = new Stack<>();

  private Long scope = null;

  public EdgeCombinationIterator(
      List<EdgeIterateInfo> edgeIterateInfoList,
      Map<String, Integer> edgeIterateOrderMap,
      Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap,
      Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap) {
    this.edgeIterateInfoList = edgeIterateInfoList;
    this.edgeIterateOrderMap = edgeIterateOrderMap;
    this.alias2VertexMap = initVertexQueryMap(alias2VertexMap);
    this.alias2EdgeMap = alias2EdgeMap;
    this.aliasEdgeQueryMap = initEdgeQueryMap(this.alias2EdgeMap, getEdgeIndexInfo());

    String firstEdgeAlias = this.edgeIterateInfoList.get(0).getEdgeAlias();
    Iterator<IEdge<IVertexId, IProperty>> edgeIt = alias2EdgeMap.get(firstEdgeAlias).iterator();
    iteratorStack.push(edgeIt);
  }

  private void pushNextIterator() {
    EdgeIterateInfo edgeIterateInfo = edgeIterateInfoList.get(iteratorStack.size());
    BuildEdgeIteratorInfo buildEdgeIteratorInfo = edgeIterateInfo.getBuildEdgeIteratorInfo();
    IVertexId queryKey = getQueryNextEdgeKey(buildEdgeIteratorInfo.getQueryKey());
    List<IEdge<IVertexId, IProperty>> edgeList =
        aliasEdgeQueryMap.get(buildEdgeIteratorInfo.getQueryEdgeAlias()).get(queryKey);
    if (CollectionUtils.isEmpty(edgeList)) {
      resultEdgeStack.pop();
      return;
    }
    Iterator<IEdge<IVertexId, IProperty>> edgeIt = edgeList.iterator();
    iteratorStack.push(edgeIt);
  }

  private IVertexId getQueryNextEdgeKey(Edge2VertexInfo queryKey) {
    int index = this.edgeIterateOrderMap.get(queryKey.getEdgeAlias());
    IEdge<IVertexId, IProperty> edge = this.resultEdgeStack.get(index);
    if (queryKey.sourceOrTarget) {
      return edge.getSourceId();
    }
    return edge.getTargetId();
  }

  public void setScope(Long limit) {
    if (null != limit) {
      this.scope = limit * 2;
    }
  }

  private boolean checkIntersectionAndDuplicateVertex(
      EdgeIterateInfo edgeIterateInfo, IEdge<IVertexId, IProperty> edge) {
    List<IntersectVertexInfo> intersectInfoList = edgeIterateInfo.getIntersectInfoList();
    if (!intersectInfoList.isEmpty()) {
      // check intersection
      for (IntersectVertexInfo intersectVertexInfo : intersectInfoList) {
        IEdge<IVertexId, IProperty> checkEdge =
            resultEdgeStack.get(intersectVertexInfo.getCheckVertexInfo().getEdgeIndex());
        IVertexId checkId;
        if (intersectVertexInfo.getCheckVertexInfo().isSourceOrTarget()) {
          checkId = checkEdge.getSourceId();
        } else {
          checkId = checkEdge.getTargetId();
        }
        IVertexId nowId;
        if (intersectVertexInfo.isIndexSourceOrTarget()) {
          nowId = edge.getSourceId();
        } else {
          nowId = edge.getTargetId();
        }
        if (!nowId.equals(checkId)) {
          return true;
        }
      }
    }

    boolean edgeOptional = edge instanceof OptionalEdge;
    Map<Integer, Tuple2<Boolean, Boolean>> duplicateVertexCheckMap =
        edgeIterateInfo.getDuplicateVertexCheck();
    if (MapUtils.isNotEmpty(duplicateVertexCheckMap)) {
      for (Map.Entry<Integer, Tuple2<Boolean, Boolean>> entry :
          duplicateVertexCheckMap.entrySet()) {
        int edgeIndex = entry.getKey();
        boolean source1 = entry.getValue()._1();
        boolean source2 = entry.getValue()._2();
        IEdge<IVertexId, IProperty> checkEdge = resultEdgeStack.get(edgeIndex);
        boolean checkEdgeOptional = checkEdge instanceof OptionalEdge;
        IVertexId id1 = source1 ? edge.getSourceId() : edge.getTargetId();
        IVertexId id2 = source2 ? checkEdge.getSourceId() : checkEdge.getTargetId();
        if (checkEdgeOptional && edgeOptional) {
          if (id1.equals(id2)) {
            IVertexId id3 = source1 ? edge.getTargetId() : edge.getSourceId();
            IVertexId id4 = source2 ? checkEdge.getTargetId() : checkEdge.getSourceId();
            if (id3.equals(id4)) {
              continue;
            } else {
              // invalid
              return true;
            }
          }
        } else if ((checkEdgeOptional || edgeOptional)) {
          continue;
        }
        if (id1.equals(id2)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean hasNext() {
    if (null != this.scope) {
      if (nextCount > this.scope) {
        return false;
      }
    }
    while (!iteratorStack.isEmpty()) {
      Iterator<IEdge<IVertexId, IProperty>> peekIt = iteratorStack.peek();
      if (peekIt.hasNext()) {
        IEdge<IVertexId, IProperty> edge = peekIt.next();
        EdgeIterateInfo edgeIterateInfo = this.edgeIterateInfoList.get(resultEdgeStack.size());
        boolean invalid = checkIntersectionAndDuplicateVertex(edgeIterateInfo, edge);
        if (invalid) {
          continue;
        }
        resultEdgeStack.push(edge);
        if (resultEdgeStack.size() == this.edgeIterateInfoList.size()) {
          return true;
        }
        pushNextIterator();
        continue;
      }
      if (1 == iteratorStack.size()) {
        return false;
      }
      iteratorStack.pop();
      resultEdgeStack.pop();
    }
    return false;
  }

  private long nextCount = 0;
  private long logCount = 10 * 10000;

  /** setter */
  public void setLogCount(long logCount) {
    this.logCount = logCount;
  }

  @Override
  public KgGraph<IVertexId> next() {
    if (resultEdgeStack.size() < this.edgeIterateInfoList.size()) {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
    }
    try {
      Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = new HashMap<>();
      Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap = new HashMap<>();
      for (int i = 0; i < this.edgeIterateInfoList.size(); ++i) {
        EdgeIterateInfo edgeIterateInfo = this.edgeIterateInfoList.get(i);
        String edgeAlias = edgeIterateInfo.getEdgeAlias();
        IEdge<IVertexId, IProperty> edge = resultEdgeStack.get(i);
        alias2EdgeMap.put(edgeAlias, Sets.newHashSet(edge));

        String sourceAlias = edgeIterateInfo.getSourceAlias();
        if (!alias2VertexMap.containsKey(sourceAlias)) {
          IVertex<IVertexId, IProperty> v =
              this.alias2VertexMap.get(sourceAlias).get(edge.getSourceId());
          alias2VertexMap.put(sourceAlias, Sets.newHashSet(v));
        }

        String targetAlias = edgeIterateInfo.getTargetAlias();
        if (!alias2VertexMap.containsKey(targetAlias)) {
          IVertex<IVertexId, IProperty> v =
              this.alias2VertexMap.get(targetAlias).get(edge.getTargetId());
          alias2VertexMap.put(targetAlias, Sets.newHashSet(v));
        }
      }
      return new KgGraphImpl(alias2VertexMap, alias2EdgeMap);
    } finally {
      resultEdgeStack.pop();
      if (nextCount++ > logCount) {
        logCount = logCount * 10;
        StringBuilder sb = new StringBuilder("EdgeCombinationIterator,count=").append(nextCount);
        for (String key : this.alias2EdgeMap.keySet()) {
          sb.append(",").append(key).append("=").append(this.alias2EdgeMap.get(key).size());
        }
        sb.append("\nEdgeIterateInfoList");
        for (int i = 0; i < this.edgeIterateInfoList.size(); ++i) {
          EdgeIterateInfo edgeIterateInfo1 = this.edgeIterateInfoList.get(i);
          sb.append(",")
              .append(i)
              .append(",e=")
              .append(edgeIterateInfo1.getEdgeAlias())
              .append(",s=")
              .append(edgeIterateInfo1.getSourceAlias())
              .append(",o=")
              .append(edgeIterateInfo1.getTargetAlias());
        }
        sb.append("\nVertexId");
        for (String key : this.alias2VertexMap.keySet()) {
          Map<IVertexId, IVertex<IVertexId, IProperty>> vertexMap = this.alias2VertexMap.get(key);
          if (1 == vertexMap.size()) {
            sb.append(",").append(key).append("=").append(vertexMap.keySet().iterator().next());
          }
        }
        log.info(sb.toString());
      }
    }
  }

  private Map<String, Map<IVertexId, List<IEdge<IVertexId, IProperty>>>> initEdgeQueryMap(
      Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap,
      Map<String, Boolean> edgeIndexInfo) {
    Map<String, Map<IVertexId, List<IEdge<IVertexId, IProperty>>>> result = new HashMap<>();
    for (Map.Entry<String, Set<IEdge<IVertexId, IProperty>>> entry : alias2EdgeMap.entrySet()) {
      String edgeAlias = entry.getKey();
      if (!edgeIndexInfo.containsKey(edgeAlias)) {
        continue;
      }
      boolean indexSource = edgeIndexInfo.get(edgeAlias);
      Map<IVertexId, List<IEdge<IVertexId, IProperty>>> edgeMap =
          result.computeIfAbsent(edgeAlias, k -> new HashMap<>());
      for (IEdge<IVertexId, IProperty> edge : entry.getValue()) {
        IVertexId indexKey;
        if (indexSource) {
          indexKey = edge.getSourceId();
        } else {
          indexKey = edge.getTargetId();
        }
        List<IEdge<IVertexId, IProperty>> edgeList =
            edgeMap.computeIfAbsent(indexKey, k -> new ArrayList<>());
        edgeList.add(edge);
      }
    }
    return result;
  }

  private Map<String, Map<IVertexId, IVertex<IVertexId, IProperty>>> initVertexQueryMap(
      Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap) {
    Map<String, Map<IVertexId, IVertex<IVertexId, IProperty>>> result = new HashMap<>();
    for (Map.Entry<String, Set<IVertex<IVertexId, IProperty>>> entry : alias2VertexMap.entrySet()) {
      String vertexAlias = entry.getKey();
      Map<IVertexId, IVertex<IVertexId, IProperty>> vertexMap =
          result.computeIfAbsent(vertexAlias, k -> new HashMap<>());
      for (IVertex<IVertexId, IProperty> vertex : entry.getValue()) {
        vertexMap.put(vertex.getId(), vertex);
      }
    }
    return result;
  }

  private Map<String, Boolean> getEdgeIndexInfo() {
    Map<String, Boolean> edgeIndexInfo = new HashMap<>();
    for (EdgeIterateInfo edgeIterateInfo : this.edgeIterateInfoList) {
      BuildEdgeIteratorInfo buildEdgeIteratorInfo = edgeIterateInfo.getBuildEdgeIteratorInfo();
      if (null == buildEdgeIteratorInfo) {
        continue;
      }
      edgeIndexInfo.put(
          buildEdgeIteratorInfo.getQueryEdgeAlias(), buildEdgeIteratorInfo.isIndexSourceOrTarget());
    }
    return edgeIndexInfo;
  }

  @Data
  public static class EdgeIterateInfo implements Serializable {
    private static final long serialVersionUID = -7914817333615035385L;
    private final String edgeAlias;
    private final String sourceAlias;
    private final String targetAlias;

    // howto init next edge iterator
    private BuildEdgeIteratorInfo buildEdgeIteratorInfo = null;

    // intersection with previous vertex check
    private List<IntersectVertexInfo> intersectInfoList = new ArrayList<>();

    // check duplicate vertex, Map<TargetEdgeIndex, Tuple2<nowEdgeSource, TargetEdgeSource>>
    private Map<Integer, Tuple2<Boolean, Boolean>> duplicateVertexCheck = new HashMap<>();

    public EdgeIterateInfo(Connection pc) {
      this.edgeAlias = pc.alias();
      this.sourceAlias = pc.source();
      this.targetAlias = pc.target();
    }
  }

  public static class IntersectVertexInfo implements Serializable {
    // now edge source or target
    private static final long serialVersionUID = -9177116827124228267L;
    private final boolean indexSourceOrTarget;

    // check vertex info
    private final Edge2VertexInfo checkVertexInfo;

    public IntersectVertexInfo(boolean indexSourceOrTarget, Edge2VertexInfo checkVertexInfo) {
      this.indexSourceOrTarget = indexSourceOrTarget;
      this.checkVertexInfo = checkVertexInfo;
    }

    /**
     * Getter method for property <tt>indexSourceOrTarget</tt>.
     *
     * @return property value of indexSourceOrTarget
     */
    public boolean isIndexSourceOrTarget() {
      return indexSourceOrTarget;
    }

    /**
     * Getter method for property <tt>checkVertexInfo</tt>.
     *
     * @return property value of checkVertexInfo
     */
    public Edge2VertexInfo getCheckVertexInfo() {
      return checkVertexInfo;
    }
  }

  public static class BuildEdgeIteratorInfo implements Serializable {
    // query edge alias
    private static final long serialVersionUID = -301765123145077701L;
    private final String queryEdgeAlias;
    private final boolean indexSourceOrTarget;

    // how to get query vertex id from previous kg graph
    private final Edge2VertexInfo queryKey;

    public BuildEdgeIteratorInfo(
        String queryEdgeAlias, boolean indexSourceOrTarget, Edge2VertexInfo edge2VertexInfo) {
      this.queryEdgeAlias = queryEdgeAlias;
      this.indexSourceOrTarget = indexSourceOrTarget;
      this.queryKey = edge2VertexInfo;
    }

    /**
     * Getter method for property <tt>queryEdgeAlias</tt>.
     *
     * @return property value of queryEdgeAlias
     */
    public String getQueryEdgeAlias() {
      return queryEdgeAlias;
    }

    /**
     * Getter method for property <tt>queryKey</tt>.
     *
     * @return property value of queryKey
     */
    public Edge2VertexInfo getQueryKey() {
      return queryKey;
    }

    /**
     * Getter method for property <tt>indexSourceOrTarget</tt>.
     *
     * @return property value of indexSourceOrTarget
     */
    public boolean isIndexSourceOrTarget() {
      return indexSourceOrTarget;
    }
  }

  public static class Edge2VertexInfo implements Serializable {
    private static final long serialVersionUID = -8606574641924896889L;
    private final int edgeIndex;
    private final String edgeAlias;
    private final boolean sourceOrTarget;

    public Edge2VertexInfo(int edgeIndex, String edgeAlias, boolean sourceOrTarget) {
      this.edgeIndex = edgeIndex;
      this.edgeAlias = edgeAlias;
      this.sourceOrTarget = sourceOrTarget;
    }

    /**
     * Getter method for property <tt>edgeAlias</tt>.
     *
     * @return property value of edgeAlias
     */
    public String getEdgeAlias() {
      return edgeAlias;
    }

    /**
     * Getter method for property <tt>sourceOrTarget</tt>.
     *
     * @return property value of sourceOrTarget
     */
    public boolean isSourceOrTarget() {
      return sourceOrTarget;
    }

    /**
     * Getter method for property <tt>edgeIndex</tt>.
     *
     * @return property value of edgeIndex
     */
    public int getEdgeIndex() {
      return edgeIndex;
    }
  }
}
