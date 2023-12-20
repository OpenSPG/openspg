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

package com.antgroup.openspg.reasoner.lube.logical

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.graph.edge.Direction
import com.antgroup.openspg.reasoner.common.trees.BottomUp
import com.antgroup.openspg.reasoner.common.types.KTString
import com.antgroup.openspg.reasoner.common.utils.ResourceLoader
import com.antgroup.openspg.reasoner.lube.catalog.Catalog
import com.antgroup.openspg.reasoner.lube.catalog.impl.{JSONGraphCatalog, PropertyGraphCatalog}
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.common.expr._
import com.antgroup.openspg.reasoner.lube.common.pattern._
import com.antgroup.openspg.reasoner.lube.common.rule.LogicRule
import com.antgroup.openspg.reasoner.lube.logical.LogicalOperatorOps.RichLogicalOperator
import com.antgroup.openspg.reasoner.lube.logical.operators.{Filter, _}
import com.antgroup.openspg.reasoner.lube.logical.optimizer.LogicalOptimizer
import com.antgroup.openspg.reasoner.lube.logical.optimizer.rules._
import com.antgroup.openspg.reasoner.lube.logical.planning.{LogicalPlanner, LogicalPlannerContext}
import com.antgroup.openspg.reasoner.lube.logical.validate.Validator
import com.antgroup.openspg.reasoner.lube.utils.transformer.impl.Expr2QlexpressTransformer
import com.antgroup.openspg.reasoner.parser.KgDslParser
import com.antgroup.openspg.reasoner.parser.expr.RuleExprParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class OptimizerTests extends AnyFunSpec {
  it("testFilterPushDown") {
    val catalog = new EmptyCatalog()
    catalog.init()
    val start = Start(
      catalog.getGraph(Catalog.defaultGraphName),
      null,
      Set.empty,
      SolvedModel(
        Map.empty,
        Map.apply(("B", NodeVar("B", Set.apply(new Field("birthDate", KTString, true))))),
        Map.empty))
    val patternScan =
      PatternScan(start, NodePattern(PatternElement("B", Set.apply("FilmDirector"), null)))
    val ruleParser = new RuleExprParser()
    val filter =
      operators.Filter(
        patternScan,
        LogicRule("R1", "", ruleParser.parse("B.birthDate > \"1980\"")))
    val finalOp = BottomUp[LogicalOperator](FilterPushDown.rule(null)).transform(filter)
    finalOp.findExactlyOne { case PatternScan(_, pattern) =>
      val qlTransformer = new Expr2QlexpressTransformer()
      qlTransformer.transform(pattern.getNode("B").rule).head should equal(
        "B.birthDate > \"1980\"")
    }
  }

  it("testEdgeToProperty") {
    val schema = ResourceLoader.loadResourceFile("TuringSchema.json")
    val catalog = new JSONGraphCatalog(schema)
    val dsl =
      """
        |Define (user:TuringCore.AlipayUser)-[bt:belongTo]->(tc:`TuringCrowd`/`通勤用户`) {
        |	GraphStructure {
        |		(user) -[pwl:workLoc]-> (aa1:CKG.AdministrativeArea)
        |		(te:TuringCore.TravelEvent) -[ptler:traveler]-> (user)
        |		(te) -[ptm:travelMode]-> (tm:TuringCore.TravelMode)
        |		(te) -[pte:travelEndpoint]-> (aa1:CKG.AdministrativeArea)
        |	}
        |  Rule {
        |    R1('常驻地在杭州'): aa1.id == '中国-浙江省-杭州市'
        |  	R2('工作日上班时间通勤用户'): dayOfWeek(te.eventTime) in [1, 2, 3, 4, 5]
        |            and hourOfDay(te.eventTime) in [6, 7, 8, 9, 10, 17, 18, 19, 20, 21]
        |    R3('公交地铁'): tm.id in ['bus', 'subway']
        |    tmCount('出行次数') = group(user).count(te.id)
        |    R4('出行次数大于3次'): tmCount >= 3
        |  }
        |}
        |""".stripMargin
    catalog.init()
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    val logicalPlan = LogicalPlanner.plan(block).head
    val finalOp = LogicalOptimizer.optimize(
      logicalPlan,
      Seq.apply(FilterPushDown, EdgeToProperty, SolvedModelPure))
    val qlTransformer = new Expr2QlexpressTransformer()
    finalOp.findExactlyOne { case ExpandInto(_, _, pattern) =>
      qlTransformer.transform(pattern.getNode("te").rule).head should equal(
        "((te.travelMode in [\"bus\",\"subway\"]) && ((dayOfWeek(te.eventTime) in [1,2,3,4,5]) && (hourOfDay(te.eventTime) in [6,7,8,9,10,17,18,19,20,21]))) && (te.travelEndpoint == \"中国-浙江省-杭州市\")")
    }
    finalOp.findExactlyOne { case PatternScan(_, pattern) =>
      qlTransformer.transform(pattern.getNode("user").rule).head should equal(
        "user.workLoc == \"中国-浙江省-杭州市\"")
    }
    finalOp.findExactlyOne { case Start(_, _, _, solved) =>
      solved.alias2Types.keys.toSet should equal(Set.apply("user", "te", "ptler"))
      solved.fields("user").asInstanceOf[NodeVar].fields.map(_.name) should equal(
        Set.apply("workLoc"))
      solved.fields("te").asInstanceOf[NodeVar].fields.map(_.name) should equal(
        Set.apply("eventTime", "id", "travelMode", "travelEndpoint"))
    }
  }

  it("concept to property") {
    val schema = ResourceLoader.loadResourceFile("TuringSchema.json")
    val catalog = new JSONGraphCatalog(schema)
    catalog.init()
    val start = Start(
      catalog.getGraph(Catalog.defaultGraphName),
      null,
      Set.empty,
      SolvedModel(
        Map.empty,
        Map.apply(
          ("te", NodeVar("te", Set.apply(new Field("eventTime", KTString, true)))),
          ("user", NodeVar("user", Set.apply())),
          ("tm", NodeVar("tm", Set.apply(new Field("id", KTString, true))))),
        Map.empty))
    val r1 = LogicRule(
      "R1",
      "xx",
      BinaryOpExpr(BEqual, UnaryOpExpr(GetField("eventTime"), Ref("te")), VString("1")))
    val r2 = LogicRule(
      "R2",
      "xx",
      BinaryOpExpr(BEqual, UnaryOpExpr(GetField("id"), Ref("tm")), VString("bus")))
    val patternElementMap = Map.apply(
      ("te", PatternElement("te", Set.apply("TuringCore.TravelEvent"), r1)),
      ("tm", PatternElement("tm", Set.apply("TuringCore.TravelMode"), r2)))
    val edges: Map[String, Set[Connection]] = Map.apply((
      "te",
      Set.apply(
        new PatternConnection("ptm", "te", Set.apply("travelMode"), "tm", Direction.OUT, null))))
    val expand = ExpandInto(
      start,
      patternElementMap("te"),
      PartialGraphPattern("te", patternElementMap, edges))
    val logicalOp =
      ExpandInto(expand, patternElementMap("tm"), NodePattern(patternElementMap("tm")))
    val finalOp = BottomUp[LogicalOperator](EdgeToProperty.rule(null)).transform(logicalOp)

    val qlTransformer = new Expr2QlexpressTransformer()
    finalOp.findExactlyOne { case ExpandInto(_, _, pattern) =>
      pattern.root.alias should equal("te")
      qlTransformer.transform(pattern.getNode("te").rule).head should equal(
        "(te.travelMode == \"bus\") && (te.eventTime == \"1\")")
    }
  }

  it("expandInto pure") {
    val dsl =
      """
        |GraphStructure {
        |     s [CustFundKG.Account, __start__='true']
        |      inUser,inUser2,outUser [CustFundKG.Account]
        |      inUser -> s[accountFundContact] as in1
        |      inUser2 -> s[accountFundContact] as in2
        |      s -> outUser [accountFundContact] as out
        |  }
        |Rule {
        |    R1("当天同时转入"):date_diff(in1.transDate,in2.transDate) == 0
        |    R2("转出金额必须大于100"): out.sumAmt > 100
        |    R3("必须是一天以内转出"): date_diff(out.transDate,in2.transDate) == 1
        |    tranOutNum = group(s).count(outUser.id)
        |    o = rule_value(tranOutNum >=5, true, false)
        |  }
        |Action {
        |  get(s.id, o)
        |}
        |""".stripMargin

    val schema: Map[String, Set[String]] = Map.apply(
      "CustFundKG.Account" -> Set.apply("id"),
      "CustFundKG.Account_accountFundContact_CustFundKG.Account" -> Set.apply(
        "transDate",
        "sumAmt"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    val logicalPlan = LogicalPlanner.plan(block).head
    val rules = Seq.apply(FilterPushDown, ExpandIntoPure)
    val finalOp = LogicalOptimizer.optimize(logicalPlan, rules)
    val cnt = finalOp.transform[Int] {
      case (scan: PatternScan, cnt) => cnt.sum + 1
      case (expand: ExpandInto, cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(2)
  }

  it("test filter merge") {
    val dsl =
      """
        |GraphStructure {
        |  (u:User)-[p:lk]->(o:User)
        |}
        |Rule {
        |  R1("年龄"): u.age > 18 && o.age > 18
        |  R2("身高"): u.height > 170 && u.height < 190 && o.height < 180
        |  R3("体重"): u.weight > 50.0 && p.weight > 1
        |}
        |Action {
        |  get(u.id as uid, u.name as uname, o.id as oid)
        |}
        |""".stripMargin
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] =
      Map.apply(
        "User" -> Set.apply("id", "name", "age", "height", "weight"),
        "User_lk_User" -> Set.apply("weight"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    val dag = Validator.validate(List.apply(block))
    val logicalPlan = LogicalPlanner.plan(dag).popRoot()
    println(logicalPlan.pretty)
    val optimizedLogicalPlan =
      LogicalOptimizer.optimize(logicalPlan, Seq.apply(FilterPushDown, FilterMerge))
    println(optimizedLogicalPlan.pretty)

    optimizedLogicalPlan.findExactlyOne { case filter @ Filter(in, _) =>
      in.isInstanceOf[Filter] should equal(false)
      val t = new Expr2QlexpressTransformer()
      val ruleString = t.transform(filter.rule)
      println(ruleString)
    }
  }

  it("test aggregation push down") {
    val dsl =
      """
        |GraphStructure {
        |  (A:User)-[e1:lk]->(B:User)-[e2:lk]->(C:User)
        |}
        |Rule {
        |  R1(""): e1.weight < e2.weight
        |  R2(""): C.height > 170
        |  countB = group(A).count(B)
        |  countC = group(A).count(C)
        |}
        |Action {
        |  get(A.id, countB, countC)
        |}
        |""".stripMargin
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] =
      Map.apply(
        "User" -> Set.apply("id", "name", "age", "height", "weight"),
        "User_lk_User" -> Set.apply("weight"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(
        catalog,
        parser,
        Map
          .apply((Constants.SPG_REASONER_MULTI_VERSION_ENABLE, true))
          .asInstanceOf[Map[String, Object]])
    val dag = Validator.validate(List.apply(block))
    val logicalPlan = LogicalPlanner.plan(dag).popRoot()
    println(logicalPlan.pretty)
    val rule =
      Seq(
        FilterPushDown,
        ExpandIntoPure,
        FilterMerge,
        SolvedModelPure,
        DistinctGet,
        AggregatePushDown)
    val optimizedLogicalPlan =
      LogicalOptimizer.optimize(logicalPlan, rule)
    println(optimizedLogicalPlan.pretty)

    val cnt = optimizedLogicalPlan.transform[Int] {
      case (agg: Aggregate, cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(2)
  }

}
