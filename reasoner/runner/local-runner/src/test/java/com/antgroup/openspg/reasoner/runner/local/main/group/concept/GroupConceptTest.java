/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.runner.local.main.group.concept;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.runner.local.load.graph.AbstractLocalGraphLoader;
import com.antgroup.openspg.reasoner.runner.local.main.LocalRunnerTestFactory;
import com.antgroup.openspg.reasoner.runner.local.main.LocalRunnerTestFactory.AssertFunction;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author donghai.ydh
 * @version GroupConceptTest.java, v 0.1 2024-01-02 16:07 donghai.ydh
 */
public class GroupConceptTest {
  private AbstractLocalGraphLoader graphLoader;
  private Map<String, Object> params;

  @Before
  public void init() {
    graphLoader =
        new AbstractLocalGraphLoader() {
          @Override
          public List<IVertex<String, IProperty>> genVertexList() {
            return Lists.newArrayList(
                constructionVertex("u1", "User"),
                constructionVertex("e1_1", "InterviewEvent", "eventTime", "1700353034"),
                constructionVertex("e1_2", "InterviewEvent", "eventTime", "1640970061"),
                constructionVertex("e1_3", "InterviewEvent", "eventTime", "1700353024"),
                constructionVertex("le1_1", "LoginEvent", "eventTime", "1700353033"),
                constructionVertex("le1_2", "LoginEvent", "eventTime", "1640970060"),
                constructionVertex("le1_3", "LoginEvent", "eventTime", "1700353023"),
                constructionVertex("le1_4", "LoginEvent", "eventTime", "1700353022"),
                constructionVertex("u2", "User"),
                constructionVertex("e2_1", "InterviewEvent", "eventTime", "1700353025"),
                constructionVertex("e2_2", "InterviewEvent", "eventTime", "1640970060"),
                constructionVertex("le2_1", "LoginEvent", "eventTime", "1700353024"),
                constructionVertex("le2_2", "LoginEvent", "eventTime", "1640970059"),
                constructionVertex("le2_3", "LoginEvent", "eventTime", "1700353023"),
                constructionVertex("参加活动", "ActivityInfo"),
                constructionVertex("参加活动/五福", "ActivityInfo"),
                constructionVertex("参加活动/红包", "ActivityInfo"),
                constructionVertex("登录来源渠道", "LoginSource"),
                constructionVertex("主动登端", "LoginSource"),
                constructionVertex("渠道拉动登端", "LoginSource"),
                constructionVertex("主动登端-账户余额查询", "LoginSource"),
                constructionVertex("老年人", "AgeLevel"),
                constructionVertex("工程师", "Occupation"),
                constructionVertex("教师", "Occupation"));
          }

          @Override
          public List<IEdge<String, IProperty>> genEdgeList() {
            return Lists.newArrayList(
                // hypernym
                constructionEdge("参加活动/五福", "isA", "参加活动"),
                constructionEdge("参加活动/红包", "isA", "参加活动"),
                constructionEdge("主动登端", "isA", "登录来源渠道"),
                constructionEdge("渠道拉动登端", "isA", "登录来源渠道"),
                constructionEdge("主动登端-账户余额查询", "isA", "主动登端"),
                constructionEdge("e1_1", "subject", "u1"),
                constructionEdge("e1_1", "activityName", "参加活动/五福"),
                constructionEdge("e1_2", "subject", "u1"),
                constructionEdge("e1_2", "activityName", "参加活动/五福"),
                constructionEdge("e1_3", "subject", "u1"),
                constructionEdge("e1_3", "activityName", "参加活动/红包"),
                constructionEdge("e2_1", "subject", "u2"),
                constructionEdge("e2_1", "activityName", "参加活动/五福"),
                constructionEdge("e2_2", "subject", "u2"),
                constructionEdge("e2_2", "activityName", "参加活动/五福"),
                constructionEdge("le1_1", "subject", "u1"),
                constructionEdge("le1_1", "sourceClassification", "主动登端-账户余额查询"),
                constructionEdge("le1_2", "subject", "u1"),
                constructionEdge("le1_2", "sourceClassification", "主动登端"),
                constructionEdge("le1_3", "subject", "u1"),
                constructionEdge("le1_3", "sourceClassification", "渠道拉动登端"),
                constructionEdge("le1_4", "subject", "u1"),
                constructionEdge("le1_4", "sourceClassification", "主动登端"),
                constructionEdge("le2_1", "subject", "u2"),
                constructionEdge("le2_1", "sourceClassification", "渠道拉动登端"),
                constructionEdge("le2_2", "subject", "u2"),
                constructionEdge("le2_2", "sourceClassification", "主动登端"),
                constructionEdge("le2_3", "subject", "u2"),
                constructionEdge("le2_3", "sourceClassification", "主动登端"),
                constructionEdge("u1", "userAgeLevelstd", "老年人"),
                constructionEdge("u1", "userOccupationstd", "工程师"),
                constructionEdge("u2", "userAgeLevelstd", "老年人"),
                constructionEdge("u2", "userOccupationstd", "教师"));
          }
        };

    params = new HashMap<>();

    /*
    // start id
    List<List<String>> startIdList = new ArrayList<>();
    startIdList.add(Lists.newArrayList("A1", "Account"));
    params.put(ConfigKey.KG_REASONER_START_ID_LIST, JSON.toJSONString(startIdList));

    // other params
    params.put("startTime", "1");
     */
  }

  @Test
  public void groupTest1() {
    LocalRunnerTestFactory.runTest(
        "Define (s:User) -[p:belongTo]-> (o:ActivityInfo) {\n"
            + "  GraphStructure {\n"
            + "    (s) <-[:subject]- (e:InterviewEvent) -[:concept_edge_expand(e, 'activityName', '参加活动/五福', \n"
            + "'ActivityInfo', [0])]-> (o)\n"
            + "  }\n"
            + "Rule {\n"
            + "    eventDay = from_unix_time(e.eventTime, 'yyyyMMdd')\n"
            + "    R1(\"timeInDay\"): eventDay in ['20220101', '20231119']\n"
            + "    p.version = cast_type(eventDay, 'long')\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "Define (s:User) -[p:belongTo]-> (o:LoginSource) {\n"
            + "  GraphStructure {\n"
            + "    (s) <-[:subject]- (e:LoginEvent) -[:concept_edge_expand(e, 'sourceClassification', '登录来源渠道', \n"
            + "'LoginSource', [0])]-> (o)\n"
            + "  }\n"
            + "Rule {\n"
            + "    eventDay = from_unix_time(e.eventTime, 'yyyyMMdd')\n"
            + "    R1(\"timeInDay\"): eventDay in ['20220101', '20231119']\n"
            + "    p.version = cast_type(eventDay, 'long')\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "  s [User,__start__='true']\n"
            + "  o [ActivityInfo]\n"
            + "  s -> o [belongTo]  as e1\n"
            + "  ls [LoginSource]\n"
            + "  s -> ls [belongTo] repeat(0,1) as e2\n"
            + "  age [AgeLevel]\n"
            + "  s -> age [userAgeLevelstd] repeat(0,1) as e3\n"
            + "  job [Occupation]\n"
            + "  s -> job [userOccupationstd] repeat(0,1) as e4\n"
            + "}\n"
            + "Rule {\n"
            + "  R1(\"时间约束\"): e2.edges().constraint((pre,cur) => cur.version == e1.version)\n"
            + "  countValue = group(o, ls, age, job, e1.version).count(s)\n"
            + "}\n"
            + "Action {\n"
            + "    get(o.id, ls.id, age.id, job.id, e1.version, countValue)\n"
            + "}",
        this.graphLoader,
        new AssertFunction() {
          @Override
          public void assertResult(LocalReasonerResult result) {
            Assert.assertEquals(46, result.getRows().size());
            Map<String, String> rstMap = new HashMap<>();
            for (Object[] strings : result.getRows()) {
              Object[] keys = Arrays.copyOfRange(strings, 0, 4);
              StringBuilder key = new StringBuilder();
              for (Object o : keys) {
                key.append(",").append(o);
              }
              rstMap.put(key.toString(), String.valueOf(strings[4]));
            }
            Assert.assertEquals("2", rstMap.get(",参加活动/五福,null,null,null,20220101"));
          }
        },
        this.params);
  }
}
