/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rdg.common;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.OptionalEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.PathEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.MirrorVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.google.common.collect.Sets;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class UnfoldEdgeImpl implements Serializable {
    private static final long serialVersionUID = 8752841991024282878L;
    private final UnfoldRepeatEdgeInfo unfoldRepeatEdgeInfo;

    public UnfoldEdgeImpl(UnfoldRepeatEdgeInfo unfoldRepeatEdgeInfo) {
        this.unfoldRepeatEdgeInfo = unfoldRepeatEdgeInfo;
    }

    public List<KgGraph<IVertexId>> unfold(KgGraphImpl kgGraph) {
        List<KgGraph<IVertexId>> result = new ArrayList<>();
        result.add(kgGraph);
        Set<IEdge<IVertexId, IProperty>> edgeSet = kgGraph.getAlias2EdgeMap().get(this.unfoldRepeatEdgeInfo.getEdgeAlias());
        for (IEdge<IVertexId, IProperty> iEdge : edgeSet) {
            if (iEdge instanceof OptionalEdge) {
                continue;
            }
            PathEdge<IVertexId, IProperty, IProperty> pathEdge = ((PathEdge<IVertexId, IProperty, IProperty>) iEdge).clone();
            while (pathEdge.getEdgeList().size() > this.unfoldRepeatEdgeInfo.getLower()) {
                if (1 == pathEdge.getEdgeList().size()) {
                    // unfold to optional edge
                    KgGraphImpl newKgGraph = new KgGraphImpl(kgGraph);
                    Edge<IVertexId, IProperty> edge = pathEdge.getEdgeList().get(0);
                    IVertexId anchorVertexId = edge.getSourceId();
                    IVertex<IVertexId, IProperty> newAnchorVertex = null;
                    for (IVertex<IVertexId, IProperty> v :
                            kgGraph.getAlias2VertexMap().get(this.unfoldRepeatEdgeInfo.getAnchorVertexAlias())) {
                        if (v.getId().equals(anchorVertexId)) {
                            newAnchorVertex = new MirrorVertex<>(v);
                            break;
                        }
                    }
                    if (null == newAnchorVertex) {
                        throw new RuntimeException("unfold_error, kgGraph=" + kgGraph + ",info=" + this.unfoldRepeatEdgeInfo);
                    }
                    newKgGraph.getAlias2EdgeMap().put(this.unfoldRepeatEdgeInfo.getEdgeAlias(),
                            Sets.newHashSet(new OptionalEdge<>(anchorVertexId, anchorVertexId)));
                    newKgGraph.getAlias2VertexMap().put(this.unfoldRepeatEdgeInfo.getFoldVertexAlias(), Sets.newHashSet(newAnchorVertex));
                    result.add(newKgGraph);
                    break;
                }
                Tuple2<Edge<IVertexId, IProperty>, Vertex<IVertexId, IProperty>> tuple2 = pathEdge.seversTail();
                KgGraphImpl newKgGraph = new KgGraphImpl(kgGraph);
                newKgGraph.getAlias2EdgeMap().put(this.unfoldRepeatEdgeInfo.getEdgeAlias(), Sets.newHashSet(pathEdge));
                newKgGraph.getAlias2VertexMap().put(this.unfoldRepeatEdgeInfo.getFoldVertexAlias(), Sets.newHashSet(tuple2._2()));
                result.add(newKgGraph);
                pathEdge = pathEdge.clone();
            }
        }
        return result;
    }
}