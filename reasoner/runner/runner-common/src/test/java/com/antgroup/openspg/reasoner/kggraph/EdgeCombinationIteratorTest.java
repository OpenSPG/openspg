/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.kggraph;

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.kggraph.impl.EdgeCombinationIterator;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphSplitStaticParameters;
import com.antgroup.openspg.reasoner.lube.common.pattern.*;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import scala.collection.JavaConversions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author donghai.ydh
 * @version EdgeCombinationIteratorTest.java, v 0.1 2023年04月24日 17:49 donghai.ydh
 */
public class EdgeCombinationIteratorTest {

    private final String DEFAULT_VERTEX_TYPE = "t";

    @Before
    public void init() {
    }

    @Test
    public void test1() {
        Pattern schema = schema1(null);
        Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = alias2VertexMap1();
        Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap = alias2EdgeMap1();

        int count = 0;

        Set<IVertexId> bIdSet = new HashSet<>();
        KgGraphSplitStaticParameters staticParameters = new KgGraphSplitStaticParameters(null, schema);
        EdgeCombinationIterator it = new EdgeCombinationIterator(staticParameters.getEdgeIterateInfoList(),
                staticParameters.getEdgeIterateOrderMap(), alias2VertexMap,
                alias2EdgeMap);
        while (it.hasNext()) {
            KgGraph<IVertexId> kgGraph = it.next();
            if (null == kgGraph) {
                continue;
            }
            count++;
            Assert.assertEquals(1, kgGraph.getEdge("E1").size());
            Assert.assertEquals(1, kgGraph.getEdge("E2").size());
            Assert.assertEquals(1, kgGraph.getVertex("A").size());
            Assert.assertEquals(1, kgGraph.getVertex("B").size());
            Assert.assertEquals(1, kgGraph.getVertex("C").size());

            Assert.assertEquals(kgGraph.getEdge("E1").iterator().next().getSourceId(), kgGraph.getVertex("B").iterator().next().getId());
            Assert.assertEquals(kgGraph.getEdge("E1").iterator().next().getTargetId(), kgGraph.getVertex("A").iterator().next().getId());
            Assert.assertEquals(kgGraph.getEdge("E2").iterator().next().getSourceId(), kgGraph.getVertex("B").iterator().next().getId());
            Assert.assertEquals(kgGraph.getEdge("E2").iterator().next().getTargetId(), kgGraph.getVertex("C").iterator().next().getId());

            bIdSet.add(kgGraph.getVertex("B").iterator().next().getId());
        }
        Assert.assertEquals(2, count);
        Assert.assertEquals(2, bIdSet.size());
        Assert.assertTrue(bIdSet.contains(IVertexId.from(21, DEFAULT_VERTEX_TYPE)));
        Assert.assertTrue(bIdSet.contains(IVertexId.from(22, DEFAULT_VERTEX_TYPE)));
    }

    @Test
    public void test2() {
        Map<String, scala.collection.immutable.Set<Connection>> addTopology = new HashMap<>();
        addTopology.put("C", JavaConversions.asScalaSet(Sets.newHashSet(
                new PatternConnection("E3", "C", null, "A", Direction.OUT, null, -1, true, false)
        )).toSet());
        Pattern schema = schema1(addTopology);

        Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = alias2VertexMap1();
        Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap = alias2EdgeMap1();
        alias2EdgeMap.put("E3", Sets.newHashSet(
                new Edge(IVertexId.from(31, DEFAULT_VERTEX_TYPE), IVertexId.from(11, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E3T")
                , new Edge(IVertexId.from(31, DEFAULT_VERTEX_TYPE), IVertexId.from(12, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E3T")
                , new Edge(IVertexId.from(32, DEFAULT_VERTEX_TYPE), IVertexId.from(11, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E3T")
                , new Edge(IVertexId.from(32, DEFAULT_VERTEX_TYPE), IVertexId.from(12, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E3T")
        ));

        int count = 0;
        Set<IVertexId> bIdSet = new HashSet<>();
        KgGraphSplitStaticParameters staticParameters = new KgGraphSplitStaticParameters(null, schema);
        EdgeCombinationIterator it = new EdgeCombinationIterator(staticParameters.getEdgeIterateInfoList(),
                staticParameters.getEdgeIterateOrderMap(), alias2VertexMap,
                alias2EdgeMap);
        while (it.hasNext()) {
            KgGraph<IVertexId> kgGraph = it.next();
            if (null == kgGraph) {
                continue;
            }
            count++;
            Assert.assertEquals(1, kgGraph.getEdge("E1").size());
            Assert.assertEquals(1, kgGraph.getEdge("E2").size());
            Assert.assertEquals(1, kgGraph.getEdge("E3").size());
            Assert.assertEquals(1, kgGraph.getVertex("A").size());
            Assert.assertEquals(1, kgGraph.getVertex("B").size());
            Assert.assertEquals(1, kgGraph.getVertex("C").size());

            Assert.assertEquals(kgGraph.getEdge("E1").iterator().next().getSourceId(), kgGraph.getVertex("B").iterator().next().getId());
            Assert.assertEquals(kgGraph.getEdge("E1").iterator().next().getTargetId(), kgGraph.getVertex("A").iterator().next().getId());
            Assert.assertEquals(kgGraph.getEdge("E2").iterator().next().getSourceId(), kgGraph.getVertex("B").iterator().next().getId());
            Assert.assertEquals(kgGraph.getEdge("E2").iterator().next().getTargetId(), kgGraph.getVertex("C").iterator().next().getId());
            Assert.assertEquals(kgGraph.getEdge("E3").iterator().next().getSourceId(), kgGraph.getVertex("C").iterator().next().getId());
            Assert.assertEquals(kgGraph.getEdge("E3").iterator().next().getTargetId(), kgGraph.getVertex("A").iterator().next().getId());

            bIdSet.add(kgGraph.getVertex("B").iterator().next().getId());
        }
        Assert.assertEquals(2, count);
        Assert.assertEquals(2, bIdSet.size());
        Assert.assertTrue(bIdSet.contains(IVertexId.from(21, DEFAULT_VERTEX_TYPE)));
        Assert.assertTrue(bIdSet.contains(IVertexId.from(22, DEFAULT_VERTEX_TYPE)));
    }

    @Test
    public void test3() {
        Pattern schema = schema2();
        Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = alias2VertexMap2();
        Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap = alias2EdgeMap2();

        int count = 0;

        Set<IVertexId> aIdSet = new HashSet<>();
        KgGraphSplitStaticParameters staticParameters = new KgGraphSplitStaticParameters(null, schema);
        EdgeCombinationIterator it = new EdgeCombinationIterator(staticParameters.getEdgeIterateInfoList(),
                staticParameters.getEdgeIterateOrderMap(), alias2VertexMap,
                alias2EdgeMap);
        while (it.hasNext()) {
            KgGraph<IVertexId> kgGraph = it.next();
            if (null == kgGraph) {
                continue;
            }
            count++;
            Assert.assertEquals(1, kgGraph.getEdge("E1").size());
            Assert.assertEquals(1, kgGraph.getEdge("E2").size());
            Assert.assertEquals(1, kgGraph.getEdge("E3").size());
            Assert.assertEquals(1, kgGraph.getVertex("A").size());
            Assert.assertEquals(1, kgGraph.getVertex("B").size());
            Assert.assertEquals(1, kgGraph.getVertex("C").size());
            Assert.assertEquals(1, kgGraph.getVertex("D").size());

            aIdSet.add(kgGraph.getVertex("A").iterator().next().getId());
        }

        Assert.assertEquals(8, count);
        Assert.assertEquals(1, aIdSet.size());
        Assert.assertTrue(aIdSet.contains(IVertexId.from(11, DEFAULT_VERTEX_TYPE)));
    }

    @Test
    public void test4() {
        Pattern schema = schema2();
        Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = alias2VertexMap2();
        Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap = alias2EdgeMap2();

        alias2EdgeMap.get("E3").add(
                new Edge(IVertexId.from(15, DEFAULT_VERTEX_TYPE), IVertexId.from(45, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E3T")
        );

        int count = 0;

        Set<IVertexId> aIdSet = new HashSet<>();
        KgGraphSplitStaticParameters staticParameters = new KgGraphSplitStaticParameters(null, schema);
        EdgeCombinationIterator it = new EdgeCombinationIterator(staticParameters.getEdgeIterateInfoList(),
                staticParameters.getEdgeIterateOrderMap(), alias2VertexMap,
                alias2EdgeMap);
        while (it.hasNext()) {
            KgGraph<IVertexId> kgGraph = it.next();
            if (null == kgGraph) {
                continue;
            }
            count++;
            Assert.assertEquals(1, kgGraph.getEdge("E1").size());
            Assert.assertEquals(1, kgGraph.getEdge("E2").size());
            Assert.assertEquals(1, kgGraph.getEdge("E3").size());
            Assert.assertEquals(1, kgGraph.getVertex("A").size());
            Assert.assertEquals(1, kgGraph.getVertex("B").size());
            Assert.assertEquals(1, kgGraph.getVertex("C").size());
            Assert.assertEquals(1, kgGraph.getVertex("D").size());

            aIdSet.add(kgGraph.getVertex("A").iterator().next().getId());
        }

        Assert.assertEquals(9, count);
        Assert.assertEquals(2, aIdSet.size());
        Assert.assertTrue(aIdSet.contains(IVertexId.from(11, DEFAULT_VERTEX_TYPE)));
        Assert.assertTrue(aIdSet.contains(IVertexId.from(15, DEFAULT_VERTEX_TYPE)));
    }

    @Test
    public void test5() {
        Pattern schema = schema1(null);
        Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = alias2VertexMap1();
        Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap = alias2EdgeMap1();

        final int num = 10;
        IVertexId b = IVertexId.from(2000, DEFAULT_VERTEX_TYPE);
        alias2VertexMap.get("B").add(new Vertex<>(b, null));
        for (int i = 0; i < num; ++i) {
            IVertexId a = IVertexId.from(1000 + i, DEFAULT_VERTEX_TYPE);
            IVertexId c = IVertexId.from(3000 + i, DEFAULT_VERTEX_TYPE);
            alias2VertexMap.get("A").add(new Vertex<>(a, null));
            alias2VertexMap.get("C").add(new Vertex<>(c, null));
            alias2EdgeMap.get("E1").add(new Edge<>(b, a, null, 0L, Direction.IN, "E1T"));
            alias2EdgeMap.get("E2").add(new Edge<>(b, c, null, 0L, Direction.OUT, "E2T"));
        }

        long count = 0;
        KgGraphSplitStaticParameters staticParameters = new KgGraphSplitStaticParameters(null, schema);
        EdgeCombinationIterator it = new EdgeCombinationIterator(staticParameters.getEdgeIterateInfoList(),
                staticParameters.getEdgeIterateOrderMap(), alias2VertexMap,
                alias2EdgeMap);
        it.setLogCount(num);
        while (it.hasNext()) {
            KgGraph<IVertexId> kgGraph = it.next();
            if (null == kgGraph) {
                continue;
            }
            count++;
        }
        Assert.assertEquals(num * num + 2, count);
    }

    // duplicate data
    @Test
    public void test6() {
        PatternElement did = new PatternElement("did", JavaConversions.asScalaSet(Sets.newHashSet("ABM.Apdid")).toSet(), null);
        PatternElement pkg = new PatternElement("pkg", JavaConversions.asScalaSet(Sets.newHashSet("ABM.Pkg")).toSet(), null);
        PatternElement pkg2 = new PatternElement("pkg2", JavaConversions.asScalaSet(Sets.newHashSet("ABM.Pkg")).toSet(), null);
        PatternElement b = new PatternElement("b", JavaConversions.asScalaSet(Sets.newHashSet("ABM.BundleApp")).toSet(), null);
        PatternElement c = new PatternElement("c", JavaConversions.asScalaSet(Sets.newHashSet("ABM.BundleApp")).toSet(), null);
        PatternElement family = new PatternElement("family",
                JavaConversions.asScalaSet(Sets.newHashSet("ABM.BundleAppFamily")).toSet(), null);
        Map<String, scala.collection.immutable.Set<Connection>> topology = new HashMap<>();
        topology.put(did.alias(), JavaConversions.asScalaSet(Sets.newHashSet(
                new PatternConnection("r2", "did", null, "pkg", Direction.OUT, null, -1, true, false)
                , new PatternConnection("r1", "did", null, "pkg2", Direction.OUT, null, -1, true, false)
        )).toSet());
        topology.put(pkg.alias(), JavaConversions.asScalaSet(Sets.newHashSet(
                new PatternConnection("e1", "pkg", null, "b", Direction.OUT, null, -1, true, false)
        )).toSet());
        topology.put(b.alias(), JavaConversions.asScalaSet(Sets.newHashSet(
                new PatternConnection("e2", "b", null, "family", Direction.OUT, null, -1, true, false)
        )).toSet());
        topology.put(pkg2.alias(), JavaConversions.asScalaSet(Sets.newHashSet(
                new PatternConnection("e4", "pkg2", null, "c", Direction.OUT, null, -1, true, false)
        )).toSet());
        topology.put(family.alias(), JavaConversions.asScalaSet(Sets.newHashSet(
                new PatternConnection("e3", "family", null, "c", Direction.IN, null, null, true, false)
        )).toSet());
        Map<String, PatternElement> map = new HashMap<>();
        map.put("did", did);
        map.put("pkg", pkg);
        map.put("pkg2", pkg2);
        map.put("b", b);
        map.put("c", c);
        map.put("family", family);
        Pattern schema = new PartialGraphPattern(family.alias(),
                JavaConversions.mapAsScalaMap(map).toMap(scala.Predef$.MODULE$.conforms()),
                Convert2ScalaUtil.toScalaImmutableMap(topology));

        Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = new HashMap<>();
        alias2VertexMap.put("did", Sets.newHashSet(
                new Vertex<>(IVertexId.from(11, DEFAULT_VERTEX_TYPE), null)
        ));
        alias2VertexMap.put("pkg", Sets.newHashSet(
                new Vertex<>(IVertexId.from(21, DEFAULT_VERTEX_TYPE), null)
        ));
        alias2VertexMap.put("pkg2", Sets.newHashSet(
                new Vertex<>(IVertexId.from(21, DEFAULT_VERTEX_TYPE), null)
                , new Vertex<>(IVertexId.from(22, DEFAULT_VERTEX_TYPE), null)
        ));
        alias2VertexMap.put("b", Sets.newHashSet(
                new Vertex<>(IVertexId.from(31, DEFAULT_VERTEX_TYPE), null)
        ));
        alias2VertexMap.put("c", Sets.newHashSet(
                new Vertex<>(IVertexId.from(32, DEFAULT_VERTEX_TYPE), null)
        ));
        alias2VertexMap.put("family", Sets.newHashSet(
                new Vertex<>(IVertexId.from(41, DEFAULT_VERTEX_TYPE), null)
        ));

        Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap = new HashMap<>();
        alias2EdgeMap.put("r1", Sets.newHashSet(
                new Edge(IVertexId.from(11, DEFAULT_VERTEX_TYPE), IVertexId.from(21, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "r")
        ));
        alias2EdgeMap.put("r2", Sets.newHashSet(
                new Edge(IVertexId.from(11, DEFAULT_VERTEX_TYPE), IVertexId.from(21, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "r")
                , new Edge(IVertexId.from(11, DEFAULT_VERTEX_TYPE), IVertexId.from(22, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "r")
        ));
        alias2EdgeMap.put("e4", Sets.newHashSet(
                new Edge(IVertexId.from(21, DEFAULT_VERTEX_TYPE), IVertexId.from(31, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "e")
        ));
        alias2EdgeMap.put("e1", Sets.newHashSet(
                new Edge(IVertexId.from(21, DEFAULT_VERTEX_TYPE), IVertexId.from(32, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "e")
                , new Edge(IVertexId.from(22, DEFAULT_VERTEX_TYPE), IVertexId.from(32, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "e")
        ));
        alias2EdgeMap.put("e3", Sets.newHashSet(
                new Edge(IVertexId.from(41, DEFAULT_VERTEX_TYPE), IVertexId.from(31, DEFAULT_VERTEX_TYPE), null, 0L, Direction.IN, "ef")
        ));
        alias2EdgeMap.put("e2", Sets.newHashSet(
                new Edge(IVertexId.from(32, DEFAULT_VERTEX_TYPE), IVertexId.from(41, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "ef")
        ));

        int count = 0;
        KgGraphSplitStaticParameters staticParameters = new KgGraphSplitStaticParameters(null, schema);
        EdgeCombinationIterator it = new EdgeCombinationIterator(staticParameters.getEdgeIterateInfoList(),
                staticParameters.getEdgeIterateOrderMap(), alias2VertexMap,
                alias2EdgeMap);
        while (it.hasNext()) {
            KgGraph<IVertexId> kgGraph = it.next();
            if (null == kgGraph) {
                continue;
            }
            count++;
        }
        Assert.assertEquals(1, count);

    }

    private Pattern schema1(Map<String, scala.collection.immutable.Set<Connection>> addTopology) {
        PatternElement A = new PatternElement("A", JavaConversions.asScalaSet(Sets.newHashSet("A")).toSet(), null);
        PatternElement B = new PatternElement("B", JavaConversions.asScalaSet(Sets.newHashSet("B")).toSet(), null);
        PatternElement C = new PatternElement("C", JavaConversions.asScalaSet(Sets.newHashSet("C")).toSet(), null);
        Map<String, scala.collection.immutable.Set<Connection>> topology = new HashMap<>();
        if (null != addTopology) {
            topology.putAll(addTopology);
        }
        topology.put("B", JavaConversions.asScalaSet(Sets.newHashSet(
                new PatternConnection("E2", "B", null, "C", Direction.OUT, null, -1, true, false)
                , new PatternConnection("E1", "B", null, "A", Direction.IN, null, null, true, false)
        )).toSet());
        return new PartialGraphPattern(B.alias(),
                JavaConversions.mapAsScalaMap(ImmutableMap.of("A", A, "B", B, "C", C)).toMap(scala.Predef$.MODULE$.conforms()),
                Convert2ScalaUtil.toScalaImmutableMap(topology));
    }

    private Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap1() {
        Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = new HashMap<>();
        alias2VertexMap.put("A", Sets.newHashSet(
                new Vertex<>(IVertexId.from(11, DEFAULT_VERTEX_TYPE), null)
                , new Vertex<>(IVertexId.from(12, DEFAULT_VERTEX_TYPE), null)
        ));
        alias2VertexMap.put("B", Sets.newHashSet(
                new Vertex<>(IVertexId.from(21, DEFAULT_VERTEX_TYPE), null)
                , new Vertex<>(IVertexId.from(22, DEFAULT_VERTEX_TYPE), null)
        ));
        alias2VertexMap.put("C", Sets.newHashSet(
                new Vertex<>(IVertexId.from(31, DEFAULT_VERTEX_TYPE), null)
                , new Vertex<>(IVertexId.from(32, DEFAULT_VERTEX_TYPE), null)
        ));
        return alias2VertexMap;
    }

    private Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap1() {
        Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap = new HashMap<>();
        alias2EdgeMap.put("E1", Sets.newHashSet(
                new Edge(IVertexId.from(21, DEFAULT_VERTEX_TYPE), IVertexId.from(11, DEFAULT_VERTEX_TYPE), null, 0L, Direction.IN, "E1T")
                , new Edge(IVertexId.from(22, DEFAULT_VERTEX_TYPE), IVertexId.from(12, DEFAULT_VERTEX_TYPE), null, 0L, Direction.IN, "E1T")
        ));

        alias2EdgeMap.put("E2", Sets.newHashSet(
                new Edge(IVertexId.from(21, DEFAULT_VERTEX_TYPE), IVertexId.from(31, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E2T")
                , new Edge(IVertexId.from(22, DEFAULT_VERTEX_TYPE), IVertexId.from(32, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E2T")
        ));
        return alias2EdgeMap;
    }

    private Pattern schema2() {
        PatternElement A = new PatternElement("A", JavaConversions.asScalaSet(Sets.newHashSet("A")).toSet(), null);
        PatternElement B = new PatternElement("B", JavaConversions.asScalaSet(Sets.newHashSet("B")).toSet(), null);
        PatternElement C = new PatternElement("C", JavaConversions.asScalaSet(Sets.newHashSet("C")).toSet(), null);
        PatternElement D = new PatternElement("D", JavaConversions.asScalaSet(Sets.newHashSet("D")).toSet(), null);
        Map<String, scala.collection.immutable.Set<Connection>> topology = new HashMap<>();
        topology.put("A", JavaConversions.asScalaSet(Sets.newHashSet(
                new PatternConnection("E1", "A", null, "B", Direction.OUT, null, -1, true, false)
                , new PatternConnection("E2", "A", null, "C", Direction.OUT, null, -1, true, false)
                , new PatternConnection("E3", "A", null, "D", Direction.OUT, null, -1, true, false)
        )).toSet());
        return new PartialGraphPattern(B.alias(),
                JavaConversions.mapAsScalaMap(ImmutableMap.of("A", A, "B", B, "C", C, "D", D)).toMap(scala.Predef$.MODULE$.conforms()),
                Convert2ScalaUtil.toScalaImmutableMap(topology));
    }

    private Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap2() {
        Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = new HashMap<>();
        alias2VertexMap.put("A", Sets.newHashSet(
                new Vertex<>(IVertexId.from(11, DEFAULT_VERTEX_TYPE), null)
                , new Vertex<>(IVertexId.from(15, DEFAULT_VERTEX_TYPE), null)
        ));
        alias2VertexMap.put("B", Sets.newHashSet(
                new Vertex<>(IVertexId.from(21, DEFAULT_VERTEX_TYPE), null)
                , new Vertex<>(IVertexId.from(22, DEFAULT_VERTEX_TYPE), null)
                , new Vertex<>(IVertexId.from(25, DEFAULT_VERTEX_TYPE), null)
        ));
        alias2VertexMap.put("C", Sets.newHashSet(
                new Vertex<>(IVertexId.from(31, DEFAULT_VERTEX_TYPE), null)
                , new Vertex<>(IVertexId.from(32, DEFAULT_VERTEX_TYPE), null)
                , new Vertex<>(IVertexId.from(35, DEFAULT_VERTEX_TYPE), null)
        ));
        alias2VertexMap.put("D", Sets.newHashSet(
                new Vertex<>(IVertexId.from(41, DEFAULT_VERTEX_TYPE), null)
                , new Vertex<>(IVertexId.from(42, DEFAULT_VERTEX_TYPE), null)
        ));
        return alias2VertexMap;
    }

    private Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap2() {
        Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap = new HashMap<>();
        alias2EdgeMap.put("E1", Sets.newHashSet(
                new Edge(IVertexId.from(11, DEFAULT_VERTEX_TYPE), IVertexId.from(21, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E1T")
                , new Edge(IVertexId.from(11, DEFAULT_VERTEX_TYPE), IVertexId.from(22, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E1T")
                , new Edge(IVertexId.from(15, DEFAULT_VERTEX_TYPE), IVertexId.from(25, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E1T")
        ));

        alias2EdgeMap.put("E2", Sets.newHashSet(
                new Edge(IVertexId.from(11, DEFAULT_VERTEX_TYPE), IVertexId.from(31, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E2T")
                , new Edge(IVertexId.from(11, DEFAULT_VERTEX_TYPE), IVertexId.from(32, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E2T")
                , new Edge(IVertexId.from(15, DEFAULT_VERTEX_TYPE), IVertexId.from(35, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E2T")
        ));

        alias2EdgeMap.put("E3", Sets.newHashSet(
                new Edge(IVertexId.from(11, DEFAULT_VERTEX_TYPE), IVertexId.from(41, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E3T")
                , new Edge(IVertexId.from(11, DEFAULT_VERTEX_TYPE), IVertexId.from(42, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E3T")
        ));
        return alias2EdgeMap;
    }

}