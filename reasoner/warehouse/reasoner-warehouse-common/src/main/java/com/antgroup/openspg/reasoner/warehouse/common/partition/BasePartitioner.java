/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.warehouse.common.partition;

import com.antgroup.openspg.reasoner.common.exception.NotImplementedException;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;

import java.io.Serializable;


public class BasePartitioner implements Serializable {
    protected final int parallel;

    protected BasePartitioner(int parallel) {
        this.parallel = parallel;
    }

    /**
     * partition
     */
    public int partition(Object obj) {
        Object partitionKey = getShuffleKey(obj);
        return defaultShuffleMode(partitionKey, this.parallel);
    }

    public Boolean canPartition(Object obj) {
        return true;
    }

    private int defaultShuffleMode(Object obj, int parallel) {
        return Math.abs(obj.hashCode()) % parallel;
    }

    private Object getShuffleKey(Object obj) {
        if (obj instanceof IVertex) {
            IVertex vertex = (IVertex) obj;
            return vertex.getId();
        } else if (obj instanceof IEdge) {
            IEdge edge = (IEdge) obj;
            return edge.getSourceId();
        } else if (obj instanceof IVertexId) {
            return obj;
        } else if (obj instanceof Integer) {
            return obj;
        } else if (obj instanceof String) {
            return obj;
        } else {
            throw new NotImplementedException("can not shuffle this type " + obj.getClass().getName(), null);
        }
    }

}