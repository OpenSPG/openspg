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
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import com.antgroup.openspg.reasoner.thinker.catalog.ResourceLogicCatalog;
import com.antgroup.openspg.reasoner.thinker.engine.DefaultThinker;
import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Node;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Predicate;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HypertensionTest {
  private GraphState<IVertexId> buildGraphState() {
    GraphState<IVertexId> graphState = new MemGraphState();
    return graphState;
  }

  private Thinker buildThinker() {
    ResourceLogicCatalog logicCatalog = new ResourceLogicCatalog("/Hypertension.txt");
    logicCatalog.init();
    Thinker thinker = new DefaultThinker(buildGraphState(), logicCatalog);
    return thinker;
  }

  @Test
  public void bloodPressureLevel() {
    Thinker thinker = buildThinker();
    Map<String, Object> context = new HashMap<>();
    context.put("收缩压", 160);
    List<Result> triples = thinker.find(new Node("血压水平分级"), context);
    Assert.assertTrue(triples.size() == 3);
  }

  @Test
  public void hypertensionLevel() {
    Thinker thinker = buildThinker();
    Map<String, Object> context = new HashMap<>();
    context.put("BMI", 35);
    context.put("GFR", 35);
    List<Result> triples = thinker.find(new Node("高血压分层"), context);
    Assert.assertTrue(triples.size() == 2);
  }

  @Test
  public void combinationDrug() {
    Thinker thinker = buildThinker();
    Map<String, Object> context = new HashMap<>();
    context.put("收缩压", 160);
    context.put("舒张压", 200);
    context.put(Constants.SPG_REASONER_THINKER_STRICT, true);
    List<Result> triples = thinker.find(null, new Predicate("基本用药方案"), new Node("药品"), context);
    Assert.assertTrue(triples.size() == 1);
  }
}
