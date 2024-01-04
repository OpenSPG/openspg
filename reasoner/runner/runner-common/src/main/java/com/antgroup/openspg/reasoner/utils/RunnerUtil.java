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

package com.antgroup.openspg.reasoner.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphSplitStaticParameters;
import com.antgroup.openspg.reasoner.lube.block.AddPredicate;
import com.antgroup.openspg.reasoner.lube.block.AddVertex;
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field;
import com.antgroup.openspg.reasoner.lube.common.expr.AggOpExpr;
import com.antgroup.openspg.reasoner.lube.common.expr.Aggregator;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.common.expr.First$;
import com.antgroup.openspg.reasoner.lube.common.graph.IRField;
import com.antgroup.openspg.reasoner.lube.common.pattern.*;
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
import com.antgroup.openspg.reasoner.udf.model.BaseUdtf;
import com.antgroup.openspg.reasoner.udf.model.LinkedUdtfResult;
import com.antgroup.openspg.reasoner.udf.model.UdtfMeta;
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.antgroup.openspg.reasoner.util.KgGraphSchema;
import com.antgroup.openspg.reasoner.warehouse.common.config.EdgeLoaderConfig;
import com.antgroup.openspg.reasoner.warehouse.common.config.GraphLoaderConfig;
import com.antgroup.openspg.reasoner.warehouse.common.config.VertexLoaderConfig;
import com.antgroup.openspg.reasoner.warehouse.common.partition.BasePartitioner;
import com.antgroup.openspg.reasoner.warehouse.utils.WareHouseUtils;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
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
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;
import scala.collection.JavaConversions;

public class RunnerUtil {
  private static final Logger log = LoggerFactory.getLogger(RunnerUtil.class);

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
      Pattern kgGraphSchema,
      KgGraphSplitStaticParameters staticParameters,
      List<String> ruleList,
      Long maxPathLimit) {
    Predicate<KgGraph<IVertexId>> predicate = new PredicateKgGraph(kgGraphSchema, ruleList);
    return value.split(splitAliasSet, kgGraphSchema, staticParameters, predicate, maxPathLimit);
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
      return optionalEdgeContext((OptionalEdge<IVertexId, IProperty>) edge);
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
    IVertexId toId = edge.getSourceId();
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
    return edgeProperty;
  }

  private static Map<String, Object> optionalEdgeContext(
      OptionalEdge<IVertexId, IProperty> optionalEdge) {
    Map<String, Object> edgeProperty = new HashMap<>();
    IProperty property = optionalEdge.getValue();
    if (null != property) {
      for (String key : property.getKeySet()) {
        edgeProperty.put(key, property.get(key));
      }
    }
    edgeProperty.put(Constants.OPTIONAL_EDGE_FLAG, true);
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
    List<String[]> result = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        for (int i = 0; i < values.length; ++i) {
          values[i] = values[i].substring(1, values[i].length() - 1);
        }
        result.add(values);
      }
    } catch (IOException e) {
      throw new RuntimeException("load csv file error", e);
    }
    return result;
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
   * compute the linked edge
   *
   * @return
   */
  public static List<KgGraph<IVertexId>> linkEdge(
      String taskId,
      KgGraph<IVertexId> kgGraph,
      PartialGraphPattern kgGraphSchema,
      KgGraphSplitStaticParameters staticParameters,
      EdgePattern<LinkedPatternConnection> linkedEdgePattern,
      UdtfMeta udtfMeta,
      BasePartitioner partitioner) {
    Iterator<KgGraph<IVertexId>> it = kgGraph.getPath(staticParameters, null);
    List<KgGraph<IVertexId>> mergeList = new ArrayList<>();

    while (it.hasNext()) {
      KgGraph<IVertexId> path = it.next();

      Map<String, Object> context =
          RunnerUtil.kgGraph2Context(RunnerUtil.getKgGraphInitContext(kgGraphSchema), path);
      List<Expr> exprList = JavaConversions.seqAsJavaList(linkedEdgePattern.edge().params());
      List<Object> paramList = new ArrayList<>();
      for (Expr expr : exprList) {
        List<String> exprStr = WareHouseUtils.getRuleList(expr);
        Object parameter = RuleRunner.getInstance().executeExpression(context, exprStr, taskId);
        paramList.add(parameter);
      }

      BaseUdtf tableFunction = udtfMeta.createTableFunction();
      tableFunction.process(paramList);
      List<List<Object>> udtfResult = tableFunction.getCollector();
      List<LinkedUdtfResult> linkedUdtfResultList =
          udtfResult.stream()
              .flatMap(List::stream)
              .filter(Objects::nonNull)
              .map(
                  obj -> {
                    if (!(obj instanceof LinkedUdtfResult)) {
                      throw new RuntimeException("linked udtf must return LinkedUdtfResult");
                    }
                    return ((LinkedUdtfResult) obj);
                  })
              .collect(Collectors.toList());
      if (CollectionUtils.isEmpty(linkedUdtfResultList)) {
        continue;
      }
      String sourceAlias = linkedEdgePattern.src().alias();
      List<IVertex<IVertexId, IProperty>> sourceList = path.getVertex(sourceAlias);
      if (null == sourceList || sourceList.size() != 1) {
        throw new RuntimeException("There is more than one start vertex in kgGraph path");
      }
      IVertexId sourceId = sourceList.get(0).getId();
      Connection pc = linkedEdgePattern.edge();

      Map<String, Set<IVertex<IVertexId, IProperty>>> newAliasVertexMap = new HashMap<>();
      Map<String, Set<IEdge<IVertexId, IProperty>>> newAliasEdgeMap = new HashMap<>();
      for (LinkedUdtfResult linkedUdtfResult : linkedUdtfResultList) {
        for (String targetIdStr : linkedUdtfResult.getTargetVertexIdList()) {
          // add target vertex
          String targetAlias = pc.target();
          PatternElement targetVertexMeta = linkedEdgePattern.dst();
          List<String> targetVertexTypes =
              new ArrayList<>(JavaConversions.setAsJavaSet(targetVertexMeta.typeNames()));
          if (targetVertexTypes.size() == 0) {
            throw new RuntimeException(
                "Linked edge target vertex type must contains at least one type");
          }
          for (String targetVertexType : targetVertexTypes) {
            IVertexId targetId = IVertexId.from(targetIdStr, targetVertexType);
            if (partitioner != null && !partitioner.canPartition(targetId)) {
              continue;
            }
            // need add property with id
            Set<IVertex<IVertexId, IProperty>> newVertexSet =
                newAliasVertexMap.computeIfAbsent(targetAlias, k -> new HashSet<>());
            newVertexSet.add(new Vertex(targetId));

            // construct new edge
            IEdge<IVertexId, IProperty> linkedEdge = new Edge<>(sourceId, targetId, null);
            linkedEdge.setType(
                sourceId.getType()
                    + "_"
                    + linkedEdgePattern.edge().funcName()
                    + "_"
                    + targetVertexType);
            String edgeAlias = pc.alias();

            Set<IEdge<IVertexId, IProperty>> newEdgeSet =
                newAliasEdgeMap.computeIfAbsent(edgeAlias, k -> new HashSet<>());
            newEdgeSet.add(linkedEdge);
          }
        }
      }
      if (!(newAliasVertexMap.isEmpty() && newAliasEdgeMap.isEmpty())) {
        KgGraph<IVertexId> newKgGraph = new KgGraphImpl(newAliasVertexMap, newAliasEdgeMap);
        path.merge(Lists.newArrayList(newKgGraph), null);
        mergeList.add(path);
      }
    }
    return mergeList;
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
      Set<IVertexId> idSet =
          edgeAlias2ValidTargetIdMap.computeIfAbsent(pc.alias(), k -> new HashSet<>());
      for (KgGraph<IVertexId> kgGraph : kgGraphList) {
        List<IVertex<IVertexId, IProperty>> vList = kgGraph.getVertex(pc.target());
        for (IVertex<IVertexId, IProperty> v : vList) {
          idSet.add(v.getId());
        }
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

  /** get join schema */
  public static PartialGraphPattern getAfterJoinSchema(
      PartialGraphPattern thisSchema,
      PartialGraphPattern otherSchema,
      scala.collection.immutable.List<Tuple2<String, String>> onAlias,
      scala.collection.immutable.Map<Var, Var> rhsSchemaMapping) {

    Map<Var, Var> aliasNameMapBack = new HashMap<>();
    Map<Var, Var> javaRhsSchemaMapping =
        new HashMap<>(JavaConversions.mapAsJavaMap(rhsSchemaMapping));
    scala.collection.immutable.Set<Field> scalaEmptySet =
        JavaConversions.asScalaSet(new HashSet<>()).toSet();
    for (Tuple2<String, String> joinOnAlias : JavaConversions.seqAsJavaList(onAlias)) {
      for (Map.Entry<Var, Var> entry : javaRhsSchemaMapping.entrySet()) {
        if (entry.getKey().name().equals(joinOnAlias._2())) {
          aliasNameMapBack.put(
              new NodeVar(entry.getValue().name(), scalaEmptySet),
              new NodeVar(joinOnAlias._1(), scalaEmptySet));
        }
      }
    }
    PartialGraphPattern otherRdgSchemaAfterRename =
        KgGraphSchema.schemaAliasMapping(otherSchema, rhsSchemaMapping);
    if (!aliasNameMapBack.isEmpty()) {
      otherRdgSchemaAfterRename =
          KgGraphSchema.schemaAliasMapping(
              otherRdgSchemaAfterRename, Convert2ScalaUtil.toScalaImmutableMap(aliasNameMapBack));
    }
    return KgGraphSchema.expandSchema(thisSchema, otherRdgSchemaAfterRename);
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
    for (Map.Entry<Var, Var> entry : javaRhsSchemaMapping.entrySet()) {
      sb.append(",");
      Var var = entry.getKey();
      if (var instanceof NodeVar) {
        sb.append("v=").append(var.name());
      } else if (var instanceof EdgeVar) {
        sb.append("e=").append(var.name());
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
    return c1.compareTo(v2);
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
          List<Object> originIds = new ArrayList<>();
          // convert 2 internal id
          if (obj instanceof Object[]) {
            Object[] ids = (Object[]) obj;
            for (Object id : ids) {
              originIds.add(id.toString());
            }
          } else {
            originIds.add(obj);
          }
          obj = originIds.toArray();
        }
        taskRunningContext.put(var, obj);
      }
    }
    return taskRunningContext;
  }
}
