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

import static com.antgroup.openspg.reasoner.kggraph.KgGraphSimpleSplitTest.DEFAULT_VERTEX_TYPE;

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphSplitStaticParameters;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SplitToWorkerIndexTest {

  @Before
  public void init() {}

  @Test
  public void testSimpleSplit() {
    Pattern schema = KgGraphSimpleSplitTest.pattern1();
    KgGraph<IVertexId> kgGraph = KgGraphSimpleSplitTest.kgGraph1();

    Map<Integer, KgGraph<IVertexId>> splitMap =
        kgGraph.splitToWorkerIndex(
            "A",
            new IVertexId2WorkerIndex() {
              @Override
              public int workerIndex(IVertexId vertexId) {
                if (11L == vertexId.getInternalId()) {
                  return 0;
                } else {
                  return 1;
                }
              }
            },
            schema,
            new KgGraphSplitStaticParameters(Sets.newHashSet("A"), schema),
            null,
            null);

    Assert.assertEquals(2, splitMap.size());
    Assert.assertEquals(11L, splitMap.get(0).getVertex("A").get(0).getId().getInternalId());
    Assert.assertEquals(12L, splitMap.get(1).getVertex("A").get(0).getId().getInternalId());
  }

  @Test
  public void testSimpleSplit2() {
    Pattern schema = KgGraphSimpleSplitTest.pattern1();
    KgGraph<IVertexId> kgGraph = KgGraphSimpleSplitTest.kgGraph1();

    Map<Integer, KgGraph<IVertexId>> splitMap =
        kgGraph.splitToWorkerIndex(
            "A",
            new IVertexId2WorkerIndex() {
              @Override
              public int workerIndex(IVertexId vertexId) {
                return 0;
              }
            },
            schema,
            new KgGraphSplitStaticParameters(Sets.newHashSet("A"), schema),
            null,
            null);

    Assert.assertEquals(1, splitMap.size());
    Assert.assertEquals(2, splitMap.get(0).getVertex("A").size());
    Assert.assertEquals(2, splitMap.get(0).getEdge("E1").size());

    int countPath = 0;
    Iterator<KgGraph<IVertexId>> pathIt =
        splitMap.get(0).getPath(new KgGraphSplitStaticParameters(null, schema), null);
    while (pathIt.hasNext()) {
      KgGraph<IVertexId> path = pathIt.next();
      if (null == path) {
        continue;
      }
      countPath++;
    }
    Assert.assertEquals(2, countPath);
  }

  @Test
  public void testSplit3() {
    Pattern schema = KgGraphSimpleSplitTest.pattern2();
    KgGraph<IVertexId> kgGraph = KgGraphSimpleSplitTest.kgGraph2();

    Map<Integer, KgGraph<IVertexId>> splitMap =
        kgGraph.splitToWorkerIndex(
            "C",
            new IVertexId2WorkerIndex() {
              @Override
              public int workerIndex(IVertexId vertexId) {
                return 0;
              }
            },
            schema,
            new KgGraphSplitStaticParameters(Sets.newHashSet("C"), schema),
            null,
            null);

    Assert.assertEquals(1, splitMap.size());
    Assert.assertEquals(1, splitMap.get(0).getVertex("A").size());
    Assert.assertEquals(1, splitMap.get(0).getEdge("E1").size());

    int countPath = 0;
    Iterator<KgGraph<IVertexId>> pathIt =
        splitMap.get(0).getPath(new KgGraphSplitStaticParameters(null, schema), null);
    while (pathIt.hasNext()) {
      KgGraph<IVertexId> path = pathIt.next();
      if (null == path) {
        continue;
      }
      countPath++;
    }
    Assert.assertEquals(1, countPath);
  }

  @Test
  public void testSimpleSplit4() {
    Pattern schema = KgGraphSimpleSplitTest.pattern2();
    KgGraphImpl kgGraph = KgGraphSimpleSplitTest.kgGraph2();

    kgGraph
        .getAlias2VertexMap()
        .get("B")
        .add(new Vertex<>(IVertexId.from(22, DEFAULT_VERTEX_TYPE), null));

    kgGraph
        .getAlias2EdgeMap()
        .get("E1")
        .add(
            new Edge(
                IVertexId.from(11, DEFAULT_VERTEX_TYPE),
                IVertexId.from(22, DEFAULT_VERTEX_TYPE),
                null,
                0L,
                Direction.OUT,
                "E1T"));
    kgGraph
        .getAlias2EdgeMap()
        .get("E2")
        .add(
            new Edge(
                IVertexId.from(22, DEFAULT_VERTEX_TYPE),
                IVertexId.from(31, DEFAULT_VERTEX_TYPE),
                null,
                0L,
                Direction.OUT,
                "E2T"));

    kgGraph
        .getAlias2VertexMap()
        .get("D")
        .add(new Vertex<>(IVertexId.from(42, DEFAULT_VERTEX_TYPE), null));

    kgGraph
        .getAlias2EdgeMap()
        .get("E3")
        .add(
            new Edge(
                IVertexId.from(31, DEFAULT_VERTEX_TYPE),
                IVertexId.from(42, DEFAULT_VERTEX_TYPE),
                null,
                0L,
                Direction.OUT,
                "E3T"));
    kgGraph
        .getAlias2EdgeMap()
        .get("E4")
        .add(
            new Edge(
                IVertexId.from(42, DEFAULT_VERTEX_TYPE),
                IVertexId.from(11, DEFAULT_VERTEX_TYPE),
                null,
                0L,
                Direction.OUT,
                "E4T"));

    Map<Integer, KgGraph<IVertexId>> splitMap =
        kgGraph.splitToWorkerIndex(
            "B",
            new IVertexId2WorkerIndex() {
              @Override
              public int workerIndex(IVertexId vertexId) {
                if (21L == vertexId.getInternalId()) {
                  return 0;
                } else {
                  return 1;
                }
              }
            },
            schema,
            new KgGraphSplitStaticParameters(Sets.newHashSet("C"), schema),
            null,
            null);

    Assert.assertEquals(2, splitMap.size());
    splitMap.forEach(
        new BiConsumer<Integer, KgGraph<IVertexId>>() {
          @Override
          public void accept(Integer integer, KgGraph<IVertexId> iVertexIdKgGraph) {
            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("B").size());
            Assert.assertEquals(2, iVertexIdKgGraph.getVertex("D").size());
            Assert.assertEquals(2, iVertexIdKgGraph.getEdge("E3").size());
            Assert.assertEquals(2, iVertexIdKgGraph.getEdge("E4").size());

            int countPath = 0;
            Iterator<KgGraph<IVertexId>> pathIt =
                iVertexIdKgGraph.getPath(new KgGraphSplitStaticParameters(null, schema), null);
            while (pathIt.hasNext()) {
              KgGraph<IVertexId> path = pathIt.next();
              if (null == path) {
                continue;
              }
              countPath++;
            }
            Assert.assertEquals(2, countPath);
          }
        });

    Map<Integer, KgGraph<IVertexId>> splitMap2 =
        kgGraph.splitToWorkerIndex(
            "D",
            new IVertexId2WorkerIndex() {
              @Override
              public int workerIndex(IVertexId vertexId) {
                if (41L == vertexId.getInternalId()) {
                  return 0;
                } else {
                  return 1;
                }
              }
            },
            schema,
            new KgGraphSplitStaticParameters(Sets.newHashSet("D"), schema),
            null,
            null);

    Assert.assertEquals(2, splitMap2.size());
    splitMap2.forEach(
        new BiConsumer<Integer, KgGraph<IVertexId>>() {
          @Override
          public void accept(Integer integer, KgGraph<IVertexId> iVertexIdKgGraph) {

            Assert.assertEquals(1, iVertexIdKgGraph.getVertex("D").size());
            Assert.assertEquals(2, iVertexIdKgGraph.getVertex("B").size());
            Assert.assertEquals(2, iVertexIdKgGraph.getEdge("E1").size());
            Assert.assertEquals(2, iVertexIdKgGraph.getEdge("E2").size());

            int countPath = 0;
            Iterator<KgGraph<IVertexId>> pathIt =
                iVertexIdKgGraph.getPath(new KgGraphSplitStaticParameters(null, schema), null);
            while (pathIt.hasNext()) {
              KgGraph<IVertexId> path = pathIt.next();
              if (null == path) {
                continue;
              }
              countPath++;
            }
            Assert.assertEquals(2, countPath);
          }
        });
  }

  @Test
  public void testSplit5() {
    Pattern schema = KgGraphSimpleSplitTest.pattern3();
    KgGraphImpl kgGraph = KgGraphSimpleSplitTest.kgGraph3();

    Map<Integer, KgGraph<IVertexId>> splitMap =
        kgGraph.splitToWorkerIndex(
            "A",
            new IVertexId2WorkerIndex() {
              @Override
              public int workerIndex(IVertexId vertexId) {
                return 0;
              }
            },
            schema,
            new KgGraphSplitStaticParameters(Sets.newHashSet("A"), schema),
            new Predicate<KgGraph<IVertexId>>() {
              @Override
              public boolean test(KgGraph<IVertexId> iVertexIdKgGraph) {
                return true;
              }
            },
            null);
    Assert.assertEquals(1, splitMap.size());
  }

  @Test
  public void testSplit6() {
    Pattern schema = KgGraphSimpleSplitTest.pattern3();
    KgGraphImpl kgGraph = KgGraphSimpleSplitTest.kgGraph3();

    Map<Integer, KgGraph<IVertexId>> splitMap =
        kgGraph.splitToWorkerIndex(
            "B",
            new IVertexId2WorkerIndex() {
              @Override
              public int workerIndex(IVertexId vertexId) {
                if (21L == vertexId.getInternalId()) {
                  return 0;
                }
                return 1;
              }
            },
            schema,
            new KgGraphSplitStaticParameters(Sets.newHashSet("B"), schema),
            new Predicate<KgGraph<IVertexId>>() {
              @Override
              public boolean test(KgGraph<IVertexId> iVertexIdKgGraph) {
                return true;
              }
            },
            null);
    Assert.assertEquals(1, splitMap.size());

    kgGraph
        .getAlias2EdgeMap()
        .get("E3")
        .add(
            new Edge(
                IVertexId.from(22, DEFAULT_VERTEX_TYPE),
                IVertexId.from(31, DEFAULT_VERTEX_TYPE),
                null,
                0L,
                Direction.OUT,
                "E3T"));

    Map<Integer, KgGraph<IVertexId>> splitMap2 =
        kgGraph.splitToWorkerIndex(
            "B",
            new IVertexId2WorkerIndex() {
              @Override
              public int workerIndex(IVertexId vertexId) {
                if (21L == vertexId.getInternalId()) {
                  return 0;
                }
                return 1;
              }
            },
            schema,
            new KgGraphSplitStaticParameters(Sets.newHashSet("B"), schema),
            new Predicate<KgGraph<IVertexId>>() {
              @Override
              public boolean test(KgGraph<IVertexId> iVertexIdKgGraph) {
                return true;
              }
            },
            null);
    Assert.assertEquals(2, splitMap2.size());

    kgGraph
        .getAlias2VertexMap()
        .get("B")
        .add(new Vertex<>(IVertexId.from(23, DEFAULT_VERTEX_TYPE), null));
    kgGraph
        .getAlias2EdgeMap()
        .get("E3")
        .add(
            new Edge(
                IVertexId.from(23, DEFAULT_VERTEX_TYPE),
                IVertexId.from(31, DEFAULT_VERTEX_TYPE),
                null,
                0L,
                Direction.OUT,
                "E3T"));

    kgGraph
        .getAlias2EdgeMap()
        .get("E1")
        .add(
            new Edge(
                IVertexId.from(11, DEFAULT_VERTEX_TYPE),
                IVertexId.from(23, DEFAULT_VERTEX_TYPE),
                null,
                0L,
                Direction.OUT,
                "E1T"));

    Map<Integer, KgGraph<IVertexId>> splitMap3 =
        kgGraph.splitToWorkerIndex(
            "B",
            new IVertexId2WorkerIndex() {
              @Override
              public int workerIndex(IVertexId vertexId) {
                if (21L == vertexId.getInternalId()) {
                  return 0;
                }
                return 1;
              }
            },
            schema,
            new KgGraphSplitStaticParameters(Sets.newHashSet("B"), schema),
            new Predicate<KgGraph<IVertexId>>() {
              @Override
              public boolean test(KgGraph<IVertexId> iVertexIdKgGraph) {
                return true;
              }
            },
            null);
    Assert.assertEquals(2, splitMap3.size());
    Assert.assertEquals(1, splitMap3.get(0).getVertex("B").size());
    Assert.assertEquals(2, splitMap3.get(1).getVertex("B").size());

    Map<Integer, KgGraph<IVertexId>> splitMap4 =
        kgGraph.splitToWorkerIndex(
            "B",
            new IVertexId2WorkerIndex() {
              @Override
              public int workerIndex(IVertexId vertexId) {
                if (21L == vertexId.getInternalId()) {
                  return 0;
                }
                return 1;
              }
            },
            schema,
            new KgGraphSplitStaticParameters(Sets.newHashSet("B"), schema),
            null,
            null);
    Assert.assertEquals(2, splitMap4.size());
    Assert.assertEquals(1, splitMap4.get(0).getVertex("B").size());
    Assert.assertEquals(2, splitMap4.get(1).getVertex("B").size());
  }
}
