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
import com.antgroup.openspg.reasoner.runner.ConfigKey;
import com.antgroup.openspg.reasoner.runner.local.LocalReasonerRunner;
import com.antgroup.openspg.reasoner.runner.local.load.graph.AbstractLocalGraphLoader;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerTask;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import scala.Tuple2;

public class KgReasonerABMLocalTest {

  @Test
  public void test11() {
    String dsl =
        "Define (s:Attribute1.Name138)-[p:xuanguFeature]->(o:Float) {\n"
            + "\tGraphStructure {\n"
            + "    \t(s)-[:p3]->(t:Attribute1.Name142)\n"
            + "    }\n"
            + "    Rule {\n"
            + "    \tv = t.stock/t.total\n"
            + "        R1(\"必须大于20%\"): v > 0.2\n"
            + "        o = v\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Attribute1.Name138)\n"
            + "}\n"
            + "Rule {\n"
            + "    v2 = s.xuanguFeature\n"
            + "}\n"
            + "Action {\n"
            + "  get(s.id, s.xuanguFeature, v2, \"\" as out)\n"
            + "}";
    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("Attribute1.Name138", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "Attribute1.Name142",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "stock", "total")));
    schema.put(
        "Attribute1.Name138_p3_Attribute1.Name142",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("transDate")));

    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerABMLocalTest$GraphLoader8");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    // 开启子查询
    params.put(ConfigKey.KG_REASONER_BINARY_PROPERTY, "false");
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setParams(params);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);

    // only u1
    Assert.assertEquals(1, result.getRows().size());
    Assert.assertTrue(Double.parseDouble(result.getRows().get(0)[1].toString()) - 0.3999 < 0.01);
    Assert.assertTrue(StringUtils.isEmpty(result.getRows().get(0)[3].toString()));
  }

  @Test
  public void test9() {
    String dsl =
        "Define (s:Attribute1.Name138)-[p:xuanguFeature]->(o:Float) {\n"
            + "\tGraphStructure {\n"
            + "    \t(s)-[:p3]->(t:Attribute1.Name142)\n"
            + "    }\n"
            + "    Rule {\n"
            + "    \tv = t.stock/t.total\n"
            + "        R1(\"必须大于20%\"): v > 0.2\n"
            + "        o = v\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t(s:Attribute1.Name138)\n"
            + "}\n"
            + "Rule {\n"
            + "    v2 = s.xuanguFeature\n"
            + "}\n"
            + "Action {\n"
            + "  get(s.id, s.xuanguFeature, v2)\n"
            + "}";
    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("Attribute1.Name138", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "Attribute1.Name142",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "stock", "total")));
    schema.put(
        "Attribute1.Name138_p3_Attribute1.Name142",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("transDate")));

    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerABMLocalTest$GraphLoader8");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    // 开启子查询
    params.put(ConfigKey.KG_REASONER_BINARY_PROPERTY, "false");
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setParams(params);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);

    // only u1
    Assert.assertEquals(1, result.getRows().size());
    Assert.assertTrue(Double.parseDouble(result.getRows().get(0)[1].toString()) - 0.3999 < 0.01);
  }

  public static class GraphLoader8 extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("A", "Attribute1.Name138"),
          constructionVertex("B", "Attribute1.Name142", "stock", 0.02, "total", 0.05));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(constructionEdge("A", "p3", "B"));
    }
  }

  @Test
  public void test10() {
    String dsl =
        "//定义额外分支\n"
            + "Define (s:RAC.Param)-[p:hasOtherFeature]->(o:Boolean) {\n"
            + "\tGraphStructure {\n"
            + "    \t(A:RAC.Param)-[:param2link]->(L:RAC.LinkID)-[:link2param]->(s),\n"
            + "        (A)-[:param2feature]->(F:RAC.Feature)\n"
            + "    }\n"
            + "    Rule {\n"
            + "    \tR1: F.featureCode == \"SESSION\"\n"
            + "    \t    \to = rule_value(s.resultFrom == \"RAC\", true, false)\n\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "    ServiceApi [RAC.ServiceApi, __start__=\"true\"]\n"
            + "    A [RAC.Param]\n"
            + "    B [RAC.Param]\n"
            + "    ServiceApi -> A [api2param]\n"
            + "    L [RAC.LinkID]\n"
            + "    A->L [param2link]\n"
            + "    L->B [link2param]\n"
            + "    F [RAC.Feature]\n"
            + "    B -> F [param2feature]\n"
            + "}\n"
            + "Rule {\n"
            + "\tR1: B.hasOtherFeature != true\n"
            + "}\n"
            + "Action {\n"
            + "\tget(ServiceApi.id, B.id, B.hasOtherFeature)\n"
            + "}";
    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
        "RAC.Param", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "resultFrom")));
    schema.put(
        "RAC.LinkID",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "stock", "total")));
    schema.put(
        "RAC.ServiceApi",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "stock", "total")));
    schema.put(
        "RAC.Feature",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "stock", "featureCode")));
    schema.put(
        "RAC.Param_param2link_RAC.LinkID",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("transDate")));
    schema.put(
        "RAC.LinkID_link2param_RAC.Param",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("transDate")));
    schema.put(
        "RAC.ServiceApi_api2param_RAC.Param",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("transDate")));
    schema.put(
        "RAC.Param_param2feature_RAC.Feature",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("transDate")));

    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerABMLocalTest$GraphLoader10");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    // 开启子查询
    params.put(ConfigKey.KG_REASONER_BINARY_PROPERTY, "false");
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setParams(params);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);

    // only u1
    Assert.assertEquals(0, result.getRows().size());
  }

  public static class GraphLoader10 extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("A", "RAC.Param", "resultFrom", "RAC"),
          constructionVertex("A1", "RAC.Param", "resultFrom", "RAC"),
          constructionVertex("B", "RAC.Param", "resultFrom", "RAC"),
          constructionVertex("L", "RAC.LinkID"),
          constructionVertex("L1", "RAC.LinkID"),
          constructionVertex("F", "RAC.Feature"),
          constructionVertex("F1", "RAC.Feature", "featureCode", "SESSION"),
          constructionVertex("BF", "RAC.Feature"),
          constructionVertex("S", "RAC.ServiceApi"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionEdge("S", "api2param", "A"),
          constructionEdge("A", "param2link", "L"),
          constructionEdge("L", "link2param", "B"),
          constructionEdge("B", "param2feature", "BF"),
          constructionEdge("L1", "link2param", "B"),
          constructionEdge("A1", "param2link", "L1"),
          constructionEdge("A", "param2feature", "F"),
          constructionEdge("A1", "param2feature", "F1"));
    }
  }

  @Test
  public void test7() {
    String dsl =
        "GraphStructure {\n"
            + "(A:CustFundKG.Account)-[:expand_linked_alipay_id(A.id)]->(B:CustFundKG.Alipay)-[t:consume]->(C:CustFundKG.Alipay|CustFundKG.BankCard)\n"
            + "}\n"
            + "Rule {\n"
            + "\t\n"
            + "}\n"
            + "Action {\n"
            + "  get(B.id, t.payDate) \n"
            + "}";
    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("CustFundKG.Account", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("CustFundKG.Alipay", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("CustFundKG.BankCard", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "CustFundKG.Alipay_consume_CustFundKG.Alipay",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("payDate")));
    schema.put(
        "CustFundKG.Alipay_consume_CustFundKG.BankCard",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("payDate")));

    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerABMLocalTest$GraphLoader6");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    task.setParams(params);
    List<Tuple2<String, String>> startIdList = new ArrayList<>();
    startIdList.add(new Tuple2<>("2088xxx", "CustFundKG.Account"));
    task.setStartIdList(startIdList);
    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);

    // only u1
    Assert.assertEquals(2, result.getRows().size());
  }

  public static class GraphLoader6 extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("2088xxx", "CustFundKG.Account"),
          constructionVertex("2088xxx", "CustFundKG.BankCard"),
          constructionVertex("2088xxx", "CustFundKG.Alipay"),
          constructionVertex("2088xx1", "CustFundKG.BankCard"),
          constructionVertex("2088xx1", "CustFundKG.Alipay"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionVersionEdge(
              "2088xxx_CustFundKG.Alipay",
              "consume",
              "2088xx1_CustFundKG.Alipay",
              1L,
              "payDate",
              123),
          constructionVersionEdge(
              "2088xxx_CustFundKG.Alipay",
              "consume",
              "2088xx1_CustFundKG.BankCard",
              1L,
              "payDate",
              321));
    }
  }

  @Test
  public void test711() {
    String dsl =
        "GraphStructure {\n"
            + "(A:CustFundKG.Account)-[:expand_linked_alipay_id(A.prop)]->(B:CustFundKG.Alipay)-[t:consume]->(C:CustFundKG.Alipay|CustFundKG.BankCard)\n"
            + "}\n"
            + "Rule {\n"
            + "\t\n"
            + "}\n"
            + "Action {\n"
            + "  get(B.id, t.payDate) \n"
            + "}";
    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
        "CustFundKG.Account", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "prop")));
    schema.put("CustFundKG.Alipay", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("CustFundKG.BankCard", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "CustFundKG.Alipay_consume_CustFundKG.Alipay",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("payDate")));
    schema.put(
        "CustFundKG.Alipay_consume_CustFundKG.BankCard",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("payDate")));

    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerABMLocalTest$GraphLoader6");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    task.setParams(params);
    List<Tuple2<String, String>> startIdList = new ArrayList<>();
    startIdList.add(new Tuple2<>("2088xxx", "CustFundKG.Account"));
    task.setStartIdList(startIdList);
    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);

    // only u1
    Assert.assertEquals(0, result.getRows().size());
  }

  @Test
  public void test3() {
    String dsl =
        "Define (s:CustFundKG.Account)-[p:aggTransAmountNumByDay]->(o:Boolean) {\n"
            + "    GraphStructure {\n"
            + "        (u:CustFundKG.Account)<-[t:accountFundContact]-(s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "    \tR1(\"当月交易\"): date_diff(from_unix_time(now(), 'yyyyMMdd'),from_unix_time(cast_type(t.transDate,'bigint'), 'yyyyMMdd')) <= 30\n"
            + "    \tao = group(s).trans_count_by_day(t, \"transDate\",\"s\", 1, \"large\")\n"
            + "    \t o = rule_value(ao>1,true,false)        \n"
            + "    }\n"
            + "}";
    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("CustFundKG.Account", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "CustFundKG.Account_accountFundContact_CustFundKG.Account",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("transDate")));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerABMLocalTest$GraphLoader2");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    task.setParams(params);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);

    // only u1
    Assert.assertEquals(1, result.getVertexList().size());
    Assert.assertEquals(
        result.getVertexList().get(0).getValue().get("aggTransAmountNumByDay"), true);
  }

  @Test
  public void test4() {
    String dsl =
        "Define (s:CustFundKG.Account)-[p:aggTransAmountNumByDay]->(o:Boolean) {\n"
            + "    GraphStructure {\n"
            + "        (u:CustFundKG.Account)<-[t:accountFundContact]-(s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "    \tR1(\"当月交易\"): date_diff(from_unix_time(now(), 'yyyyMMdd'),from_unix_time(cast_type(t.transDate,'bigint')/1000, 'yyyyMMdd')) <= 30\n"
            + "    \tao = group(s).trans_count_by_day(t, \"transDate\",\"ms\", 3, \"small\")\n"
            + "    \t o = rule_value(ao>1,true,false)        \n"
            + "    }\n"
            + "}";
    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("CustFundKG.Account", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "CustFundKG.Account_accountFundContact_CustFundKG.Account",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("transDate")));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerABMLocalTest$GraphLoader3");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    task.setParams(params);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);

    // only u1
    Assert.assertEquals(1, result.getVertexList().size());
    Assert.assertEquals(
        result.getVertexList().get(0).getValue().get("aggTransAmountNumByDay"), true);
  }

  @Test
  public void test5() {
    String dsl =
        "Define (s:CustFundKG.Account)-[p:aggTransAmountNumByDay]->(o:Boolean) {\n"
            + "    GraphStructure {\n"
            + "        (u:CustFundKG.Account)<-[t:accountFundContact]-(s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "    \tR1(\"当月交易\"): date_diff(from_unix_time(now(), 'yyyyMMdd'),from_unix_time(cast_type(t.transDate,'bigint')/1000/1000, 'yyyyMMdd')) <= 30\n"
            + "    \tao = group(s).trans_count_by_day(t, \"transDate\",\"us\", 3, \"small\")\n"
            + "    \t o = rule_value(ao>1,true,false)        \n"
            + "    }\n"
            + "}";
    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("CustFundKG.Account", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "CustFundKG.Account_accountFundContact_CustFundKG.Account",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("transDate")));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerABMLocalTest$GraphLoader4");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    task.setParams(params);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);

    // only u1
    Assert.assertEquals(1, result.getVertexList().size());
    Assert.assertEquals(
        result.getVertexList().get(0).getValue().get("aggTransAmountNumByDay"), false);
  }

  @Test
  public void test6() {
    String dsl =
        "Define (s:CustFundKG.Account)-[p:aggTransAmountNumByDay]->(o:Boolean) {\n"
            + "    GraphStructure {\n"
            + "        (u:CustFundKG.Account)<-[t:accountFundContact]-(s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "    \tR1(\"当月交易\"): date_diff(from_unix_time(now(), 'yyyyMMdd'),from_unix_time(cast_type(t.transDate,'bigint')/1000/1000, 'yyyyMMdd')) <= 30\n"
            + "    \tao = group(s).trans_count_by_day(t, \"transDate\",\"xx\", 3, \"small\")\n"
            + "    \t o = rule_value(ao>1,true,false)        \n"
            + "    }\n"
            + "}";
    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("CustFundKG.Account", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "CustFundKG.Account_accountFundContact_CustFundKG.Account",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("transDate")));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerABMLocalTest$GraphLoader4");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    task.setParams(params);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);

    // only u1
    Assert.assertTrue(result.getErrMsg().contains("time unit need in s/ms/us, but this is xx"));
  }

  public static class GraphLoader4 extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("A", "CustFundKG.Account"),
          constructionVertex("B", "CustFundKG.Account"),
          constructionVertex("C1", "CustFundKG.Account"),
          constructionVertex("C2", "CustFundKG.Account"),
          constructionVertex("C3", "CustFundKG.Account"),
          constructionVertex("C4", "CustFundKG.Account"),
          constructionVertex("C5", "CustFundKG.Account"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionVersionEdge(
              "A",
              "accountFundContact",
              "B",
              1L,
              "transDate",
              (System.currentTimeMillis() - 3600 * 48 * 1000) * 1000,
              "amount",
              10),
          constructionVersionEdge(
              "A",
              "accountFundContact",
              "B",
              2L,
              "transDate",
              (System.currentTimeMillis() - 3600 * 48 * 1000) * 1000,
              "amount",
              10),
          constructionVersionEdge(
              "A",
              "accountFundContact",
              "B",
              3L,
              "transDate",
              System.currentTimeMillis() * 1000 - 400,
              "amount",
              10),
          constructionVersionEdge(
              "A",
              "accountFundContact",
              "B",
              4L,
              "transDate",
              System.currentTimeMillis() * 1000 - 400,
              "amount",
              10),
          constructionVersionEdge(
              "A",
              "accountFundContact",
              "C1",
              4L,
              "transDate",
              System.currentTimeMillis() * 1000 - 400,
              "amount",
              10),
          constructionVersionEdge(
              "A",
              "accountFundContact",
              "C1",
              4L,
              "transDate",
              System.currentTimeMillis() * 1000 - 400,
              "amount",
              10),
          constructionVersionEdge(
              "A",
              "accountFundContact",
              "C2",
              4L,
              "transDate",
              System.currentTimeMillis() * 1000 - 400,
              "amount",
              15),
          constructionVersionEdge(
              "A",
              "accountFundContact",
              "C3",
              4L,
              "transDate",
              System.currentTimeMillis() * 1000 - 400,
              "amount",
              14),
          constructionVersionEdge(
              "A",
              "accountFundContact",
              "C4",
              4L,
              "transDate",
              System.currentTimeMillis() * 1000 - 400,
              "amount",
              13),
          constructionVersionEdge(
              "A",
              "accountFundContact",
              "C5",
              4L,
              "transDate",
              System.currentTimeMillis() * 1000 - 400,
              "amount",
              1));
    }
  }

  public static class GraphLoader3 extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("A", "CustFundKG.Account"),
          constructionVertex("B", "CustFundKG.Account"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionVersionEdge(
              "A", "accountFundContact", "B", 1L, "transDate", System.currentTimeMillis() - 400),
          constructionVersionEdge(
              "A", "accountFundContact", "B", 2L, "transDate", System.currentTimeMillis() - 400),
          constructionVersionEdge(
              "A",
              "accountFundContact",
              "B",
              3L,
              "transDate",
              System.currentTimeMillis() - 3600 * 48 * 1000),
          constructionVersionEdge(
              "A",
              "accountFundContact",
              "B",
              4L,
              "transDate",
              System.currentTimeMillis() - 3600 * 48 * 1000));
    }
  }

  public static class GraphLoader2 extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("A", "CustFundKG.Account"),
          constructionVertex("B", "CustFundKG.Account"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionVersionEdge(
              "A",
              "accountFundContact",
              "B",
              1L,
              "transDate",
              System.currentTimeMillis() / 1000 - 3600),
          constructionVersionEdge(
              "A",
              "accountFundContact",
              "B",
              2L,
              "transDate",
              System.currentTimeMillis() / 1000 - 3600),
          constructionVersionEdge(
              "A",
              "accountFundContact",
              "B",
              3L,
              "transDate",
              System.currentTimeMillis() / 1000 - 3600 * 48),
          constructionVersionEdge(
              "A",
              "accountFundContact",
              "B",
              4L,
              "transDate",
              System.currentTimeMillis() / 1000 - 3600 * 48));
    }
  }

  private Catalog initABMSchema() {
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
        "ABM.Pkg",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "markResult", "algoMarkResult")));
    schema.put("ABM.BundleApp", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ABM.BundleAppFamily", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "ABM.Apdid",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "insPkgToolsLabel", "insPkgToolsLabelCnt")));
    schema.put(
        "ABM.User",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "hunterLabelCount", "hunterLabel")));
    schema.put(
        "ABM.Pkg_pkg2bundleApp_ABM.BundleApp",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "ABM.BundleApp_belong_ABM.BundleAppFamily",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "ABM.Apdid_install_ABM.Pkg", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("rn")));
    schema.put(
        "ABM.User_acc2apdid_ABM.Apdid", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "ABM.Apdid_acc2apdid_ABM.User", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    return catalog;
  }

  public static class GraphLoader extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("BundleAppFamily", "ABM.BundleAppFamily"),
          constructionVertex("BundleApp", "ABM.BundleApp"),
          constructionVertex("Pkg1", "ABM.Pkg"),
          constructionVertex("Pkg2", "ABM.Pkg"),
          constructionVertex("Pkg3", "ABM.Pkg"),
          constructionVertex("Pkg4", "ABM.Pkg"),
          constructionVertex(
              "Apdid", "ABM.Apdid", "insPkgToolsLabel", "abc", "insPkgToolsLabelCnt", 3),
          constructionVertex("user", "ABM.User", "hunterLabel", "abc", "hunterLabelCount", 5),
          constructionVertex("Pkg", "ABM.Pkg", "algoMarkResult", "UNSAFE"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionEdge("Pkg", "pkg2bundleApp", "BundleApp"),
          constructionEdge("Pkg1", "pkg2bundleApp", "BundleApp"),
          constructionEdge("Pkg2", "pkg2bundleApp", "BundleApp"),
          constructionEdge("Pkg3", "pkg2bundleApp", "BundleApp"),
          constructionEdge("Pkg4", "pkg2bundleApp", "BundleApp"),
          constructionEdge("BundleApp", "belong", "BundleAppFamily"),
          constructionEdge("Apdid", "install", "Pkg1", "rn", 1),
          constructionEdge("Apdid", "install", "Pkg2", "rn", 1000),
          constructionEdge("Apdid", "install", "Pkg3", "rn", 3),
          constructionEdge("Apdid", "install", "Pkg4", "rn", 2),
          constructionEdge("user", "acc2apdid", "Apdid"));
    }
  }
}
