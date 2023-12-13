/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.kggraph;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;


public interface IVertexId2WorkerIndex {
    int workerIndex(IVertexId vertexId);
}