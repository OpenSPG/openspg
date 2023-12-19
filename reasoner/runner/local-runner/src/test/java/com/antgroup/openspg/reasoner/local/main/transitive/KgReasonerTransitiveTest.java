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

package com.antgroup.openspg.reasoner.local.main.transitive;

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
import com.antgroup.openspg.reasoner.runner.ConfigKey;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import scala.collection.immutable.Set;

public class KgReasonerTransitiveTest {

  @Test
  public void testTransitive1() {
    String dsl =
        "GraphStructure {\n"
            + "  A [FilmPerson]\n"
            + "  C,D [FilmDirector]\n"
            + "  A->C [test] as e1\n"
            + "  C->D [t1] repeat(0,2) as e2\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.name,C.name,D.name)\n"
            + "}";

    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put("FilmPerson", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "name")));
    schema.put(
        "FilmDirector", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "name")));
    schema.put(
        "FilmPerson_test_FilmDirector", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "FilmDirector_t1_FilmDirector", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.local.main.transitive.KgReasonerTransitiveTest$GraphLoader");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    params.put(ConfigKey.KG_REASONER_BINARY_PROPERTY, "false");
    params.put(Constants.SPG_REASONER_MULTI_VERSION_ENABLE, "true");
    task.setParams(params);

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);

    // only u1
    // check result
    Assert.assertEquals(4, result.getRows().size());
    Assert.assertEquals(3, result.getRows().get(0).length);
    java.util.Set<String> dSet = new HashSet<>();
    for (Object[] strings : result.getRows()) {
      dSet.add(String.valueOf(strings[2]));
    }
    Assert.assertTrue(dSet.contains("C1"));
    Assert.assertTrue(dSet.contains("C2"));
    Assert.assertTrue(dSet.contains("D21"));
    Assert.assertTrue(dSet.contains("D22"));
  }

  public static class GraphLoader extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("A1", "FilmPerson", "name", "A1"),
          constructionVertex("C1", "FilmDirector", "name", "C1"),
          constructionVertex("A2", "FilmPerson", "name", "A2"),
          constructionVertex("C2", "FilmDirector", "name", "C2"),
          constructionVertex("D21", "FilmDirector", "name", "D21"),
          constructionVertex("D22", "FilmDirector", "name", "D22"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionEdge("A1", "test", "C1"),
          constructionEdge("A2", "test", "C2"),
          constructionEdge("C2", "t1", "D21"),
          constructionEdge("D21", "t1", "D22"));
    }
  }
}
