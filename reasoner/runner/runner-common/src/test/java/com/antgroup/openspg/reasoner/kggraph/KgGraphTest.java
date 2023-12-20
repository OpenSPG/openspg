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

package com.antgroup.openspg.reasoner.kggraph;

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.IVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.graphstate.LoadSourceData;
import com.antgroup.openspg.reasoner.graphstate.MockSourceData3;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphSplitStaticParameters;
import com.antgroup.openspg.reasoner.lube.common.pattern.*;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import scala.collection.JavaConversions;

public class KgGraphTest {

  private KgGraphImpl kgGraph = new KgGraphImpl();

  private LoadSourceData sourceData = new MockSourceData3();

  private Pattern schema;

  private static final String DEFAULT_VERTEX_TYPE = "t";

  @Before
  public void init() {
    kgGraph.setAlias2VertexMap(sourceData.loadAlias2Vertex());
    kgGraph.setAlias2EdgeMap(sourceData.loadAlias2Edge());
    schema = sourceData.loadPattern();
  }

  @Test
  public void testSplit() {
    // split on A
    Set<String> splitVertexAliasSet1 = Sets.newHashSet("A");
    List<KgGraph<IVertexId>> splitRst =
        kgGraph.split(
            splitVertexAliasSet1,
            schema,
            new KgGraphSplitStaticParameters(splitVertexAliasSet1, schema),
            null,
            null);
    Assert.assertTrue(splitRst.size() == kgGraph.getAlias2VertexMap().get("A").size());
    splitRst.stream()
        .map(kgGraph -> ((KgGraphImpl) kgGraph))
        .forEach(kgGraph -> Assert.assertTrue(kgGraph.getAlias2VertexMap().get("A").size() == 1));
    splitRst.stream().forEach(kgGraph -> kgGraph.show());

    // split on B
    Pattern schemaB =
        new PartialGraphPattern(
            "B",
            JavaConversions.mapAsScalaMap(
                    ImmutableMap.of(
                        "B",
                        schema.getNode("B"),
                        "A",
                        schema.getNode("A"),
                        "C",
                        schema.getNode("C")))
                .toMap(scala.Predef$.MODULE$.conforms()),
            schema.topology());
    Set<String> splitVertexAliasSet2 = Sets.newHashSet("B");
    splitRst =
        kgGraph.split(
            splitVertexAliasSet2,
            schemaB,
            new KgGraphSplitStaticParameters(splitVertexAliasSet2, schemaB),
            null,
            null);
    Assert.assertTrue(splitRst.size() == kgGraph.getAlias2VertexMap().get("B").size());
    splitRst.stream()
        .map(kgGraph -> ((KgGraphImpl) kgGraph))
        .forEach(kgGraph -> Assert.assertTrue(kgGraph.getAlias2VertexMap().get("B").size() == 1));
    splitRst.stream().forEach(kgGraph -> kgGraph.show());

    // split on C
    Pattern schemaC =
        new PartialGraphPattern(
            "C",
            JavaConversions.mapAsScalaMap(
                    ImmutableMap.of(
                        "A",
                        schema.getNode("A"),
                        "B",
                        schema.getNode("B"),
                        "C",
                        schema.getNode("C")))
                .toMap(scala.Predef$.MODULE$.conforms()),
            schema.topology());
    Set<String> splitVertexAliasSet3 = Sets.newHashSet("C");
    splitRst =
        kgGraph.split(
            splitVertexAliasSet3,
            schemaC,
            new KgGraphSplitStaticParameters(splitVertexAliasSet3, schemaC),
            null,
            null);
    Assert.assertTrue(splitRst.size() == kgGraph.getAlias2VertexMap().get("C").size());
    splitRst.stream()
        .map(kgGraph -> ((KgGraphImpl) kgGraph))
        .forEach(kgGraph -> Assert.assertTrue(kgGraph.getAlias2VertexMap().get("C").size() == 1));
    splitRst.stream().forEach(kgGraph -> kgGraph.show());
  }

  @Test
  public void testGetPath() {
    Iterator<KgGraph<IVertexId>> it =
        kgGraph.getPath(new KgGraphSplitStaticParameters(null, schema), null);
    int count = 0;
    while (it.hasNext()) {
      KgGraphImpl graph = ((KgGraphImpl) it.next());
      if (null != graph) {
        graph.show();
        count++;
        // one vertex alias has only one vertex
        for (String pe : JavaConversions.setAsJavaSet(schema.topology().keySet())) {
          Assert.assertTrue(graph.getAlias2VertexMap().get(pe).size() == 1);
        }

        for (String edgeAlias : graph.getAlias2EdgeMap().keySet()) {
          Assert.assertTrue(graph.getAlias2EdgeMap().get(edgeAlias).size() == 1);
        }
      }
    }
    Assert.assertTrue(count == 2);

    // schema is null
    try {
      it = kgGraph.getPath(null, null);
    } catch (NullPointerException e) {
      Assert.assertTrue(true);
    }
  }

  @Test
  public void testGetPathSingleVertex() {
    KgGraphImpl kgGraph1 = new KgGraphImpl();
    Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = new HashMap<>();
    IVertex<IVertexId, IProperty> v1 = new Vertex(IVertexId.from(1L, DEFAULT_VERTEX_TYPE));
    IVertex<IVertexId, IProperty> v2 = new Vertex(IVertexId.from(2L, DEFAULT_VERTEX_TYPE));
    IVertex<IVertexId, IProperty> v3 = new Vertex(IVertexId.from(3L, DEFAULT_VERTEX_TYPE));
    alias2VertexMap.put("A", Sets.newHashSet(v1, v2, v3));
    kgGraph1.setAlias2VertexMap(alias2VertexMap);

    PatternElement A = new PatternElement("A", null, null);
    Map<String, scala.collection.immutable.Set<Connection>> topology = new HashMap<>();
    Pattern schema1 =
        new PartialGraphPattern(
            A.alias(),
            JavaConversions.mapAsScalaMap(ImmutableMap.of("A", A))
                .toMap(scala.Predef$.MODULE$.conforms()),
            Convert2ScalaUtil.toScalaImmutableMap(topology));
    Iterator<KgGraph<IVertexId>> it =
        kgGraph1.getPath(new KgGraphSplitStaticParameters(null, schema1), null);
    int count = 0;
    while (it.hasNext()) {
      KgGraph<IVertexId> tmp = it.next();
      Assert.assertTrue(tmp.getVertex("A").size() == 1);
      count++;
    }
    Assert.assertTrue(count == 3);
  }

  @Test
  public void testPipeline() {
    // Schema of total data is B -> C <- A

    PatternElement C = new PatternElement("C", null, null);
    PatternElement B = new PatternElement("B", null, null);
    PatternElement A = new PatternElement("A", null, null);
    PatternConnection B_C =
        new PatternConnection(
            "BC", B.alias(), null, C.alias(), Direction.OUT, null, -1, true, false);
    PatternConnection A_C =
        new PatternConnection(
            "CA", C.alias(), null, A.alias(), Direction.IN, null, -1, true, false);
    Set<Connection> C_Connect =
        new HashSet() {
          {
            add(A_C);
          }
        };
    Set<Connection> B_Connect =
        new HashSet() {
          {
            add(B_C);
          }
        };
    Map<String, scala.collection.immutable.Set<Connection>> topology = new HashMap<>();
    topology.put(B.alias(), Convert2ScalaUtil.toScalaImmutableSet(B_Connect));
    topology.put(C.alias(), Convert2ScalaUtil.toScalaImmutableSet(B_Connect));
    // schema1 is B -> C
    Pattern schema1 =
        new PartialGraphPattern(
            B.alias(),
            JavaConversions.mapAsScalaMap(ImmutableMap.of("C", C, "B", B, "A", A))
                .toMap(scala.Predef$.MODULE$.conforms()),
            Convert2ScalaUtil.toScalaImmutableMap(topology));

    // patternScan on B
    IVertex<IVertexId, IProperty> b1 =
        new Vertex<>(IVertexId.from("b1", DEFAULT_VERTEX_TYPE), new VertexVersionProperty());
    Edge bc1 =
        new Edge(
            IVertexId.from("b1", DEFAULT_VERTEX_TYPE),
            IVertexId.from("c1", DEFAULT_VERTEX_TYPE),
            null,
            0L,
            Direction.OUT,
            "BC");
    Edge bc2 =
        new Edge(
            IVertexId.from("b1", DEFAULT_VERTEX_TYPE),
            IVertexId.from("c2", DEFAULT_VERTEX_TYPE),
            null,
            0L,
            Direction.OUT,
            "BC");
    Map<String, List<IEdge<IVertexId, IProperty>>> alias2EdgeMap = new HashMap<>();
    alias2EdgeMap.put(
        "BC",
        new ArrayList() {
          {
            add(bc1);
            add(bc2);
          }
        });

    KgGraphImpl kgGraph1 = new KgGraphImpl();
    kgGraph1.init(b1, alias2EdgeMap, schema1);
    Assert.assertTrue(kgGraph1.getAlias2VertexMap().get("B").size() == 1);
    Set<IVertex<IVertexId, IProperty>> virtualCSet = kgGraph1.getAlias2VertexMap().get("C");
    for (IVertex cVertex : virtualCSet) {
      // vertex C is virtual
      Assert.assertTrue(cVertex.getValue() == null);
    }
    Assert.assertTrue(kgGraph1.getAlias2EdgeMap().get("BC").size() == 2);

    // split on C
    schema1 =
        new PartialGraphPattern(
            C.alias(),
            JavaConversions.mapAsScalaMap(ImmutableMap.of("B", B, "C", C))
                .toMap(scala.Predef$.MODULE$.conforms()),
            Convert2ScalaUtil.toScalaImmutableMap(topology));
    Set<String> splitVertexAliasSet1 = Sets.newHashSet("C");
    List<KgGraph<IVertexId>> splitRst =
        kgGraph1.split(
            splitVertexAliasSet1,
            schema1,
            new KgGraphSplitStaticParameters(splitVertexAliasSet1, schema1),
            null,
            null);
    // splitRst.stream().forEach(g -> g.show());
    Assert.assertTrue(splitRst.size() == 2);
    KgGraphImpl c1Msg = ((KgGraphImpl) splitRst.get(0));
    KgGraphImpl c2Msg = ((KgGraphImpl) splitRst.get(1));

    // Omit shuffle to C and merge on C

    // schema2 is C <- A
    Map<String, scala.collection.immutable.Set<Connection>> topology2 = new HashMap<>();
    topology2.put(C.alias(), Convert2ScalaUtil.toScalaImmutableSet(C_Connect));
    topology2.put(A.alias(), Convert2ScalaUtil.toScalaImmutableSet(C_Connect));
    Pattern schema2 =
        new PartialGraphPattern(
            C.alias(),
            JavaConversions.mapAsScalaMap(ImmutableMap.of("C", C, "A", A))
                .toMap(scala.Predef$.MODULE$.conforms()),
            Convert2ScalaUtil.toScalaImmutableMap(topology2));

    // pattern scan C
    // worker1
    IVertex<IVertexId, IProperty> c1 =
        new Vertex<>(IVertexId.from("c1", DEFAULT_VERTEX_TYPE), new VertexVersionProperty());
    Edge ca1 =
        new Edge(
            IVertexId.from("c1", DEFAULT_VERTEX_TYPE),
            IVertexId.from("a1", DEFAULT_VERTEX_TYPE),
            null,
            0L,
            Direction.IN,
            "CA");
    Map<String, List<IEdge<IVertexId, IProperty>>> alias2EdgeMap2 = new HashMap<>();
    alias2EdgeMap2.put(
        "CA",
        new ArrayList() {
          {
            add(ca1);
          }
        });
    KgGraph<IVertexId> kgGraph2 = new KgGraphImpl();
    kgGraph2.init(c1, alias2EdgeMap2, schema2);
    System.out.println("c1 <- a1:");
    kgGraph2.show();

    // pattern scan C
    // worker2
    IVertex<IVertexId, IProperty> c2 =
        new Vertex<>(IVertexId.from("c2", DEFAULT_VERTEX_TYPE), new VertexVersionProperty());
    Edge ca2 =
        new Edge(
            IVertexId.from("c2", DEFAULT_VERTEX_TYPE),
            IVertexId.from("a1", DEFAULT_VERTEX_TYPE),
            null,
            0L,
            Direction.IN,
            "CA");
    Map<String, List<IEdge<IVertexId, IProperty>>> alias2EdgeMap3 = new HashMap<>();
    alias2EdgeMap3.put(
        "CA",
        new ArrayList() {
          {
            add(ca2);
          }
        });
    KgGraph<IVertexId> kgGraph3 = new KgGraphImpl();
    kgGraph3.init(c2, alias2EdgeMap3, schema2);
    System.out.println("c2 <- a1:");
    kgGraph3.show();

    // expand on C
    KgGraph<IVertexId> c1MsgExpandKgGraph = kgGraph2;
    KgGraph<IVertexId> c2MsgExpandKgGraph = kgGraph3;
    if (kgGraph2.getVertex("C").get(0).getId().getInternalId()
        != c1Msg.getVertex("C").get(0).getId().getInternalId()) {
      c1MsgExpandKgGraph = kgGraph3;
      c2MsgExpandKgGraph = kgGraph2;
    }
    c1Msg.expand(
        c1MsgExpandKgGraph,
        new PartialGraphPattern(
            "C",
            JavaConversions.mapAsScalaMap(ImmutableMap.of("C", C))
                .toMap(scala.Predef$.MODULE$.conforms()),
            null));
    c1Msg.show();
    Assert.assertTrue(c1Msg.getAlias2EdgeMap().containsKey("CA"));
    Assert.assertTrue(c1Msg.getVertex("C").size() == 1);
    // c1 is already a real vertex, not a virtual vertex
    Assert.assertTrue(c1Msg.getVertex("C").get(0).getValue() != null);
    // a1 is a virtual vertex
    Assert.assertTrue(c1Msg.getVertex("A").get(0).getValue() == null);

    c2Msg.expand(
        c2MsgExpandKgGraph,
        new PartialGraphPattern(
            "C",
            JavaConversions.mapAsScalaMap(ImmutableMap.of("C", C))
                .toMap(scala.Predef$.MODULE$.conforms()),
            null));
    c2Msg.show();

    // merge on A, there are two messages send to A
    KgGraph<IVertexId> kgGraph4 = new KgGraphImpl();
    List<KgGraph<IVertexId>> msgs = new ArrayList<>();
    msgs.add(c1Msg);
    msgs.add(c2Msg);
    kgGraph4.merge(msgs, new PartialGraphPattern(null, null, null));
    kgGraph4.show();
    Assert.assertTrue(kgGraph4.getVertex("C").size() == 2);
    Assert.assertTrue(kgGraph4.getVertex("B").size() == 1);
    Assert.assertTrue(kgGraph4.getEdge("CA").size() == 2);
    Assert.assertTrue(kgGraph4.getEdge("BC").size() == 2);
    // A is an virtual vertex
    Assert.assertTrue(kgGraph4.getVertex("A").get(0).getValue() == null);

    // schema3 has only one vertex and no edges
    Pattern schema3 =
        new PartialGraphPattern(
            A.alias(),
            JavaConversions.mapAsScalaMap(ImmutableMap.of("A", A))
                .toMap(scala.Predef$.MODULE$.conforms()),
            new scala.collection.immutable.HashMap<>());
    IVertex<IVertexId, IProperty> a1 =
        new Vertex<>(IVertexId.from("a1", DEFAULT_VERTEX_TYPE), new VertexVersionProperty());
    // pattern on A
    KgGraph<IVertexId> kgGraph5 = new KgGraphImpl();
    kgGraph5.init(a1, new HashMap<>(), schema3);

    // expand on A
    kgGraph4.expand(kgGraph5, schema3);
    kgGraph4.show();
    // A is a real vertex
    Assert.assertTrue(kgGraph5.getVertex("A").get(0).getValue() != null);
  }

  @Test
  public void testSetProperty() {
    Map<String, Object> propertyMap = new HashMap<>();
    propertyMap.put("newProp", 1);
    // set vertex property, specific version
    kgGraph.setVertexProperty("A", propertyMap, 1L);
    List<IVertex<IVertexId, IProperty>> aVertexSet = kgGraph.getVertex("A");
    for (IVertex<IVertexId, IProperty> vertex : aVertexSet) {
      Object val = ((IVersionProperty) vertex.getValue()).get("newProp", 1L);
      Assert.assertTrue(val.equals(1));
    }

    // set vertex property, not specific version
    kgGraph.setVertexProperty("A", propertyMap, null);
    aVertexSet = kgGraph.getVertex("A");
    for (IVertex<IVertexId, IProperty> vertex : aVertexSet) {
      Object val = ((IVersionProperty) vertex.getValue()).get("newProp");
      Assert.assertTrue(val.equals(1));
    }

    // set edge property
    kgGraph.setEdgeProperty("E1", propertyMap);
    List<IEdge<IVertexId, IProperty>> e1EdgeSet = kgGraph.getEdge("E1");
    for (IEdge<IVertexId, IProperty> edge : e1EdgeSet) {
      Assert.assertTrue(edge.getValue().get("newProp").equals(1));
    }

    try {
      // vertex alias not in kgGraph
      kgGraph.setVertexProperty("NOT_EXIST", propertyMap, 1L);

      // edge alias not in kgGraph
      kgGraph.setEdgeProperty("NOT_EXIST", propertyMap);
      Assert.assertTrue(false);
    } catch (Exception e) {
      Assert.assertTrue(true);
    }

    // delete vertex property
    propertyMap.put("newProp", null);
    kgGraph.setVertexProperty("A", propertyMap, null);
    aVertexSet = kgGraph.getVertex("A");
    for (IVertex<IVertexId, IProperty> vertex : aVertexSet) {
      Object val = ((IVersionProperty) vertex.getValue()).get("newProp");
      Assert.assertTrue(val == null);
    }

    // delete vertex
    kgGraph.setVertexProperty("A", null, null);
    aVertexSet = kgGraph.getVertex("A");
    for (IVertex<IVertexId, IProperty> vertex : aVertexSet) {
      Assert.assertTrue(vertex.getValue().getKeySet().isEmpty());
    }

    // delete edge property
    propertyMap.put("newProp", null);
    kgGraph.setEdgeProperty("E1", propertyMap);
    e1EdgeSet = kgGraph.getEdge("E1");
    for (IEdge<IVertexId, IProperty> edge : e1EdgeSet) {
      Assert.assertTrue(edge.getValue().get("newProp") == null);
    }

    // delete edge
    kgGraph.setEdgeProperty("E1", null);
    e1EdgeSet = kgGraph.getEdge("E1");
    for (IEdge<IVertexId, IProperty> edge : e1EdgeSet) {
      Assert.assertTrue(edge.getValue().getKeySet().isEmpty());
    }
  }
}
