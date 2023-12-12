/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.warehouse.utils;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;

import java.util.HashSet;
import java.util.Set;


public class DebugVertexIdSet {
    /**
     * vertex alias
     */
    public static volatile String DEBUG_VERTEX_ALIAS = null;

    /**
     * vertex id set
     */
    public static volatile Set<IVertexId> DEBUG_VERTEX_ID_SET = new HashSet<>();
}