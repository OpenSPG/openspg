/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.kggraph.impl;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;

import java.util.Arrays;

/**
 * @author donghai.ydh
 * @version KgGraphKey.java, v 0.1 2023年07月20日 11:15 donghai.ydh
 */
public class KgGraphKey {
    private final IVertexId[] vertexIds;

    public KgGraphKey(IVertexId[] vertexIds) {
        this.vertexIds = vertexIds;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof KgGraphKey)) {
            return false;
        }
        KgGraphKey other = (KgGraphKey) obj;
        return Arrays.equals(this.vertexIds, other.vertexIds);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vertexIds);
    }

}