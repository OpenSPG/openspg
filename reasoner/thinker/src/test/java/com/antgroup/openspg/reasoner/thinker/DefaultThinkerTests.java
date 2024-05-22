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

import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.thinker.catalog.MockLogicCatalog;
import com.antgroup.openspg.reasoner.thinker.engine.DefaultThinker;
import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Node;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Predicate;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import java.util.*;
import org.junit.Assert;
import org.junit.Test;

public class DefaultThinkerTests {
  private GraphState<IVertexId> buildGraphState() {
    Vertex<IVertexId, IProperty> a1 = GraphUtil.makeVertex("a1", "A");
    Vertex<IVertexId, IProperty> a2 = GraphUtil.makeVertex("a2", "A");
    Vertex<IVertexId, IProperty> b = GraphUtil.makeVertex("b", "B");
    Vertex<IVertexId, IProperty> c = GraphUtil.makeVertex("c", "C");

    Edge a1b = GraphUtil.makeEdge(a1, b, "ab");
    Edge a2b = GraphUtil.makeEdge(a2, b, "ab");
    Edge bc = GraphUtil.makeEdge(b, c, "bc");

    return GraphUtil.buildMemState(Arrays.asList(a1, a2, b, c), Arrays.asList(a1b, a2b, bc));
  }

  @Test
  public void testFindForward() {
    MockLogicCatalog mockLogicCatalog = new MockLogicCatalog();
    mockLogicCatalog.init();
    Thinker thinker = new DefaultThinker(buildGraphState(), mockLogicCatalog);
    List<Result> triples = thinker.find(new Entity("a1", "A"), null, null);
    Assert.assertTrue(triples.size() == 2);
  }

  @Test
  public void testBackForward() {
    MockLogicCatalog mockLogicCatalog = new MockLogicCatalog();
    mockLogicCatalog.init();
    Thinker thinker = new DefaultThinker(buildGraphState(), mockLogicCatalog);
    List<Result> triples = thinker.find(null, null, new Entity("b", "B"));
    Assert.assertTrue(triples.size() == 2);
  }

  private Rule getR1() {
    String rule = "Define (D/`d`) {\n" + "  R1:A/`a1` and A/`a2`\n" + "}";
    SimplifyThinkerParser parser = new SimplifyThinkerParser();
    return parser.parseSimplifyDsl(rule, null).head();
  }

  private Rule getR2() {
    String rule =
        "Define (a:A)-[:ac]->(c:C) {\n"
            + " R1: (a)-[: ab]->(b: B) AND (b)-[:bc]->(c:C) \n"
            + "}\n"
            + "Description: \"(a, ab, b), (b, bc, c) -> (a, ac, c)\"";
    SimplifyThinkerParser parser = new SimplifyThinkerParser();
    return parser.parseSimplifyDsl(rule, null).head();
  }

  @Test
  public void testFindWithRule1() {
    MockLogicCatalog logicCatalog = new MockLogicCatalog(Arrays.asList(getR1()));
    logicCatalog.init();
    Thinker thinker = new DefaultThinker(buildGraphState(), logicCatalog);
    Entity a1 = new Entity("a1", "A");
    Entity a2 = new Entity("a2", "A");
    Map<String, Object> context = new HashMap<>();
    context.put(a1.toString(), a1);
    context.put(a2.toString(), a2);
    List<Result> triples = thinker.find(new Node("D"), context);
    Assert.assertTrue(triples.size() == 1);
  }

  @Test
  public void testFindWithRule2() {
    MockLogicCatalog logicCatalog = new MockLogicCatalog(Arrays.asList(getR2()));
    logicCatalog.init();
    Thinker thinker = new DefaultThinker(buildGraphState(), logicCatalog);
    List<Result> triples = thinker.find(new Entity("a1", "A"), new Predicate("ac"), new Node("C"));
    Assert.assertTrue(triples.size() == 1);
    triples = thinker.find(new Node("A"), new Predicate("ac"), new Entity("c", "C"));
    Assert.assertTrue(triples.size() == 2);
  }
}
