/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.kggraph;

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphSplitStaticParameters;
import com.antgroup.openspg.reasoner.lube.common.pattern.*;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import scala.collection.JavaConversions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author donghai.ydh
 * @version KgGraphSimpleSplitTest.java, v 0.1 2023年04月21日 19:35 donghai.ydh
 */
public class KgGraphSimpleSplitTest {

    public static final String DEFAULT_VERTEX_TYPE = "t";

    @Before
    public void init() {
    }

    @Test
    public void testNotNeedSplit() {
        Pattern schema = pattern1();
        KgGraph<IVertexId> kgGraph = kgGraph1();

        Set<String> splitVertexAliasSet = Sets.newHashSet("B", "C", "D");
        List<KgGraph<IVertexId>> rst1 = kgGraph.split(splitVertexAliasSet, schema,
                new KgGraphSplitStaticParameters(splitVertexAliasSet, schema), null, null);
        Assert.assertEquals(1, rst1.size());
        Assert.assertEquals(rst1.get(0), kgGraph);

        Set<String> splitVertexAliasSet2 = Sets.newHashSet("D");
        List<KgGraph<IVertexId>> rst2 = kgGraph.split(splitVertexAliasSet2, schema,
                new KgGraphSplitStaticParameters(splitVertexAliasSet2, schema), null, null);
        Assert.assertEquals(1, rst2.size());
        Assert.assertEquals(rst2.get(0), kgGraph);

    }

    @Test
    public void testSampleSplit1() {
        Pattern schema = pattern1();
        KgGraph<IVertexId> kgGraph = kgGraph1();

        Set<String> splitVertexAliasSet = Sets.newHashSet("A");
        List<KgGraph<IVertexId>> rst1 = kgGraph.split(splitVertexAliasSet, schema,
                new KgGraphSplitStaticParameters(splitVertexAliasSet, schema), null, null);
        Assert.assertEquals(2, rst1.size());
        rst1.stream().forEach(iVertexIdKgGraph -> {
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("A").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("B").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("C").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("D").size());
        });

        Set<String> splitVertexAliasSet2 = Sets.newHashSet("A", "C");
        List<KgGraph<IVertexId>> rst2 = kgGraph.split(splitVertexAliasSet2, schema,
                new KgGraphSplitStaticParameters(splitVertexAliasSet2, schema), null, null);
        Assert.assertEquals(2, rst2.size());
        rst2.stream().forEach(iVertexIdKgGraph -> {
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("A").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("B").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("C").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("D").size());
        });
    }

    @Test
    public void testSampleSplit2() {
        Pattern schema = pattern1();
        KgGraphImpl kgGraph = kgGraph1();
        kgGraph.getAlias2VertexMap().get("A").addAll(Lists.newArrayList(
                new Vertex<>(IVertexId.from(13, DEFAULT_VERTEX_TYPE), null)
                , new Vertex<>(IVertexId.from(14, DEFAULT_VERTEX_TYPE), null)
        ));

        kgGraph.getAlias2VertexMap().get("B").add(
                new Vertex<>(IVertexId.from(22, DEFAULT_VERTEX_TYPE), null)
        );

        kgGraph.getAlias2EdgeMap().get("E1").add(
                new Edge(IVertexId.from(13, DEFAULT_VERTEX_TYPE), IVertexId.from(22, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E1T")
        );
        kgGraph.getAlias2EdgeMap().get("E1").add(
                new Edge(IVertexId.from(14, DEFAULT_VERTEX_TYPE), IVertexId.from(22, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E1T")
        );

        kgGraph.getAlias2EdgeMap().get("E2").add(
                new Edge(IVertexId.from(22, DEFAULT_VERTEX_TYPE), IVertexId.from(31, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E2T")
        );

        Set<String> splitVertexAliasSet1 = Sets.newHashSet("A", "B");
        List<KgGraph<IVertexId>> rst1 = kgGraph.split(splitVertexAliasSet1, schema,
                new KgGraphSplitStaticParameters(splitVertexAliasSet1, schema), null, null);
        Assert.assertEquals(4, rst1.size());
        rst1.stream().forEach(iVertexIdKgGraph -> {
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("A").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("B").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("C").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("D").size());
        });

        Set<String> splitVertexAliasSet2 = Sets.newHashSet("A");
        List<KgGraph<IVertexId>> rst2 = kgGraph.split(splitVertexAliasSet2, schema,
                new KgGraphSplitStaticParameters(splitVertexAliasSet2, schema), null, null);
        Assert.assertEquals(4, rst2.size());
        rst2.stream().forEach(iVertexIdKgGraph -> {
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("A").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("B").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("C").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("D").size());
        });

        Set<String> splitVertexAliasSet3 = Sets.newHashSet("B");
        List<KgGraph<IVertexId>> rst3 = kgGraph.split(splitVertexAliasSet3, schema,
                new KgGraphSplitStaticParameters(splitVertexAliasSet3, schema), null, null);
        Assert.assertEquals(2, rst3.size());
        rst3.stream().forEach(iVertexIdKgGraph -> {
            Assert.assertEquals(2, iVertexIdKgGraph.getVertex("A").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("B").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("C").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("D").size());
        });

        kgGraph.getAlias2VertexMap().get("D").add(
                new Vertex<>(IVertexId.from(42, DEFAULT_VERTEX_TYPE), null)
        );

        kgGraph.getAlias2EdgeMap().get("E3").add(
                new Edge(IVertexId.from(31, DEFAULT_VERTEX_TYPE), IVertexId.from(42, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E3T")
        );

        Set<String> splitVertexAliasSet4 = Sets.newHashSet("D");
        List<KgGraph<IVertexId>> rst4 = kgGraph.split(splitVertexAliasSet4, schema,
                new KgGraphSplitStaticParameters(splitVertexAliasSet4, schema), null, null);
        Assert.assertEquals(2, rst4.size());
        rst4.stream().forEach(iVertexIdKgGraph -> {
            Assert.assertEquals(4, iVertexIdKgGraph.getVertex("A").size());
            Assert.assertEquals(2, iVertexIdKgGraph.getVertex("B").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("C").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("D").size());
        });
    }

    @Test
    public void testSampleSplit3() {
        Pattern schema = pattern2();
        KgGraphImpl kgGraph = kgGraph2();

        Set<String> splitVertexAliasSet1 = Sets.newHashSet("C", "D");
        List<KgGraph<IVertexId>> rst1 = kgGraph.split(splitVertexAliasSet1, schema,
                new KgGraphSplitStaticParameters(splitVertexAliasSet1, schema), null, null);
        Assert.assertEquals(1, rst1.size());
        Assert.assertEquals(rst1.get(0), kgGraph);

        kgGraph.getAlias2VertexMap().get("B").add(
                new Vertex<>(IVertexId.from(22, DEFAULT_VERTEX_TYPE), null)
        );

        kgGraph.getAlias2EdgeMap().get("E1").add(
                new Edge(IVertexId.from(11, DEFAULT_VERTEX_TYPE), IVertexId.from(22, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E1T")
        );
        kgGraph.getAlias2EdgeMap().get("E2").add(
                new Edge(IVertexId.from(22, DEFAULT_VERTEX_TYPE), IVertexId.from(31, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E2T")
        );

        kgGraph.getAlias2VertexMap().get("D").add(
                new Vertex<>(IVertexId.from(42, DEFAULT_VERTEX_TYPE), null)
        );

        kgGraph.getAlias2EdgeMap().get("E3").add(
                new Edge(IVertexId.from(31, DEFAULT_VERTEX_TYPE), IVertexId.from(42, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E3T")
        );
        kgGraph.getAlias2EdgeMap().get("E4").add(
                new Edge(IVertexId.from(42, DEFAULT_VERTEX_TYPE), IVertexId.from(11, DEFAULT_VERTEX_TYPE), null, 0L, Direction.OUT, "E4T")
        );

        Set<String> splitVertexAliasSet2 = Sets.newHashSet("B", "D");
        List<KgGraph<IVertexId>> rst2 = kgGraph.split(splitVertexAliasSet2, schema,
                new KgGraphSplitStaticParameters(splitVertexAliasSet2, schema), null, null);
        Assert.assertEquals(4, rst2.size());
        rst2.stream().forEach(iVertexIdKgGraph -> {
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("A").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("B").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("C").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("D").size());
        });

        Set<String> splitVertexAliasSet3 = Sets.newHashSet("B");
        List<KgGraph<IVertexId>> rst3 = kgGraph.split(splitVertexAliasSet3, schema,
                new KgGraphSplitStaticParameters(splitVertexAliasSet3, schema), null, null);
        Assert.assertEquals(2, rst3.size());
        rst3.stream().forEach(iVertexIdKgGraph -> {
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("A").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("B").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("C").size());
            Assert.assertEquals(2, iVertexIdKgGraph.getVertex("D").size());
        });

    }

    @Test
    public void testSampleSplit4() {
        Pattern schema = pattern3();
        KgGraphImpl kgGraph = kgGraph3();

        Set<String> splitVertexAliasSet1 = Sets.newHashSet("B");
        List<KgGraph<IVertexId>> rst1 = kgGraph.split(splitVertexAliasSet1, schema,
                new KgGraphSplitStaticParameters(splitVertexAliasSet1, schema), null, null);
        Assert.assertEquals(1, rst1.size());
        rst1.stream().forEach(iVertexIdKgGraph -> {
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("A").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("B").size());
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("C").size());
            Assert.assertEquals(IVertexId.from(21, DEFAULT_VERTEX_TYPE), iVertexIdKgGraph.getVertex("B").get(0).getId());
        });
    }

    public static Pattern pattern1() {
        PatternElement A = new PatternElement("A", JavaConversions.asScalaSet(Sets.newHashSet("A")).toSet(), null);
        PatternElement B = new PatternElement("B", JavaConversions.asScalaSet(Sets.newHashSet("B")).toSet(), null);
        PatternElement C = new PatternElement("C", JavaConversions.asScalaSet(Sets.newHashSet("C")).toSet(), null);
        PatternElement D = new PatternElement("D", JavaConversions.asScalaSet(Sets.newHashSet("D")).toSet(), null);

        Connection A_B = new PatternConnection("E1", A.alias(), null, B.alias(), Direction.OUT, null, -1, true, false);
        Connection B_C = new PatternConnection("E2", B.alias(), null, C.alias(), Direction.OUT, null, -1, true, false);
        Connection C_D = new PatternConnection("E3", C.alias(), null, D.alias(), Direction.OUT, null, -1, true, false);

        Set<Connection> C_Connect = new HashSet() {{
            add(C_D);
            add(B_C);
        }};
        Set<Connection> B_Connect = new HashSet() {{
            add(B_C);
            add(A_B);
        }};
        Set<Connection> A_Connect = new HashSet() {{
            add(A_B);
        }};
        Set<Connection> D_Connect = new HashSet() {{
            add(C_D);
        }};

        Map<String, scala.collection.immutable.Set<Connection>> topology = new HashMap<>();
        topology.put(C.alias(), Convert2ScalaUtil.toScalaImmutableSet(C_Connect));
        topology.put(B.alias(), Convert2ScalaUtil.toScalaImmutableSet(B_Connect));
        topology.put(A.alias(), Convert2ScalaUtil.toScalaImmutableSet(A_Connect));
        topology.put(D.alias(), Convert2ScalaUtil.toScalaImmutableSet(D_Connect));

        Pattern schema = new PartialGraphPattern(C.alias(),
                JavaConversions.mapAsScalaMap(ImmutableMap.of("A", A, "B", B, "C", C, "D", D)).toMap(
                        scala.Predef$.MODULE$.conforms()),
                Convert2ScalaUtil.toScalaImmutableMap(topology));
        return schema;
    }

    public static Pattern pattern2() {
        PatternElement A = new PatternElement("A", JavaConversions.asScalaSet(Sets.newHashSet("A")).toSet(), null);
        PatternElement B = new PatternElement("B", JavaConversions.asScalaSet(Sets.newHashSet("B")).toSet(), null);
        PatternElement C = new PatternElement("C", JavaConversions.asScalaSet(Sets.newHashSet("C")).toSet(), null);
        PatternElement D = new PatternElement("D", JavaConversions.asScalaSet(Sets.newHashSet("D")).toSet(), null);

        PatternConnection A_B = new PatternConnection("E1", A.alias(), null, B.alias(), Direction.OUT, null, -1, true, false);
        PatternConnection B_C = new PatternConnection("E2", B.alias(), null, C.alias(), Direction.OUT, null, -1, true, false);
        PatternConnection C_D = new PatternConnection("E3", C.alias(), null, D.alias(), Direction.OUT, null, -1, true, false);
        PatternConnection D_A = new PatternConnection("E4", D.alias(), null, A.alias(), Direction.OUT, null, -1, true, false);

        Set<Connection> C_Connect = new HashSet() {{
            add(C_D);
            add(B_C);
        }};
        Set<Connection> B_Connect = new HashSet() {{
            add(B_C);
            add(A_B);
        }};
        Set<Connection> A_Connect = new HashSet() {{
            add(A_B);
            add(D_A);
        }};
        Set<Connection> D_Connect = new HashSet() {{
            add(C_D);
            add(D_A);
        }};

        Map<String, scala.collection.immutable.Set<Connection>> topology = new HashMap<>();
        topology.put(C.alias(), Convert2ScalaUtil.toScalaImmutableSet(C_Connect));
        topology.put(B.alias(), Convert2ScalaUtil.toScalaImmutableSet(B_Connect));
        topology.put(A.alias(), Convert2ScalaUtil.toScalaImmutableSet(A_Connect));
        topology.put(D.alias(), Convert2ScalaUtil.toScalaImmutableSet(D_Connect));

        Pattern schema = new PartialGraphPattern(C.alias(),
                JavaConversions.mapAsScalaMap(ImmutableMap.of("A", A, "B", B, "C", C, "D", D)).toMap(
                        scala.Predef$.MODULE$.conforms()),
                Convert2ScalaUtil.toScalaImmutableMap(topology));
        return schema;
    }

    public static Pattern pattern3() {
        PatternElement A = new PatternElement("A", JavaConversions.asScalaSet(Sets.newHashSet("A")).toSet(), null);
        PatternElement B = new PatternElement("B", JavaConversions.asScalaSet(Sets.newHashSet("B")).toSet(), null);
        PatternElement C = new PatternElement("C", JavaConversions.asScalaSet(Sets.newHashSet("C")).toSet(), null);

        PatternConnection A_B = new PatternConnection("E1", A.alias(), null, B.alias(), Direction.OUT, null, -1, true, false);
        PatternConnection A_C = new PatternConnection("E2", A.alias(), null, C.alias(), Direction.OUT, null, -1, true, false);
        PatternConnection B_C = new PatternConnection("E3", B.alias(), null, C.alias(), Direction.OUT, null, -1, true, false);

        Set<Connection> C_Connect = new HashSet() {{
            add(B_C);
            add(A_C);
        }};
        Set<Connection> B_Connect = new HashSet() {{
            add(B_C);
            add(A_B);
        }};
        Set<Connection> A_Connect = new HashSet() {{
            add(A_B);
            add(A_C);
        }};
        Map<String, scala.collection.immutable.Set<Connection>> topology = new HashMap<>();
        topology.put(C.alias(), Convert2ScalaUtil.toScalaImmutableSet(C_Connect));
        topology.put(B.alias(), Convert2ScalaUtil.toScalaImmutableSet(B_Connect));
        topology.put(A.alias(), Convert2ScalaUtil.toScalaImmutableSet(A_Connect));

        Pattern schema = new PartialGraphPattern(A.alias(),
                JavaConversions.mapAsScalaMap(ImmutableMap.of("A", A, "B", B, "C", C)).toMap(
                        scala.Predef$.MODULE$.conforms()),
                Convert2ScalaUtil.toScalaImmutableMap(topology));
        return schema;
    }

    public static KgGraphImpl kgGraph1() {
        KgGraphImpl kgGraph = new KgGraphImpl();
        kgGraph.getAlias2VertexMap().put("A", Sets.newHashSet(
                new Vertex<>(IVertexId.from(11, DEFAULT_VERTEX_TYPE), null)
                , new Vertex<>(IVertexId.from(12, DEFAULT_VERTEX_TYPE), null)
        ));
        kgGraph.getAlias2VertexMap().put("B", Sets.newHashSet(
                new Vertex<>(IVertexId.from(21, DEFAULT_VERTEX_TYPE), null)
        ));
        kgGraph.getAlias2VertexMap().put("C", Sets.newHashSet(
                new Vertex<>(IVertexId.from(31, DEFAULT_VERTEX_TYPE), null)
        ));
        kgGraph.getAlias2VertexMap().put("D", Sets.newHashSet(
                new Vertex<>(IVertexId.from(41, DEFAULT_VERTEX_TYPE), null)
        ));

        kgGraph.getAlias2EdgeMap().put("E1", Sets.newHashSet(
                new Edge(IVertexId.from(11, DEFAULT_VERTEX_TYPE), IVertexId.from(21, DEFAULT_VERTEX_TYPE), null, 0L,
                        Direction.OUT, "E1T")
                , new Edge(IVertexId.from(12, DEFAULT_VERTEX_TYPE), IVertexId.from(21, DEFAULT_VERTEX_TYPE), null, 0L,
                        Direction.OUT, "E1T")
        ));

        kgGraph.getAlias2EdgeMap().put("E2", Sets.newHashSet(
                new Edge(IVertexId.from(21, DEFAULT_VERTEX_TYPE), IVertexId.from(31, DEFAULT_VERTEX_TYPE), null, 0L,
                        Direction.OUT, "E2T")
        ));

        kgGraph.getAlias2EdgeMap().put("E3", Sets.newHashSet(
                new Edge(IVertexId.from(31, DEFAULT_VERTEX_TYPE), IVertexId.from(41, DEFAULT_VERTEX_TYPE), null, 0L,
                        Direction.OUT, "E3T")
        ));
        return kgGraph;
    }

    public static KgGraphImpl kgGraph2() {
        KgGraphImpl kgGraph = new KgGraphImpl();
        kgGraph.getAlias2VertexMap().put("A", Sets.newHashSet(
                new Vertex<>(IVertexId.from(11, DEFAULT_VERTEX_TYPE), null)
        ));
        kgGraph.getAlias2VertexMap().put("B", Sets.newHashSet(
                new Vertex<>(IVertexId.from(21, DEFAULT_VERTEX_TYPE), null)
        ));
        kgGraph.getAlias2VertexMap().put("C", Sets.newHashSet(
                new Vertex<>(IVertexId.from(31, DEFAULT_VERTEX_TYPE), null)
        ));
        kgGraph.getAlias2VertexMap().put("D", Sets.newHashSet(
                new Vertex<>(IVertexId.from(41, DEFAULT_VERTEX_TYPE), null)
        ));

        kgGraph.getAlias2EdgeMap().put("E1", Sets.newHashSet(
                new Edge(IVertexId.from(11, DEFAULT_VERTEX_TYPE), IVertexId.from(21, DEFAULT_VERTEX_TYPE), null, 0L,
                        Direction.OUT, "E1T")
        ));

        kgGraph.getAlias2EdgeMap().put("E2", Sets.newHashSet(
                new Edge(IVertexId.from(21, DEFAULT_VERTEX_TYPE), IVertexId.from(31, DEFAULT_VERTEX_TYPE), null, 0L,
                        Direction.OUT, "E2T")
        ));

        kgGraph.getAlias2EdgeMap().put("E3", Sets.newHashSet(
                new Edge(IVertexId.from(31, DEFAULT_VERTEX_TYPE), IVertexId.from(41, DEFAULT_VERTEX_TYPE), null, 0L,
                        Direction.OUT, "E3T")
        ));
        kgGraph.getAlias2EdgeMap().put("E4", Sets.newHashSet(
                new Edge(IVertexId.from(41, DEFAULT_VERTEX_TYPE), IVertexId.from(11, DEFAULT_VERTEX_TYPE), null, 0L,
                        Direction.OUT, "E4T")
        ));

        return kgGraph;
    }

    public static KgGraphImpl kgGraph3() {
        KgGraphImpl kgGraph = new KgGraphImpl();
        kgGraph.getAlias2VertexMap().put("A", Sets.newHashSet(
                new Vertex<>(IVertexId.from(11, DEFAULT_VERTEX_TYPE), null)
        ));
        kgGraph.getAlias2VertexMap().put("B", Sets.newHashSet(
                new Vertex<>(IVertexId.from(21, DEFAULT_VERTEX_TYPE), null)
                , new Vertex<>(IVertexId.from(22, DEFAULT_VERTEX_TYPE), null)
        ));
        kgGraph.getAlias2VertexMap().put("C", Sets.newHashSet(
                new Vertex<>(IVertexId.from(31, DEFAULT_VERTEX_TYPE), null)
        ));

        kgGraph.getAlias2EdgeMap().put("E1", Sets.newHashSet(
                new Edge(IVertexId.from(11, DEFAULT_VERTEX_TYPE), IVertexId.from(21, DEFAULT_VERTEX_TYPE), null, 0L,
                        Direction.OUT, "E1T")
                , new Edge(IVertexId.from(11, DEFAULT_VERTEX_TYPE), IVertexId.from(22, DEFAULT_VERTEX_TYPE), null, 0L,
                        Direction.OUT, "E1T")
        ));

        kgGraph.getAlias2EdgeMap().put("E2", Sets.newHashSet(
                new Edge(IVertexId.from(11, DEFAULT_VERTEX_TYPE), IVertexId.from(31, DEFAULT_VERTEX_TYPE), null, 0L,
                        Direction.OUT, "E2T")
        ));

        kgGraph.getAlias2EdgeMap().put("E3", Sets.newHashSet(
                new Edge(IVertexId.from(21, DEFAULT_VERTEX_TYPE), IVertexId.from(31, DEFAULT_VERTEX_TYPE), null, 0L,
                        Direction.OUT, "E3T")
        ));
        return kgGraph;
    }

}