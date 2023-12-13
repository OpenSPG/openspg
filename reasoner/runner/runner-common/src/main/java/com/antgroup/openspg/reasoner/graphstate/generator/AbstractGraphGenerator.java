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
package com.antgroup.openspg.reasoner.graphstate.generator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.IVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.common.utils.PropertyUtil;
import com.antgroup.openspg.reasoner.parser.DemoGraphParser;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;
import scala.collection.JavaConversions;



public abstract class AbstractGraphGenerator implements Serializable {
    private static final long serialVersionUID = -5588043366117487019L;
    protected IVertex<String, IProperty> constructionVertex(String bizId, String type, Object... kvs) {
        int kvsLen = kvs.length;
        Object[] property = Arrays.copyOf(kvs, kvsLen + 2);
        property[kvsLen] = Constants.CONTEXT_LABEL;
        property[kvsLen + 1] = type;
        IVertexId vertexId = IVertexId.from(bizId, type);
        IVersionProperty versionProperty = PropertyUtil.buildVertexProperty(vertexId, convert2VersionProperty(bizId, 0, property));
        return new Vertex<>(bizId, versionProperty);
    }

    protected IVertex<String, IProperty> constructionVersionVertex(String bizId, String type, long version, Object... kvs) {
        int kvsLen = kvs.length;
        Object[] property = Arrays.copyOf(kvs, kvsLen + 2);
        property[kvsLen] = Constants.CONTEXT_LABEL;
        property[kvsLen + 1] = type;
        IVertexId vertexId = IVertexId.from(bizId, type);
        IVersionProperty versionProperty = PropertyUtil.buildVertexProperty(vertexId, convert2VersionProperty(bizId, version, property));
        return new Vertex<>(bizId, versionProperty);
    }

    protected IEdge<String, IProperty> constructionEdge(String s, String p, String o, Object... kvs) {
        Map<String, Object> rowPropertyMap = convert2Property(kvs);
        rowPropertyMap.put(Constants.EDGE_FROM_ID_KEY, s);
        rowPropertyMap.put(Constants.EDGE_TO_ID_KEY, o);
        IProperty edgeProperty = PropertyUtil.buildEdgeProperty(p, rowPropertyMap);
        return new Edge<>(s, o, edgeProperty, 0, Direction.OUT, p);
    }

    protected IEdge<String, IProperty> constructionVersionEdge(String s, String p, String o, long t, Object... kvs) {
        Map<String, Object> rowPropertyMap = convert2Property(kvs);
        rowPropertyMap.put(Constants.EDGE_FROM_ID_KEY, s);
        rowPropertyMap.put(Constants.EDGE_TO_ID_KEY, o);
        IProperty edgeProperty = PropertyUtil.buildEdgeProperty(p, rowPropertyMap);
        return new Edge<>(s, o, edgeProperty, t, Direction.OUT, p);
    }

    protected Map<String, TreeMap<Long, Object>> convert2VersionProperty(String bizId, long version, Object... kvs) {
        Map<String, Object> rowPropertyMap = convert2Property(kvs);
        rowPropertyMap.put(Constants.NODE_ID_KEY, bizId);
        Map<String, TreeMap<Long, Object>> result = new HashMap<>();
        for (String key : rowPropertyMap.keySet()) {
            Map<Long, Object> versionPropertyMap = result.computeIfAbsent(key, k -> new TreeMap<>());
            versionPropertyMap.put(version, rowPropertyMap.get(key));
        }
        return result;
    }

    protected Map<String, Object> convert2Property(Object... kvs) {
        Preconditions.checkArgument(kvs.length % 2 == 0, "The number of config kv should be even.");
        Map<String, Object> property = new HashMap<>();
        for (int i = 0; i < kvs.length; i = i + 2) {
            property.put(String.valueOf(kvs[i]), kvs[i + 1]);
        }
        return property;
    }

    /**
     * get vertex list than can write to graph state
     */
    public List<IVertex<IVertexId, IProperty>> getVertexList() {
        return getGraphData()._1();
    }

    /**
     * get edge list that already aggregated by source id
     */
    public Map<IVertexId, Tuple2<List<IEdge<IVertexId, IProperty>>, List<IEdge<IVertexId, IProperty>>>> getEdgeAggregated() {
        Map<IVertexId, Tuple2<List<IEdge<IVertexId, IProperty>>, List<IEdge<IVertexId, IProperty>>>> edgeAggregatedMap = new HashMap<>();
        List<IEdge<IVertexId, IProperty>> allEdge = getGraphData()._2();
        for (IEdge<IVertexId, IProperty> edge : allEdge) {
            Tuple2<List<IEdge<IVertexId, IProperty>>, List<IEdge<IVertexId, IProperty>>> inOutEdgeList =
                    edgeAggregatedMap.computeIfAbsent(edge.getSourceId(), k -> new Tuple2<>(new ArrayList<>(), new ArrayList<>()));
            if (Direction.OUT.equals(edge.getDirection())) {
                inOutEdgeList._2().add(edge);
            } else if (Direction.IN.equals(edge.getDirection())) {
                inOutEdgeList._1().add(edge);
            }
        }
        return edgeAggregatedMap;
    }

    protected Tuple2<List<IVertex<IVertexId, IProperty>>, List<IEdge<IVertexId, IProperty>>> generateGraphData(List<IVertex<String, IProperty>> vertexList, List<IEdge<String, IProperty>> edgeList) {
        Map<String, IVertex<String, IProperty>> vertexMap = new HashMap<>();
        vertexList.forEach(vertex -> vertexMap.put(vertex.getId() + "_" + RunnerUtil.getVertexTypeFromProperty(vertex.getValue()), vertex));
        vertexList.forEach(vertex -> vertexMap.put(vertex.getId(), vertex));

        List<IEdge<IVertexId, IProperty>> edgeListWithReasonerId = edgeList.stream().map(
                (Function<IEdge<String, IProperty>, IEdge<IVertexId, IProperty>>) edge -> {
                    IVertex<String, IProperty> source = vertexMap.get(edge.getSourceId());
                    if (null == source) {
                        throw new RuntimeException("source vertex " + edge.getSourceId() + " does not exists");
                    }

                    IVertex<String, IProperty> target = vertexMap.get(edge.getTargetId());
                    if (null == target) {
                        throw new RuntimeException("target vertex " + edge.getTargetId() + " does not exists");
                    }
                    String sourceType = RunnerUtil.getVertexTypeFromProperty(source.getValue());
                    String targetType = RunnerUtil.getVertexTypeFromProperty(target.getValue());
                    return new Edge<>(IVertexId.from(source.getId(), sourceType),
                            IVertexId.from(target.getId(), targetType),
                            edge.getValue(), edge.getVersion(), edge.getDirection(),
                            sourceType + "_" + edge.getType() + "_" + targetType);
                }).collect(Collectors.toList());

        Tuple2<List<IVertex<IVertexId, IProperty>>, List<IEdge<IVertexId, IProperty>>> result =
                new Tuple2<>(new ArrayList<>(), new ArrayList<>());

        edgeListWithReasonerId.forEach(edge -> {
            result._2().add(edge);
        });

        edgeListWithReasonerId.stream().map(this::revertEdge).forEach(edge -> {
            result._2().add(edge);
        });

        vertexList
                .stream()
                .map((Function<IVertex<String, IProperty>, IVertex<IVertexId, IProperty>>) v -> {
                    IVertexId id = IVertexId.from(v.getId(), RunnerUtil.getVertexTypeFromProperty(v.getValue()));
                    IProperty value = v.getValue();
                    value.remove(Constants.CONTEXT_LABEL);
                    return new Vertex<>(id, value);
                })
                .forEach(v -> result._1().add(v));
        return result;
    }

    /**
     * get demo graph with txt
     * @param demoGraph
     * @return
     */
    public Tuple2<List<IVertex<IVertexId, IProperty>>, List<IEdge<IVertexId, IProperty>>> getGraphData(String demoGraph) {
        DemoGraphParser parser = new DemoGraphParser();
        Tuple2<scala.collection.immutable.List<Vertex<String, IProperty>>,
                scala.collection.immutable.List<Edge<String, IProperty>>> data = parser.parse(demoGraph);
        return generateGraphData(Lists.newArrayList(JavaConversions.asJavaCollection(data._1)),
                Lists.newArrayList(JavaConversions.asJavaCollection(data._2)));
    }
    /**
     * get graph data
     */
    public Tuple2<List<IVertex<IVertexId, IProperty>>, List<IEdge<IVertexId, IProperty>>> getGraphData() {
        String demoGraph = getDemoGraph();
        if (StringUtils.isNotBlank(demoGraph)) {
            return getGraphData(demoGraph);
        }
        List<IVertex<String, IProperty>> vertexList = this.genVertexList();
        List<IEdge<String, IProperty>> edgeList = this.genEdgeList();

        return generateGraphData(vertexList, edgeList);
    }

    private IEdge<IVertexId, IProperty> revertEdge(IEdge<IVertexId, IProperty> edge) {
        return new Edge<>(edge.getTargetId(), edge.getSourceId(), edge.getValue(), edge.getVersion(),
                Direction.OUT == edge.getDirection() ? Direction.IN : Direction.OUT,
                edge.getType());
    }

    /**
     * use demo graph to parse data
     * @return
     */
    public String getDemoGraph() {
        return "";
    }
    /**
     * please provide your mock vertex list
     */
    public abstract List<IVertex<String, IProperty>> genVertexList();

    /**
     * please provide your mock edge list
     */
    public abstract List<IEdge<String, IProperty>> genEdgeList();
}