/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rdg.common;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


public interface KgGraphListProcess extends Serializable {
    List<KgGraph<IVertexId>> reduce(Collection<KgGraph<IVertexId>> kgGraphs);
}