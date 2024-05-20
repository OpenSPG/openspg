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

import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import com.antgroup.openspg.reasoner.thinker.catalog.MockLogicCatalog;
import com.antgroup.openspg.reasoner.thinker.engine.DefaultThinker;
import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Predicate;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Value;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class MedTests {
  private String rule1 =
      "Define (a:Med.Examination/`尿酸`)-[:abnormalRule]->(c: string) {\n"
          + " R1: contains(population, '男性') AND (value > 416) AND ((a)-[: highExplain]->(c))\n"
          + "}\n"
          + "Description: \"对于男性，尿酸的正常范围是[150umol/L-416umol/L]\"";

  private String rule2 =
      "Define (a:Med.Examination/`尿酸`)-[:abnormalRule]->(c: string) {\n"
          + " R1: contains(population, '男性') AND (value < 150) AND ((a)-[: lowExplain]->(c))\n"
          + "}\n"
          + "Description: \"对于男性，尿酸的正常范围是[150umol/L-416umol/L]\"";

  private String rule3 =
      "Define (a:Med.Examination/`尿酸`)-[:abnormalRule]->(c: string) {\n"
          + " R1: contains(population, '女性') AND (value > 357) AND ((a)-[: highExplain]->(c)) \n"
          + "}\n"
          + "Description: \"对于女性，尿酸的正常范围是[89umol/L-357umol/L]\"";

  private String rule4 =
      "Define (a:Med.Examination/`尿酸`)-[:abnormalRule]->(c: string) {\n"
          + " R1: contains(population, '女性') AND (value < 89) AND ((a)-[: lowExplain]->(c)) \n"
          + "}\n"
          + "Description: \"对于女性，尿酸的正常范围是[89umol/L-357umol/L]\"";

  private List<Rule> getRules() {
    List<Rule> rules = new LinkedList<>();
    SimplifyThinkerParser parser = new SimplifyThinkerParser();
    rules.add(parser.parseSimplifyDsl(rule1, null).head());
    rules.add(parser.parseSimplifyDsl(rule2, null).head());
    rules.add(parser.parseSimplifyDsl(rule3, null).head());
    rules.add(parser.parseSimplifyDsl(rule4, null).head());
    return rules;
  }

  private GraphState<IVertexId> buildGraphState() {
    GraphState<IVertexId> graphState = new MemGraphState();

    Vertex<IVertexId, IProperty> vertex1 =
        new Vertex<>(
            IVertexId.from("尿酸", "Med.Examination"),
            new VertexVersionProperty(
                "highExplain", "highExplain desc", "lowExplain", "lowExplain desc"));
    graphState.addVertex(vertex1);

    return graphState;
  }

  @Test
  public void test() {
    MockLogicCatalog logicCatalog = new MockLogicCatalog(getRules());
    logicCatalog.init();
    Thinker thinker = new DefaultThinker(buildGraphState(), logicCatalog);
    // test for normal
    Map<String, Object> context = new HashMap<>();
    context.put("population", "男性");
    context.put("value", 460);
    List<Result> triples =
        thinker.find(
            new Entity("尿酸", "Med.Examination"),
            new Predicate("abnormalRule"),
            new Value(),
            context);
    Assert.assertTrue(triples.size() == 1);

    // test for absent
    triples =
        thinker.find(
            new Entity("尿酸", "Med.Examination"),
            new Predicate("abnormalRule"),
            new Value(),
            new HashMap<>());
    Assert.assertTrue(triples.size() == 4);
  }
}
