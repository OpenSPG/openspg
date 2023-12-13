/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rdg.common;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.lube.logical.EdgeVar;
import com.antgroup.openspg.reasoner.lube.logical.NodeVar;
import com.antgroup.openspg.reasoner.lube.logical.Var;
import scala.collection.JavaConversions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author donghai.ydh
 * @version KgGraphRenameImpl.java, v 0.1 2023年10月31日 20:15 donghai.ydh
 */
public class KgGraphRenameImpl implements Serializable {
    private final Map<String, String> vertexAliasMap;
    private final Map<String, String> edgeAliasMap;

    public KgGraphRenameImpl(scala.collection.immutable.Map<Var, Var> schemaMapping) {
        this.vertexAliasMap = new HashMap<>();
        this.edgeAliasMap = new HashMap<>();
        for (Map.Entry<Var, Var> entry : JavaConversions.mapAsJavaMap(schemaMapping).entrySet()) {
            if (entry.getKey() instanceof NodeVar && entry.getValue() instanceof NodeVar
                    && !entry.getKey().name().equals(entry.getValue().name())) {
                this.vertexAliasMap.put(entry.getKey().name(), entry.getValue().name());
            } else if (entry.getKey() instanceof EdgeVar && entry.getValue() instanceof EdgeVar
                    && !entry.getKey().name().equals(entry.getValue().name())) {
                this.edgeAliasMap.put(entry.getKey().name(), entry.getValue().name());
            }
        }
    }

    public KgGraph<IVertexId> rename(KgGraph<IVertexId> value) {
        if (this.vertexAliasMap.isEmpty() && this.edgeAliasMap.isEmpty()) {
            return value;
        }
        KgGraphImpl kgGraph = (KgGraphImpl) value;
        for (Map.Entry<String, String> entry : this.vertexAliasMap.entrySet()) {
            kgGraph.getAlias2VertexMap().put(entry.getValue(), kgGraph.getAlias2VertexMap().remove(entry.getKey()));
        }
        for (Map.Entry<String, String> entry : this.edgeAliasMap.entrySet()) {
            kgGraph.getAlias2EdgeMap().put(entry.getValue(), kgGraph.getAlias2EdgeMap().remove(entry.getKey()));
        }
        return value;
    }

    public KgGraph<IVertexId> renameAndRemoveRoot(KgGraph<IVertexId> value, String rootVertexAlias) {
        KgGraphImpl kgGraph = (KgGraphImpl) rename(value);
        kgGraph.getAlias2VertexMap().remove(this.vertexAliasMap.get(rootVertexAlias));
        return kgGraph;
    }
}