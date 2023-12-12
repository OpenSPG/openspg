package com.antgroup.openspg.reasoner.warehouse.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import org.apache.commons.collections4.CollectionUtils;


public class VertexSubGraph implements Serializable {
    /**
     * center vertex in vertex subgraph
     */
    private final IVertex<IVertexId, IProperty> vertex;

    /**
     * in edges for center vertex
     */
    private final List<IEdge<IVertexId, IProperty>> inEdges;
    private final Map<String, Long>                 inEdgeCntMap;

    /**
     * out edges for center vertex
     */
    private final List<IEdge<IVertexId, IProperty>> outEdges;
    private final Map<String, Long>                 outEdgeCntMap;

    public VertexSubGraph(IVertex<IVertexId, IProperty> vertex) {
        this.vertex = vertex;
        this.inEdges = new ArrayList<>();
        this.inEdgeCntMap = new HashMap<>();
        this.outEdges = new ArrayList<>();
        this.outEdgeCntMap = new HashMap<>();
    }

    public void addInEdge(IEdge<IVertexId, IProperty> inEdge) {
        if (inEdge == null) {
            return;
        }
        inEdges.add(inEdge);

        Long edgeCnt = inEdgeCntMap.getOrDefault(inEdge.getType(), 0L);
        inEdgeCntMap.put(inEdge.getType(), edgeCnt + 1);
    }

    public void addInEdge(List<Edge<IVertexId, IProperty>> inEdgeList) {
        if (CollectionUtils.isEmpty(inEdgeList)) {
            return;
        }
        for (IEdge<IVertexId, IProperty> inEdge : inEdgeList) {
            addInEdge(inEdge);
        }
    }

    public void addOutEdge(IEdge<IVertexId, IProperty> outEdge) {
        if (outEdge == null) {
            return;
        }
        outEdges.add(outEdge);

        Long edgeCnt = outEdgeCntMap.getOrDefault(outEdge.getType(), 0L);
        outEdgeCntMap.put(outEdge.getType(), edgeCnt + 1);
    }

    public void addOutEdge(List<Edge<IVertexId, IProperty>> outEdgeList) {
        if (CollectionUtils.isEmpty(outEdgeList)) {
            return;
        }
        for (IEdge<IVertexId, IProperty> outEdge : outEdgeList) {
            addOutEdge(outEdge);
        }
    }

    public boolean vertexEquals(IVertexId vertexId) {
        return vertex.getId().equals(vertexId);
    }

    public IVertex<IVertexId, IProperty> getVertex() {
        return vertex;
    }

    public List<IEdge<IVertexId, IProperty>> getInEdges() {
        return inEdges;
    }

    public Long getInEdgeCnt(String edgeType) {
        return inEdgeCntMap.getOrDefault(edgeType, 0L);
    }

    public List<IEdge<IVertexId, IProperty>> getOutEdges() {
        return outEdges;
    }

    public Long getOutEdgeCnt(String edgeType) {
        return outEdgeCntMap.getOrDefault(edgeType, 0L);
    }

    public Long getTotalEdgeCnt() {
        final long[] count = {0};
        inEdgeCntMap.values().forEach(v -> count[0] += v);
        outEdgeCntMap.values().forEach(v -> count[0] += v);
        return count[0];
    }

    /**
     * Getter method for property <tt>inEdgeCntMap</tt>.
     *
     * @return property value of inEdgeCntMap
     */
    public Map<String, Long> getInEdgeCntMap() {
        return inEdgeCntMap;
    }

    /**
     * Getter method for property <tt>outEdgeCntMap</tt>.
     *
     * @return property value of outEdgeCntMap
     */
    public Map<String, Long> getOutEdgeCntMap() {
        return outEdgeCntMap;
    }

    @Override
    public String toString() {
        return "vertexId=" + vertex.getId();
    }
}
