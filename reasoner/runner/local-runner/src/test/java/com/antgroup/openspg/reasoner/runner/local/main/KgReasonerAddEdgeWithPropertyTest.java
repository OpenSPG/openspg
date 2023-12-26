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

public class KgReasonerAddEdgeWithPropertyTest {
  @Test
  public void testAddEdgeWithProperty() {
    String dsl =
        "Define (s:User)-[p:tradeInfo]->(o:User) {\n"
            + "    GraphStructure {\n"
            + "        (s:User)-[t:trade]->(o:User)\n"
            + "    } Rule {\n"
            + "        R1(\"交易时间在90天内\"): t.trade_time < now()\n"
            + "        trade_num(\"计算每个交易对象的交易次数\") = group(s,o).count(t)\n"
            + "        p.trade_num = trade_num\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "Define (s:User)-[p:belongTo]->(o:CrowdType/RepeatTradeUser) {\n"
            + "    GraphStructure {\n"
            + "        (s:User)-[t:tradeInfo]->(u:User)\n"
            + "    } Rule {\n"
            + "        trade_user_count = group(s).count(u.id)\n"
            + "        R2(\"至少有3个交易对手\"): trade_user_count >= 3\n"
            + "        every_user_trade_more_then3 = group(s).countIf(t.trade_num > 3, t.trade_num)\n"
            + "        R3(\"每个交于对手交易大于3比\"): every_user_trade_more_then3 >= 1\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "  (s:CrowdType/`RepeatTradeUser`)\n"
            + "}\n"
            + "Rule{\n"
            + "}\n"
            + "Action {\n"
            + "  get(s.id)\n"
            + "}\n";

    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("User", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("CrowdType", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "User_trade_User", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("trade_time")));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.KgReasonerAddEdgeWithPropertyTest$GraphLoader");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setParams(params);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);

    // only u1
    Assert.assertEquals(1, result.getRows().size());
    Assert.assertEquals("u1", result.getRows().get(0)[0]);
  }

  public static class GraphLoader extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("u1", "User"),
          constructionVertex("u2", "User"),
          constructionVertex("u3", "User"),
          constructionVertex("u4", "User"),
          constructionVertex("RepeatTradeUser", "CrowdType"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionVersionEdge("u1", "trade", "u2", 1, "trade_time", 1),
          constructionVersionEdge("u1", "trade", "u2", 2, "trade_time", 2),
          constructionVersionEdge("u1", "trade", "u2", 3, "trade_time", 3),
          constructionVersionEdge("u1", "trade", "u3", 1, "trade_time", 1),
          constructionVersionEdge("u1", "trade", "u3", 2, "trade_time", 2),
          constructionVersionEdge("u1", "trade", "u3", 3, "trade_time", 3),
          constructionVersionEdge("u1", "trade", "u3", 4, "trade_time", 4),
          constructionVersionEdge("u1", "trade", "u4", 1, "trade_time", 1),
          constructionVersionEdge("u1", "trade", "u4", 2, "trade_time", 2),
          constructionVersionEdge("u1", "trade", "u4", 3, "trade_time", 3),
          constructionVersionEdge("u2", "trade", "u3", 1, "trade_time", 1),
          constructionVersionEdge("u2", "trade", "u3", 2, "trade_time", 2),
          constructionVersionEdge("u2", "trade", "u3", 3, "trade_time", 3),
          constructionVersionEdge("u2", "trade", "u4", 1, "trade_time", 1),
          constructionVersionEdge("u2", "trade", "u4", 2, "trade_time", 2),
          constructionVersionEdge("u2", "trade", "u4", 3, "trade_time", 3));
    }
  }
}
