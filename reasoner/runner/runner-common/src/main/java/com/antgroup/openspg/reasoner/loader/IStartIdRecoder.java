/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.loader;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;


public interface IStartIdRecoder {


    void addStartId(IVertexId id);
    void flush();
    long getStartIdCount();
}