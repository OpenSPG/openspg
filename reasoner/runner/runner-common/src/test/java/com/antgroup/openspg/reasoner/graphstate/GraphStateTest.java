/*
 * Copyright 2023 Ant Group CO., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.reasoner.graphstate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import com.antgroup.openspg.reasoner.graphstate.model.MergeTypeEnum;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GraphStateTest {

    private GraphState<IVertexId> graphState = new MemGraphState();

    /*
     * datasource is below
     *          vs1
     *         /  \
     *       vt1   vt2
     */
    private LoadSourceData sourceData = new MockSourceData();

    @Before
    public void init() {
        sourceData.getVertexList().forEach(vertex -> graphState.addVertex(vertex));
        sourceData.getEdgeAggregated().forEach(
                (s, listListTuple2) -> graphState.addEdges(s, listListTuple2._1(), listListTuple2._2()));
    }

    @Test
    public void testMergeVertexProperty() {
        Vertex<IVertexId, IProperty> vertex1 = new Vertex<>();
        vertex1.setId(IVertexId.from("vs2", "Student"));
        IProperty property1 = new VertexVersionProperty();
        property1.put("type", "Student");
        vertex1.setValue(property1);
        graphState.addVertex(vertex1);

        IVertexId vertexId = IVertexId.from("vs2", "Student");
        Map<String, Object> property = new HashMap<>();
        // empty
        graphState.mergeVertexProperty(IVertexId.from("empty", "Student"), property, MergeTypeEnum.REPLACE, 0L);
        // replace
        property.put("gmtCreate", "2023.02.07");
        graphState.mergeVertexProperty(vertexId, property, MergeTypeEnum.REPLACE, 0L);
        Assert.assertTrue(graphState.getVertex(vertexId, 0L).getValue().isKeyExist("gmtCreate"));
        // append
        property.put("gmtCreate", "2023.02.08");
        graphState.mergeVertexProperty(vertexId, property, MergeTypeEnum.APPEND, 0L);
        String val = (String) graphState.getVertex(vertexId, null).getValue().get("gmtCreate");
        Assert.assertTrue(val.contains("2023.02.07") && val.contains("2023.02.08"));
        // version is null
        property.put("gmtCreate", "2023.02.09");
        graphState.mergeVertexProperty(vertexId, property, MergeTypeEnum.REPLACE, null);
        val = (String) graphState.getVertex(vertexId, null).getValue().get("gmtCreate");
        Assert.assertTrue(val.contains("2023.02.09") && !val.contains("2023.02.07") && !val.contains("2023.02.08"));

        // merge new version property
        property.put("gmtCreate", "2023.02.10");
        graphState.mergeVertexProperty(vertexId, property, MergeTypeEnum.REPLACE, 1L);
        val = (String) graphState.getVertex(vertexId, 0L).getValue().get("gmtCreate");
        Assert.assertTrue(val.equals("2023.02.09"));
        val = (String) ((VertexVersionProperty) graphState.getVertex(vertexId, 1L).getValue()).get("gmtCreate", 1L);
        Assert.assertTrue(val.equals("2023.02.10"));
    }

    @Test
    public void testGetVertex() {
        IVertexId vertexId = graphState.getVertexIterator((Set<String>) null).next().getId();
        // version is null
        IVertex<IVertexId, IProperty> v = graphState.getVertex(vertexId, null);
        Assert.assertTrue(null != v);
        // version is 0
        v = graphState.getVertex(vertexId, 0L);
        Assert.assertTrue(null != v);
        // version not exist
        v = graphState.getVertex(vertexId, 1L);
        // use mock data every vertex has one property "id"
        Assert.assertTrue(null != v && v.getValue().getSize() == 1);
        IVertexId vertexIdNotExist = IVertexId.from("not_exist_id", "Student");
        v = graphState.getVertex(vertexIdNotExist, null);
    }

    private IEdge<IVertexId, IProperty> getOriginEdge(Iterator<IEdge<IVertexId, IProperty>> it, IEdge<IVertexId, IProperty> edge) {
        while (it.hasNext()) {
            IEdge<IVertexId, IProperty> tmp = it.next();
            if (tmp.getSourceId().equals(edge.getSourceId())
                    && tmp.getTargetId().equals(edge.getTargetId())
                    && tmp.getType().equals(edge.getType())
                    && tmp.getVersion().equals(edge.getVersion())) {
                return tmp;
            }
        }
        return null;
    }

    @Test
    public void testUpdateEdgeProperty() {
        IEdge<IVertexId, IProperty> edge = graphState.getEdgeIterator((Set<String>) null).next();
        IProperty property = edge.getValue();
        Assert.assertTrue(!property.isKeyExist("xGmtCreateX"));

        // update exist version edge
        property.put("xGmtCreateX", "2023.02.07");
        graphState.updateEdgeProperty(edge.getSourceId(), edge.getType(), edge.getTargetId(), 0L, property);
        Iterator<IEdge<IVertexId, IProperty>> it = graphState.getEdgeIterator((Set<String>) null);
        IEdge<IVertexId, IProperty> edge1 = getOriginEdge(it, edge);
        Assert.assertTrue(edge1.getValue().isKeyExist("xGmtCreateX"));

        // update not exist version edge
        try {
            graphState.updateEdgeProperty(edge.getSourceId(), edge.getType(), edge.getTargetId(), 1L, property);
            Assert.assertTrue(false);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testMergeEdgeProperty() {
        Map<String, Object> property = new HashMap<>();
        property.put("xGmtCreateX", "2023.02.13");
        IEdge<IVertexId, IProperty> edge = graphState.getEdgeIterator((Set<String>) null).next();
        Assert.assertTrue(!edge.getValue().isKeyExist("xGmtCreateX"));
        // merge new property
        graphState.mergeEdgeProperty(edge.getSourceId(), edge.getType(), edge.getTargetId(), edge.getVersion(), null, property,
                MergeTypeEnum.REPLACE);
        Iterator<IEdge<IVertexId, IProperty>> it = graphState.getEdgeIterator((Set<String>) null);
        IEdge<IVertexId, IProperty> edge1 = getOriginEdge(it, edge);
        Assert.assertTrue(edge1.getValue().isKeyExist("xGmtCreateX"));

        // merge with replace
        property.put("xGmtCreateX", "2023.02.13");
        property.put("xNewPropX", "newProp");
        graphState.mergeEdgeProperty(edge.getSourceId(), edge.getType(), edge.getTargetId(), edge.getVersion(), null, property,
                MergeTypeEnum.REPLACE);
        it = graphState.getEdgeIterator((Set<String>) null);
        IEdge<IVertexId, IProperty> edge2 = getOriginEdge(it, edge);
        Assert.assertTrue(edge2.getValue().get("xGmtCreateX").equals("2023.02.13"));
        Assert.assertTrue(edge2.getValue().isKeyExist("xNewPropX"));

        // merge with append
        property.put("xGmtCreateX", "2023.02.14");
        property.put("xNewPropX2", "xNewPropX2");
        graphState.mergeEdgeProperty(edge.getSourceId(), edge.getType(), edge.getTargetId(), edge.getVersion(), null, property,
                MergeTypeEnum.APPEND);
        it = graphState.getEdgeIterator((Set<String>) null);
        IEdge<IVertexId, IProperty> edge3 = getOriginEdge(it, edge);
        Assert.assertTrue(((String) edge3.getValue().get("xGmtCreateX")).contains("2023.02.13"));
        Assert.assertTrue(((String) edge3.getValue().get("xGmtCreateX")).contains("2023.02.14"));
        Assert.assertTrue(edge3.getValue().isKeyExist("xNewPropX2"));
    }

    @Test
    public void testGetEdge() {
        IVertexId vertexId = graphState.getVertexIterator((Set<String>) null).next().getId();
        IEdge<IVertexId, IProperty> edge = graphState.getEdges(vertexId, null, null, null, Direction.BOTH).get(0);
        // same condition
        List<IEdge<IVertexId, IProperty>> edges1 = graphState.getEdges(edge.getSourceId(), edge.getVersion(), edge.getVersion(),
                new HashSet<String>() {{add(edge.getType());}}, edge.getDirection());
        Assert.assertTrue(null != getOriginEdge(edges1, edge));

        // version is null
        List<IEdge<IVertexId, IProperty>> edges2 = graphState.getEdges(edge.getSourceId(), null, edge.getVersion(),
                new HashSet<String>() {{add(edge.getType());}}, edge.getDirection());
        Assert.assertTrue(null != getOriginEdge(edges2, edge));

        // type is empty
        List<IEdge<IVertexId, IProperty>> edges3 = graphState.getEdges(edge.getSourceId(), edge.getVersion(), null, new HashSet<>(),
                edge.getDirection());
        Assert.assertTrue(null == getOriginEdge(edges3, edge));

        // vertexId not exist
        IVertexId vertexIdNotExist = IVertexId.from("not_exist_id", "Student");
        List<IEdge<IVertexId, IProperty>> edges4 = graphState.getEdges(vertexIdNotExist, edge.getVersion(), null, new HashSet<>(),
                edge.getDirection());
        Assert.assertTrue(null == getOriginEdge(edges4, edge));

    }

    private IEdge<IVertexId, IProperty> getOriginEdge(List<IEdge<IVertexId, IProperty>> edgeList, IEdge<IVertexId, IProperty> edge) {
        for (IEdge<IVertexId, IProperty> tmp : edgeList) {
            if (tmp.getSourceId().equals(edge.getSourceId())
                    && tmp.getTargetId().equals(edge.getTargetId())
                    && tmp.getType().equals(edge.getType())
                    && tmp.getVersion().equals(edge.getVersion())) {
                return tmp;
            }
        }
        return null;
    }

    @Test
    public void testGetVertexIterator() {
        Iterator<IVertex<IVertexId, IProperty>> it = graphState.getVertexIterator((Set<String>) null);
        Assert.assertTrue(null != it);
        Assert.assertTrue(it.hasNext());
        IVertex<IVertexId, IProperty> vertex = it.next();
        it = graphState.getVertexIterator(new HashSet<String>() {{add((RunnerUtil.getVertexType(vertex)));}});
        Assert.assertTrue(it.hasNext());
        Assert.assertTrue(getOriginVertex(it, vertex));

        it = graphState.getVertexIterator(vertex1 -> vertex1.getId().equals(vertex.getId()));
        Assert.assertTrue(getOriginVertex(it, vertex));
    }

    private boolean getOriginVertex(Iterator<IVertex<IVertexId, IProperty>> it, IVertex<IVertexId, IProperty> vertex) {
        while (it.hasNext()) {
            IVertex<IVertexId, IProperty> vertex1 = it.next();
            if (vertex1.getId().equals(vertex.getId())) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testGetEdgeIterator() {
        IVertexId vertexId = graphState.getVertexIterator((Set<String>) null).next().getId();
        List<IEdge<IVertexId, IProperty>> edgeList = graphState.getEdges(vertexId, null, null, null, Direction.BOTH);
        Assert.assertTrue(!edgeList.isEmpty());
        IEdge<IVertexId, IProperty> edge = edgeList.get(0);
        Iterator<IEdge<IVertexId, IProperty>> it = graphState.getEdgeIterator(new HashSet<String>() {{add(edge.getType());}});
        Assert.assertTrue(null != it);
        Assert.assertTrue(getOriginEdge(it, edge) != null);

        it = graphState.getEdgeIterator(edge1 -> edge1.getType().equals(edge.getType()));
        Assert.assertTrue(getOriginEdge(it, edge) != null);
    }
}