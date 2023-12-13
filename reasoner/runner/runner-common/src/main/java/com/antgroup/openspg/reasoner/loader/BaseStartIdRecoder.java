/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.loader;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;


public abstract class BaseStartIdRecoder implements Iterator<IVertexId> {
    private static final Map<String, BaseStartIdRecoder> START_ID_MAP = new ConcurrentHashMap<>();

    /**
     * base recoder
     */
    public static BaseStartIdRecoder get(String contextId, int index, boolean init, boolean mem) {
        if (init) {
            BaseStartIdRecoder recoder;
            if (mem) {
                recoder = new MemStartIdRecoder();
            } else {
                recoder = new DiskStartIdRecorder(getKey(contextId, index));
            }
            START_ID_MAP.put(getKey(contextId, index), recoder);
        }
        return START_ID_MAP.get(getKey(contextId, index));
    }

    /**
     * get start id recoder
     */
    public static BaseStartIdRecoder get(String contextId, int index) {
        return START_ID_MAP.get(getKey(contextId, index));
    }

    /**
     * remove recoder
     */
    public static void remove(String contextId, int index) {
        START_ID_MAP.remove(getKey(contextId, index));
    }

    private static String getKey(String contextId, int index) {
        return contextId + index;
    }

    public abstract void addStartId(IVertexId id);

    public abstract void flush();

    public abstract long getStartIdCount();
}