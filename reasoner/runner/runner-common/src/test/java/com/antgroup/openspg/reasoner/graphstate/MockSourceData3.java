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

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.IVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.EdgeProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.lube.common.pattern.*;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import scala.collection.JavaConversions;

public class MockSourceData3 extends LoadSourceData {

  private static final String DEFAULT_VERTEX_TYPE = "t";

  @Override
  public List<IVertex<String, IProperty>> genVertexList() {
    return null;
  }

  @Override
  public List<IEdge<String, IProperty>> genEdgeList() {
    return null;
  }

  /**
   * Load alias to vertex map from source data
   *
   * @return
   */
  @Override
  public Map<String, Set<IVertex<IVertexId, IProperty>>> loadAlias2Vertex() {
    {
      Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = new HashMap<>();
      IVersionProperty property1 = new VertexVersionProperty();
      Vertex c1 = new Vertex<>(IVertexId.from("c1", DEFAULT_VERTEX_TYPE), property1);
      IVersionProperty property2 = new VertexVersionProperty();
      Vertex c2 = new Vertex<>(IVertexId.from("c2", DEFAULT_VERTEX_TYPE), property2);
      IVersionProperty property3 = new VertexVersionProperty();
      Vertex b1 = new Vertex<>(IVertexId.from("b1", DEFAULT_VERTEX_TYPE), property3);
      IVersionProperty property4 = new VertexVersionProperty();
      Vertex a1 = new Vertex<>(IVertexId.from("a1", DEFAULT_VERTEX_TYPE), property4);
      IVersionProperty property5 = new VertexVersionProperty();
      Vertex a2 = new Vertex<>(IVertexId.from("a2", DEFAULT_VERTEX_TYPE), property5);

      alias2VertexMap.put(
          "C",
          new HashSet() {
            {
              add(c1);
              add(c2);
            }
          });
      alias2VertexMap.put(
          "B",
          new HashSet() {
            {
              add(b1);
            }
          });
      alias2VertexMap.put(
          "A",
          new HashSet() {
            {
              add(a1);
              add(a2);
            }
          });
      return alias2VertexMap;
    }
  }

  /**
   * Load alias to edge map from source data
   *
   * @return
   */
  @Override
  public Map<String, Set<IEdge<IVertexId, IProperty>>> loadAlias2Edge() {
    Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap = new HashMap<>();
    IProperty property1 = new EdgeProperty();
    Edge ab1 =
        new Edge(
            IVertexId.from("a1", DEFAULT_VERTEX_TYPE),
            IVertexId.from("b1", DEFAULT_VERTEX_TYPE),
            property1,
            0L,
            Direction.OUT,
            "directFilm");
    IProperty property2 = new EdgeProperty();
    Edge ab2 =
        new Edge(
            IVertexId.from("a2", DEFAULT_VERTEX_TYPE),
            IVertexId.from("b1", DEFAULT_VERTEX_TYPE),
            property2,
            0L,
            Direction.OUT,
            "directFilm");
    IProperty property3 = new EdgeProperty();
    Edge bc1 =
        new Edge(
            IVertexId.from("b1", DEFAULT_VERTEX_TYPE),
            IVertexId.from("c1", DEFAULT_VERTEX_TYPE),
            property3,
            0L,
            Direction.OUT,
            "workmates");
    IProperty property4 = new EdgeProperty();
    Edge bc2 =
        new Edge(
            IVertexId.from("b1", DEFAULT_VERTEX_TYPE),
            IVertexId.from("c2", DEFAULT_VERTEX_TYPE),
            property4,
            0L,
            Direction.OUT,
            "workmates");
    IProperty property5 = new EdgeProperty();
    Edge ac1 =
        new Edge(
            IVertexId.from("a1", DEFAULT_VERTEX_TYPE),
            IVertexId.from("c2", DEFAULT_VERTEX_TYPE),
            property5,
            0L,
            Direction.OUT,
            "writerOfFilm");
    IProperty property6 = new EdgeProperty();
    Edge ac2 =
        new Edge(
            IVertexId.from("a2", DEFAULT_VERTEX_TYPE),
            IVertexId.from("c1", DEFAULT_VERTEX_TYPE),
            property6,
            0L,
            Direction.OUT,
            "writerOfFilm");

    alias2EdgeMap.put(
        "E1",
        new HashSet() {
          {
            add(ab1);
            add(ab2);
          }
        });
    alias2EdgeMap.put(
        "E2",
        new HashSet() {
          {
            add(ac1);
            add(ac2);
          }
        });
    alias2EdgeMap.put(
        "E3",
        new HashSet() {
          {
            add(bc1);
            add(bc2);
          }
        });

    return alias2EdgeMap;
  }

  /**
   * Load source data schema
   *
   * @return
   */
  @Override
  public Pattern loadPattern() {
    PatternElement C =
        new PatternElement("C", JavaConversions.asScalaSet(Sets.newHashSet("C")).toSet(), null);
    PatternElement B =
        new PatternElement("B", JavaConversions.asScalaSet(Sets.newHashSet("B")).toSet(), null);
    PatternElement A =
        new PatternElement("A", JavaConversions.asScalaSet(Sets.newHashSet("A")).toSet(), null);

    PatternConnection B_C =
        new PatternConnection(
            "E3", B.alias(), null, C.alias(), Direction.OUT, null, null, true, false);
    PatternConnection A_B =
        new PatternConnection(
            "E1", A.alias(), null, B.alias(), Direction.OUT, null, null, true, false);
    PatternConnection A_C =
        new PatternConnection(
            "E2", A.alias(), null, C.alias(), Direction.OUT, null, null, true, false);

    Set<Connection> C_Connect =
        new HashSet() {
          {
            add(B_C);
            add(A_C);
          }
        };
    Set<Connection> B_Connect =
        new HashSet() {
          {
            add(B_C);
            add(A_B);
          }
        };
    Set<Connection> A_Connect =
        new HashSet() {
          {
            add(A_B);
            add(A_C);
          }
        };
    Map<String, scala.collection.immutable.Set<Connection>> topology = new HashMap<>();
    topology.put(C.alias(), Convert2ScalaUtil.toScalaImmutableSet(C_Connect));
    topology.put(B.alias(), Convert2ScalaUtil.toScalaImmutableSet(B_Connect));
    topology.put(A.alias(), Convert2ScalaUtil.toScalaImmutableSet(A_Connect));

    Pattern schema =
        new PartialGraphPattern(
            A.alias(),
            JavaConversions.mapAsScalaMap(ImmutableMap.of("A", A, "B", B, "C", C))
                .toMap(scala.Predef$.MODULE$.conforms()),
            Convert2ScalaUtil.toScalaImmutableMap(topology));
    return schema;
  }
}
