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

package com.antgroup.openspg.reasoner.runner.local.main;

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog;
import com.antgroup.openspg.reasoner.recorder.DefaultRecorder;
import com.antgroup.openspg.reasoner.runner.ConfigKey;
import com.antgroup.openspg.reasoner.runner.local.KGReasonerLocalRunner;
import com.antgroup.openspg.reasoner.runner.local.load.graph.AbstractLocalGraphLoader;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerTask;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class KgReasonerAggTest {

  @Test
  public void testAgg3() {
    String dsl =
        "GraphStructure {\n"
            + "    s [AMLz50.Custid, __start__='true']\n"
            + "    s2 [AMLz50.Custid]\n"
            + "\n"
            + "    u1,u2 [AMLz50.Userinfo]\n"
            + "    test [Test]\n"
            + "    s->u1 [has]\n"
            + "    s2->u2 [has]\n"
            + "    u1->u2 [aml90dTradeEdge] as e\n"
            + "    s->test [aml90dTradeCrimeReport]\n"
            + "    s2->test [aml90dTradeCrimeReport]\n"
            + "} \n"
            + "Rule {\n"
            + "    totalAmt = group(s, s2).sum(e.payAmt90d)\n"
            + "    R1: totalAmt > 1000\n"
            + "    nums = group(s).count(s2)\n"
            + "    result = rule_value(nums >= 1, true, false)\n"
            + "}\n"
            + "Action {\n"
            + "  get(s.id, result, nums)\n"
            + "}";

    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("AMLz50.Custid", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("AMLz50.Userinfo", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("Test", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "AMLz50.Userinfo_aml90dTradeEdge_AMLz50.Userinfo",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("payAmt90d")));
    schema.put(
        "AMLz50.Custid_aml90dTradeCrimeReport_Test",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("payAmt90d")));
    schema.put(
        "AMLz50.Custid_has_AMLz50.Userinfo",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerAggTest$GraphLoader223");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    params.put(ConfigKey.KG_REASONER_BINARY_PROPERTY, "false");
    params.put(Constants.SPG_REASONER_MULTI_VERSION_ENABLE, "true");
    task.setParams(params);
    task.setExecutorTimeoutMs(99999999999999999L);

    task.setExecutionRecorder(new DefaultRecorder());

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);

    System.out.println(task.getExecutionRecorder().toReadableString());

    // only u1
    Assert.assertEquals(1, result.getRows().size());
    Assert.assertEquals("s", result.getRows().get(0)[0]);
    Assert.assertEquals("true", result.getRows().get(0)[1]);
    Assert.assertEquals("1", result.getRows().get(0)[2]);
  }

  public static class GraphLoader223 extends AbstractLocalGraphLoader {
    @Override
    public String getDemoGraph() {
      return "Graph {\n"
          + "    s [AMLz50.Custid]\n"
          + "    s2 [AMLz50.Custid]\n"
          + "\n"
          + "    test [Test]\n"
          + "    u1,u12,u13,u14,u15,u2 [AMLz50.Userinfo]\n"
          + "    s->u1 [has]\n"
          + "    s->u12 [has]\n"
          + "    s->u13 [has]\n"
          + "    s->u14 [has]\n"
          + "    s->u15 [has]\n"
          + "    s2->u2 [has]\n"
          + "    u1->u2 [aml90dTradeEdge, payAmt90d=10000] as e\n"
          + "    s->test [aml90dTradeCrimeReport]"
          + "    s2->test [aml90dTradeCrimeReport]"
          + "}";
    }

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("C_37091", "CRO.Company", "regNo", "C_37091"),
          constructionVertex("C_7661", "CRO.Company", "regNo", "C_7661"),
          constructionVertex("C_8125", "CRO.Company", "regNo", "C_8125"),
          constructionVertex("P1", "CRO.Person"),
          constructionVertex("P2", "CRO.Person"),
          constructionVertex("P3", "CRO.Person"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionEdge("P1", "corporate", "C_37091"),
          constructionEdge("P1", "control", "C_7661"),
          constructionEdge("P1", "superviseDirctor", "C_7661"),
          constructionEdge("P2", "control", "C_7661"),
          constructionEdge("P2", "corporate", "C_7661"),
          constructionEdge("P2", "superviseDirctor", "C_7661"),
          constructionEdge("P2", "corporate", "C_8125"),
          constructionEdge("P3", "superviseDirctor", "C_7661"),
          constructionEdge("P3", "corporate", "C_8125"));
    }
  }

  @Test
  public void testAgg2() {
    String dsl =
        "GraphStructure {\n"
            + "    s [AMLz50.Custid, __start__='true']\n"
            + "    s2 [AMLz50.Custid]\n"
            + "\n"
            + "    u1,u2 [AMLz50.Userinfo]\n"
            + "    s->u1 [has]\n"
            + "    s2->u2 [has]\n"
            + "    u1->u2 [aml90dTradeEdge] as e\n"
            + "    s->s2 [aml90dTradeCrimeReport]\n"
            + "} \n"
            + "Rule {\n"
            + "    totalAmt = group(s, s2).sum(e.payAmt90d)\n"
            + "    R1: totalAmt > 1000\n"
            + "    nums = group(s).count(s2)\n"
            + "    result = rule_value(nums >= 1, true, false)\n"
            + "}\n"
            + "Action {\n"
            + "  get(s.id, result, nums)\n"
            + "}";

    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("AMLz50.Custid", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("AMLz50.Userinfo", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "AMLz50.Userinfo_aml90dTradeEdge_AMLz50.Userinfo",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("payAmt90d")));
    schema.put(
        "AMLz50.Custid_aml90dTradeCrimeReport_AMLz50.Custid",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("payAmt90d")));
    schema.put(
        "AMLz50.Custid_has_AMLz50.Userinfo",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerAggTest$GraphLoader22");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    params.put(ConfigKey.KG_REASONER_BINARY_PROPERTY, "false");
    params.put(Constants.SPG_REASONER_MULTI_VERSION_ENABLE, "true");
    task.setParams(params);
    task.setExecutorTimeoutMs(99999999999999999L);

    task.setExecutionRecorder(new DefaultRecorder());

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);

    System.out.println(task.getExecutionRecorder().toReadableString());

    // only u1
    Assert.assertEquals(1, result.getRows().size());
    Assert.assertEquals("s", result.getRows().get(0)[0]);
    Assert.assertEquals("true", result.getRows().get(0)[1]);
    Assert.assertEquals("1", result.getRows().get(0)[2]);
  }

  public static class GraphLoader22 extends AbstractLocalGraphLoader {
    @Override
    public String getDemoGraph() {
      return "Graph {\n"
          + "    s [AMLz50.Custid]\n"
          + "    s2 [AMLz50.Custid]\n"
          + "\n"
          + "    u1,u12,u13,u14,u15,u2 [AMLz50.Userinfo]\n"
          + "    s->u1 [has]\n"
          + "    s->u12 [has]\n"
          + "    s->u13 [has]\n"
          + "    s->u14 [has]\n"
          + "    s->u15 [has]\n"
          + "    s2->u2 [has]\n"
          + "    u1->u2 [aml90dTradeEdge, payAmt90d=10000] as e\n"
          + "    s->s2 [aml90dTradeCrimeReport]"
          + "}";
    }

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("C_37091", "CRO.Company", "regNo", "C_37091"),
          constructionVertex("C_7661", "CRO.Company", "regNo", "C_7661"),
          constructionVertex("C_8125", "CRO.Company", "regNo", "C_8125"),
          constructionVertex("P1", "CRO.Person"),
          constructionVertex("P2", "CRO.Person"),
          constructionVertex("P3", "CRO.Person"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionEdge("P1", "corporate", "C_37091"),
          constructionEdge("P1", "control", "C_7661"),
          constructionEdge("P1", "superviseDirctor", "C_7661"),
          constructionEdge("P2", "control", "C_7661"),
          constructionEdge("P2", "corporate", "C_7661"),
          constructionEdge("P2", "superviseDirctor", "C_7661"),
          constructionEdge("P2", "corporate", "C_8125"),
          constructionEdge("P3", "superviseDirctor", "C_7661"),
          constructionEdge("P3", "corporate", "C_8125"));
    }
  }

  @Test
  public void testAgg() {
    String dsl =
        "GraphStructure {\n"
            + "  // 公司通过人间接关系，两度\n"
            + "  B [CRO.Company,__start__='true']   \n"
            + "  A, C [CRO.Company]   \n"
            + "  E, F [CRO.Person]           \n"
            + "  E -> A [superviseDirctor, corporate, control] as E1\n"
            + "  E -> B [superviseDirctor, corporate, control] as E2\n"
            + "  F -> B [superviseDirctor, corporate, control] as E3\n"
            + "  F -> C [superviseDirctor, corporate, control] as E4\n"
            + "}\n"
            + "Rule {\n"
            + "  R1: A.id<C.id    \n"
            + "  R2: A.id<B.id\n"
            + "  R3: B.id<C.id\n"
            + "  R4: A.regNo != null && A.regNo != ''\n"
            + "  R5: B.regNo != null && B.regNo != ''\n"
            + "  R6: C.regNo != null && C.regNo != ''\n"
            + "}\n"
            + "\n"
            + "Action {\n"
            + " distinctGet(A.regNo as seed_reg_no, B.regNo as reg_seed_no, C.regNo as reg_2_seed_no)\n"
            + "}";

    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
        "CRO.Company", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "regNo")));
    schema.put("CRO.Person", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "CRO.Person_superviseDirctor_CRO.Company",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "CRO.Person_corporate_CRO.Company",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "CRO.Person_control_CRO.Company", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerAggTest$GraphLoader");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    params.put(ConfigKey.KG_REASONER_BINARY_PROPERTY, "false");
    params.put(Constants.SPG_REASONER_MULTI_VERSION_ENABLE, "true");
    task.setParams(params);

    task.setExecutionRecorder(new DefaultRecorder());

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);

    // only u1
    Assert.assertEquals(1, result.getRows().size());
    Assert.assertEquals("C_37091", result.getRows().get(0)[0]);
    Assert.assertEquals("C_7661", result.getRows().get(0)[1]);
    Assert.assertEquals("C_8125", result.getRows().get(0)[2]);

    System.out.println(task.getExecutionRecorder().toReadableString());
  }

  public static class GraphLoader extends AbstractLocalGraphLoader {
    @Override
    public String getDemoGraph() {
      return "Graph {\n"
          + "  C_37091 [CRO.Company, regNo='C_37091']\n"
          + "  C_7661 [CRO.Company, regNo='C_7661']\n"
          + "  C_8125 [CRO.Company, regNo='C_8125'] \n"
          + "  P1 [CRO.Person]\n"
          + "  P2 [CRO.Person]\n"
          + "  P3 [CRO.Person]\n"
          + "\n"
          + "\n"
          + "  P1->C_37091[corporate]\n"
          + "  P1->C_7661 [control]\n"
          + "  P1->C_7661 [superviseDirctor]\n"
          + "\n"
          + "  P2->C_7661[corporate]\n"
          + "  P2->C_7661 [control]\n"
          + "  P2->C_7661 [superviseDirctor]\n"
          + "\n"
          + "  P3->C_8125[corporate]\n"
          + "  P3->C_7661[superviseDirctor]\n"
          + "\n"
          + "}";
    }

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("C_37091", "CRO.Company", "regNo", "C_37091"),
          constructionVertex("C_7661", "CRO.Company", "regNo", "C_7661"),
          constructionVertex("C_8125", "CRO.Company", "regNo", "C_8125"),
          constructionVertex("P1", "CRO.Person"),
          constructionVertex("P2", "CRO.Person"),
          constructionVertex("P3", "CRO.Person"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionEdge("P1", "corporate", "C_37091"),
          constructionEdge("P1", "control", "C_7661"),
          constructionEdge("P1", "superviseDirctor", "C_7661"),
          constructionEdge("P2", "control", "C_7661"),
          constructionEdge("P2", "corporate", "C_7661"),
          constructionEdge("P2", "superviseDirctor", "C_7661"),
          constructionEdge("P2", "corporate", "C_8125"),
          constructionEdge("P3", "superviseDirctor", "C_7661"),
          constructionEdge("P3", "corporate", "C_8125"));
    }
  }

  @Test
  public void testDistinct() {
    String dsl =
        "GraphStructure {\n"
            + "  A, B [FilmDirector]       // 定义了A,B两个导演\n"
            + "  C, D [Film]               // 定义了C,D两部电影\n"
            + "  E [FilmStar]              // E是电影明星\n"
            + "  C->A [directFilm] as F1   // C的导演是A\n"
            + "  D->B [directFilm] as F2   // D的导演是B\n"
            + "  C->E [starOfFilm] as F3   // C的主演是E\n"
            + "  D->E [starOfFilm] as F4   // D的主演也是E\n"
            + "}\n"
            + "Rule {\n"
            + "  R1: A.id<B.id      // 由于这个图结构是对称的，因此规则需要指定A.id<B.id, 否则会有两条数据返回\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id,B.id)\n"
            + "}";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("Film", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("FilmDirector", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("FilmStar", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "Film_directFilm_FilmDirector", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "Film_starOfFilm_FilmStar", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerAggTest$GraphLoader2");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    params.put(ConfigKey.KG_REASONER_BINARY_PROPERTY, "false");
    params.put(Constants.SPG_REASONER_MULTI_VERSION_ENABLE, "true");
    task.setParams(params);

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    Assert.assertEquals(result.getRows().size(), 1);
  }

  public static class GraphLoader2 extends AbstractLocalGraphLoader {
    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("D1", "FilmDirector"),
          constructionVertex("D2", "FilmDirector"),
          constructionVertex("F1", "Film"),
          constructionVertex("F2", "Film"),
          constructionVertex("F3", "Film"),
          constructionVertex("Star", "FilmStar"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionEdge("F1", "directFilm", "D1"),
          constructionEdge("F1", "starOfFilm", "Star"),
          constructionEdge("F2", "directFilm", "D1"),
          constructionEdge("F2", "starOfFilm", "Star"),
          constructionEdge("F3", "directFilm", "D2"),
          constructionEdge("F3", "starOfFilm", "Star"));
    }
  }
}
