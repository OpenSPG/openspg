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

import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog
import com.antgroup.openspg.reasoner.lube.logical.LogicalOperatorOps.RichLogicalOperator
import com.antgroup.openspg.reasoner.lube.logical.operators.{Aggregate, BoundedVarLenExpand, PatternScan, Project, Start}
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

  it("online") {
    val dsl = """Define (s:User)-[p:redPacket]->(o:Int) {
                |	GraphStructure {
                | (s)
                | }
                |  Rule {
                |LatestHighFrequencyMonthPayCount=s.ngfe_tag__pay_cnt_m
                |Latest30DayPayCount=s.ngfe_tag__pay_cnt_d
                |Latest7DayPayCount=s.ngfe_tag__pay_cnt_d
                |LatestTTT = Latest7DayPayCount.accumulate(+)
                |LatestHighFrequencyMonthAveragePayCount=get_first_notnull(maximum(LatestHighFrequencyMonthPayCount), 0.0) / 30.0
                |Latest7DayPayCountSum=Latest7DayPayCount
                |Latest7DayPayCountAverage=Latest7DayPayCountSum / 7.0
                |HighReduceValue=(LatestHighFrequencyMonthAveragePayCount - Latest7DayPayCountAverage)/LatestHighFrequencyMonthAveragePayCount
                |HighLost("高频降频100%"):HighReduceValue == 1
                |HighReduce80("高频降频80%"):HighReduceValue >= 0.8 and HighReduceValue < 1
                |HighReduce50("高频降频50%"):HighReduceValue >= 0.5 and HighReduceValue < 0.8
                |HighReduce30("高频降频30%"):HighReduceValue >= 0.3 and HighReduceValue < 0.5
                |HighReduce10("高频降频10%"):HighReduceValue >= 0.1 and HighReduceValue < 0.3
                |Latest3060DayPayCount=s.ngfe_tag__pay_cnt_d
                |Latest30DayPayDayCount=size(Latest30DayPayCount)
                |Latest3060DayPayDayCount=size(Latest3060DayPayCount)
                |High1("高频用户1"):Latest3060DayPayDayCount < 13 and Latest30DayPayDayCount >= 13
                |High2("高频用户2"):Latest3060DayPayDayCount > 12 and Latest30DayPayDayCount >= 13
                |Middle1("中频用户1"):Latest3060DayPayDayCount == 0 and Latest30DayPayDayCount >= 4 and Latest30DayPayDayCount <= 12
                |Middle2("中频用户2"):Latest3060DayPayDayCount >= 1 and Latest3060DayPayDayCount <= 3 and Latest30DayPayDayCount >= 4 and Latest30DayPayDayCount <= 12
                |Middle3("中频用户3"):Latest3060DayPayDayCount >= 4 and Latest30DayPayDayCount >= 4 and Latest30DayPayDayCount <= 12
                |Low1("低频用户1"):Latest3060DayPayDayCount >= 1 and Latest3060DayPayDayCount <= 3 and Latest30DayPayDayCount >= 1 and Latest30DayPayDayCount <= 3
                |Low2("低频用户2"):(Latest3060DayPayDayCount > 3 or Latest3060DayPayDayCount == 0) and Latest30DayPayDayCount >= 1 and Latest30DayPayDayCount <= 3
                |Latest6090DayPayCount=s.ngfe_tag__pay_cnt_d
                |Latest6090DayPayDayCount=size(Latest6090DayPayCount)
                |Latest60DayPayCount=s.ngfe_tag__pay_cnt_d
                |Latest60DayPayDayCount=size(Latest60DayPayCount)
                |Sleep1("沉睡用户1"):Latest6090DayPayDayCount > 0 and Latest60DayPayDayCount == 0
                |Sleep2("沉睡用户2"):Latest3060DayPayDayCount > 0 and Latest30DayPayDayCount == 0
                |HistoricallyPay=s.ngfe_tag__pay_cnt_total
                |HistoricallyPayCount=size(HistoricallyPay)
                |New("新用户"):HistoricallyPayCount == 0 and Latest30DayPayDayCount == 0
                |Latest90DayPayCount=s.ngfe_tag__pay_cnt_d
                |Latest90DayPayDayCount=size(Latest90DayPayCount)
                |Lost("流失用户"):HistoricallyPayCount > 0 and Latest90DayPayDayCount == 0
                |o=get_first_notnull(rule_value(HighLost, "high_lost"), rule_value(HighReduce80, "high_reduce_80"),rule_value(HighReduce50, "high_reduce_50"), rule_value(HighReduce30, "high_reduce_30"), rule_value(HighReduce10, "high_reduce_10"), rule_value(High1, "high_1"), rule_value(High2, "high_2"), rule_value(Middle1, "middle_1"), rule_value(Middle2, "middle_2"), rule_value(Middle3, "middle_3"), rule_value(Low1, "low_1"), rule_value(Low2, "low_2"), rule_value(Sleep1, "sleep_1"), rule_value(Sleep2, "sleep_2"), rule_value(New, "new"), rule_value(Lost, "lost"))
                |  }
                |}""".stripMargin
    val parser = new OpenSPGDslParser()
    val block = parser.parse(dsl)
    println(block.pretty)
    val schema: Map[String, Set[String]] = Map.apply(
      "User" -> Set
        .apply("ngfe_tag__pay_cnt_m", "ngfe_tag__pay_cnt_total", "ngfe_tag__pay_cnt_d"))
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
      "User_belongTo_TuringCrowd" -> Set.empty)
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
        |	c = rule_value(p.avgProfit > 0, 1,0 ) + rule_value(p2.times>3, 1,0)
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
