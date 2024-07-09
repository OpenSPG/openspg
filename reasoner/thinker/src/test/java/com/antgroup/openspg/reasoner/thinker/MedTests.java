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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
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
import java.util.*;
import org.junit.Assert;
import org.junit.Test;

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
    String str = "{\"spg.reasoner.thinker.strict\":true,\"乙肝表面抗原\":\"225.000\",\"乙肝表面抗原_lower\":\"0\",\"乙肝表面抗原_upper\":\"0.5\",\"乙肝表面抗体\":\"0.1\",\"乙肝表面抗体_lower\":\"0\",\"乙肝表面抗体_upper\":\"10\",\"乙肝e抗原\":\"69.000\",\"乙肝e抗原_lower\":\"0\",\"乙肝e抗原_upper\":\"0.5\",\"乙肝e抗体\":\"0.00\",\"乙肝e抗体_lower\":\"0\",\"乙肝e抗体_upper\":\"0.2\",\"乙肝核心抗体\":\"4.050\",\"乙肝核心抗体_lower\":\"0\",\"乙肝核心抗体_upper\":\"0.9\"}";
    Map<String, Object> context = JSONObject.parseObject(str, new TypeReference<Map<String, Object>>() {});
    List<Result> triples =
            thinker.find(
                    null,
                    new Predicate("确诊"),
                    new Node("Medical.DiseaseTerm"),
                    context);
    Assert.assertTrue(triples.size() == 1);
  }
}
