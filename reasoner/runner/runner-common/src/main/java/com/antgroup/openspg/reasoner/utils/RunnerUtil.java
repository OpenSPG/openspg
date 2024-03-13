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

package com.antgroup.openspg.reasoner.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.exception.NotImplementedException;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.SPO;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.OptionalEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.PathEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.type.MapType2IdFactory;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.MirrorVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.NoneVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.common.utils.CombinationIterator;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphSplitStaticParameters;
import com.antgroup.openspg.reasoner.lube.block.AddPredicate;
import com.antgroup.openspg.reasoner.lube.block.AddVertex;
import com.antgroup.openspg.reasoner.lube.common.expr.AggOpExpr;
import com.antgroup.openspg.reasoner.lube.common.expr.Aggregator;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.common.expr.First$;
import com.antgroup.openspg.reasoner.lube.common.graph.IRField;
import com.antgroup.openspg.reasoner.lube.common.pattern.Connection;
import com.antgroup.openspg.reasoner.lube.common.pattern.EdgePattern;
import com.antgroup.openspg.reasoner.lube.common.pattern.LinkedPatternConnection;
import com.antgroup.openspg.reasoner.lube.common.pattern.PartialGraphPattern;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.antgroup.openspg.reasoner.lube.common.pattern.PatternElement;
import com.antgroup.openspg.reasoner.lube.common.rule.Rule;
import com.antgroup.openspg.reasoner.lube.logical.EdgeVar;
import com.antgroup.openspg.reasoner.lube.logical.NodeVar;
import com.antgroup.openspg.reasoner.lube.logical.PropertyVar;
import com.antgroup.openspg.reasoner.lube.logical.RepeatPathVar;
import com.antgroup.openspg.reasoner.lube.logical.RichVar;
import com.antgroup.openspg.reasoner.lube.logical.Var;
import com.antgroup.openspg.reasoner.lube.utils.RuleUtils;
import com.antgroup.openspg.reasoner.rdg.common.FoldRepeatEdgeInfo;
import com.antgroup.openspg.reasoner.rdg.common.UnfoldRepeatEdgeInfo;
import com.antgroup.openspg.reasoner.runner.ConfigKey;
import com.antgroup.openspg.reasoner.session.KGReasonerSession;
import com.antgroup.openspg.reasoner.udf.UdfMng;
import com.antgroup.openspg.reasoner.udf.UdfMngFactory;
import com.antgroup.openspg.reasoner.udf.model.LazyUdaf;
import com.antgroup.openspg.reasoner.udf.model.UdtfMeta;
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.antgroup.openspg.reasoner.util.KgGraphSchema;
import com.antgroup.openspg.reasoner.warehouse.common.config.EdgeLoaderConfig;
import com.antgroup.openspg.reasoner.warehouse.common.config.GraphLoaderConfig;
import com.antgroup.openspg.reasoner.warehouse.common.config.VertexLoaderConfig;
import com.antgroup.openspg.reasoner.warehouse.utils.WareHouseUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;
import scala.collection.JavaConversions;

public class RunnerUtil {

  /** kg id is global id property */
  public static final String KG_REASONER_PROPERTY_GLOBAL_ID = "kgId";

  /**
   * Check if cur is between start and end
   *
   * @param start
   * @param end
   * @param cur
   * @return
   */
  public static Boolean between(Long start, Long end, Long cur) {
    boolean isInInterval = true;
    if (start != null && start != 0) {
      isInInterval = cur >= start;
    }
    if (end != null && end != 0) {
      isInInterval = isInInterval && (cur <= end);
    }
    return isInInterval;
  }

  /** get vertex type */
  public static String getVertexType(IVertex<IVertexId, IProperty> vertex) {
    return vertex.getId().getType();
  }

  /** get edge source type */
  public static String getEdgeSourceType(IEdge<IVertexId, IProperty> edge) {
    return edge.getSourceId().getType();
  }

  /** get edge source biz id */
  public static <K> String getEdgeSourceBizId(IEdge<K, IProperty> edge) {
    throw new NotImplementedException("", null);
  }

  /** get edge target type */
  public static String getEdgeTargetType(IEdge<IVertexId, IProperty> edge) {
    return edge.getTargetId().getType();
  }

  /** get edge target biz id */
  public static <K> String getEdgeTargetBizId(IEdge<K, IProperty> edge) {
    throw new NotImplementedException("", null);
  }

  /** expend KgGraph to path, filter than merge */
  public static KgGraph<IVertexId> filterKgGraph(
      KgGraph<IVertexId> value,
      Pattern kgGraphSchema,
      KgGraphSplitStaticParameters staticParameters,
      List<String> ruleList,
      Long maxPathLimit) {
    KgGraph<IVertexId> resultMsg = new KgGraphImpl();
    List<KgGraph<IVertexId>> mergeMsgList = new ArrayList<>();

    Predicate<KgGraph<IVertexId>> predicate = new PredicateKgGraph(kgGraphSchema, ruleList);
    Iterator<KgGraph<IVertexId>> pathIt = value.getPath(staticParameters, predicate);
    long count = 0;
    while (pathIt.hasNext()) {
      KgGraph<IVertexId> path = pathIt.next();
      if (null == path) {
        continue;
      }

      mergeMsgList.add(path);
      count++;
      if (null != maxPathLimit && count >= maxPathLimit) {
        break;
      }
    }

    if (CollectionUtils.isEmpty(mergeMsgList)) {
      return null;
    }

    resultMsg.merge(mergeMsgList, kgGraphSchema);
    return resultMsg;
  }

  /** filter kgGraph by rule, will not expend kgGraph to path, just split by vertex alias set */
  public static List<KgGraph<IVertexId>> filterKgGraph(
      KgGraph<IVertexId> value,
      Set<String> splitAliasSet,
      Set<String> edgeAliasSet,
      Pattern kgGraphSchema,
      KgGraphSplitStaticParameters staticParameters,
      List<String> ruleList,
      Long maxPathLimit) {
    List<String> sortedEdgeAliasList = Lists.newArrayList(edgeAliasSet);
    ArrayList<KgGraph<IVertexId>> result = new ArrayList<>();
    Predicate<KgGraph<IVertexId>> predicate = new PredicateKgGraph(kgGraphSchema, ruleList);
    List<KgGraph<IVertexId>> splitList =
        value.split(splitAliasSet, kgGraphSchema, staticParameters, null, maxPathLimit);
    for (KgGraph<IVertexId> kgGraph : splitList) {
      if (CollectionUtils.isEmpty(sortedEdgeAliasList)) {
        if (!predicate.test(kgGraph)) {
          continue;
        }
        result.add(kgGraph);
      } else {
        List<String> useEdgeAliasList = Lists.newArrayList(sortedEdgeAliasList);
        List<List<IEdge<IVertexId, IProperty>>> combinEdgeList = new ArrayList<>();
        Iterator<String> it = useEdgeAliasList.iterator();
        while (it.hasNext()) {
          String edgeAlias = it.next();
          List<IEdge<IVertexId, IProperty>> edgeList = kgGraph.getEdge(edgeAlias);
          if (edgeList.size() <= 1) {
            it.remove();
            continue;
          }
          combinEdgeList.add(edgeList);
        }
        if (useEdgeAliasList.isEmpty()) {
          if (!predicate.test(kgGraph)) {
            continue;
          }
          result.add(kgGraph);
          continue;
        }
        CombinationIterator<IEdge<IVertexId, IProperty>> cIt =
            new CombinationIterator<>(combinEdgeList);
        while (cIt.hasNext()) {
          List<IEdge<IVertexId, IProperty>> edgeList = cIt.next();
          KgGraphImpl tmpKgGraph = new KgGraphImpl((KgGraphImpl) kgGraph);
          for (int i = 0; i < useEdgeAliasList.size(); ++i) {
            String edgeAlias = useEdgeAliasList.get(i);
            tmpKgGraph.getAlias2EdgeMap().put(edgeAlias, Sets.newHashSet(edgeList.get(i)));
          }
          if (!predicate.test(tmpKgGraph)) {
            continue;
          }
          result.add(tmpKgGraph);
        }
      }
    }
    result.trimToSize();
    return result;
  }

  public static void doStarPathLimit(
      Map<String, List<IEdge<IVertexId, IProperty>>> adjEdges, long limit, int minEdgeNum) {
    long resultCount = 1;
    int shrinkFactorValidNum = 0;
    final double shrinkFactorThreshold = 1.1;
    for (Map.Entry<String, List<IEdge<IVertexId, IProperty>>> entry : adjEdges.entrySet()) {
      int size = entry.getValue().size();
      if (!(size <= minEdgeNum)) {
        shrinkFactorValidNum++;
      }
      resultCount = resultCount * size;
    }
    if (resultCount <= limit || shrinkFactorValidNum <= 0) {
      return;
    }
    double shrinkFactor = Math.pow(1.0 * resultCount / limit, 1.0 / shrinkFactorValidNum);
    if (shrinkFactor <= shrinkFactorThreshold) {
      return;
    }
    for (Map.Entry<String, List<IEdge<IVertexId, IProperty>>> entry : adjEdges.entrySet()) {
      List<IEdge<IVertexId, IProperty>> edgeList = entry.getValue();
      if (edgeList.size() <= minEdgeNum) {
        continue;
      }
      int newListSize = (int) (edgeList.size() / shrinkFactor) + 1;
      if (newListSize < edgeList.size()) {
        entry.setValue(new ArrayList<>(edgeList.subList(0, newListSize)));
      }
    }
  }

  /** get vertex type from property */
  public static String getVertexTypeFromProperty(IProperty property) {
    return String.valueOf(property.get(Constants.CONTEXT_LABEL));
  }

  /**
   * KgGraph path 2 context
   *
   * @param kgGraph
   * @return
   */
  public static Map<String, Object> kgGraph2Context(
      Map<String, Object> initContext, KgGraph<IVertexId> kgGraph) {
    Map<String, Object> context = new HashMap<>();
    for (String alias : kgGraph.getVertexAlias()) {
      List<IVertex<IVertexId, IProperty>> vertexList = kgGraph.getVertex(alias);
      if (CollectionUtils.isEmpty(vertexList)) {
        continue;
      }
      context.put(alias, vertexContext(vertexList.get(0)));
    }

    for (String alias : kgGraph.getEdgeAlias()) {
      List<IEdge<IVertexId, IProperty>> edgeList = kgGraph.getEdge(alias);
      if (CollectionUtils.isEmpty(edgeList)) {
        continue;
      }
      context.put(alias, edgeContext(edgeList.get(0), null, kgGraph));
    }
    for (Map.Entry<String, Object> entry : initContext.entrySet()) {
      context.putIfAbsent(entry.getKey(), entry.getValue());
    }
    return context;
  }

  /** init context */
  public static Map<String, Object> getKgGraphInitContext(Pattern kgGraphSchema) {
    Map<String, Object> context = new HashMap<>();
    for (Connection connection : RunnerUtil.getConnectionSet(kgGraphSchema)) {
      context.put(connection.source(), new HashMap<>());
      context.put(connection.alias(), new HashMap<>());
      context.put(connection.target(), new HashMap<>());
    }
    return context;
  }

  /**
   * KgGraph 2 PathInfo in DSL 1.0 format
   *
   * @param kgGraph
   * @return
   */
  public static String getPathInfo(KgGraph<IVertexId> kgGraph) {
    List<Map<String, Object>> context = new ArrayList<>();
    if (null == kgGraph) {
      return JSON.toJSONString(
              context,
              SerializerFeature.PrettyFormat,
              SerializerFeature.DisableCircularReferenceDetect,
              SerializerFeature.SortField);
    }
    for (String alias : kgGraph.getVertexAlias()) {
      List<IVertex<IVertexId, IProperty>> vertexList = kgGraph.getVertex(alias);
      if (CollectionUtils.isEmpty(vertexList)) {
        continue;
      }
      Map<String, Object> vc = vertexContext(vertexList.get(0));
      vc.put(Constants.CONTEXT_TYPE, "vertex");
      vc.put("__alias__", alias);
      context.add(vc);
    }

    for (String alias : kgGraph.getEdgeAlias()) {
      List<IEdge<IVertexId, IProperty>> edgeList = kgGraph.getEdge(alias);
      if (CollectionUtils.isEmpty(edgeList)) {
        continue;
      }
      IEdge<IVertexId, IProperty> edge = edgeList.get(0);
      if (null == edge) {
        continue;
      }
      if (edge instanceof PathEdge) {
        flattenPathEdgeContext(
                (PathEdge<IVertexId, IProperty, IProperty>) edge, null, kgGraph, context);
      } else {
        Map<String, Object> eMap = getEdgePropertyMap(edge, null, kgGraph, alias);
        context.add(eMap);
      }
    }
    return JSON.toJSONString(
            context,
            SerializerFeature.PrettyFormat,
            SerializerFeature.DisableCircularReferenceDetect,
            SerializerFeature.SortField);
  }

  public static void flattenPathEdgeContext(
          PathEdge<IVertexId, IProperty, IProperty> edge,
          String edgeType,
          KgGraph<IVertexId> kgGraph,
          List<Map<String, Object>> context) {
    List<Vertex<IVertexId, IProperty>> vertexList = edge.getVertexList();
    if (CollectionUtils.isNotEmpty(vertexList)) {
      for (Vertex<IVertexId, IProperty> v : vertexList) {
        Map<String, Object> vc = vertexContext(v);
        vc.put(Constants.CONTEXT_TYPE, "vertex");
        context.add(vc);
      }
    }
    List<Edge<IVertexId, IProperty>> edgeList = edge.getEdgeList();
    if (CollectionUtils.isNotEmpty(edgeList)) {
      for (Edge<IVertexId, IProperty> e : edgeList) {
        context.add(getEdgePropertyMap(e, edgeType, kgGraph, null));
      }
    }
  }

  public static Map<String, Object> getEdgePropertyMap(
          IEdge<IVertexId, IProperty> edge, String edgeType, KgGraph<IVertexId> kgGraph, String alias) {
    Map<String, Object> edgeProperty = new HashMap<>();
    if (edge instanceof OptionalEdge) {
      edgeProperty.put(Constants.CONTEXT_LABEL, edgeType);
      IProperty property = edge.getValue();
      if (null != property) {
        for (String key : property.getKeySet()) {
          edgeProperty.put(key, property.get(key));
        }
      }
      edgeProperty.put(Constants.OPTIONAL_EDGE_FLAG, true);
    } else {
      edgeProperty.putAll(edgeContext(edge, edgeType, kgGraph));
    }
    edgeProperty.put("__alias__", alias);
    edgeProperty.put(Constants.CONTEXT_TYPE, "edge");
    return edgeProperty;
  }

  /** get vertex context in alias */
  public static Map<String, Object> vertexContext(
      IVertex<IVertexId, IProperty> vertex, String alias) {
    Map<String, Object> result = new HashMap<>();
    result.put(alias, vertexContext(vertex));
    return result;
  }

  /** vertex context */
  public static Map<String, Object> vertexContext(IVertex<IVertexId, IProperty> vertex) {
    Map<String, Object> vertexProperty = new HashMap<>();
    if (vertex instanceof MirrorVertex) {
      vertexProperty.put(Constants.MIRROR_VERTEX_FLAG, true);
    } else if (vertex instanceof NoneVertex) {
      return noneVertexContext((NoneVertex<IVertexId, IProperty>) vertex);
    }
    IProperty property = vertex.getValue();
    if (null != property) {
      for (String key : property.getKeySet()) {
        vertexProperty.put(key, property.get(key));
      }
    }
    vertexProperty.put(Constants.CONTEXT_LABEL, getVertexType(vertex));
    vertexProperty.put(Constants.VERTEX_INTERNAL_ID_KEY, vertex.getId().getInternalId());
    return vertexProperty;
  }

  private static Map<String, Object> noneVertexContext(NoneVertex<IVertexId, IProperty> vertex) {
    Map<String, Object> vertexProperty = new HashMap<>();
    IProperty property = vertex.getValue();
    if (null != property) {
      for (String key : property.getKeySet()) {
        vertexProperty.put(key, property.get(key));
      }
    }
    vertexProperty.put(Constants.NONE_VERTEX_FLAG, true);
    return vertexProperty;
  }

  /** get edge context in alias */
  public static Map<String, Object> edgeContext(
      IEdge<IVertexId, IProperty> edge, SPO spo, String alias) {
    if (null == spo) {
      spo = new SPO(edge.getType());
    }
    Map<String, Object> result = new HashMap<>();
    result.put(alias, edgeContext(edge, spo.getP(), null));
    return result;
  }

  /** get edge context */
  public static Map<String, Object> edgeContext(IEdge<IVertexId, IProperty> edge, String edgeType) {
    return edgeContext(edge, edgeType, null);
  }

  /** get edge context */
  public static Map<String, Object> edgeContext(
      IEdge<IVertexId, IProperty> edge, String edgeType, KgGraph<IVertexId> kgGraph) {
    if (edge instanceof PathEdge) {
      return pathEdgeContext((PathEdge<IVertexId, IProperty, IProperty>) edge, edgeType, kgGraph);
    } else if (edge instanceof OptionalEdge) {
      return optionalEdgeContext((OptionalEdge<IVertexId, IProperty>) edge, edgeType);
    }
    Map<String, Object> edgeProperty = new HashMap<>();
    if (StringUtils.isEmpty(edgeType)) {
      SPO spo = new SPO(edge.getType());
      edgeType = spo.getP();
    }
    IProperty property = edge.getValue();
    if (null != property) {
      for (String key : property.getKeySet()) {
        edgeProperty.put(key, property.get(key));
      }
    }
    edgeProperty.put(Constants.CONTEXT_LABEL, edgeType);
    IVertexId fromId = edge.getSourceId();
    IVertexId toId = edge.getTargetId();
    if (Direction.IN.equals(edge.getDirection())) {
      IVertexId tmp = fromId;
      fromId = toId;
      toId = tmp;
    }
    edgeProperty.put(Constants.EDGE_FROM_INTERNAL_ID_KEY, fromId.getInternalId());
    edgeProperty.put(Constants.EDGE_FROM_ID_TYPE_KEY, fromId.getType());
    edgeProperty.put(Constants.EDGE_TO_INTERNAL_ID_KEY, toId.getInternalId());
    edgeProperty.put(Constants.EDGE_TO_ID_TYPE_KEY, toId.getType());
    return edgeProperty;
  }

  public static Map<String, Object> pathEdgeContext(
      PathEdge<IVertexId, IProperty, IProperty> edge, String edgeType, KgGraph<IVertexId> kgGraph) {
    Map<String, Object> edgeProperty = new HashMap<>();
    edgeProperty.put(Constants.CONTEXT_LABEL, edgeType);
    IProperty property = edge.getValue();
    if (null != property) {
      for (String key : property.getKeySet()) {
        edgeProperty.put(key, property.get(key));
      }
    }
    edgeProperty.put(Constants.REPEAT_EDGE_FLAG, true);
    edgeProperty.put("edges", edge.getEdgeList());
    IVertex<IVertexId, IProperty> sourceVertex = kgGraph.findVertex(edge.getSourceId());
    IVertex<IVertexId, IProperty> targetVertex = kgGraph.findVertex(edge.getTargetId());
    List<IVertex<IVertexId, IProperty>> vertexList = new ArrayList<>();
    vertexList.add(sourceVertex);
    if (CollectionUtils.isNotEmpty(edge.getVertexList())) {
      vertexList.addAll(edge.getVertexList());
    }
    vertexList.add(targetVertex);
    edgeProperty.put("nodes", vertexList);
    if (CollectionUtils.isNotEmpty(edge.getEdgeList())) {
      Object fromIdObj = edge.getEdgeList().get(0).getValue().get(Constants.EDGE_FROM_ID_KEY);
      if (null != fromIdObj) {
        edgeProperty.put(Constants.EDGE_FROM_ID_KEY, fromIdObj);
      }
      Object toIdObj =
          edge.getEdgeList()
              .get(edge.getEdgeList().size() - 1)
              .getValue()
              .get(Constants.EDGE_TO_ID_KEY);
      if (null != toIdObj) {
        edgeProperty.put(Constants.EDGE_TO_ID_KEY, toIdObj);
      }
    }
    return edgeProperty;
  }

  private static Map<String, Object> optionalEdgeContext(
      OptionalEdge<IVertexId, IProperty> optionalEdge, String edgeType) {
    Map<String, Object> edgeProperty = new HashMap<>();
    edgeProperty.put(Constants.CONTEXT_LABEL, edgeType);
    IProperty property = optionalEdge.getValue();
    if (null != property) {
      for (String key : property.getKeySet()) {
        edgeProperty.put(key, property.get(key));
      }
    }
    edgeProperty.put(Constants.OPTIONAL_EDGE_FLAG, true);
    edgeProperty.put("edges", new ArrayList<>());
    edgeProperty.put("nodes", new ArrayList<>());
    return edgeProperty;
  }

  /** get dst vertex context */
  public static Map<String, Object> dstVertexContext(IEdge<IVertexId, IProperty> edge) {
    Map<String, Object> edgeContext = edgeContext(edge, null);
    Map<String, Object> vertexProperty = new HashMap<>();
    if (Direction.OUT.equals(edge.getDirection())) {
      vertexProperty.put(Constants.CONTEXT_LABEL, edgeContext.get(Constants.EDGE_TO_ID_TYPE_KEY));
      vertexProperty.put(Constants.NODE_ID_KEY, edgeContext.get(Constants.EDGE_TO_ID_KEY));
      vertexProperty.put(
          Constants.VERTEX_INTERNAL_ID_KEY, edgeContext.get(Constants.EDGE_TO_INTERNAL_ID_KEY));
    } else {
      vertexProperty.put(Constants.CONTEXT_LABEL, edgeContext.get(Constants.EDGE_FROM_ID_TYPE_KEY));
      vertexProperty.put(Constants.NODE_ID_KEY, edgeContext.get(Constants.EDGE_FROM_ID_KEY));
      vertexProperty.put(
          Constants.VERTEX_INTERNAL_ID_KEY, edgeContext.get(Constants.EDGE_FROM_INTERNAL_ID_KEY));
    }
    return vertexProperty;
  }

  public static final String FLATTEN_SEPARATOR = ".";

  /**
   * make context flatten
   *
   * @param context
   * @return
   */
  public static Map<String, Object> flattenContext(Map<String, Object> context) {
    Map<String, Object> result = new HashMap<>();
    for (Map.Entry<String, Object> entry : context.entrySet()) {
      if (!(entry.getValue() instanceof Map)) {
        continue;
      }
      Map<String, Object> propertyMap = (Map<String, Object>) entry.getValue();
      for (Map.Entry<String, Object> propertyEntry : propertyMap.entrySet()) {
        result.put(
            entry.getKey() + FLATTEN_SEPARATOR + propertyEntry.getKey(), propertyEntry.getValue());
      }
    }
    return result;
  }

  /** load csv file to list */
  public static List<String[]> loadCsvFile(String file) {
    CSVReader reader;
    try {
      reader = new CSVReader(new FileReader(file));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    try {
      return reader.readAll();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** get connection set from pattern */
  public static Set<Connection> getConnectionSet(Pattern schema) {
    Set<Connection> connectionSet = new HashSet<>();
    for (String pe : JavaConversions.mapAsJavaMap(schema.topology()).keySet()) {
      Set<Connection> partConnectionSet =
          JavaConversions.setAsJavaSet(schema.topology().get(pe).get());
      connectionSet.addAll(partConnectionSet);
    }
    return connectionSet;
  }

  /** get edge endpoint alias */
  public static List<String> getEdgeEndPointAlias(String edgeAlias, Pattern schema) {
    List<String> rst = new ArrayList<>();
    for (Connection connection : RunnerUtil.getConnectionSet(schema)) {
      if (connection.alias().equals(edgeAlias)) {
        rst.add(connection.source());
        rst.add(connection.target());
      }
    }
    return rst;
  }

  /** get neighbor alias */
  public static String getNeighborAlias(String alias, Connection pc) {
    if (alias.equals(pc.source())) {
      return pc.target();
    } else if (alias.equals(pc.target())) {
      return pc.source();
    }
    return null;
  }

  /**
   * get start id info from params
   *
   * @param params
   * @return
   */
  public static List<Tuple2<String, String>> getStartIdFromParams(Map<String, Object> params) {
    String startIdListStr = (String) params.get(ConfigKey.KG_REASONER_START_ID_LIST);
    if (StringUtils.isEmpty(startIdListStr)) {
      return null;
    }
    List<Tuple2<String, String>> startIdList = new ArrayList<>();
    JSONArray idListJson = JSON.parseArray(startIdListStr);
    for (int i = 0; i < idListJson.size(); ++i) {
      String idTupleStr = idListJson.getString(i);
      JSONArray idTupleJson = JSON.parseArray(idTupleStr);
      startIdList.add(new Tuple2<>(idTupleJson.getString(0), idTupleJson.getString(1)));
    }
    return startIdList;
  }

  /** get dsl config map */
  public static Map<String, Object> getOfflineDslParams(Map configMap, boolean base64Decode) {
    String rowStr = RunnerUtil.getStringOrDefault(configMap, ConfigKey.KG_REASONER_PARAMS, "");
    if (StringUtils.isEmpty(rowStr)) {
      return new HashMap<>();
    }
    String paramsJsonStr = rowStr;
    if (base64Decode) {
      paramsJsonStr = new String(Base64.getDecoder().decode(rowStr), StandardCharsets.UTF_8);
    }
    if (StringUtils.isEmpty(paramsJsonStr)) {
      return new HashMap<>();
    }
    return new HashMap<>(JSON.parseObject(paramsJsonStr));
  }

  /**
   * get string from map or return default value
   *
   * @param config
   * @param configKey
   * @param defaultValue
   * @return
   */
  public static String getStringOrDefault(Map config, String configKey, String defaultValue) {
    if (config.containsKey(configKey)) {
      return String.valueOf(config.get(configKey));
    } else {
      return defaultValue;
    }
  }

  /**
   * choose the udtfMeta corresponding to linkedEdgePattern
   *
   * @param pattern
   * @return
   */
  public static UdtfMeta chooseUdtfMeta(EdgePattern<LinkedPatternConnection> pattern) {
    UdfMng udfMng = UdfMngFactory.getUdfMng();
    List<UdtfMeta> udtfMetaList = udfMng.getAllUdtfMeta();
    String funcName = pattern.edge().funcName();
    UdtfMeta resultUdtfMeta = null;
    for (UdtfMeta udtfMeta : udtfMetaList) {
      if (funcName.equals(udtfMeta.getName())) {
        resultUdtfMeta = udtfMeta;
        break;
      }
    }
    if (null == resultUdtfMeta) {
      throw new RuntimeException("No found udtf=" + funcName);
    }
    return resultUdtfMeta;
  }

  /** get rule use vertex and edge */
  public static Tuple2<Set<String>, Set<Connection>> getRuleUseVertexAndEdgeSet(
      Rule rule, PartialGraphPattern kgGraphSchema) {
    Set<String> vertexAliasSet = new HashSet<>();
    Set<Connection> edgePatternSet = new HashSet<>();
    scala.collection.immutable.Set<String> nodesAlias = KgGraphSchema.getNodesAlias(kgGraphSchema);
    scala.collection.immutable.Set<String> edgesAlias = KgGraphSchema.getEdgesAlias(kgGraphSchema);
    List<IRField> irFieldList =
        JavaConversions.seqAsJavaList(
            RuleUtils.getAllInputFieldInRule(rule, nodesAlias, edgesAlias));
    for (IRField irField : irFieldList) {
      if (nodesAlias.contains(irField.name())) {
        vertexAliasSet.add(irField.name());
      }
      if (edgesAlias.contains(irField.name())) {
        Connection pc = KgGraphSchema.getPatternConnection(kgGraphSchema, irField.name());
        edgePatternSet.add(pc);
      }
    }
    for (Connection pc : edgePatternSet) {
      vertexAliasSet.remove(pc.source());
      vertexAliasSet.remove(pc.target());
    }
    return new Tuple2<>(vertexAliasSet, edgePatternSet);
  }

  /** get vertex alias set */
  public static Set<String> getVertexAliasSet(PartialGraphPattern schema) {
    Set<String> result = new HashSet<>();
    for (Connection connection : getConnectionSet(schema)) {
      result.add(connection.source());
      result.add(connection.target());
    }
    return result;
  }
  /** if match pattern contains edges in kgGraphSchema, there has intersection alias */
  public static Set<String> getIntersectionAliasSet(Pattern kgGraphSchema, Pattern matchPattern) {
    Set<String> matchRootNeighborSet = new HashSet<>();
    String matchRootAlias = matchPattern.root().alias();
    for (Connection pc : RunnerUtil.getConnectionSet(matchPattern)) {
      String neighborAlias = RunnerUtil.getNeighborAlias(matchRootAlias, pc);
      if (StringUtils.isNotEmpty(neighborAlias)) {
        matchRootNeighborSet.add(neighborAlias);
      }
    }

    Set<String> kgGraphAliasSet = new HashSet<>();
    for (Connection pc : RunnerUtil.getConnectionSet(kgGraphSchema)) {
      kgGraphAliasSet.add(pc.source());
      kgGraphAliasSet.add(pc.target());
    }

    matchRootNeighborSet.retainAll(kgGraphAliasSet);
    if (matchRootNeighborSet.isEmpty()) {
      return null;
    }
    return matchRootNeighborSet;
  }

  /** get intersection vertex's edge target */
  public static Map<String, Set<IVertexId>> getEdgeAlias2ValidTargetIdMap(
      Set<String> intersectionAliasSet,
      Collection<KgGraph<IVertexId>> kgGraphList,
      Pattern schema) {
    Map<String, Set<IVertexId>> edgeAlias2ValidTargetIdMap = new HashMap<>();
    Set<Connection> pcSet = new HashSet<>();
    for (String alias : intersectionAliasSet) {
      pcSet.addAll(JavaConversions.setAsJavaSet(KgGraphSchema.getNeighborEdges(schema, alias)));
    }
    for (Connection pc : pcSet) {
      Set<IVertexId> idSet = new HashSet<>();
      boolean hasVertex = true;
      for (KgGraph<IVertexId> kgGraph : kgGraphList) {
        KgGraphImpl kgGraphImpl = (KgGraphImpl) kgGraph;
        Set<IVertex<IVertexId, IProperty>> vertexSet =
            kgGraphImpl.getAlias2VertexMap().get(pc.target());
        if (null == vertexSet) {
          hasVertex = false;
          break;
        }
        for (IVertex<IVertexId, IProperty> v : vertexSet) {
          idSet.add(v.getId());
        }
      }
      if (hasVertex) {
        edgeAlias2ValidTargetIdMap.put(pc.alias(), idSet);
      }
    }
    return edgeAlias2ValidTargetIdMap;
  }

  /**
   * @param matchedKgGraph
   * @param rootAlias
   * @return
   */
  public static int getMinVertexCount(KgGraph<IVertexId> matchedKgGraph, String rootAlias) {
    int minMatchCount = Integer.MAX_VALUE;
    for (String alias : matchedKgGraph.getVertexAlias()) {
      if (alias.equals(rootAlias)) {
        continue;
      }
      int size = matchedKgGraph.getVertex(alias).size();
      if (size < minMatchCount) {
        minMatchCount = size;
      }
    }
    if (minMatchCount == Integer.MAX_VALUE) {
      return 1;
    }
    return minMatchCount;
  }

  /** is vertex alias */
  public static Boolean isVertexAlias(String alias, Pattern pattern) {
    for (Connection pc : getConnectionSet(pattern)) {
      if (pc.alias().equals(alias)) {
        return false;
      } else if (pc.source().equals(alias)) {
        return true;
      } else if (pc.target().equals(alias)) {
        return true;
      }
    }
    return null;
  }

  /** get vertex alias set */
  public static Set<String> getVertexAliasSet(Pattern pattern) {
    Set<String> result = new HashSet<>();
    for (Connection pc : getConnectionSet(pattern)) {
      result.add(pc.source());
      result.add(pc.target());
    }
    return result;
  }

  /** check is all first aggregator */
  public static List<String> getFirstEdgeAliasList(
      scala.collection.immutable.Map<Var, Aggregator> aggregations) {
    List<String> result = new ArrayList<>();
    if (null == aggregations || aggregations.isEmpty()) {
      return null;
    }
    for (Map.Entry<Var, Aggregator> entry : JavaConversions.mapAsJavaMap(aggregations).entrySet()) {
      Var var = entry.getKey();
      if (!(var instanceof EdgeVar)) {
        return null;
      }
      Aggregator aggregator = entry.getValue();
      if (!(aggregator instanceof AggOpExpr)) {
        return null;
      }
      AggOpExpr aggOpExpr = (AggOpExpr) aggregator;
      if (aggOpExpr.name() != First$.MODULE$) {
        return null;
      }
      EdgeVar edgeVar = (EdgeVar) var;
      result.add(edgeVar.name());
    }
    return result;
  }

  private static final Map<Integer, Map<IVertexId, Collection<KgGraph<IVertexId>>>>
      JOIN_RIGHT_DATA_STATIC = new ConcurrentHashMap<>();

  public static Map<IVertexId, Collection<KgGraph<IVertexId>>> getRightJoinData(int index) {
    return JOIN_RIGHT_DATA_STATIC.computeIfAbsent(index, k -> new HashMap<>());
  }

  public static void clearRightJoinData(int index) {
    JOIN_RIGHT_DATA_STATIC.remove(index);
  }

  /** get fold repeat info */
  public static FoldRepeatEdgeInfo getFoldRepeatEdgeInfo(
      scala.collection.immutable.List<Tuple2<scala.collection.immutable.List<Var>, RichVar>>
          windMapping,
      PartialGraphPattern kgGraphSchema) {
    String fromEdgeAlias = windMapping.head()._1.apply(1).name();
    String toEdgeAlias = windMapping.head()._2.name();
    String fromVertexAlias = windMapping.head()._1.apply(2).name();
    String toVertexAlias =
        ((RepeatPathVar) (windMapping.head()._2)).pathVar().elements().apply(2).name();
    return new FoldRepeatEdgeInfo(fromEdgeAlias, toEdgeAlias, fromVertexAlias, toVertexAlias);
  }

  /** get unfold repeat edge info */
  public static UnfoldRepeatEdgeInfo getUnfoldEdgeInfo(
      scala.collection.immutable.List<Tuple2<RichVar, scala.collection.immutable.List<Var>>>
          mapping,
      PartialGraphPattern kgGraphSchema) {
    String edgeAlias = ((RepeatPathVar) mapping.head()._1).pathVar().name();
    String foldVertexAlias = mapping.head()._2().apply(2).name();
    String anchorVertexAlias = mapping.head()._2().apply(0).name();
    int lower = ((RepeatPathVar) mapping.head()._1).lower();
    return new UnfoldRepeatEdgeInfo(edgeAlias, foldVertexAlias, anchorVertexAlias, lower);
  }

  /** readable pattern */
  public static String getReadablePattern(Pattern pattern) {
    StringBuilder sb = new StringBuilder();
    sb.append("root=").append(pattern.root().alias());
    Set<Connection> connectionSet = RunnerUtil.getConnectionSet(pattern);
    if (connectionSet.isEmpty()) {
      return sb.toString();
    }
    sb.append(",edge=");
    boolean first = true;
    for (Connection connection : connectionSet) {
      if (!first) {
        sb.append(",");
      } else {
        first = false;
      }
      sb.append(connection.alias());
    }
    return sb.toString();
  }

  /** readable */
  public static String getReadableAsList(scala.collection.immutable.List<String> as) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < as.size(); ++i) {
      if (sb.length() > 0) {
        sb.append(",");
      }
      sb.append(as.apply(i));
    }
    return sb.toString();
  }

  public static String getReadableByKey(scala.collection.immutable.List<Var> byKey) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < byKey.size(); ++i) {
      if (sb.length() > 0) {
        sb.append(",");
      }
      Var var = byKey.apply(i);
      sb.append(var.name());
    }
    return sb.toString();
  }

  public static String getReadableAddVertex(AddVertex addVertex) {
    StringBuilder sb = new StringBuilder();
    sb.append("alias=")
        .append(addVertex.s().alias())
        .append(",type=")
        .append(addVertex.s().typeNames().iterator().next());
    return sb.toString();
  }

  public static String getReadableAddPredicate(AddPredicate addPredicate) {
    StringBuilder sb = new StringBuilder();
    sb.append("alias=").append(addPredicate.predicate().alias());
    sb.append(",type=").append(addPredicate.predicate().label());
    sb.append(",from=").append(addPredicate.predicate().source());
    sb.append(",to=").append(addPredicate.predicate().target());
    return sb.toString();
  }

  public static String getReadableAddFields(Map<Var, List<String>> addFieldsInfo) {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<Var, List<String>> entry : addFieldsInfo.entrySet()) {
      if (sb.length() > 0) {
        sb.append(",");
      }
      Var var = entry.getKey();
      String str = var.toString();
      if (var instanceof PropertyVar) {
        PropertyVar propertyVar = (PropertyVar) var;
        str = propertyVar.name() + "." + propertyVar.field().name();
      }
      sb.append(str).append("=").append(Arrays.toString(entry.getValue().toArray(new String[0])));
    }
    return sb.toString();
  }

  public static String getReadableJoinString(
      scala.collection.immutable.List<Tuple2<String, String>> onAlias,
      scala.collection.immutable.Map<Var, Var> rhsSchemaMapping) {
    StringBuilder sb = new StringBuilder();
    sb.append(onAlias);
    Map<Var, Var> javaRhsSchemaMapping =
        new HashMap<>(JavaConversions.mapAsJavaMap(rhsSchemaMapping));
    sb.append(",right=");
    boolean comma = false;
    for (Map.Entry<Var, Var> entry : javaRhsSchemaMapping.entrySet()) {
      if (comma) {
        sb.append(",");
      } else {
        comma = true;
      }
      Var var = entry.getKey();
      if (var instanceof NodeVar) {
        sb.append("v:").append(var.name());
      } else if (var instanceof EdgeVar) {
        sb.append("e:").append(var.name());
      }
    }
    return sb.toString();
  }

  /** get group by var */
  public static Object[] getVarFromKgGraph(
      KgGraph<IVertexId> kgGraph, scala.collection.immutable.List<Var> varList) {
    Object[] result = new Object[varList.size()];
    for (int i = 0; i < varList.size(); ++i) {
      Var var = varList.apply(i);
      if (var instanceof NodeVar) {
        result[i] = getVertexProperty(kgGraph, var.name(), Constants.VERTEX_INTERNAL_ID_KEY);
      } else if (var instanceof PropertyVar) {
        PropertyVar propertyVar = (PropertyVar) var;
        result[i] = getProperty(kgGraph, propertyVar.name(), propertyVar.field().name());
      }
    }
    return result;
  }

  /** get property */
  public static Object getProperty(KgGraph<IVertexId> kgGraph, String alias, String propertyName) {
    IProperty propertyMap;
    List<IVertex<IVertexId, IProperty>> vertexList = kgGraph.getVertex(alias);
    if (CollectionUtils.isEmpty(vertexList)) {
      List<IEdge<IVertexId, IProperty>> edgeList = kgGraph.getEdge(alias);
      if (CollectionUtils.isEmpty(edgeList)) {
        throw new RuntimeException("unknown alias " + alias + "," + kgGraph);
      }
      propertyMap = edgeList.get(0).getValue();
    } else {
      propertyMap = vertexList.get(0).getValue();
    }
    return propertyMap.get(propertyName);
  }

  /** get vertex property from KgGraph */
  public static Object getVertexProperty(
      KgGraph<IVertexId> kgGraph, String alias, String propertyName) {
    IProperty propertyMap;
    List<IVertex<IVertexId, IProperty>> vertexList = kgGraph.getVertex(alias);
    if (CollectionUtils.isEmpty(vertexList)) {
      throw new RuntimeException("unknown alias " + alias + "," + kgGraph);
    }
    if (Constants.VERTEX_INTERNAL_ID_KEY.equals(propertyName)) {
      return vertexList.get(0).getId();
    }
    propertyMap = vertexList.get(0).getValue();
    return propertyMap.get(propertyName);
  }

  private static Double changeNumberObj(Comparable o) {
    if (o instanceof Integer) {
      return ((Integer) o).doubleValue();
    }
    if (o instanceof Long) {
      return ((Long) o).doubleValue();
    }
    if (o instanceof Float) {
      return ((Float) o).doubleValue();
    }
    if (o instanceof Double) {
      return (Double) o;
    }
    return null;
  }

  private static int compareComparables(Comparable o1, Comparable o2) {
    // Ensure we compare objects of the same type
    if (o1.getClass().equals(o2.getClass())) {
      return o1.compareTo(o2);
    }
    Double compare1 = changeNumberObj(o1);
    Double compare2 = changeNumberObj(o2);

    if (compare1 != null && compare2 != null) {
      return compare1.compareTo(compare2);
    }
    // Different types, compare class names or any other logic
    return o1.getClass().getName().compareTo(o2.getClass().getName());
  }

  /** compare two object */
  public static int compareTwoObject(Object v1, Object v2) {
    if (null == v1) {
      if (null == v2) {
        return 0;
      }
      return -1;
    }
    if (!(v1 instanceof Comparable)) {
      throw new RuntimeException("value can not comparable, " + v1.getClass().getName());
    }
    if (null == v2) {
      return 1;
    }
    Comparable c1 = (Comparable) v1;
    Comparable c2 = (Comparable) v2;
    return compareComparables(c1, c2);
  }

  /** get edge string id */
  public static String getEdgeIdentifier(IEdge<IVertexId, IProperty> edge) {
    return edge.getSourceId().getInternalId()
        + edge.getType()
        + edge.getTargetId().getInternalId()
        + edge.getVersion()
        + edge.getDirection();
  }

  /** init type id mapping */
  public static void initTypeMapping(GraphLoaderConfig graphLoaderConfig) {
    for (VertexLoaderConfig vertexLoaderConfig : graphLoaderConfig.getVertexLoaderConfigs()) {
      MapType2IdFactory.getMapType2Id().getIdByType(vertexLoaderConfig.getVertexType());
    }
    for (EdgeLoaderConfig edgeLoaderConfig : graphLoaderConfig.getEdgeLoaderConfigs()) {
      MapType2IdFactory.getMapType2Id().getIdByType(edgeLoaderConfig.getEdgeType());
    }
  }

  /** get running context */
  public static Map<String, Object> getTaskRunningContext(
      KGReasonerSession session, Map<String, Object> params) {
    Map<String, Object> taskRunningContext = new HashMap<>();
    Map<String, String> idFilterMaps =
        JavaConversions.mapAsJavaMap(session.getIdFilterParameters());
    Set<String> variableMap = JavaConversions.setAsJavaSet(session.getParameterVariable());
    if (variableMap != null && variableMap.size() != 0) {
      for (String var : variableMap) {
        if (!params.containsKey(var)) {
          throw new RuntimeException("parameter " + var + " must input");
        }
        Expr expr = session.parser().parseExpr(params.get(var).toString());
        List<String> rule = WareHouseUtils.getRuleList(expr);
        Object obj = RuleRunner.getInstance().executeExpression(new HashMap<>(), rule, "");

        if (idFilterMaps.containsValue(var)) {
          // convert 2 string id
          if (obj instanceof Object[]) {
            List<Object> originIds = new ArrayList<>();
            Object[] ids = (Object[]) obj;
            for (Object id : ids) {
              originIds.add(id.toString());
            }
            obj = originIds.toArray();
          }
        }
        taskRunningContext.put(var, obj);
      }
    }
    return taskRunningContext;
  }

  public static Set<String> getAllVertexAlias(Pattern schema) {
    Set<String> result = new HashSet<>();
    for (Connection connection : getConnectionSet(schema)) {
      result.add(connection.source());
      result.add(connection.target());
    }
    return result;
  }

  public static List<String> joinAliasAfterMapping(
      scala.collection.immutable.List<Tuple2<String, String>> onAlias,
      scala.collection.immutable.Map<Var, Var> lhsSchemaMapping) {
    Map<String, String> lMap = getAlaisMapping(lhsSchemaMapping);
    List<String> joinAlias = new ArrayList<>();
    for (int i = 0; i < onAlias.size(); ++i) {
      Tuple2<String, String> oldTuple2 = onAlias.apply(i);
      joinAlias.add(lMap.getOrDefault(oldTuple2._1(), oldTuple2._1()));
    }
    return joinAlias;
  }

  public static scala.collection.immutable.Map<Var, Var> newRhsSchemaMapping(
      scala.collection.immutable.Map<Var, Var> rhsSchemaMapping,
      scala.collection.immutable.List<Tuple2<String, String>> onAlias) {
    Map<String, String> rightMapLeft = new HashMap<>();
    for (int i = 0; i < onAlias.size(); ++i) {
      Tuple2<String, String> tuple2 = onAlias.apply(i);
      rightMapLeft.put(tuple2._2(), tuple2._1());
    }
    Map<Var, Var> result = new HashMap<>();
    for (Var key : JavaConversions.asJavaIterable(rhsSchemaMapping.keys())) {
      Var value = rhsSchemaMapping.apply(key);
      if (rightMapLeft.containsKey(key.name())) {
        result.put(key, new NodeVar(rightMapLeft.get(key.name()), null));
      } else {
        result.put(key, value);
      }
    }
    return Convert2ScalaUtil.toScalaImmutableMap(result);
  }

  public static Map<String, String> getAlaisMapping(
      scala.collection.immutable.Map<Var, Var> schemaMapping) {
    Map<String, String> result = new HashMap<>();
    for (Map.Entry<Var, Var> entry : JavaConversions.mapAsJavaMap(schemaMapping).entrySet()) {
      if (entry.getKey() instanceof NodeVar
          && entry.getValue() instanceof NodeVar
          && !entry.getKey().name().equals(entry.getValue().name())) {
        result.put(entry.getKey().name(), entry.getValue().name());
      } else if (entry.getKey() instanceof EdgeVar
          && entry.getValue() instanceof EdgeVar
          && !entry.getKey().name().equals(entry.getValue().name())) {
        result.put(entry.getKey().name(), entry.getValue().name());
      }
    }
    return result;
  }

  /** outer join none graph, edge oder */
  public static List<Connection> getJoinNoneEdgeOrder(String startAlias, Pattern schema) {
    List<Connection> result = new ArrayList<>();
    Set<String> connectedVertexAlias = new HashSet<>();
    connectedVertexAlias.add(startAlias);
    Set<Connection> tmpConnectionSet = RunnerUtil.getConnectionSet(schema);
    while (!tmpConnectionSet.isEmpty()) {
      boolean find = false;
      for (Connection connection : tmpConnectionSet) {
        if (connectedVertexAlias.contains(connection.source())) {
          tmpConnectionSet.remove(connection);
          result.add(connection);
          connectedVertexAlias.add(connection.target());
          find = true;
          break;
        } else if (connectedVertexAlias.contains(connection.target())) {
          tmpConnectionSet.remove(connection);
          result.add(connection);
          connectedVertexAlias.add(connection.source());
          find = true;
          break;
        }
      }
      if (!find) {
        throw new RuntimeException("can not find order");
      }
    }
    return result;
  }

  public static List<String> sortGroupByAlias(
      List<String> byAliasList, Set<String> validRootAlias) {
    List<String> rstList = new ArrayList<>();
    for (String alias : byAliasList) {
      if (rstList.contains(alias)) {
        continue;
      }
      if (validRootAlias.contains(alias)) {
        rstList.add(alias);
      }
    }
    for (String alias : byAliasList) {
      if (rstList.contains(alias)) {
        continue;
      }
      rstList.add(alias);
    }
    return rstList;
  }
  /** outer join none */
  public static void kgGraphJoinNone(KgGraphImpl kgGraph, List<Connection> noneEdgeOrder) {
    for (Connection connection : noneEdgeOrder) {
      IVertex<IVertexId, IProperty> sourceV = null;
      IVertex<IVertexId, IProperty> targetV = null;
      Set<IVertex<IVertexId, IProperty>> sourceSet =
          kgGraph.getAlias2VertexMap().get(connection.source());
      if (CollectionUtils.isNotEmpty(sourceSet)) {
        sourceV = sourceSet.iterator().next();
      }
      Set<IVertex<IVertexId, IProperty>> targetSet =
          kgGraph.getAlias2VertexMap().get(connection.target());
      if (CollectionUtils.isNotEmpty(targetSet)) {
        targetV = targetSet.iterator().next();
      }
      if (null == sourceV && null == targetV) {
        throw new RuntimeException("noneEdgeOder error" + Arrays.toString(noneEdgeOrder.toArray()));
      }

      if (null == sourceV) {
        sourceV = targetV;
        kgGraph
            .getAlias2VertexMap()
            .put(connection.source(), Sets.newHashSet(new NoneVertex<>(sourceV.getId())));
      } else if (null == targetV) {
        targetV = sourceV;
        kgGraph
            .getAlias2VertexMap()
            .put(connection.target(), Sets.newHashSet(new NoneVertex<>(targetV.getId())));
      }
      kgGraph
          .getAlias2EdgeMap()
          .put(
              connection.alias(),
              Sets.newHashSet(new OptionalEdge<>(sourceV.getId(), targetV.getId())));
    }
  }

  public static Tuple2<List<String>, List<String>> getOverlapAlias(
      PartialGraphPattern leftSchema, PartialGraphPattern rightSchema) {
    Tuple2<
            scala.collection.immutable.Set<PatternElement>,
            scala.collection.immutable.Set<Connection>>
        tuple2 = KgGraphSchema.getOverlapSchema(leftSchema, rightSchema);
    List<String> overlapVertexAlias = new ArrayList<>();
    List<String> overlapEdgeAlias = new ArrayList<>();
    for (PatternElement patternElement : JavaConversions.setAsJavaSet(tuple2._1())) {
      overlapVertexAlias.add(patternElement.alias());
    }
    for (Connection connection : JavaConversions.setAsJavaSet(tuple2._2())) {
      overlapEdgeAlias.add(connection.alias());
    }
    return new Tuple2<>(overlapVertexAlias, overlapEdgeAlias);
  }

  public static Map<String, Tuple2<Direction, Direction>> getOverlapEdgeDirectionDiff(
      PartialGraphPattern leftSchema, PartialGraphPattern rightSchema) {
    return JavaConversions.mapAsJavaMap(
        KgGraphSchema.getEdgeDirectionDiff(leftSchema, rightSchema));
  }

  public static void updateUdafDataFromProperty(
      LazyUdaf udaf, IProperty property, String propertyName) {
    if (property.isKeyExist(propertyName)) {
      udaf.update(property.get(propertyName));
    }
  }
}
