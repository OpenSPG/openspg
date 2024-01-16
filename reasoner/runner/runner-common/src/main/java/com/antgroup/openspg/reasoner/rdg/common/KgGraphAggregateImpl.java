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

import static com.antgroup.openspg.reasoner.warehouse.utils.DebugVertexIdSet.DEBUG_VERTEX_ALIAS;
import static com.antgroup.openspg.reasoner.warehouse.utils.DebugVertexIdSet.DEBUG_VERTEX_ID_SET;

import com.antgroup.openspg.reasoner.common.exception.NotImplementedException;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.AggregationSchemaInfo;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphSplitStaticParameters;
import com.antgroup.openspg.reasoner.lube.common.expr.AggIfOpExpr;
import com.antgroup.openspg.reasoner.lube.common.expr.AggOpExpr;
import com.antgroup.openspg.reasoner.lube.common.expr.Aggregator;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.common.expr.GetField;
import com.antgroup.openspg.reasoner.lube.common.expr.Ref;
import com.antgroup.openspg.reasoner.lube.common.expr.UnaryOpExpr;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.antgroup.openspg.reasoner.lube.logical.EdgeVar;
import com.antgroup.openspg.reasoner.lube.logical.NodeVar;
import com.antgroup.openspg.reasoner.lube.logical.PathVar;
import com.antgroup.openspg.reasoner.lube.logical.PropertyVar;
import com.antgroup.openspg.reasoner.lube.logical.Var;
import com.antgroup.openspg.reasoner.rdg.common.groupProcess.AggIfOpProcessBaseGroupProcess;
import com.antgroup.openspg.reasoner.rdg.common.groupProcess.AggOpProcessBaseGroupProcess;
import com.antgroup.openspg.reasoner.rdg.common.groupProcess.BaseGroupProcess;
import com.antgroup.openspg.reasoner.udf.model.BaseUdaf;
import com.antgroup.openspg.reasoner.udf.model.UdafMeta;
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;
import scala.collection.DebugUtils;

public class KgGraphAggregateImpl implements Serializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(DebugUtils.class);
  private static final long serialVersionUID = -4440174716170402565L;

  private final Pattern kgGraphSchema;
  private final KgGraphSplitStaticParameters staticParameters;
  private final Set<String> vertexAliasSet;
  private final Map<Var, Aggregator> aggregatorMap;
  private final Long maxPathLimit;
  private final String taskId;
  private final Set<String> byAliasSet;

  private final AggregationSchemaInfo aggregationSchemaInfo;
  private final Map<String, Object> initRuleContext;

  // property aggregator
  private transient Map<String, List<BaseGroupProcess>> propertyAggregatorParsedMap;

  // path value aggregator
  private transient Map<String, List<BaseGroupProcess>> pathValueAggregatorParsedMap;

  // struct aggregator, for example: First, KeepLongestPath etc.
  private transient Map<String, List<BaseGroupProcess>> structAggregatorParsedMap;

  /** aggregate implement */
  public KgGraphAggregateImpl(
      String taskId,
      String rootAlias,
      List<String> byAliasList,
      Pattern kgGraphSchema,
      Map<Var, Aggregator> aggregatorMap,
      Long maxPathLimit) {
    this.kgGraphSchema = kgGraphSchema;
    this.aggregationSchemaInfo = new AggregationSchemaInfo(this.kgGraphSchema);
    this.initRuleContext = RunnerUtil.getKgGraphInitContext(this.kgGraphSchema);
    this.staticParameters = new KgGraphSplitStaticParameters(null, this.kgGraphSchema);
    this.vertexAliasSet = RunnerUtil.getVertexAliasSet(kgGraphSchema);
    this.aggregatorMap = new HashMap<>(aggregatorMap);
    this.maxPathLimit = maxPathLimit;
    this.taskId = taskId;
    this.byAliasSet = Sets.newHashSet(byAliasList);
    this.byAliasSet.add(rootAlias);
  }

  private BaseGroupProcess getGroupProcess(Var var, Aggregator aggOpExpr) {
    if (aggOpExpr instanceof AggOpExpr) {
      return new AggOpProcessBaseGroupProcess(this.taskId, var, aggOpExpr);
    }

    if (aggOpExpr instanceof AggIfOpExpr) {
      return new AggIfOpProcessBaseGroupProcess(this.taskId, var, aggOpExpr);
    }
    throw new NotImplementedException(
        "unsupported aggregator, type=" + aggOpExpr.getClass().getName(), null);
  }

  /** init implement */
  public void init() {
    this.structAggregatorParsedMap = new HashMap<>();
    this.propertyAggregatorParsedMap = new HashMap<>();
    this.pathValueAggregatorParsedMap = new HashMap<>();
    for (Map.Entry<Var, Aggregator> entry : this.aggregatorMap.entrySet()) {
      Var var = entry.getKey();
      String aliasKey = var.name();
      Aggregator aggregator = entry.getValue();

      BaseGroupProcess aggOpProcess = getGroupProcess(var, aggregator);
      List<BaseGroupProcess> list;
      if (aggOpProcess.notPropertyAgg()) {
        list = this.structAggregatorParsedMap.computeIfAbsent(aliasKey, k -> new ArrayList<>());
      } else {
        if (aggOpProcess.getExprUseAliasSet().size() > 1) {
          list =
              this.pathValueAggregatorParsedMap.computeIfAbsent(aliasKey, k -> new ArrayList<>());
        } else {
          list = this.propertyAggregatorParsedMap.computeIfAbsent(aliasKey, k -> new ArrayList<>());
        }
      }
      list.add(aggOpProcess);
    }
  }

  /** process property var */
  public Map<String, Object> propertyVarMap(
      Collection<KgGraph<IVertexId>> values, List<BaseGroupProcess> aggInfoList) {

    Map<String, Object> propertyMap = new HashMap<>();
    for (BaseGroupProcess aggInfo : aggInfoList) {
      PropertyVar var = (PropertyVar) aggInfo.getVar();

      // 进行聚合计算
      UdafMeta udafMeta = aggInfo.getUdafMeta();
      Object[] udafInitParams = aggInfo.getUdfInitParams();
      List<String> ruleList = aggInfo.getRuleList();
      List<KgGraph<IVertexId>> valueFilteredList = getValueFilteredList(values, ruleList);
      Object aggValue = doAggregation(valueFilteredList, udafMeta, udafInitParams, aggInfo);
      String targetPropertyName = var.field().name();
      propertyMap.put(targetPropertyName, aggValue);
    }
    return propertyMap;
  }

  private List<KgGraph<IVertexId>> getValueFilteredList(
      Collection<KgGraph<IVertexId>> values, List<String> ruleList) {
    List<KgGraph<IVertexId>> valueFilteredList = new ArrayList<>();
    for (KgGraph<IVertexId> value : values) {
      if (CollectionUtils.isNotEmpty(ruleList)) {
        KgGraph<IVertexId> kgGraph =
            RunnerUtil.filterKgGraph(
                value, kgGraphSchema, staticParameters, ruleList, maxPathLimit);
        if (null != kgGraph) {
          valueFilteredList.add(kgGraph);
        }
      } else {
        valueFilteredList.add(value);
      }
    }
    return valueFilteredList;
  }

  /** do aggregate */
  public KgGraph<IVertexId> map(Collection<KgGraph<IVertexId>> values) {
    // aggregate path value before merge
    Map<String, Map<String, Object>> alias2PropertyMap = new HashMap<>();
    for (Map.Entry<String, List<BaseGroupProcess>> entry :
        this.pathValueAggregatorParsedMap.entrySet()) {
      String alias = entry.getKey();
      alias2PropertyMap.put(alias, propertyVarMap(values, entry.getValue()));
    }

    // Aggregating should according to graph, so we need remove duplicate vertexes and edges
    // according to ID
    KgGraphImpl kgGraph = new KgGraphImpl();
    kgGraph.merge(values, this.kgGraphSchema);

    // use merged kg graph as input
    values = Lists.newArrayList(kgGraph);
    for (Map.Entry<String, List<BaseGroupProcess>> entry :
        this.propertyAggregatorParsedMap.entrySet()) {
      String alias = entry.getKey();
      alias2PropertyMap.put(alias, propertyVarMap(values, entry.getValue()));
    }

    KgGraph<IVertexId> value = values.iterator().next();

    for (Map.Entry<String, Map<String, Object>> entry : alias2PropertyMap.entrySet()) {
      String alias = entry.getKey();
      Map<String, Object> propertyMap = entry.getValue();
      if (vertexAliasSet.contains(alias)) {
        IVertex<IVertexId, IProperty> vertex = value.getVertex(alias).get(0);
        value.aggregateVertex(
            alias, Sets.newHashSet(vertex), this.aggregationSchemaInfo, new HashSet<>());
        aggToVirtualVertexId((KgGraphImpl) value, alias, null);
        value.setVertexProperty(alias, propertyMap, 0L);
      } else {
        IEdge<IVertexId, IProperty> edge = value.getEdge(alias).get(0);
        value.aggregateEdge(
            alias, Sets.newHashSet(edge), this.aggregationSchemaInfo, new HashSet<>());
        aggToVirtualEdgeId((KgGraphImpl) value, alias);
        value.setEdgeProperty(alias, propertyMap);
      }
    }

    for (Map.Entry<String, List<BaseGroupProcess>> entry :
        this.structAggregatorParsedMap.entrySet()) {
      String alias = entry.getKey();
      for (BaseGroupProcess aggInfo : entry.getValue()) {
        Var var = aggInfo.getVar();

        // 进行聚合计算
        UdafMeta udafMeta = aggInfo.getUdafMeta();
        Object[] udafInitParams = aggInfo.getUdfInitParams();
        List<String> ruleList = aggInfo.getRuleList();
        List<KgGraph<IVertexId>> valueFilteredList = getValueFilteredList(values, ruleList);
        Object aggValue = doAggregation(valueFilteredList, udafMeta, udafInitParams, aggInfo);

        // 聚合结果赋值
        if (var instanceof NodeVar) {
          IVertex<IVertexId, IProperty> vertex = (IVertex<IVertexId, IProperty>) aggValue;
          value.aggregateVertex(
              alias, Sets.newHashSet(vertex), this.aggregationSchemaInfo, new HashSet<>());
        } else if (var instanceof EdgeVar || var instanceof PathVar) {
          IEdge<IVertexId, IProperty> edge = (IEdge<IVertexId, IProperty>) aggValue;
          value.aggregateEdge(
              alias, Sets.newHashSet(edge), this.aggregationSchemaInfo, new HashSet<>());
        } else {
          throw new RuntimeException("will never run this code");
        }
      }
    }

    return value;
  }

  private IVertex<IVertexId, IProperty> aggToVirtualVertexId(
      KgGraphImpl value, String vertexAlias, String expectEdgeAlias) {
    Set<IVertex<IVertexId, IProperty>> vertexSet =
        new HashSet<>(value.getAlias2VertexMap().get(vertexAlias));
    IVertex<IVertexId, IProperty> vertex = vertexSet.iterator().next();
    IVertexId newId =
        IVertexId.from(UUID.randomUUID().getMostSignificantBits(), vertex.getId().getType());
    List<Tuple2<String, Boolean>> edgeInfoList =
        aggregationSchemaInfo.getVertexHasEdgeMap().get(vertexAlias);
    for (Tuple2<String, Boolean> edgeInfo : edgeInfoList) {
      String edgeAlias = edgeInfo._1();
      if (edgeAlias.equals(expectEdgeAlias)) {
        continue;
      }
      boolean checkSource = edgeInfo._2();
      Set<IEdge<IVertexId, IProperty>> edgeSet =
          new HashSet<>(value.getAlias2EdgeMap().get(edgeAlias));
      Iterator<IEdge<IVertexId, IProperty>> edgeIt = edgeSet.iterator();

      List<IEdge<IVertexId, IProperty>> newEdgeList = new ArrayList<>();
      while (edgeIt.hasNext()) {
        IEdge<IVertexId, IProperty> edge = edgeIt.next();
        if (checkSource) {
          if (edge.getSourceId().equals(vertex.getId())) {
            edgeIt.remove();
            IEdge<IVertexId, IProperty> newEdge =
                new Edge<>(
                    newId,
                    edge.getTargetId(),
                    edge.getValue(),
                    edge.getVersion(),
                    edge.getDirection(),
                    edge.getType());
            newEdgeList.add(newEdge);
          }
        } else {
          if (edge.getTargetId().equals(vertex.getId())) {
            edgeIt.remove();
            IEdge<IVertexId, IProperty> newEdge =
                new Edge<>(
                    edge.getSourceId(),
                    newId,
                    edge.getValue(),
                    edge.getVersion(),
                    edge.getDirection(),
                    edge.getType());
            newEdgeList.add(newEdge);
          }
        }
      }
      edgeSet.addAll(newEdgeList);
      value.getAlias2EdgeMap().put(edgeAlias, edgeSet);
    }
    IVertex<IVertexId, IProperty> newVertex = vertex.clone();
    newVertex.setId(newId);
    vertexSet.remove(vertex);
    vertexSet.add(newVertex);
    value.getAlias2VertexMap().put(vertexAlias, vertexSet);
    return newVertex;
  }

  private IEdge<IVertexId, IProperty> aggToVirtualEdgeId(KgGraphImpl value, String edgeAlias) {
    Set<IEdge<IVertexId, IProperty>> edgeSet =
        new HashSet<>(value.getAlias2EdgeMap().get(edgeAlias));
    IEdge<IVertexId, IProperty> edge = edgeSet.iterator().next();

    IVertexId sourceId = edge.getSourceId();
    IVertexId targetId = edge.getTargetId();
    List<Tuple2<String, Boolean>> edgeInfoList =
        aggregationSchemaInfo.getEdgeEndpointMap().get(edgeAlias);
    for (Tuple2<String, Boolean> tuple2 : edgeInfoList) {
      String vertexAlias = tuple2._1();
      if (byAliasSet.contains(vertexAlias)) {
        continue;
      }
      IVertex<IVertexId, IProperty> newVertex = aggToVirtualVertexId(value, vertexAlias, edgeAlias);
      boolean checkSource = tuple2._2();
      if (checkSource) {
        sourceId = newVertex.getId();
      } else {
        targetId = newVertex.getId();
      }
    }
    IEdge<IVertexId, IProperty> newEdge = edge.clone();
    newEdge.setSourceId(sourceId);
    newEdge.setTargetId(targetId);
    edgeSet.remove(edge);
    edgeSet.add(newEdge);
    value.getAlias2EdgeMap().put(edgeAlias, edgeSet);
    return newEdge;
  }

  private void updateUdafDataFromProperty(BaseUdaf udaf, IProperty property, String propertyName) {
    if (property.isKeyExist(propertyName)) {
      udaf.update(property.get(propertyName));
    }
  }

  private Object doAggregation(
      List<KgGraph<IVertexId>> valueFilteredList,
      UdafMeta udafMeta,
      Object[] udafInitParams,
      BaseGroupProcess aggInfo) {
    BaseUdaf udaf = udafMeta.createAggregateFunction();
    if (null != udafInitParams) {
      udaf.initialize(udafInitParams);
    }

    String sourceAlias = null;
    String sourcePropertyName = null;
    Set<String> aliasList = aggInfo.getExprUseAliasSet();
    if (aliasList.size() <= 1) {
      Expr sourceExpr = aggInfo.getAggEle();
      // aggregate by vertex subgraph
      if (sourceExpr instanceof Ref) {
        Ref sourceRef = (Ref) sourceExpr;
        sourceAlias = sourceRef.refName();
      } else if (sourceExpr instanceof UnaryOpExpr) {
        UnaryOpExpr expr = (UnaryOpExpr) sourceExpr;
        GetField getField = (GetField) expr.name();
        sourceAlias = ((Ref) expr.arg()).refName();
        sourcePropertyName = getField.fieldName();
      }
      if (!StringUtils.isEmpty(DEBUG_VERTEX_ALIAS)) {
        for (KgGraph<IVertexId> valueFiltered : valueFilteredList) {
          if (valueFiltered.hasFocusVertexId(DEBUG_VERTEX_ALIAS, DEBUG_VERTEX_ID_SET)) {
            StringBuffer sb = new StringBuffer();
            for (KgGraph<IVertexId> valueFiltered2 : valueFilteredList) {
              sb.append(valueFiltered2).append("## ");
            }
            LOGGER.info("DebugKgGraph," + "Aggregate" + "," + sb);
            break;
          }
        }
      }
      String finalSourcePropertyName = sourcePropertyName;
      for (KgGraph<IVertexId> valueFiltered : valueFilteredList) {
        if (valueFiltered.getVertexAlias().contains(sourceAlias)) {
          List<IVertex<IVertexId, IProperty>> vertexList = valueFiltered.getVertex(sourceAlias);
          if (sourcePropertyName == null) {
            vertexList.forEach(udaf::update);
          } else {
            vertexList.forEach(
                v -> updateUdafDataFromProperty(udaf, v.getValue(), finalSourcePropertyName));
          }
        } else {
          List<IEdge<IVertexId, IProperty>> edgeList = valueFiltered.getEdge(sourceAlias);
          if (sourcePropertyName == null) {
            edgeList.forEach(udaf::update);
          } else {
            edgeList.forEach(
                e -> updateUdafDataFromProperty(udaf, e.getValue(), finalSourcePropertyName));
          }
        }
      }
    } else {
      // aggregate by path
      List<String> ruleList = aggInfo.getExprRuleString();
      for (KgGraph<IVertexId> valueFiltered : valueFilteredList) {
        Iterator<KgGraph<IVertexId>> it = valueFiltered.getPath(this.staticParameters, null);
        while (it.hasNext()) {
          KgGraph<IVertexId> path = it.next();
          if (null == path) {
            continue;
          }
          Map<String, Object> context = RunnerUtil.kgGraph2Context(this.initRuleContext, path);
          Object expressionResult =
              RuleRunner.getInstance().executeExpression(context, ruleList, this.taskId);
          udaf.update(expressionResult);
        }
      }
    }

    return udaf.evaluate();
  }
}
