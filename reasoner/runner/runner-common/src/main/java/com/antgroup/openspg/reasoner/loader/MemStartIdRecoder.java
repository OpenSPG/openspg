/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.loader;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author donghai.ydh
 * @version MemStartIdRecoder.java, v 0.1 2023年03月29日 17:48 donghai.ydh
 */
public class MemStartIdRecoder extends BaseStartIdRecoder {
    // use TreeSet, sort vertex id
    protected Set<IVertexId> startIdSet = new TreeSet<>();

    protected Iterator<IVertexId> internalIt = null;

    @Override
    public void addStartId(IVertexId id) {
        startIdSet.add(id);
    }

    @Override
    public void flush() {
        this.internalIt = this.startIdSet.iterator();
    }

    @Override
    public long getStartIdCount() {
        return startIdSet.size();
    }

    @Override
    public boolean hasNext() {
        return this.internalIt.hasNext();
    }

    @Override
    public IVertexId next() {
        IVertexId id = this.internalIt.next();
        this.internalIt.remove();
        return id;
    }
}