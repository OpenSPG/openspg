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

/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.reasoner.local.main.basetest;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import scala.Tuple2;

public class FilmBaseTestData {
  public static Catalog getMockCatalogSchema() {
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
        "Film",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "filmBudget", "filmGross")));
    schema.put(
        "FilmDirector",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "age", "name", "gender")));
    schema.put(
        "FilmStar",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "age", "name", "gender")));
    schema.put(
        "Film_starOfFilm_FilmStar",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("joinTs")));
    schema.put(
        "Film_directOfFilm_FilmDirector", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    return catalog;
  }

  public static class FilmGraphGenerator extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("root", "Film", "filmBudget", 100),
          constructionVertex("f1", "Film"),
          constructionVertex("f2", "Film"),
          constructionVertex("f3", "Film"),
          constructionVertex("L1_1_star", "FilmStar", "age", 60, "gender", "男"),
          constructionVertex("L1_2_star", "FilmStar", "age", 30, "gender", "男"),
          constructionVertex("L1_3_star", "FilmStar", "age", 18, "gender", "女"),
          constructionVertex("L1_1_director", "FilmDirector"),
          constructionVertex("L1_2_director", "FilmDirector"),
          constructionVertex("L1_3_director", "FilmDirector"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionVersionEdge("root", "starOfFilm", "L1_1_star", 0, "joinTs", 100),
          constructionVersionEdge("root", "starOfFilm", "L1_2_star", 0, "joinTs", 200),
          constructionVersionEdge("root", "starOfFilm", "L1_3_star", 0, "joinTs", 300),
          constructionVersionEdge("root", "starOfFilm", "L1_3_star", 1, "joinTs", 400),
          constructionVersionEdge("f1", "starOfFilm", "L1_1_star", 0, "joinTs", 100),
          constructionVersionEdge("f1", "starOfFilm", "L1_2_star", 0, "joinTs", 200),
          constructionVersionEdge("f1", "starOfFilm", "L1_3_star", 0, "joinTs", 300),
          constructionVersionEdge("f2", "starOfFilm", "L1_1_star", 0, "joinTs", 100),
          constructionVersionEdge("f2", "starOfFilm", "L1_2_star", 0, "joinTs", 200),
          constructionVersionEdge("f2", "starOfFilm", "L1_3_star", 0, "joinTs", 300),
          constructionVersionEdge("f3", "starOfFilm", "L1_1_star", 0, "joinTs", 10),
          constructionVersionEdge("f3", "starOfFilm", "L1_2_star", 0, "joinTs", 200),
          constructionVersionEdge("f3", "starOfFilm", "L1_3_star", 0, "joinTs", 600),
          constructionVersionEdge("root", "directOfFilm", "L1_1_director", 0),
          constructionVersionEdge("f1", "directOfFilm", "L1_1_director", 0),
          constructionVersionEdge("f2", "directOfFilm", "L1_2_director", 0),
          constructionVersionEdge("f3", "directOfFilm", "L1_3_director", 0));
    }
  }

  public static class FilmGraphGeneratorTopK extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("root", "Film", "filmBudget", 100),
          constructionVertex("f1", "Film"),
          constructionVertex("f2", "Film"),
          constructionVertex("f3", "Film"),
          constructionVertex("L1_1_star", "FilmStar", "age", 60, "gender", "男"),
          constructionVertex("L1_2_star", "FilmStar", "age", 30, "gender", "男"),
          constructionVertex("L1_3_star", "FilmStar", "age", 18, "gender", "男"),
          constructionVertex("L1_1_director", "FilmDirector"),
          constructionVertex("L1_2_director", "FilmDirector"),
          constructionVertex("L1_3_director", "FilmDirector"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionVersionEdge("root", "starOfFilm", "L1_1_star", 0, "joinTs", 100),
          constructionVersionEdge("root", "starOfFilm", "L1_2_star", 0, "joinTs", 200),
          constructionVersionEdge("root", "starOfFilm", "L1_3_star", 0, "joinTs", 300),
          constructionVersionEdge("root", "starOfFilm", "L1_3_star", 1, "joinTs", 400),
          constructionVersionEdge("f1", "starOfFilm", "L1_1_star", 0, "joinTs", 100),
          constructionVersionEdge("f1", "starOfFilm", "L1_2_star", 0, "joinTs", 200),
          constructionVersionEdge("f1", "starOfFilm", "L1_3_star", 0, "joinTs", 300),
          constructionVersionEdge("f2", "starOfFilm", "L1_1_star", 0, "joinTs", 101),
          constructionVersionEdge("f2", "starOfFilm", "L1_2_star", 0, "joinTs", 201),
          constructionVersionEdge("f3", "starOfFilm", "L1_1_star", 0, "joinTs", 10),
          constructionVersionEdge("root", "directOfFilm", "L1_1_director", 0),
          constructionVersionEdge("f1", "directOfFilm", "L1_1_director", 0),
          constructionVersionEdge("f2", "directOfFilm", "L1_2_director", 0),
          constructionVersionEdge("f3", "directOfFilm", "L1_3_director", 0));
    }
  }

  public static List<String[]> runTestResult(String dsl, String dataClass) {
    Catalog catalog = getMockCatalogSchema();
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // use test catalog
    task.setCatalog(catalog);

    task.setGraphLoadClass(dataClass);

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    params.put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true);
    task.setParams(params);

    task.setStartIdList(Lists.newArrayList(new Tuple2<>("root", "Film")));

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

  public static List<String[]> runTestResult(String dsl) {
    return runTestResult(
        dsl,
        "com.antgroup.openspg.reasoner.local.main.basetest.FilmBaseTestData$FilmGraphGenerator");
  }
}
