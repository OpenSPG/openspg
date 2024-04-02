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

package com.antgroup.openspg.reasoner.parser

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.exception.{
  KGDSLGrammarException,
  KGDSLInvalidTokenException,
  KGDSLOneTaskException
}
import com.antgroup.openspg.reasoner.lube.block._
import com.antgroup.openspg.reasoner.lube.common.expr._
import com.antgroup.openspg.reasoner.lube.common.graph._
import com.antgroup.openspg.reasoner.lube.common.pattern.{EntityElement, LinkedPatternConnection}
import com.antgroup.openspg.reasoner.lube.common.rule.ProjectRule
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers.contain
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class OpenSPGDslParserTest extends AnyFunSpec {
  val parser = new OpenSPGDslParser()
  it("test return edge or node") {
    val dsl = """GraphStructure {
                |  A [Film]}
                |Rule {
                |  R1('下沉到数据加载'): 'c54e6f7dd4dacc1ac5b0fa66565a4a60' == A.id
                |}
                |Action {
                |  get(A.id, A.__property_json__, A, __path__)
                |}""".stripMargin
    val blocks = parser.parseMultipleStatement(dsl, Map.apply(Constants.START_ALIAS -> "o"))
    val block = blocks.head
    print(block.pretty)
    block.isInstanceOf[TableResultBlock] should equal(true)
    block.asInstanceOf[TableResultBlock].asList.size should equal(4)
    block
      .asInstanceOf[TableResultBlock]
      .selectList
      .fields(2)
      .isInstanceOf[IRProperty] should equal(true)
    block
      .asInstanceOf[TableResultBlock]
      .selectList
      .fields(2)
      .asInstanceOf[IRProperty]
      .field should equal(Constants.PROPERTY_JSON_KEY)
  }
  it("test gql 0") {
    val dsl = """MATCH (s)-[]->(o) RETURN s.id, o.id"""
    val blocks = parser.parseMultipleStatement(dsl, Map.apply(Constants.START_ALIAS -> "o"))
    val block = blocks.head
    print(block.pretty)
    block.isInstanceOf[TableResultBlock] should equal(true)
    block.asInstanceOf[TableResultBlock].asList.size should equal(2)
    block.dependencies.head.isInstanceOf[MatchBlock] should equal(true)
    block.dependencies.head
      .asInstanceOf[MatchBlock]
      .patterns("unresolved_default_path")
      .graphPattern
      .rootAlias should equal("o")
  }

  it("test gql 1") {
    val dsl = """MATCH (s)-[]->(o) RETURN s.id, o.id"""
    val block = parser.parse(dsl)
    print(block.pretty)
    block.isInstanceOf[TableResultBlock] should equal(true)
    block.asInstanceOf[TableResultBlock].asList.size should equal(2)
  }

  it("test gql 2") {
    val dsl = """MATCH (s)-[]->(o) WHERE s.id = 1 RETURN s.id, o.id"""
    val block = parser.parse(dsl)
    print(block.pretty)
    block.isInstanceOf[TableResultBlock] should equal(true)
    block.asInstanceOf[TableResultBlock].asList.size should equal(2)
    block.dependencies.head.isInstanceOf[FilterBlock] should equal(true)
    block.dependencies.head.asInstanceOf[FilterBlock].rules.getExpr should equal(
      BinaryOpExpr(BEqual, UnaryOpExpr(GetField("id"), Ref("s")), VLong("1")))
  }

  it("test gql 3") {
    val dsl =
      """MATCH (s)-[]->(o),(o)-[]->(p1)
        |WHERE s.id > o.id
        |RETURN s.id As s_id, o.id, p1.id""".stripMargin

    val block = parser.parse(dsl)
    print(block.pretty)
    block.isInstanceOf[TableResultBlock] should equal(true)
    block.asInstanceOf[TableResultBlock].asList.size should equal(3)
  }

  it("test gql 4") {
    val dsl = """MATCH (s:`OpenSource.TaxonomyOfApp`/`赌博APP`) RETURN s.id"""

    val block = parser.parse(dsl)
    print(block.pretty)
    block.isInstanceOf[TableResultBlock] should equal(true)
    block.asInstanceOf[TableResultBlock].asList.size should equal(1)
    block.dependencies.head.isInstanceOf[MatchBlock] should equal(true)
    block.dependencies.head
      .asInstanceOf[MatchBlock]
      .patterns
      .size should equal(1)
    block.dependencies.head
      .asInstanceOf[MatchBlock]
      .patterns
      .head
      ._2
      .graphPattern
      .nodes("s")
      .isInstanceOf[EntityElement] should equal(true)
  }

  it("test spg concept label") {
    val dsl = """Structure {
                |	o1 [`OpenSource.TaxonomyOfApp`/`赌博APP`, __start__='true']
                |}
                |Constraint {
                |}
                |Action {
                |	get(o1.id)
                |}""".stripMargin

    val block = parser.parse(dsl)
    print(block.pretty)
  }

  it("test concept label") {
    val dsl = """GraphStructure {
                |	o1 [`OpenSource.TaxonomyOfApp`/`赌博APP`, __start__='true']
                |}
                |Rule {
                |}
                |Action {
                |	get(o1.id)
                |}""".stripMargin

    val block = parser.parse(dsl)
    print(block.pretty)
  }

  it("test exception") {
    val dsl = """Define(s:OpenSource.App) -[p:appReleaser]-> (o:OpenSource.LegalPerson) {
                |    GraphStructure {  (o) -[p1:releaseApp]-> (s) }
                |    Rule { }
                |}
                |
                |Define(s:OpenSource.App) -[p:actualOwner]-> (o:OpenSource.Person) {
                |    GraphStructure {
                |        s -[p1:appReleaser]-> (o1:OpenSource.LegalPerson) -[p2:shareHolder]-> (o)
                |    }
                |    Rule {
                |        R1 : p2.shareHoldingRatio > 0.5
                |    }
                |}
                |
                |
                |GraphStructure {
                |    (s:OpenSource.App) -[p:actualOwner]-> (o:OpenSource.Person)
                |}
                |Rule {}
                |Action {
                |    get(s.id, o.id)
                |}""".stripMargin

    try {
      parser.parseMultipleStatement(dsl)
    } catch {
      case ex: KGDSLInvalidTokenException =>
        ex.getMessage.contains("mismatched input") should equal(true)
    }
  }

  it("opChainTest") {
    val chain = OpChainExpr(Filter(BinaryOpExpr(BNotEqual, Ref("a"), Ref("b"))), null)

    val case1 =
      parser.parseOpChain2Block(chain, IRVariable("abc"), null, null, KG(Map.empty, Map.empty))
    case1.isInstanceOf[FilterBlock] should equal(true)

    val case2Chain = OpChainExpr(ListOpExpr(Get(1), Ref("a")), null)
    val case2 = parser.parseOpChain2Block(
      case2Chain,
      IRVariable("abc"),
      null,
      null,
      KG(Map.empty, Map.empty))
    case2.isInstanceOf[ProjectBlock] should equal(true)

    val case3Chain = OpChainExpr(
      AggIfOpExpr(AggOpExpr(Count, Ref("a")), BinaryOpExpr(BGreaterThan, Ref("a"), VLong("1"))),
      null)
    val case3 = parser.parseOpChain2Block(
      case3Chain,
      IRVariable("abc"),
      null,
      null,
      KG(Map.empty, Map.empty))
    case3.isInstanceOf[ProjectBlock] should equal(true)

    val case4Chain = OpChainExpr(AggOpExpr(Count, Ref("a")), null)
    val case4 = parser.parseOpChain2Block(
      case4Chain,
      IRVariable("abc"),
      null,
      null,
      KG(Map.empty, Map.empty))
    case4.isInstanceOf[ProjectBlock] should equal(true)

    val case5Chain = OpChainExpr(Filter(BinaryOpExpr(BNotEqual, Ref("a"), Ref("b"))), null)
    val groupAgg =
      GraphAggregatorExpr("unresolved_default_path", List.apply(Ref("A"), Ref("B")), null)
    val case5 = parser.parseOpChain2Block(
      case5Chain,
      IRVariable("abc"),
      groupAgg,
      null,
      KG(Map.empty, Map.empty))
    case5.isInstanceOf[FilterBlock] should equal(true)

  }

  it("opChain test6") {
    val case6Chain = OpChainExpr(AggOpExpr(Count, Ref("a")), null)
    val groupAgg2 =
      GraphAggregatorExpr("unresolved_default_path", List.apply(Ref("A"), Ref("B")), null)
    try {
      val case6 =
        parser.parseOpChain2Block(case6Chain, null, groupAgg2, null, KG(Map.empty, Map.empty))
      true should equal(false)
    } catch {
      case ex: KGDSLGrammarException =>
        ex.getMessage
          .contains("AggregationBlock generated left variable is null") should equal(true)
    }
  }

  // TODO: add new unit tests, since this case A, B is unknown
//  it("opChain test7") {
//    val caseChain = OpChainExpr(AggOpExpr(Count, Ref("a")), null)
//    val groupAgg =
//      GraphAggregatorExpr("unresolved_default_path", List.apply(Ref("A"), Ref("B")), null)
//    val case1 = parser.parseOpChain2Block(
//      caseChain,
//      null,
//      groupAgg,
//      null,
//      KG(Map.apply("a" -> IRNode("a", Set.empty)), Map.empty))
//    case1.isInstanceOf[AggregationBlock] should equal(true)
//    case1
//      .asInstanceOf[AggregationBlock]
//      .aggregations
//      .pairs
//      .contains(IRNode("a", Set.empty)) should equal(true)
//  }
//
//  it("opChain test8") {
//    val caseChain = OpChainExpr(AggOpExpr(Count, Ref("a")), null)
//    val groupAgg =
//      GraphAggregatorExpr("unresolved_default_path", List.apply(Ref("A"), Ref("B")), null)
//    val case1 = parser.parseOpChain2Block(
//      caseChain,
//      null,
//      groupAgg,
//      null,
//      KG(Map.empty, Map.apply("a" -> IREdge("a", Set.empty))))
//    case1.isInstanceOf[AggregationBlock] should equal(true)
//    case1
//      .asInstanceOf[AggregationBlock]
//      .aggregations
//      .pairs
//      .contains(IREdge("a", Set.empty)) should equal(true)
//  }

  it("addproperies1") {
    val dsl = """Define (s:DomainFamily)-[p:totalText]->(o:Text) {
                |    GraphStructure {
                |        (s)<-[:belong]-(d:Domain)
                |    }
                |    Rule {
                |        o = "abc"
                |    }
                |}""".stripMargin

    val block = parser.parse(dsl)
    print(block.pretty)
    block.dependencies.head.isInstanceOf[ProjectBlock] should equal(true)
    val proj = block.dependencies.head.asInstanceOf[ProjectBlock]
    proj.projects.items.head._2 should equal(ProjectRule(IRProperty("s", "totalText"), Ref("o")))
  }

  it("addproperies with constraint") {
    val dsl = """Define (s:DomainFamily)-[p:totalText]->(o:Text) {
                |    Structure {
                |        (s)<-[:belong]-(d:Domain)
                |    }
                |    Constraint {
                |        o = "abc"
                |    }
                |}""".stripMargin

    val block = parser.parse(dsl)
    print(block.pretty)
    block.dependencies.head.isInstanceOf[ProjectBlock] should equal(true)
    val proj = block.dependencies.head.asInstanceOf[ProjectBlock]
    proj.projects.items.head._2 should equal(ProjectRule(IRProperty("s", "totalText"), Ref("o")))
  }

  it("addproperies2") {
    val dsl = """Define (s:DomainFamily)-[p:total_domain_num]->(o:Integer) {
                |    GraphStructure {
                |        (s)<-[:belong]-(d:Domain)
                |    }
                |    Rule {
                |        num = group(s).count(d)
                |        o = num
                |    }
                |}""".stripMargin

    val block = parser.parse(dsl)
    print(block.pretty)
    block.dependencies.head.isInstanceOf[ProjectBlock] should equal(true)
    val proj = block.dependencies.head.asInstanceOf[ProjectBlock]
    proj.projects.items.head._2 should equal(
      ProjectRule(IRProperty("s", "total_domain_num"), Ref("o")))
  }
  it("addproperies") {
    val dsl = """Define (s:DomainFamily)-[p:total_domain_num]->(o:Int) {
                |    GraphStructure {
                |        (s)<-[:belong]-(d:Domain)
                |    }
                |    Rule {
                |        num = group(s).count(d)
                |        o = num
                |    }
                |}""".stripMargin

    val block = parser.parse(dsl)
    print(block.pretty)
    block.dependencies.head.isInstanceOf[ProjectBlock] should equal(true)
    val proj = block.dependencies.head.asInstanceOf[ProjectBlock]
    proj.projects.items.head._2 should equal(
      ProjectRule(IRProperty("s", "total_domain_num"), Ref("o")))
  }
  it("addNode") {
    val dsl = """Define (s:DomainFamily)-[p:total_domain_num]->(o:Int) {
                |    GraphStructure {
                |        (s)<-[:belong]-(d:Domain)
                |    }
                |    Rule {
                |        num = group(s).count(d)
                |        o = num
                |    }
                |    Action {
                |          	a = createNodeInstance(
                |    	        type=o,
                |    	        value={
                |    		        主体 = o2.行业
                |    		        客体 = `影响/正面影响`
                |    		        时间 = e.发生时间
                |    		      空间 = e.发生空间
                |             }
                |           )
                |    createEdgeInstance(
                |        src=a,
                |        dst=s,
                |        type=`abcde`,
                |        value={
                |        }
                |    )
                |    }
                |}""".stripMargin

    val block = parser.parse(dsl)
    print(block.pretty)
    block.dependencies.head.isInstanceOf[ProjectBlock] should equal(true)
    block.asInstanceOf[DDLBlock].ddlOp.size should equal(3)
    block.asInstanceOf[DDLBlock].ddlOp.head.isInstanceOf[AddProperty] should equal(true)
  }

  it("addNodeException") {
    val dsl = """Define (s:DomainFamily)-[p:total_domain_num]->(o:Int) {
                |    GraphStructure {
                |        (s)<-[:belong]-(d:Domain)
                |    }
                |    Rule {
                |        num = group(s).count(d)
                |        o = num
                |    }
                |    Action {
                |    createEdgeInstance(
                |        abc=a,
                |        abc=s,
                |        abc=`abcde`,
                |        value={
                |        }
                |    )
                |    }
                |}""".stripMargin

    try {
      parser.parse(dsl)
    } catch {
      case ex: KGDSLGrammarException =>
        ex.getMessage.contains("must has type param") should equal(true)
    }
  }

  it("addNodeException with conflict") {
    val dsl =
      """Define (s: `HengSheng.TaxonomyOfCompanyAccident`/`周期性行业头部上市公司停产事故`)-[p: leadTo]->(o: `HengSheng.TaxonomyOfIndustryInfluence`/`价格上升`) {
                |    GraphStructure {
                |		(s:HengSheng.Company)-[:subject]->(c:HengSheng.Company)-[:belongIndustry]->(d:HengSheng.Industry)
                |    }
                |    Rule {
                |
                |    }
                |    Action {
                |    	createNodeInstance(
                |        	type=HengSheng.IndustryInfluence,
                |            value={
                |            	subject=d.id
                |                objectWho="上升"
                |                influenceDegree="上升"
                |                indexTag=”价格“
                |            }
                |        )
                |    }
                |}""".stripMargin

    try {
      parser.parse(dsl)
    } catch {
      case ex: KGDSLGrammarException =>
        ex.getMessage.contains("PatternElement can not merge") should equal(true)
    }
  }

  it("addNodeException with conflict2") {
    val dsl = """Define (s: HengSheng.Company)-[p: leadTo]->(o: `HengSheng.TaxonomyOfIndustryInfluence`/`价格上升`) {
                |    GraphStructure {
                |		(s:`HengSheng.TaxonomyOfCompanyAccident`/`周期性行业头部上市公司停产事故`)-[:subject]->(c:HengSheng.Company)-[:belongIndustry]->(d:HengSheng.Industry)
                |    }
                |    Rule {
                |
                |    }
                |    Action {
                |    	createNodeInstance(
                |        	type=HengSheng.IndustryInfluence,
                |            value={
                |            	subject=d.id
                |                objectWho="上升"
                |                influenceDegree="上升"
                |                indexTag="价格"
                |            }
                |        )
                |    }
                |}""".stripMargin

    try {
      parser.parse(dsl)
    } catch {
      case ex: KGDSLGrammarException =>
        ex.getMessage.contains("PatternElement can not merge") should equal(true)
    }
  }

  it("action with edge") {
    val dsl =
      """Define (s: `HengSheng.TaxonomyOfCompanyAccident`/`周期性行业头部上市公司停产事故`)-[p: leadTo]->(o: `HengSheng.TaxonomyOfIndustryInfluence`/`价格上升`) {
                |    GraphStructure {
                |		(s)-[:subject]->(c:HengSheng.Company)-[:belongIndustry]->(d),
                |  (d:`HengSheng.Industry`/`矿产业`)-[:t]->(c:Compnay)
                |    }
                |    Rule {
                |
                |    }
                |    Action {
                |    	createNodeInstance(
                |        	type=HengSheng.IndustryInfluence,
                |            value={
                |            	subject=d.id
                |                objectWho="上升"
                |                influenceDegree="上升"
                |                indexTag=”价格“
                |            }
                |        )
                |    }
                |}""".stripMargin

    val block = parser.parse(dsl)
    print(block.pretty)
    block.dependencies.head.isInstanceOf[MatchBlock] should equal(true)
    block.asInstanceOf[DDLBlock].ddlOp.size should equal(2)
    block.asInstanceOf[DDLBlock].ddlOp.head.isInstanceOf[AddPredicate] should equal(true)

  }

  it("addNodeException2") {
    val dsl = """Define (s:DomainFamily)-[p:total_domain_num]->(o:Int) {
                |    GraphStructure {
                |        (s)<-[:belong]-(d:Domain)
                |    }
                |    Rule {
                |        num = group(s).count(d)
                |        o = num
                |    }
                |    Action {
                |    createEdgeInstance(
                |        abc=a,
                |        abc=s,
                |        type=`abcde`,
                |        value={
                |        }
                |    )
                |    }
                |}""".stripMargin

    try {
      parser.parse(dsl)
    } catch {
      case ex: KGDSLGrammarException =>
        ex.getMessage.contains("must has src param") should equal(true)
    }
  }

  it("addNodeException3") {
    val dsl = """Define (s:DomainFamily)-[p:total_domain_num]->(o:Int) {
                |    GraphStructure {
                |        (s)<-[:belong]-(d:Domain)
                |    }
                |    Rule {
                |        num = group(s).count(d)
                |        o = num
                |    }
                |    Action {
                |    createEdgeInstance(
                |        src=a,
                |        abc=s,
                |        type=`abcde`,
                |        value={
                |        }
                |    )
                |    }
                |}""".stripMargin

    try {
      parser.parse(dsl)
    } catch {
      case ex: KGDSLGrammarException =>
        ex.getMessage.contains("must has dst param") should equal(true)
    }
  }

  it("addNodeException4") {
    val dsl = """Define (s:DomainFamily)-[p:total_domain_num]->(o:Int) {
                |    GraphStructure {
                |        (s)<-[:belong]-(d:Domain)
                |    }
                |    Rule {
                |        num = group(s).count(d)
                |        o = num
                |    }
                |    Action {
                |    createEdgeInstance(
                |        src=a,
                |        dst=s,
                |        type=`abcde`,
                |        value2={
                |        }
                |    )
                |    }
                |}""".stripMargin

    try {
      parser.parse(dsl)
    } catch {
      case ex: KGDSLGrammarException =>
        ex.getMessage.contains("must has value param") should equal(true)
    }
  }

  it("case1_ref_dsl") {
    val dsl = """// 1
                |Define (s:DomainFamily)-[p:black_relate_rate]->(o:Pkg) {
                |    GraphStructure {
                |        (o)-[:use]->(d:Domain),(d)-[belong]->(s)
                |    }
                |    Rule {
                |        R1: o.is_black == true
                |        domain_num = group(s,o).count(d)
                |        p.same_domain_num = domain_num
                |    }
                |}
                |
                |Define (s:DomainFamily)-[p:total_domain_num]->(o:Int) {
                |    GraphStructure {
                |        (s)<-[:belong]-(d:Domain)
                |    }
                |    Rule {
                |        o = group(s).count(d)
                |    }
                |}
                |
                |//
                |Define (s:Pkg)-[p:target]->(o:User) {
                |    GraphStructure {
                |        (s)<-[p1:black_relate_rate]-(df:DomainFamily),
                |        (df)<-[:belong]-(d:Domain),
                |        (o)-[visit]->(d)
                |    } Rule {
                |        visit_time = group(o, df).count(d)
                |        R1("必须大于2次"): visit_time > 1
                |        R2("必须占比大于50%"): visit_time / df.total_domain_num > 0.5
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
                |}""".stripMargin

    val blocks = parser.parseMultipleStatement(dsl)

    blocks.size should equal(4)

    for (i <- blocks.indices) {
      print(blocks(i).pretty)
      print("\n======" + i + "======\n")
    }

    blocks.head.asInstanceOf[DDLBlock].ddlOp.head.isInstanceOf[AddPredicate] should equal(true)
    blocks.head
      .asInstanceOf[DDLBlock]
      .ddlOp
      .head
      .asInstanceOf[AddPredicate]
      .predicate
      .label should equal("black_relate_rate")
    blocks.head
      .asInstanceOf[DDLBlock]
      .ddlOp
      .head
      .asInstanceOf[AddPredicate]
      .predicate
      .fields
      .keySet should contain("same_domain_num")

    blocks(1).asInstanceOf[DDLBlock].ddlOp.head.isInstanceOf[AddProperty] should equal(true)
    blocks(1)
      .asInstanceOf[DDLBlock]
      .ddlOp
      .head
      .asInstanceOf[AddProperty]
      .propertyName should equal("total_domain_num")

    blocks(2).asInstanceOf[DDLBlock].ddlOp.head.isInstanceOf[AddPredicate] should equal(true)
    blocks(2)
      .asInstanceOf[DDLBlock]
      .ddlOp
      .head
      .asInstanceOf[AddPredicate]
      .predicate
      .label should equal("target")

    blocks(3).isInstanceOf[TableResultBlock] should equal(true)
  }
  it("case1_ref_dsl2") {
    val dsl = """// 1
                |Define (s:DomainFamily)-[p:black_relate_rate]->(p:Pkg) {
                |    GraphStructure {
                |        (p)-[:use]->(d:Domain),(d)-[belong]->(s)
                |    }
                |    Rule {
                |        R1: p.is_black == true
                |        domain_num = group(s,p).count(d)
                |        p.same_domain_num = domain_num
                |    }
                |}
                |
                |Define (s:DomainFamily)-[p:total_domain_num]->(o:Int) {
                |    GraphStructure {
                |        (s)<-[:belong]-(d:Domain)
                |    }
                |    Rule {
                |        o = group(s).count(d)
                |    }
                |}
                |
                |//
                |Define (s:Pkg)-[p:target]->(o:User) {
                |    GraphStructure {
                |        (s)<-[p1:black_relate_rate]-(df:DomainFamily),
                |        (df)<-[:belong]-(d:Domain),
                |        (o)-[visit]->(d)
                |    } Rule {
                |        visit_time = group(o, df).count(d)
                |        R1("必须大于2次"): visit_time > 1
                |        R2("必须占比大于50%"): visit_time / df.total_domain_num > 0.5
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
                |}""".stripMargin

    try {
      parser.parse(dsl)
      true should equal(false)
    } catch {
      case ex: KGDSLOneTaskException => true should equal(true)
    }
  }
  it("case_parse_assis") {
    val dsl =
      """Define (s:DomainFamily)-[p:black_relate_rate]->(p:Pkg) {
        |    GraphStructure {
        |        (p)-[:use]->(d:Domain)-[belong]->(s)
        |    }
        |    Rule {
        |        R1: p.is_black == true
        |        domain_num = group(s,p).count(d)
        |        p.same_domain_num = domain_num + 1
        |    }
        |}""".stripMargin

    val block = parser.parse(dsl)
    print(block.pretty)
  }

  it("case1_get_exception") {
    val dsl =
      """
        |GraphStructure {
        |  (s:User)
        |}
        |Rule {
        |}
        |Action {
        |  get(a().name, o as b)
        |}
        |""".stripMargin

    try {
      parser.parse(dsl)
      true should equal(false)
    } catch {
      case ex: KGDSLGrammarException => true should equal(true)
    }
  }

  it("test repeat function") {
    val dsl = "GraphStructure {\n" +
      "  A [RelatedParty, __start__='true']\n" +
      "  B [RelatedParty]\n" +
      "  A->B [holdShare] repeat(1,10) as e\n" +
      "}\n" +
      "Rule {\n" +
      "  R: group(A,B).keep_shortest_path(e)\n" +
      "}\n" +
      "Action {\n" +
      "  get(A.id,B.id)  \n" +
      "}"

    val blocks = parser.parse(dsl)
    print(blocks.pretty)
  }

  it("case1_start") {
    val dsl =
      """
        |GraphStructure {
        |  A [test]
        |  D [test, __start__='true']
        |  D->C [abc] as D_C_2
        |}
        |Rule {
        |}
        |Action {
        |  get(A.id)
        |}
        |""".stripMargin

    val blocks = parser.parse(dsl)
    print(blocks.pretty)
    val blockRst = """└─TableResultBlock(selectList=OrderedFields(List(IRProperty(A,id))), asList=List(A.id), distinct=false)
                     |    └─MatchBlock(patterns=Map(unresolved_default_path -> GraphPath(unresolved_default_path,GraphPattern(D,Map(A -> (A:test), D -> (D:test)),Map(D -> Set((D)->[D_C_2:abc]-(C)))),Map(D -> Set(), A -> Set(id), D_C_2 -> Set())),false)))
                     |        └─SourceBlock(graph=KG(Map(D -> IRNode(D,Set()), A -> IRNode(A,Set(id))),Map(D_C_2 -> IREdge(D_C_2,Set()))))""".stripMargin
    blocks.pretty should equal(blockRst)
  }

  it("parse test abm benchmark") {
    val dsl = """  GraphStructure {
                |     s [CustFundKG.Account, __start__='true']
                |      inUser,inUser2,outUser [CustFundKG.Account]
                |      inUser -> s[accountFundContact] as in1
                |      inUser2 -> s[accountFundContact] as in2
                |      s -> outUser [accountFundContact] as out
                |  }
                |Rule {
                |    R1("当天同时转入"):floor(abs(ceil(date_diff(in1.transDate,in2.transDate)))) == 0
                |    o = rule_value(tranOutNum >=5, true, false)
                |  }
                |Action {
                |  get(s.id, o)
                |}""".stripMargin

    val blocks = parser.parseMultipleStatement(dsl, null)
    print(blocks.head.pretty)
    val block = blocks.head
    block.dependencies.head.dependencies.head.isInstanceOf[FilterBlock] should equal(true)
  }

  it("case1_start_func empty param") {
    val dsl =
      """
        |GraphStructure {
        |  A [test]
        |  D [test]
        |  D->C [abc] as D_C_2
        |}
        |Rule {
        |}
        |Action {
        |  get(A.id)
        |}
        |""".stripMargin

    val blocks = parser.parseMultipleStatement(dsl, null)
    print(blocks.head.pretty)
    val blockRst = """└─TableResultBlock(selectList=OrderedFields(List(IRProperty(A,id))), asList=List(A.id), distinct=false)
                     |    └─MatchBlock(patterns=Map(unresolved_default_path -> GraphPath(unresolved_default_path,GraphPattern(null,Map(A -> (A:test), D -> (D:test)),Map(D -> Set((D)->[D_C_2:abc]-(C)))),Map(D -> Set(), A -> Set(id), D_C_2 -> Set())),false)))
                     |        └─SourceBlock(graph=KG(Map(D -> IRNode(D,Set()), A -> IRNode(A,Set(id))),Map(D_C_2 -> IREdge(D_C_2,Set()))))""".stripMargin
    blocks.head.pretty should equal(blockRst)
  }

  it("case1_start_func") {
    val dsl =
      """
        |GraphStructure {
        |  A [test]
        |  D [test]
        |  D->C [abc] as D_C_2
        |}
        |Rule {
        |}
        |Action {
        |  get(A.id)
        |}
        |""".stripMargin

    val blocks = parser.parseMultipleStatement(dsl, Map.apply(Constants.START_ALIAS -> "D"))
    print(blocks.head.pretty)
    val blockRst = """└─TableResultBlock(selectList=OrderedFields(List(IRProperty(A,id))), asList=List(A.id), distinct=false)
                     |    └─MatchBlock(patterns=Map(unresolved_default_path -> GraphPath(unresolved_default_path,GraphPattern(D,Map(A -> (A:test), D -> (D:test)),Map(D -> Set((D)->[D_C_2:abc]-(C)))),Map(D -> Set(), A -> Set(id), D_C_2 -> Set())),false)))
                     |        └─SourceBlock(graph=KG(Map(D -> IRNode(D,Set()), A -> IRNode(A,Set(id))),Map(D_C_2 -> IREdge(D_C_2,Set()))))""".stripMargin
    blocks.head.pretty should equal(blockRst)
  }

  it("case1_get_exception2") {
    val dsl =
      """
        |GraphStructure {
        |  (s:User)
        |}
        |Rule {
        |}
        |Action {
        |  get(a(), o as b)
        |}
        |""".stripMargin

    try {
      parser.parse(dsl)
      true should equal(false)
    } catch {
      case ex: KGDSLGrammarException =>
        print(ex)
        ex.getMessage should equal("Action get must be a variable, not a express")
    }
  }

  it("case1_get_exception3") {
    val dsl =
      """
        |GraphStructure {
        |  (s:User)
        |}
        |Rule {
        |}
        |Action {
        |  get(s.name.tmp, o as b)
        |}
        |""".stripMargin

    try {
      parser.parse(dsl)
      true should equal(false)
    } catch {
      case ex: KGDSLGrammarException =>
        print(ex)
        ex.getMessage should equal("Action get must be a variable, not a express")
    }
  }

  it("case1_get_exception4") {
    val dsl =
      """
        |GraphStructure {
        |  (s:User)
        |}
        |Rule {
        |}
        |Action {
        |  get(s.name,"label" as b)
        |}
        |""".stripMargin

    val block = parser.parse(dsl)
    print(block.pretty)
    block.dependencies.head.isInstanceOf[ProjectBlock] should equal(true)
    block.asInstanceOf[TableResultBlock].asList.head should equal("s.name")
    block.asInstanceOf[TableResultBlock].asList(1) should equal("b")
  }

  it("case1_get_exception5") {
    val dsl =
      """
        |GraphStructure {
        |  (s:User)
        |}
        |Rule {
        |}
        |Action {
        |  get(s.name,"label" as b).as(table0(s,v1))
        |}
        |""".stripMargin

    val block = parser.parse(dsl)
    print(block.pretty)
    block.dependencies.head.isInstanceOf[ProjectBlock] should equal(true)
    block.asInstanceOf[TableResultBlock].asList.head should equal("s")
    block.asInstanceOf[TableResultBlock].asList(1) should equal("v1")
  }

  it("case1_get_exception6") {
    val dsl =
      """
        |GraphStructure {
        |  (s:User)
        |}
        |Rule {
        |}
        |Action {
        |  get(s.name, o as b).as(a,b,c)
        |}
        |""".stripMargin

    try {
      parser.parse(dsl)
      true should equal(false)
    } catch {
      case ex: KGDSLGrammarException =>
        print(ex)
        ex.getMessage should equal("as output column not equal get element")
    }
  }
  it("case1_parameter") {
    val dsl = """Define (s:User where id==$id)-[p:belongTo]->(o:`accountQueryCrowd`/`cardUser`) {
                |  GraphStructure {
                |    (s)<-[E1:relateCreditCardPaymentBindEvent]-(evt:creditCardPaymentBindEvent)
                |  }
                |  Rule {
                |    R1("银行卡规则"): evt.cardBank in ['PingAnBank', 'CITIC']
                |    R2("是否查询账户"): evt.accountQuery == 'Y'
                |    R3("是否绑定"): evt.bindSelf == $bindSelf
                |    BindNum = group(s).sum(evt.cardNum)
                |  	 R4('绑定数目'): BindNum > 0
                |    R5('智信确权'): s.zhixin == 'Y'
                |  }
                |}""".stripMargin

    val block = parser.parse(dsl)
    print(block.pretty)
    block.isInstanceOf[DDLBlock] should equal(true)
    block.asInstanceOf[DDLBlock].ddlOp.head.isInstanceOf[AddPredicate] should equal(true)
    block
      .asInstanceOf[DDLBlock]
      .ddlOp
      .head
      .asInstanceOf[AddPredicate]
      .predicate
      .label equals ("belongTo")
    val text =
      """└─DDLBlock(ddlOp=Set(AddPredicate(PredicateElement(belongTo,p,(s:User,BinaryOpExpr(name=BEqual)),EntityElement(cardUser,accountQueryCrowd,o),Map(__to_id_type__->VString(value=accountQueryCrowd/cardUser),__to_id__->VString(value=cardUser),__from_id__->UnaryOpExpr(name=GetField(id)),__from_id_type__->VString(value=User)),OUT))))
        *    └─FilterBlock(rules=LogicRule(R5,智信确权,BinaryOpExpr(name=BEqual)))
        *        └─FilterBlock(rules=LogicRule(R4,绑定数目,BinaryOpExpr(name=BGreaterThan)))
        *            └─AggregationBlock(aggregations=Aggregations(Map(IRVariable(BindNum) -> AggOpExpr(name=Sum))), group=List(IRNode(s,Set(zhixin))))
        *                └─FilterBlock(rules=LogicRule(R3,是否绑定,BinaryOpExpr(name=BEqual)))
        *                    └─FilterBlock(rules=LogicRule(R2,是否查询账户,BinaryOpExpr(name=BEqual)))
        *                        └─FilterBlock(rules=LogicRule(R1,银行卡规则,BinaryOpExpr(name=BIn)))
        *                            └─MatchBlock(patterns=Map(unresolved_default_path -> GraphPath(unresolved_default_path,GraphPattern(s,Map(s -> (s:User,BinaryOpExpr(name=BEqual)), evt -> (evt:creditCardPaymentBindEvent)),Map(s -> Set((s)<-[E1:relateCreditCardPaymentBindEvent]-(evt)))),Map(s -> Set(zhixin), evt -> Set(cardNum, bindSelf, accountQuery, cardBank), E1 -> Set())),false)))
        *                                └─SourceBlock(graph=KG(Map(s -> IRNode(s,Set(zhixin)), evt -> IRNode(evt,Set(cardNum, bindSelf, accountQuery, cardBank))),Map(E1 -> IREdge(E1,Set()))))"""
        .stripMargin('*')
    block.pretty should equal(text)
    val parameters = parser.getAllParameters()
    parameters.size should equal(2)
    parameters.contains("id") should equal(true)
    parameters.contains("bindSelf") should equal(true)
  }

  it("case group udf") {
    val dsl = """Define (s:CustFundKG.Account)-[p:aggTransAmountNumByDay]->(o:Boolean) {
                |    GraphStructure {
                |        (u:CustFundKG.Account)<-[t:accountFundContact]-(s)
                |    }
                |	Rule {
                |    	R1("当月交易"): date_diff(from_unix_time(now(), 'yyyyMMdd'),t.transDate) <= 30
                |    	o = group(s).groupByAttrDoCount(t,"tranDate", 50)
                |    }
                |}
                |
              """.stripMargin

    val block = parser.parse(dsl)
    print(block.pretty)
    val text = """└─DDLBlock(ddlOp=Set(AddProperty((s:CustFundKG.Account),aggTransAmountNumByDay,KTBoolean)))
                 |    └─ProjectBlock(projects=ProjectFields(Map(IRProperty(s,aggTransAmountNumByDay) -> ProjectRule(IRProperty(s,aggTransAmountNumByDay),Ref(refName=o)))))
                 |        └─AggregationBlock(aggregations=Aggregations(Map(IRVariable(o) -> AggOpExpr(name=AggUdf(groupByAttrDoCount,List(VString(value=tranDate), VLong(value=50)))))), group=List(IRNode(s,Set())))
                 |            └─FilterBlock(rules=LogicRule(R1,当月交易,BinaryOpExpr(name=BNotSmallerThan)))
                 |                └─MatchBlock(patterns=Map(unresolved_default_path -> GraphPath(unresolved_default_path,GraphPattern(s,Map(u -> (u:CustFundKG.Account), s -> (s:CustFundKG.Account)),Map(u -> Set((u)<-[t:accountFundContact]-(s)))),Map(u -> Set(), s -> Set(), t -> Set(transDate))),false)))
                 |                    └─SourceBlock(graph=KG(Map(s -> IRNode(s,Set()), u -> IRNode(u,Set())),Map(t -> IREdge(t,Set(transDate)))))""".stripMargin
    block.pretty should equal(text)
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

    val block = parser.parse(dsl)
    print(block.pretty)
    val text = """└─TableResultBlock(selectList=OrderedFields(List(IRProperty(s,id), IRVariable(o))), asList=List(s.id, b), distinct=false)
                 *    └─ProjectBlock(projects=ProjectFields(Map(IRVariable(o) -> ProjectRule(IRVariable(o),FunctionExpr(name=rule_value)))))
                 *        └─ProjectBlock(projects=ProjectFields(Map(IRVariable(R8) -> LogicRule(R8,白富美,BinaryOpExpr(name=BAnd)))))
                 *            └─ProjectBlock(projects=ProjectFields(Map(IRVariable(R7) -> LogicRule(R7,高富帅,BinaryOpExpr(name=BAnd)))))
                 *                └─FilterBlock(rules=LogicRule(R6,长得高,BinaryOpExpr(name=BOr)))
                 *                    └─ProjectBlock(projects=ProjectFields(Map(IRVariable(R5) -> LogicRule(R5,颜值高,BinaryOpExpr(name=BGreaterThan)))))
                 *                        └─ProjectBlock(projects=ProjectFields(Map(IRVariable(R4) -> LogicRule(R4,女性,BinaryOpExpr(name=BEqual)))))
                 *                            └─ProjectBlock(projects=ProjectFields(Map(IRVariable(R3) -> LogicRule(R3,男性,BinaryOpExpr(name=BEqual)))))
                 *                                └─ProjectBlock(projects=ProjectFields(Map(IRVariable(R2) -> LogicRule(R2,有车,BinaryOpExpr(name=BEqual)))))
                 *                                    └─ProjectBlock(projects=ProjectFields(Map(IRVariable(R1) -> LogicRule(R1,有房,BinaryOpExpr(name=BEqual)))))
                 *                                        └─MatchBlock(patterns=Map(unresolved_default_path -> GraphPath(unresolved_default_path,GraphPattern(null,Map(s -> (s:User)),Map(),Map(s -> Set(beautiful, haveCar, haveHouse, height, id, gender))),false)))
                 *                                            └─SourceBlock(graph=KG(Map(s -> IRNode(s,Set(beautiful, haveCar, haveHouse, height, id, gender))),Map()))"""
      .stripMargin('*')
    block.pretty should equal(text)
  }

  it("dsl2") {
    val dsl = "GraphStructure {\n" +
      "\t(A:Film)-[E1:directFilm]-(B:FilmDirector)\n" +
      "\t(A:Film)-[E2:writerOfFilm]-(C:FilmWriter)\n" +
      "\t(B:FilmDirector)-[E3:workmates]-(C:FilmWriter)\n" +
      "}\n" + "Rule {\n" + "\tR1(\"80后导演\"): B.birthDate > '1980'\n" +
      "\tR2(\"导演编剧同性别\"): B.gender == C.gender\n" + "}\n" +
      "Action {\n" + "\tget(B.name, C.name)\n" + "}"

    val block = parser.parse(dsl)
    print(block.pretty)
    val text = """└─TableResultBlock(selectList=OrderedFields(List(IRProperty(B,name), IRProperty(C,name))), asList=List(B.name, C.name), distinct=false)
                 *    └─FilterBlock(rules=LogicRule(R2,导演编剧同性别,BinaryOpExpr(name=BEqual)))
                 *        └─FilterBlock(rules=LogicRule(R1,80后导演,BinaryOpExpr(name=BGreaterThan)))
                 *            └─MatchBlock(patterns=Map(unresolved_default_path -> GraphPath(unresolved_default_path,GraphPattern(null,Map(B -> (B:FilmDirector), C -> (C:FilmWriter), A -> (A:Film)),Map(B -> Set((B)<->[E3:workmates]-(C))), A -> Set((A)<->[E2:writerOfFilm]-(C)), (A)<->[E1:directFilm]-(B)))),Map(E3 -> Set(), A -> Set(), E2 -> Set(), E1 -> Set(), B -> Set(birthDate, gender, name), C -> Set(gender, name))),false)))
                 *                └─SourceBlock(graph=KG(Map(A -> IRNode(A,Set()), C -> IRNode(C,Set(gender, name)), B -> IRNode(B,Set(birthDate, gender, name))),Map(E1 -> IREdge(E1,Set()), E3 -> IREdge(E3,Set()), E2 -> IREdge(E2,Set()))))"""
      .stripMargin('*')
    block.pretty should equal(text)
  }

  it("linked_edge_test") {
    val dsl =
      """
        |GraphStructure {
        |       (s:Park)-[e:nearby(s.boundary, o.center, 10)]->(o:Subway)
        |   }
        |   Rule{}
        |   Action {get(s)}
        |""".stripMargin

    val block = parser.parse(dsl)
    print(block.pretty)
    val edges =
      block.dependencies.head.asInstanceOf[MatchBlock].patterns.head._2.graphPattern.edges
    edges.get("s").size should equal(1)
    edges.get("s").head.head.isInstanceOf[LinkedPatternConnection] shouldEqual true
    val linkedPatternConnection = edges.get("s").head.head.asInstanceOf[LinkedPatternConnection]
    linkedPatternConnection.funcName should equal("nearby")
    linkedPatternConnection.params.size should equal(3)
    linkedPatternConnection.limit should equal(-1)

    try {
      val dsl2 =
        """
          |GraphStructure {
          |       (s:Park)<-[e:nearby(s.boundary, o.center, 10)]-(o:Subway)
          |       (s)-[e2:sameCity()]-(o)
          |   }
          |   Rule{}
          |   Action {get(s)}
          |""".stripMargin
      val block2 = parser.parse(dsl2)
      print(block2.pretty)
      true should equal(true)
    } catch {
      case ex: Exception =>
        true should equal(false)
    }
  }
}
