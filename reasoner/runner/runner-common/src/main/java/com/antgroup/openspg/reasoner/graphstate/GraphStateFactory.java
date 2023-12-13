/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.graphstate;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class GraphStateFactory {
    private static final Map<Integer, GraphState<IVertexId>> GRAPH_STATE_MAP = new ConcurrentHashMap<>();

    /**
     * return graph state on worker
     */
    public static GraphState<IVertexId> getGraphState(int index) {
        return GRAPH_STATE_MAP.get(index);
    }

    /**
     * init graph state
     */
    public static void putGraphState(int index, GraphState<IVertexId> graphState) {
        GRAPH_STATE_MAP.put(index, graphState);
    }
}