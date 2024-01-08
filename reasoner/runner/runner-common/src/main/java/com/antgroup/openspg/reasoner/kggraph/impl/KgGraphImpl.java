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

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.reasoner.common.exception.InvalidGraphException;
import com.antgroup.openspg.reasoner.common.exception.KGValueException;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.IVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.MirrorVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.NoneVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.common.primitives.Bytes;
import com.antgroup.openspg.reasoner.common.utils.PropertyUtil;
import com.antgroup.openspg.reasoner.kggraph.AggregationSchemaInfo;
import com.antgroup.openspg.reasoner.kggraph.IVertexId2WorkerIndex;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.lube.block.SortItem;
import com.antgroup.openspg.reasoner.lube.common.pattern.Connection;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.antgroup.openspg.reasoner.util.KgGraphSchema;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;
import scala.collection.JavaConversions;

public class KgGraphImpl implements KgGraph<IVertexId>, Serializable {
  private static final Logger log = LoggerFactory.getLogger(KgGraphImpl.class);
  /** Vertex aliases to vertex mappings */
  private static final long serialVersionUID = 4129310228727972996L;

  private Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap;

  /** Edge aliases to edge mappings */
  private Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap;

  /** default */
  public KgGraphImpl() {
    this.alias2VertexMap = new HashMap<>();
    this.alias2EdgeMap = new HashMap<>();
  }

  /** from map */
  public KgGraphImpl(
      Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap,
      Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap) {
    this.alias2VertexMap = alias2VertexMap;
    this.alias2EdgeMap = alias2EdgeMap;
  }

  /** copy */
  public KgGraphImpl(KgGraphImpl kgGraph) {
    this.alias2VertexMap = new HashMap<>(kgGraph.getAlias2VertexMap());
    this.alias2EdgeMap = new HashMap<>(kgGraph.getAlias2EdgeMap());
  }

  /**
   * Initialize the tree structure of KgGraph based on the root vertex and adjacent edges
   *
   * @param root the root of vertex subgraph
   * @param adjEdges the adjacent edges of root in vertex subgraph
   * @param schema describe the structure of vertex subgraph
   */
  @Override
  public void init(
      IVertex<IVertexId, IProperty> root,
      Map<String, List<IEdge<IVertexId, IProperty>>> adjEdges,
      Pattern schema) {
    if (null == root) {
      throw new KGValueException("Root and adjEdges cant be null", null);
    }

    // Data that does not conform to the schema
    Set<Connection> edgeSchemaSet = RunnerUtil.getConnectionSet(schema);
    for (Connection connection : edgeSchemaSet) {
      List<IEdge<IVertexId, IProperty>> edgeList = adjEdges.get(connection.alias());
      if (CollectionUtils.isEmpty(edgeList)) {
        log.warn("[KgGraphImpl.init] Data that does not conform to the schema");
        return;
      }
    }

    String vertexAlias = schema.root().alias();
    Set<IVertex<IVertexId, IProperty>> vertexSet =
        alias2VertexMap.computeIfAbsent(vertexAlias, k -> new HashSet<>());
    vertexSet.add(root);

    if (null == adjEdges || adjEdges.isEmpty()) {
      // log.info("[KgGraphImpl.init] adjEdges is empty, just scan root.");
      return;
    }
    for (String edgeAlias : adjEdges.keySet()) {
      Set<IEdge<IVertexId, IProperty>> edgeSet =
          alias2EdgeMap.computeIfAbsent(edgeAlias, k -> new HashSet<>());
      edgeSet.addAll(adjEdges.get(edgeAlias));

      // add virtual vertex
      Set<Connection> connectionSet =
          JavaConversions.setAsJavaSet(schema.topology().get(schema.root().alias()).get());
      String targetVertexAlias =
          connectionSet.stream()
              .filter(connection -> connection.alias().equals(edgeAlias))
              .map(connection -> connection.target())
              .collect(Collectors.toList())
              .get(0);
      Set<IVertex<IVertexId, IProperty>> targetVertexSet =
          alias2VertexMap.computeIfAbsent(targetVertexAlias, k -> new HashSet<>());
      adjEdges.get(edgeAlias).stream()
          .map(e -> e.getTargetId())
          .map(targetId -> new Vertex(targetId))
          .forEach(v -> targetVertexSet.add(v));
    }
  }

  private Bytes getKgGraphKeyBySplitVertex(List<String> vertexAliases, KgGraph<IVertexId> kgGraph) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(vertexAliases.size() * 16);
    for (String vertexAlias : vertexAliases) {
      IVertexId vid = kgGraph.getVertex(vertexAlias).get(0).getId();
      byteBuffer.put(vid.getBytes());
    }
    return new Bytes(byteBuffer.array());
  }

  /**
   * Split out the KgGraphs centered on the actual vertex of the alias from the current KgGraph
   *
   * @param inputVertexAliases
   * @param schema
   * @param filter
   * @param limit
   * @return
   */
  @Override
  public List<KgGraph<IVertexId>> split(
      Set<String> inputVertexAliases,
      Pattern schema,
      KgGraphSplitStaticParameters staticParameters,
      Predicate<KgGraph<IVertexId>> filter,
      Long limit) {
    Set<String> vertexAliases = Sets.newHashSet(inputVertexAliases);

    // remove alias that vertex set size is 1
    vertexAliases.removeIf(vertexAlias -> 1 == this.alias2VertexMap.get(vertexAlias).size());
    if (vertexAliases.isEmpty()) {
      // not need split
      if (null != filter && !filter.test(this)) {
        return new ArrayList<>();
      }
      return Lists.newArrayList(this);
    }

    // can do simple copy split
    if (staticParameters.canDoSampleSplit(this.alias2VertexMap)) {
      List<KgGraph<IVertexId>> resultList =
          doSimpleSplit(vertexAliases, schema, staticParameters, limit);
      if (null == filter) {
        return resultList;
      }
      List<KgGraph<IVertexId>> tmpResultList = resultList;
      resultList = new ArrayList<>();
      for (KgGraph<IVertexId> kgGraph : tmpResultList) {
        if (!filter.test(kgGraph)) {
          continue;
        }
        resultList.add(kgGraph);
      }
      return resultList;
    }

    Map<Bytes, KgGraph<IVertexId>> resultMap = new HashMap<>();
    List<String> vertexAliasList = Lists.newArrayList(vertexAliases);
    EdgeCombinationIterator it =
        new EdgeCombinationIterator(
            staticParameters.getEdgeIterateInfoList(),
            staticParameters.getEdgeIterateOrderMap(),
            this.alias2VertexMap,
            this.alias2EdgeMap);
    it.setScope(limit);
    while (it.hasNext()) {
      KgGraph<IVertexId> path = it.next();
      if (null == path) {
        continue;
      }
      if (null != filter && !filter.test(path)) {
        continue;
      }
      Bytes key = getKgGraphKeyBySplitVertex(vertexAliasList, path);
      KgGraph<IVertexId> kgGraph = resultMap.computeIfAbsent(key, k -> new KgGraphImpl());
      kgGraph.merge(Lists.newArrayList(path), schema);
      if (null != limit && resultMap.size() >= limit) {
        // reach max path limit
        break;
      }
    }
    return Lists.newArrayList(resultMap.values());
  }

  /**
   * @param vertexAlias target vertex alias
   * @param vertexId2WorkerIndex function, convert vertex id to worker index
   * @param schema
   * @param filter
   * @param limit max result limit, null means no constraints are applied
   * @return
   */
  @Override
  public Map<Integer, KgGraph<IVertexId>> splitToWorkerIndex(
      String vertexAlias,
      IVertexId2WorkerIndex vertexId2WorkerIndex,
      Pattern schema,
      KgGraphSplitStaticParameters staticParameters,
      Predicate<KgGraph<IVertexId>> filter,
      Long limit) {
    Map<Integer, KgGraph<IVertexId>> result = new HashMap<>();
    Set<String> vertexAliases = Sets.newHashSet(vertexAlias);
    // remove alias that vertex set size is 1
    vertexAliases.removeIf(alias -> 1 == this.alias2VertexMap.get(alias).size());
    if (vertexAliases.isEmpty()) {
      // not need split
      int workerIndex =
          vertexId2WorkerIndex.workerIndex(this.getVertex(vertexAlias).get(0).getId());
      if (workerIndex < 0) {
        return result;
      }
      result.put(workerIndex, this);
      return result;
    }

    // can do simple copy split
    if (null == filter && staticParameters.canDoSampleSplit(this.alias2VertexMap)) {
      return doSimpleSplitToWorkerIndex(vertexAlias, vertexId2WorkerIndex, staticParameters);
    }

    List<KgGraph<IVertexId>> splitList =
        this.split(vertexAliases, schema, staticParameters, filter, limit);
    for (KgGraph<IVertexId> kgGraph : splitList) {
      int workerIndex =
          vertexId2WorkerIndex.workerIndex(kgGraph.getVertex(vertexAlias).get(0).getId());
      if (workerIndex < 0) {
        continue;
      }
      KgGraph<IVertexId> kgGraphOnWorker =
          result.computeIfAbsent(workerIndex, k -> new KgGraphImpl());
      kgGraphOnWorker.merge(Lists.newArrayList(kgGraph), schema);
    }
    return result;
  }

  private Map<IVertexId, Integer> getVertexId2IndexMap(
      IVertexId2WorkerIndex vertexId2WorkerIndex,
      Set<IVertex<IVertexId, IProperty>> needSplitVertexSet) {
    Map<IVertexId, Integer> result = new HashMap<>();
    for (IVertex<IVertexId, IProperty> vertex : needSplitVertexSet) {
      int index = vertexId2WorkerIndex.workerIndex(vertex.getId());
      result.put(vertex.getId(), index);
    }
    return result;
  }

  private Map<Integer, KgGraph<IVertexId>> doSimpleSplitToWorkerIndex(
      String vertexAlias,
      IVertexId2WorkerIndex vertexId2WorkerIndex,
      KgGraphSplitStaticParameters staticParameters) {
    // base graph, for copy
    Map<String, Set<IVertex<IVertexId, IProperty>>> baseAlias2VertexMap =
        new HashMap<>(this.alias2VertexMap);
    Map<String, Set<IEdge<IVertexId, IProperty>>> baseAlias2EdgeMap =
        new HashMap<>(this.alias2EdgeMap);

    Set<IVertex<IVertexId, IProperty>> needSplitVertexSet = baseAlias2VertexMap.remove(vertexAlias);
    Map<Tuple2<String, Boolean>, Set<IEdge<IVertexId, IProperty>>> needSplitEdgeMap =
        new HashMap<>();
    Set<Connection> needSplitConnectionSet = staticParameters.getNeedSplitEdgeSet();
    for (Connection pc : needSplitConnectionSet) {
      Tuple2<String, Boolean> edgeTypeAndIndexSource =
          new Tuple2<>(pc.alias(), pc.source().equals(vertexAlias));
      needSplitEdgeMap.put(edgeTypeAndIndexSource, baseAlias2EdgeMap.remove(pc.alias()));
    }

    Map<IVertexId, Integer> id2IndexMap =
        getVertexId2IndexMap(vertexId2WorkerIndex, needSplitVertexSet);
    Set<Integer> workerIndexSet = new HashSet<>();

    Map<Integer, List<IVertex<IVertexId, IProperty>>> splitVertexMap = new HashMap<>();
    for (IVertex<IVertexId, IProperty> vertex : needSplitVertexSet) {
      Integer index = id2IndexMap.get(vertex.getId());
      if (null == index || index < 0) {
        continue;
      }
      workerIndexSet.add(index);
      List<IVertex<IVertexId, IProperty>> vertexList =
          splitVertexMap.computeIfAbsent(index, k -> new ArrayList<>());
      vertexList.add(vertex);
    }
    Map<Tuple2<String, Integer>, List<IEdge<IVertexId, IProperty>>> splitEdgeMap = new HashMap<>();
    for (Map.Entry<Tuple2<String, Boolean>, Set<IEdge<IVertexId, IProperty>>> entry :
        needSplitEdgeMap.entrySet()) {
      String edgeAlias = entry.getKey()._1();
      boolean indexSource = entry.getKey()._2();
      for (IEdge<IVertexId, IProperty> edge : entry.getValue()) {
        IVertexId vertexId;
        if (indexSource) {
          vertexId = edge.getSourceId();
        } else {
          vertexId = edge.getTargetId();
        }
        Integer index = id2IndexMap.get(vertexId);
        if (null == index || index < 0) {
          continue;
        }
        workerIndexSet.add(index);
        List<IEdge<IVertexId, IProperty>> edgeList =
            splitEdgeMap.computeIfAbsent(new Tuple2<>(edgeAlias, index), k -> new ArrayList<>());
        edgeList.add(edge);
      }
    }

    Map<Integer, KgGraph<IVertexId>> result = new HashMap<>();

    for (int index : workerIndexSet) {
      List<IVertex<IVertexId, IProperty>> vertexList = splitVertexMap.get(index);
      if (CollectionUtils.isEmpty(vertexList)) {
        continue;
      }
      boolean hasEmptyEdgeList = false;
      Map<String, Set<IEdge<IVertexId, IProperty>>> copyAlias2EdgeMap = new HashMap<>();
      for (Connection pc : needSplitConnectionSet) {
        List<IEdge<IVertexId, IProperty>> edgeList =
            splitEdgeMap.get(new Tuple2<>(pc.alias(), index));
        if (CollectionUtils.isEmpty(edgeList)) {
          hasEmptyEdgeList = true;
          break;
        }
        copyAlias2EdgeMap.put(pc.alias(), Sets.newHashSet(edgeList));
      }
      if (hasEmptyEdgeList) {
        continue;
      }

      KgGraphImpl kgGraph = new KgGraphImpl();
      kgGraph.alias2VertexMap.putAll(baseAlias2VertexMap);
      kgGraph.alias2EdgeMap.putAll(baseAlias2EdgeMap);
      kgGraph.alias2VertexMap.put(vertexAlias, Sets.newHashSet(vertexList));
      kgGraph.alias2EdgeMap.putAll(copyAlias2EdgeMap);
      result.put(index, kgGraph);
    }
    return result;
  }

  public List<KgGraph<IVertexId>> doSimpleSplit(
      Set<String> vertexAliases,
      Pattern schema,
      KgGraphSplitStaticParameters staticParameters,
      Long limit) {
    // base graph, for copy
    Map<String, Set<IVertex<IVertexId, IProperty>>> baseAlias2VertexMap =
        new HashMap<>(this.alias2VertexMap);
    Map<String, Set<IEdge<IVertexId, IProperty>>> baseAlias2EdgeMap =
        new HashMap<>(this.alias2EdgeMap);
    for (String alias : vertexAliases) {
      baseAlias2VertexMap.remove(alias);
    }
    Set<Connection> needSplitEdgeSet = staticParameters.getNeedSplitEdgeSet();
    for (Connection pc : needSplitEdgeSet) {
      baseAlias2EdgeMap.remove(pc.alias());
    }

    List<Set<Connection>> edgeCombinationList = staticParameters.getSplitConnectedSubgraph();
    if (edgeCombinationList.size() > 1) {
      throw new InvalidGraphException("not support unconnected graph", null);
    }
    Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = new HashMap<>();
    for (String vertexAlias : staticParameters.getConnectedSubgraphVertexAliasSet()) {
      alias2VertexMap.put(vertexAlias, this.alias2VertexMap.get(vertexAlias));
    }
    Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap = new HashMap<>();
    for (String edgeAlias : staticParameters.getConnectedSubgraphEdgeAliasSet()) {
      alias2EdgeMap.put(edgeAlias, this.alias2EdgeMap.get(edgeAlias));
    }
    // use sub edge iterate info list
    EdgeCombinationIterator it =
        new EdgeCombinationIterator(
            staticParameters.getSubEdgeIterateInfoList(),
            staticParameters.getSubEdgeIterateOrderMap(),
            alias2VertexMap,
            alias2EdgeMap);
    it.setScope(limit);
    List<String> sortedVertexAliasList = Lists.newArrayList(vertexAliases);
    Map<KgGraphKey, KgGraph<IVertexId>> result = new HashMap<>();
    long count = 0;
    while (it.hasNext()) {
      KgGraph<IVertexId> kgg = it.next();
      if (null == kgg) {
        continue;
      }
      count++;

      KgGraphImpl kgGraph = (KgGraphImpl) kgg;
      kgGraph.getAlias2VertexMap().putAll(baseAlias2VertexMap);
      kgGraph.getAlias2EdgeMap().putAll(baseAlias2EdgeMap);

      if (kgGraph.checkDuplicateVertex()) {
        continue;
      }

      KgGraphKey kgGraphKey = getKgGraphKey(kgGraph, sortedVertexAliasList);
      KgGraph<IVertexId> oldKgGraph = result.get(kgGraphKey);
      if (null == oldKgGraph) {
        result.put(kgGraphKey, kgGraph);
      } else {
        oldKgGraph.merge(Lists.newArrayList(kgGraph), schema);
      }
      if (null != limit && count >= limit) {
        break;
      }
    }
    return Lists.newArrayList(result.values());
  }

  private KgGraphKey getKgGraphKey(KgGraph<IVertexId> kgGraph, List<String> vertexAliases) {
    IVertexId[] vertexIds = new IVertexId[vertexAliases.size()];
    for (int i = 0; i < vertexAliases.size(); ++i) {
      String alias = vertexAliases.get(i);
      IVertexId vertexId = kgGraph.getVertex(alias).get(0).getId();
      vertexIds[i] = vertexId;
    }
    return new KgGraphKey(vertexIds);
  }

  /**
   * Merge the received messages into the current KgGraph
   *
   * @param msgs
   * @param schema the structure of the merged subgraph
   */
  @Override
  public void merge(Collection<KgGraph<IVertexId>> msgs, Pattern schema) {
    if (CollectionUtils.isEmpty(msgs)) {
      return;
    }

    for (KgGraph<IVertexId> msg : msgs) {
      // merge vertex
      KgGraphImpl graph = (KgGraphImpl) msg;
      for (String vertexAlias : graph.getAlias2VertexMap().keySet()) {
        Set<IVertex<IVertexId, IProperty>> otherVertexSet =
            graph.getAlias2VertexMap().get(vertexAlias);
        if (!this.alias2VertexMap.containsKey(vertexAlias)) {
          this.alias2VertexMap.put(vertexAlias, otherVertexSet);
        } else {
          Set<IVertex<IVertexId, IProperty>> thisVertexSet = this.alias2VertexMap.get(vertexAlias);
          if (thisVertexSet != otherVertexSet) {
            Set<IVertex<IVertexId, IProperty>> newVertexSet = new HashSet<>(thisVertexSet);
            newVertexSet.addAll(otherVertexSet);
            this.alias2VertexMap.put(vertexAlias, newVertexSet);
            // this.alias2VertexMap.get(vertexAlias).addAll(otherVertexSet);
          }
        }
      }

      // merge edge
      for (String edgeAlias : graph.getAlias2EdgeMap().keySet()) {
        Set<IEdge<IVertexId, IProperty>> otherEdgeSet = graph.getAlias2EdgeMap().get(edgeAlias);
        if (!this.alias2EdgeMap.containsKey(edgeAlias)) {
          this.alias2EdgeMap.put(edgeAlias, otherEdgeSet);
        } else {
          Set<IEdge<IVertexId, IProperty>> thisEdgeSet = this.alias2EdgeMap.get(edgeAlias);
          if (thisEdgeSet != otherEdgeSet) {
            Set<IEdge<IVertexId, IProperty>> newEdgeSet = new HashSet<>(thisEdgeSet);
            newEdgeSet.addAll(otherEdgeSet);
            this.alias2EdgeMap.put(edgeAlias, newEdgeSet);
            // this.alias2EdgeMap.get(edgeAlias).addAll(otherEdgeSet);
          }
        }
      }
    }
  }

  /**
   * VirtualVertex is a point in graph matching that only has an ID without any attributes. As the
   * matching process progresses, attributes will be added to it, and here we merge the attributes
   * into it.
   */
  private void mergeAndOverwriteVirtualVertex(List<KgGraph<IVertexId>> msgs) {
    Map<String, Map<IVertexId, IVertex<IVertexId, IProperty>>> aliasVirtualVertexMap =
        new HashMap<>();

    for (KgGraph<IVertexId> msg : msgs) {
      // merge vertex
      KgGraphImpl graph = (KgGraphImpl) msg;
      for (String vertexAlias : graph.getAlias2VertexMap().keySet()) {

        Set<IVertex<IVertexId, IProperty>> thisVertexSet =
            this.alias2VertexMap.computeIfAbsent(vertexAlias, k -> new HashSet<>());
        Set<IVertex<IVertexId, IProperty>> otherVertexSet =
            graph.getAlias2VertexMap().getOrDefault(vertexAlias, new HashSet<>());

        Map<IVertexId, IVertex<IVertexId, IProperty>> virtualVertexMap =
            aliasVirtualVertexMap.get(vertexAlias);
        if (null == virtualVertexMap) {
          // init virtual vertex map
          virtualVertexMap = new HashMap<>();
          for (IVertex<IVertexId, IProperty> v : thisVertexSet) {
            if (v.getValue() != null) {
              continue;
            }
            virtualVertexMap.put(v.getId(), v);
          }
          aliasVirtualVertexMap.put(vertexAlias, virtualVertexMap);
        }

        // If otherVertexSet has real vertex, delete the virtual vertex in thisVertexSet
        if (!virtualVertexMap.isEmpty()) {
          for (IVertex<IVertexId, IProperty> otherVertex : otherVertexSet) {
            if (virtualVertexMap.containsKey(otherVertex.getId())
                && null != otherVertex.getValue()) {
              thisVertexSet.remove(virtualVertexMap.get(otherVertex.getId()));
            }
          }
        }
        thisVertexSet.addAll(otherVertexSet);
      }

      // merge edge
      for (String edgeAlias : graph.getAlias2EdgeMap().keySet()) {
        Set<IEdge<IVertexId, IProperty>> thisEdgeSet =
            this.getAlias2EdgeMap().computeIfAbsent(edgeAlias, k -> new HashSet<>());
        Set<IEdge<IVertexId, IProperty>> otherEdgeSet = graph.getAlias2EdgeMap().get(edgeAlias);
        if (CollectionUtils.isNotEmpty(otherEdgeSet)) {
          thisEdgeSet.addAll(otherEdgeSet);
        }
      }
    }
  }

  /**
   * Add the new KgGraph to the current KgGraph, and the schema of the KgGraph changes
   *
   * @param subGraph
   * @param schema the structure of the expanded subgraph
   */
  @Override
  public void expand(KgGraph<IVertexId> subGraph, Pattern schema) {
    String rootAlias = schema.root().alias();
    Set<IVertex<IVertexId, IProperty>> rootVertexSet = this.alias2VertexMap.remove(rootAlias);
    IVertex<IVertexId, IProperty> rootVertex = rootVertexSet.iterator().next();
    if (rootVertex instanceof MirrorVertex) {
      KgGraphImpl subKgGraph = (KgGraphImpl) subGraph;
      IVertex<IVertexId, IProperty> matchedRootVertex =
          subKgGraph.getAlias2VertexMap().get(rootAlias).iterator().next();
      subKgGraph
          .getAlias2VertexMap()
          .put(rootAlias, Sets.newHashSet(new MirrorVertex<>(matchedRootVertex)));
    }
    this.merge(Lists.newArrayList(subGraph), schema);
  }

  @Override
  public int expandAndPrune(
      Pattern thisSchema,
      KgGraph<IVertexId> matchedKgGraph,
      Pattern matchedSchema,
      Pattern finalSchema,
      Set<String> intersectionVertexAliasSet) {
    KgGraphImpl tmpMatchedKgGraph = new KgGraphImpl((KgGraphImpl) matchedKgGraph);
    int minMatchCount = Integer.MAX_VALUE;
    for (String vertexAlias : intersectionVertexAliasSet) {
      Set<IVertex<IVertexId, IProperty>> matchedVertexSet =
          tmpMatchedKgGraph.alias2VertexMap.remove(vertexAlias);
      Set<IVertex<IVertexId, IProperty>> thisVertexSet = this.alias2VertexMap.get(vertexAlias);
      if (thisVertexSet instanceof HashSet) {
        thisVertexSet =
            (Set<IVertex<IVertexId, IProperty>>)
                ((HashSet<IVertex<IVertexId, IProperty>>) thisVertexSet).clone();
      } else {
        thisVertexSet = new HashSet<>(thisVertexSet);
      }
      boolean change = thisVertexSet.retainAll(matchedVertexSet);
      if (thisVertexSet.isEmpty()) {
        // no intersection, return empty KgGraph
        return 0;
      }

      if (thisVertexSet.size() < minMatchCount) {
        minMatchCount = thisVertexSet.size();
      }

      if (change) {
        this.alias2VertexMap.put(vertexAlias, thisVertexSet);
        Map<String, Boolean> pruneEdgeAliasAndDirection =
            getPruneEdgeAliasAndDirection(thisSchema, vertexAlias);
        this.pruningEdgesByVertexSet(thisVertexSet, pruneEdgeAliasAndDirection);
      }

      if (matchedVertexSet.size() > thisVertexSet.size()) {
        tmpMatchedKgGraph.alias2VertexMap.put(vertexAlias, thisVertexSet);
        Map<String, Boolean> pruneEdgeAliasAndDirection =
            getPruneEdgeAliasAndDirection(matchedSchema, vertexAlias);
        tmpMatchedKgGraph.pruningEdgesByVertexSet(thisVertexSet, pruneEdgeAliasAndDirection);
      }
    }
    this.alias2VertexMap.remove(matchedSchema.root().alias());
    this.merge(Lists.newArrayList(tmpMatchedKgGraph), finalSchema);
    return minMatchCount;
  }

  private Map<String, Boolean> getPruneEdgeAliasAndDirection(Pattern schema, String vertexAlias) {
    Set<Connection> pcSet =
        JavaConversions.setAsJavaSet(KgGraphSchema.getNeighborEdges(schema, vertexAlias));
    Map<String, Boolean> pruneEdgeAliasAndDirection = new HashMap<>();
    for (Connection pc : pcSet) {
      if (pc.source().equals(vertexAlias)) {
        pruneEdgeAliasAndDirection.put(pc.alias(), true);
      } else if (pc.target().equals(vertexAlias)) {
        pruneEdgeAliasAndDirection.put(pc.alias(), false);
      }
    }
    return pruneEdgeAliasAndDirection;
  }

  private void pruningEdgesByVertexSet(
      Set<IVertex<IVertexId, IProperty>> validVertexSet,
      Map<String, Boolean> pruneEdgeAliasAndDirection) {
    IVertex<IVertexId, IProperty> useForCheckVertex = new Vertex<>(null, null);
    for (Map.Entry<String, Boolean> entry : pruneEdgeAliasAndDirection.entrySet()) {
      String edgeAlias = entry.getKey();
      boolean checkSource = entry.getValue();
      Set<IEdge<IVertexId, IProperty>> newEdgeSet = new HashSet<>();
      Set<IEdge<IVertexId, IProperty>> edgeSet = this.alias2EdgeMap.get(edgeAlias);
      for (IEdge<IVertexId, IProperty> edge : edgeSet) {
        if (checkSource) {
          useForCheckVertex.setId(edge.getSourceId());
        } else {
          useForCheckVertex.setId(edge.getTargetId());
        }
        if (!validVertexSet.contains(useForCheckVertex)) {
          continue;
        }
        newEdgeSet.add(edge);
      }
      this.alias2EdgeMap.put(edgeAlias, newEdgeSet);
    }
  }

  /**
   * Get the path from KgGraph, flat all alias vertexes
   *
   * @return
   */
  @Override
  public Iterator<KgGraph<IVertexId>> getPath(
      KgGraphSplitStaticParameters staticParameters, Predicate<KgGraph<IVertexId>> filter) {
    // single vertex path
    if (this.alias2EdgeMap.isEmpty() && 1 == this.alias2VertexMap.size()) {
      List<KgGraph<IVertexId>> result = new ArrayList<>();
      this.alias2VertexMap.forEach(
          new BiConsumer<String, Set<IVertex<IVertexId, IProperty>>>() {
            @Override
            public void accept(String vertexAlias, Set<IVertex<IVertexId, IProperty>> vertexSet) {
              for (IVertex<IVertexId, IProperty> vertex : vertexSet) {
                Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = new HashMap<>();
                alias2VertexMap.put(vertexAlias, Sets.newHashSet(vertex));
                KgGraph<IVertexId> kgGraph = new KgGraphImpl(alias2VertexMap, alias2EdgeMap);
                if (null != filter && !filter.test(kgGraph)) {
                  continue;
                }
                result.add(kgGraph);
              }
            }
          });
      return result.iterator();
    }
    // getPathSize();
    return new KgGraphIterator(
        staticParameters.getEdgeIterateInfoList(),
        staticParameters.getEdgeIterateOrderMap(),
        this.alias2VertexMap,
        this.alias2EdgeMap,
        filter);
  }

  private static final long LOG_PATH_SIZE_THRESHOLD = 10 * 10000 * 10000;

  /**
   * Different versions of edges may exist between two vertexes, so the Cartesian product of edges
   * is used as the total number of paths
   *
   * @return
   */
  private Long getPathSize() {
    long size = 1;
    if (alias2EdgeMap.isEmpty()) {
      // only have one vertex alias
      int vertexAliasNum = alias2VertexMap.keySet().size();
      if (vertexAliasNum != 1) {
        throw new RuntimeException(
            String.format(
                "Schema has only one vertex, but KgGraph has %d vertex alias", vertexAliasNum));
      }
      for (String vertexAlias : alias2VertexMap.keySet()) {
        size = alias2VertexMap.get(vertexAlias).size();
        return size;
      }
    }
    Map<String, Integer> edgeCountMap = new HashMap<>();
    for (Map.Entry<String, Set<IEdge<IVertexId, IProperty>>> entry :
        this.alias2EdgeMap.entrySet()) {
      String type = entry.getKey();
      int edgeSize = entry.getValue().size();
      edgeCountMap.put(type, edgeSize);
      size *= edgeSize;
    }
    if (size > LOG_PATH_SIZE_THRESHOLD) {
      List<IVertexId> idList = new ArrayList<>();
      for (Map.Entry<String, Set<IVertex<IVertexId, IProperty>>> entry :
          this.alias2VertexMap.entrySet()) {
        if (1 == entry.getValue().size()) {
          idList.add(entry.getValue().iterator().next().getId());
        }
        edgeCountMap.put(entry.getKey(), entry.getValue().size());
      }
      log.info(
          "KgGraphPathSize="
              + size
              + ",aliasCountMap="
              + JSON.toJSONString(edgeCountMap)
              + ",idList="
              + idList);
    }
    return size;
  }

  @Override
  public void aggregateEdge(
      String edgeAlias,
      Set<IEdge<IVertexId, IProperty>> edgeSet,
      AggregationSchemaInfo aggregationSchemaInfo,
      Set<String> aggregatedAliasSet) {
    if (aggregatedAliasSet.contains(edgeAlias)) {
      return;
    }
    aggregatedAliasSet.add(edgeAlias);

    Set<IEdge<IVertexId, IProperty>> oldEdgeSet = this.alias2EdgeMap.get(edgeAlias);

    // 裁掉边
    this.alias2EdgeMap.put(edgeAlias, edgeSet);

    if (edgeSet.size() == oldEdgeSet.size()) {
      return;
    }

    List<Tuple2<String, Boolean>> endpointVertexes =
        aggregationSchemaInfo.getEdgeEndpointMap().get(edgeAlias);
    for (Tuple2<String, Boolean> tuple2 : endpointVertexes) {
      String vertexAlias = tuple2._1();
      if (aggregatedAliasSet.contains(vertexAlias)) {
        continue;
      }
      boolean checkSource = tuple2._2();

      Set<IVertexId> validVertexIdSet = new HashSet<>();
      for (IEdge<IVertexId, IProperty> edge : edgeSet) {
        if (checkSource) {
          validVertexIdSet.add(edge.getSourceId());
        } else {
          validVertexIdSet.add(edge.getTargetId());
        }
      }
      Set<IVertex<IVertexId, IProperty>> newVertexSet = new HashSet<>();
      for (IVertex<IVertexId, IProperty> vertex : this.alias2VertexMap.get(vertexAlias)) {
        if (validVertexIdSet.contains(vertex.getId())) {
          newVertexSet.add(vertex);
        }
      }
      this.aggregateVertex(vertexAlias, newVertexSet, aggregationSchemaInfo, aggregatedAliasSet);
    }
  }

  @Override
  public void aggregateVertex(
      String vertexAlias,
      Set<IVertex<IVertexId, IProperty>> vertexSet,
      AggregationSchemaInfo aggregationSchemaInfo,
      Set<String> aggregatedAliasSet) {
    if (aggregatedAliasSet.contains(vertexAlias)) {
      return;
    }
    aggregatedAliasSet.add(vertexAlias);

    Set<IVertex<IVertexId, IProperty>> oldVertexSet = this.alias2VertexMap.get(vertexAlias);

    // 裁掉点
    this.alias2VertexMap.put(vertexAlias, vertexSet);

    if (vertexSet.size() == oldVertexSet.size()) {
      return;
    }

    IVertex<IVertexId, IProperty> tmpCheckVertex = new Vertex<>();
    List<Tuple2<String, Boolean>> edgeInfoList =
        aggregationSchemaInfo.getVertexHasEdgeMap().get(vertexAlias);
    for (Tuple2<String, Boolean> tuple2 : edgeInfoList) {
      String edgeAlias = tuple2._1();
      if (aggregatedAliasSet.contains(edgeAlias)) {
        continue;
      }
      boolean checkSource = tuple2._2();

      Set<IEdge<IVertexId, IProperty>> newEdgeSet = new HashSet<>();
      for (IEdge<IVertexId, IProperty> edge : this.alias2EdgeMap.get(edgeAlias)) {
        if (checkSource) {
          tmpCheckVertex.setId(edge.getSourceId());
        } else {
          tmpCheckVertex.setId(edge.getTargetId());
        }
        if (vertexSet.contains(tmpCheckVertex)) {
          newEdgeSet.add(edge);
        }
      }
      this.aggregateEdge(edgeAlias, newEdgeSet, aggregationSchemaInfo, aggregatedAliasSet);
    }
  }

  /**
   * Sort the current KgGraph by sortItems
   *
   * @param sortItems
   * @param limit
   * @param schema
   */
  @Override
  public void executeSort(List<SortItem> sortItems, int limit, Pattern schema) {}

  /**
   * Adjust the current KgGraph according to scheme including structure and property
   *
   * @param schema
   */
  @Override
  public void adjustGraph(Pattern schema) {}

  /**
   * Get vertex from KgGraph based on vertex alias
   *
   * @param alias
   * @return
   */
  @Override
  public List<IVertex<IVertexId, IProperty>> getVertex(String alias) {
    List<IVertex<IVertexId, IProperty>> result = new ArrayList<>();
    Set<IVertex<IVertexId, IProperty>> vertexSet = alias2VertexMap.get(alias);
    if (CollectionUtils.isNotEmpty(vertexSet)) {
      result.addAll(vertexSet);
    }
    return result;
  }

  /**
   * Get edge from KgGraph based on edge alias
   *
   * @param alias
   * @return
   */
  @Override
  public List<IEdge<IVertexId, IProperty>> getEdge(String alias) {
    List<IEdge<IVertexId, IProperty>> result = new ArrayList<>();
    Set<IEdge<IVertexId, IProperty>> edgeSet = alias2EdgeMap.get(alias);
    if (CollectionUtils.isNotEmpty(edgeSet)) {
      result.addAll(edgeSet);
    }
    return result;
  }

  @Override
  public Set<String> getVertexAlias() {
    return alias2VertexMap.keySet();
  }

  @Override
  public Set<String> getEdgeAlias() {
    return alias2EdgeMap.keySet();
  }

  @Override
  public void show() {
    log.info("--------------------Vertex---------------------------");
    StringBuffer sb = new StringBuffer();
    for (String vertexAlias : alias2VertexMap.keySet()) {
      sb.setLength(0);
      sb.append(vertexAlias).append(": ");
      alias2VertexMap.get(vertexAlias).stream()
          .forEach(vertex -> sb.append(vertex.getId()).append(", "));
      log.info(sb.toString());
    }
    log.info("--------------------Edge-----------------------------");
    for (String edgeAlias : alias2EdgeMap.keySet()) {
      sb.setLength(0);
      sb.append(edgeAlias).append(": ");
      alias2EdgeMap.get(edgeAlias).stream()
          .forEach(
              edge -> {
                sb.append(edge.getSourceId());
                if (edge.getDirection().equals(Direction.OUT)) {
                  sb.append(" -> ");
                }
                if (edge.getDirection().equals(Direction.IN)) {
                  sb.append(" <- ");
                }
                if (edge.getDirection().equals(Direction.BOTH)) {
                  sb.append(" <-> ");
                }
                sb.append(edge.getTargetId());
                sb.append(", ");
              });
      log.info(sb.toString());
    }
  }

  @Override
  public boolean hasFocusVertexId(String alias, Set<IVertexId> focusVertexIdSet) {
    Set<IVertex<IVertexId, IProperty>> vertexSet = alias2VertexMap.get(alias);
    if (null == vertexSet) {
      return false;
    }
    for (IVertex<IVertexId, IProperty> v : vertexSet) {
      if (focusVertexIdSet.contains(v.getId())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean checkDuplicateVertex() {
    Set<IVertexId> idSet = new HashSet<>();
    for (Set<IVertex<IVertexId, IProperty>> vertexSet : this.alias2VertexMap.values()) {
      if (1 == vertexSet.size()) {
        IVertex<IVertexId, IProperty> vertex = vertexSet.iterator().next();
        IVertexId id = vertex.getId();
        if (vertex instanceof NoneVertex) {
          continue;
        }
        if (idSet.contains(id)) {
          return true;
        } else {
          idSet.add(id);
        }
      }
    }
    return false;
  }

  @Override
  public IVertex<IVertexId, IProperty> findVertex(IVertexId id) {
    IVertex<IVertexId, IProperty> searchVertex = new Vertex<>(id);
    for (Map.Entry<String, Set<IVertex<IVertexId, IProperty>>> entry : alias2VertexMap.entrySet()) {
      if (entry.getValue().contains(searchVertex)) {
        for (IVertex<IVertexId, IProperty> v : entry.getValue()) {
          if (v.getId().equals(id)) {
            return v;
          }
        }
      }
    }
    return null;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, Set<IVertex<IVertexId, IProperty>>> entry : alias2VertexMap.entrySet()) {
      String alias = entry.getKey();
      Set<IVertex<IVertexId, IProperty>> vertexSet = entry.getValue();
      sb.append("\nvertex=").append(alias).append(",size=").append(vertexSet.size()).append(",{");
      int count = 0;
      for (IVertex<IVertexId, IProperty> v : vertexSet) {
        if (count++ > 100) {
          break;
        }
        sb.append(v);
      }
      sb.append("}");
    }

    for (Map.Entry<String, Set<IEdge<IVertexId, IProperty>>> entry : alias2EdgeMap.entrySet()) {
      String alias = entry.getKey();
      Set<IEdge<IVertexId, IProperty>> edgeSet = entry.getValue();
      sb.append("\nedge=").append(alias).append(",c=").append(edgeSet.size()).append("{");
      int count = 0;
      for (IEdge<IVertexId, IProperty> e : edgeSet) {
        if (count++ > 100) {
          break;
        }
        sb.append(e);
      }
      sb.append("}");
    }
    return sb.toString();
  }

  /**
   * Getter method for property <tt>alias2VertexMap</tt>.
   *
   * @return property value of alias2VertexMap
   */
  public Map<String, Set<IVertex<IVertexId, IProperty>>> getAlias2VertexMap() {
    return alias2VertexMap;
  }

  /**
   * Setter method for property <tt>alias2VertexMap</tt>.
   *
   * @param alias2VertexMap value to be assigned to property alias2VertexMap
   */
  public void setAlias2VertexMap(Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap) {
    this.alias2VertexMap = alias2VertexMap;
  }

  /**
   * Getter method for property <tt>alias2EdgeMap</tt>.
   *
   * @return property value of alias2EdgeMap
   */
  public Map<String, Set<IEdge<IVertexId, IProperty>>> getAlias2EdgeMap() {
    return alias2EdgeMap;
  }

  /**
   * Setter method for property <tt>alias2EdgeMap</tt>.
   *
   * @param alias2EdgeMap value to be assigned to property alias2EdgeMap
   */
  public void setAlias2EdgeMap(Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap) {
    this.alias2EdgeMap = alias2EdgeMap;
  }

  private static class KgGraphIterator extends EdgeCombinationIterator {
    private final Predicate<KgGraph<IVertexId>> filter;

    public KgGraphIterator(
        List<EdgeIterateInfo> edgeIterateInfoList,
        Map<String, Integer> edgeIterateOrderMap,
        Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap,
        Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap,
        Predicate<KgGraph<IVertexId>> filter) {
      super(edgeIterateInfoList, edgeIterateOrderMap, alias2VertexMap, alias2EdgeMap);
      this.filter = filter;
    }

    @Override
    public KgGraph<IVertexId> next() {
      KgGraph<IVertexId> kgGraph = super.next();
      if (null == kgGraph) {
        return null;
      }
      if (null != filter && !filter.test(kgGraph)) {
        return null;
      }
      return kgGraph;
    }
  }

  /**
   * set vertex property
   *
   * @param alias - vertex alias
   * @param propertyMap - key is new property name, value is new property value
   * @param version - vertex version, if null add properties to the 0 version
   */
  @Override
  public void setVertexProperty(String alias, Map<String, Object> propertyMap, Long version) {
    if (StringUtils.isBlank(alias)) {
      log.warn("[KgGraphImpl.setVertexProperty] alias is empty");
      return;
    }

    if (!alias2VertexMap.containsKey(alias)) {
      throw new RuntimeException(
          String.format("[KgGraphImpl.setVertexProperty] no vertex alias with alias = %s", alias));
    }

    Set<IVertex<IVertexId, IProperty>> vertexSet = alias2VertexMap.get(alias);
    Set<IVertex<IVertexId, IProperty>> updateVertexSet = new HashSet<>();
    vertexSet.forEach(
        vertex -> {
          if (null == vertex.getValue()) {
            // without expendInto, vertex property can be null
            IVersionProperty versionProperty =
                PropertyUtil.buildVertexProperty(vertex.getId(), new HashMap<>());
            vertex.setValue(versionProperty);
          }
          if (!(vertex.getValue() instanceof IVersionProperty)) {
            throw new RuntimeException(
                "[KgGraphImpl.setVertexProperty] vertex property is not multi-version");
          }
          if (null == propertyMap || propertyMap.isEmpty()) {
            log.debug(
                "[KgGraphImpl.setVertexProperty] propertyMap is empty, delete alias all properties");
            vertex.setValue(PropertyUtil.buildVertexProperty(vertex.getId(), null));
            updateVertexSet.add(vertex);
          } else {
            IVersionProperty versionProperty;
            if (vertex.getValue() == null) {
              versionProperty = PropertyUtil.buildVertexProperty(vertex.getId(), null);
            } else {
              versionProperty = (IVersionProperty) vertex.getValue().clone();
            }
            for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
              String key = entry.getKey();
              Object value = entry.getValue();
              if (null == value) {
                versionProperty.remove(key);
                continue;
              }
              versionProperty.put(key, propertyMap.get(key), version);
            }
            IVertex<IVertexId, IProperty> newVertex = vertex.clone();
            newVertex.setValue(versionProperty);
            updateVertexSet.add(newVertex);
          }
        });
    alias2VertexMap.put(alias, updateVertexSet);
  }

  /**
   * set edge property
   *
   * @param alias - edge alias
   * @param propertyMap - key is new property name, value is new property value
   */
  @Override
  public void setEdgeProperty(String alias, Map<String, Object> propertyMap) {
    if (StringUtils.isBlank(alias)) {
      log.warn("[KgGraphImpl.setEdgeProperty] alias is empty");
      return;
    }

    if (!alias2EdgeMap.containsKey(alias)) {
      throw new RuntimeException(
          String.format("[KgGraphImpl.setEdgeProperty] no edge alias with alias = %s", alias));
    }
    Set<IEdge<IVertexId, IProperty>> edgeSet = alias2EdgeMap.get(alias);
    Set<IEdge<IVertexId, IProperty>> updateEdgeSet = new HashSet<>();
    edgeSet.forEach(
        edge -> {
          if (null == propertyMap || propertyMap.isEmpty()) {
            log.debug(
                "[KgGraphImpl.setEdgeProperty] propertyMap is empty, delete alias all properties");
            edge.setValue(PropertyUtil.buildEdgeProperty(edge.getType(), null));
            updateEdgeSet.add(edge);
          } else {
            IProperty edgeValue;
            if (edge.getValue() == null) {
              edgeValue = PropertyUtil.buildEdgeProperty(edge.getType(), null);
            } else {
              edgeValue = edge.getValue().clone();
            }
            for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
              String key = entry.getKey();
              Object value = entry.getValue();
              if (null == value) {
                edgeValue.remove(key);
                continue;
              }
              edgeValue.put(key, propertyMap.get(key));
            }
            IEdge<IVertexId, IProperty> newEdge = edge.clone();
            newEdge.setValue(edgeValue);
            updateEdgeSet.add(newEdge);
          }
        });
    alias2EdgeMap.put(alias, updateEdgeSet);
  }

  @Override
  public void cloneVertexSet(String alias) {
    Set<IVertex<IVertexId, IProperty>> vertexSet = alias2VertexMap.get(alias);
    Set<IVertex<IVertexId, IProperty>> newVertexSet = new HashSet<>();
    for (IVertex<IVertexId, IProperty> vertex : vertexSet) {
      Vertex<IVertexId, IProperty> newVertex = new Vertex<>(vertex.getId());
      if (null == vertex.getValue()) {
        // without expendInto, vertex value can be null
        newVertex.setValue(null);
      } else {
        newVertex.setValue(vertex.getValue().clone());
      }
      newVertexSet.add(newVertex);
    }
    this.alias2VertexMap.put(alias, newVertexSet);
  }

  @Override
  public void cloneEdgeSet(String alias) {
    Set<IEdge<IVertexId, IProperty>> edgeSet = alias2EdgeMap.get(alias);
    Set<IEdge<IVertexId, IProperty>> newEdgeSet = new HashSet<>();
    for (IEdge<IVertexId, IProperty> edge : edgeSet) {
      Edge<IVertexId, IProperty> newEdge =
          new Edge<>(
              edge.getSourceId(),
              edge.getTargetId(),
              null,
              edge.getVersion(),
              edge.getDirection(),
              edge.getType());
      if (null == edge.getValue()) {
        // without expendInto, vertex value can be null
        newEdge.setValue(null);
      } else {
        newEdge.setValue(edge.getValue().clone());
      }
      newEdgeSet.add(newEdge);
    }
    alias2EdgeMap.put(alias, newEdgeSet);
  }
}
