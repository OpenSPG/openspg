/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.kggraph;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;

/**
 * @author donghai.ydh
 * @version IVertexId2WorkerIndex.java, v 0.1 2023年04月28日 17:35 donghai.ydh
 */
public interface IVertexId2WorkerIndex {
    int workerIndex(IVertexId vertexId);
}