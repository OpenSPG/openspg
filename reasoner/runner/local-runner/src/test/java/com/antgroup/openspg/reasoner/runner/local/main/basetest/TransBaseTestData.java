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

package com.antgroup.openspg.reasoner.runner.local.main.basetest;

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog;
import com.antgroup.openspg.reasoner.runner.local.KGReasonerLocalRunner;
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
import scala.Tuple2;

public class TransBaseTestData {
  public static Catalog getMockCatalogSchema() {
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put("User", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "User_trans_User",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("payDate", "amount")));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    return catalog;
  }

  public static class TransGraphGenerator extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("1", "User"),
          constructionVertex("2", "User"),
          constructionVertex("3", "User"),
          constructionVertex("4", "User"),
          constructionVertex("5", "User"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionVersionEdge("1", "trans", "2", 0, "payDate", "20230910", "amount", 150),
          constructionVersionEdge("1", "trans", "2", 2, "payDate", "20230910", "amount", 150),
          constructionVersionEdge("2", "trans", "3", 0, "payDate", "20230910", "amount", 100),
          constructionVersionEdge("2", "trans", "3", 2, "payDate", "20230910", "amount", 100),
          constructionVersionEdge("3", "trans", "1", 0, "payDate", "20230910", "amount", 100),
          constructionVersionEdge("3", "trans", "1", 2, "payDate", "20230910", "amount", 100),
          constructionVersionEdge("4", "trans", "2", 0, "payDate", "20230910", "amount", 200),
          constructionVersionEdge("3", "trans", "4", 0, "payDate", "20230910", "amount", 200),
          constructionVersionEdge("5", "trans", "2", 0, "payDate", "20230910", "amount", 200),
          constructionVersionEdge("3", "trans", "5", 0, "payDate", "20230910", "amount", 200));
    }
  }

  public static List<String[]> runTestResult(String dsl, Map<String, Object> runParams) {
    Catalog catalog = getMockCatalogSchema();
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // use test catalog
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.basetest.TransBaseTestData$TransGraphGenerator");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    params.put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true);
    params.putAll(runParams);
    task.setParams(params);

    task.setStartIdList(Lists.newArrayList(new Tuple2<>("1", "User")));

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    List<String[]> rst = new ArrayList<>();
    for (Object[] r : result.getRows()) {
      String[] out = new String[r.length];
      for (int i = 0; i < r.length; i++) {
        out[i] = String.valueOf(r[i]);
      }
      rst.add(out);
    }
    return rst;
  }
}
