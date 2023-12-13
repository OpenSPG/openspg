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
package com.antgroup.openspg.reasoner.graphstate;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.graphstate.model.MergeTypeEnum;
import com.antgroup.openspg.reasoner.lube.common.rule.Rule;


public interface GraphState<K> {

    /**
     * init parameters
     *
     * @param param
     */
    void init(Map<String, String> param);

    /**
     * add vertex with all version properties to graph state
     *
     * @param vertex
     */
    void addVertex(IVertex<K, IProperty> vertex);

    /**
     * with version
     *
     * @param vertex
     */
    void addVertex(IVertex<K, IProperty> vertex, Long version);

    /**
     * merge properties of vertex
     * just write, good performance
     *
     * @param id
     * @param property
     * @param mergeType
     * @param version
     */
    void mergeVertexProperty(K id, Map<String, Object> property, MergeTypeEnum mergeType, Long version);

    /**
     * set the properties of the vertex to be cached
     * if not set, all properties are cached by default
     *
     * @param properties, key is vertex type, value is the properties need to be cached
     */
    void setVertexCacheProperty(Map<String, Set<String>> properties);

    /**
     * get vertex with the specific version properties
     *
     * @param id
     * @param version if null return vertex with the all version properties
     * @return
     */
    IVertex<K, IProperty> getVertex(K id, Long version);

    /**
     * get vertex with the specific version properties, with filter rule
     *
     * @param id
     * @param version
     * @param rule
     * @return
     */
    IVertex<K, IProperty> getVertex(K id, Long version, Rule rule);

    /**
     * add inEdges and outEdges of a vertex
     *
     * @param vertexId
     * @param inEdges
     * @param outEdges
     */
    void addEdges(K vertexId, List<IEdge<K, IProperty>> inEdges, List<IEdge<K, IProperty>> outEdges);

    /**
     * update specific edge properties
     * edges are redundantly stored and only one edge has been updated
     * need to call (o, p, s, t) to update the other edge
     *
     * @param s
     * @param p
     * @param o
     * @param version
     * @param property
     */
    void updateEdgeProperty(K s, String p, K o, Long version, IProperty property);

    /**
     * merge specific edge properties
     * edges are redundantly stored and only one edge has been updated
     * need to call (o, p, s, t) to update the other edge
     *
     * @param s
     * @param p
     * @param o
     * @param version   should not been null
     * @param property
     * @param mergeType
     */
    void mergeEdgeProperty(K s, String p, K o, Long version, Direction direction, Map<String, Object> property, MergeTypeEnum mergeType);

    /**
     * set the properties of the edge to be cached
     * if not set, all properties are cached by default
     *
     * @param properties, key is edge type, value is the properties need to be cached
     */
    void setEdgeCacheProperty(Map<String, Set<String>> properties);

    /**
     * get the edge without property of the specific type of vertex
     *
     * @param vertexId
     * @param startVersion if null, get default version edge of the vertex
     * @param endVersion   if null, get default version edge of the vertex
     * @param types        if null, get all type edges of the vertex
     * @param direction
     * @return key is edge type, value is direct_o_version
     */
    List<IEdge<K, IProperty>> getEdgesWithoutProperty(K vertexId, Long startVersion, Long endVersion, Set<String> types,
                                                      Direction direction);

    /**
     * get specific types edges of vertex
     *
     * @param vertexId
     * @param startVersion if null, get default version edge of the vertex
     * @param endVersion   if null, get default version edge of the vertex
     * @param types        not empty
     * @param direction
     * @return
     */
    List<IEdge<K, IProperty>> getEdges(K vertexId, Long startVersion, Long endVersion, Set<String> types, Direction direction);

    /**
     * get specific types edges with filter rule
     *
     * @param vertexId
     * @param startVersion
     * @param endVersion
     * @param direction
     * @param typeAndRuleMap, There is OR relationship between List<Rule>
     * @return
     */
    List<IEdge<K, IProperty>> getEdges(K vertexId, Long startVersion, Long endVersion, Set<String> types, Direction direction,
                                       Map<String, List<Rule>> typeAndRuleMap);

    /**
     * get specific type vertex iterator
     *
     * @param vertexType
     * @return
     */
    Iterator<IVertex<K, IProperty>> getVertexIterator(Set<String> vertexType);

    /**
     * get vertex iterator with user-defined filter
     *
     * @param filter
     * @return
     */
    Iterator<IVertex<K, IProperty>> getVertexIterator(Predicate<IVertex<K, IProperty>> filter);

    /**
     * get specific type edge iterator
     *
     * @param edgeType
     * @return
     */
    Iterator<IEdge<K, IProperty>> getEdgeIterator(Set<String> edgeType);

    /**
     * get edge iterator with user-defined filter
     *
     * @param filter
     * @return
     */
    Iterator<IEdge<K, IProperty>> getEdgeIterator(Predicate<IEdge<K, IProperty>> filter);

    /**
     * checkPoint
     */
    void checkPoint();

    /**
     * close
     */
    void close();
}