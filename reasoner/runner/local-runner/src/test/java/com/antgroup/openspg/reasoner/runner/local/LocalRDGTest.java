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

package com.antgroup.openspg.reasoner.runner.local;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.lube.common.pattern.*;
import com.antgroup.openspg.reasoner.lube.common.rule.LogicRule;
import com.antgroup.openspg.reasoner.parser.expr.RuleExprParser;
import com.antgroup.openspg.reasoner.runner.local.impl.LocalRunnerThreadPool;
import com.antgroup.openspg.reasoner.runner.local.rdg.LocalRDG;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import scala.collection.JavaConversions;

public class LocalRDGTest {

  @Test
  public void localRDGTest() {
    LocalRDG localRDG =
        new LocalRDG(
            new MemGraphState(),
            new ArrayList<>(),
            LocalRunnerThreadPool.getThreadPoolExecutor(null),
            1000,
            "",
            "",
            null);
    localRDG.limit(1);
    localRDG.show(10);
  }

  @Test
  public void testFilter() throws Exception {
    LocalRDG localRDG =
        new LocalRDG(
            new MemGraphState(),
            new ArrayList<>(),
            LocalRunnerThreadPool.getThreadPoolExecutor(null),
            1000,
            "",
            "",
            null);

    List<KgGraph<IVertexId>> kgGraphList = new ArrayList<>();
    Map<String, Set<IVertex<IVertexId, IProperty>>> alias2VertexMap = new HashMap<>();
    alias2VertexMap.put(
        "A",
        Sets.newHashSet(
            new Vertex<>(IVertexId.from(0L, "type1")), new Vertex<>(IVertexId.from(1L, "type1"))));
    Map<String, Set<IEdge<IVertexId, IProperty>>> alias2EdgeMap = new HashMap<>();
    KgGraphImpl kgGraph = new KgGraphImpl(alias2VertexMap, alias2EdgeMap);
    kgGraphList.add(kgGraph);

    Field kgGraphField = LocalRDG.class.getDeclaredField("kgGraphList");
    kgGraphField.setAccessible(true);
    kgGraphField.set(localRDG, kgGraphList);

    Field schemaField = LocalRDG.class.getDeclaredField("kgGraphSchema");
    schemaField.setAccessible(true);
    schemaField.set(localRDG, loadPattern());

    localRDG.setMaxPathLimit(1L);
    RuleExprParser ruleParser = new RuleExprParser();
    localRDG.filter(new LogicRule("R1", "", ruleParser.parse("19 > 18")));

    List<KgGraph<IVertexId>> kgGraphListAfterFilter =
        (List<KgGraph<IVertexId>>) kgGraphField.get(localRDG);
    Assert.assertEquals(1, kgGraphListAfterFilter.size());
  }

  public Pattern loadPattern() {
    PatternElement A = new PatternElement("A", null, null);
    Map<String, scala.collection.immutable.Set<Connection>> topology = new HashMap<>();
    Pattern schema =
        new PartialGraphPattern(
            A.alias(),
            JavaConversions.mapAsScalaMap(ImmutableMap.of("A", A))
                .toMap(scala.Predef$.MODULE$.conforms()),
            Convert2ScalaUtil.toScalaImmutableMap(topology));
    return schema;
  }
}
