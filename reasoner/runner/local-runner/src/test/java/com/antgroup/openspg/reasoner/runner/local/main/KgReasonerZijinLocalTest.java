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
import com.antgroup.openspg.reasoner.runner.local.LocalReasonerRunner;
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
import scala.Tuple2;

public class KgReasonerZijinLocalTest {

  @Test
  public void test41() {
    String dsl =
        "//异常借贷行为\n"
            + "GraphStructure {\n"
            + "A[AMLz50.BlackCompanyRelated]\n"
            + "}\n"
            + "Rule\n"
            + "{\t\n"
            + "R1(\"频繁花呗借款\")     = rule_value(A.explainStrategyList rlike 'HuaBeiMultiLending', true, false)\n"
            + "R2(\"花呗提前还款\")  = rule_value(A.explainStrategyList rlike 'HuaBeiMultiPrepay', true, false)\n"
            + "R3(\"花呗逾期未还\")  = rule_value(A.explainStrategyList rlike 'HuaBeiCreditOverdue', true, false)\n"
            + "\n"
            + "result = R1 or R2 or R3\n"
            + "\t\tr5 = rule_value(R3, \"花呗逾期未还\", \"\")\n"
            + "\t\tr4 = rule_value(R1, \"频繁借款\", r5)\n"
            + "\t\tr3 = rule_value(R1 && R3, \"频繁借款，花呗逾期未还\", r4)\n"
            + "    r2 = rule_value(R1 && R2, \"频繁借款后提前还款\", r3)\n"
            + "    r1 = rule_value(R1 && R2 && R3, \"频繁借款后提前还款，花呗逾期未还\", r2)\n"
            + "}\n"
            + "Action {\n"
            + "\tget(A.id,result,r1)\n"
            + "}";
    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
        "AMLz50.BlackCompanyRelated",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("explainStrategyList")));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerZijinLocalTest$GraphLoader41");
    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(ConfigKey.KG_REASONER_BINARY_PROPERTY, "false");
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setParams(params);
    task.setExecutorTimeoutMs(99999999999999999L);
    task.setExecutionRecorder(new DefaultRecorder());
    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(task.getExecutionRecorder().toReadableString());
    // only u1
    Assert.assertEquals(1, result.getRows().size());
  }

  public static class GraphLoader41 extends AbstractLocalGraphLoader {
    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(constructionVertex("SA", "AMLz50.BlackCompanyRelated"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList();
    }
  }

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
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerZijinLocalTest$GraphLoader");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setParams(params);

    LocalReasonerRunner runner = new LocalReasonerRunner();
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
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerZijinLocalTest$GraphLoader");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setParams(params);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    Assert.assertEquals(1, result.getRows().size());
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
