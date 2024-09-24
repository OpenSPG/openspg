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

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import com.antgroup.openspg.reasoner.thinker.catalog.ResourceLogicCatalog;
import com.antgroup.openspg.reasoner.thinker.engine.DefaultThinker;
import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Node;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Predicate;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class ProofWriterTest {
  private GraphState<IVertexId> buildGraphState() {
    GraphState<IVertexId> graphState = new MemGraphState();
    return graphState;
  }

  private Thinker buildThinker(String fileName) {
    ResourceLogicCatalog logicCatalog = new ResourceLogicCatalog(fileName);
    logicCatalog.init();
    Thinker thinker = new DefaultThinker(buildGraphState(), logicCatalog);
    return thinker;
  }

  private Triple makeTriple(String sType, String rType, String oType) {
    return new Triple(new Node(sType), new Predicate(rType), new Node(oType));
  }

  private Triple makeTriple(String id, String sType, String rType, String oType) {
    return new Triple(new Entity(id, sType), new Predicate(rType), new Node(oType));
  }

  private Triple makeTriple(String id, String sType, String rType, String oId, String oType) {
    return new Triple(new Entity(id, sType), new Predicate(rType), new Entity(oId, oType));
  }

  @Test
  public void testCase1() {
    Thinker thinker = buildThinker("/ProofWriter.txt");
    Map<String, Object> context = new HashMap<>();
    Triple t1 = makeTriple("cow", "iss", "round");
    Triple t2 = makeTriple("lion", "iss", "round");
    Triple t3 = makeTriple("rabbit", "iss", "kind");
    Triple t4 = makeTriple("tiger", "iss", "big");
    Triple t5 = makeTriple("tiger", "iss", "kind");

    Triple t6 = makeTriple("cow", "needs", "lion");
    Triple t7 = makeTriple("cow", "needs", "rabbit");
    Triple t8 = makeTriple("cow", "sees", "lion");
    Triple t9 = makeTriple("cow", "visits", "tiger");
    Triple t10 = makeTriple("rabbit", "visits", "tiger");
    Triple t11 = makeTriple("tiger", "sees", "rabbit");
    Triple t12 = makeTriple("tiger", "visits", "rabbit");
    List<Triple> triples = Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12);
    triples.forEach(t -> context.put(t.toString(), t));
    List<Result> result =
        thinker.find(new Node("tiger"), new Predicate("iss"), new Node("young"), context);
    Assert.assertTrue(result.size() == 1);
  }

  @Test
  public void testCase2() {
    Thinker thinker = buildThinker("/ProofWriter2.txt");
    Map<String, Object> context = new HashMap<>();
    Triple t1 = makeTriple("Bob", "Thing", "iss", "big");
    Triple t2 = makeTriple("Bob", "Thing", "iss", "nice");
    Triple t3 = makeTriple("Bob", "Thing", "iss", "smart");
    Triple t4 = makeTriple("Charlie", "Thing", "iss", "nice");
    Triple t5 = makeTriple("Dave", "Thing", "iss", "nice");
    Triple t6 = makeTriple("Erin", "Thing", "iss", "big");
    Triple t7 = makeTriple("Erin", "Thing", "iss", "blue");
    Triple t8 = makeTriple("Erin", "Thing", "iss", "furry");
    Triple t9 = makeTriple("Erin", "Thing", "iss", "quiet");
    Triple t10 = makeTriple("Erin", "Thing", "iss", "round");
    Triple t11 = makeTriple("Erin", "Thing", "iss", "smart");
    List<Triple> triples = Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    triples.forEach(t -> context.put(t.toString(), t));
    List<Result> result =
        thinker.find(
            new Entity("Charlie", "Thing"), new Predicate("iss"), new Node("blue"), context);
    Assert.assertTrue(result.size() == 1);
  }

  @Test
  public void testCase3() {
    Thinker thinker = buildThinker("/ProofWriter3.txt");
    Map<String, Object> context = new HashMap<>();
    Triple t1 = makeTriple("Anne", "Thing", "iss", "kind");
    Triple t2 = makeTriple("Anne", "Thing", "iss", "quiet");
    Triple t3 = makeTriple("Anne", "Thing", "iss", "smart");
    Triple t4 = makeTriple("Bob", "Thing", "iss", "kind");
    Triple t5 = makeTriple("Bob", "Thing", "iss", "nice");
    Triple t6 = makeTriple("Bob", "Thing", "iss", "smart");
    Triple t7 = makeTriple("Fiona", "Thing", "iss", "red");
    Triple t8 = makeTriple("Gary", "Thing", "iss", "nice");
    Triple t9 = makeTriple("Gary", "Thing", "iss", "white");
    List<Triple> triples = Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8, t9);
    triples.forEach(t -> context.put(t.toString(), t));
    List<Result> result =
        thinker.find(
            new Entity("Fiona", "Thing"), new Predicate("iss"), new Node("smart"), context);
    Assert.assertTrue(result.size() == 1);
  }

  @Test
  public void testCase4() {
    Thinker thinker = buildThinker("/ProofWriter4.txt");
    Map<String, Object> context = new HashMap<>();
    Triple t1 = makeTriple("bald eagle", "Thing", "eats", "cow", "Thing");
    Triple t2 = makeTriple("bald eagle", "Thing", "iss", "young");
    Triple t3 = makeTriple("cow", "Thing", "iss", "nice");
    Triple t4 = makeTriple("cow", "Thing", "sees", "bald eagle", "Thing");
    Triple t5 = makeTriple("dog", "Thing", "eats", "bald eagle", "Thing");
    Triple t6 = makeTriple("tiger", "Thing", "eats", "cow", "Thing");
    Triple t7 = makeTriple("tiger", "Thing", "needs", "bald eagle", "Thing");
    List<Triple> triples = Arrays.asList(t1, t2, t3, t4, t5, t6, t7);
    triples.forEach(t -> context.put(t.toString(), t));
    List<Result> result =
        thinker.find(
            new Entity("tiger", "Thing"), new Predicate("iss"), new Node("young"), context);
    Assert.assertTrue(result.size() == 1);
  }
}
