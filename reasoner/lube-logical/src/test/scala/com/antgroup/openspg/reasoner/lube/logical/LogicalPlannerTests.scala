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

package com.antgroup.openspg.reasoner.lube.logical

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog
import com.antgroup.openspg.reasoner.lube.logical.LogicalOperatorOps.RichLogicalOperator
import com.antgroup.openspg.reasoner.lube.logical.operators._
import com.antgroup.openspg.reasoner.lube.logical.optimizer.LogicalOptimizer
import com.antgroup.openspg.reasoner.lube.logical.planning.{LogicalPlanner, LogicalPlannerContext}
import com.antgroup.openspg.reasoner.lube.logical.validate.Validator
import com.antgroup.openspg.reasoner.parser.OpenSPGDslParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class LogicalPlannerTests extends AnyFunSpec {

  it("case1") {
    val dsl =
      """Define (s:User)-[p:belongTo]->(o:`accountQueryCrowd`/`规则1`) {
        |  GraphStructure {
        |    (s)<-[E1:relateCreditCardPaymentBindEvent]-(evt:creditCardPaymentBindEvent)
        |  }
        |  Rule {
        |    R1("银行卡规则"): evt.cardBank in ['PingAnBank', 'CITIC']
        |    R2("是否查询账户"): evt.accountQuery == 'Y'
        |    R3("是否绑定"): evt.bindSelf == 'Y'
        |    BindNum = group(s).sum(evt.cardNum)
        |  	 R4('绑定数目'): BindNum > 0
        |    R5('智信确权'): s.zhixin == 'Y'
        |  }
        |}""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    println(block.pretty)
    val schema: Map[String, Set[String]] = Map.apply(
      "accountQueryCrowd" -> Set.empty,
      "User" -> Set.apply("zhixin"),
      "creditCardPaymentBindEvent" -> Set.apply(
        "cardBank",
        "accountQuery",
        "bindSelf",
        "cardNum"),
      "creditCardPaymentBindEvent_relateCreditCardPaymentBindEvent_User" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    val logicalPlan = LogicalPlanner.plan(block)
    println(logicalPlan.head.pretty)
    val optimizedLogicalPlan = LogicalOptimizer.optimize(logicalPlan.head)
    println(optimizedLogicalPlan.pretty)
  }

  it("case3") {
    val dsl =
      """
        |Define (user:User)-[p:belongto]->(o:`TuringCrowd`/`中等消费`) {
        |	GraphStructure {
        |    (s:TradeEvent)-[pr:relateUser]->(user:User)
        |	}
        |  Rule{
        |    R1('必须是男性'): user.sex  == '男'
        |    R2('交易周期是日和月'): s.statPriod in ['日', '月']
        |    DayliyAmount = group(user).if(s.statPriod=='日').sum(s.amount)
        |    MonthAmount = group(user).if(s.statPriod=='月').sum(s.amount)
        |    R3('日消费额大于300'): DayliyAmount > 300
        |    R4('月消费额小于500'): MonthAmount < 500
        |    R5('召回人群'): (R3 and R1) and (not(R4 and R1))
        |  }
        |}
        |""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    println(block.pretty)
    val schema: Map[String, Set[String]] = Map.apply(
      "TuringCrowd" -> Set.empty,
      "TradeEvent" -> Set.apply("statPriod", "amount"),
      "User" -> Set.apply("sex"),
      "TradeEvent_relateUser_User" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    val logicalPlan = LogicalPlanner.plan(block)
    println(logicalPlan.head.pretty)
    val optimizedLogicalPlan = LogicalOptimizer.optimize(logicalPlan.head)
    println(optimizedLogicalPlan.pretty)
  }

  it("case4") {
    val dsl =
      """
        |GraphStructure {
        |  (s:User)
        |}
        |Rule {
        |  R1('有房'): s.haveHouse  == 'Y'
        |  R2('有车'): s.haveCar == 'Y'
        |  R3('男性'): s.gender == '男'
        |  R4('女性'): s.gender == '女'
        |  R5('颜值高'): s.beautiful > 8
        |  R6('长得高'): (R3 && s.height > 180) || (R4 && s.height > 170)
        |  R7('高富帅'): R1 && R2 && R3 && R5
        |  R8('白富美'): R1 && R2 && R4 && R5
        |  o = rule_value(R7, '高富帅', rule_value(R8, '白富美', '普通人'))
        |}
        |Action {
        |  get(s.id, o as b)
        |}
        |""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    println(block.pretty)
    val schema: Map[String, Set[String]] = Map.apply(
      "User" -> Set.apply("id", "haveHouse", "haveCar", "gender", "beautiful", "height"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    val logicalPlan = LogicalPlanner.plan(block)
    println(logicalPlan.head.pretty)
    val optimizedLogicalPlan = LogicalOptimizer.optimize(logicalPlan.head)
    println(optimizedLogicalPlan.pretty)
  }

  it("dsl2") {
    val dsl = "GraphStructure {\n" +
      "\t(A:Film)-[E1:directFilm]-(B:FilmDirector)\n" +
      "\t(A:Film)-[E2:writerOfFilm]-(C:FilmWriter)\n" +
      "\t(B:FilmDirector)-[E3:workmates]-(C:FilmWriter)\n" +
      "}\n" + "Rule {\n" + "\tR1(\"80后导演\"): B.birthDate > '1980'\n" +
      "\tR2(\"导演编剧同性别\"): B.gender == C.gender\n" + "}\n" +
      "Action {\n" + "\tget(B.name, C.name)\n" + "}"
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    println(block.pretty)
    val schema: Map[String, Set[String]] = Map.apply(
      "Film" -> Set.empty,
      "FilmDirector" -> Set.apply("birthDate", "gender", "name"),
      "FilmWriter" -> Set.apply("gender", "name"),
      "Film_directFilm_FilmDirector" -> Set.empty,
      "Film_writerOfFilm_FilmWriter" -> Set.empty,
      "FilmDirector_workmates_FilmWriter" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    val logicalPlan = LogicalPlanner.plan(block)
    println(logicalPlan.head.pretty)
    val optimizedLogicalPlan = LogicalOptimizer.optimize(logicalPlan.head)
    println(optimizedLogicalPlan.pretty)
  }

  it("test start flag") {
    val dsl =
      """
        |GraphStructure {
        |  A [test]
        |  D [test, __start__='true']
        |  D->A [abc]
        |}
        |Rule {
        |}
        |Action {
        |  get(A.id)
        |}
        |""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] =
      Map.apply("test" -> Set.apply("id"), "test_abc_test" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    val logicalPlan = LogicalPlanner.plan(block).head
    logicalPlan.findExactlyOne { case PatternScan(_, pattern) =>
      pattern.root.alias should equal("D")
    }
  }

  it("test concept used as type") {
    val dsl =
      """
        |GraphStructure {
        |   (u:`TuringCrowd`/`CrowdRelate张三`)
        |}
        |Rule {
        |}
        |Action {
        |    get(u.id)
        |}
        |
        |""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] = Map.apply(
      "TuringCrowd" -> Set.empty,
      "User" -> Set.apply("id"),
      "User_belongTo_TuringCrowd" -> Set.apply(
        Constants.EDGE_FROM_ID_KEY,
        Constants.EDGE_TO_ID_KEY))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    val dag = Validator.validate(List.apply(block))
    val logicalPlan = LogicalPlanner.plan(dag).popRoot()
    logicalPlan.findExactlyOne { case Start(_, _, _, solved) =>
      solved.fields.size should equal(3)
    }
  }

  it("test pattern scan with aggregation") {
    val dsl =
      """
        |GraphStructure {
        |  (s: User)<-[E1:relateCreditCardPaymentBindEvent]-(evt:creditCardPaymentBindEvent)
        |}
        |Rule {
        |  R1("银行卡规则"): evt.cardBank in ['PingAnBank', 'CITIC']
        |  R2("是否查询账户"): evt.accountQuery == 'Y'
        |  R3("是否绑定"): evt.bindSelf == 'Y'
        |  BindNum = group(s).sum(evt.cardNum)
        |	 R4('绑定数目'): BindNum > 0
        |  R5('智信确权'): s.zhixin == 'Y'
        |}
        |Action {
        | get(s.id)
        |}
        |""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] = Map.apply(
      "User" -> Set.apply("zhixin", "id"),
      "creditCardPaymentBindEvent" -> Set.apply(
        "cardBank",
        "accountQuery",
        "bindSelf",
        "cardNum"),
      "creditCardPaymentBindEvent_relateCreditCardPaymentBindEvent_User" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    val logicalPlan = LogicalPlanner.plan(block).head
    val scan = logicalPlan.findExactlyOne { case op: PatternScan =>
      op.pattern.root.alias should equal("s")
    }
  }

  it("anonymous_label") {
    val dsl =
      """GraphStructure {
        |(B:Alipay|Jijin)-[t:consume|transfer]->(C:Alipay|BankCard|Jijin)
        |}
        |Rule {
        |hour = hourOfDay(t.payDate)
        |//R1("凌晨交易"): hour <=5 && hour >=0
        |}
        |Action {
        |  get(B.id, t.payDate, hour)
        |}""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] = Map.apply(
      "Alipay" -> Set.apply("id"),
      "BankCard" -> Set.apply("id"),
      "Jijin" -> Set.apply("id"),
      "Alipay_consume_Alipay" -> Set.apply("payDate"),
      "Alipay_consume_BackCard" -> Set.apply("payDate"),
      "BankCard_consume_Jijin" -> Set.apply("payDate"),
      "Jijin_transfer_BankCard" -> Set.apply("payDate"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    val logicalPlan = LogicalPlanner.plan(block).head
    val scan = logicalPlan.findExactlyOne { case op: PatternScan =>
      op.pattern.root.alias should equal("C")
    }
  }

  it("anonymous_label2") {
    val dsl =
      """GraphStructure {
        |(B)-[t]->(C)
        |}
        |Rule {
        |hour = hourOfDay(t.payDate)
        |//R1("凌晨交易"): hour <=5 && hour >=0
        |}
        |Action {
        |  get(B.id, t.payDate, hour)
        |}""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] = Map.apply(
      "Alipay" -> Set.apply("id"),
      "BankCard" -> Set.apply("id"),
      "Jijin" -> Set.apply("id"),
      "Alipay_consume_Alipay" -> Set.apply("payDate"),
      "Alipay_consume_BackCard" -> Set.apply("payDate"),
      "BankCard_consume_Jijin" -> Set.apply("payDate"),
      "Jijin_transfer_BankCard" -> Set.apply("payDate"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    val logicalPlan = LogicalPlanner.plan(block).head
    print(logicalPlan.pretty)
    val scan = logicalPlan.findExactlyOne { case op: PatternScan =>
      op.pattern.root.alias should equal("C")
    }
  }

  it("test project planning") {
    val dsl =
      """
        |GraphStructure {
        |  (s: AttributePOC.User)-[p: holdPMProduct]->(o: AttributePOC.PortfolioManager),
        |  (s)-[p2:followPM]->(o)
        |}
        |Rule {
        |	c = rule_value(p.avgProfit > 0, 1,0 ) && rule_value(p2.times>3, 1,0)
        |
        |}
        |Action {
        |  get(s.id, p.avgProfit, c)
        |}
        |}""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] = Map.apply(
      "AttributePOC.User" -> Set.apply("id"),
      "AttributePOC.PortfolioManager" -> Set.apply("id"),
      "AttributePOC.User_holdPMProduct_AttributePOC.PortfolioManager" -> Set.apply("avgProfit"),
      "AttributePOC.User_followPM_AttributePOC.PortfolioManager" -> Set.apply("times"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    val logicalPlan = LogicalPlanner.plan(block).head
    print(logicalPlan.pretty)
    val cnt = logicalPlan.transform[Int] {
      case (project: Project, cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(1)
  }

  it("test multi aggregation") {
    val dsl =
      """
        |GraphStructure {
        |  (s: AttributePOC.User)-[p: holdPMProduct]->(o: AttributePOC.PortfolioManager)
        |}
        |Rule {
        |	amt = group(s, o).sum(p.amt)
        |   totalAmt = group(s).sum(amt)
        |
        |}
        |Action {
        |  get(s.id, totalAmt)
        |}
        |}""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] = Map.apply(
      "AttributePOC.User" -> Set.apply("id"),
      "AttributePOC.PortfolioManager" -> Set.apply("id"),
      "AttributePOC.User_holdPMProduct_AttributePOC.PortfolioManager" -> Set.apply("amt"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    val logicalPlan = LogicalPlanner.plan(block).head
    print(logicalPlan.pretty)
    val cnt = logicalPlan.transform[Int] {
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

  it("test transitive") {
    val dsl =
      """
        |GraphStructure {
        |  A [FilmPerson]
        |  C,D [FilmDirector]
        |  A->C [test] as e1
        |  C->D [t1] repeat(2,5) as e2
        |}
        |Rule {
        |}
        |Action {
        |  get(A.name,C.name)
        |}
        |""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    val schema: Map[String, Set[String]] = Map.apply(
      "FilmPerson" -> Set.apply("name"),
      "FilmDirector" -> Set.apply("name"),
      "FilmPerson_test_FilmDirector" -> Set.empty,
      "FilmDirector_t1_FilmDirector" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    val logicalPlan = LogicalPlanner.plan(block).head
    print(logicalPlan.pretty)
    val cnt = logicalPlan.transform[Int] {
      case (agg: BoundedVarLenExpand, cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(5)
  }
}
