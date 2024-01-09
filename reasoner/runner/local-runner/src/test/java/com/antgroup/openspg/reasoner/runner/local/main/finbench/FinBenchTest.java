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

package com.antgroup.openspg.reasoner.runner.local.main.finbench;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.runner.ConfigKey;
import com.antgroup.openspg.reasoner.runner.local.load.graph.AbstractLocalGraphLoader;
import com.antgroup.openspg.reasoner.runner.local.main.LocalRunnerTestFactory;
import com.antgroup.openspg.reasoner.runner.local.main.LocalRunnerTestFactory.AssertFunction;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FinBenchTest {

  private AbstractLocalGraphLoader graphLoader;
  private Map<String, Object> params;

  @Before
  public void init() {
    this.graphLoader =
        new AbstractLocalGraphLoader() {
          @Override
          public List<IVertex<String, IProperty>> genVertexList() {
            return Lists.newArrayList(
                constructionVertex("A1", "Account"),
                constructionVertex("A2", "Account"),
                constructionVertex("A3", "Account"),
                constructionVertex("A4", "Account"),
                constructionVertex("M3", "Medium", "isBlocked", true),
                constructionVertex("M4", "Medium", "isBlocked", false));
          }

          @Override
          public List<IEdge<String, IProperty>> genEdgeList() {
            return Lists.newArrayList(
                constructionEdge("A1", "transfer", "A2", "timestamp", 1, "amount", 100),
                constructionEdge("A2", "transfer", "A3", "timestamp", 1, "amount", 100),
                constructionEdge("A3", "transfer", "A4", "timestamp", 1, "amount", 50),
                constructionEdge("A3", "transfer", "A1", "timestamp", 1, "amount", 50),
                constructionEdge("A3", "signIn", "M3", "timestamp", 1),
                constructionEdge("A4", "signIn", "M4", "timestamp", 1));
          }
        };

    params = new HashMap<>();
    // start id
    List<List<String>> startIdList = new ArrayList<>();
    startIdList.add(Lists.newArrayList("A1", "Account"));
    startIdList.add(Lists.newArrayList("A2", "Account"));
    startIdList.add(Lists.newArrayList("A3", "Account"));
    params.put(ConfigKey.KG_REASONER_START_ID_LIST, JSON.toJSONString(startIdList));

    // other params
    params.put("startTime", "1");
    params.put("endTime", "1");

    params.put("trc1id", "'A1'");
    params.put("trc4id1", "'A1'");
    params.put("trc4id2", "'A2'");
  }

  @Test
  public void trc1() {
    LocalRunnerTestFactory.runTest(
        "GraphStructure {\n"
            + "    account[Account,__start__='true']\n"
            + "    other [Account]\n"
            + "    medium [Medium]\n"
            + "    account -> other [transfer] repeat(1,3) as edge1\n"
            + "    other -> medium [signIn] as edge2\n"
            + "}\n"
            + "Rule {\n"
            + "    R1(\"参数\"): account.id == $trc1id\n"
            + "    R2(\"transfer时间\"): edge1.edges().constraint((pre, cur) => pre.timestamp >= $startTime and pre\n"
            + ".timestamp <= \n"
            + "$endTime and cur.timestamp >= $startTime and cur.timestamp <= $endTime)\n"
            + "    R3(\"signIn时间\"): edge2.timestamp >= $startTime and edge2.timestamp <= $endTime\n"
            + "    R4(\"isBlocked\"): medium.isBlocked == true\n"
            + "    repeat_len = repeat_edge_length(edge1)\n"
            + "}\n"
            + "Action {\n"
            + "    get(other.id, repeat_len, medium.id)\n"
            + "}",
        this.graphLoader,
        new AssertFunction() {
          @Override
          public void assertResult(LocalReasonerResult result) {
            Assert.assertEquals(result.getRows().size(), 1);
            Assert.assertEquals(result.getRows().get(0)[0], "A3");
          }
        },
        this.params);
  }

  @Test
  public void trc4() {
    LocalRunnerTestFactory.runTest(
        "GraphStructure {\n"
            + "    (src:Account) -[edge1:transfer]-> (dst:Account) -[edge2:transfer]-> (other:Account) \n"
            + "-[edge3:transfer]-> (src)\n"
            + "}\n"
            + "Rule {\n"
            + "    R1(\"src参数\"): src.id == $trc4id1\n"
            + "    R2(\"dst参数\"): dst.id == $trc4id2\n"
            + "    R3(\"transfer时间1\"): edge1.timestamp >= $startTime and edge1.timestamp <= $endTime\n"
            + "    R4(\"transfer时间2\"): edge2.timestamp >= $startTime and edge2.timestamp <= $endTime\n"
            + "    R5(\"transfer时间3\"): edge3.timestamp >= $startTime and edge3.timestamp <= $endTime\n"
            + "    numEdge2 = group(src,dst,other).count(edge2)\n"
            + "    numEdge3 = group(src,dst,other).count(edge3)\n"
            + "    sumEdge2Amount = group(src,dst,other).sum(edge2.amount)\n"
            + "    sumEdge3Amount = group(src,dst,other).sum(edge3.amount)\n"
            + "    maxEdge2Amount = group(src,dst,other).max(edge2.amount)\n"
            + "    maxEdge3Amount = group(src,dst,other).max(edge3.amount)\n"
            + "}\n"
            + "Action {\n"
            + "    get(other.id, numEdge2, sumEdge2Amount, maxEdge2Amount, numEdge3, sumEdge3Amount, maxEdge3Amount)\n"
            + "}",
        this.graphLoader,
        new AssertFunction() {
          @Override
          public void assertResult(LocalReasonerResult result) {
            Assert.assertEquals(result.getRows().size(), 1);
            Assert.assertEquals(result.getRows().get(0)[0], "A3");
            Assert.assertEquals(result.getRows().get(0)[1], "1");
          }
        },
        this.params);
  }
}
