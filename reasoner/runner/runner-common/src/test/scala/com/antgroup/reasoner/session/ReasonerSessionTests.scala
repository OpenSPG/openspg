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

import scala.collection.mutable.ListBuffer

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.exception.SchemaException
import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog
import com.antgroup.openspg.reasoner.lube.common.expr.{AggIfOpExpr, BAnd, BinaryOpExpr}
import com.antgroup.openspg.reasoner.lube.common.graph.IRGraph
import com.antgroup.openspg.reasoner.lube.logical.optimizer.LogicalOptimizer
import com.antgroup.openspg.reasoner.lube.logical.planning.{LogicalPlanner, LogicalPlannerContext}
import com.antgroup.openspg.reasoner.lube.physical.operators._
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG
import com.antgroup.openspg.reasoner.parser.OpenSPGDslParser
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner
import com.antgroup.openspg.reasoner.util.LoaderUtil
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{contain, convertToAnyShouldWrapper, equal}

class ReasonerSessionTests extends AnyFunSpec {

  // scalastyle:off
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
        |    R1('LivedInHangzhou'): aa1.id == '中国-浙江省-杭州市'
        |  	R2('CommutersAtWeekdays'): dayOfWeek(te.eventTime) in [1, 2, 3, 4, 5]
        |            and hourOfDay(te.eventTime) in [6, 7, 8, 9, 10, 17, 18, 19, 20, 21]
        |    R3('BusAndSubway'): tm.id in ['bus', 'subway']
        |    tmCount('NumberOfTrips') = group(user).count(te.id)
        |    R4('TripsOver3'): tmCount >= 3
        |    R5('idNotNull'): user.id != ''
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
    val schema: Map[String, Set[String]] = Map.apply(
      "TuringCore.AlipayUser" -> Set.apply("id"),
      "CKG.AdministrativeArea" -> Set.apply("id"),
      "TuringCore.TravelEvent" -> Set.apply("eventTime", "id"),
      "TuringCore.TravelMode" -> Set.apply("id"),
      "TuringCore.AlipayUser_workLoc_CKG.AdministrativeArea" -> Set.empty,
      "TuringCore.TravelEvent_traveler_TuringCore.AlipayUser" -> Set.empty,
      "TuringCore.TravelEvent_travelMode_TuringCore.TravelMode" -> Set.empty,
      "TuringCore.TravelEvent_travelEndpoint_CKG.AdministrativeArea" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new OpenSPGDslParser(), catalog)
    val rst = session.plan(dsl, Map.empty)
    rst.foreach(session.getResult(_))
    val node = catalog.getGraph(IRGraph.defaultGraphName).getNode("TuringCore.AlipayUser")
    node.properties.map(_.name) should contain("teCount")
  }

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
    val session = new EmptySession(new OpenSPGDslParser(), catalog)
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
    val session = new EmptySession(new OpenSPGDslParser(), catalog)
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
    forwardDirectionPhysicalOpOrder should equal(
      "Start,Cache,DrivingRDG,PatternScan,LinkedExpand,ExpandInto,ExpandInto,Drop,Filter,Drop,DDL,Join,PatternScan,ExpandInto,Drop,Select")
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
    val session = new EmptySession(new OpenSPGDslParser(), catalog)
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
    physicalOpOrder should equal(
      "Start,Cache,DrivingRDG,PatternScan,LinkedExpand,ExpandInto,ExpandInto,Drop,Filter,Drop,DDL,Join,PatternScan,Cache,DrivingRDG,PatternScan,LinkedExpand,ExpandInto,ExpandInto,Drop,Filter,Drop,DDL,Join,ExpandInto,ExpandInto,Drop,Filter,Drop,Select")
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
    val session = new EmptySession(new OpenSPGDslParser(), catalog)
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
    val session = new EmptySession(new OpenSPGDslParser(), catalog)
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
    val parser = new OpenSPGDslParser()
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
    val session = new EmptySession(new OpenSPGDslParser(), catalog)
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
    val session = new EmptySession(new OpenSPGDslParser(), catalog)
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
    val session = new EmptySession(new OpenSPGDslParser(), catalog)
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
    cnt should equal(6)
  }

  it("test concept check") {
    val dsl =
      """
        |MATCH (s:`SupplyChain.Industry`/`商贸-资本品商贸`)
        |RETURN s.id, s.name
        |""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "SupplyChain.Industry" -> Set.apply("id", "name"),
      "SupplyChain.Product" -> Set.apply("id", "name"),
      "SupplyChain.Product_belongTo_SupplyChain.Industry" -> Set.apply("payDate", "bizComment"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new OpenSPGDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply(
          (Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true),
          (Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (expand: ExpandInto[EmptyRDG], cnt) =>
        cnt.sum + 1
      case (pattern: PatternScan[EmptyRDG], cnt) =>
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


  it("test concept check 2") {
    val dsl =
      """
        |MATCH
        |    (u:`RiskMining.TaxOfRiskUser`/`赌博App开发者`)-[:developed]->(app:`RiskMining.TaxOfRiskApp`/`赌博应用`),
        |    (b:`RiskMining.TaxOfRiskUser`/`赌博App老板`)-[:release]->(app)
        |RETURN
        |    u.id, b.id ,app.id""".stripMargin
    val schema: Map[String, Set[String]] = Map.apply(
      "RiskMining.TaxOfRiskUser" -> Set.apply("id", "name"),
      "RiskMining.TaxOfRiskApp" -> Set.apply("id", "name"),
      "RiskMining.User" -> Set.apply("id", "name"),
      "RiskMining.App" -> Set.apply("id", "name"),
      "RiskMining.User_release_RiskMining.App" -> Set.apply("payDate", "bizComment"),
      "RiskMining.User_developed_RiskMining.App" -> Set.apply("payDate", "bizComment"),
      "RiskMining.User_belongTo_RiskMining.TaxOfRiskUser" -> Set.apply("payDate", "bizComment"),
      "RiskMining.App_belongTo_RiskMining.TaxOfRiskApp" -> Set.apply("payDate", "bizComment"))
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new OpenSPGDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply(
          (Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true),
          (Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (expand: ExpandInto[EmptyRDG], cnt) =>
        cnt.sum + 1
      case (pattern: PatternScan[EmptyRDG], cnt) =>
        cnt.sum + 1
      case (_, cnt) =>
        if (cnt.isEmpty) {
          0
        } else {
          cnt.sum
        }
    }
    cnt should equal(6)
  }


  it("test agg count") {
    val dsl =
      """
        |GraphStructure {
        |        (s:AMLz50.Userinfo)-[:expand_linked_alipay_id(s.id)]->(B:CustFundKG.Yeb)-[t:transfer]->(C:CustFundKG.Yeb)
        |}
        |Rule {
        |  R1:date_diff(from_unix_time(now(), 'yyyyMMdd'),from_unix_time(t.payDate, 'yyyyMMdd')) <= 90
        |  R2:t.bizComment rlike "(赌博)|(赌)|(上分)|(下分)|(输)|(赢)|(福利)|(好运)|(反水)"
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
    val session = new EmptySession(new OpenSPGDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply(
          (Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true),
          (Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true))
        .asInstanceOf[Map[String, Object]])
    val cnt = rst.head.transform[Int] {
      case (agg: Aggregate[EmptyRDG], cnt) =>
        val sum = agg.aggregations
          .map(a => {
            a._2 match {
              case AggIfOpExpr(
                    _,
                    BinaryOpExpr(BAnd, BinaryOpExpr(_, _, _), BinaryOpExpr(_, _, _))) =>
                1
              case _ => 0
            }
          })
          .sum
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
    val schema: Map[String, Set[String]] =
      Map.apply("User" -> Set.apply("id"), "User_trans_User" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new OpenSPGDslParser(), catalog)
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
    val schema: Map[String, Set[String]] =
      Map.apply("User" -> Set.apply("id"), "User_trans_User" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new OpenSPGDslParser(), catalog)
    val rst = session.plan(
      dsl,
      Map
        .apply(
          (Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true),
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
    val schema: Map[String, Set[String]] =
      Map.apply("User" -> Set.apply("id"), "User_trans_User" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new OpenSPGDslParser(), catalog)
    try {
      session.plan(
        dsl,
        Map
          .apply(
            (Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true),
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
    val schema: Map[String, Set[String]] =
      Map.apply("User" -> Set.apply("id"), "User_trans_User" -> Set.empty)
    val catalog = new PropertyGraphCatalog(schema)
    catalog.init()
    val session = new EmptySession(new OpenSPGDslParser(), catalog)
    try {
      session.plan(
        dsl,
        Map
          .apply(
            (Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true),
            (Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true))
          .asInstanceOf[Map[String, Object]])
    } catch {
      case ex: SchemaException =>
        println(ex.getMessage)
        ex.getMessage.contains("Cannot find IRNode(D,Set()).name") should equal(true)
    }
  }
}
