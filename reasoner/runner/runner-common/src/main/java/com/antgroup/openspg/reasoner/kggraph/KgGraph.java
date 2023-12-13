/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.kggraph;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphSplitStaticParameters;
import com.antgroup.openspg.reasoner.lube.block.SortItem;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Data structure that transmits messages between workers
 *
 * @author kejian
 * @version KgGraph.java, v 0.1 2023年02月14日 7:45 PM kejian
 */
public interface KgGraph<K> {

    /**
     * Initialize the tree structure of KgGraph based on the root vertex and adjacent edges
     *
     * @param root          the root of vertex subgraph
     * @param alias2EdgeMap the adjacent edges of root in vertex subgraph
     * @param schema        describe the structure of vertex subgraph
     *                      Guarantee that the source/target/direction in the schema
     *                      are consistent with the source/target/direction of the actual edge
     */
    void init(IVertex<K, IProperty> root, Map<String, List<IEdge<K, IProperty>>> alias2EdgeMap, Pattern schema);

    /**
     * Split out the KgGraphs centered on the actual vertex of the alias from the current KgGraph
     *
     * @param vertexAliases
     * @param schema,       the structure of the converted subgraph
     * @param filter,       Filter function on the split KgGraph, return true will be included
     * @param limit,        max result limit, null means no constraints are applied
     * @return
     */
    List<KgGraph<K>> split(Set<String> vertexAliases, Pattern schema, KgGraphSplitStaticParameters staticParameters,
                           Predicate<KgGraph<K>> filter, Long limit);

    /**
     * Split out the KgGraphs centered on worker index
     *
     * @param vertexAlias          target vertex alias
     * @param vertexId2WorkerIndex function, convert vertex id to worker index
     * @param schema
     * @param filter
     * @param limit                max result limit, null means no constraints are applied
     * @return
     */
    Map<Integer, KgGraph<K>> splitToWorkerIndex(String vertexAlias, IVertexId2WorkerIndex vertexId2WorkerIndex,
                                                Pattern schema, KgGraphSplitStaticParameters staticParameters,
                                                Predicate<KgGraph<K>> filter, Long limit);

    /**
     * Merge the received messages into the current KgGraph
     *
     * @param msgs
     * @param schema the structure of the merged subgraph
     */
    void merge(Collection<KgGraph<K>> msgs, Pattern schema);

    /**
     * Add the new KgGraph to the current KgGraph, and the schema of the KgGraph changes
     *
     * @param subGraph
     * @param schema   the structure of the expanded subgraph
     */
    void expand(KgGraph<K> subGraph, Pattern schema);

    /**
     * expend KgGraph and prune by intersection vertex
     *
     * @return valid or not
     */
    int expandAndPrune(Pattern thisSchema, KgGraph<IVertexId> matchedKgGraph, Pattern matchedSchema,
                       Pattern finalSchema, Set<String> intersectionVertexAliasSet);

    /**
     * Get the path from KgGraph, flat all alias vertexes
     * Iterator may return null, which needs to be checked when call it.
     *
     * @param staticParameters
     * @param filter,          Filter function on path
     * @return
     */
    Iterator<KgGraph<K>> getPath(KgGraphSplitStaticParameters staticParameters, Predicate<KgGraph<K>> filter);

    /**
     * aggregate by edge
     */
    void aggregateEdge(String edgeAlias, Set<IEdge<IVertexId, IProperty>> edgeSet, AggregationSchemaInfo aggregationSchemaInfo,
                       Set<String> aggregatedAliasSet);

    /**
     * aggregate by vertex
     */
    void aggregateVertex(String vertexAlias, Set<IVertex<K, IProperty>> vertexSet, AggregationSchemaInfo aggregationSchemaInfo,
                         Set<String> aggregatedAliasSet);

    /**
     * Sort the current KgGraph by sortItems
     *
     * @param sortItems
     * @param limit
     * @param schema
     */
    void executeSort(List<SortItem> sortItems, int limit, Pattern schema);

    /**
     * Adjust the current KgGraph according to scheme including structure and property
     *
     * @param schema
     */
    void adjustGraph(Pattern schema);

    /**
     * Get vertex from KgGraph based on vertex alias
     *
     * @param alias
     * @return
     */
    List<IVertex<K, IProperty>> getVertex(String alias);

    /**
     * Get edge from KgGraph based on edge alias
     *
     * @param alias
     * @return
     */
    List<IEdge<K, IProperty>> getEdge(String alias);

    /**
     * get vertex alias set
     *
     * @return
     */
    Set<String> getVertexAlias();

    /**
     * get edge alias set
     *
     * @return
     */
    Set<String> getEdgeAlias();

    /**
     * Show kgGraph structure
     */
    void show();

    /**
     * set vertex property
     *
     * @param alias       - vertex alias
     * @param propertyMap - key is new property name, value is new property value
     * @param version     - vertex version, if null add properties to the 0 version
     */
    void setVertexProperty(String alias, Map<String, Object> propertyMap, Long version);

    /**
     * set edge property
     *
     * @param alias       - edge alias
     * @param propertyMap - key is new property name, value is new property value
     */
    void setEdgeProperty(String alias, Map<String, Object> propertyMap);

    /**
     * clone vertex set
     */
    void cloneVertexSet(String alias);

    /**
     * clone edge set
     */
    void cloneEdgeSet(String alias);

    /**
     * check KgGraph have focus vertex id
     */
    boolean hasFocusVertexId(String alias, Set<IVertexId> focusVertexIdSet);

    /**
     * check duplicate vertex
     */
    boolean checkDuplicateVertex();
}