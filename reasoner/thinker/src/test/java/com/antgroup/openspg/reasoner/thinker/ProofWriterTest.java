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

package com.antgroup.openspg.reasoner.thinker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import org.junit.Assert;
import org.junit.Test;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import com.antgroup.openspg.reasoner.thinker.catalog.ResourceLogicCatalog;
import com.antgroup.openspg.reasoner.thinker.engine.DefaultThinker;
import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Node;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Predicate;

public class ProofWriterTest {
  private GraphState<IVertexId> buildGraphState() {
    GraphState<IVertexId> graphState = new MemGraphState();
    return graphState;
  }

  private Thinker buildThinker() {
    ResourceLogicCatalog logicCatalog = new ResourceLogicCatalog("/ProofWriter.txt");
    logicCatalog.init();
    Thinker thinker = new DefaultThinker(buildGraphState(), logicCatalog);
    return thinker;
  }

  private Triple makeTriple(String sType, String rType, String oType) {
    return new Triple(new Node(sType), new Predicate(rType), new Node(oType));
  }

  @Test
  public void testCase1() {
    Thinker thinker = buildThinker();
    Map<String, Object> context = new HashMap<>();
    Triple t1 = makeTriple("cow", "iss", "round");
    Triple t2 = makeTriple("lion", "iss", "round");
    Triple t3 = makeTriple("rabbit", "iss", "kind");
    Triple t4 = makeTriple("tiger", "iss", "big");
    Triple t5 = makeTriple("tiger", "iss", "round");

    Triple t6 = makeTriple("cow", "needs", "lion");
    Triple t7 = makeTriple("cow", "needs", "rabbit");
    Triple t8 = makeTriple("cow", "sees", "lion");
    Triple t9 = makeTriple("cow", "visits", "tiger");
    Triple t10 = makeTriple("rabbit", "visits", "tiger");
    Triple t11 = makeTriple("tiger", "sees", "rabbit");
    Triple t12 = makeTriple("tiger", "visits", "rabbit");
    List<Triple> triples = Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12);
    triples.forEach(t -> context.put(t.toString(), t));
    List<Result> result = thinker.find(new Node("tiger"), new Predicate("iss"), new Node("young"), context);
    Assert.assertTrue(result.size() == 2);
  }
}
