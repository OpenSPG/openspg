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

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.thinker.catalog.ResourceLogicCatalog;
import com.antgroup.openspg.reasoner.thinker.engine.DefaultThinker;
import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Node;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Predicate;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Value;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MedTests {
  private GraphState<IVertexId> buildGraphState() {
    Vertex<IVertexId, IProperty> vertex1 =
        GraphUtil.makeVertex(
            "尿酸",
            "Med.Examination",
            "highExplain",
            "highExplain desc",
            "lowExplain",
            "lowExplain desc");
    return GraphUtil.buildMemState(Arrays.asList(vertex1), new LinkedList<>());
  }

  @Test
  public void test() {
    ResourceLogicCatalog logicCatalog = new ResourceLogicCatalog("/Medical.txt");
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

  @Test
  public void testHigh() {
    ResourceLogicCatalog logicCatalog = new ResourceLogicCatalog("/Medical.txt");
    logicCatalog.init();
    Thinker thinker = new DefaultThinker(buildGraphState(), logicCatalog);
    Map<String, Object> context = new HashMap<>();
    context.put("value", "阳性");
    List<Result> triples =
        thinker.find(
            new Entity("尿酸", "Med.Examination"),
            new Predicate("abnormalRule"),
            new Value(),
            context);
    Assert.assertTrue(triples.size() == 2);
  }

  @Test
  public void testStrict() {
    ResourceLogicCatalog logicCatalog = new ResourceLogicCatalog("/Medical.txt");
    logicCatalog.init();
    Thinker thinker = new DefaultThinker(buildGraphState(), logicCatalog);
    Map<String, Object> context = new HashMap<>();
    context.put("value", "阳性");
    context.put(Constants.SPG_REASONER_THINKER_STRICT, true);
    List<Result> triples =
        thinker.find(
            new Entity("尿酸", "Med.Examination"),
            new Predicate("abnormalRule"),
            new Value(),
            context);
    Assert.assertTrue(triples.size() == 0);
  }

  @Test
  public void testHepatitis() {
    ResourceLogicCatalog logicCatalog = new ResourceLogicCatalog("/Medical.txt");
    logicCatalog.init();
    Thinker thinker = new DefaultThinker(buildGraphState(), logicCatalog);
    Map<String, Object> context = new HashMap<>();
    ;
    context.put("spg.reasoner.thinker.strict", true);
    context.put("孕酮", "1.25");
    context.put("a", "12");

    List<Result> triples =
            thinker.find(null, new Predicate("diagnosis"), new Node("Medical.DiagnosisRecommend"), context);
    System.out.println(triples.size());


  }
}
