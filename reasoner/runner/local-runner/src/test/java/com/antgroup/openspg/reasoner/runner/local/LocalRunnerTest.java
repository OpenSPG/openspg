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

package com.antgroup.openspg.reasoner.runner.local;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.utils.PropertyUtil;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog;
import com.antgroup.openspg.reasoner.progress.ProgressReport;
import com.antgroup.openspg.reasoner.runner.ConfigKey;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerTask;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import com.antgroup.openspg.reasoner.utils.SimpleObjSerde;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import scala.Tuple2;

public class LocalRunnerTest {

  @Test
  public void doTestAvg2() {
    String dsl =
        "Define (s: AttributePOC.User)-[p: holdPMProduct]->(o: AttributePOC.PortfolioManager) {\n"
            + "    GraphStructure {\n"
            + "\t(s)-[p1:holdProduct]->(u1:AttributePOC.Fund)\n"
            + "    (o)-[p2:underControl]->(u1)\n"
            + "    }\n"
            + "    Rule {\n"
            + "    }\n"
            + "}";
    dsl =
        dsl
            + "GraphStructure {\n"
            + "(s:AttributePOC.User)-[p:followPM]->(o:AttributePOC.PortfolioManager),\n"
            + "(s)-[p2:holdPMProduct]->(o:AttributePOC.PortfolioManager)\n"
            + "    }\n"
            + "    Rule {\n"
            + "\n"
            + "    }\n"
            + "Action {\n"
            + "  get(s.id, p.times, p.holdRet)   // 获取A和B两个导演的名字，调试和在线执行时直接看渲染出来的子图\n"
            + "}";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass("com.antgroup.openspg.reasoner.runner.local.loader.TestFundGraphLoader");
    task.getParams().put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true);
    task.getParams().put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setStartIdList(Lists.newArrayList(new Tuple2<>("u1", "AttributePOC.User")));

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("AttributePOC.User", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "AttributePOC.PortfolioManager",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("AttributePOC.Fund", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "AttributePOC.User_holdProduct_AttributePOC.Fund",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("holdRet")));
    schema.put(
        "AttributePOC.PortfolioManager_underControl_AttributePOC.Fund",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("holdRet")));
    schema.put(
        "AttributePOC.User_followPM_AttributePOC.PortfolioManager",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("holdRet", "times")));

    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    Assert.assertEquals(result.getRows().size(), 1);
    Assert.assertEquals(result.getRows().get(0)[0], "u1");
    Assert.assertEquals(result.getRows().get(0)[1], "4");
    clear();
  }

  @Test
  public void doTestAvg() {
    String dsl =
        "Define (s: AttributePOC.User)-[p: holdPMProduct2]->(o: AttributePOC.PortfolioManager) {\n"
            + "    GraphStructure {\n"
            + "\t(s)-[p1:holdProduct]->(u1:AttributePOC.Fund)<-[p2:underControl]-(o)\n"
            + "    }\n"
            + "    Rule {\n"
            + "        avgProfit = group(s,o).avg(p1.holdRet)\n"
            + "        p.avgProfit = avgProfit\n"
            + "    }\n"
            + "}";
    dsl +=
        "GraphStructure {\n"
            + "  (s: AttributePOC.User)-[p: holdPMProduct2]->(o: AttributePOC.PortfolioManager)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "  get(s.id, p.avgProfit)\n"
            + "}";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass("com.antgroup.openspg.reasoner.runner.local.loader.TestFundGraphLoader");

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("AttributePOC.User", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "AttributePOC.PortfolioManager",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("AttributePOC.Fund", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "AttributePOC.User_holdProduct_AttributePOC.Fund",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("holdRet")));
    schema.put(
        "AttributePOC.PortfolioManager_underControl_AttributePOC.Fund",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("holdRet")));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);
    task.getParams().put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true);
    task.getParams().put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    Assert.assertEquals(result.getRows().size(), 1);
    Assert.assertEquals(result.getRows().get(0)[0], "u1");
    Assert.assertEquals(result.getRows().get(0)[1], "0.02");
    clear();
  }

  @Test
  public void doTestMin() {
    String dsl =
        "Define (s: AttributePOC.User)-[p: holdPMProduct2]->(o: AttributePOC.PortfolioManager) {\n"
            + "    GraphStructure {\n"
            + "\t(s)-[p1:holdProduct]->(u1:AttributePOC.Fund)<-[p2:underControl]-(o)\n"
            + "    }\n"
            + "    Rule {\n"
            + "        avgProfit = group(s,o).min(p1.holdRet)\n"
            + "        p.avgProfit = avgProfit\n"
            + "    }\n"
            + "}";
    dsl +=
        "GraphStructure {\n"
            + "  (s: AttributePOC.User)-[p: holdPMProduct2]->(o: AttributePOC.PortfolioManager)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "  get(s.id,p.avgProfit)\n"
            + "}";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass("com.antgroup.openspg.reasoner.runner.local.loader.TestFundGraphLoader");

    // use test catalog
    /*
    dslParams.put("projId", "363000092");
    Catalog catalog = CatalogFactory.createCatalog(dslParams);
     */

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("AttributePOC.User", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "AttributePOC.PortfolioManager",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("AttributePOC.Fund", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "AttributePOC.User_holdProduct_AttributePOC.Fund",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("holdRet")));
    schema.put(
        "AttributePOC.PortfolioManager_underControl_AttributePOC.Fund",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("holdRet")));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    Assert.assertEquals(result.getRows().size(), 1);
    Assert.assertEquals(result.getRows().get(0)[0], "u1");
    Assert.assertEquals(result.getRows().get(0)[1], "0.02");
    clear();
  }

  @Test
  public void doTestMax() {
    String dsl =
        "Define (s: AttributePOC.User)-[p: holdPMProduct2]->(o: AttributePOC.PortfolioManager) {\n"
            + "    GraphStructure {\n"
            + "\t(s)-[p1:holdProduct]->(u1:AttributePOC.Fund)<-[p2:underControl]-(o)\n"
            + "    }\n"
            + "    Rule {\n"
            + "        avgProfit = group(s,o).max(p1.holdRet)\n"
            + "        p.avgProfit = avgProfit\n"
            + "    }\n"
            + "}";
    dsl +=
        "GraphStructure {\n"
            + "  (s: AttributePOC.User)-[p: holdPMProduct2]->(o: AttributePOC.PortfolioManager)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "  get(s.id,p.avgProfit)\n"
            + "}";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass("com.antgroup.openspg.reasoner.runner.local.loader.TestFundGraphLoader");

    // use test catalog
    /*
    dslParams.put("projId", "363000092");
    Catalog catalog = CatalogFactory.createCatalog(dslParams);
     */

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("AttributePOC.User", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "AttributePOC.PortfolioManager",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("AttributePOC.Fund", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "AttributePOC.User_holdProduct_AttributePOC.Fund",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("holdRet")));
    schema.put(
        "AttributePOC.PortfolioManager_underControl_AttributePOC.Fund",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("holdRet")));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    Assert.assertEquals(result.getRows().size(), 1);
    Assert.assertEquals(result.getRows().get(0)[0], "u1");
    Assert.assertEquals(result.getRows().get(0)[1], "0.02");
    clear();
  }

  @Test
  public void doTestLocalRunnerDependency() {

    String dsl =
        "Define (user:TuringCore.AlipayUser)-[teCount:teCount]->(o:Long) {\n"
            + "\tGraphStructure {\n"
            + "\t\t(user) -[pwl:workLoc]-> (aa1:CKG.AdministrativeArea)\n"
            + "\t\t(te:TuringCore.TravelEvent) -[ptler:traveler]-> (user)\n"
            + "\t\t(te) -[ptm:travelMode]-> (tm:TuringCore.TravelMode)\n"
            + "\t\t(te) -[pte:travelEndpoint]-> (aa1:CKG.AdministrativeArea)\n"
            + "\t}\n"
            + "  Rule {\n"
            + "    R1('常驻地在杭州'): aa1.id == '中国-浙江省-杭州市'\n"
            + "  \tR2('工作日上班时间通勤用户'): dayOfWeek(te.eventTime) in [1, 2, 3, 4, 5] \n"
            + "            and hourOfDay(te.eventTime) in [6, 7, 8, 9, 10, 17, 18, 19, 20, 21] \n"
            + "    R3('公交地铁'): tm.id in ['bus', 'train']\n"
            + "    teCount('出行次数') = group(user).count(te.id)\n"
            + "    o=teCount\n"
            + "  }\n"
            + "}";

    dsl +=
        "\n"
            + "GraphStructure {\n"
            + "  (user:TuringCore.AlipayUser)\n"
            + "}\n"
            + "Rule{\n"
            + "  R1: user.teCount >= 3\n"
            + "}\n"
            + "Action{\n"
            + "  get(user.id, user.teCount)\n"
            + "}";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.loader.TestCrowdGraphLoader");

    // use test catalog
    /*
    dslParams.put("projId", "363000092");
    Catalog catalog = CatalogFactory.createCatalog(dslParams);
     */

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
        "CKG.AdministrativeArea", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "TuringCore.TravelMode", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "TuringCore.AlipayUser", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "TuringCore.TravelEvent",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "eventTime")));
    schema.put(
        "TuringCore.AlipayUser_workLoc_CKG.AdministrativeArea",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "TuringCore.TravelEvent_traveler_TuringCore.AlipayUser",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "TuringCore.TravelEvent_travelEndpoint_CKG.AdministrativeArea",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "TuringCore.TravelEvent_travelMode_TuringCore.TravelMode",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    Assert.assertEquals(result.getRows().size(), 1);
    Assert.assertEquals(result.getRows().get(0)[0], "u1");
    Assert.assertEquals(result.getRows().get(0)[1], "3");

    task.getParams().put(ConfigKey.KG_REASONER_OUTPUT_GRAPH, true);
    LocalReasonerResult result2 = runner.run(task);
    System.out.println(result2);
    Assert.assertEquals(result2.isGraphResult(), true);
    Assert.assertEquals(result2.getVertexList().size(), 1);
    clear();
  }

  @Test
  public void testComplexMultiDefineDsl() {
    String dsl =
        "// 当月交易量\n"
            + "Define (s:CustFundKG.Account)-[p:cur_month_num]->(o:Int) {\n"
            + "    GraphStructure {\n"
            + "        (u:CustFundKG.Account)-[t:accountFundContact]-(s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "      \tR1(\"当月交易量\"): date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate) <= 20\n"
            + "        o = group(s).count(u.id)\n"
            + "    }\n"
            + "}\n"
            + "// 次月交易量\n"
            + "Define (s:CustFundKG.Account)-[p:last_month_num]->(o:Int) {\n"
            + "    GraphStructure {\n"
            + "        (u:CustFundKG.Account)-[t:accountFundContact]-(s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "      \tdate_delta = date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate)\n"
            + "      \tR1(\"次月交易量\"): date_delta > 20 && date_delta <=40\n"
            + "        o = group(s).count(u.id)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "// 次次月交易量\n"
            + "Define (s:CustFundKG.Account)-[p:last_last_month_num]->(o:Int) {\n"
            + "    GraphStructure {\n"
            + "        (u:CustFundKG.Account)-[t:accountFundContact]-(s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "      \tdate_delta = date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate)\n"
            + "      \tR1(\"次月交易量\"): date_delta > 40 && date_delta <=60\n"
            + "        o = group(s).count(u.id)\n"
            + "    }\n"
            + "}\n"
            + "// 倍数\n"
            + "Define (s:CustFundKG.Account)-[p:last_trans_multiple]->(o:Float) {\n"
            + "\t\tGraphStructure {\n"
            + "        (s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "      \tmultiple = s.last_month_num*1.0 / s.last_last_month_num\n"
            + "        o = multiple\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "// 倍数\n"
            + "Define (s:CustFundKG.Account)-[p:cur_trans_multiple]->(o:Float) {\n"
            + "\t\tGraphStructure {\n"
            + "        (s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "      \tmultiple = s.cur_month_num*1.0 / s.last_month_num\n"
            + "        o = multiple\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "Define (s:CustFundKG.Account)-[p:is_trans_raise_more_after_down]->(o:Boolean) {\n"
            + "\t\tGraphStructure {\n"
            + "        (s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "      \tR1(\"T月交易量级超过T-1月交易量级3倍\"): s.last_trans_multiple >=3\n"
            + "      \tR2(\"T+1月交易量级小于T月交易量级的1/2\"): s.cur_trans_multiple <0.5\n"
            + "      \to = rule_value(R1 && R2, true, false)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "        (s:CustFundKG.Account)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id, s.is_trans_raise_more_after_down, s.cur_trans_multiple, s.last_trans_multiple, s"
            + ".last_last_month_num, s.last_month_num, s.cur_month_num)\n"
            + "}\n";
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.loader.TestMultiVersionGraphLoader");

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("CustFundKG.Account", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "CustFundKG.Account_accountFundContact_CustFundKG.Account",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("sumAmt", "transDate")));

    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.getParams().put(ConfigKey.KG_REASONER_CATALOG, SimpleObjSerde.ser(catalog));

    task.setStartIdList(Lists.newArrayList(new Tuple2<>("1", "CustFundKG.Account")));

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println("##########################");
    System.out.println(result);
    System.out.println("##########################");

    clear();
  }

  @Test
  public void testEdgePropertyDefineDsl2() {
    String dsl =
        "Define (s:InsProduct.Product)-[p:liabilityLight]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "  (s)-[p:includeLiability]->(l:InsProduct.Liability)\n"
            + "}\n"
            + "Rule {\n"
            + "  R1: l.name like \"%轻症疾病保险金%\"\n"
            + "  o = true\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (s:InsProduct.Product)-[p:liabilityMid]->(o:Boolean) {\n"
            + "GraphStructure {\n"
            + "  (s)-[p:includeLiability]->(l:InsProduct.Liability)\n"
            + "}\n"
            + "Rule {\n"
            + "  R1: l.name like \"%中症疾病保险金%\"\n"
            + "  o = true\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "  A [InsProduct.Product]\n"
            + "}\n"
            + "Rule {\n"
            + "  R1: A.liabilityLight == null || A.liabilityMid == null\n"
            + "  output = rule_value(R1, true, false)\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.name,output,A.liabilityLight, A.liabilityMid) \n"
            + "}";
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.loader.TestInsProductGraphLoader");

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
        "InsProduct.Product", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "name")));
    schema.put(
        "InsProduct.Liability",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "name")));
    schema.put(
        "InsProduct.Product_includeLiability_InsProduct.Liability",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));

    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.getParams().put(ConfigKey.KG_REASONER_CATALOG, SimpleObjSerde.ser(catalog));
    task.getParams().put(ConfigKey.KG_REASONER_BINARY_PROPERTY, false);
    task.getParams().put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);

    task.setStartIdList(Lists.newArrayList(new Tuple2<>("保险产品", "InsProduct.Product")));

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println("##########################");
    System.out.println(result);
    System.out.println("##########################");

    clear();
  }

  @Test
  public void testEdgePropertyDefineDsl() {
    String dsl =
        "//1 先定义每两个用户间进行聚合交易\n"
            + "Define (s:CustFundKG.Account)-[p:tranTargetUser]->(o:CustFundKG.Account) {\n"
            + "    GraphStructure {\n"
            + "        (o)<-[t:accountFundContact]-(s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "      tran_count = group(s,o).count(t.sumAmt)\n"
            + "      p.tran_count = tran_count\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "\n"
            + "Define (s:CustFundKG.Account)-[p:is_repeat_tran_user]->(o:Int) {\n"
            + "    GraphStructure {\n"
            + "         (s)-[t:tranTargetUser]->(u:CustFundKG.Account)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        user_num(\"交易笔数大于3的重复用户个数\") = group(s).countIf(t.tran_count>=3, u.id)\n"
            + "        R1(\"超过10个\"): user_num > 10\n"
            + "        o = rule_value(R1, true, false)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "//获取结果\n"
            + "GraphStructure {\n"
            + "\ts [CustFundKG.Account]\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action{\n"
            + "\tget(s.id, s.is_repeat_tran_user)\n"
            + "}";
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.loader.TestMultiVersionGraphLoader");

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("CustFundKG.Account", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "CustFundKG.Account_accountFundContact_CustFundKG.Account",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("sumAmt")));

    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.getParams().put(ConfigKey.KG_REASONER_CATALOG, SimpleObjSerde.ser(catalog));

    task.setStartIdList(Lists.newArrayList(new Tuple2<>("1", "CustFundKG.Account")));

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println("##########################");
    System.out.println(result);
    System.out.println("##########################");

    clear();
  }

  @Test
  public void testComplexMultiTimeUDFDefineDsl() {
    String dsl =
        "//1 先定义流入资金总数\n"
            + "Define (s:CustFundKG.Account)-[p:total_in_trans_num]->(o:Int) {\n"
            + "    GraphStructure {\n"
            + "        (u:CustFundKG.Account)-[t:accountFundContact]->(s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        o = group(s).count(t.sumAmt)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "//2 定义整百流入笔数\n"
            + "Define (s:CustFundKG.Account)-[p:multiples_hundred_in_trans_num]->(o:Int) {\n"
            + "    GraphStructure {\n"
            + "        (u:CustFundKG.Account)-[t:accountFundContact]->(s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "    \tR1(\"必须是整百交易\"): t.sumAmt % 100 == 0\n"
            + "        o = group(s).count(t.sumAmt)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "// 判断是否汇聚赌博\n"
            + "Define (s:CustFundKG.Account)-[p:is_pooling_gambling_funds]->(o:Float) {\n"
            + "    GraphStructure {\n"
            + "        (s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        R0(\"存在流入和整百资金\"): s.multiples_hundred_in_trans_num != null && s.total_in_trans_num != null\n"
            + "    \tR1(\"流入整百金额笔数大于2比\"): s.multiples_hundred_in_trans_num > 2\n"
            + "        R2(\"整百交易占比大于2%\"): s.multiples_hundred_in_trans_num /cast_type(s.total_in_trans_num,'double') > 0.02\n"
            + "        o = rule_value(R2, true, false)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "//获取结果\n"
            + "GraphStructure {\n"
            + "\ts [CustFundKG.Account]\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action{\n"
            + "\tget(s.id, s.is_pooling_gambling_funds, s.multiples_hundred_in_trans_num, s.total_in_trans_num)\n"
            + "}";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.loader.TestMultiVersionGraphLoader");

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("CustFundKG.Account", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "CustFundKG.Account_accountFundContact_CustFundKG.Account",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("sumAmt")));

    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.getParams().put(ConfigKey.KG_REASONER_CATALOG, SimpleObjSerde.ser(catalog));

    task.setStartIdList(Lists.newArrayList(new Tuple2<>("1", "CustFundKG.Account")));

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println("##########################");
    System.out.println(result);
    System.out.println("##########################");

    clear();
  }

  @Test
  public void doTestComplexDefineDsl() {
    String dsl =
        "// 1\n"
            + "Define (s:DomainFamily)-[p:blackRelateRate]->(o:Pkg) {\n"
            + "    GraphStructure {\n"
            + "        (o)-[:use]->(d:Domain)-[:belong]->(s)\n"
            + "    }\n"
            + "    Rule {\n"
            + "        R1: o.is_black == true\n"
            // + "        domain_num = group(s,o).count(d)\n"
            // + "        p.same_domain_num = domain_num\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "// 2\n"
            + "Define (s:DomainFamily)-[p:total_domain_num]->(o:Int) {\n"
            + "    GraphStructure {\n"
            + "        (s)<-[:belong]-(d:Domain)\n"
            + "    }\n"
            + "    Rule {\n"
            + "        o = group(s).count(d.id)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "// 3\n"
            + "Define (s:Pkg)-[p:target]->(o:User) {\n"
            + "    GraphStructure {\n"
            + "        (s)<-[p1:blackRelateRate]-(df:DomainFamily),\n"
            + "        (df)<-[:belong]-(d:Domain),\n"
            + "        (o)-[:visit]->(d)\n"
            + "    } Rule {\n"
            + "        visit_time = group(o, df).count(d.id)\n"
            + "        R1(\"必须大于1次\"): visit_time >= 1\n"
            + "        R2(\"必须占比大于50%\"): 1.0 * visit_time / df.total_domain_num > 0.2\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "// 4\n"
            + "GraphStructure {\n"
            + "    (s:Pkg)-[p:target]->(o:User)\n"
            + "}\n"
            + "Rule {\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id,o.id)\n"
            + "}";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.loader.TestDefineGraphLoader");

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
        "DomainFamily",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "total_domain_num")));
    schema.put("Pkg", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "is_black")));
    schema.put("Domain", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("User", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("Pkg_use_Domain", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "Domain_belong_DomainFamily", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("User_visit_Domain", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));

    // define
    schema.put(
        "DomainFamily_blackRelateRate_Pkg",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("Pkg_target_User", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.getParams().put(ConfigKey.KG_REASONER_CATALOG, SimpleObjSerde.ser(catalog));

    task.setStartIdList(Lists.newArrayList(new Tuple2<>("black_app_1", "Pkg")));

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println("##########################");
    System.out.println(result);
    System.out.println("##########################");

    clear();
  }

  @Test
  public void doTestWithStart() {
    String dsl =
        "// 查找使用了相同主演的两个导演\n"
            + "GraphStructure {\n"
            + "  (a:ABM.User)-[hasIp:acc2ip]->(ip:ABM.IP)\n"
            + "}\n"
            + "Rule {\n"
            + "  \tR1(\"必选180天以内\"): hasIp.ipUse180dCnt >=2\n"
            + "    R2(\"必须是高危地区\"): ip.country in ['菲律宾','柬埔寨','老挝','日本','香港','台湾','泰国','澳门','越南','马来西亚','印度尼西亚']\n"
            + "    result = rule_value(R2, true, false)\n"
            + "}\n"
            + "Action {\n"
            + "  get(a.id, result, ip.country , hasIp.ipUse180dCnt)   // 获取A和B两个导演的名字，调试和在线执行时直接看渲染出来的子图\n"
            + "}";
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.loader.TestDefineGraphLoader");

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("ABM.User", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ABM.IP", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "country")));
    schema.put(
        "ABM.User_acc2ip_ABM.IP",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("ipUse180dCnt")));

    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.getParams().put(ConfigKey.KG_REASONER_CATALOG, SimpleObjSerde.ser(catalog));
    task.setStartIdList(Lists.newArrayList(new Tuple2<>("user1", "ABM.User")));

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println("##########################");
    System.out.println(result);
    System.out.println("##########################");

    clear();
  }

  @Test
  public void doTestComplexLabelDsl() {
    String dsl =
        "// 查找使用了相同主演的两个导演\n"
            + "GraphStructure {\n"
            + " S [CustFundKG.Account, __start__='true']\n"
            + " A,E [STD.AlipayAccount]\n"
            + " S->A [accountId]\n"
            + " B,C [CustFundKG.Alipay,CustFundKG.BankCard]\n"
            + " B->A [accountId]\n"
            + " B->C [transfer,consume] as t\n"
            + " D [CustFundKG.Account]\n"
            + " D->E [accountId]\n"
            + " C->E [accountId]\n"
            + "}\n"
            + "Rule {\n"
            + "transNum = group(S,D).sum(t.amount)\n"
            + "}\n"
            + "Action {\n"
            + "  get(S.id,D.id, transNum) \n"
            + "}";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.loader.TestMultiLabelGraphLoader");

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
        "CustFundKG.Account",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "accountId")));
    schema.put(
        "CustFundKG.Alipay",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "accountId")));
    schema.put(
        "CustFundKG.BankCard",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "accountId")));
    schema.put("STD.AlipayAccount", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));

    // define
    schema.put(
        "CustFundKG.Account_accountId_STD.AlipayAccount",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "CustFundKG.Alipay_accountId_STD.AlipayAccount",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "CustFundKG.BankCard_accountId_STD.AlipayAccount",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "CustFundKG.BankCard_transfer_CustFundKG.Alipay",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("amount")));
    schema.put(
        "CustFundKG.BankCard_consume_CustFundKG.Alipay",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("amount")));
    schema.put(
        "CustFundKG.BankCard_transfer_CustFundKG.BankCard",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("amount")));
    schema.put(
        "CustFundKG.BankCard_consume_CustFundKG.BankCard",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("amount")));
    schema.put(
        "CustFundKG.Alipay_transfer_CustFundKG.BankCard",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("amount")));
    schema.put(
        "CustFundKG.Alipay_consume_CustFundKG.BankCard",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("amount")));
    schema.put(
        "CustFundKG.Alipay_transfer_CustFundKG.Alipay",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("amount")));
    schema.put(
        "CustFundKG.Alipay_consume_CustFundKG.Alipay",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("amount")));

    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.getParams().put(ConfigKey.KG_REASONER_CATALOG, SimpleObjSerde.ser(catalog));

    task.setStartIdList(Lists.newArrayList(new Tuple2<>("S", "CustFundKG.Account")));

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println("##########################");
    System.out.println(result);
    System.out.println("##########################");
    Assert.assertEquals(1, result.getRows().size());
    clear();
  }

  @Test
  public void doTestSpatioTemporalDsl() {
    String nearbyDsl =
        "GraphStructure{\n"
            + "    (s:PE.JiuZhi)-[e1:nearby(s.shape4, o.shape2, 100)]->(o:PE.JiuZhi)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id)\n"
            + "}";

    Map<String, Object> params = new HashMap<>();
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
        "PE.JiuZhi",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "shape4", "shape2")));
    schema.put("STD.S2CellId", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "PE.JiuZhi_shape2S2CellId_STD.S2CellId",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();

    params.put(ConfigKey.KG_REASONER_CATALOG, SimpleObjSerde.ser(catalog));
    params.put(Constants.START_ALIAS, "s");

    String outputFile = "/tmp/local/runner/" + UUID.randomUUID() + ".csv";

    LocalRunnerMain.main(
        new String[] {
          "-q",
          nearbyDsl,
          "-o",
          outputFile,
          "-g",
          "com.antgroup.openspg.reasoner.runner.local.loader.TestSpatioTemporalGraphLoader",
          "-s",
          "s",
          "-st",
          "st",
          "-start",
          "[[\"MOCK1\",\"PE.JiuZhi\"]]",
          "-params",
          JSON.toJSONString(params)
        });
    List<String[]> rst = RunnerUtil.loadCsvFile(outputFile);
    Assert.assertEquals(2, rst.size());
    clear();
  }

  private void clear() {
    PropertyUtil.reset();
    ProgressReport.clear();
  }
}
