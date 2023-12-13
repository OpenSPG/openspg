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

package com.antgroup.reasoner.session

import com.antgroup.openspg.reasoner.catalog.impl.{KGCatalog, KgSchemaConnectionInfo}
import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.exception.SchemaException
import com.antgroup.openspg.reasoner.common.utils.ResourceLoader
import com.antgroup.openspg.reasoner.lube.catalog.impl.{JSONGraphCatalog, PropertyGraphCatalog}
import com.antgroup.openspg.reasoner.lube.common.expr.{AggIfOpExpr, BAnd, BinaryOpExpr}
import com.antgroup.openspg.reasoner.lube.common.graph.IRGraph
import com.antgroup.openspg.reasoner.lube.logical.optimizer.LogicalOptimizer
import com.antgroup.openspg.reasoner.lube.logical.planning.{LogicalPlanner, LogicalPlannerContext}
import com.antgroup.openspg.reasoner.lube.physical.operators._
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG
import com.antgroup.openspg.reasoner.parser.KgDslParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{contain, convertToAnyShouldWrapper, equal}

import scala.collection.mutable.ListBuffer
import com.antgroup.openspg.reasoner.rule.RuleRunner
import com.antgroup.openspg.reasoner.util.LoaderUtil

class ReasonerSessionTests extends AnyFunSpec {

  def getKgSchemaConnectionInfo(): KgSchemaConnectionInfo = {
    KgSchemaConnectionInfo("http://kgengine-1.gz00b.stable.alipay.net", "a8bB6398B6Da9170")
  }

  it("test rule reference") {
    val dsl =
      """
        |Define (user:TuringCore.AlipayUser)-[teCount:teCount]->(o:Long) {
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
        |    R5('id不为空'): user.id != ''
        |    o = tmCount
        |  }
        |}
        |
        |GraphStructure {
        |  (user:TuringCore.AlipayUser)
        |}
        |Rule{
        |}
        |Action{
        |  get(user.teCount)
        |}
        |""".stripMargin
    val schema = ResourceLoader.loadResourceFile("TuringSchema.json")
    val catalog = new JSONGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val rst = session.plan(dsl, Map.empty)
    rst.foreach(session.getResult(_))
    val node = catalog.getGraph(IRGraph.defaultGraphName).getNode("TuringCore.AlipayUser")
    node.properties.map(_.name) should contain("teCount")
  }

  // scalastyle:off
  it("dependency_analysis") {
    val dsl =
      """
        |GraphStructure {
        |       (s:Park)-[e:nearby(s.boundary, o.center, 10.1)]->(o:Subway)
        |   }
        |   Rule{}
        |   Action {get(s.boundary)}
        |""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "Park" -> Set.apply("boundary"),
      "Subway" -> Set.apply("center"),
      "STD.S2CellId" -> Set.empty,
      "Subway_centerS2CellId_STD.S2CellId" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val physicalOperatorList = session.plan(dsl, Map.empty)
    val definePhysicalOp = physicalOperatorList.head
    val expandIntoCount = definePhysicalOp.transform[Int] {
      case (LinkedExpand(in, pattern, meta), count) =>
        in.isInstanceOf[PatternScan[EmptyRDG]] should equal(true)
        count.head
      case (ExpandInto(_, _, _, _), count) =>
        count.head + 1
      case (_, count) =>
        if (count.isEmpty) {
          0
        } else {
          count.head
        }
    }
    expandIntoCount should equal(2)
  }

  it("dependency_analysis_subquery") {
    val dsl =
      """
        |GraphStructure {
        |       (s:Park)-[e:nearby(s.boundary, o.center, 10.1)]->(o:Subway)
        |   }
        |   Rule{}
        |   Action {get(s.boundary, o.center)}
        |""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "Park" -> Set.apply("boundary"),
      "Subway" -> Set.apply("center"),
      "STD.S2CellId" -> Set.empty,
      "Subway_centerS2CellId_STD.S2CellId" -> Set.empty,
      "Park_boundaryS2CellId_STD.S2CellId" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val physicalOperatorList = session.plan(
      dsl,
      Map
        .apply(
          Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE -> true,
          Constants.START_LABEL -> "Park",
          "1" -> "1")
        .asInstanceOf[Map[String, Object]])
    physicalOperatorList.size should equal(1)
    var subqueryPhysicalOp = physicalOperatorList.head
    val forwardDirectionPhysicalOpOrder = getPhysicalPlanOrder(subqueryPhysicalOp)

    val reversePhysicalOperatorList = session.plan(
      dsl,
      Map
        .apply(
          Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE -> true,
          Constants.START_LABEL -> "Subway",
          Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE -> true,
          "1" -> "1")
        .asInstanceOf[Map[String, Object]])
    subqueryPhysicalOp = reversePhysicalOperatorList.head
    val reverseDirectionPhysicalOpOrder = getPhysicalPlanOrder(subqueryPhysicalOp)
    forwardDirectionPhysicalOpOrder.equals(reverseDirectionPhysicalOpOrder) should equal(true)
    println(forwardDirectionPhysicalOpOrder)
    forwardDirectionPhysicalOpOrder should equal("Start,Cache,DrivingRDG,PatternScan,LinkedExpand,ExpandInto,ExpandInto,Drop,Filter,Drop,DDL,Join,PatternScan,ExpandInto,Drop,Select")
  }

  it("multiple_st_edge_test") {
    val dsl: String =
      """
        |GraphStructure{
        |	(poi1: PE.AntPOI)-[e1:within(poi1.location, aoi1.coords)]->(aoi1:PE.AntAOI)<-[e2:within(poi2.location, aoi1.coords)]-(poi2:PE.AntPOI)
        |}
        |Rule {
        |	R1: poi1.id != poi2.id
        |}
        |Action {
        |	get(poi1.id, aoi1.id, poi2.id)
        |}""".stripMargin

    val schema: Map[String, Set[String]] = Map.apply(
      "PE.AntPOI" -> Set.apply("id", "location"),
      "PE.AntAOI" -> Set.apply("id", "coords"),
      "STD.S2CellId" -> Set.empty,
      "PE.AntAOI_coordsS2CellId_STD.S2CellId" -> Set.empty,
      "PE.AntPOI_locationS2CellId_STD.S2CellId" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val physicalOperatorList = session.plan(
      dsl,
      Map
        .apply(
          Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE -> true,
          Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE -> true,
          Constants.START_LABEL -> "PE.AntPOI",
          "1" -> "1")
        .asInstanceOf[Map[String, Object]])
    physicalOperatorList.size should equal(1)
    var subqueryPhysicalOp = physicalOperatorList.head
    val physicalOpOrder = getPhysicalPlanOrder(subqueryPhysicalOp)
    println(physicalOpOrder)
    physicalOpOrder should equal("Start,Cache,DrivingRDG,PatternScan,LinkedExpand,ExpandInto,ExpandInto,Drop,Filter,Drop,DDL,Join,PatternScan,Cache,DrivingRDG,PatternScan,LinkedExpand,ExpandInto,ExpandInto,Drop,Filter,Drop,DDL,Join,ExpandInto,ExpandInto,Drop,Filter,Drop,Select")
  }

  private def getPhysicalPlanOrder[T <: RDG[T]](physicalOperator: PhysicalOperator[T]) = {
    val opNameList: ListBuffer[String] = ListBuffer.empty
    physicalOperator.transform[Unit] {
      case (addInto: AddInto[T], _) =>
        opNameList += "AddInto"
      case (agg: Aggregate[T], _) =>
        opNameList += "Aggregate"
      case (cache: Cache[T], _) =>
        opNameList += "Cache"
      case (ddl: DDL[T], _) =>
        opNameList += "DDL"
      case (drivingRDG: DrivingRDG[T], _) =>
        opNameList += "DrivingRDG"
      case (drop: Drop[T], _) =>
        opNameList += "Drop"
      case (expandInto: ExpandInto[T], _) =>
        opNameList += "ExpandInto"
      case (filter: Filter[T], _) =>
        opNameList += "Filter"
      case (join: Join[T], _) =>
        opNameList += "Join"
      case (linkedExpand: LinkedExpand[T], _) =>
        opNameList += "LinkedExpand"
      case (orderBy: OrderBy[T], _) =>
        opNameList += "OrderBy"
      case (scan: PatternScan[T], _) =>
        opNameList += "PatternScan"
      case (select: Select[T], _) =>
        opNameList += "Select"
      case (start: Start[T], _) =>
        opNameList += "Start"
      case (_, _) =>
    }
    opNameList.mkString(",")
  }

  it("testBehindLinkedEdge") {
    val dsl =
      """
        |GraphStructure {
        | (p:Park)-[e1:SameCity]->(b:BusinessArea)-[e2:nearby(b.center, s.center, 5)]->(s:Subway)
        |}
        |Rule{R1(""): e1.city = 'Beijing'}
        |Action{get(s.center)}
        |""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "Park" -> Set.empty,
      "BusinessArea" -> Set.apply("center"),
      "Subway" -> Set.apply("center"),
      "STD.S2CellId" -> Set.empty,
      "Subway_centerS2CellId_STD.S2CellId" -> Set.empty,
      "Park_SameCity_BusinessArea" -> Set.apply("city"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val physicalOperatorList = session.plan(dsl, Map.empty)
    physicalOperatorList.size should equal(2)
    val definePhysicalPlan = physicalOperatorList.head
    val expandIntoCount = definePhysicalPlan.transform[Int] {
      case (LinkedExpand(in, pattern, meta), count) =>
        in.isInstanceOf[PatternScan[EmptyRDG]] should equal(true)
        count.head
      case (ExpandInto(_, _, _, _), count) =>
        count.head + 1
      case (_, count) =>
        if (count.isEmpty) {
          0
        } else {
          count.head
        }
    }
    expandIntoCount should equal(2)

    val originBlock = physicalOperatorList(1)
    originBlock.isInstanceOf[Select[EmptyRDG]] should equal(true)
    originBlock.transform[Unit] {
      case (PatternScan(_, pattern, _), _) =>
        pattern.topology("b").size should equal(2)
      case (LinkedExpand(_, _, _), _) =>
        true should equal(false)
      case _ =>
    }
  }

  // scalastyle:on

  it("test add predicate") {
    val dsl =
      """
        |Define (s:User)-[p:tradeInfo]->(u:User) {
        |    GraphStructure {
        |        (s)-[t:trade]->(u:User)
        |    } Rule {
        |        R1("交易时间在90天内"): t.trade_time < -90
        |        trade_num = group(s,u).count(t)
        |        p.trade_num = trade_num
        |    }
        |}
        |
        |GraphStructure {
        |   (s:User)-[t:tradeInfo]->(u:User)
        |}
        |Rule {
        |   R1: t.trade_num > 100
        |}
        |Action {
        |    get(s.id)
        |}
        |
        |""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "User" -> Set.apply("id"),
      "User_trade_User" -> Set.apply("trade_num", "trade_time"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val rst = session.plan(dsl, Map.empty)
    rst.foreach(session.getResult(_))
    val edge = catalog.getGraph(IRGraph.defaultGraphName).getEdge("User_tradeInfo_User")
    edge.properties.map(_.name) should contain("trade_num")
  }

  it("test filter push down to loader config") {
    val dsl =
      """
        |GraphStructure {
        |    (s: User)-[t:trade]->(u:User)
        |}
        |Rule {
        |    R1("交易时间在90天内"): t.trade_time < -90
        |    trade_num = group(s,u).count(t)
        |    p.trade_num = trade_num
        |}
        |Action {
        | get(s.id)
        |}
        |""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "User" -> Set.apply("id"),
      "User_trade_User" -> Set.apply("trade_num", "trade_time"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    implicit val context: LogicalPlannerContext =
      LogicalPlannerContext(catalog, parser, Map.empty)
    val logicalOp = LogicalPlanner.plan(block).head
    val op = LogicalOptimizer.optimize(logicalOp)
    val loaderConfig = LoaderUtil.getLoaderConfig(List.apply(op), catalog)
    loaderConfig
      .getEdgeLoaderConfigs()
      .iterator()
      .next()
      .getPropertiesFilterRules
      .size() should equal(1)
  }

  it("test sub-query") {
    val dsl =
      """
        |Define (s:DomainFamily)-[p:blackRelateRate]->(o:Pkg) {
        |    GraphStructure {
        |        (o)-[:use]->(d:Domain)-[:belong]->(s)
        |    }
        |    Rule {
        |        R1: o.is_black == true
        |        domain_num = group(s,o).count(d.id)
        |        p.same_domain_num = domain_num
        |    }
        |}
        |
        |Define (s:DomainFamily)-[p:totalDomainNum]->(o:Int) {
        |    GraphStructure {
        |        (s)<-[:belong]-(d:Domain)
        |    }
        |    Rule {
        |        o = group(s).count(d.id)
        |    }
        |}
        |
        |Define (s:Pkg)-[p:target]->(o:User) {
        |    GraphStructure {
        |        (s)<-[p1:blackRelateRate]-(df:DomainFamily),
        |        (df)<-[:belong]-(d:Domain),
        |        (o)-[:visit]->(d)
        |    } Rule {
        |        visit_time = group(o, df).count(d.id)
        |        R1("必须大于2次"): visit_time > 1
        |        R2("必须占比大于50%"): visit_time / df.totalDomainNum > 0.5
        |    }
        |}
        |
        |GraphStructure {
        |    (s:Pkg)-[p:target]->(o:User)
        |}
        |Rule {
        |
        |}
        |Action {
        |    get(s.id,o.id)
        |}
        |""".stripMargin
    val parser = new KgDslParser()
    val schema: Map[String, Set[String]] = Map.apply(
      "Pkg" -> Set.apply("id", "is_black"),
      "User" -> Set.apply("id"),
      "Domain" -> Set.apply("id"),
      "DomainFamily" -> Set.apply("totalDomainNum"),
      "Pkg_use_Domain" -> Set.empty,
      "Domain_belong_DomainFamily" -> Set.empty,
      "User_visit_Domain" -> Set.empty,
      "DomainFamily_belong_Domain" -> Set.empty,
      "Pkg_blackRelateRate_DomainFamily" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (join: Join[EmptyRDG], cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(3)
  }

  it("test sub query add property with catalog") {
    val dsl =
      """
        |GraphStructure {
        |    (s:OpenSource.Person)
        |}
        |Rule {
        |
        |}
        |Action {
        |    get(s.installGamblingAppNum)
        |}
        |""".stripMargin
    val catalog = new KGCatalog(638000139L, getKgSchemaConnectionInfo())
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (join: Join[EmptyRDG], cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(2)
  }

  it("test for filter") {
    val dsl = """Define (s:Company)-[p:mainSupply]->(o:Company) {
                |  GraphStructure{
                |  	(s)-[:product]->(upProd:Product)-[:hasSupplyChain]->(downProd:Product)<-[:product]-(o),
                |  	(o)-[f:fundTrans]->(s)
                |  	(otherCompany:Company)-[otherf:fundTrans]->(s)
                |  }
                |	Rule {
                |  	// 计算公司o的转入占比
                |  	targetTransSum("o转入的金额总数") = group(s,o).sum(f.transAmt)
                |  	otherTransSum("总共转入金额") = group(s).sum(otherf.transAmt)
                |
                |  	transRate = targetTransSum*1.0/(otherTransSum + targetTransSum)
                |  	//R1("占比必须超过50%"): targetTransSum*1.0/(otherTransSum + targetTransSum) > 0.5
                |   R1("占比必须超过50%"): transRate > 0.5
                |  }
                |}
                |
                |GraphStructure {
                | (s:Company)-[p:mainSupply]->(o:Company)
                |}
                |Rule {
                |
                |}
                |Action {
                | get(s.id, o.id)
                |}
                |""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "Company" -> Set.apply("id"),
      "Product" -> Set.apply("id"),
      "Product_hasSupplyChain_Product" -> Set.apply("id"),
      "Company_product_Product" -> Set.apply("id"),
      "Company_fundTrans_Company" -> Set.apply("id", "transAmt"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()

    val session = new EmptySession(new KgDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply(
          (Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true),
          (Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (join: Join[EmptyRDG], cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(1)
  }

  it("test sub query parse cost benchmark 1") {
    val dsl =
      """GraphStructure {
        |(A:ABM.User)-[:expand_linked_alipay_id(A.id)]->(B:CustFundKG.Alipay|CustFundKG.BankCard|CustFundKG.Default|CustFundKG.Huabei|CustFundKG.Jiebei|CustFundKG.MyBank|CustFundKG.Other|CustFundKG.Yeb|CustFundKG.YIB)-[transOut:transfer|unknown|fundRedeem|fundPurchase|dingqiPurchase|taxRefund|transfer|consume|ylbPurchase|jbRepay|hbRepay|creditCardRepay|withdraw|gkhRepay|yebPurchase|corpCreditRepay|deposit|merchantSettle|ylbRedeem|dingqiRedeem|jbLoan|corpCreditLoan|sceneLoan]->(C), (B)<-[transIn:transfer|unknown|fundRedeem|fundPurchase|dingqiPurchase|taxRefund|transfer|consume|ylbPurchase|jbRepay|hbRepay|creditCardRepay|withdraw|gkhRepay|yebPurchase|corpCreditRepay|deposit|merchantSettle|ylbRedeem|dingqiRedeem|jbLoan|corpCreditLoan|sceneLoan]-(D)
        |}
        |Rule {
        |inSum = group(A,B).sum(transIn.amount)
        |outSum = group(A,B).sum(transOut.amount)
        |} Action
        |{
        |get(A.id, A.name, A.age, A.gender, B.containerType, inSum, outSum)
        |}""".stripMargin
    val catalog = new KGCatalog(308000003L, getKgSchemaConnectionInfo)
    catalog.init()

    val startTime = System.currentTimeMillis()
    val session = new EmptySession(new KgDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (join: Join[EmptyRDG], cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    val costMs = System.currentTimeMillis() - startTime
    // scalastyle:off println
    println(costMs)
  }
  it("test sub query parse cost benchmark") {
    val dsl =
      """
        |//1 先定义流入对手
        |Define (s:CustFundKG.Account)-[p:in_trans_user_num]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)-[t:accountFundContact]->(s)
        |    }
        |	Rule {
        |        o = group(s).count(u)
        |    }
        |}
        |
        |//2 先定义流出对手
        |Define (s:CustFundKG.Account)-[p:out_trans_user_num]->(o:Int) {
        |    GraphStructure {
        |        (s)-[t:accountFundContact]->(u:CustFundKG.Account)
        |    }
        |	Rule {
        |        o = group(s).count(u)
        |    }
        |}
        |
        |//3 定义交易对手众多
        |Define (s:CustFundKG.Account)-[p:too_many_trans_user_num]->(o:Boolean) {
        |    GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |        R1("流入或流出对手超过120名"): s.out_trans_user_num > 120 || s.in_trans_user_num > 120
        |        o = rule_value(R1, true, false)
        |    }
        |}
        |
        |
        |//1 先定义转出对手数目
        |Define (s:CustFundKG.Account)-[p:total_out_trans_user_num]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)<-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |        R1("1月内交易"): date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate) <= 30
        |        o = group(s).count(u)
        |    }
        |}
        |
        |
        |
        |Define (s:CustFundKG.Account)-[p:is_too_many_out_trans_user]->(o:Boolean) {
        |    GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |        R0("存在转出"): s.total_out_trans_user_num != null
        |        R2("大于50个"): s.total_out_trans_user_num >= 50
        |        o = rule_value(R2, true, false)
        |    }
        |}
        |
        |
        |// 当月交易量
        |Define (s:CustFundKG.Account)-[p:cur_month_num]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |      	R1("当月交易量"): date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate) <= 30
        |        o = group(s).sum(t.transCount)
        |    }
        |}
        |// 次月交易量
        |Define (s:CustFundKG.Account)-[p:last_month_num]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |      	date_delta = date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate)
        |      	R1("次月交易量"): date_delta > 30 && date_delta <=60
        |        o = group(s).sum(t.transCount)
        |    }
        |}
        |
        |// 倍数
        |Define (s:CustFundKG.Account)-[p:trans_multiple]->(o:Float) {
        |		GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |    	cur_month = rule_value(s.cur_month_num==null, 0.0, s.cur_month_num*1.0)
        |        last_month = rule_value(s.last_month_num == null, 1, s.last_month_num)
        |      	multiple = cur_month / last_month
        |        o = multiple
        |    }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:is_trans_raise_more]->(o:Boolean) {
        |		GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |      	o = rule_value(s.trans_multiple >= 3, true, false)
        |    }
        |}
        |
        |
        |// 当月交易量
        |Define (s:CustFundKG.Account)-[p:cur_month_num_20]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |      	R1("当月交易量"): date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate) <= 20
        |        o = group(s).sum(t.transCount)
        |    }
        |}
        |// 次月交易量
        |Define (s:CustFundKG.Account)-[p:last_month_num_20]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |      	date_delta = date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate)
        |      	R1("次月交易量"): date_delta > 20 && date_delta <=40
        |        o = group(s).count(t.transCount)
        |    }
        |}
        |
        |// 次次月交易量
        |Define (s:CustFundKG.Account)-[p:last_last_month_num_20]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |      	date_delta = date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate)
        |      	R1("次月交易量"): date_delta > 40 && date_delta <=60
        |        o = group(s).count(t.transCount)
        |    }
        |}
        |// 倍数
        |Define (s:CustFundKG.Account)-[p:last_trans_multiple_20]->(o:Float) {
        |		GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |      	multiple = s.last_month_num_20*1.0 / s.last_last_month_num_20
        |        o = multiple
        |    }
        |}
        |
        |// 倍数
        |Define (s:CustFundKG.Account)-[p:cur_trans_multiple_20]->(o:Float) {
        |		GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |      	multiple = s.cur_month_num_20*1.0 / s.last_month_num_20
        |        o = multiple
        |    }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:is_trans_raise_more_after_down]->(o:Boolean) {
        |		GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |      	R1("T月交易量级超过T-1月交易量级3倍"): s.last_trans_multiple_20 >=3
        |      	R2("T+1月交易量级小于T月交易量级的1/2"): s.cur_trans_multiple_20 <0.5
        |      	o = rule_value(R1 && R2, true, false)
        |    }
        |}
        |
        |
        |// 当月交易量
        |Define (s:CustFundKG.Account)-[p:trans_every_day]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)<-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |    	R1("当月交易"): date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate) <= 30
        |        month_trans_num = group(s).sum(t.transCount)
        |        o = month_trans_num*1.0/30
        |    }
        |}
        |
        |// 日均交易笔数多
        |Define (s:CustFundKG.Account)-[p:is_lot_of_trans_pre_day]->(o:Boolean) {
        |		GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |      	R1("日交易大于20"): s.trans_every_day >= 20
        |        o = rule_value(R1, true, false)
        |    }
        |}
        |
        |
        |Define (s:CustFundKG.Account)-[p:preDayMutlTrans]->(o:Int) {
        |    GraphStructure {
        |(s)-[:expand_linked_alipay_id(s.id)]->(B:CustFundKG.Alipay|CustFundKG.BankCard|CustFundKG.Default|CustFundKG.Huabei|CustFundKG.Jiebei|CustFundKG.MyBank|CustFundKG.Other|CustFundKG.Yeb|CustFundKG.YIB)-[t:transfer|unknown|fundRedeem|fundPurchase|dingqiPurchase|taxRefund|transfer|consume|ylbPurchase|jbRepay|hbRepay|creditCardRepay|withdraw|gkhRepay|yebPurchase|corpCreditRepay|deposit|merchantSettle|ylbRedeem|dingqiRedeem|jbLoan|corpCreditLoan|sceneLoan]->(C)
        |
        |	}
        |	Rule {
        |    	R1("当月交易"): date_diff(from_unix_time(now(), 'yyyyMMdd'),from_unix_time(t.payDate, 'yyyyMMdd')) <= 30
        |        preDayNums = group(s).trans_count_by_day(t, "payDate", "s", 50, "large")
        |        o = rule_value(preDayNums > 10, true, false)
        |    }
        |}
        |
        |
        |Define (s:CustFundKG.Account)-[:nightTrans]->(o:Boolean) {
        |  GraphStructure {
        |  (s)-[:expand_linked_alipay_id(s.id)]->(B:CustFundKG.Alipay|CustFundKG.BankCard|CustFundKG.Default|CustFundKG.Huabei|CustFundKG.Jiebei|CustFundKG.MyBank|CustFundKG.Other|CustFundKG.Yeb|CustFundKG.YIB)-[t:transfer|unknown|fundRedeem|fundPurchase|dingqiPurchase|taxRefund|transfer|consume|ylbPurchase|jbRepay|hbRepay|creditCardRepay|withdraw|gkhRepay|yebPurchase|corpCreditRepay|deposit|merchantSettle|ylbRedeem|dingqiRedeem|jbLoan|corpCreditLoan|sceneLoan]->(C)
        |  }
        |  Rule {
        |  hour = hourOfDay(cast_type(t.payDate, 'bigint')*1000)
        |  R1: hour <=5 && hour >=0
        |  o = rule_value(R1, true, false)
        |  }
        |}
        |
        |//1 先定义交易次数
        |Define (s:CustFundKG.Account)-[p:total_trans_num]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |        o = group(s).sum(t.transCount)
        |    }
        |}
        |
        |//2 定义万元以上
        |Define (s:CustFundKG.Account)-[p:large_trans]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |    	R1("大笔交易"): t.sumAmt >= 10000*100
        |        o = group(s).sum(t.transCount)
        |    }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:is_large_trans_user]->(o:Boolean) {
        |    GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |        R0("存在大笔交易"): s.large_trans != null
        |        R2("占比"): s.large_trans*1.0/s.total_trans_num>0.2
        |        o = rule_value(R2, true, false)
        |    }
        |}
        |
        |
        |Define (s:CustFundKG.Account)-[p:tranTargetUser]->(o:CustFundKG.Account) {
        |    GraphStructure {
        |        (o)<-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |      tran_count = group(s,o).sum(t.transCount)
        |      p.tran_count = tran_count
        |    }
        |}
        |
        |
        |
        |Define (s:CustFundKG.Account)-[p:is_repeat_tran_user]->(o:Int) {
        |    GraphStructure {
        |         (s)-[t:tranTargetUser]->(u:CustFundKG.Account)
        |    }
        |	Rule {
        |        user_num("交易笔数大于3的重复用户个数") = group(s).countIf(t.tran_count>=3, u)
        |        R1("超过10个"): user_num > 10
        |        o = rule_value(R1, true, false)
        |    }
        |}
        |
        |//1 先定义30天以内金额大于0.01、0.1、1.0大于3比
        |Define (s:CustFundKG.Account)-[p:is_test_trans]->(o:Boolean) {
        |    GraphStructure {
        |(s)-[:expand_linked_alipay_id(s.id)]->(B:CustFundKG.Alipay|CustFundKG.BankCard|CustFundKG.Default|CustFundKG.Huabei|CustFundKG.Jiebei|CustFundKG.MyBank|CustFundKG.Other|CustFundKG.Yeb|CustFundKG.YIB)-[t:transfer|unknown|fundRedeem|fundPurchase|dingqiPurchase|taxRefund|transfer|consume|ylbPurchase|jbRepay|hbRepay|creditCardRepay|withdraw|gkhRepay|yebPurchase|corpCreditRepay|deposit|merchantSettle|ylbRedeem|dingqiRedeem|jbLoan|corpCreditLoan|sceneLoan]-(C)
        |
        |    }
        |	Rule {
        |      R1("30天以内"):date_diff(from_unix_time(now(), 'yyyyMMdd'),from_unix_time(t.payDate, 'yyyyMMdd')) <= 30
        |      tran1("转账0.01") = group(s).countIf(t.amount == 1, t.amount)
        |      tran2("转账0.1") = group(s).countIf(t.amount == 10, t.amount)
        |      tran3("转账1") = group(s).countIf(t.amount == 100, t.amount)
        |      R2("大于3比"): tran1 + tran2 + tran3 > 3
        |      o = rule_value(R2, true, false)
        |    }
        |}
        |
        |//1 先定义30天以内金额大于0.01、0.1、1.0大于3比
        |Define (s:CustFundKG.Account)-[p:is_lucky_trans]->(o:Boolean) {
        |    GraphStructure {
        |(s)-[:expand_linked_alipay_id(s.id)]->(B:CustFundKG.Alipay|CustFundKG.BankCard|CustFundKG.Default|CustFundKG.Huabei|CustFundKG.Jiebei|CustFundKG.MyBank|CustFundKG.Other|CustFundKG.Yeb|CustFundKG.YIB)-[t:transfer|unknown|fundRedeem|fundPurchase|dingqiPurchase|taxRefund|transfer|consume|ylbPurchase|jbRepay|hbRepay|creditCardRepay|withdraw|gkhRepay|yebPurchase|corpCreditRepay|deposit|merchantSettle|ylbRedeem|dingqiRedeem|jbLoan|corpCreditLoan|sceneLoan]-(C)
        |
        |    }
        |	Rule {
        |      R1("30天以内"):date_diff(from_unix_time(now(), 'yyyyMMdd'),from_unix_time(t.payDate, 'yyyyMMdd')) <= 30
        |      tran1("转账0.88 0.66") = group(s).countIf(t.amount < 10 && (t.amount%100 == 88 || t.amount%100 == 66), t.amount)
        |      tran2("转账68 88") = group(s).countIf(t.amount > 45 && (t.amount%100 == 8800 || t.amount%100 == 6800), t.amount)
        |      R2("大于3比"): tran1 + tran2 > 5
        |      o = rule_value(R2, true, false)
        |    }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:hasExceptionTrans]->(o:Boolean) {
        |    GraphStructure {
        |      (s)-[:expand_linked_alipay_id(s.id)]->(u:ABM.User)
        |  }
        |  Rule {
        |      R1("交易对手大于2个被上报"): u.cntptyNoReport3m >2
        |      R2("上报对手60天聚合值大于1w"): u.cntptyNoReport3m > 10000
        |      o = rule_value(R1 && R2, true, false)
        |  }
        |}
        |
        |
        |Define (s:CustFundKG.Account)-[p:transInNum]->(o:Int) {
        |  GraphStructure {
        |  	(u:CustFundKG.Account)-[t:accountFundContact]->(s)
        |  }
        |  Rule {
        |    transNum = group(s).sum(t.transCount)
        |  	o = transNum
        |  }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:transInAmount]->(o:CustFundKG.Account) {
        |  GraphStructure {
        |  	(o)-[t:accountFundContact]->(s)
        |  }
        |  Rule {
        |    transAmount = group(s,o).sum(t.sumAmt)
        |  	p.transAmount = transAmount
        |  }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:centralizedTransfer]->(o:String) {
        |  GraphStructure {
        |  	(s)-[t:transInAmount]->(u:CustFundKG.Account)
        |  }
        |  Rule {
        |    R1("流入资金笔数大于100"): s.transInNum > 100
        |  	totalAmount = group(s).sum(t.transAmount)
        |    top5Amount = group(s).order_edge_and_slice_sum(t.transAmount, "desc", 5)
        |  	top5Rate("top5流入资金占比")= top5Amount*1.0/totalAmount
        |  	o = rule_value(top5Rate > 0.5, "集中转入", rule_value(top5Rate < 0.3, "分散转入", ""))
        |  }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:transOutNum]->(o:Int) {
        |  GraphStructure {
        |  	(u:CustFundKG.Account)<-[t:accountFundContact]-(s)
        |  }
        |  Rule {
        |    transNum = group(s).sum(t.transCount)
        |  	o = transNum
        |  }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:transOutAmount]->(o:CustFundKG.Account) {
        |  GraphStructure {
        |  	(o)<-[t:accountFundContact]-(s)
        |  }
        |  Rule {
        |    transAmount = group(s,o).sum(t.sumAmt)
        |  	p.transAmount = transAmount
        |  }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:centralizedTransferOut]->(o:String) {
        |  GraphStructure {
        |  	(s)-[t:transOutAmount]->(u:CustFundKG.Account)
        |  }
        |  Rule {
        |    R1("流入资金笔数大于100"): s.transOutNum > 100
        |  	totalAmount = group(s).sum(t.transAmount)
        |    top5Amount = group(s).order_edge_and_slice_sum(t.transAmount, "desc", 5)
        |  	top5Rate("top5流入资金占比")= top5Amount*1.0/totalAmount
        |  	o = rule_value(top5Rate > 0.5, "集中转出", rule_value(top5Rate < 0.3, "分散转出", ""))
        |  }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:badIpUsed]->(o:Boolean) {
        |  GraphStructure {
        |    (s)-[:expand_linked_alipay_id(s.id)]->(a:ABM.User)-[hasIp:acc2ip]->(ip:ABM.IP)
        |  }
        |  Rule {
        |      R1("必选180天以内"): hasIp.ipUse180dCnt >=3
        |      R2("必须是高危地区"): ip.country in ['菲律宾','柬埔寨','老挝','日本','香港','台湾','泰国','澳门','越南','马来西亚','印度尼西亚']
        |      o = rule_value(R2 && R1, true, false)
        |  }
        |}
        |//获取信息
        |GraphStructure {
        |	s [CustFundKG.Account]
        |}
        |Rule {
        |}
        |Action{
        |	get(s.id, s.too_many_trans_user_num, s.is_too_many_out_trans_user, s.is_trans_raise_more, s.is_trans_raise_more_after_down, s.is_lot_of_trans_pre_day, s.preDayMutlTrans, s.nightTrans, s.is_large_trans_user,s.is_repeat_tran_user, s.is_test_trans,s.is_lucky_trans, s.hasExceptionTrans, s.centralizedTransfer, s.centralizedTransferOut, s.badIpUsed)
        |}
        |""".stripMargin
    val containerNode: Map[String, Set[String]] = Map.apply(
      "CustFundKG.Alipay" -> Set.apply("id"),
      "CustFundKG.BankCard" -> Set.apply("id"),
      "CustFundKG.Default" -> Set.apply("id"),
      "CustFundKG.Huabei" -> Set.apply("id"),
      "CustFundKG.Jiebei" -> Set.apply("id"),
      "CustFundKG.MyBank" -> Set.apply("id"),
      "CustFundKG.Other" -> Set.apply("id"),
      "CustFundKG.Alipay" -> Set.apply("id"),
      "CustFundKG.Yeb" -> Set.apply("id"),
      "CustFundKG.YIB" -> Set.apply("id"))
    val edges = Set.apply(
      "transfer",
      "unknown",
      "fundRedeem",
      "fundPurchase",
      "dingqiPurchase",
      "taxRefund",
      "consume",
      "ylbPurchase",
      "jbRepay",
      "hbRepay",
      "creditCardRepay",
      "withdraw",
      "gkhRepay",
      "yebPurchase",
      "corpCreditRepay",
      "deposit",
      "merchantSettle",
      "ylbRedeem",
      "dingqiRedeem",
      "jbLoan",
      "corpCreditLoan",
      "sceneLoan")
    val attrs = Set.apply("payDate", "amount")

    var schema: Map[String, Set[String]] = Map.apply(
      "CustFundKG.Account" -> Set.apply("id", "is_black"),
      "CustFundKG.Account_accountFundContact_CustFundKG.Account" ->
        Set.apply("transCount", "sumAmt", "transDate"),
      "ABM.User" -> Set.apply("id", "cntptyNoReport3m"),
      "ABM.IP" -> Set.apply("id", "country"),
      "ABM.User_acc2ip_ABM.IP" -> Set.apply("ipUse180dCnt"))
    schema = schema ++ containerNode
    for (h <- containerNode.keySet) {
      for (t <- containerNode.keySet) {
        for (e <- edges) {
          val label = h + "_" + e + "_" + t
          schema = schema + (label -> attrs)
        }
      }
    }
    val catalog = new PropertyGraphCatalog(schema)

    catalog.init()

    RuleRunner.getInstance
    println("===========")
    val session = new EmptySession(new KgDslParser(), catalog)
    val startTime = System.currentTimeMillis()

    val rst = session.plan(
      dsl,
      Map
        .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true),
          (Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, false))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (join: Join[EmptyRDG], cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    val costMs = System.currentTimeMillis() - startTime
    // scalastyle:off println
    println(costMs)
    costMs < 3000 should equal(true)
  }

  it("test sub query parse cost benchmark enable plan logger") {
    val dsl =
      """
        |//1 先定义流入对手
        |Define (s:CustFundKG.Account)-[p:in_trans_user_num]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)-[t:accountFundContact]->(s)
        |    }
        |	Rule {
        |        o = group(s).count(u)
        |    }
        |}
        |
        |//2 先定义流出对手
        |Define (s:CustFundKG.Account)-[p:out_trans_user_num]->(o:Int) {
        |    GraphStructure {
        |        (s)-[t:accountFundContact]->(u:CustFundKG.Account)
        |    }
        |	Rule {
        |        o = group(s).count(u)
        |    }
        |}
        |
        |//3 定义交易对手众多
        |Define (s:CustFundKG.Account)-[p:too_many_trans_user_num]->(o:Boolean) {
        |    GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |        R1("流入或流出对手超过120名"): s.out_trans_user_num > 120 || s.in_trans_user_num > 120
        |        o = rule_value(R1, true, false)
        |    }
        |}
        |
        |
        |//1 先定义转出对手数目
        |Define (s:CustFundKG.Account)-[p:total_out_trans_user_num]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)<-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |        R1("1月内交易"): date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate) <= 30
        |        o = group(s).count(u)
        |    }
        |}
        |
        |
        |
        |Define (s:CustFundKG.Account)-[p:is_too_many_out_trans_user]->(o:Boolean) {
        |    GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |        R0("存在转出"): s.total_out_trans_user_num != null
        |        R2("大于50个"): s.total_out_trans_user_num >= 50
        |        o = rule_value(R2, true, false)
        |    }
        |}
        |
        |
        |// 当月交易量
        |Define (s:CustFundKG.Account)-[p:cur_month_num]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |      	R1("当月交易量"): date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate) <= 30
        |        o = group(s).sum(t.transCount)
        |    }
        |}
        |// 次月交易量
        |Define (s:CustFundKG.Account)-[p:last_month_num]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |      	date_delta = date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate)
        |      	R1("次月交易量"): date_delta > 30 && date_delta <=60
        |        o = group(s).sum(t.transCount)
        |    }
        |}
        |
        |// 倍数
        |Define (s:CustFundKG.Account)-[p:trans_multiple]->(o:Float) {
        |		GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |    	cur_month = rule_value(s.cur_month_num==null, 0.0, s.cur_month_num*1.0)
        |        last_month = rule_value(s.last_month_num == null, 1, s.last_month_num)
        |      	multiple = cur_month / last_month
        |        o = multiple
        |    }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:is_trans_raise_more]->(o:Boolean) {
        |		GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |      	o = rule_value(s.trans_multiple >= 3, true, false)
        |    }
        |}
        |
        |
        |// 当月交易量
        |Define (s:CustFundKG.Account)-[p:cur_month_num_20]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |      	R1("当月交易量"): date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate) <= 20
        |        o = group(s).sum(t.transCount)
        |    }
        |}
        |// 次月交易量
        |Define (s:CustFundKG.Account)-[p:last_month_num_20]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |      	date_delta = date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate)
        |      	R1("次月交易量"): date_delta > 20 && date_delta <=40
        |        o = group(s).count(t.transCount)
        |    }
        |}
        |
        |// 次次月交易量
        |Define (s:CustFundKG.Account)-[p:last_last_month_num_20]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |      	date_delta = date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate)
        |      	R1("次月交易量"): date_delta > 40 && date_delta <=60
        |        o = group(s).count(t.transCount)
        |    }
        |}
        |// 倍数
        |Define (s:CustFundKG.Account)-[p:last_trans_multiple_20]->(o:Float) {
        |		GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |      	multiple = s.last_month_num_20*1.0 / s.last_last_month_num_20
        |        o = multiple
        |    }
        |}
        |
        |// 倍数
        |Define (s:CustFundKG.Account)-[p:cur_trans_multiple_20]->(o:Float) {
        |		GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |      	multiple = s.cur_month_num_20*1.0 / s.last_month_num_20
        |        o = multiple
        |    }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:is_trans_raise_more_after_down]->(o:Boolean) {
        |		GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |      	R1("T月交易量级超过T-1月交易量级3倍"): s.last_trans_multiple_20 >=3
        |      	R2("T+1月交易量级小于T月交易量级的1/2"): s.cur_trans_multiple_20 <0.5
        |      	o = rule_value(R1 && R2, true, false)
        |    }
        |}
        |
        |
        |// 当月交易量
        |Define (s:CustFundKG.Account)-[p:trans_every_day]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)<-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |    	R1("当月交易"): date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate) <= 30
        |        month_trans_num = group(s).sum(t.transCount)
        |        o = month_trans_num*1.0/30
        |    }
        |}
        |
        |// 日均交易笔数多
        |Define (s:CustFundKG.Account)-[p:is_lot_of_trans_pre_day]->(o:Boolean) {
        |		GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |      	R1("日交易大于20"): s.trans_every_day >= 20
        |        o = rule_value(R1, true, false)
        |    }
        |}
        |
        |
        |Define (s:CustFundKG.Account)-[p:preDayMutlTrans]->(o:Int) {
        |    GraphStructure {
        |(s)-[:expand_linked_alipay_id(s.id)]->(B:CustFundKG.Alipay|CustFundKG.BankCard|CustFundKG.Default|CustFundKG.Huabei|CustFundKG.Jiebei|CustFundKG.MyBank|CustFundKG.Other|CustFundKG.Yeb|CustFundKG.YIB)-[t:transfer|unknown|fundRedeem|fundPurchase|dingqiPurchase|taxRefund|transfer|consume|ylbPurchase|jbRepay|hbRepay|creditCardRepay|withdraw|gkhRepay|yebPurchase|corpCreditRepay|deposit|merchantSettle|ylbRedeem|dingqiRedeem|jbLoan|corpCreditLoan|sceneLoan]->(C)
        |
        |	}
        |	Rule {
        |    	R1("当月交易"): date_diff(from_unix_time(now(), 'yyyyMMdd'),from_unix_time(t.payDate, 'yyyyMMdd')) <= 30
        |        preDayNums = group(s).trans_count_by_day(t, "payDate", "s", 50, "large")
        |        o = rule_value(preDayNums > 10, true, false)
        |    }
        |}
        |
        |
        |Define (s:CustFundKG.Account)-[:nightTrans]->(o:Boolean) {
        |  GraphStructure {
        |  (s)-[:expand_linked_alipay_id(s.id)]->(B:CustFundKG.Alipay|CustFundKG.BankCard|CustFundKG.Default|CustFundKG.Huabei|CustFundKG.Jiebei|CustFundKG.MyBank|CustFundKG.Other|CustFundKG.Yeb|CustFundKG.YIB)-[t:transfer|unknown|fundRedeem|fundPurchase|dingqiPurchase|taxRefund|transfer|consume|ylbPurchase|jbRepay|hbRepay|creditCardRepay|withdraw|gkhRepay|yebPurchase|corpCreditRepay|deposit|merchantSettle|ylbRedeem|dingqiRedeem|jbLoan|corpCreditLoan|sceneLoan]->(C)
        |  }
        |  Rule {
        |  hour = hourOfDay(cast_type(t.payDate, 'bigint')*1000)
        |  R1: hour <=5 && hour >=0
        |  o = rule_value(R1, true, false)
        |  }
        |}
        |
        |//1 先定义交易次数
        |Define (s:CustFundKG.Account)-[p:total_trans_num]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |        o = group(s).sum(t.transCount)
        |    }
        |}
        |
        |//2 定义万元以上
        |Define (s:CustFundKG.Account)-[p:large_trans]->(o:Int) {
        |    GraphStructure {
        |        (u:CustFundKG.Account)-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |    	R1("大笔交易"): t.sumAmt >= 10000*100
        |        o = group(s).sum(t.transCount)
        |    }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:is_large_trans_user]->(o:Boolean) {
        |    GraphStructure {
        |        (s)
        |    }
        |	Rule {
        |        R0("存在大笔交易"): s.large_trans != null
        |        R2("占比"): s.large_trans*1.0/s.total_trans_num>0.2
        |        o = rule_value(R2, true, false)
        |    }
        |}
        |
        |
        |Define (s:CustFundKG.Account)-[p:tranTargetUser]->(o:CustFundKG.Account) {
        |    GraphStructure {
        |        (o)<-[t:accountFundContact]-(s)
        |    }
        |	Rule {
        |      tran_count = group(s,o).sum(t.transCount)
        |      p.tran_count = tran_count
        |    }
        |}
        |
        |
        |
        |Define (s:CustFundKG.Account)-[p:is_repeat_tran_user]->(o:Int) {
        |    GraphStructure {
        |         (s)-[t:tranTargetUser]->(u:CustFundKG.Account)
        |    }
        |	Rule {
        |        user_num("交易笔数大于3的重复用户个数") = group(s).countIf(t.tran_count>=3, u)
        |        R1("超过10个"): user_num > 10
        |        o = rule_value(R1, true, false)
        |    }
        |}
        |
        |//1 先定义30天以内金额大于0.01、0.1、1.0大于3比
        |Define (s:CustFundKG.Account)-[p:is_test_trans]->(o:Boolean) {
        |    GraphStructure {
        |(s)-[:expand_linked_alipay_id(s.id)]->(B:CustFundKG.Alipay|CustFundKG.BankCard|CustFundKG.Default|CustFundKG.Huabei|CustFundKG.Jiebei|CustFundKG.MyBank|CustFundKG.Other|CustFundKG.Yeb|CustFundKG.YIB)-[t:transfer|unknown|fundRedeem|fundPurchase|dingqiPurchase|taxRefund|transfer|consume|ylbPurchase|jbRepay|hbRepay|creditCardRepay|withdraw|gkhRepay|yebPurchase|corpCreditRepay|deposit|merchantSettle|ylbRedeem|dingqiRedeem|jbLoan|corpCreditLoan|sceneLoan]-(C)
        |
        |    }
        |	Rule {
        |      R1("30天以内"):date_diff(from_unix_time(now(), 'yyyyMMdd'),from_unix_time(t.payDate, 'yyyyMMdd')) <= 30
        |      tran1("转账0.01") = group(s).countIf(t.amount == 1, t.amount)
        |      tran2("转账0.1") = group(s).countIf(t.amount == 10, t.amount)
        |      tran3("转账1") = group(s).countIf(t.amount == 100, t.amount)
        |      R2("大于3比"): tran1 + tran2 + tran3 > 3
        |      o = rule_value(R2, true, false)
        |    }
        |}
        |
        |//1 先定义30天以内金额大于0.01、0.1、1.0大于3比
        |Define (s:CustFundKG.Account)-[p:is_lucky_trans]->(o:Boolean) {
        |    GraphStructure {
        |(s)-[:expand_linked_alipay_id(s.id)]->(B:CustFundKG.Alipay|CustFundKG.BankCard|CustFundKG.Default|CustFundKG.Huabei|CustFundKG.Jiebei|CustFundKG.MyBank|CustFundKG.Other|CustFundKG.Yeb|CustFundKG.YIB)-[t:transfer|unknown|fundRedeem|fundPurchase|dingqiPurchase|taxRefund|transfer|consume|ylbPurchase|jbRepay|hbRepay|creditCardRepay|withdraw|gkhRepay|yebPurchase|corpCreditRepay|deposit|merchantSettle|ylbRedeem|dingqiRedeem|jbLoan|corpCreditLoan|sceneLoan]-(C)
        |
        |    }
        |	Rule {
        |      R1("30天以内"):date_diff(from_unix_time(now(), 'yyyyMMdd'),from_unix_time(t.payDate, 'yyyyMMdd')) <= 30
        |      tran1("转账0.88 0.66") = group(s).countIf(t.amount < 10 && (t.amount%100 == 88 || t.amount%100 == 66), t.amount)
        |      tran2("转账68 88") = group(s).countIf(t.amount > 45 && (t.amount%100 == 8800 || t.amount%100 == 6800), t.amount)
        |      R2("大于3比"): tran1 + tran2 > 5
        |      o = rule_value(R2, true, false)
        |    }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:hasExceptionTrans]->(o:Boolean) {
        |    GraphStructure {
        |      (s)-[:expand_linked_alipay_id(s.id)]->(u:ABM.User)
        |  }
        |  Rule {
        |      R1("交易对手大于2个被上报"): u.cntptyNoReport3m >2
        |      R2("上报对手60天聚合值大于1w"): u.cntptyNoReport3m > 10000
        |      o = rule_value(R1 && R2, true, false)
        |  }
        |}
        |
        |
        |Define (s:CustFundKG.Account)-[p:transInNum]->(o:Int) {
        |  GraphStructure {
        |  	(u:CustFundKG.Account)-[t:accountFundContact]->(s)
        |  }
        |  Rule {
        |    transNum = group(s).sum(t.transCount)
        |  	o = transNum
        |  }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:transInAmount]->(o:CustFundKG.Account) {
        |  GraphStructure {
        |  	(o)-[t:accountFundContact]->(s)
        |  }
        |  Rule {
        |    transAmount = group(s,o).sum(t.sumAmt)
        |  	p.transAmount = transAmount
        |  }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:centralizedTransfer]->(o:String) {
        |  GraphStructure {
        |  	(s)-[t:transInAmount]->(u:CustFundKG.Account)
        |  }
        |  Rule {
        |    R1("流入资金笔数大于100"): s.transInNum > 100
        |  	totalAmount = group(s).sum(t.transAmount)
        |    top5Amount = group(s).order_edge_and_slice_sum(t.transAmount, "desc", 5)
        |  	top5Rate("top5流入资金占比")= top5Amount*1.0/totalAmount
        |  	o = rule_value(top5Rate > 0.5, "集中转入", rule_value(top5Rate < 0.3, "分散转入", ""))
        |  }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:transOutNum]->(o:Int) {
        |  GraphStructure {
        |  	(u:CustFundKG.Account)<-[t:accountFundContact]-(s)
        |  }
        |  Rule {
        |    transNum = group(s).sum(t.transCount)
        |  	o = transNum
        |  }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:transOutAmount]->(o:CustFundKG.Account) {
        |  GraphStructure {
        |  	(o)<-[t:accountFundContact]-(s)
        |  }
        |  Rule {
        |    transAmount = group(s,o).sum(t.sumAmt)
        |  	p.transAmount = transAmount
        |  }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:centralizedTransferOut]->(o:String) {
        |  GraphStructure {
        |  	(s)-[t:transOutAmount]->(u:CustFundKG.Account)
        |  }
        |  Rule {
        |    R1("流入资金笔数大于100"): s.transOutNum > 100
        |  	totalAmount = group(s).sum(t.transAmount)
        |    top5Amount = group(s).order_edge_and_slice_sum(t.transAmount, "desc", 5)
        |  	top5Rate("top5流入资金占比")= top5Amount*1.0/totalAmount
        |  	o = rule_value(top5Rate > 0.5, "集中转出", rule_value(top5Rate < 0.3, "分散转出", ""))
        |  }
        |}
        |
        |Define (s:CustFundKG.Account)-[p:badIpUsed]->(o:Boolean) {
        |  GraphStructure {
        |    (s)-[:expand_linked_alipay_id(s.id)]->(a:ABM.User)-[hasIp:acc2ip]->(ip:ABM.IP)
        |  }
        |  Rule {
        |      R1("必选180天以内"): hasIp.ipUse180dCnt >=3
        |      R2("必须是高危地区"): ip.country in ['菲律宾','柬埔寨','老挝','日本','香港','台湾','泰国','澳门','越南','马来西亚','印度尼西亚']
        |      o = rule_value(R2 && R1, true, false)
        |  }
        |}
        |//获取信息
        |GraphStructure {
        |	s [CustFundKG.Account]
        |}
        |Rule {
        |}
        |Action{
        |	get(s.id, s.too_many_trans_user_num, s.is_too_many_out_trans_user, s.is_trans_raise_more, s.is_trans_raise_more_after_down, s.is_lot_of_trans_pre_day, s.preDayMutlTrans, s.nightTrans, s.is_large_trans_user,s.is_repeat_tran_user, s.is_test_trans,s.is_lucky_trans, s.hasExceptionTrans, s.centralizedTransfer, s.centralizedTransferOut, s.badIpUsed)
        |}
        |""".stripMargin
    val containerNode: Map[String, Set[String]] = Map.apply(
      "CustFundKG.Alipay" -> Set.apply("id"),
      "CustFundKG.BankCard" -> Set.apply("id"),
      "CustFundKG.Default" -> Set.apply("id"),
      "CustFundKG.Huabei" -> Set.apply("id"),
      "CustFundKG.Jiebei" -> Set.apply("id"),
      "CustFundKG.MyBank" -> Set.apply("id"),
      "CustFundKG.Other" -> Set.apply("id"),
      "CustFundKG.Alipay" -> Set.apply("id"),
      "CustFundKG.Yeb" -> Set.apply("id"),
      "CustFundKG.YIB" -> Set.apply("id"))
    val edges = Set.apply(
      "transfer",
      "unknown",
      "fundRedeem",
      "fundPurchase",
      "dingqiPurchase",
      "taxRefund",
      "consume",
      "ylbPurchase",
      "jbRepay",
      "hbRepay",
      "creditCardRepay",
      "withdraw",
      "gkhRepay",
      "yebPurchase",
      "corpCreditRepay",
      "deposit",
      "merchantSettle",
      "ylbRedeem",
      "dingqiRedeem",
      "jbLoan",
      "corpCreditLoan",
      "sceneLoan")
    val attrs = Set.apply("payDate", "amount")

    var schema: Map[String, Set[String]] = Map.apply(
      "CustFundKG.Account" -> Set.apply("id", "is_black"),
      "CustFundKG.Account_accountFundContact_CustFundKG.Account" ->
        Set.apply("transCount", "sumAmt", "transDate"),
      "ABM.User" -> Set.apply("id", "cntptyNoReport3m"),
      "ABM.IP" -> Set.apply("id", "country"),
      "ABM.User_acc2ip_ABM.IP" -> Set.apply("ipUse180dCnt"))
    schema = schema ++ containerNode
    for (h <- containerNode.keySet) {
      for (t <- containerNode.keySet) {
        for (e <- edges) {
          val label = h + "_" + e + "_" + t
          schema = schema + (label -> attrs)
        }
      }
    }
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()

    RuleRunner.getInstance
    val session = new EmptySession(new KgDslParser(), catalog)
    val startTime = System.currentTimeMillis()

    val rst = session.plan(
      dsl,
      Map
        .apply(
          (Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true),
          (Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (join: Join[EmptyRDG], cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    val costMs = System.currentTimeMillis() - startTime
    // scalastyle:off println
    println(costMs)
  }
  it("test sub query depends with catalog3") {
    val dsl =
      """
        |Define (u1:AttributePOC.BrinsonAttribute)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfBrinsonAttribute`/`市场贡献大`) {
        |  GraphStructure {
        |    (s:AttributePOC.BrinsonAttribute)-[p1:factorValue]->(u1)
        |  }
        |
        |  Rule {
        |    R1: u1.factorType == "market"
        |    R4: s.factorType == "total"
        |    v = (u1.factorValue/ s.factorValue)
        |    R2("必须大于50%"): v > 0.5
        |  }
        |}
        |
        |Define (u2:AttributePOC.BrinsonAttribute)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfBrinsonAttribute`/`选股贡献大`) {
        |  GraphStructure {
        |    (s:AttributePOC.BrinsonAttribute)-[p1:factorValue]->(u1:AttributePOC.BrinsonAttribute)
        |    (s)-[p2:factorValue]->(u2)
        |    (s)-[p3:factorValue]->(u3:AttributePOC.BrinsonAttribute)
        |  }
        |
        |  Rule {
        |    R1: u1.factorType == "cluster"
        |    R2: u2.factorType == "stock"
        |    R3: u3.factorType == "trade"
        |    R4: s.factorType == "total"
        |    v = (u1.factorValue/ s.factorValue + u3.factorValue / s.factorValue)
        |    R6("必须大于50%"): v < 0.5
        |    R5("交易收益大于选股"): u2.factorValue > u3.factorValue
        |  }
        |}
        |
        |Define (u2:AttributePOC.BrinsonAttribute)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfBrinsonAttribute`/`交易贡献大`) {
        |  GraphStructure {
        |    (s:AttributePOC.BrinsonAttribute)-[p1:factorValue]->(u1:AttributePOC.BrinsonAttribute)
        |    (s)-[p2:factorValue]->(u2)
        |    (s)-[p3:factorValue]->(u3:AttributePOC.BrinsonAttribute)
        |  }
        |
        |  Rule {
        |    R1: u1.factorType == "cluster"
        |    R2: u2.factorType == "trade"
        |    R3: u3.factorType == "stock"
        |    R4: s.factorType == "total"
        |    v = (u1.factorValue/ s.factorValue + u2.factorValue / s.factorValue)
        |    R5("必须大于50%"): v > 0.5
        |    R6("交易收益大于选股"): u2.factorValue > u3.factorValue
        |  }
        |}
        |
        |Define (s: AttributePOC.TracebackDay)-[p: market]->(o: Float) {
        | GraphStructure {
        |    (s:AttributePOC.TracebackDay)-[:day]->(f: AttributePOC.BrinsonAttribute)-[:factorValue]->(u1:`AttributePOC.TaxonomyOfBrinsonAttribute`/`市场贡献大`)
        |	}
        |    Rule {
        |    o = u1.factorValue
        |    }
        |}
        |
        |Define (s: AttributePOC.TracebackDay)-[p: stock]->(o: Float) {
        | GraphStructure {
        |    (s:AttributePOC.TracebackDay)-[:day]->(f: AttributePOC.BrinsonAttribute)-[:factorValue]->(u1:`AttributePOC.TaxonomyOfBrinsonAttribute`/`选股贡献大`)
        |	}
        |    Rule {
        |    o = u1.factorValue
        |    }
        |}
        |
        |
        |Define (s: AttributePOC.TracebackDay)-[p: trade]->(o: Float) {
        | GraphStructure {
        |    (s:AttributePOC.TracebackDay)-[:day]->(f: AttributePOC.BrinsonAttribute)-[:factorValue]->(u1:`AttributePOC.TaxonomyOfBrinsonAttribute`/`交易贡献大`)
        |	}
        |    Rule {
        |    o = u1.factorValue
        |    }
        |}
        |
        |Define (s: AttributePOC.TracebackDay)-[p: result]->(o: Text) {
        |    GraphStructure {
        |   (s: AttributePOC.TracebackDay)
        |}
        |Rule {
        |// 按照选股、交易、市场的顺序输出
        |  str1 = rule_value(s.stock == null, "", concat("选股", ": ", s.stock, ', '))
        |    str2 = concat(str1, rule_value(s.trade == null, "", concat("交易", ": ", s.trade, ', ')))
        |    str3 = concat(str2, rule_value(s.market == null, "", concat("市场", ": ", s.market)))
        |    o = str3
        |}
        |}
        |
        |Define (u1:AttributePOC.Scenario)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfScenario`/`基金收益分析`) {
        |  GraphStructure {
        |  	(u1)<-[p1:scConfig]-(s:AttributePOC.TracebackDay)
        |  }
        |  Rule {
        |    R1: s.result != null
        |  }
        |}
        |
        |// 查找使用了相同主演的两个导演
        |GraphStructure {
        |(s:AttributePOC.TracebackDay)-[:scConfig]->(u3:`AttributePOC.TaxonomyOfScenario`/`基金收益分析`)
        |}
        |Rule {
        |
        |}
        |Action {
        |  get(s.id, u3.id, s.result)
        |}""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "AttributePOC.TracebackDay" -> Set.apply("id"),
      "AttributePOC.Scenario" -> Set.apply("id"),
      "AttributePOC.BrinsonAttribute" -> Set.apply("id", "factorValue", "factorType"),
      "AttributePOC.TaxonomyOfBrinsonAttribute" -> Set.apply("id"),
      "AttributePOC.TaxonomyOfScenario" -> Set.apply("id"),
      "AttributePOC.TracebackDay_day_AttributePOC.BrinsonAttribute" -> Set.empty,
      "AttributePOC.BrinsonAttribute_factorValue_AttributePOC.BrinsonAttribute" -> Set.empty,
      "AttributePOC.Scenario_scConfig_AttributePOC.TracebackDay" -> Set.empty,
      "AttributePOC.TracebackDay_scConfig_AttributePOC.Scenario" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (join: Join[EmptyRDG], cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(15)
  }

  it("test sub query depends with catalog2") {
    val dsl =
      """
        |Define (u1:AttributePOC.BrinsonAttribute)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfBrinsonAttribute`/`市场贡献大`) {
        |  GraphStructure {
        |    (s:AttributePOC.BrinsonAttribute)-[p1:factorValue]->(u1)
        |  }
        |
        |  Rule {
        |    R1: u1.factorType == "market"
        |    R4: s.factorType == "total"
        |    v = (u1.factorValue/ s.factorValue)
        |    R2("必须大于50%"): v > 0.5
        |  }
        |}
        |
        |Define (u2:AttributePOC.BrinsonAttribute)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfBrinsonAttribute`/`选股贡献大`) {
        |  GraphStructure {
        |    (s:AttributePOC.BrinsonAttribute)-[p1:factorValue]->(u1:AttributePOC.BrinsonAttribute)
        |    (s)-[p2:factorValue]->(u2)
        |    (s)-[p3:factorValue]->(u3:AttributePOC.BrinsonAttribute)
        |  }
        |
        |  Rule {
        |    R1: u1.factorType == "cluster"
        |    R2: u2.factorType == "stock"
        |    R3: u3.factorType == "trade"
        |    R4: s.factorType == "total"
        |    v = (u1.factorValue/ s.factorValue + u3.factorValue / s.factorValue)
        |    R6("必须大于50%"): v < 0.5
        |    R5("交易收益大于选股"): u2.factorValue > u3.factorValue
        |  }
        |}
        |
        |Define (u2:AttributePOC.BrinsonAttribute)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfBrinsonAttribute`/`交易贡献大`) {
        |  GraphStructure {
        |    (s:AttributePOC.BrinsonAttribute)-[p1:factorValue]->(u1:AttributePOC.BrinsonAttribute)
        |    (s)-[p2:factorValue]->(u2)
        |    (s)-[p3:factorValue]->(u3:AttributePOC.BrinsonAttribute)
        |  }
        |
        |  Rule {
        |    R1: u1.factorType == "cluster"
        |    R2: u2.factorType == "trade"
        |    R3: u3.factorType == "stock"
        |    R4: s.factorType == "total"
        |    v = (u1.factorValue/ s.factorValue + u2.factorValue / s.factorValue)
        |    R5("必须大于50%"): v > 0.5
        |    R6("交易收益大于选股"): u2.factorValue > u3.factorValue
        |  }
        |}
        |
        |Define (s: AttributePOC.TracebackDay)-[p: market]->(o: Float) {
        | GraphStructure {
        |    (s:AttributePOC.TracebackDay)-[:day]->(f: AttributePOC.BrinsonAttribute)-[:factorValue]->(u1:`AttributePOC.TaxonomyOfBrinsonAttribute`/`市场贡献大`)
        |	}
        |    Rule {
        |    o = u1.factorValue
        |    }
        |}
        |
        |Define (s: AttributePOC.TracebackDay)-[p: stock]->(o: Float) {
        | GraphStructure {
        |    (s:AttributePOC.TracebackDay)-[:day]->(f: AttributePOC.BrinsonAttribute)-[:factorValue]->(u1:`AttributePOC.TaxonomyOfBrinsonAttribute`/`选股贡献大`)
        |	}
        |    Rule {
        |    o = u1.factorValue
        |    }
        |}
        |
        |
        |Define (s: AttributePOC.TracebackDay)-[p: trade]->(o: Float) {
        | GraphStructure {
        |    (s:AttributePOC.TracebackDay)-[:day]->(f: AttributePOC.BrinsonAttribute)-[:factorValue]->(u1:`AttributePOC.TaxonomyOfBrinsonAttribute`/`交易贡献大`)
        |	}
        |    Rule {
        |    o = u1.factorValue
        |    }
        |}
        |
        |Define (s: AttributePOC.TracebackDay)-[p: result]->(o: Text) {
        |    GraphStructure {
        |   (s: AttributePOC.TracebackDay)
        |}
        |Rule {
        |// 按照选股、交易、市场的顺序输出
        |  str1 = rule_value(s.stock == null, "", concat("选股", ": ", s.stock, ', '))
        |    str2 = concat(str1, rule_value(s.trade == null, "", concat("交易", ": ", s.trade, ', ')))
        |    str3 = concat(str2, rule_value(s.market == null, "", concat("市场", ": ", s.market)))
        |    o = str3
        |}
        |}
        |
        |Define (u1:AttributePOC.Scenario)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfScenario`/`基金收益分析`) {
        |  GraphStructure {
        |  	(u1)<-[p1:scConfig]-(s:AttributePOC.TracebackDay)
        |  }
        |  Rule {
        |    R1: s.result != null
        |  }
        |}
        |
        |GraphStructure {
        |(s:AttributePOC.TracebackDay)-[:day]->(f: AttributePOC.BrinsonAttribute)-[:factorValue]->(u1:`AttributePOC.TaxonomyOfBrinsonAttribute`/`交易贡献大`),
        |(s)-[:day]->(f)-[:factorValue]->(u2:`AttributePOC.TaxonomyOfBrinsonAttribute`/`市场贡献大`),
        |(s)-[:scConfig]->(u3:`AttributePOC.TaxonomyOfScenario`/`基金收益分析`)
        |}
        |Rule {
        |
        |}
        |Action {
        |get(s.id) // 获取A和B两个导演的名字，调试和在线执行时直接看渲染出来的子图
        |}
        |""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "AttributePOC.TracebackDay" -> Set.apply("id"),
      "AttributePOC.Scenario" -> Set.apply("id"),
      "AttributePOC.BrinsonAttribute" -> Set.apply("id", "factorValue", "factorType"),
      "AttributePOC.TaxonomyOfBrinsonAttribute" -> Set.apply("id"),
      "AttributePOC.TaxonomyOfScenario" -> Set.apply("id"),
      "AttributePOC.TracebackDay_day_AttributePOC.BrinsonAttribute" -> Set.empty,
      "AttributePOC.BrinsonAttribute_factorValue_AttributePOC.BrinsonAttribute" -> Set.empty,
      "AttributePOC.Scenario_scConfig_AttributePOC.TracebackDay" -> Set.empty,
      "AttributePOC.TracebackDay_scConfig_AttributePOC.Scenario" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (join: Join[EmptyRDG], cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(10)
  }
  it("test sub query depends with belongTo parse 2") {
    val dsl =
      """
        |Define (s:AttributePOC.BrinsonAttribute)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfBrinsonAttribute`/`交易`) {
        |  GraphStructure {
        |    (s)-[p1:factorValue]->(u1:AttributePOC.BrinsonAttribute)
        |  }
        |
        |  Rule {
        |  }
        |}

        |GraphStructure {
        |   (s:`AttributePOC.TaxonomyOfBrinsonAttribute`/`交易`)
        |}
        |Rule {
        |  }
        |Action {
        |  get(s.id)
        |}""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "AttributePOC.TracebackDay" -> Set.apply("id"),
      "AttributePOC.BrinsonAttribute" -> Set.apply("id", "factorValue", "factorType"),
      "AttributePOC.TaxonomyOfBrinsonAttribute" -> Set.apply("id"),
      "AttributePOC.TracebackDay_day_AttributePOC.BrinsonAttribute" -> Set.empty,
      "AttributePOC.BrinsonAttribute_factorValue_AttributePOC.BrinsonAttribute" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (join: Join[EmptyRDG], cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(1)
  }

  it("test sub query depends with belongTo parse exception") {
    val dsl =
      """
        |Define (u2:AttributePOC.BrinsonAttribute)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfBrinsonAttribute`/`交易`) {
        |  GraphStructure {
        |    (s:AttributePOC.BrinsonAttribute)-[p1:factorValue]->(u1:AttributePOC.BrinsonAttribute)
        |    (s)-[p2:factorValue]->(u2)
        |    (s)-[p3:factorValue]->(u3:AttributePOC.BrinsonAttribute)
        |  }
        |
        |  Rule {
        |    R1: u1.factorType == "cluster"
        |    R2: u2.factorType == "trade"
        |    R3: u3.factorType == "stock"
        |    R6: s.factorType == "total"
        |    v = (u1.factorValue/ s.factorValue + u2.factorValue / s.factorValue)
        |    R4("必须大于50%"): v > 0.5
        |    R5("交易收益大于选股"): u2.factorValue > u3.factorValue
        |  }
        |}

        |GraphStructure {
        |   (s:AttributePOC.BrinsonAttribute)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfBrinsonAttribute`/`交易`),
        |   (o1:AttributePOC.BrinsonAttribute)-[p2:factorValue]->(o)
        |}
        |Rule {
        |  }
        |Action {
        |  get(s.id)
        |}""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "AttributePOC.TracebackDay" -> Set.apply("id"),
      "AttributePOC.BrinsonAttribute" -> Set.apply("id", "factorValue", "factorType"),
      "AttributePOC.TaxonomyOfBrinsonAttribute" -> Set.apply("id", "tmp"),
      "AttributePOC.TracebackDay_day_AttributePOC.BrinsonAttribute" -> Set.empty,
      "AttributePOC.BrinsonAttribute_factorValue_AttributePOC.BrinsonAttribute" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    try {
      val rst = session.plan(
        dsl,
        Map
          .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true))
          .asInstanceOf[Map[String, Object]])
    } catch {
      case ex: SchemaException =>
        ex.getMessage.contains("BelongTo find conflict") should equal(true)
    }

  }

  it("test sub query depends with belongTo parse") {
    val dsl =
      """
        |Define (u2:AttributePOC.BrinsonAttribute)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfBrinsonAttribute`/`交易`) {
        |  GraphStructure {
        |    (s:AttributePOC.BrinsonAttribute)-[p1:factorValue]->(u1:AttributePOC.BrinsonAttribute)
        |    (s)-[p2:factorValue]->(u2)
        |    (s)-[p3:factorValue]->(u3:AttributePOC.BrinsonAttribute)
        |  }
        |
        |  Rule {
        |    R1: u1.factorType == "cluster"
        |    R2: u2.factorType == "trade"
        |    R3: u3.factorType == "stock"
        |    R6: s.factorType == "total"
        |    v = (u1.factorValue/ s.factorValue + u2.factorValue / s.factorValue)
        |    R4("必须大于50%"): v > 0.5
        |    R5("交易收益大于选股"): u2.factorValue > u3.factorValue
        |  }
        |}

        |GraphStructure {
        |   (s:AttributePOC.BrinsonAttribute)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfBrinsonAttribute`/`交易`)
        |}
        |Rule {
        |  }
        |Action {
        |  get(s.id)
        |}""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "AttributePOC.TracebackDay" -> Set.apply("id"),
      "AttributePOC.BrinsonAttribute" -> Set.apply("id", "factorValue", "factorType"),
      "AttributePOC.TaxonomyOfBrinsonAttribute" -> Set.apply("id", "tmp"),
      "AttributePOC.TracebackDay_day_AttributePOC.BrinsonAttribute" -> Set.empty,
      "AttributePOC.BrinsonAttribute_factorValue_AttributePOC.BrinsonAttribute" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (join: Join[EmptyRDG], cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(1)
  }

  it("test sub query depends with catalog") {
    val dsl =
      """
        |Define (u2:AttributePOC.BrinsonAttribute)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfBrinsonAttribute`/`交易`) {
        |  GraphStructure {
        |    (s:AttributePOC.BrinsonAttribute)-[p1:factorValue]->(u1:AttributePOC.BrinsonAttribute)
        |    (s)-[p2:factorValue]->(u2)
        |    (s)-[p3:factorValue]->(u3:AttributePOC.BrinsonAttribute)
        |  }
        |
        |  Rule {
        |    R1: u1.factorType == "cluster"
        |    R2: u2.factorType == "trade"
        |    R3: u3.factorType == "stock"
        |    R6: s.factorType == "total"
        |    v = (u1.factorValue/ s.factorValue + u2.factorValue / s.factorValue)
        |    R4("必须大于50%"): v > 0.5
        |    R5("交易收益大于选股"): u2.factorValue > u3.factorValue
        |  }
        |}
        |Define (u1:AttributePOC.BrinsonAttribute)-[p:belongTo]->(o:`AttributePOC.TaxonomyOfBrinsonAttribute`/`市场`) {
        |  GraphStructure {
        |    (s:AttributePOC.BrinsonAttribute)-[p1:factorValue]->(u1:AttributePOC.BrinsonAttribute)
        |  }
        |
        |  Rule {
        |    R1: u1.factorType == "market"
        |    R2: s.factorType == "total"
        |    v = (u1.factorValue/ s.factorValue)
        |    R4("必须大于50%"): v > 0.5
        |
        |  }
        |}
        |Define (s: AttributePOC.TracebackDay)-[p: trade]->(o: Float) {
        |GraphStructure {
        |   (s)-[:day]->(f: AttributePOC.BrinsonAttribute)-[:factorValue]->(u1:`AttributePOC.TaxonomyOfBrinsonAttribute`/`交易`)
        |}
        |Rule {
        |    o = u1.factorValue
        |}
        |}
        |Define (s: AttributePOC.TracebackDay)-[p: market]->(o: Float) {
        |GraphStructure {
        |   (s)-[:day]->(f: AttributePOC.BrinsonAttribute)-[:factorValue]->(u1:`AttributePOC.TaxonomyOfBrinsonAttribute`/`市场`)
        |}
        |Rule {
        |    o = u1.factorValue
        |}
        |}
        |Define (s: AttributePOC.TracebackDay)-[p: res]->(o: Text) {
        |GraphStructure {
        |   (s)
        |}
        |Rule {
        |    str2 = rule_value(s.trade == null, "", concat("交易", "->", s.trade))
        |    str3 = concat(str2, rule_value(s.market == null, "", concat("市场", "->", s.market)))
        |    o = str3
        |}
        |}
        |GraphStructure {
        |   (s: AttributePOC.TracebackDay)
        |}
        |Rule {
        |  }
        |Action {
        |  get(s.res)
        |}""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "AttributePOC.TracebackDay" -> Set.apply("id"),
      "AttributePOC.BrinsonAttribute" -> Set.apply("id", "factorValue", "factorType"),
      "AttributePOC.TaxonomyOfBrinsonAttribute" -> Set.apply("id"),
      "AttributePOC.TracebackDay_day_AttributePOC.BrinsonAttribute" -> Set.empty,
      "AttributePOC.BrinsonAttribute_factorValue_AttributePOC.BrinsonAttribute" -> Set.empty,
      "AttributePOC.BrinsonAttribute_belongTo_AttributePOC.TaxonomyOfBrinsonAttribute" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (join: Join[EmptyRDG], cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(5)
  }

  it("test sub query add predicate with catalog") {
    val dsl =
      """
        |GraphStructure {
        |    (s:OpenSource.App)-[p:appReleaser]->(o:OpenSource.LegalPerson)
        |}
        |Rule {
        |
        |}
        |Action {
        |    get(o.name)
        |}
        |""".stripMargin
    val catalog = new KGCatalog(638000139L, getKgSchemaConnectionInfo())
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (join: Join[EmptyRDG], cnt) => cnt.sum + 1
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
    val schema: Map[String, Set[String]] = Map.apply(
      "FilmPerson" -> Set.apply("name"),
      "FilmDirector" -> Set.apply("name"),
      "FilmPerson_test_FilmDirector" -> Set.empty,
      "FilmDirector_t1_FilmDirector" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply(
          (Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true),
          (Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (join: Join[EmptyRDG], cnt) => cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(5)
  }

  it("test agg count") {
    val dsl =
      """
        |GraphStructure {
        |        (s:AMLz50.Userinfo)-[:expand_linked_alipay_id(s.id)]->(B:CustFundKG.Yeb)-[t:transfer]->(C:CustFundKG.Yeb)
        |}
        |Rule {
        |  R1("90天以内"):date_diff(from_unix_time(now(), 'yyyyMMdd'),from_unix_time(t.payDate, 'yyyyMMdd')) <= 90
        |  R2("转账付款附言含赌博关键词"):t.bizComment rlike "(赌博)|(赌)|(上分)|(下分)|(输)|(赢)|(福利)|(好运)|(反水)"
        |  transnum = group(s).countIf(R1 and R2, t)
        |  o = rule_value(transnum > 0, true, false)
        |      }
        |Action {
        |	get(s.id, o, transnum)
        |}
        |""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "AMLz50.Userinfo" -> Set.empty,
      "CustFundKG.Yeb" -> Set.empty,
      "CustFundKG.Yeb_transfer_CustFundKG.Yeb" -> Set.apply("payDate", "bizComment"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply(
          (Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true),
          (Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (agg: Aggregate[EmptyRDG], cnt) =>
        val sum = agg.aggregations.map(a => {
          a._2 match {
            case AggIfOpExpr(_,
            BinaryOpExpr(BAnd, BinaryOpExpr(_, _, _), BinaryOpExpr(_, _, _))) =>
              1
            case _ => 0
          }
        }).sum
        cnt.sum + sum
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(1)
  }


  it("test id push down") {
    val dsl =
      """
        |
        |GraphStructure {
        |  A [User, __start__='true']
        |  B,C [User]
        |  A->B [trans] as p1
        |  B->C [trans] as p2
        |  C->A [trans] as p3
        |  }
        |Rule {
        |R1: A.id in $idSet1
        |R2: B.id in $idSet2
        |R3: C.id in $idSet2
        |}
        |Action {
        |    get(A.id, B.id, C.id)
        |}
        |""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "User" -> Set.apply("id"),
      "User_trans_User" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (expandInto: ExpandInto[EmptyRDG], cnt) =>
        var ele = expandInto.pattern.getNode("B")
        if (ele == null) {
          ele = expandInto.pattern.getNode("C")
        }
        ele.rule should equal(null)
        cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(2)
  }

//  it("test id push down 2") {
//    val dsl =
//      """
//        |
//        |GraphStructure {
//        |  A [User, __start__='true']
//        |  B,C [User]
//        |  A->B [trans] as p1
//        |  B->C [trans] as p2
//        |  C->A [trans] as p3
//        |  }
//        |Rule {
//        |R1: A.id in $idSet1
//        |R2: B.id in $idSet2
//        |R3: C.id in $idSet2
//        |}
//        |Action {
//        |    get(A.id, C.id)
//        |}
//        |""".stripMargin
//    val schema: Map[String, Set[String]] = Map.apply(
//      "User" -> Set.apply("id"),
//      "User_trans_User" -> Set.empty)
//    val catalog = new PropertyGraphCatalog(schema)
//    catalog.init()
//    val session = new EmptySession(new KgDslParser(), catalog)
//    val rst = session.plan(
//      dsl,
//      Map
//        .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true),
//          (Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true))
//        .asInstanceOf[Map[String, Object]])
//    val cnt = rst.head.transform[Int] {
//      case (expandInto: ExpandInto[EmptyRDG], cnt) =>
//        var ele = expandInto.pattern.getNode("B")
//        (ele != null) should equal(true)
//        ele.rule should equal(null)
//        cnt.sum + 1
//      case (_, cnt) =>
//        if (cnt.isEmpty) {
//          0
//        } else {
//          cnt.sum
//        }
//    }
//    cnt should equal(1)
//  }
//
//  it("test id push down 3") {
//    val dsl =
//      """
//        |
//        |GraphStructure {
//        |  A [User, __start__='true']
//        |  B,C [User]
//        |  A->B [trans] as p1
//        |  B->C [trans] as p2
//        |  C->A [trans] as p3
//        |  }
//        |Rule {
//        |R1: A.id in $idSet1
//        |R2: B.id in $idSet2
//        |R3: C.id in $idSet2
//        |}
//        |Action {
//        |    get(A.id, C.id)
//        |}
//        |""".stripMargin
//    val schema: Map[String, Set[String]] = Map.apply(
//      "User" -> Set.apply("id"),
//      "User_trans_User" -> Set.empty)
//    val catalog = new PropertyGraphCatalog(schema)
//    catalog.init()
//    val session = new EmptySession(new KgDslParser(), catalog)
//    val rst = session.plan(
//      dsl,
//      Map
//        .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true),
//          (Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true))
//        .asInstanceOf[Map[String, Object]])
//    val cnt = rst.head.transform[Int] {
//      case (expandInto: ExpandInto[EmptyRDG], cnt) =>
//        var ele = expandInto.pattern.getNode("C")
//        (ele != null) should equal(true)
//        ele.rule should equal(null)
//        cnt.sum + 1
//      case (_, cnt) =>
//        if (cnt.isEmpty) {
//          0
//        } else {
//          cnt.sum
//        }
//    }
//    cnt should equal(1)
//  }


  it("test id push down 4") {
    val dsl =
      """
        |
        |GraphStructure {
        |  A [User, __start__='true']
        |  B,C [User]
        |  A->B [trans] as p1
        |  B->C [trans] as p2
        |  C->A [trans] as p3
        |  }
        |Rule {
        |R1: A.id in $idSet1
        |}
        |Action {
        |    get(A.id, C.id)
        |}
        |""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "User" -> Set.apply("id"),
      "User_trans_User" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true),
          (Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (expandInto: ExpandInto[EmptyRDG], cnt) =>
        var ele = expandInto.pattern.getNode("C")
        (ele != null) should equal(true)
        ele.rule should equal(null)
        cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(1)
  }


  it("test schema absent test") {
    val dsl =
      """
        |
        |GraphStructure {
        |  A [User, __start__='true']
        |  B,C [User]
        |  A->B [trans] as p1
        |  B->C [trans] as p2
        |  C->A [test] as p3
        |  }
        |Rule {
        |R1: A.id in $idSet1
        |}
        |Action {
        |    get(A.id, C.id)
        |}
        |""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "User" -> Set.apply("id"),
      "User_trans_User" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    try {
      session.plan(
        dsl,
        Map
          .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true),
            (Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true))
          .asInstanceOf[Map[String, Object]])
    } catch {
      case ex: SchemaException =>
        println(ex.getMessage)
        ex.getMessage.contains("Cannot find p3 types in") should equal(true)
    }
  }

  it("test schema absent test2") {
    val dsl =
      """
        |
        |GraphStructure {
        |  A [User, __start__='true']
        |  B,C [User]
        |  D [TEST]
        |  A->B [trans] as p1
        |  B->C [trans] as p2
        |  C->A [trans] as p3
        |  }
        |Rule {
        |R1: A.id in $idSet1
        |}
        |Action {
        |    get(A.id, C.id)
        |}
        |""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "User" -> Set.apply("id"),
      "User_trans_User" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new KgDslParser(), catalog)
    try {
      session.plan(
        dsl,
        Map
          .apply((Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true),
            (Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true))
          .asInstanceOf[Map[String, Object]])
    } catch {
      case ex: SchemaException =>
        println(ex.getMessage)
        ex.getMessage.contains("Cannot find IRNode(D,Set()).name") should equal(true)
    }
  }
}
