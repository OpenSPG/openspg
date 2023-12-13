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

/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rdg.common;

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
import com.antgroup.openspg.reasoner.lube.logical.PropertyVar;
import com.antgroup.openspg.reasoner.lube.logical.Var;
import com.antgroup.openspg.reasoner.rdg.common.groupProcess.AggIfOpProcessBaseGroupProcess;
import com.antgroup.openspg.reasoner.rdg.common.groupProcess.AggOpProcessBaseGroupProcess;
import com.antgroup.openspg.reasoner.rdg.common.groupProcess.BaseGroupProcess;
import com.antgroup.openspg.reasoner.udf.model.BaseUdaf;
import com.antgroup.openspg.reasoner.udf.model.UdafMeta;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import scala.Tuple2;

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


public class KgGraphAggregateImpl implements Serializable {

private static final long serialVersionUID = -4440174716170402565L;
    private final Pattern                      kgGraphSchema;
    private final KgGraphSplitStaticParameters staticParameters;
    private final Set<String>                  vertexAliasSet;
    private final Map<Var, Aggregator>         aggregatorMap;
    private final Long                         maxPathLimit;
    private final String                       taskId;
    private final Set<String>                  byAliasSet;

    private final AggregationSchemaInfo aggregationSchemaInfo;

    // property aggregator 优先处理
    private transient Map<String, List<BaseGroupProcess>> propertyAggregatorParsedMap;

    // first聚合后处理
    private transient Map<String, List<BaseGroupProcess>> aggregatorParsedMap;

    /**
     * aggregate implement
     */
    public KgGraphAggregateImpl(String taskId, String rootAlias, List<String> byAliasList, Pattern kgGraphSchema,
                                Map<Var, Aggregator> aggregatorMap,
                                Long maxPathLimit) {
        this.kgGraphSchema = kgGraphSchema;
        this.aggregationSchemaInfo = new AggregationSchemaInfo(this.kgGraphSchema);
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
        throw new NotImplementedException("unsupported aggregator, type=" + aggOpExpr.getClass().getName(), null);
    }

    /**
     * init implement
     */
    public void init() {
        this.aggregatorParsedMap = new HashMap<>();
        this.propertyAggregatorParsedMap = new HashMap<>();
        for (Map.Entry<Var, Aggregator> entry : this.aggregatorMap.entrySet()) {
            Var var = entry.getKey();
            String aliasKey = var.name();
            Aggregator aggregator = entry.getValue();

            BaseGroupProcess aggOpProcess = getGroupProcess(var, aggregator);
            List<BaseGroupProcess> list;
            if (aggOpProcess.isFirstAgg()) {
                list = this.aggregatorParsedMap.computeIfAbsent(aliasKey, k -> new ArrayList<>());
            } else {
                list = this.propertyAggregatorParsedMap.computeIfAbsent(aliasKey, k -> new ArrayList<>());
            }
            list.add(aggOpProcess);
        }
    }

    /**
     * process property var
     */
    public Map<String, Object> propertyVarMap(Collection<KgGraph<IVertexId>> values,
                                              List<BaseGroupProcess> aggInfoList) {

        Map<String, Object> propertyMap = new HashMap<>();
        for (BaseGroupProcess aggInfo : aggInfoList) {
            PropertyVar var = (PropertyVar) aggInfo.getVar();

            // 进行聚合计算
            UdafMeta udafMeta = aggInfo.getUdafMeta();
            Object[] udafInitParams = aggInfo.getUdfInitParams();
            List<String> ruleList = aggInfo.getRuleList();
            List<KgGraph<IVertexId>> valueFilteredList = getValueFilteredList(values, ruleList);
            Object aggValue = doAggregation(valueFilteredList, udafMeta, udafInitParams, aggInfo.getAggEle());
            String targetPropertyName = var.field().name();
            propertyMap.put(targetPropertyName, aggValue);
        }
        return propertyMap;
    }

    private List<KgGraph<IVertexId>> getValueFilteredList(Collection<KgGraph<IVertexId>> values, List<String> ruleList) {
        List<KgGraph<IVertexId>> valueFilteredList = new ArrayList<>();
        for (KgGraph<IVertexId> value : values) {
            if (CollectionUtils.isNotEmpty(ruleList)) {
                KgGraph<IVertexId> kgGraph = RunnerUtil.filterKgGraph(value, kgGraphSchema, staticParameters, ruleList, maxPathLimit);
                if (null != kgGraph) {
                    valueFilteredList.add(kgGraph);
                }
            } else {
                valueFilteredList.add(value);
            }
        }
        return valueFilteredList;
    }

    /**
     * do aggregate
     */
    public KgGraph<IVertexId> map(Collection<KgGraph<IVertexId>> values) {
        // Aggregating should according to graph, so we need remove duplicate vertexes and edges according to ID
        KgGraphImpl kgGraph = new KgGraphImpl();
        kgGraph.merge(values, this.kgGraphSchema);

        // use merged kg graph as input
        values = Lists.newArrayList(kgGraph);
        Map<String, Map<String, Object>> alias2PropertyMap = new HashMap<>();
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
                value.aggregateVertex(alias, Sets.newHashSet(vertex), this.aggregationSchemaInfo, new HashSet<>());
                aggToVirtualVertexId((KgGraphImpl) value, alias, null);
                value.setVertexProperty(alias, propertyMap, 0L);
            } else {
                IEdge<IVertexId, IProperty> edge = value.getEdge(alias).get(0);
                value.aggregateEdge(alias, Sets.newHashSet(edge), this.aggregationSchemaInfo, new HashSet<>());
                aggToVirtualEdgeId((KgGraphImpl) value, alias);
                value.setEdgeProperty(alias, propertyMap);
            }
        }

        for (Map.Entry<String, List<BaseGroupProcess>> entry :
                this.aggregatorParsedMap.entrySet()) {
            String alias = entry.getKey();
            for (BaseGroupProcess aggInfo : entry.getValue()) {
                Var var = aggInfo.getVar();

                // 进行聚合计算
                UdafMeta udafMeta = aggInfo.getUdafMeta();
                Object[] udafInitParams = aggInfo.getUdfInitParams();
                List<String> ruleList = aggInfo.getRuleList();
                List<KgGraph<IVertexId>> valueFilteredList = getValueFilteredList(values, ruleList);
                Object aggValue = doAggregation(valueFilteredList, udafMeta, udafInitParams, aggInfo.getAggEle());

                // 聚合结果赋值
                if (var instanceof NodeVar) {
                    IVertex<IVertexId, IProperty> vertex = (IVertex<IVertexId, IProperty>) aggValue;
                    value.aggregateVertex(alias, Sets.newHashSet(vertex), this.aggregationSchemaInfo, new HashSet<>());
                } else if (var instanceof EdgeVar) {
                    IEdge<IVertexId, IProperty> edge = (IEdge<IVertexId, IProperty>) aggValue;
                    value.aggregateEdge(alias, Sets.newHashSet(edge), this.aggregationSchemaInfo, new HashSet<>());
                } else {
                    throw new RuntimeException("will never run this code");
                }
            }
        }

        return value;
    }

    private IVertex<IVertexId, IProperty> aggToVirtualVertexId(KgGraphImpl value, String vertexAlias, String expectEdgeAlias) {
        Set<IVertex<IVertexId, IProperty>> vertexSet = value.getAlias2VertexMap().get(vertexAlias);
        IVertex<IVertexId, IProperty> vertex = vertexSet.iterator().next();
        IVertexId newId = IVertexId.from(UUID.randomUUID().getMostSignificantBits(), vertex.getId().getType());
        List<Tuple2<String, Boolean>> edgeInfoList = aggregationSchemaInfo.getVertexHasEdgeMap().get(vertexAlias);
        for (Tuple2<String, Boolean> edgeInfo : edgeInfoList) {
            String edgeAlias = edgeInfo._1();
            if (edgeAlias.equals(expectEdgeAlias)) {
                continue;
            }
            boolean checkSource = edgeInfo._2();
            Set<IEdge<IVertexId, IProperty>> edgeSet = value.getAlias2EdgeMap().get(edgeAlias);
            Iterator<IEdge<IVertexId, IProperty>> edgeIt = edgeSet.iterator();

            List<IEdge<IVertexId, IProperty>> newEdgeList = new ArrayList<>();
            while (edgeIt.hasNext()) {
                IEdge<IVertexId, IProperty> edge = edgeIt.next();
                if (checkSource) {
                    if (edge.getSourceId().equals(vertex.getId())) {
                        edgeIt.remove();
                        IEdge<IVertexId, IProperty> newEdge = new Edge<>(newId, edge.getTargetId(), edge.getValue(), edge.getVersion(),
                                edge.getDirection(), edge.getType());
                        newEdgeList.add(newEdge);
                    }
                } else {
                    if (edge.getTargetId().equals(vertex.getId())) {
                        edgeIt.remove();
                        IEdge<IVertexId, IProperty> newEdge = new Edge<>(edge.getSourceId(), newId, edge.getValue(), edge.getVersion(),
                                edge.getDirection(), edge.getType());
                        newEdgeList.add(newEdge);
                    }
                }
            }
            edgeSet.addAll(newEdgeList);
        }
        IVertex<IVertexId, IProperty> newVertex = vertex.clone();
        newVertex.setId(newId);
        vertexSet.remove(vertex);
        vertexSet.add(newVertex);
        return newVertex;
    }

    private IEdge<IVertexId, IProperty> aggToVirtualEdgeId(KgGraphImpl value, String edgeAlias) {
        Set<IEdge<IVertexId, IProperty>> edgeSet = value.getAlias2EdgeMap().get(edgeAlias);
        IEdge<IVertexId, IProperty> edge = edgeSet.iterator().next();

        IVertexId sourceId = edge.getSourceId();
        IVertexId targetId = edge.getTargetId();
        List<Tuple2<String, Boolean>> edgeInfoList = aggregationSchemaInfo.getEdgeEndpointMap().get(edgeAlias);
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
        return newEdge;
    }

    private void updateUdafDataFromProperty(BaseUdaf udaf, IProperty property, String propertyName) {
        if (property.isKeyExist(propertyName)) {
            udaf.update(property.get(propertyName));
        }
    }

    private Object doAggregation(List<KgGraph<IVertexId>> valueFilteredList,
                                 UdafMeta udafMeta,
                                 Object[] udafInitParams,
                                 Expr sourceExpr) {
        BaseUdaf udaf = udafMeta.createAggregateFunction();
        if (null != udafInitParams) {
            udaf.initialize(udafInitParams);
        }

        String sourceAlias = null;
        String sourcePropertyName = null;
        if (sourceExpr instanceof Ref) {
            Ref sourceRef = (Ref) sourceExpr;
            sourceAlias = sourceRef.refName();
        } else if (sourceExpr instanceof UnaryOpExpr) {
            UnaryOpExpr expr = (UnaryOpExpr) sourceExpr;
            GetField getField = (GetField) expr.name();
            sourceAlias = ((Ref) expr.arg()).refName();
            sourcePropertyName = getField.fieldName();
        }

        String finalSourcePropertyName = sourcePropertyName;
        for (KgGraph<IVertexId> valueFiltered : valueFilteredList) {
            if (valueFiltered.getVertexAlias().contains(sourceAlias)) {
                List<IVertex<IVertexId, IProperty>> vertexList = valueFiltered.getVertex(sourceAlias);
                if (sourcePropertyName == null) {
                    vertexList.forEach(udaf::update);
                } else {
                    vertexList.forEach(v -> updateUdafDataFromProperty(udaf, v.getValue(),
                            finalSourcePropertyName));
                }
            } else {
                List<IEdge<IVertexId, IProperty>> edgeList = valueFiltered.getEdge(sourceAlias);
                if (sourcePropertyName == null) {
                    edgeList.forEach(udaf::update);
                } else {
                    edgeList.forEach(e -> updateUdafDataFromProperty(udaf, e.getValue(),
                            finalSourcePropertyName));
                }
            }
        }

        return udaf.evaluate();
    }
}