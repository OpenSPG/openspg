/*
 * Copyright 2023 OpenSPG Authors
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

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphSplitStaticParameters;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SplitPathLimitTest {

  @Before
  public void init() {}

  @Test
  public void testSplitToWorkerIndexLimit() {
    Pattern schema = KgGraphSimpleSplitTest.pattern1();
    KgGraphImpl kgGraph = KgGraphSimpleSplitTest.kgGraph1();

    // without filter, do simple copy split
    // max path limit not effective
    Map<Integer, KgGraph<IVertexId>> splitMap =
        kgGraph.splitToWorkerIndex(
            "A",
            new IVertexId2WorkerIndex() {
              @Override
              public int workerIndex(IVertexId vertexId) {
                if (11L == vertexId.getInternalId()) {
                  return 0;
                }
                return 1;
              }
            },
            schema,
            new KgGraphSplitStaticParameters(Sets.newHashSet("A"), schema),
            null,
            1L);
    Assert.assertEquals(2, splitMap.size());

    // with filter, expand all paths
    // max path limit effective, will return 1 message
    splitMap =
        kgGraph.splitToWorkerIndex(
            "A",
            new IVertexId2WorkerIndex() {
              @Override
              public int workerIndex(IVertexId vertexId) {
                if (11L == vertexId.getInternalId()) {
                  return 0;
                }
                return 1;
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
            1L);
    Assert.assertEquals(1, splitMap.size());
  }

  @Test
  public void testSplitLimit() {
    Pattern schema = KgGraphSimpleSplitTest.pattern1();
    KgGraphImpl kgGraph = KgGraphSimpleSplitTest.kgGraph1();

    // without filter, do simple copy split
    // max path limit also effective, will return 1 message
    Set<String> splitVertexAliasSet = Sets.newHashSet("A");
    List<KgGraph<IVertexId>> splitResult =
        kgGraph.split(
            splitVertexAliasSet,
            schema,
            new KgGraphSplitStaticParameters(splitVertexAliasSet, schema),
            null,
            1L);
    Assert.assertEquals(1, splitResult.size());

    // with filter, max path limit effective, will return 1 message
    splitResult =
        kgGraph.split(
            splitVertexAliasSet,
            schema,
            new KgGraphSplitStaticParameters(splitVertexAliasSet, schema),
            new Predicate<KgGraph<IVertexId>>() {
              @Override
              public boolean test(KgGraph<IVertexId> iVertexIdKgGraph) {
                return true;
              }
            },
            1L);
    Assert.assertEquals(1, splitResult.size());
  }
}
