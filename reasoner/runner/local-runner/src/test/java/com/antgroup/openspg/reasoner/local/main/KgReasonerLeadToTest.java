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
import org.junit.Assert;
import org.junit.Test;

public class KgReasonerLeadToTest {
  @Test
  public void addVertexTest1() {
    String dsl =
        "Define (s:DomainFamily)-[p:total_domain_num]->(o:Int) {\n"
            + "    GraphStructure {\n"
            + "        (s)<-[e:belong]-(d:Domain)\n"
            + "    }\n"
            + "    Rule {\n"
            + "        num = group(s).count(d)\n"
            + "        o = num\n"
            + "    }\n"
            + "    Action {\n"
            + "          \tcreateNodeInstance(\n"
            + "    \t        type=DomainFamilyCount,\n"
            + "    \t        value={\n"
            + "    \t\t        id = s.id\n"
            + "    \t\t        主体 = s.hangye\n"
            + "    \t\t        客体 = o\n"
            + "    \t\t        时间 = e.occurTime\n"
            + "    \t\t        空间 = e.occurSpace\n"
            + "             }\n"
            + "           )\n"
            + "    }"
            + "}";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
        "DomainFamily", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id", "hangye")));
    schema.put("Domain", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "Domain_belong_DomainFamily",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("occurTime", "occurSpace")));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.local.main.KgReasonerLeadToTest$GraphLoaderForAddVertex");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setParams(params);

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    Assert.assertEquals(1, result.getVertexList().size());
  }

  public static class GraphLoaderForAddVertex extends AbstractLocalGraphLoader {
    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("df1", "DomainFamily"),
          constructionVertex("d1", "Domain"),
          constructionVertex("d2", "Domain"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionEdge("d1", "belong", "df1"), constructionEdge("d2", "belong", "df1"));
    }
  }

  @Test
  public void leadToTest1() {
    String dsl =
        "Define (s: `HengSheng.TaxonomyOfCompanyAccident`/`周期性行业头部上市公司停产事故`)-[p: leadTo]->(o: `HengSheng.TaxonomyOfCompanyInfluence`/`利润下降`) {\n"
            + "    GraphStructure {\n"
            + " \n  }\n"
            + "    Rule {\n"
            + "    }\n"
            + "    Action {\n"
            + "    \tcompanyInfluenceEvent = createNodeInstance(\n"
            + "        \ttype=HengSheng.CompanyInfluence,\n"
            + "            value={\n"
            + "            \tname='公司利润下降'\n"
            + "                influenceDegree='下降'\n"
            + "                indexTag='利润'\n"
            + "                subject=s.subject\n"
            + "                objectWho='下降'\n"
            + "            }\n"
            + "        )\n"
            + "        createEdgeInstance(\n"
            + "            src=s,\n"
            + "            dst=companyInfluenceEvent,\n"
            + "            type=leadTo,\n"
            + "            value={\n"
            + "                \n"
            + "            }\n"
            + "        )\n"
            + "    }";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);

    // add mock catalog
    Map<String, scala.collection.immutable.Set<String>> schema = new HashMap<>();
    schema.put(
        "HengSheng.TaxonomyOfCompanyAccident",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "HengSheng.TaxonomyOfCompanyInfluence",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "HengSheng.CompanyAccident",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet(
                "id",
                "companyIndustryType",
                "companyType",
                "companyMarketShare",
                "accidentImpactType",
                "subject")));
    schema.put("HengSheng.Industry", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("HengSheng.Company", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "HengSheng.CompanyInfluence",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "name", "influenceDegree", "indexTag", "subject", "objectWho")));
    schema.put(
        "HengSheng.CompanyAccident_subject_HengSheng.Industry",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "HengSheng.Company_belongIndustry_HengSheng.Industry",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "HengSheng.CompanyAccident_belongTo_HengSheng.TaxonomyOfCompanyAccident",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "HengSheng.CompanyInfluence_belongTo_HengSheng.TaxonomyOfCompanyInfluence",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    schema.put(
        "HengSheng.CompanyAccident_leadTo_HengSheng.CompanyInfluence",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);

    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.local.main.KgReasonerLeadToTest$GraphLoader");

    // enable subquery
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    params.put(Constants.START_ALIAS, "s");
    task.setParams(params);

    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    Assert.assertEquals(1, result.getVertexList().size());
  }

  public static class GraphLoader extends AbstractLocalGraphLoader {

    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("周期性行业头部上市公司停产事故", "HengSheng.TaxonomyOfCompanyAccident"),
          constructionVertex("利润下降", "HengSheng.TaxonomyOfCompanyInfluence"),
          constructionVertex(
              "s1",
              "HengSheng.CompanyAccident",
              "companyIndustryType",
              "cyclical",
              "companyType",
              "listedCompany",
              "companyMarketShare",
              0.1,
              "accidentImpactType",
              "productionHalt",
              "subject",
              "c1"),
          constructionVertex("i1", "HengSheng.Industry"),
          constructionVertex("c1", "HengSheng.Company"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionEdge("s1", "subject", "i1"),
          constructionEdge("c1", "belongIndustry", "i1")

          // add belongTo
          ,
          constructionEdge("s1", "belongTo", "周期性行业头部上市公司停产事故"));
    }
  }
}
