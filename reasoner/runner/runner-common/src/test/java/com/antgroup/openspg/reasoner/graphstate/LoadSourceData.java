/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.graphstate;

import java.util.Map;
import java.util.Set;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.generator.AbstractGraphGenerator;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;


public abstract class LoadSourceData extends AbstractGraphGenerator {

    /**
     * Load alias to vertex map from source data
     *
     * @return
     */
    public abstract Map<String, Set<IVertex<IVertexId, IProperty>>> loadAlias2Vertex();

    /**
     * Load alias to edge map from source data
     *
     * @return
     */
    public abstract Map<String, Set<IEdge<IVertexId, IProperty>>> loadAlias2Edge();

    /**
     * Load source data schema
     * @return
     */
    public abstract Pattern loadPattern();
}