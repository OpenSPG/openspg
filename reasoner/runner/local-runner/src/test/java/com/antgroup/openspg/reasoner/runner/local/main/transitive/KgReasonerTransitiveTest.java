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

package com.antgroup.openspg.reasoner.runner.local.main.transitive;

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog;
import com.antgroup.openspg.reasoner.runner.ConfigKey;
import com.antgroup.openspg.reasoner.runner.local.LocalReasonerRunner;
import com.antgroup.openspg.reasoner.runner.local.load.graph.AbstractLocalGraphLoader;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerTask;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import scala.Tuple2;
import scala.collection.immutable.Set;

public class KgReasonerTransitiveTest {
  private LocalReasonerResult doProcess(String dsl) {
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    // add mock catalog
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put(
        "RelatedParty",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "name", "entityType")));
    schema.put(
        "RelatedParty_trans_RelatedParty",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("payDate", "amount", "logId")));
    schema.put(
        "RelatedParty_holdShare_RelatedParty",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("rate")));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.transitive.KgReasonerTransitiveTest$CompanyGraphLoader");
    task.setStartIdList(Lists.newArrayList(new Tuple2<>("P1", "RelatedParty")));
    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    params.put(ConfigKey.KG_REASONER_BINARY_PROPERTY, "false");
    params.put(Constants.SPG_REASONER_MULTI_VERSION_ENABLE, "true");
    task.setParams(params);
    LocalReasonerRunner runner = new LocalReasonerRunner();
    return runner.run(task);
  }

  public static class CompanyGraphLoader extends AbstractLocalGraphLoader {
    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("P1", "RelatedParty", "name", "P1", "entityType", "PERSON"),
          constructionVertex("C1", "RelatedParty", "name", "C1", "entityType", "COMPANY"),
          constructionVertex("C2", "RelatedParty", "name", "C2", "entityType", "COMPANY"),
          constructionVertex("C3", "RelatedParty", "name", "C3", "entityType", "COMPANY"),
          constructionVertex("C4", "RelatedParty", "name", "C4", "entityType", "COMPANY"),
          constructionVertex("C5", "RelatedParty", "name", "C5", "entityType", "COMPANY"),
          constructionVertex("C6", "RelatedParty", "name", "C6", "entityType", "COMPANY"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionEdge("P1", "holdShare", "C1", "rate", 0.5),
          constructionEdge("P1", "holdShare", "C2", "rate", 0.5),
          constructionEdge("C1", "holdShare", "C3", "rate", 0.5),
          constructionEdge("C2", "holdShare", "C3", "rate", 0.5),
          constructionEdge("C2", "holdShare", "C4", "rate", 1),
          constructionEdge("C4", "holdShare", "C5", "rate", 0.2),
          constructionEdge("C4", "holdShare", "C6", "rate", 0.5),
          constructionEdge("C6", "holdShare", "C5", "rate", 0.5),
          constructionEdge("P1", "trans", "C2", "payDate", 1, "amount", 5, "logId", 1),
          constructionEdge("P1", "trans", "C1", "payDate", 1, "amount", 5, "logId", 4),
          constructionEdge("C1", "trans", "C3", "payDate", 2, "amount", 5, "logId", 4),
          constructionEdge("C2", "trans", "C3", "payDate", 0, "amount", 5, "logId", 1),
          constructionEdge("C2", "trans", "C4", "payDate", 3, "amount", 5, "logId", 1),
          constructionEdge("C4", "trans", "C5", "payDate", 4, "amount", 5, "logId", 2),
          constructionEdge("C4", "trans", "C6", "payDate", 5, "amount", 5, "logId", 1),
          constructionEdge("C6", "trans", "C5", "payDate", 6, "amount", 5, "logId", 1));
    }
  }

  @Test
  public void testTransitiveWithPathLongest() {
    String dsl =
        "GraphStructure {\n"
            + "  A [RelatedParty, __start__='true']\n"
            + "  B [RelatedParty]\n"
            + "  A->B [holdShare] repeat(1,10) as e\n"
            + "}\n"
            + "Rule {\n"
            + "  R1(\"只保留最长的路径\"): group(A).keep_longest_path(e)\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id,B.id)  \n"
            + "}";
    LocalReasonerResult result = doProcess(dsl);
    // check result
    Assert.assertEquals(1, result.getRows().size());
    Assert.assertEquals(2, result.getRows().get(0).length);
    Assert.assertEquals(result.getRows().get(0)[0], "P1");
    Assert.assertEquals(result.getRows().get(0)[1], "C5");
  }

  @Test
  public void testTransitiveWithPathShortest() {
    String dsl =
        "GraphStructure {\n"
            + "  A [RelatedParty, __start__='true']\n"
            + "  B [RelatedParty]\n"
            + "  A->B [holdShare] repeat(1,10) as e\n"
            + "}\n"
            + "Rule {\n"
            + "  R1: group(A).keep_shortest_path(e)\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id,B.id)  \n"
            + "}";
    LocalReasonerResult result = doProcess(dsl);
    // check result
    Assert.assertEquals(1, result.getRows().size());
    Assert.assertEquals(2, result.getRows().get(0).length);
    Assert.assertEquals(result.getRows().get(0)[0], "P1");
    Assert.assertTrue("C2,C1".contains(result.getRows().get(0)[1].toString()));
  }

  @Test
  public void testTransitiveWithPathAll() {
    String dsl =
        "GraphStructure {\n"
            + "  A [RelatedParty, __start__='true']\n"
            + "  B [RelatedParty]\n"
            + "  A->B [holdShare] repeat(1,10) as e\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id,B.id)  \n"
            + "}";
    LocalReasonerResult result = doProcess(dsl);
    // check result
    Assert.assertEquals(8, result.getRows().size());
  }

  @Test
  public void testTransitiveWithComputeAcc() {
    String dsl =
        "GraphStructure {\n"
            + "  A [RelatedParty, __start__='true']\n"
            + "  B [RelatedParty]\n"
            + "  A->B [holdShare] repeat(1,10) as e\n"
            + "}\n"
            + "Rule {\n"
            + "totalRate = e.edges().reduce((x,y) => y.rate * x, 1)"
            + "  R1(\"只保留最长的路径\"): group(A).keep_longest_path(e)\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id,B.id, totalRate)  \n"
            + "}";
    LocalReasonerResult result = doProcess(dsl);
    // check result
    Assert.assertEquals(1, result.getRows().size());
    Assert.assertEquals(3, result.getRows().get(0).length);
    Assert.assertEquals(result.getRows().get(0)[0], "P1");
    Assert.assertEquals(result.getRows().get(0)[1], "C5");
    Assert.assertEquals(result.getRows().get(0)[2], "0.125");
  }

  @Test
  public void testTransitiveWithRule1() {
    String dsl =
        "GraphStructure {\n"
            + "  A [RelatedParty, __start__='true']\n"
            + "  B [RelatedParty]\n"
            + "  A->B [trans] repeat(1,10) as e\n"
            + "}\n"
            + "Rule {\n"
            + "R1(\"要求转账logId一致\"): e.edges().constraint((pre,cur) => cur.logId == pre.logId)"
            + "R2(\"要求前一个时间小于后一个时间\"): e.edges().constraint((pre,cur) => cur.payDate > pre.payDate)"
            + "R3(\"只保留最长的路径\"): group(A).keep_longest_path(e)\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id,B.id)  \n"
            + "}";
    LocalReasonerResult result = doProcess(dsl);
    // check result
    Assert.assertEquals(1, result.getRows().size());
    Assert.assertEquals(2, result.getRows().get(0).length);
    Assert.assertEquals(result.getRows().get(0)[0], "P1");
    Assert.assertEquals(result.getRows().get(0)[1], "C5");
  }

  @Test
  public void testTransitiveWithRule2() {
    String dsl =
        "GraphStructure {\n"
            + "  A [RelatedParty, __start__='true']\n"
            + "  B,C [RelatedParty]\n"
            + "  B->C [trans] repeat(1,10) as e\n"
            + "  A->B [trans] as f\n"
            + "}\n"
            + "Rule {\n"
            + "R1(\"要求转账logId一致\"): e.edges().constraint((pre,cur) => cur.logId == f.logId)"
            + "R2(\"时间大于第一个\"): e.edges().constraint((pre,cur) => cur.payDate > f.payDate)"
            + "R11(\"要求前一个时间小于后一个时间\"): e.edges().constraint((pre,cur) => cur.payDate > pre.payDate)"
            + "R3(\"只保留最长的路径\"): group(A).keep_longest_path(e)\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id,C.id)  \n"
            + "}";
    LocalReasonerResult result = doProcess(dsl);
    // check result
    Assert.assertEquals(1, result.getRows().size());
    Assert.assertEquals(2, result.getRows().get(0).length);
    Assert.assertEquals(result.getRows().get(0)[0], "P1");
    Assert.assertEquals(result.getRows().get(0)[1], "C5");
  }

  @Test
  public void testTransitive1() {
    String dsl =
        "GraphStructure {\n"
            + "  A [FilmPerson]\n"
            + "  C,D [FilmDirector]\n"
            + "  A->C [test] as e1\n"
            + "  C->D [t1] repeat(0,2) as e2\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.name,C.name,D.name)\n"
            + "}";

    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put("FilmPerson", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "name")));
    schema.put(
        "FilmDirector", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "name")));
    schema.put(
        "FilmPerson_test_FilmDirector", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "FilmDirector_t1_FilmDirector", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.transitive.KgReasonerTransitiveTest$GraphLoader");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    params.put(ConfigKey.KG_REASONER_BINARY_PROPERTY, "false");
    params.put(Constants.SPG_REASONER_MULTI_VERSION_ENABLE, "true");
    task.setParams(params);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);

    // only u1
    // check result
    Assert.assertEquals(4, result.getRows().size());
    Assert.assertEquals(3, result.getRows().get(0).length);
    java.util.Set<String> dSet = new HashSet<>();
    for (Object[] strings : result.getRows()) {
      dSet.add(String.valueOf(strings[2]));
    }
    Assert.assertTrue(dSet.contains("C1"));
    Assert.assertTrue(dSet.contains("C2"));
    Assert.assertTrue(dSet.contains("D21"));
    Assert.assertTrue(dSet.contains("D22"));
  }

  @Test
  public void transitiveAndOptionTest() {
    String dsl =
        "GraphStructure {\n"
            + "  X [T,__start__='true']\n"
            + "  A [T1]\n"
            + "  B, C, D [T2]\n"
            + "  X->A [ET1] as e1\n"
            + "  A->B [ET2, __optional__='true'] as e2\n"
            + "  A->C [ET3, __optional__='true'] as e3\n"
            + "  C->D [ET4] repeat(0,1) as e4\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "  get(X.id,A.id,B.id,C.id,D.id)\n"
            + "}";
    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    // add mock catalog
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put("T", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("T1", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("T2", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("T_ET1_T1", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("T1_ET2_T2", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("T1_ET3_T2", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("T2_ET4_T2", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.transitive.KgReasonerTransitiveTest$GraphLoader2");
    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    params.put(ConfigKey.KG_REASONER_BINARY_PROPERTY, "false");
    params.put(Constants.SPG_REASONER_MULTI_VERSION_ENABLE, "true");
    task.setParams(params);
    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    // check result
    Assert.assertEquals(5, result.getRows().size());
  }

  public static class GraphLoader2 extends AbstractLocalGraphLoader {
    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          // case1
          constructionVertex("x1", "T"),
          constructionVertex("a1", "T1"),
          constructionVertex("b1", "T2"),
          constructionVertex("c1", "T2"),
          constructionVertex("d1", "T2")
          // case2
          ,
          constructionVertex("x2", "T"),
          constructionVertex("a2", "T1")
          // case3
          ,
          constructionVertex("x3", "T"),
          constructionVertex("a3", "T1"),
          constructionVertex("b3", "T2")
          // case4
          ,
          constructionVertex("x4", "T"),
          constructionVertex("a4", "T1"),
          constructionVertex("c4", "T2"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          // case1
          constructionEdge("x1", "ET1", "a1"),
          constructionEdge("a1", "ET2", "b1"),
          constructionEdge("a1", "ET3", "c1"),
          constructionEdge("c1", "ET4", "d1")
          // case2
          ,
          constructionEdge("x2", "ET1", "a2")
          // case3
          ,
          constructionEdge("x3", "ET1", "a3"),
          constructionEdge("a3", "ET2", "b3")
          // case4
          ,
          constructionEdge("x4", "ET1", "a4"),
          constructionEdge("a4", "ET3", "c4"));
    }
  }

  public static class GraphLoader extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("A1", "FilmPerson", "name", "A1"),
          constructionVertex("C1", "FilmDirector", "name", "C1"),
          constructionVertex("A2", "FilmPerson", "name", "A2"),
          constructionVertex("C2", "FilmDirector", "name", "C2"),
          constructionVertex("D21", "FilmDirector", "name", "D21"),
          constructionVertex("D22", "FilmDirector", "name", "D22"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionEdge("A1", "test", "C1"),
          constructionEdge("A2", "test", "C2"),
          constructionEdge("C2", "t1", "D21"),
          constructionEdge("D21", "t1", "D22"));
    }
  }
}
