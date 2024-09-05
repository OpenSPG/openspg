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

public class ProntoQATests {
  private GraphState<IVertexId> buildGraphState() {
    GraphState<IVertexId> graphState = new MemGraphState();
    return graphState;
  }

  private Thinker buildThinker() {
    ResourceLogicCatalog logicCatalog = new ResourceLogicCatalog("/ProntoQA.txt");
    logicCatalog.init();
    Thinker thinker = new DefaultThinker(buildGraphState(), logicCatalog);
    return thinker;
  }

  private Triple makeTriple(String sType, String rType, String oType) {
    return new Triple(new Node(sType), new Predicate(rType), new Node(oType));
  }

  private Triple makeTriple(Entity s, String rType, String oType) {
    return new Triple(s, new Predicate(rType), new Node(oType));
  }

  @Test
  public void testCase1() {
    Thinker thinker = buildThinker();
    Map<String, Object> context = new HashMap<>();
    Entity entity = new Entity("Polly", "Thing");
    Triple t1 = makeTriple(entity, "iss", "rompus");
    Triple t2 = makeTriple(entity, "iss", "zumpus");
    Triple t3 = makeTriple(entity, "iss", "impus");
    Triple t4 = makeTriple(entity, "iss", "yumpus");

    List<Triple> triples = Arrays.asList(t1, t2, t3, t4);
    triples.forEach(t -> context.put(t.toString(), t));
    List<Result> result =
        thinker.find(
            new Entity("Polly", "Thing"), new Predicate("iss"), new Node("lorpus"), context);
    Assert.assertTrue(result.size() == 1);
  }
}
