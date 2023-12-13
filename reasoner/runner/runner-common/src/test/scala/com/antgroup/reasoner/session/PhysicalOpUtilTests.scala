package com.antgroup.reasoner.session

import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog
import com.antgroup.openspg.reasoner.lube.physical.util.PhysicalOperatorUtil
import com.antgroup.openspg.reasoner.parser.KgDslParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class PhysicalOpUtilTests extends AnyFunSpec {
  it("test physical operator utils") {
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
    PhysicalOperatorUtil.getStartTypes(rst.head) should equal (Set.apply("User"))
  }
}
