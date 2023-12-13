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

/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.graphstate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.lube.common.pattern.*;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.google.common.collect.ImmutableMap;
import scala.collection.JavaConversions;


public class MockSourceData2 extends LoadSourceData {
    /*
         a1  a2  a3  a4
          \  /    \  /
           b1      b2
            \      /
             \    /
               c1
     */

    /**
     * Load alias to vertex map from source data
     *
     * @return
     */
    @Override
    public Map<String, Set<IVertex<IVertexId, IProperty>>> loadAlias2Vertex() {
        Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = new HashMap<>();
        Vertex c1 = new Vertex<>(IVertexId.from("c1", "NULL"));
        Vertex b1 = new Vertex<>(IVertexId.from("b1", "NULL"));
        Vertex b2 = new Vertex<>(IVertexId.from("b2", "NULL"));
        Vertex a1 = new Vertex<>(IVertexId.from("a1", "NULL"));
        Vertex a2 = new Vertex<>(IVertexId.from("a2", "NULL"));
        Vertex a3 = new Vertex<>(IVertexId.from("a3", "NULL"));
        Vertex a4 = new Vertex<>(IVertexId.from("a4", "NULL"));
        alias2VertexMap.put("C", new HashSet() {{add(c1);}});
        alias2VertexMap.put("B", new HashSet() {{
            add(b1);
            add(b2);
        }});
        alias2VertexMap.put("A", new HashSet() {{
            add(a1);
            add(a2);
            add(a3);
            add(a4);
        }});
        return alias2VertexMap;
    }

    /**
     * Load alias to edge map from source data
     *
     * @return
     */
    @Override
    public Map<String, Set<IEdge<IVertexId, IProperty>>> loadAlias2Edge() {
        Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap = new HashMap<>();
        Edge bc1 = new Edge(IVertexId.from("b1", "NULL"), IVertexId.from("c1", "NULL"), null, 0L, Direction.OUT, "E");
        Edge bc2 = new Edge(IVertexId.from("b2", "NULL"), IVertexId.from("c1", "NULL"), null, 0L, Direction.OUT, "E");
        Edge ab1 = new Edge(IVertexId.from("a1", "NULL"), IVertexId.from("b1", "NULL"), null, 0L, Direction.OUT, "E");
        Edge ab2 = new Edge(IVertexId.from("a2", "NULL"), IVertexId.from("b1", "NULL"), null, 0L, Direction.OUT, "E");
        Edge ab3 = new Edge(IVertexId.from("a3", "NULL"), IVertexId.from("b2", "NULL"), null, 0L, Direction.OUT, "E");
        Edge ab4 = new Edge(IVertexId.from("a4", "NULL"), IVertexId.from("b2", "NULL"), null, 0L, Direction.OUT, "E");

        alias2EdgeMap.put("B_C", new HashSet() {{
            add(bc1);
            add(bc2);
        }});
        alias2EdgeMap.put("A_B", new HashSet() {{
            add(ab1);
            add(ab2);
            add(ab3);
            add(ab4);
        }});
        return alias2EdgeMap;
    }

    /**
     * Load source data schema
     *
     * @return
     */
    @Override
    public Pattern loadPattern() {
        PatternElement C = new PatternElement("C", null, null);
        PatternElement B = new PatternElement("B", null, null);
        PatternElement A = new PatternElement("A", null, null);

        PatternConnection B_C = new PatternConnection("B_C", B.alias(), null, C.alias(), Direction.OUT, null, -1, true, false);
        PatternConnection A_B = new PatternConnection("A_B", A.alias(), null, B.alias(), Direction.OUT, null, -1, true, false);
        Set<Connection> C_Connect = new HashSet() {{
            add(B_C);
        }};
        Set<Connection> B_Connect = new HashSet() {{
            add(B_C);
            add(A_B);
        }};
        Set<Connection> A_Connect = new HashSet() {{
            add(A_B);
        }};
        Map<String, scala.collection.immutable.Set<Connection>> topology = new HashMap<>();
        topology.put(C.alias(), Convert2ScalaUtil.toScalaImmutableSet(C_Connect));
        topology.put(B.alias(), Convert2ScalaUtil.toScalaImmutableSet(B_Connect));
        topology.put(A.alias(), Convert2ScalaUtil.toScalaImmutableSet(A_Connect));

        Pattern schema = new PartialGraphPattern(A.alias(),
                JavaConversions.mapAsScalaMap(ImmutableMap.of("A", A, "B", B, "C", C)).toMap(scala.Predef$.MODULE$.conforms()),
                Convert2ScalaUtil.toScalaImmutableMap(topology));
        return schema;
    }

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
        return null;
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
        return null;
    }
}