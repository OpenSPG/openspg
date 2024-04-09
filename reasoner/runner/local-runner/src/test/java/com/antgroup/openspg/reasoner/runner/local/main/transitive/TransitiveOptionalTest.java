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

package com.antgroup.openspg.reasoner.runner.local.main.transitive;

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog;
import com.antgroup.openspg.reasoner.recorder.DefaultRecorder;
import com.antgroup.openspg.reasoner.runner.ConfigKey;
import com.antgroup.openspg.reasoner.runner.local.LocalReasonerRunner;
import com.antgroup.openspg.reasoner.runner.local.load.graph.AbstractLocalGraphLoader;
import com.antgroup.openspg.reasoner.runner.local.loader.MockLocalGraphLoader;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerTask;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.immutable.Set;

public class TransitiveOptionalTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransitiveOptionalTest.class);

  private MockLocalGraphLoader getGraphWithDemoL2() {
    String demoGraphStr =
        "Graph {\n"
            + "  L01 [TestFinParty.RelatedParty,id='L01',entityType='CORPORATION']\n"
            + "  L02 [TestFinParty.RelatedParty,id='L02',entityType='PERSON']\n"
            + "  L12 [TestFinParty.RelatedParty,id='L12',entityType='CORPORATION']\n"
            + "  L20 [TestFinParty.RelatedParty,id='L20',entityType='CORPORATION']\n"
            + "  L11 [TestFinParty.RelatedParty,id='L11',entityType='CORPORATION']\n"
            + "  L02 -> L01 [relatedReason, relatedReason='DIRECTOR_D']\n"
            + "  L02 -> L01 [votingRatio, votingRatio=10]\n"
            + "  L02 -> L12 [votingRatio, votingRatio=10]\n"
            + "  L02 -> L12 [relatedReason, relatedReason='HUGE_INFLUENCE']\n"
            + "  L02 -> L20 [votingRatio, votingRatio=15]\n"
            + "  L02 -> L20 [relatedReason, relatedReason='CONTROL']\n"
            + "  L02 -> L11 [votingRatio, votingRatio=60]\n"
            + "  L02 -> L11 [relatedReason, relatedReason='HUGE_INFLUENCE']\n"
            + "  L01 -> L12 [votingRatio, votingRatio=30]\n"
            + "}";
    return getGraphWithDemo(demoGraphStr);
  }

  private MockLocalGraphLoader getGraphWithDemo(String demoGraphStr) {
    return new MockLocalGraphLoader(demoGraphStr);
  }

  private LocalReasonerResult runTest(
      Map<String, Set<String>> schema, String dsl, String demoGraphStr) {
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    MemGraphState memGraphState = new MemGraphState();
    AbstractLocalGraphLoader graphLoader = getGraphWithDemo(demoGraphStr);
    graphLoader.setGraphState(memGraphState);
    graphLoader.load();
    task.setGraphState(memGraphState);

    // for debug
    task.setExecutorTimeoutMs(60 * 60 * 1000);
    task.setExecutionRecorder(new DefaultRecorder());

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    params.put(ConfigKey.KG_REASONER_BINARY_PROPERTY, "false");
    params.put(Constants.SPG_REASONER_MULTI_VERSION_ENABLE, "true");
    task.setParams(params);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult rst = runner.run(task);
    LOGGER.info(task.getExecutionRecorder().toReadableString());
    return rst;
  }

  @Test
  public void testRuleWithOptional3_1() {
    String dsl =
        "GraphStructure {\n"
            + "start_Robot_Film [Robot.Film, __start__='true']\n"
            + "Robot_FilmStar [Robot.FilmStar]\n"
            + "start_Robot_Film<->Robot_FilmStar [starOfFilm, __optional__='true'] as Robot_Film_starOfFilm_Robot_FilmStar\n"
            + "\n"
            + "}\n"
            + "Rule {\n"
            + "//R0: (Robot_FilmStar.__label__ == 'Robot.FilmStar' && Robot_FilmStar.id == 'Robot_FilmStar_224')\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "  get(start_Robot_Film.id) \n"
            + "}";
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put("Robot.Film", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("Robot.FilmStar", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "Robot.Film_starOfFilm_Robot.FilmStar",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));

    String dataGraphStr =
        "Graph {\n"
            + "  Robot_FilmStar_224 [Robot.FilmStar]\n"
            + "  start_Robot_Film_921 [Robot.Film]\n"
            + "\n"
            + "  start_Robot_Film_921 -> Robot_FilmStar_224 [starOfFilm]\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr);

    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testOptional1() {
    String dsl =
        ""
            + "GraphStructure {\n"
            + "  A [a, __start__='true']\n"
            + "  B [b]\n"
            + "  C [c]\n"
            + "  D [d]\n"
            + "  E [e]\n"
            + "  F [f]\n"
            + "  A->B [ab, __optional__='true']\n"
            + "  A->C [ac,__optional__='true']\n"
            + "  A->D [ad, __optional__='true']\n"
            + "  C->E [ce, __optional__='true']\n"
            + "  D->F [df]\n"
            + "}\n"
            + "Rule {\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.name,B.name,C.name,D.name) \n"
            + "}";

    String dataGraphStr =
        "Graph {\n"
            + "  A_537 [a,name='491']\n"
            + "  D_51 [d,name='404']\n"
            + "  F_834 [f]\n"
            + "\n"
            + "  D_51 -> F_834 [df]\n"
            + "  A_537 -> D_51 [ad]\n"
            + "}";
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put("a", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("b", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("c", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("d", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("e", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("f", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("a_ab_b", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("a_ac_c", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("a_ad_d", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("c_ce_e", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("d_df_f", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));

    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr);

    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testOptional2() {
    String dsl =
        ""
            + "GraphStructure {\n"
            + "  A [a, __start__='true']\n"
            + "  B [b]\n"
            + "  C [c]\n"
            + "  D [d]\n"
            + "  E [e]\n"
            + "  F [f]\n"
            + "  A->B [ab, __optional__='true']\n"
            + "  A->C [ac,__optional__='true']\n"
            + "  A->D [ad, __optional__='true']\n"
            + "  C->E [ce, __optional__='true']\n"
            + "  D->F [df, __optional__='true']\n"
            + "}\n"
            + "Rule {\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.name,B.name,C.name,D.name) \n"
            + "}";

    String dataGraphStr =
        "Graph {\n"
            + "  A_537 [a,name='491']\n"
            + "  D_51 [d,name='404']\n"
            + "  F_834 [f]\n"
            + "\n"
            + "  A_537 -> D_51 [ad]\n"
            + "}";
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put("a", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("b", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("c", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("d", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("e", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("f", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("a_ab_b", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("a_ac_c", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("a_ad_d", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("c_ce_e", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("d_df_f", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));

    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr);

    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testOptional3() {
    String dsl =
        ""
            + "GraphStructure {\n"
            + "  A [a, __start__='true']\n"
            + "  B [b]\n"
            + "  C [c]\n"
            + "  D [d]\n"
            + "  E [e]\n"
            + "  F [f]\n"
            + "  A->B [ab, __optional__='true']\n"
            + "  A->C [ac,__optional__='true']\n"
            + "  A->D [ad, __optional__='true']\n"
            + "  C->E [ce, __optional__='true']\n"
            + "  D->F [df, __optional__='true']\n"
            + "}\n"
            + "Rule {\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.name,B.name,C.name,D.name) \n"
            + "}";

    String dataGraphStr =
        "Graph {\n"
            + "  A_537 [a,name='491']\n"
            + "  D_51 [d,name='404']\n"
            + "  F_834 [f]\n"
            + "\n"
            + "}";
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put("a", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("b", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("c", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("d", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("e", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("f", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("a_ab_b", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("a_ac_c", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("a_ad_d", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("c_ce_e", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("d_df_f", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));

    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr);

    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testOptionalWithRepeatEmpty() {
    String dsl =
        ""
            + "GraphStructure {\n"
            + " A [a, __start__='true']\n"
            + " B [b]\n"
            + " A->B [ab]\n"
            + " B1 [b1]\n"
            + " B->B1 [bb, __optional__='true']  as e1\n"
            + " C1 [b1]\n"
            + " B1->C1 [cc] repeat(0,10) as e2\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.name,B.name,B1.name)   \n"
            + "}";

    String dataGraphStr =
        "Graph {\n"
            + "  A_38 [a,name='A']\n"
            + "  B_38 [b,name='B']\n"
            + "  A_38 -> B_38 [ab]\n"
            + "}";

    Map<String, Set<String>> schema = new HashMap<>();
    schema.put("a", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("b", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("b1", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));

    schema.put("a_ab_b", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("b_bb_b1", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("b1_cc_b1", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));

    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testOptionalWithRepeat2() {
    String dsl =
        ""
            + "GraphStructure {\n"
            + " A [a, __start__='true']\n"
            + " B [b]\n"
            + " A->B [ab, __optional__='true']\n"
            + " B1 [b]\n"
            + " B->B1 [bb] repeat(0,10) as e1\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.name,B.name,B1.name)   \n"
            + "}";

    String dataGraphStr = "Graph {\n" + "  A_38 [a,name='629']\n" + "}";

    Map<String, Set<String>> schema = new HashMap<>();
    schema.put("a", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("b", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("b1", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));

    schema.put("a_ab_b", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("b_bb_b", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("b_cc_b", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));

    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testOptionalWithRepeat() {
    String dsl =
        ""
            + "GraphStructure {\n"
            + " A [a, __start__='true']\n"
            + " B [b]\n"
            + " A->B [ab, __optional__='true']\n"
            + " B1 [b]\n"
            + " B->B1 [bb] repeat(0,10) as e1\n"
            + "}\n"
            + "Rule {\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.name,B.name,B1.name)   \n"
            + "}";

    String dataGraphStr =
        "Graph {\n"
            + "  A_38 [a,name='629']\n"
            + "  B_754 [b,name='243']\n"
            + "  B1_480 [b,name='406']\n"
            + "\n"
            + "  A_38 -> B_754 [ab]\n"
            + "  B_754 -> B1_480 [bb]\n"
            + "}";

    Map<String, Set<String>> schema = new HashMap<>();
    schema.put("a", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("b", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));
    schema.put("b1", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("name")));

    schema.put("a_ab_b", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("b_bb_b", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put("b_cc_b", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));

    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(2, rst.getRows().size());

    dataGraphStr = "Graph {\n" + "  A_38 [a,name='629']\n" + "}";

    rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(1, rst.getRows().size());

    dataGraphStr =
        "Graph {\n"
            + "  A_204 [a,name='829']\n"
            + "  B_619 [b,name='404']\n"
            + "  B1_654 [b,name='848']\n"
            + "   B2_654 [b,name='849']\n"
            + "\n"
            + "B1_654->B2_654 [bb]\n"
            + "  B_619 -> B1_654 [bb]\n"
            + "  A_204 -> B_619 [ab]\n"
            + "}";
    rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(3, rst.getRows().size());
  }

  @Test
  public void testRepeatDsl() {
    Map<String, Set<String>> schema = getRelatedParty();
    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "  B,C,D,E,E1 [TestFinParty.RelatedParty]\n"
            + "  //4.4--B\n"
            + "  B->A [relatedReason] repeat(1,2) as e1\n"
            + "  //4.6\n"
            + "  B->C [relatedReason] as e2\n"
            + "  C->D [relatedReason] repeat(0,1) as e3\n"
            + "\n"
            + "  B->E [relatedReason] as e4\n"
            + "  E->E1 [relatedReason] as e5\n"
            + "\n"
            + "}\n"
            + "Rule {\n"
            + "  //4.4\n"
            + "  R1: A.id == 'A_992'\n"
            + "  R2: B.id == 'B_843'\n"
            + "  R3: C.id == 'C_690'\n"
            + "  R4: (exists(D) and D.id == 'D_333') or (not exists(D))\n"
            + "  R5: E.id == 'E_314'\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "//  get(A.id, B.id, C.id, D.id, E.id)\n"
            + "  get(A.id, B.id, C.id, D.id, E.id, E1.id)\n"
            + "}";

    String dataGraphStr =
        "Graph {\n"
            + "  A_992 [TestFinParty.RelatedParty,entityType='883',id='A_992']\n"
            + "  B_843 [TestFinParty.RelatedParty,entityType='982',id='B_843']\n"
            + "  C_690 [TestFinParty.RelatedParty,entityType='655',id='C_690']\n"
            + "  D_333 [TestFinParty.RelatedParty,entityType='995',id='D_333']\n"
            + "  E_314 [TestFinParty.RelatedParty,id='E_314']\n"
            + "  E1_343 [TestFinParty.RelatedParty,id='f']\n"
            + "\n"
            + "  E_314 -> E1_343 [relatedReason,relatedReason='277']\n"
            + "  B_843 -> A_992 [relatedReason,relatedReason='235']\n"
            + "  B_843 -> C_690 [relatedReason,relatedReason='440']\n"
            + "  C_690 -> D_333 [relatedReason,relatedReason='785']\n"
            + "  B_843 -> E_314 [relatedReason,relatedReason='61']\n"
            + "}";

    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(2, rst.getRows().size());
    java.util.Set<Object> dSet = new HashSet<>();
    for (Object[] row : rst.getRows()) {
      dSet.add(row[3]);
    }
    Assert.assertTrue(dSet.contains("D_333"));
    Assert.assertTrue(dSet.contains(null));

    dataGraphStr =
        "Graph {\n"
            + "  A_992 [TestFinParty.RelatedParty,entityType='883',id='A_992']\n"
            + "  B_843 [TestFinParty.RelatedParty,entityType='982',id='B_843']\n"
            + "  C_690 [TestFinParty.RelatedParty,entityType='655',id='C_690']\n"
            + "  D_333 [TestFinParty.RelatedParty,entityType='995',id='D_333']\n"
            + "  E_314 [TestFinParty.RelatedParty,id='E_314']\n"
            + "  E1_343 [TestFinParty.RelatedParty,id='f']\n"
            + "\n"
            + "  E_314 -> E1_343 [relatedReason,relatedReason='277']\n"
            + "  B_843 -> A_992 [relatedReason,relatedReason='235']\n"
            + "  B_843 -> C_690 [relatedReason,relatedReason='440']\n"
            + "  B_843 -> E_314 [relatedReason,relatedReason='61']\n"
            + "}";
    rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void ruleTest() {
    Map<String, Set<String>> schema = getRelatedParty();

    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "  B [TestFinParty.RelatedParty]\n"
            + "  B->A [relatedReason] as F1\n"
            + "}\n"
            + "Rule {\n"
            + "  R1: B.entityType == 'PERSON'\n"
            + "  R2: F1.relatedReason like '%SUPERVISOR'\n"
            + "  R3: A.id == 'A_865'\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, B.id)\n"
            + "}";
    String dataGraphStr =
        "Graph {\n"
            + "  A_865 [TestFinParty.RelatedParty,id='A_865']\n"
            + "  B_534 [TestFinParty.RelatedParty,entityType='105',id='B_534']\n"
            + "\n"
            + "  B_534 -> A_865 [relatedReason,relatedReason='951']\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(0, rst.getRows().size());

    dataGraphStr =
        "Graph {\n"
            + "  A_865 [TestFinParty.RelatedParty,id='A_865']\n"
            + "  B1 [TestFinParty.RelatedParty,entityType='PERSON',id='B1']\n"
            + "  B2 [TestFinParty.RelatedParty,entityType='PERSON',id='B2']\n"
            + "  B3 [TestFinParty.RelatedParty,entityType='PERSON',id='B3']\n"
            + "  B4 [TestFinParty.RelatedParty,entityType='PERSON',id='B4']\n"
            + "\n"
            + "  B1 -> A_865 [relatedReason,relatedReason='DIRECTOR_D']\n"
            + "  B2 -> A_865 [relatedReason,relatedReason='SENIOR_MANAGER']\n"
            + "  B3 -> A_865 [relatedReason,relatedReason='HUGE_INFLUENCE']\n"
            + "  B4 -> A_865 [relatedReason,relatedReason='SUPERVISOR']\n"
            + "}";
    rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testRepeatWithOptional() {

    Map<String, Set<String>> schema = getRelatedParty();
    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "  B, C [TestFinParty.RelatedParty]\n"
            + "// 1.17的B\n"
            + "  B->A [votingRatio] as F1\n"
            + "// 1.19的C\n"
            + "  B->C [relatedReason] repeat(1,20) as F2\n"
            + "\n"
            + "  B->A [relatedReason, __optional__='true'] as F3\n"
            + "}\n"
            + "Rule {\n"
            + "// 1.17的Rule\n"
            + "  R1: A.belongCategory == 'MY_BB'\n"
            + "  R2: F1.votingRatio >= 10\n"
            + "  R3: B.belongCategory != 'MY_GROUP'\n"
            + "  R4: B.entityType == 'CORPORATION'\n"
            + "// 1.19的Rule\n"
            + "  R5: F2.edges().constraint((pre,cur) => cur.relatedReason == 'CONTROL')\n"
            + "  R6: C.entityType == 'CORPORATION'\n"
            + "\n"
            + "  R7: F3.relatedReason == 'CONTROL'\n"
            + "  R8: A.id == 'A_524'\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, B.id, C.id).as(view0(a_id,b_id,c_id))\n"
            + "}";
    String dataGraphStr =
        "Graph {\n"
            + "  A_524 [TestFinParty.RelatedParty,belongCategory='MY_BB',id='A_524']\n"
            + "  B_5 [TestFinParty.RelatedParty,belongCategory='MY_BB',entityType='CORPORATION',id='B_5']\n"
            + "  C_407 [TestFinParty.RelatedParty,entityType='CORPORATION',id='C_407']\n"
            + "\n"
            + "  B_5 -> A_524 [votingRatio,votingRatio=11]\n"
            + "  B_5 -> C_407 [relatedReason,relatedReason='CONTROL']\n"
            + "  B_5 -> A_524 [relatedReason,relatedReason='CONTROL']\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(1, rst.getRows().size());

    dataGraphStr =
        "Graph {\n"
            + "  A_524 [TestFinParty.RelatedParty,belongCategory='MY_BB',id='A_524']\n"
            + "  B_5 [TestFinParty.RelatedParty,belongCategory='MY_BB',entityType='CORPORATION',id='B_5']\n"
            + "  C_407 [TestFinParty.RelatedParty,entityType='CORPORATION',id='C_407']\n"
            + "\n"
            + "  B_5 -> A_524 [votingRatio,votingRatio=11]\n"
            + "  B_5 -> C_407 [relatedReason,relatedReason='CONTROL']\n"
            + "  B_5 -> A_524 [relatedReason,relatedReason='abc']\n"
            + "}";
    rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(1, rst.getRows().size());

    dataGraphStr =
        "Graph {\n"
            + "  A_524 [TestFinParty.RelatedParty,belongCategory='MY_BB',id='A_524']\n"
            + "  B_5 [TestFinParty.RelatedParty,belongCategory='MY_BB',entityType='CORPORATION',id='B_5']\n"
            + "  C_407 [TestFinParty.RelatedParty,entityType='CORPORATION',id='C_407']\n"
            + "\n"
            + "  B_5 -> A_524 [votingRatio,votingRatio=11]\n"
            + "  B_5 -> C_407 [relatedReason,relatedReason='CONTROL']\n"
            + "}";
    rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testRollBackOptional2() {
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put(
        "TestFinParty.RelatedParty",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("name", "entityType", "belongCategory")));
    schema.put(
        "TestFinParty.RelatedParty_relatedReason_TestFinParty.RelatedParty",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("votingRatio", "relatedReason")));

    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "  B, C, D [TestFinParty.RelatedParty]\n"
            + "// 2.1\n"
            + "  B->A [relatedReason] repeat(1,20) as e1\n"
            + "// 2.5\n"
            + "  B->C [relatedReason, __optional__='true'] as e2\n"
            + "  C->D [relatedReason] repeat(0,20) as e3\n"
            + "}\n"
            + "Rule {\n"
            + "// 2.1\n"
            + "  R1: e1.edges().constraint((pre,cur) => cur.relatedReason == 'CONTROL')\n"
            + "  R2: A.entityType == 'CORPORATION'\n"
            + "  R3: B.entityType == 'CORPORATION'\n"
            + "// 2.5\n"
            + "  R4: e2.relatedReason == 'CONTROL'\n"
            + "  R1: e3.edges().constraint((pre,cur) => cur.relatedReason == 'CONTROL')\n"
            + "  R6: C.entityType == 'CORPORATION'\n"
            + "  R7: D.entityType == 'CORPORATION'\n"
            + "  R8: C.belongCategory != 'MY_GROUP' && C.belongCategory != 'MY_BB'\n"
            + "  R9: D.belongCategory != 'MY_GROUP' && D.belongCategory != 'MY_BB'\n"
            + "  R10: A.id == 'A_89'\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, B.id, C.id, D.id)\n"
            + "}";
    String dataGraphStr =
        "Graph {\n"
            + "  A_89 [TestFinParty.RelatedParty,entityType='CORPORATION',id='A_89']\n"
            + "  B_191 [TestFinParty.RelatedParty,entityType='PERSON',id='B_191']\n"
            + "  C_280 [TestFinParty.RelatedParty,belongCategory='498',entityType='CORPORATION',id='C_280']\n"
            + "  D_465 [TestFinParty.RelatedParty,belongCategory='83',entityType='CORPORATION',id='D_465']\n"
            + "\n"
            + "  B_191 -> A_89 [relatedReason,relatedReason='CONTROL']\n"
            + "  B_191 -> C_280 [relatedReason,relatedReason='CONTROL']\n"
            + "  C_280 -> D_465 [relatedReason,relatedReason='CONTROL']\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(0, rst.getRows().size());
  }

  private Map<String, Set<String>> getRelatedParty() {
    Map<String, Set<String>> schema = new HashMap<>();

    schema.put(
        "TestFinParty.Start",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("name", "entityType", "belongCategory")));
    schema.put(
        "TestFinParty.Start_relatedReason_TestFinParty.RelatedParty",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "votingRatio", "relatedReason", "name")));
    schema.put(
        "TestFinParty.Start_votingRatio_TestFinParty.RelatedParty",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "votingRatio", "relatedReason", "name")));
    schema.put(
        "TestFinParty.Start_shareholdingRatio_TestFinParty.RelatedParty",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "votingRatio", "relatedReason", "shareholdingRatio", "name")));

    schema.put(
        "TestFinParty.RelatedParty",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("name", "entityType", "belongCategory")));
    schema.put(
        "TestFinParty.RelatedParty_relatedReason_TestFinParty.RelatedParty",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "votingRatio", "relatedReason", "name")));
    schema.put(
        "TestFinParty.RelatedParty_votingRatio_TestFinParty.RelatedParty",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "votingRatio", "relatedReason", "name")));
    schema.put(
        "TestFinParty.RelatedParty_shareholdingRatio_TestFinParty.RelatedParty",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "votingRatio", "relatedReason", "shareholdingRatio", "name")));

    return schema;
  }

  @Test
  public void testRollBackOptional3() {
    Map<String, Set<String>> schema = getRelatedParty();
    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "  B, C, D [TestFinParty.RelatedParty]\n"
            + "// 2.1\n"
            + "  B->A [relatedReason]  as e1\n"
            + "// 2.5\n"
            + "  B->C [relatedReason, __optional__='true'] as e2\n"
            + "  C->D [relatedReason] repeat(0,20) as e3\n"
            + "}\n"
            + "Rule {\n"
            + "// 2.1\n"
            + "  R1: e1.relatedReason == 'CONTROL'\n"
            + "  R2: A.entityType == 'CORPORATION'\n"
            + "  R3: B.entityType == 'CORPORATION'\n"
            + "// 2.5\n"
            + "  R4: e2.relatedReason == 'CONTROL'\n"
            + "  R5: e3.edges().constraint((pre,cur) => cur.relatedReason == 'CONTROL')\n"
            + "  R6: C.entityType == 'CORPORATION'\n"
            + "  R7: (not exists(D)) or (exists(D) and D.entityType == 'CORPORATION')\n"
            + "  R8: C.belongCategory != 'MY_GROUP' && C.belongCategory != 'MY_BB'\n"
            + "  R9: (not exists(D)) or (exists(D) && D.belongCategory != 'MY_GROUP' && D.belongCategory != 'MY_BB')\n"
            + "  R10: A.id == 'A_89'\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, B.id, C.id, D.id)\n"
            + "}";
    String dataGraphStr =
        "Graph {\n"
            + "  A_89 [TestFinParty.RelatedParty,entityType='CORPORATION',id='A_89']\n"
            + "  B_191 [TestFinParty.RelatedParty,entityType='CORPORATION',id='B']\n"
            + "  C_280 [TestFinParty.RelatedParty,belongCategory='498',entityType='CORPORATION',id='C']\n"
            + "  D_465 [TestFinParty.RelatedParty,belongCategory='83',entityType='CORPORATION',id='D']\n"
            + "\n"
            + "  B_191 -> A_89 [relatedReason,relatedReason='CONTROL']\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testOptionalRmvBack2() {
    Map<String, Set<String>> schema = getRelatedParty();

    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "B, C, D [TestFinParty.RelatedParty]\n"
            + "// 1.7.1的B, 1.32.1的B\n"
            + "B->A [relatedReason] as F1\n"
            + "\n"
            + "// 1.8的C\n"
            + "B->C [relatedReason, __optional__='true'] as F3\n"
            + "// 1.10.1的D E\n"
            + "C->D [votingRatio] repeat(1,2) as F4\n"
            + "}\n"
            + "Rule {\n"
            + "  R1: A.id == 'A'\n"
            + "  R2: F3.id == 'F3'\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, B.id, C.id, D.id)\n"
            + "}";
    String dataGraphStr =
        "Graph {\n"
            + "  A_730 [TestFinParty.RelatedParty,id='A']\n"
            + "  B_471 [TestFinParty.RelatedParty,id='B']\n"
            + "  C_278 [TestFinParty.RelatedParty,id='C']\n"
            + "  D_815 [TestFinParty.RelatedParty,id='D']\n"
            + "\n"
            + "  B_471 -> A_730 [relatedReason]\n"
            + "  B_471 -> C_278 [relatedReason, id='f1']\n"
            + "  C_278 -> D_815 [votingRatio,id='f2']\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(0, rst.getRows().size());
  }

  @Test
  public void testRepeatLoop1() {
    Map<String, Set<String>> schema = getRelatedParty();

    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "B, C [TestFinParty.RelatedParty]\n"
            + "// 1.7.1的B, 1.32.1的B\n"
            + "B->A [relatedReason] as F1\n"
            + "\n"
            + "// 1.8的C\n"
            + "B->C [relatedReason] repeat(1,20) as F3\n"
            + "}\n"
            + "Rule {\n"
            + "  R1: A.id == 'A_730'\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, B.id, C.id).as(table0(a_id,b_id,c_id))\n"
            + "}";
    String dataGraphStr =
        "Graph {\n"
            + "  A_730 [TestFinParty.RelatedParty,id='A_730']\n"
            + "  B_471 [TestFinParty.RelatedParty,id='B']\n"
            + "  C_278 [TestFinParty.RelatedParty,id='C']\n"
            + "\n"
            + "  B_471 -> A_730 [relatedReason]\n"
            + "  A_730 -> B_471 [relatedReason]\n"
            + "  B_471 -> C_278 [relatedReason]\n"
            + "  C_278 -> A_730 [relatedReason]\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testRepeatLoop3() {
    Map<String, Set<String>> schema = getRelatedParty();

    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "B, C [TestFinParty.RelatedParty]\n"
            + "// 1.7.1的B, 1.32.1的B\n"
            + "B->A [relatedReason] as F1\n"
            + "\n"
            + "// 1.8的C\n"
            + "B->C [relatedReason] repeat(1,20) as F3\n"
            + "}\n"
            + "Rule {\n"
            + "  R1: A.id == 'A_730'\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, B.id, C.id)\n"
            + "}";
    String dataGraphStr =
        "Graph {\n"
            + "  A_730 [TestFinParty.RelatedParty,id='A_730']\n"
            + "  B_471 [TestFinParty.RelatedParty,id='B']\n"
            + "  C_278 [TestFinParty.RelatedParty,id='C']\n"
            + "\n"
            + "  B_471 -> A_730 [relatedReason]\n"
            + "  A_730 -> B_471 [relatedReason]\n"
            + "  B_471 -> C_278 [relatedReason]\n"
            + "  C_278 -> A_730 [vote]\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testRepeatLoop4() {
    Map<String, Set<String>> schema = getRelatedParty();

    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "B, C [TestFinParty.RelatedParty]\n"
            + "// 1.7.1的B, 1.32.1的B\n"
            + "B->A [relatedReason] as F1\n"
            + "\n"
            + "// 1.8的C\n"
            + "B->C [relatedReason] repeat(1,20) as F3\n"
            + "}\n"
            + "Rule {\n"
            + "  R1: A.id == 'A_730'\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, B.id, C.id)\n"
            + "}";
    String dataGraphStr =
        "Graph {\n"
            + "  A_730 [TestFinParty.RelatedParty,id='A_730']\n"
            + "  B_471 [TestFinParty.RelatedParty,id='B']\n"
            + "  C_278 [TestFinParty.RelatedParty,id='C']\n"
            + "  D_278 [TestFinParty.RelatedParty,id='C']\n"
            + "\n"
            + "  B_471 -> A_730 [relatedReason]\n"
            + "  A_730 -> B_471 [relatedReason]\n"
            + "  B_471 -> C_278 [relatedReason]\n"
            + "  C_278 -> D_278 [relatedReason]\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr);
    Assert.assertEquals(2, rst.getRows().size());
  }

  @Test
  public void testRenameHang() {
    Map<String, Set<String>> schema = getRelatedParty();

    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "  D [TestFinParty.RelatedParty]\n"
            + "  B3,B4,C3,C4 [TestFinParty.RelatedParty]\n"
            + "  X5,X6,X7,X8 [TestFinParty.RelatedParty]\n"
            + "  Y5,Y6,Y7,Y8 [TestFinParty.RelatedParty]\n"
            + "\n"
            + "// 3.5\n"
            + "  D->A [relatedReason] repeat(1,20) as e5\n"
            + "// 3.3 B3 C3\n"
            + "  B3->D [relatedReason, __optional__='true'] as e6\n"
            + "  B3->C3 [relatedReason, __optional__='true'] as e7\n"
            + "//3.4--B4 C4\n"
            + "  B4->A [relatedReason, __optional__='true'] as e8\n"
            + "  B4->C4 [relatedReason, __optional__='true'] as e9\n"
            + "\n"
            + "//3.18\n"
            + "  B3->X5 [relatedReason, __optional__='true'] as e14\n"
            + "  C3->X6 [relatedReason, __optional__='true'] as e15\n"
            + "  B4->X7 [relatedReason, __optional__='true'] as e16\n"
            + "  C4->X8 [relatedReason, __optional__='true'] as e17\n"
            + "  X5->Y5 [relatedReason] repeat(0,20) as e22\n"
            + "  X6->Y6 [relatedReason] repeat(0,20) as e23\n"
            + "  X7->Y7 [relatedReason] repeat(0,20) as e24\n"
            + "  X8->Y8 [relatedReason] repeat(0,20) as e25\n"
            + "}\n"
            + "Rule {  \n"
            + "  R1: A.entityType == 'CORPORATION'\n"
            + "// 3.5\n"
            + "  R10: D.entityType == 'CORPORATION'\n"
            + "  R1711: e5.edges().constraint((pre,cur) => cur.relatedReason == 'CONTROL')\n"
            + "// 3.3\n"
            + "  R12: B3.entityType == 'PERSON'\n"
            + "  R13: C3.entityType == 'PERSON'\n"
            + "  R14: e6.relatedReason like '%DIRECTOR_D' || e6.relatedReason like '%SUPERVISOR' || e6.relatedReason like '%SENIOR_MANAGER'\n"
            + "  R15: e7.relatedReason like '%CLOSE_FAMILY_MEMBERS' || e7.relatedReason like '%OTHER_CLOSE_FAMILY_MEMBERS'\n"
            + "//3.4\n"
            + "  R16: B4.entityType == 'PERSON'\n"
            + "  R17: C4.entityType == 'PERSON'\n"
            + "  R18: e8.relatedReason like '%DIRECTOR_D' || e8.relatedReason like '%SUPERVISOR' || e8.relatedReason like '%SENIOR_MANAGER'\n"
            + "  R19: e9.relatedReason like '%CLOSE_FAMILY_MEMBERS' || e9.relatedReason like '%OTHER_CLOSE_FAMILY_MEMBERS'\n"
            + "\n"
            + "//3.18\n"
            + "  R24: e14.relatedReason == 'CONTROL' || e14.relatedReason like '%CONTROL_TOGETHER'\n"
            + "  R25: e15.relatedReason == 'CONTROL' || e15.relatedReason like '%CONTROL_TOGETHER'\n"
            + "  R26: e16.relatedReason == 'CONTROL' || e16.relatedReason like '%CONTROL_TOGETHER'\n"
            + "  R27: e17.relatedReason == 'CONTROL' || e17.relatedReason like '%CONTROL_TOGETHER'\n"
            + "  R171: e23.edges().constraint((pre,cur) => cur.relatedReason == 'CONTROL')\n"
            + "  R173: e22.edges().constraint((pre,cur) => cur.relatedReason == 'CONTROL')\n"
            + "  R172: e24.edges().constraint((pre,cur) => cur.relatedReason == 'CONTROL')\n"
            + "  R17: e25.edges().constraint((pre,cur) => cur.relatedReason == 'CONTROL')\n"
            + "  R40: X5.entityType == 'CORPORATION'\n"
            + "  R41: X6.entityType == 'CORPORATION'\n"
            + "  R42: X7.entityType == 'CORPORATION'\n"
            + "  R43: X8.entityType == 'CORPORATION'\n"
            + "  R48: (exists(Y5) and Y5.entityType == 'CORPORATION') or (not exists(Y5))\n"
            + "  R49: (exists(Y6) and Y6.entityType == 'CORPORATION') or (not exists(Y6))\n"
            + "  R50: (exists(Y7) and Y7.entityType == 'CORPORATION') or (not exists(Y7))\n"
            + "  R51: (exists(Y8) and Y8.entityType == 'CORPORATION') or (not exists(Y8))\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, D.id, B3.id, C3.id, B4.id, C4.id, X5.id, X6.id, X7.id, X8.id, Y5.id, Y6.id, Y7.id, Y8.id)\n"
            + "}";

    String dataGraphStr2 =
        "Graph {\n"
            + "  A [TestFinParty.RelatedParty,id='A', entityType='CORPORATION']\n"
            + "  B [TestFinParty.RelatedParty,id='B', entityType='CORPORATION']\n"
            + "  B -> A [relatedReason, relatedReason='CONTROL',name='abc']\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr2);
    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testRepeatUntil1() {
    Map<String, Set<String>> schema = getRelatedParty();

    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "  B, C [TestFinParty.RelatedParty]\n"
            + "// 1.17的B 必须存在\n"
            + "  C->B [votingRatio] repeat(0,2) as F1\n"
            + "// 1.19的C D 可以不存在\n"
            + "  B->A [votingRatio, __optional__='true'] as F2\n"
            + "}\n"
            + "Rule {\n"
            + "  R1: A.id == 'A'\n"
            + "  R1(\"只保留最长的路径\"): group(A).keep_longest_path(F1)\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, B.id, C.id)\n"
            + "}";

    String dataGraphStr2 =
        "Graph {\n"
            + "  A [TestFinParty.RelatedParty,id='A']\n"
            + "  B [TestFinParty.RelatedParty,id='B', entityType='PERSON']\n"
            + "  B1 [TestFinParty.RelatedParty,id='B1', entityType='PERSON']\n"
            + "  B2 [TestFinParty.RelatedParty,id='B2', entityType='PERSON']\n"
            + "  C [TestFinParty.RelatedParty,id='C', entityType='PERSON']\n"
            + "  B -> A [votingRatio, votingRatio=10,name='abc']\n"
            + "  B1 -> B [votingRatio, votingRatio=10,name='abc']\n"
            + "  B2 -> B1 [votingRatio, votingRatio=10,name='abc']\n"
            + "  B1 -> C [votingRatio, votingRatio=10,name='abc']\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr2);
    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testRepeatUntil2() {
    Map<String, Set<String>> schema = getRelatedParty();

    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "  B, C [TestFinParty.RelatedParty]\n"
            + "// 1.17的B 必须存在\n"
            + "  C->B [votingRatio] repeat(0,20) as F1\n"
            + "// 1.19的C D 可以不存在\n"
            + "  B->A [votingRatio, __optional__='true'] as F2\n"
            + "}\n"
            + "Rule {\n"
            + "  R1: A.id == 'A'\n"
            + "  R1(\"只保留最长的路径\"): group(A).keep_longest_path(F1)\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, B.id, C.id)\n"
            + "}";

    String dataGraphStr2 =
        "Graph {\n"
            + "  A [TestFinParty.RelatedParty,id='A']\n"
            + "  B [TestFinParty.RelatedParty,id='B', entityType='PERSON']\n"
            + "  B -> A [votingRatio, votingRatio=10,name='abc']\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr2);
    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testRepeatUntil3() {
    Map<String, Set<String>> schema = getRelatedParty();

    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "  B, C, D [TestFinParty.RelatedParty]\n"
            + "// 1.17的B 必须存在\n"
            + "  C->B [votingRatio] repeat(0,20) as F1\n"
            + "  C->D [votingRatio] as F3\n"
            + "// 1.19的C D 可以不存在\n"
            + "  B->A [votingRatio] as F2\n"
            + "}\n"
            + "Rule {\n"
            + "  R1: A.id == 'A'\n"
            + "  R1(\"只保留最长的路径\"): group(A).keep_longest_path(F1)\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, B.id, C.id,D.id)\n"
            + "}";

    String dataGraphStr2 =
        "Graph {\n"
            + "  A [TestFinParty.RelatedParty,id='A']\n"
            + "  B [TestFinParty.RelatedParty,id='B', entityType='PERSON']\n"
            + "  B1 [TestFinParty.RelatedParty,id='B1', entityType='PERSON']\n"
            + "  C [TestFinParty.RelatedParty,id='C', entityType='PERSON']\n"
            + "  B1 -> B [votingRatio, votingRatio=10,name='abc']\n"
            + "  B -> A [votingRatio, votingRatio=10,name='abc']\n"
            + "  B1 -> C [votingRatio, votingRatio=10,name='abc']\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr2);
    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testRepeatUntil4() {
    Map<String, Set<String>> schema = getRelatedParty();

    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "  B, C, D [TestFinParty.RelatedParty]\n"
            + "// 1.17的B 必须存在\n"
            + "  C->B [votingRatio] repeat(0,20) as F1\n"
            + "  C->D [votingRatio] as F3\n"
            + "// 1.19的C D 可以不存在\n"
            + "  B->A [votingRatio] as F2\n"
            + "}\n"
            + "Rule {\n"
            + "  R1: A.id == 'A'\n"
            + "  R1(\"只保留最长的路径\"): group(A).keep_longest_path(F1)\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, B.id, C.id,D.id)\n"
            + "}";

    String dataGraphStr2 =
        "Graph {\n"
            + "  A [TestFinParty.RelatedParty,id='A']\n"
            + "  B [TestFinParty.RelatedParty,id='B', entityType='PERSON']\n"
            + "  C [TestFinParty.RelatedParty,id='C', entityType='PERSON']\n"
            + "  B -> A [votingRatio, votingRatio=10,name='abc']\n"
            + "  B -> C [votingRatio, votingRatio=10,name='abc']\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr2);
    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testIterCompute0() {
    Map<String, Set<String>> schema = getRelatedParty();

    String dsl =
        "GraphStructure {\n"
            + "  S [TestFinParty.Start, __start__='true']\n"
            + "  A [TestFinParty.RelatedParty]\n"
            + "  B [TestFinParty.RelatedParty]\n"
            + "\n"
            + "  S->A [shareholdingRatio]  as e2\n"
            + "  B->A [shareholdingRatio] repeat(0,20) as e1\n"
            + "}\n"
            + "Rule {\n"
            + "  totalRate = e1.edges().reduce((x,y) => y.shareholdingRatio/100.0 * x, 1)\n"
            + " R1: totalRate > 0.05\n"
            + "  R0(\"只保留最长的路径\"): group(S, A).keep_longest_path(e1)\n"
            + "  R2: B.entityType == 'CORPORATION'\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, B.id)\n"
            + "}";

    String dataGraphStr2 =
        "Graph {\n"
            + "  S [TestFinParty.Start,id='S',entityType='CORPORATION']\n"
            + "  A [TestFinParty.RelatedParty,id='A',entityType='CORPORATION']\n"
            + "  B [TestFinParty.RelatedParty,id='B', entityType='CORPORATION']\n"
            + "  C3 [TestFinParty.RelatedParty,id='C3', entityType='CORPORATION']\n"
            + "  S -> A [shareholdingRatio, shareholdingRatio=50,name='abc']\n"
            + "  B -> A [shareholdingRatio, shareholdingRatio=50,name='abc']\n"
            + "  C3 -> B [shareholdingRatio, shareholdingRatio=10,name='abc']\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr2);
    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testIterCompute1() {
    Map<String, Set<String>> schema = getRelatedParty();

    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "  B [TestFinParty.RelatedParty]\n"
            + "\n"
            + "  B->A [shareholdingRatio] repeat(0,20) as e1\n"
            + "}\n"
            + "Rule {\n"
            + "  totalRate = e1.edges().reduce((x,y) => y.shareholdingRatio/100.0 * x, 1)\n"
            + " R1: totalRate > 0.05\n"
            + "  R0(\"只保留最长的路径\"): group(A).keep_longest_path(e1)\n"
            + "R2: (exists(B) and B.entityType == 'CORPORATION') or (not exist(B))\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, B.id)\n"
            + "}";

    String dataGraphStr2 =
        "Graph {\n"
            + "  A [TestFinParty.RelatedParty,id='A',entityType='CORPORATION']\n"
            + "  B [TestFinParty.RelatedParty,id='B', entityType='CORPORATION']\n"
            + "  C2 [TestFinParty.RelatedParty,id='C2', entityType='CORPORATION']\n"
            + "  C1 [TestFinParty.RelatedParty,id='C1', entityType='CORPORATION']\n"
            + "  C3 [TestFinParty.RelatedParty,id='C3', entityType='CORPORATION']\n"
            + "  D1 [TestFinParty.RelatedParty,id='D1', entityType='CORPORATION']\n"
            + "  B -> A [shareholdingRatio, shareholdingRatio=50,name='abc']\n"
            + "  C1 -> B [shareholdingRatio, shareholdingRatio=50,name='abc']\n"
            + "  C2 -> B [shareholdingRatio, shareholdingRatio=15,name='abc']\n"
            + "  C3 -> B [shareholdingRatio, shareholdingRatio=10,name='abc']\n"
            + "  D1 -> C1 [shareholdingRatio, shareholdingRatio=50,name='abc']\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr2);
    Assert.assertEquals(6, rst.getRows().size());
  }

  @Test
  public void testMultiRule() {
    Map<String, Set<String>> schema = getRelatedParty();

    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "  B, D, X, Y [TestFinParty.RelatedParty]\n"
            + "// 1.7.1的B, 1.32.1的B\n"
            + "  B->A [relatedReason] as F1\n"
            + "// 1.7.3的B\n"
            + "  B->A [votingRatio] as F2\n"
            + "// 1.10.2的X Y\n"
            + "  B->D [votingRatio] as F4\n"
            + "\n"
            + "  B->X [votingRatio] as F6\n"
            + "\n"
            + "  X->Y [relatedReason] repeat(0,20) as F8\n"
            + "}\n"
            + "Rule {\n"
            + "// 1.7.1的Rule, 1.7.3的Rule, 1.32.1的Rule\n"
            + "  R1: B.entityType == 'PERSON'\n"
            + "  R2: F1.relatedReason like '%DIRECTOR_D' || F1.relatedReason like '%TOP_EXECUTIVE' || F2.votingRatio >= 10 || F1.relatedReason like '%SUPERVISOR'\n"
            + "// 1.10.2的Rule\n"
            + "  R5: D.belongCategory=='MY_GROUP' || D.belongCategory=='MY_BB'\n"
            + "  R6: F4.votingRatio >= 30 && F6.votingRatio >= 10\n"
            + "  R17: F8.edges().constraint((pre,cur) => cur.relatedReason == 'CONTROL')\n"
            + "  R8: X.entityType == 'CORPORATION'\n"
            + "  R9: Y.entityType == 'CORPORATION'\n"
            + "  R10: X.belongCategory != 'MY_GROUP' && X.belongCategory != 'MY_BB'\n"
            + "  R11: A.id == 'A'\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, B.id, D.id, X.id, Y.id)\n"
            + "}";

    String dataGraphStr2 =
        "Graph {\n"
            + "  A_810 [TestFinParty.RelatedParty,id='A']\n"
            + "  B_464 [TestFinParty.RelatedParty,entityType='PERSON',id='B']\n"
            + "  D_354 [TestFinParty.RelatedParty,belongCategory='MY_GROUP',id='D']\n"
            + "  X_693 [TestFinParty.RelatedParty,belongCategory='319',entityType='CORPORATION',id='X']\n"
            + "  Y_837 [TestFinParty.RelatedParty,entityType='CORPORATION',id='Y']\n"
            + "\n"
            + "  B_464 -> X_693 [votingRatio,votingRatio=9]\n"
            +
            //                "  X_693 -> Y_837 [relatedReason,relatedReason='CONTROL']\n" +
            "  B_464 -> A_810 [relatedReason,relatedReason='DIRECTOR_D']\n"
            + "  B_464 -> A_810 [votingRatio,votingRatio=10]\n"
            + "  B_464 -> D_354 [votingRatio,votingRatio=40]\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr2);
    Assert.assertEquals(0, rst.getRows().size());
  }

  @Test
  public void testMultiRule2() {
    Map<String, Set<String>> schema = getRelatedParty();

    String dsl =
        "GraphStructure {\n"
            + "  A [TestFinParty.RelatedParty, __start__='true']\n"
            + "  B, D, X, Y [TestFinParty.RelatedParty]\n"
            + "// 1.7.1的B, 1.32.1的B\n"
            + "  B->A [relatedReason] as F1\n"
            + "// 1.7.3的B\n"
            + "  B->A [votingRatio] as F2\n"
            + "// 1.10.2的X Y\n"
            + "  B->D [votingRatio] as F4\n"
            + "\n"
            + "  B->X [votingRatio] as F6\n"
            + "\n"
            + "  X->Y [relatedReason] repeat(0,20) as F8\n"
            + "}\n"
            + "Rule {\n"
            + "// 1.7.1的Rule, 1.7.3的Rule, 1.32.1的Rule\n"
            + "  R1: B.entityType == 'PERSON'\n"
            + "  R2: F1.relatedReason like '%DIRECTOR_D' || F1.relatedReason like '%TOP_EXECUTIVE' || F2.votingRatio >= 10 || F1.relatedReason like '%SUPERVISOR'\n"
            + "// 1.10.2的Rule\n"
            + "  R5: D.belongCategory=='MY_GROUP' || D.belongCategory=='MY_BB'\n"
            + "  R6: F4.votingRatio >= 30 && F6.votingRatio >= 10\n"
            + "  R17: F8.edges().constraint((pre,cur) => cur.relatedReason == 'CONTROL')\n"
            + "  R8: X.entityType == 'CORPORATION'\n"
            + "  R9: (exists(Y) and Y.entityType == 'CORPORATION') or (not exists(Y))\n"
            + "  R10: X.belongCategory != 'MY_GROUP' && X.belongCategory != 'MY_BB'\n"
            + "  R11: A.id == 'A_810'\n"
            + "}\n"
            + "Action {\n"
            + "  get(A.id, B.id, D.id, X.id, Y.id)\n"
            + "}";

    String dataGraphStr2 =
        "Graph {\n"
            + "  A_810 [TestFinParty.RelatedParty,id='A_810']\n"
            + "  B_464 [TestFinParty.RelatedParty,entityType='PERSON',id='B']\n"
            + "  D_354 [TestFinParty.RelatedParty,belongCategory='MY_GROUP',id='D']\n"
            + "  X_693 [TestFinParty.RelatedParty,belongCategory='319',entityType='CORPORATION',id='X']\n"
            + "  Y_837 [TestFinParty.RelatedParty,entityType='CORPORATION',id='Y']\n"
            + "\n"
            + "  B_464 -> X_693 [votingRatio,votingRatio=30]\n"
            +
            //                "  X_693 -> Y_837 [relatedReason,relatedReason='CONTROL']\n" +
            "  B_464 -> A_810 [relatedReason,relatedReason='DIRECTOR_D']\n"
            + "  B_464 -> A_810 [votingRatio,votingRatio=10]\n"
            + "  B_464 -> D_354 [votingRatio,votingRatio=40]\n"
            + "}";
    LocalReasonerResult rst = runTest(schema, dsl, dataGraphStr2);
    Assert.assertEquals(1, rst.getRows().size());
  }

  @Test
  public void testContextSolver() {
    String dsl =
      "Define (A:RAC.Param)-[p:related]->(B:RAC.Param) {\n"
              + "    GraphStructure {\n"
              + "      (A)-[:param2link]->(L:RAC.LinkID)\n"
              + "      (L)-[:link2param]->(B)\n"
              + "    }\n"
              + "    Rule {\n"
              + "     p.id = L.id\n"
              + "     p.context = concat(A.context, \",\", B.context)\n"
              + "    }\n"
              + "   }\n"
              + " GraphStructure {\n"
              + "   A [RAC.Param]\n\n"
              + "   B [RAC.Param]\n"
              + "   A -> B [related] repeat(1,10) as e\n"
              + " }\n"
              + " Rule {\n"
//              + "   R1: concatStr = e.edges().reduce((res, ele) => concat(res, \",\", ele.context), \"\") \n"
//              + "   R2: racContextSensitivitySolver(concatStr, \",\", 1) \n"
              + "     R1: concatStr = e.edges().reduce((res, ele) => concat(res, ele.context), \"\") \n"
              + " }"
              + " Action {\n"
              + "   get(A.id,B.id,__path__)"
              + "}\n";

    System.out.println(dsl);
    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put("RAC.Param", Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "parameterIndex", "context")));
    schema.put("RAC.LinkID", Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "srcContext", "dstContext", "linkFieldList")));
    schema.put(
            "RAC.Param_param2link_RAC.LinkID", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
            "RAC.LinkID_link2param_RAC.Param", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
            "com.antgroup.openspg.reasoner.runner.local.main.transitive.TransitiveOptionalTest$RacGraphLoader");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, false);
    params.put(ConfigKey.KG_REASONER_BINARY_PROPERTY, "false");
    params.put(Constants.SPG_REASONER_MULTI_VERSION_ENABLE, "true");
    task.setParams(params);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);

  }

  public static class RacGraphLoader extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
        constructionVertex("C1", "RAC.Param", "id", "C1", "parameterIndex", "1", "context","0"),
        constructionVertex("C2", "RAC.Param", "id", "C1", "parameterIndex", "1", "context","1"),
        constructionVertex("C3", "RAC.Param", "id", "C1", "parameterIndex", "1", "context","-1"),
        constructionVertex("C4", "RAC.Param", "id", "C1", "parameterIndex", "1", "context","-2"),
        constructionVertex("C5", "RAC.Param", "id", "C1", "parameterIndex", "1", "context","0"),
        constructionVertex("L1", "RAC.LinkID", "id", "L1", "srcContext", "0", "dstContext", "1",
              "linkFieldList", "[\"com.alipay.A\",\"com.alipay.B\"]"),
        constructionVertex("L2", "RAC.LinkID", "id", "L2", "srcContext", "-1", "dstContext", "0",
              "linkFieldList", "[\"com.alipay.X\"]"),
        constructionVertex("L3", "RAC.LinkID", "id", "L3", "srcContext", "-2", "dstContext", "0",
              "linkFieldList", "[\"com.alipay.A\"]"),
        constructionVertex("L4", "RAC.LinkID", "id", "L4", "srcContext", "-1", "dstContext", "0",
              "linkFieldList", "[\"com.alipay.X\"]")
      );

    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
              constructionEdge("C1", "param2link", "L1"),
              constructionEdge("L1", "link2param", "C2"),
              constructionEdge("C2", "param2link", "L2"),
              constructionEdge("C2", "param2link", "L3"),
              constructionEdge("L2", "link2param", "C3"),
              constructionEdge("L3", "link2param", "C4"),
              constructionEdge("C4", "param2link", "L4"),
              constructionEdge("L4", "link2param", "C5")
      );
    }
  }
}
