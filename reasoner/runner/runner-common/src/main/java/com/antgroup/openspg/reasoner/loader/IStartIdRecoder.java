/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.loader;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;

/**
 * @author donghai.ydh
 * @version IStartIdRecoder.java, v 0.1 2023年03月29日 17:41 donghai.ydh
 */
public interface IStartIdRecoder {


    void addStartId(IVertexId id);
    void flush();
    long getStartIdCount();
}