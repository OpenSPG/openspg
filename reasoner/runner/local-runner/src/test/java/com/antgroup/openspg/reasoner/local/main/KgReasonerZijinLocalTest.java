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

package com.antgroup.openspg.reasoner.local.main;

import com.antgroup.openspg.reasoner.catalog.CatalogFactory;
import com.antgroup.openspg.reasoner.catalog.impl.KgSchemaConnectionInfo;
import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.local.KGReasonerLocalRunner;
import com.antgroup.openspg.reasoner.local.load.graph.AbstractLocalGraphLoader;
import com.antgroup.openspg.reasoner.local.model.LocalReasonerResult;
import com.antgroup.openspg.reasoner.local.model.LocalReasonerTask;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import scala.Tuple2;

public class KgReasonerZijinLocalTest {

  @Test
  public void test3() {
    String dsl =
        "// 当月交易量\n"
            + "Define (s:CustFundKG.Account)-[p:cur_month_num]->(o:Int) {\n"
            + "    GraphStructure {\n"
            + "        (u:CustFundKG.Account)-[t:accountFundContact]-(s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "      \tR1(\"当月交易量\"): date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate) <= 30\n"
            + "        o = group(s).count(t.transDate)\n"
            + "    }\n"
            + "}\n"
            + "// 次月交易量\n"
            + "Define (s:CustFundKG.Account)-[p:last_month_num]->(o:Int) {\n"
            + "    GraphStructure {\n"
            + "        (u:CustFundKG.Account)-[t:accountFundContact]-(s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "      \tdate_delta = date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate)\n"
            + "      \tR1(\"次月交易量\"): date_delta > 30 && date_delta <=60\n"
            + "        o = group(s).count(t.transDate)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "// 倍数\n"
            + "Define (s:CustFundKG.Account)-[p:trans_multiple]->(o:Float) {\n"
            + "\t\tGraphStructure {\n"
            + "        (s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "    \tcur_month = rule_value(s.cur_month_num==null, 0.0, s.cur_month_num*1.0)\n"
            + "        last_month = rule_value(s.last_month_num == null, 1, s.last_month_num)\n"
            + "      \tmultiple = cur_month / last_month\n"
            + "        o = multiple\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "Define (s:CustFundKG.Account)-[p:is_trans_raise_more]->(o:Boolean) {\n"
            + "\t\tGraphStructure {\n"
            + "        (s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "      \to = rule_value(s.trans_multiple >= 3, true, false)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "        (s:CustFundKG.Account)\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id, s.is_trans_raise_more, s.trans_multiple, s.last_month_num, s.cur_month_num)\n"
            + "}\n";
    LocalReasonerTask task = new LocalReasonerTask();
    task.setStartIdList(Lists.newArrayList(new Tuple2<>("2088xx3", "CustFundKG.Account")));

    task.setExecutorTimeoutMs(60 * 1000 * 100);
    task.setDsl(dsl);

    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("CustFundKG.Account", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "CustFundKG.Account_accountFundContact_CustFundKG.Account",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("transDate")));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.local.main.KgReasonerZijinLocalTest$GraphLoader");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setParams(params);

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    Assert.assertEquals(1, result.getRows().size());
  }

  @Test
  public void test4() {
    String dsl =
        "GraphStructure {\n"
            + "s [CustFundKG.Account, __start__='true']\n"
            + "u [CustFundKG.Account] \n"
            + "u->s [accountFundContact] as t\n"
            + "}\n"
            + "Rule {\n"
            + "o = group(s).count(t)\n"
            + "}\n"
            + "Action {\n"
            + "\tget(s.id,t.sumAmt,t.transCount)\n"
            + "}\n";
    LocalReasonerTask task = new LocalReasonerTask();
    task.setStartIdList(Lists.newArrayList(new Tuple2<>("2088xx4", "CustFundKG.Account")));

    task.setExecutorTimeoutMs(60 * 1000 * 100);
    task.setDsl(dsl);

    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("CustFundKG.Account", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "CustFundKG.Account_accountFundContact_CustFundKG.Account",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("transDate", "sumAmt", "transCount")));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.local.main.KgReasonerZijinLocalTest$GraphLoader");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setParams(params);

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    Assert.assertEquals(1, result.getRows().size());
  }

  @Test
  public void test21() {
    String dsl =
        "//1 先定义流入资金总数\n"
            + "Define (s:CustFundKG.Account)-[p:total_in_trans_num]->(o:Int) {\n"
            + "    GraphStructure {\n"
            + "       (s)-[:expand_linked_alipay_id(s.id)]-> (B:CustFundKG.Alipay|CustFundKG.BankCard|CustFundKG.Default|CustFundKG.Huabei|CustFundKG.Jiebei|CustFundKG.MyBank|CustFundKG.Other|CustFundKG.Yeb|CustFundKG.YIB)<-[transIn:transfer|unknown|fundRedeem|fundPurchase|dingqiPurchase|taxRefund|transfer|consume|ylbPurchase|jbRepay|hbRepay|creditCardRepay|withdraw|gkhRepay|yebPurchase|corpCreditRepay|deposit|merchantSettle|ylbRedeem|dingqiRedeem|jbLoan|corpCreditLoan|sceneLoan]-(C)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        o = group(s,B).count(transIn)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "//2 定义整百流入笔数\n"
            + "Define (s:CustFundKG.Account)-[p:multiples_hundred_in_trans_num]->(o:Int) {\n"
            + "    GraphStructure {\n"
            + "       (s)-[:expand_linked_alipay_id(s.id)]-> (B:CustFundKG.Alipay|CustFundKG.BankCard|CustFundKG.Default|CustFundKG.Huabei|CustFundKG.Jiebei|CustFundKG.MyBank|CustFundKG.Other|CustFundKG.Yeb|CustFundKG.YIB)<-[transIn:transfer|unknown|fundRedeem|fundPurchase|dingqiPurchase|taxRefund|transfer|consume|ylbPurchase|jbRepay|hbRepay|creditCardRepay|withdraw|gkhRepay|yebPurchase|corpCreditRepay|deposit|merchantSettle|ylbRedeem|dingqiRedeem|jbLoan|corpCreditLoan|sceneLoan]-(C)\n"
            + "    }\n"
            + "\tRule {\n"
            + "    \tR1(\"必须是整百交易\"): transIn.amount % 100*100 == 0\n"
            + "        o = group(s).count(transIn)\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "// 判断是否汇聚赌博\n"
            + "Define (s:CustFundKG.Account)-[p:is_pooling_gambling_funds]->(o:Boolean) {\n"
            + "    GraphStructure {\n"
            + "        (s)\n"
            + "    }\n"
            + "\tRule {\n"
            + "        R0(\"存在流入和整百资金\"): s.multiples_hundred_in_trans_num != null && s.total_in_trans_num != null\n"
            + "    \tR1(\"流入整百金额笔数大于24比\"): s.multiples_hundred_in_trans_num > 24\n"
            + "        R2(\"整百交易占比大于2%\"): s.multiples_hundred_in_trans_num*1.0 /s.total_in_trans_num > 0.02\n"
            + "        o = rule_value(R0 && R1 && R2, true, false)\n"
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
    task.setExecutorTimeoutMs(60 * 1000 * 100);
    task.setDsl(dsl);

    Map<String, Object> dslParams = new HashMap<>();
    // use test catalog
    dslParams.put("projId", "308000003");
    Catalog catalog =
        CatalogFactory.createCatalog(
            dslParams,
            new KgSchemaConnectionInfo("http://kgengine.stable.alipay.net", "ba15431974C4ABBb"));
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.local.main.KgReasonerZijinLocalTest$GraphLoader");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setParams(params);

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    Assert.assertEquals(2, result.getRows().size());
  }

  @Test
  public void test2() {
    String dsl =
        "GraphStructure {\n"
            + "(B:CustFundKG.Alipay|CustFundKG.BankCard|CustFundKG.Huabei|CustFundKG.Yeb|CustFundKG.YIB|CustFundKG.Other|CustFundKG.Jiebei|CustFundKG.MyBank)-[t:consume|transfer]->(C)\n"
            + "}\n"
            + "Rule {\n"
            + "hour = hourOfDay(t.payDate*1000)\n"
            + "//R1(\"凌晨交易\"): hour <=5 && hour >=0\n"
            + "}\n"
            + "Action {\n"
            + "  get(B.id, t.payDate, hour) \n"
            + "}";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setExecutorTimeoutMs(60 * 1000 * 100);
    task.setDsl(dsl);

    Map<String, Object> dslParams = new HashMap<>();
    // use test catalog
    dslParams.put("projId", "308000003");
    Catalog catalog =
        CatalogFactory.createCatalog(
            dslParams,
            new KgSchemaConnectionInfo("http://kgengine.stable.alipay.net", "ba15431974C4ABBb"));
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.local.main.KgReasonerZijinLocalTest$GraphLoader");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setParams(params);

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    Assert.assertEquals(2, result.getRows().size());
  }

  public static class GraphLoader extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("2088xx", "CustFundKG.Alipay"),
          constructionVertex("2088xxx1", "CustFundKG.BankCard"),
          constructionVertex("2088xxx2", "CustFundKG.Alipay"),
          constructionVertex("2088xx3", "CustFundKG.Account"),
          constructionVertex("2088xx4", "CustFundKG.Account"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionEdge("2088xx", "transfer", "2088xxx1", "payDate", 1691950562L),
          constructionEdge("2088xx", "transfer", "2088xxx2", "payDate", 1691950562L),
          constructionEdge("2088xx3", "accountFundContact", "2088xx4", "transDate", "20230801"));
    }
  }

  @Test
  public void test5() {
    String dsl =
        "Define (u1:AttributePOC.BrinsonAttribute)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfBrinsonAttribute`/`市场贡献大`) {\n"
            + "  GraphStructure {\n"
            + "    (s:AttributePOC.BrinsonAttribute)-[p1:factorValue]->(u1)\n"
            + "  }\n"
            + "\n"
            + "  Rule {\n"
            + "    R1: u1.factorType == \"market\"\n"
            + "    R4: s.factorType == \"total\"\n"
            + "    v = (u1.factorValue/ s.factorValue)\n"
            + "    R2(\"必须大于50%\"): v > 0.5\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "Define (u2:AttributePOC.BrinsonAttribute)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfBrinsonAttribute`/`选股贡献大`) {\n"
            + "  GraphStructure {\n"
            + "    (s:AttributePOC.BrinsonAttribute)-[p1:factorValue]->(u1:AttributePOC.BrinsonAttribute)\n"
            + "    (s)-[p2:factorValue]->(u2)\n"
            + "    (s)-[p3:factorValue]->(u3:AttributePOC.BrinsonAttribute)\n"
            + "  }\n"
            + "\n"
            + "  Rule {\n"
            + "    R1: u1.factorType == \"cluster\"\n"
            + "    R2: u2.factorType == \"stock\"\n"
            + "    R3: u3.factorType == \"trade\"\n"
            + "    R4: s.factorType == \"total\"\n"
            + "    v = (u1.factorValue/ s.factorValue + u3.factorValue / s.factorValue)\n"
            + "    R6(\"必须大于50%\"): v < 0.5\n"
            + "    R5(\"交易收益大于选股\"): u2.factorValue > u3.factorValue\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "Define (u2:AttributePOC.BrinsonAttribute)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfBrinsonAttribute`/`交易贡献大`) {\n"
            + "  GraphStructure {\n"
            + "    (s:AttributePOC.BrinsonAttribute)-[p1:factorValue]->(u1:AttributePOC.BrinsonAttribute)\n"
            + "    (s)-[p2:factorValue]->(u2)\n"
            + "    (s)-[p3:factorValue]->(u3:AttributePOC.BrinsonAttribute)\n"
            + "  }\n"
            + "\n"
            + "  Rule {\n"
            + "    R1: u1.factorType == \"cluster\"\n"
            + "    R2: u2.factorType == \"trade\"\n"
            + "    R3: u3.factorType == \"stock\"\n"
            + "    R4: s.factorType == \"total\"\n"
            + "    v = (u1.factorValue/ s.factorValue + u2.factorValue / s.factorValue)\n"
            + "    R5(\"必须大于50%\"): v > 0.5\n"
            + "    R6(\"交易收益大于选股\"): u2.factorValue > u3.factorValue\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "Define (s: AttributePOC.TracebackDay)-[p: market]->(o: Float) {\n"
            + " GraphStructure {\n"
            + "    (s:AttributePOC.TracebackDay)-[:day]->(f: AttributePOC.BrinsonAttribute)-[:factorValue]->(u1:`AttributePOC.TaxonomyOfBrinsonAttribute`/`市场贡献大`)\n"
            + "\t}\n"
            + "    Rule {\n"
            + "    o = u1.factorValue\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "Define (s: AttributePOC.TracebackDay)-[p: stock]->(o: Float) {\n"
            + " GraphStructure {\n"
            + "    (s:AttributePOC.TracebackDay)-[:day]->(f: AttributePOC.BrinsonAttribute)-[:factorValue]->(u1:`AttributePOC.TaxonomyOfBrinsonAttribute`/`选股贡献大`)\n"
            + "\t}\n"
            + "    Rule {\n"
            + "    o = u1.factorValue\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s: AttributePOC.TracebackDay)-[p: trade]->(o: Float) {\n"
            + " GraphStructure {\n"
            + "    (s:AttributePOC.TracebackDay)-[:day]->(f: AttributePOC.BrinsonAttribute)-[:factorValue]->(u1:`AttributePOC.TaxonomyOfBrinsonAttribute`/`交易贡献大`)\n"
            + "\t}\n"
            + "    Rule {\n"
            + "    o = u1.factorValue\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "Define (s: AttributePOC.TracebackDay)-[p: result]->(o: Text) {\n"
            + "    GraphStructure {\n"
            + "   (s)\n"
            + "}\n"
            + "Rule {\n"
            + "// 按照选股、交易、市场的顺序输出\n"
            + "  str1 = rule_value(s.stock == null, \"\", concat(\"选股\", \": \", s.stock, ', '))\n"
            + "    str2 = concat(str1, rule_value(s.trade == null, \"\", concat(\"交易\", \": \", s.trade, ', ')))\n"
            + "    str3 = concat(str2, rule_value(s.market == null, \"\", concat(\"市场\", \": \", s.market)))\n"
            + "    o = str3\n"
            + "}\n"
            + "}\n"
            + "\n"
            + "Define (u1:AttributePOC.Scenario)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfScenario`/`基金收益分析`) {\n"
            + "  GraphStructure {\n"
            + "  \t(u1)<-[p1:scConfig]-(s:AttributePOC.TracebackDay)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    R1: s.result != null\n"
            + "  }\n"
            + "}";
    dsl =
        dsl
            + "GraphStructure {\n"
            + "   (s: AttributePOC.TracebackDay)\n"
            + "}\n"
            + "Rule {\n"
            + "// 按照选股、交易、市场的顺序输出\n"
            + "  str1 = rule_value(s.stock == null, \"\", concat(\"选股\", \": \", s.stock, ', '))\n"
            + "   str2 = rule_value(s.trade == null, \"\", concat(\"交易\", \": \", s.trade, ', '))\n"
            + "}\n"
            + "Action {\n"
            + "  get(s.id, str1, str2) \n"
            + "}";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setExecutorTimeoutMs(60 * 1000 * 100);
    task.setDsl(dsl);

    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
        "AttributePOC.TracebackDay", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "AttributePOC.Scenario", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "AttributePOC.BrinsonAttribute",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "factorValue", "factorType")));
    schema.put(
        "AttributePOC.TaxonomyOfBrinsonAttribute",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "AttributePOC.TaxonomyOfScenario",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "AttributePOC.TracebackDay_day_AttributePOC.BrinsonAttribute",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "AttributePOC.BrinsonAttribute_factorValue_AttributePOC.BrinsonAttribute",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "AttributePOC.Scenario_scConfig_AttributePOC.TracebackDay",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "AttributePOC.TracebackDay_scConfig_AttributePOC.Scenario",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));

    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.local.main.KgReasonerZijinLocalTest$GraphLoader2");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setParams(params);

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    Assert.assertEquals(1, result.getRows().size());
    Assert.assertTrue(StringUtils.isNotBlank(result.getRows().get(0)[1].toString()));
  }

  public static class GraphLoader2 extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("day", "AttributePOC.TracebackDay"),
          constructionVertex(
              "s", "AttributePOC.BrinsonAttribute", "factorType", "total", "factorValue", 0.01),
          constructionVertex(
              "stock",
              "AttributePOC.BrinsonAttribute",
              "factorType",
              "stock",
              "factorValue",
              0.004),
          constructionVertex(
              "trade",
              "AttributePOC.BrinsonAttribute",
              "factorType",
              "trade",
              "factorValue",
              -0.005),
          constructionVertex(
              "market",
              "AttributePOC.BrinsonAttribute",
              "factorType",
              "market",
              "factorValue",
              0.006),
          constructionVertex(
              "cluster",
              "AttributePOC.BrinsonAttribute",
              "factorType",
              "cluster",
              "factorValue",
              -0.003),
          constructionVertex("市场贡献大", "AttributePOC.TaxonomyOfBrinsonAttribute"),
          constructionVertex("选股贡献大", "AttributePOC.TaxonomyOfBrinsonAttribute"),
          constructionVertex("交易贡献大", "AttributePOC.TaxonomyOfBrinsonAttribute"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionEdge("day", "day", "s"),
          constructionEdge("s", "factorValue", "stock"),
          constructionEdge("s", "factorValue", "trade"),
          constructionEdge("s", "factorValue", "market"),
          constructionEdge("s", "factorValue", "cluster"));
    }
  }
}
