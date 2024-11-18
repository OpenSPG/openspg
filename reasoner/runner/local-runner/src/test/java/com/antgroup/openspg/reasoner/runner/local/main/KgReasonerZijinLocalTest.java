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

package com.antgroup.openspg.reasoner.runner.local.main;

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog;
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
}
