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

package com.antgroup.openspg.reasoner.runner.local.group.concept

import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState
import com.antgroup.openspg.reasoner.lube.catalog.Catalog
import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog
import com.antgroup.openspg.reasoner.lube.physical.operators.{ExpandInto, PatternScan, Start}
import com.antgroup.openspg.reasoner.parser.OpenSPGDslParser
import com.antgroup.openspg.reasoner.runner.local.impl.LocalReasonerSession
import com.antgroup.openspg.reasoner.runner.local.rdg.TypeTags
import org.scalatest.BeforeAndAfter
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}


class GroupConceptPlanTest extends AnyFunSpec with BeforeAndAfter {
  it("plan group concept") {
    val dsl =
      """Define (s:User) -[p:belongTo]-> (o:ActivityInfo) {
        |  GraphStructure {
        |    (s) <-[:subject]- (e:InterviewEvent) -[:concept_edge_expand(e, 'activityName', ['参加活动/五福'],
        |'ActivityInfo')]-> (o)
        |  }
        |Rule {
        |    eventDay = from_unix_time(e.eventTime, 'yyyyMMdd')
        |    R1("timeInDay"): from_unix_time(e.eventTime, 'yyyyMMdd') in ['20220101', '20231119']
        |    p.version = cast_type(eventDay, 'long')
        |  }
        |}
        |
        |Define (s:User) -[p:belongTo]-> (o:LoginSource) {
        |  GraphStructure {
        |    (s) <-[:subject]- (e:LoginEvent) -[:concept_edge_expand(e, 'sourceClassification',
        |['登录来源渠道','主动登端','渠道拉动登端','主动登端-账户余额查询'],
        |'LoginSource')]-> (o)
        |  }
        |Rule {
        |    eventDay = from_unix_time(e.eventTime, 'yyyyMMdd')
        |    R1("timeInDay"): from_unix_time(e.eventTime, 'yyyyMMdd') in ['20220101', '20231119']
        |    p.version = cast_type(eventDay, 'long')
        |  }
        |}
        |
        |GraphStructure {
        |  s [User,__start__='true']
        |  o [ActivityInfo]
        |  s -> o [belongTo]  as e1
        |  ls [LoginSource]
        |  s -> ls [belongTo] repeat(0,1) as e2
        |  age [AgeLevel]
        |  s -> age [userAgeLevelstd] repeat(0,1) as e3
        |  job [Occupation]
        |  s -> job [userOccupationstd] repeat(0,1) as e4
        |}
        |Rule {
        |  R1("时间约束"): e2.edges().constraint((pre,cur) => cur.version == e1.version)
        |  countValue = group(o, ls, age, job, e1.version).count(s)
        |}
        |Action {
        |    get(o.id, ls.id, age.id, job.id, e1.version, countValue)
        |}""".stripMargin

    val schema: Map[String, Set[String]] = Map.apply(
      "User" -> Set.apply("id"),
      "User_userOccupationstd_Occupation" -> Set.apply("__to_id__", "__from_id__"),
      "LoginEvent_sourceClassification_LoginSource" -> Set.apply("__to_id__", "__from_id__"),
      "InterviewEvent_activityName_ActivityInfo" -> Set.apply("__to_id__", "__from_id__"),
      "LoginEvent_subject_User" -> Set.apply("__to_id__", "__from_id__"),
      "InterviewEvent_subject_User" -> Set.apply("__to_id__", "__from_id__"),
      "User_userAgeLevelstd_AgeLevel" -> Set.apply("__to_id__", "__from_id__"),
      "LoginSource_isA_LoginSource" -> Set.apply("__to_id__", "__from_id__"),
      "ActivityInfo_isA_ActivityInfo" -> Set.apply("__to_id__", "__from_id__"),
      "AgeLevel" -> Set.apply("id"),
      "LoginSource" -> Set.apply("id"),
      "LoginEvent" -> Set.apply("id", "eventTime"),
      "InterviewEvent" -> Set.apply("id", "eventTime"),
      "Occupation" -> Set.apply("id"),
      "ActivityInfo" -> Set.apply("id")
    )
    val catalog: Catalog = new PropertyGraphCatalog(schema)
    catalog.init()

    val canNotExpandToSet = Set.apply("o", "ls", "age", "job");

    val session = new LocalReasonerSession(new OpenSPGDslParser,
      catalog, TypeTags.rdgTypeTag, new MemGraphState)
    val opList = session.plan(dsl, Map.empty)
    val f = opList.last.transform[Boolean] {
      case (Start(_, alias, _, _), _) =>
        !alias.equals("s")
      case (PatternScan(_, pattern, _), _) =>
        !pattern.root.alias.equals("s")
      case (ExpandInto(_, _, pattern, _), bList) =>
        canNotExpandToSet.contains(pattern.root.alias) || bList.contains(true)
      case (_, bList) => bList.contains(true)
    }
    f should equal(false)
  }

}
