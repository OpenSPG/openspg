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

package com.antgroup.openspg.reasoner.runner.local;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.utils.PropertyUtil;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.catalog.GeneralSemanticRule;
import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog;
import com.antgroup.openspg.reasoner.progress.ProgressReport;
import com.antgroup.openspg.reasoner.recorder.DefaultRecorder;
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
  public void testConceptExpand() {
    String rule1 =
        "Define (s:RiskMining.App)-[p:belongTo]->(o:`RiskMining.TaxOfRiskApp`/`赌博应用`) {\n"
            + "            Structure {\n"
            + "                (s)\n"
            + "            }\n"
            + "            Constraint {\n"
            + "                R1(\"风险标记为赌博\"): s.riskMark like \"%赌博%\"\n"
            + "            }\n"
            + "        }";
    String rule2 =
        "Define (s:RiskMining.Person)-[p:belongTo]->(o:`RiskMining.TaxOfRiskUser`/`赌博App开发者`) {\n"
            + "            Structure {\n"
            + "                (s)-[:developed]->(app:`RiskMining.TaxOfRiskApp`/`赌博应用`)\n"
            + "            }\n"
            + "            Constraint {\n"
            + "            }\n"
            + "        }";
    String dsl = "MATCH (u:`RiskMining.TaxOfRiskUser`/`赌博App开发者`) RETURN u.name";

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
        "RiskMining.App",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "name", "nightTrader")));
    schema.put(
        "RiskMining.Person", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "name")));
    schema.put(
        "RiskMining.TaxOfRiskApp",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "name")));
    schema.put(
        "RiskMining.TaxOfRiskUser",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "name")));
    schema.put(
        "RiskMining.Person_belongTo_RiskMining.TaxOfRiskUser",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "RiskMining.App_belongTo_RiskMining.TaxOfRiskApp",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "RiskMining.Person_developed_RiskMining.App",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));

    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    catalog
        .getGraph("KG")
        .registerRule(
            "RiskMining.App_belongTo_RiskMining.TaxOfRiskApp/赌博应用", new GeneralSemanticRule(rule1));
    catalog
        .getGraph("KG")
        .addEdge(
            "RiskMining.App",
            "belongTo",
            "RiskMining.TaxOfRiskApp/赌博应用",
            Direction.OUT,
            Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()),
            false);
    catalog
        .getGraph("KG")
        .registerRule(
            "RiskMining.Person_belongTo_RiskMining.TaxOfRiskUser/赌博应用",
            new GeneralSemanticRule(rule2));
    catalog
        .getGraph("KG")
        .addEdge(
            "RiskMining.User",
            "belongTo",
            "RiskMining.TaxOfRiskUser/赌博应用",
            Direction.OUT,
            Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()),
            false);
    //    String dsl = rule;
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.loader.TestFanxiqianGraphLoader");
    task.getParams().put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true);
    task.getParams().put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.getParams().put(ConfigKey.KG_REASONER_OUTPUT_GRAPH, true);
    task.setStartIdList(Lists.newArrayList(new Tuple2<>("张三", "Test.User")));
    task.setExecutionRecorder(new DefaultRecorder());
    task.setExecutorTimeoutMs(99999999999999999L);

    task.setCatalog(catalog);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    System.out.println(task.getExecutionRecorder().toReadableString());
  }

  @Test
  public void testCreateConceptInstance() {
    String rule =
        "Define (s:Test.User)-[p:belongTo]->(o:`Test.UserFeature`/`白领`) {\n"
            + "    GraphStructure {\n"
            + "        (s)\n"
            + "    }\n"
            + "    Rule {\n"
            + "        r1(\"属于教师\") = s.nightTrader == 1\n"
            + "        r2(\"属于程序员\") = s.nightTrader == 2\n"
            + "        r3(\"属于医生\") = s.nightTrader == 3\n"
            + "        s_r1 = rule_value(r1, \"教师\", \"\")\n"
            + "        s_r2 = rule_value(r2, \"程序员\", s_r1)\n"
            + "        s_r3 = rule_value(r3, \"医生\", s_r2)\n"
            + "        R: s_r3 != \"\"\n"
            + "    }\n"
            + "    Action {\n"
            + "        sub_concept = createNodeInstance(\n"
            + "            type=Test.UserFeature,\n"
            + "            value={\n"
            + "                id=concat(\"白领-\", s_r3)\n"
            + "            }\n"
            + "        )\n"
            + "        createEdgeInstance(\n"
            + "            src=s,\n"
            + "            dst=sub_concept,\n"
            + "            type=belongTo,\n"
            + "            value={\n"
            + "                __to_id_type__='Test.UserFeature'\n"
            + "                __from_id_type__='Test.User'\n"
            + "            }\n"
            + "        )\n"
            + "    }\n"
            + "}";
    String rule1 =
        "Define (s:Test.User)-[p:belongTo]->(o:`Test.UserFeature`/`学生`) {\n"
            + "    GraphStructure {\n"
            + "        (s)\n"
            + "    }\n"
            + "    Rule {\n"
            + "        r1(\"就读幼儿园\") = s.nightTrader == 1\n"
            + "        r2(\"就读小学\") = s.nightTrader == 2\n"
            + "        r3(\"就读中学\") = s.nightTrader == 3\n"
            + "        s_r1 = rule_value(r1, \"就读幼儿园\", \"\")\n"
            + "        s_r2 = rule_value(r2, \"就读小学\", s_r1)\n"
            + "        s_r3 = rule_value(r3, \"就读中学\", s_r2)\n"
            + "        R: s_r3 != \"\"\n"
            + "    }\n"
            + "    Action {\n"
            + "        sub_concept = createNodeInstance(\n"
            + "            type=Test.UserFeature,\n"
            + "            value={\n"
            + "                id=concat(\"学生-\", s_r3)\n"
            + "            }\n"
            + "        )\n"
            + "        createEdgeInstance(\n"
            + "            src=s,\n"
            + "            dst=sub_concept,\n"
            + "            type=belongTo,\n"
            + "            value={\n"
            + "                __to_id_type__='Test.UserFeature'\n"
            + "                __from_id_type__='Test.User'\n"
            + "            }\n"
            + "        )\n"
            + "    }\n"
            + "}";
    String dsl =
        "match (s:Test.User)-[p:belongTo]->(o:Test.UserFeature)-[p2:newedge]->(o2:Test.TaxOfUserFeature) return s.id,p,o.id, o2.id"
            + "";
    //    String dsl = rule;
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.loader.TestFanxiqianGraphLoader");
    task.getParams().put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true);
    task.getParams().put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.getParams().put(ConfigKey.KG_REASONER_OUTPUT_GRAPH, true);
    task.setStartIdList(Lists.newArrayList(new Tuple2<>("张三", "Test.User")));
    task.setExecutionRecorder(new DefaultRecorder());
    task.setExecutorTimeoutMs(99999999999999999L);

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
        "Test.User",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "name", "nightTrader")));
    schema.put(
        "Test.UserFeature", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "name")));
    schema.put(
        "Test.TaxOfUserFeature",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "name")));

    schema.put(
        "Test.User_belongTo_Test.UserFeature",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));

    schema.put(
        "Test.UserFeature_newedge_Test.TaxOfUserFeature",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));

    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    catalog
        .getGraph("KG")
        .registerRule("Test.User_belongTo_Test.UserFeature/白领", new GeneralSemanticRule(rule));
    catalog
        .getGraph("KG")
        .addEdge(
            "Test.User",
            "belongTo",
            "Test.UserFeature/白领",
            Direction.OUT,
            Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()),
            false);
    catalog
        .getGraph("KG")
        .registerRule("Test.User_belongTo_Test.UserFeature/学生", new GeneralSemanticRule(rule1));
    catalog
        .getGraph("KG")
        .addEdge(
            "Test.User",
            "belongTo",
            "Test.UserFeature/学生",
            Direction.OUT,
            Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()),
            false);
    task.setCatalog(catalog);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    System.out.println(task.getExecutionRecorder().toReadableString());
    Assert.assertEquals(result.getRows().size(), 2);
    Assert.assertEquals(result.getRows().get(0)[0], "张三");
    clear();
  }

  @Test
  public void testx() {
    /*
    String rule = "Define (s:SourceNumber)-[p:longCallContact]->(o:DestNumber) {\n"
            + "                    STRUCTURE {\n"
            + "                        (s)-[:hasRecord]->(r:Record)-[:destNumber]->(o)\n"
            + "                    }\n"
            + "                    CONSTRAINT {\n"
            + "                        maxDuration = group(s,o).max(r.callDuration)\n"
            + "                        R1(\"超长通话\"): maxDuration > 300\n"
            + "                    }\n"
            + "                }";

    String dsl =
            "match (s:SourceNumber)-[p:longCallContact]->(o:DestNumber) return s.id,p,o.id";
     */


    String dsl2 = " GraphStructure{\n"
            + "                        (s)-[:hasRecord]->(r:Record)-[:destNumber]->(o)\n"
            + "                    }\n"
            + "                    Rule {\n"
            + "                        maxDuration = group(s,o).max(r.callDuration)\n"
            + "                        R1(\"超长通话\"): maxDuration > 300\n"
            + "                    }\n"
            + "                Action {get(s.id,o.id)}";


    //    String dsl = rule;
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl2);
    task.setGraphLoadClass(
            "com.antgroup.openspg.reasoner.runner.local.loader.TestFanxiqianGraphLoader2");
    task.getParams().put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true);
    task.getParams().put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.getParams().put(ConfigKey.KG_REASONER_OUTPUT_GRAPH, true);
    task.setStartIdList(Lists.newArrayList(new Tuple2<>("s1", "SourceNumber")));
    task.setExecutionRecorder(new DefaultRecorder());
    task.setExecutorTimeoutMs(99999999999999999L);

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
            "SourceNumber",
            Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
            "Record", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "callDuration")));
    schema.put(
            "DestNumber",
            Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));

    schema.put(
            "SourceNumber_hasRecord_Record",
            Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));

    schema.put(
            "Record_destNumber_DestNumber",
            Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));

    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    /*
    catalog .getGraph("KG")
            .registerRule("SourceNumber_longCallContact_DestNumber", new GeneralSemanticRule(rule));
     */
    task.setCatalog(catalog);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    System.out.println(task.getExecutionRecorder().toReadableString());
    clear();

  }
  @Test
  public void doTestFilter() {
    String dsl =
        "GraphStructure {\n"
            + "        (s1:Road.Event)-[p1:subject]-(o1:Road.Researcher)\n"
            + "        (s1:Road.Event)-[p3:object]-(o3:Road.Area)\n"
            + "        (s1:Road.Event)-[p2:province]-(o2:Road.AdministrativeRegion)\n"
            + "}\n"
            + "Rule {\n"
            + "        R0: o1.id == \"张三\"\n"
            + "        R1: o2.name rlike \"江西省\"\n"
            + "}\n"
            + "Action {\n"
            + "    get(o3.name)\n"
            + "}";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass("com.antgroup.openspg.reasoner.runner.local.loader.TestRoadGraphLoader");
    task.getParams().put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true);
    task.getParams().put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setStartIdList(Lists.newArrayList(new Tuple2<>("张三", "Road.Researcher")));

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("Road.Researcher", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "Road.Event",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "kgstartDateRaw")));
    schema.put("Road.Area", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "name")));
    schema.put(
        "Road.AdministrativeRegion",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "name")));

    schema.put(
        "Road.Event_subject_Road.Researcher",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("holdRet")));
    schema.put(
        "Road.Event_object_Road.Area",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("holdRet")));
    schema.put(
        "Road.Event_province_Road.AdministrativeRegion",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("holdRet")));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    Assert.assertEquals(result.getRows().size(), 1);
    Assert.assertEquals(result.getRows().get(0)[0], "江西yy校");
    clear();
  }

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

    LocalReasonerRunner runner = new LocalReasonerRunner();
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

    LocalReasonerRunner runner = new LocalReasonerRunner();
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

    LocalReasonerRunner runner = new LocalReasonerRunner();
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

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    Assert.assertEquals(result.getRows().size(), 1);
    Assert.assertEquals(result.getRows().get(0)[0], "u1");
    Assert.assertEquals(result.getRows().get(0)[1], "0.02");
    clear();
  }

  @Test
  public void doTestConcatAggIfDsl() {
    String dsl =
        "\n"
            + "GraphStructure {\n"
            + "  (user:TuringCore.AlipayUser)<-[e:traveler]-(te:TuringCore.TravelEvent)\n"
            + "}\n"
            + "Rule{\n"
            + "  concatEps = group(user).ConcatAggIf(te.travelMode == 'train', te.travelEndpoint)\n"
            + "  R1: concatEps != ''\n"
            + "}\n"
            + "Action{\n"
            + "  get(user.id, concatEps)\n"
            + "}";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.loader.TestCrowdGraphLoader");

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
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "eventTime", "travelMode", "travelEndpoint")));
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

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    Assert.assertEquals(result.getRows().size(), 1);
    Assert.assertEquals(result.getRows().get(0)[0], "u1");
    Assert.assertEquals(result.getRows().get(0)[1], "中国-浙江省-杭州市,中国-浙江省-杭州市");
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
    task.setExecutionRecorder(new DefaultRecorder());
    task.getParams().put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    System.out.println(task.getExecutionRecorder().toReadableString());
    Assert.assertEquals(result.getRows().size(), 1);
    Assert.assertEquals(result.getRows().get(0)[0], "u1");
    Assert.assertEquals(result.getRows().get(0)[1], "3");

    task.getParams().put(ConfigKey.KG_REASONER_OUTPUT_GRAPH, true);

    task.setExecutionRecorder(new DefaultRecorder());
    LocalReasonerResult result2 = runner.run(task);
    System.out.println(result2);
    Assert.assertEquals(result2.isGraphResult(), true);
    Assert.assertEquals(result2.getVertexList().size(), 1);
    clear();
    System.out.println(task.getExecutionRecorder().toReadableString());
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
    task.setExecutionRecorder(new DefaultRecorder());

    task.setStartIdList(Lists.newArrayList(new Tuple2<>("保险产品", "InsProduct.Product")));

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println("##########################");
    System.out.println(result);
    System.out.println("##########################");
    System.out.println(task.getExecutionRecorder().toReadableString());

    clear();
  }

  @Test
  public void testEdgePropertyDefineDsl() {
    String dsl =
        "//1 先定义每两个用户间进行聚合交易总值\n"
            + "Define (s:CustFundKG.Account)-[p:tranTargetUser]->(o:CustFundKG.Account) {\n"
            + "    GraphStructure {\n"
            + "        (o)-[t:accountFundContact]-(s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "      tran_count = group(s,o).count(t.sumAmt)\n"
            + "      tran_amt = group(s,o).sum(t.sumAmt)\n"
            + "      p.tran_count = tran_count\n"
            + "      p.tran_amt = tran_amt\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "\n"
            + "//在定义经常交易的用户流水总和\n"
            + "Define (s:CustFundKG.Account)-[p:total_trans_amt]->(o:Int) {\n"
            + "    GraphStructure {\n"
            + "         (s)-[t:tranTargetUser]->(u:CustFundKG.Account)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        R(\"交易笔数大于1才保留\") : t.tran_count>=1\n"
            + "        o = group(s).sum(t.tran_amt)\n"
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
            + "\tget(s.id, s.total_trans_amt)\n"
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

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println("##########################");
    System.out.println(result);
    System.out.println("##########################");

    clear();
  }

  @Test
  public void doTestWithStart() {
    String dsl =
        "// ip在高危地区\n"
            + "GraphStructure {\n"
            + "  (a:ABM.User)-[hasIp:acc2ip]->(ip:ABM.IP)\n"
            + "}\n"
            + "Rule {\n"
            + "  \tR1(\"必选180天以内\"): hasIp.ipUse180dCnt >=2\n"
            + "    R2(\"必须是高危地区\"): ip.country in ['地址1','地址2','地址3']\n"
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

    LocalReasonerRunner runner = new LocalReasonerRunner();
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

    LocalReasonerRunner runner = new LocalReasonerRunner();
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

    LocalReasonerMain.doMain(
        new String[] {
          "--projectId",
          "2",
          "--query",
          nearbyDsl,
          "--output",
          outputFile,
          "--graphLoaderClass",
          "com.antgroup.openspg.reasoner.runner.local.loader.TestSpatioTemporalGraphLoader",
          "--graphStoreUrl",
          "",
          "--schemaUrl",
          "s",
          "-startIdList",
          "[[\"MOCK1\",\"PE.JiuZhi\"]]",
          "--params",
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
