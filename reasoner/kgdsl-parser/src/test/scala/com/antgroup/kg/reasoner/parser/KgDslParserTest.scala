package com.antgroup.openspg.reasoner.parser

import com.antgroup.openspg.reasoner.common.constants.Constants
import com.antgroup.openspg.reasoner.common.exception.{KGDSLGrammarException, KGDSLInvalidTokenException, KGDSLOneTaskException}
import com.antgroup.openspg.reasoner.common.types.{KTInteger, KTString}
import com.antgroup.openspg.reasoner.lube.block._
import com.antgroup.openspg.reasoner.lube.common.expr._
import com.antgroup.openspg.reasoner.lube.common.graph.{IRProperty, IRVariable}
import com.antgroup.openspg.reasoner.lube.common.pattern.{EntityElement, LinkedPatternConnection}
import com.antgroup.openspg.reasoner.lube.common.rule.ProjectRule
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class KgDslParserTest extends AnyFunSpec {
  it ("test gql 0") {
    val dsl = """MATCH (s)-[]->(o) RETURN s.id, o.id"""
    val parser = new KgDslParser()
    val blocks = parser.parseMultipleStatement(dsl, Map.apply(Constants.START_ALIAS -> "o"))
    val block = blocks.head
    print(block.pretty)
    block.isInstanceOf[TableResultBlock] should equal(true)
    block.asInstanceOf[TableResultBlock].asList.size should equal(2)
    block.dependencies.head.isInstanceOf[MatchBlock] should equal(true)
    block.dependencies
      .head.asInstanceOf[MatchBlock].patterns("unresolved_default_path")
      .graphPattern.rootAlias should equal("o")
  }

  it ("test gql 1") {
    val dsl = """MATCH (s)-[]->(o) RETURN s.id, o.id"""
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
    block.isInstanceOf[TableResultBlock] should equal(true)
    block.asInstanceOf[TableResultBlock].asList.size should equal(2)
  }

  it ("test gql 2") {
    val dsl = """MATCH (s)-[]->(o) WHERE s.id = 1 RETURN s.id, o.id"""
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
    block.isInstanceOf[TableResultBlock] should equal(true)
    block.asInstanceOf[TableResultBlock].asList.size should equal(2)
    block.dependencies.head.isInstanceOf[FilterBlock] should equal(true)
    block.dependencies
      .head.asInstanceOf[FilterBlock]
      .rules.getExpr should equal(
      BinaryOpExpr(BEqual, UnaryOpExpr(GetField("id"), Ref("s")), VLong("1")))
  }


  it ("test gql 3") {
    val dsl =
      """MATCH (s)-[]->(o),(o)-[]->(p1)
        |WHERE s.id > o.id
        |RETURN s.id As s_id, o.id, p1.id""".stripMargin
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
    block.isInstanceOf[TableResultBlock] should equal(true)
    block.asInstanceOf[TableResultBlock].asList.size should equal(3)
  }


  it ("test gql 4") {
    val dsl = """MATCH (s:`OpenSource.TaxonomyOfApp`/`赌博APP`) RETURN s.id"""
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
    block.isInstanceOf[TableResultBlock] should equal(true)
    block.asInstanceOf[TableResultBlock].asList.size should equal(1)
    block.dependencies.head.isInstanceOf[MatchBlock] should equal(true)
    block.dependencies.head
      .asInstanceOf[MatchBlock].patterns.size should equal(1)
    block.dependencies.head
      .asInstanceOf[MatchBlock]
      .patterns.head._2.graphPattern.nodes("s").isInstanceOf[EntityElement] should equal(true)
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
    val parser = new KgDslParser()
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
    val parser = new KgDslParser()
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
    val parser = new KgDslParser()
    try {
      parser.parseMultipleStatement(dsl)
    } catch {
      case ex: KGDSLInvalidTokenException =>
        ex.getMessage.contains("mismatched input") should equal(true)
    }
  }
  it("test mock") {
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
        |    R1('常驻地在杭州'): aa1.stdId == '中国-浙江省-杭州市'
        |  	R2('工作日上班时间通勤用户'): dayOfWeek(te.eventTime) in [1, 2, 3, 4, 5]
        |            and hourOfDay(te.eventTime) in [6, 7, 8, 9, 10, 17, 18, 19, 20, 21]
        |    R3('公交地铁'): tm.id in ['bus', 'subway']
        |    tmCount('出行次数') = group(user).count(te.id)
        |    R4('出行次数大于3次'): tmCount >= 3
        |    R5('id不为空'): user.id != ''
        |  }
        |}
        |""".stripMargin
    val parser = new KgDslParser()
    try {
      parser.parse(dsl)
    } catch {
      case ex: KGDSLGrammarException =>
        ex.getMessage.contains("add property must assign") should equal(true)
    }
  }
  it("opChainTest") {
    val chain = OpChainExpr(Filter(BinaryOpExpr(BNotEqual, Ref("a"), Ref("b"))), null)
    val parser = new KgDslParser()
    val case1 = parser.parseOpChain2Block(chain, IRVariable("abc"), null, null)
    case1.isInstanceOf[FilterBlock] should equal(true)

    val case2Chain = OpChainExpr(ListOpExpr(Get(1), Ref("a")), null)
    val case2 = parser.parseOpChain2Block(case2Chain, IRVariable("abc"), null, null)
    case2.isInstanceOf[ProjectBlock] should equal(true)

    val case3Chain = OpChainExpr(
      AggIfOpExpr(AggOpExpr(Count, Ref("a")), BinaryOpExpr(BGreaterThan, Ref("a"), VLong("1"))),
      null)
    val case3 = parser.parseOpChain2Block(case3Chain, IRVariable("abc"), null, null)
    case3.isInstanceOf[ProjectBlock] should equal(true)

    val case4Chain = OpChainExpr(AggOpExpr(Count, Ref("a")), null)
    val case4 = parser.parseOpChain2Block(case4Chain, IRVariable("abc"), null, null)
    case4.isInstanceOf[ProjectBlock] should equal(true)

    val case5Chain = OpChainExpr(Filter(BinaryOpExpr(BNotEqual, Ref("a"), Ref("b"))), null)
    val groupAgg =
      GraphAggregatorExpr("unresolved_default_path", List.apply(Ref("A"), Ref("B")), null)
    val case5 = parser.parseOpChain2Block(case5Chain, IRVariable("abc"), groupAgg, null)
    case5.isInstanceOf[FilterBlock] should equal(true)
  }
  it("addproperies1") {
    val dsl = """Define (s:DomainFamily)-[p:totalText]->(o:Text) {
                |    GraphStructure {
                |        (s)<-[:belong]-(d:Domain)
                |    }
                |    Rule {
                |        o = "abc"
                |    }
                |}""".stripMargin
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
    block.dependencies.head.isInstanceOf[ProjectBlock] should equal(true)
    val proj = block.dependencies.head.asInstanceOf[ProjectBlock]
    proj.projects.items.head._2 should equal(
      ProjectRule(IRProperty("s", "totalText"), KTString, Ref("o")))
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
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
    block.dependencies.head.isInstanceOf[ProjectBlock] should equal(true)
    val proj = block.dependencies.head.asInstanceOf[ProjectBlock]
    proj.projects.items.head._2 should equal(
      ProjectRule(IRProperty("s", "totalText"), KTString, Ref("o")))
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
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
    block.dependencies.head.isInstanceOf[ProjectBlock] should equal(true)
    val proj = block.dependencies.head.asInstanceOf[ProjectBlock]
    proj.projects.items.head._2 should equal(
      ProjectRule(IRProperty("s", "total_domain_num"), KTInteger, Ref("o")))
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
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
    block.dependencies.head.isInstanceOf[ProjectBlock] should equal(true)
    val proj = block.dependencies.head.asInstanceOf[ProjectBlock]
    proj.projects.items.head._2 should equal(
      ProjectRule(IRProperty("s", "total_domain_num"), KTInteger, Ref("o")))
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
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
    block.dependencies.head.isInstanceOf[ProjectBlock] should equal(true)
    block.asInstanceOf[DDLBlock].ddlOp.size should equal(2)
    block.asInstanceOf[DDLBlock].ddlOp.head.isInstanceOf[AddVertex] should equal(true)
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
    val parser = new KgDslParser()
    try {
      parser.parse(dsl)
    } catch {
    case ex: KGDSLGrammarException =>
      ex.getMessage.contains("must has type param") should equal(true)
    }
  }

  it("addNodeException with conflict") {
    val dsl = """Define (s: `HengSheng.TaxonomyOfCompanyAccident`/`周期性行业头部上市公司停产事故`)-[p: leadTo]->(o: `HengSheng.TaxonomyOfIndustryInfluence`/`价格上升`) {
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
    val parser = new KgDslParser()

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
    val parser = new KgDslParser()

    try {
      parser.parse(dsl)
    } catch {
      case ex: KGDSLGrammarException =>
        ex.getMessage.contains("PatternElement can not merge") should equal(true)
    }
  }

  it("action with edge") {
    val dsl = """Define (s: `HengSheng.TaxonomyOfCompanyAccident`/`周期性行业头部上市公司停产事故`)-[p: leadTo]->(o: `HengSheng.TaxonomyOfIndustryInfluence`/`价格上升`) {
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
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
    block.dependencies.head.isInstanceOf[MatchBlock] should equal(true)
    block.asInstanceOf[DDLBlock].ddlOp.size should equal(1)
    block.asInstanceOf[DDLBlock].ddlOp.head.isInstanceOf[AddVertex] should equal(true)

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
    val parser = new KgDslParser()
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
    val parser = new KgDslParser()
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
    val parser = new KgDslParser()
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

    val parser = new KgDslParser()
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
      .head
      ._1 should equal("same_domain_num")

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

    val parser = new KgDslParser()
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
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
  }
  it("case1_if_path") {
    val dsl = """GraphStructure {
               |	path1: (Product:InsProduct.Product)-[:includeLiability]->(Liability01:InsProduct.Liability),
               |	(Liability01)-[:liabilityProperty]->(DiseasesType:InsProduct.DiseasesType),
               |	(Product)-[:channel]->(ProductChannel:InsProduct.InsProductChannel),
               |	(Product)-[:productProperty]->(InsurancePeriod:InsProduct.InsurancePeriod),
               |	(Product)-[:productProperty]->(MaxInsureAge:InsProduct.MaxInsureAge)
               |
               |	path2:
               |	(Product)-[:includeLiability]->(Liability02:InsProduct.Liability),
               |	(Liability02)-[:liabilityProperty]->(PaymentAmountType:InsProduct.PaymentAmountType)
               |  }
               |
               |  Rule {
               |    // 单次给付
               |    R1("单次给付"): Liability01.standardLiabilityUnit in ["特定疾病单次给付保险金"] && DiseasesType.value ==  "重度疾病"
               |
               |    // 身故保险金
               |    R2("path2不存在时为true，否则判断身故保险金"): (not path2) || Liability02.standardLiabilityUnit in ['身故保险金']
               |
               |    // 身故保险金，返保费
               |    R3("path2不存在时为true，否则判断身故保险金，返保费"): (not path2) || (Liability02.standardLiabilityUnit in ['身故保险金'] && PaymentAmountType.value = "累计已交保费")
               |
               |    // 身故保险金，返保额
               |    R4("path2不存在时为true，否则判断身故保险金，返保额"): (not path2) || (Liability02.standardLiabilityUnit in ['身故保险金'] && PaymentAmountType.value = "基本保额")
               |
               |    // 保障期间>=70岁 或 终身(999岁、999年)
               |    R5("保障期间>=70岁 或 终身(999岁、999年)"): (StrContains(InsurancePeriod.value, "岁") && Cast(SubStr(InsurancePeriod.value,1,InStr(InsurancePeriod.value, "岁")-1) ,'int') >= 70) || (StrContains(InsurancePeriod.value, "年") && Cast(SubStr(InsurancePeriod.value,1,InStr(InsurancePeriod.value, "年")-1) ,'int') > 200)
               |
               |    // 最大投保年龄>22岁
               |   R6("最大投保年龄>22岁"): Cast(MaxInsureAge.value ,'int') > 22
               |   res1 = rule_value(R1 && R4 && R5 && R6, '终身储蓄型重疾', '其他重疾')
               |   // 计算结果
               |   res = rule_value(R1 && R3 && R5 && R6, '终身消费型重疾', res1)
               |
               |  }
               |
               |  Action {
               |    get(Product.prodNo As  prodno,  Product.name As  prodname, ProductChannel.name As channel,  res As labelname)
               |  }""".stripMargin

    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
    val expectRes =
      """└─TableResultBlock(selectList=OrderedFields(List(IRProperty(Product,prodNo), IRProperty(Product,name), IRProperty(ProductChannel,name), IRVariable(res))), asList=List(prodno, prodname, channel, labelname))
        |    └─ProjectBlock(projects=ProjectFields(Map(IRVariable(res) -> ProjectRule(IRVariable(res),KTObject,FunctionExpr(name=rule_value)))))
        |        └─ProjectBlock(projects=ProjectFields(Map(IRVariable(R6) -> LogicRule(R6,最大投保年龄>22岁,BinaryOpExpr(name=BGreaterThan)))))
        |            └─ProjectBlock(projects=ProjectFields(Map(IRVariable(R5) -> LogicRule(R5,保障期间>=70岁 或 终身(999岁、999年),BinaryOpExpr(name=BOr)))))
        |                └─FilterBlock(rules=LogicRule(R2,path2不存在时为true，否则判断身故保险金,BinaryOpExpr(name=BOr)))
        |                    └─ProjectBlock(projects=ProjectFields(Map(IRVariable(R1) -> LogicRule(R1,单次给付,BinaryOpExpr(name=BAnd)))))
        |                        └─MatchBlock(patterns=Map(path1 -> GraphPath(path1,GraphPattern(null,Map(InsurancePeriod -> (InsurancePeriod:InsProduct.InsurancePeriod), Liability01 -> (Liability01:InsProduct.Liability), DiseasesType -> (DiseasesType:InsProduct.DiseasesType), Product -> (Product:InsProduct.Product), MaxInsureAge -> (MaxInsureAge:InsProduct.MaxInsureAge), ProductChannel -> (ProductChannel:InsProduct.InsProductChannel)),Map(Product -> Set((Product)->[anonymous_4:includeLiability]-(Liability01)), (Product)->[anonymous_10:channel]-(ProductChannel)), (Product)->[anonymous_13:productProperty]-(InsurancePeriod)), (Product)->[anonymous_16:productProperty]-(MaxInsureAge))), Liability01 -> Set((Liability01)->[anonymous_7:liabilityProperty]-(DiseasesType)))),Map(anonymous_13 -> Set(), anonymous_16 -> Set(), anonymous_4 -> Set(), anonymous_10 -> Set(), InsurancePeriod -> Set(value), Liability01 -> Set(standardLiabilityUnit), DiseasesType -> Set(value), anonymous_7 -> Set(), Product -> Set(prodNo, name), MaxInsureAge -> Set(value), ProductChannel -> Set(name))),false), path2 -> GraphPath(path2,GraphPattern(null,Map(Product -> (Product:InsProduct.Product), Liability02 -> (Liability02:InsProduct.Liability), PaymentAmountType -> (PaymentAmountType:InsProduct.PaymentAmountType)),Map(Product -> Set((Product)->[anonymous_19:includeLiability]-(Liability02))), Liability02 -> Set((Liability02)->[anonymous_22:liabilityProperty]-(PaymentAmountType)))),Map(anonymous_13 -> Set(), anonymous_16 -> Set(), anonymous_4 -> Set(), anonymous_10 -> Set(), InsurancePeriod -> Set(value), Liability01 -> Set(standardLiabilityUnit), DiseasesType -> Set(value), anonymous_7 -> Set(), Product -> Set(prodNo, name), MaxInsureAge -> Set(value), ProductChannel -> Set(name))),false)))
        |                            └─SourceBlock(graph=KG(Map(InsurancePeriod -> IRNode(InsurancePeriod,Set(value)), Liability01 -> IRNode(Liability01,Set(standardLiabilityUnit)), DiseasesType -> IRNode(DiseasesType,Set(value)), Product -> IRNode(Product,Set(prodNo, name)), MaxInsureAge -> IRNode(MaxInsureAge,Set(value)), ProductChannel -> IRNode(ProductChannel,Set(name))),Map(anonymous_13 -> IREdge(anonymous_13,Set()), anonymous_16 -> IREdge(anonymous_16,Set()), anonymous_4 -> IREdge(anonymous_4,Set()), anonymous_10 -> IREdge(anonymous_10,Set()), anonymous_7 -> IREdge(anonymous_7,Set()))))""".stripMargin
    block.pretty should equal(expectRes)
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
    val parser = new KgDslParser()
    try {
      parser.parse(dsl)
      true should equal(false)
    } catch {
      case ex: KGDSLGrammarException => true should equal(true)
    }
  }

  it("case1_start") {
    val dsl =
      """
        |GraphStructure {
        |  A [test]
        |  D [test, __start__='true']
        |  D->C [abc]
        |}
        |Rule {
        |}
        |Action {
        |  get(A.id)
        |}
        |""".stripMargin
    val parser = new KgDslParser()
    val blocks = parser.parse(dsl)
    print(blocks.pretty)
    val blockRst = """└─TableResultBlock(selectList=OrderedFields(List(IRProperty(A,id))), asList=List(A.id))
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
    val parser = new KgDslParser()
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
        |  D->C [abc]
        |}
        |Rule {
        |}
        |Action {
        |  get(A.id)
        |}
        |""".stripMargin
    val parser = new KgDslParser()
    val blocks = parser.parseMultipleStatement(dsl, null)
    print(blocks.head.pretty)
    val blockRst = """└─TableResultBlock(selectList=OrderedFields(List(IRProperty(A,id))), asList=List(A.id))
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
        |  D->C [abc]
        |}
        |Rule {
        |}
        |Action {
        |  get(A.id)
        |}
        |""".stripMargin
    val parser = new KgDslParser()
    val blocks = parser.parseMultipleStatement(dsl, Map.apply(Constants.START_ALIAS -> "D"))
    print(blocks.head.pretty)
    val blockRst = """└─TableResultBlock(selectList=OrderedFields(List(IRProperty(A,id))), asList=List(A.id))
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
    val parser = new KgDslParser()
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
    val parser = new KgDslParser()
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
    val parser = new KgDslParser()
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
    val parser = new KgDslParser()
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
    val parser = new KgDslParser()
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
    val parser = new KgDslParser()
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
      """└─DDLBlock(ddlOp=Set(AddPredicate(PredicateElement(belongTo,p,(s:User,BinaryOpExpr(name=BEqual)),EntityElement(cardUser,accountQueryCrowd,o),Map(),OUT))))
        *    └─FilterBlock(rules=LogicRule(R5,智信确权,BinaryOpExpr(name=BEqual)))
        *        └─FilterBlock(rules=LogicRule(R4,绑定数目,BinaryOpExpr(name=BGreaterThan)))
        *            └─AggregationBlock(aggregations=Aggregations(Map(IRVariable(BindNum) -> AggOpExpr(name=Sum))), group=List(s))
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
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
    val text = """└─DDLBlock(ddlOp=Set(AddProperty((s:CustFundKG.Account),aggTransAmountNumByDay,KTBoolean)))
                 |    └─ProjectBlock(projects=ProjectFields(Map(IRProperty(s,aggTransAmountNumByDay) -> ProjectRule(IRProperty(s,aggTransAmountNumByDay),KTBoolean,Ref(refName=o)))))
                 |        └─AggregationBlock(aggregations=Aggregations(Map(IRVariable(o) -> AggOpExpr(name=AggUdf(groupByAttrDoCount,List(VString(value=tranDate), VLong(value=50)))))), group=List(s))
                 |            └─FilterBlock(rules=LogicRule(R1,当月交易,BinaryOpExpr(name=BNotSmallerThan)))
                 |                └─MatchBlock(patterns=Map(unresolved_default_path -> GraphPath(unresolved_default_path,GraphPattern(s,Map(u -> (u:CustFundKG.Account), s -> (s:CustFundKG.Account)),Map(u -> Set((u)<-[t:accountFundContact]-(s)))),Map(u -> Set(), s -> Set(), t -> Set(transDate))),false)))
                 |                    └─SourceBlock(graph=KG(Map(s -> IRNode(s,Set()), u -> IRNode(u,Set())),Map(t -> IREdge(t,Set(transDate)))))""".stripMargin
    block.pretty should equal(text)
  }
  it("case1") {
    val dsl = """Define (s:User)-[p:belongTo]->(o:`accountQueryCrowd`/`cardUser`) {
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
    val parser = new KgDslParser()
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
      """└─DDLBlock(ddlOp=Set(AddPredicate(PredicateElement(belongTo,p,(s:User),EntityElement(cardUser,accountQueryCrowd,o),Map(),OUT))))
        *    └─FilterBlock(rules=LogicRule(R5,智信确权,BinaryOpExpr(name=BEqual)))
        *        └─FilterBlock(rules=LogicRule(R4,绑定数目,BinaryOpExpr(name=BGreaterThan)))
        *            └─AggregationBlock(aggregations=Aggregations(Map(IRVariable(BindNum) -> AggOpExpr(name=Sum))), group=List(s))
        *                └─FilterBlock(rules=LogicRule(R3,是否绑定,BinaryOpExpr(name=BEqual)))
        *                    └─FilterBlock(rules=LogicRule(R2,是否查询账户,BinaryOpExpr(name=BEqual)))
        *                        └─FilterBlock(rules=LogicRule(R1,银行卡规则,BinaryOpExpr(name=BIn)))
        *                            └─MatchBlock(patterns=Map(unresolved_default_path -> GraphPath(unresolved_default_path,GraphPattern(s,Map(s -> (s:User), evt -> (evt:creditCardPaymentBindEvent)),Map(s -> Set((s)<-[E1:relateCreditCardPaymentBindEvent]-(evt)))),Map(s -> Set(zhixin), evt -> Set(cardNum, bindSelf, accountQuery, cardBank), E1 -> Set())),false)))
        *                                └─SourceBlock(graph=KG(Map(s -> IRNode(s,Set(zhixin)), evt -> IRNode(evt,Set(cardNum, bindSelf, accountQuery, cardBank))),Map(E1 -> IREdge(E1,Set()))))"""
        .stripMargin('*')
    block.pretty should equal(text)
  }

  it("case3") {
    val dsl =
      """
        |Define (s:User)-[p:belongto]->(o:`TuringCrowd`/`taxonUser`) {
        |	GraphStructure {
        |    (s:TradeEvent)-[pr:relateUser]->(user:User)
        |	}
        |  Rule{
        |    R1('必须是男性'): user.sex  == '男'
        |    R2('交易周期是日和月'): s.statPriod in ['日', '月']
        |    DayliyAmount = group(user).if(s.statPriod == '日').sum(s.amount)
        |    MonthAmount = group(user).if(s.statPriod == '月').sum(s.amount)
        |    R3('日消费额大于300'): DayliyAmount > 300
        |    R4('月消费额小于500'): MonthAmount < 500
        |    R5('召回人群'): (R3 and R1) and (not(R4 and R1))
        |  }
        |}
        |""".stripMargin
    val parser = new KgDslParser()
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
      """└─DDLBlock(ddlOp=Set(AddPredicate(PredicateElement(belongto,p,(s:User),EntityElement(taxonUser,TuringCrowd,o),Map(),OUT))))
        *    └─FilterBlock(rules=LogicRule(R5,召回人群,BinaryOpExpr(name=BAnd)))
        *        └─AggregationBlock(aggregations=Aggregations(Map(IRVariable(MonthAmount) -> AggIfOpExpr, IRVariable(DayliyAmount) -> AggIfOpExpr)), group=List(user))
        *            └─FilterBlock(rules=LogicRule(R2,交易周期是日和月,BinaryOpExpr(name=BIn)))
        *                └─MatchBlock(patterns=Map(unresolved_default_path -> GraphPath(unresolved_default_path,GraphPattern(s,Map(s -> (s:TradeEvent,User), user -> (user:User)),Map(s -> Set((s)->[pr:relateUser]-(user)))),Map(s -> Set(amount, statPriod), user -> Set(sex), pr -> Set())),false)))
        *                    └─SourceBlock(graph=KG(Map(s -> IRNode(s,Set(amount, statPriod)), user -> IRNode(user,Set(sex))),Map(pr -> IREdge(pr,Set()))))"""
        .stripMargin('*')
    block.pretty should equal(text)
  }

  it("case4_belongTo") {
    val dsl =
      """
        |Define (s:User)-[p:belongTo]->(o:`TuringCrowd`/`CrowdRelate张三`) {
        |	GraphStructure {
        |    (s:TradeEvent)-[pr:relateUser]->(user:User)
        |	}
        |  Rule{
        |    R1('必须是男性'): user.sex  == '男'
        |    R2('交易周期是日和月'): s.statPriod in ['日', '月']
        |    DayliyAmount = group(user).if(s.statPriod == '日').sum(s.amount)
        |    MonthAmount = group(user).if(s.statPriod == '月').sum(s.amount)
        |    R3('日消费额大于300'): DayliyAmount > 300
        |    R4('月消费额小于500'): MonthAmount < 500
        |    R5('召回人群'): (R3 and R1) and (not(R4 and R1))
        |  }
        |}
        |""".stripMargin
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
    block.isInstanceOf[DDLBlock] should equal(true)
    block
      .asInstanceOf[DDLBlock]
      .ddlOp
      .head
      .asInstanceOf[AddPredicate]
      .predicate
      .target
      .isInstanceOf[EntityElement] should equal(true)
    block
      .asInstanceOf[DDLBlock]
      .ddlOp
      .head
      .asInstanceOf[AddPredicate]
      .predicate
      .target
      .asInstanceOf[EntityElement]
      .id should equal("CrowdRelate张三")
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
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
    val text = """└─TableResultBlock(selectList=OrderedFields(List(IRProperty(s,id), IRVariable(o))), asList=List(s.id, b))
                 *    └─ProjectBlock(projects=ProjectFields(Map(IRVariable(o) -> ProjectRule(IRVariable(o),KTObject,FunctionExpr(name=rule_value)))))
                 *        └─FilterBlock(rules=LogicRule(R6,长得高,BinaryOpExpr(name=BOr)))
                 *            └─ProjectBlock(projects=ProjectFields(Map(IRVariable(R5) -> LogicRule(R5,颜值高,BinaryOpExpr(name=BGreaterThan)))))
                 *                └─ProjectBlock(projects=ProjectFields(Map(IRVariable(R4) -> LogicRule(R4,女性,BinaryOpExpr(name=BEqual)))))
                 *                    └─ProjectBlock(projects=ProjectFields(Map(IRVariable(R3) -> LogicRule(R3,男性,BinaryOpExpr(name=BEqual)))))
                 *                        └─ProjectBlock(projects=ProjectFields(Map(IRVariable(R2) -> LogicRule(R2,有车,BinaryOpExpr(name=BEqual)))))
                 *                            └─ProjectBlock(projects=ProjectFields(Map(IRVariable(R1) -> LogicRule(R1,有房,BinaryOpExpr(name=BEqual)))))
                 *                                └─MatchBlock(patterns=Map(unresolved_default_path -> GraphPath(unresolved_default_path,GraphPattern(null,Map(s -> (s:User)),Map(),Map(s -> Set(beautiful, haveCar, haveHouse, height, id, gender))),false)))
                 *                                    └─SourceBlock(graph=KG(Map(s -> IRNode(s,Set(beautiful, haveCar, haveHouse, height, id, gender))),Map()))"""
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
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
    val text = """└─TableResultBlock(selectList=OrderedFields(List(IRProperty(B,name), IRProperty(C,name))), asList=List(B.name, C.name))
                 *    └─FilterBlock(rules=LogicRule(R2,导演编剧同性别,BinaryOpExpr(name=BEqual)))
                 *        └─FilterBlock(rules=LogicRule(R1,80后导演,BinaryOpExpr(name=BGreaterThan)))
                 *            └─MatchBlock(patterns=Map(unresolved_default_path -> GraphPath(unresolved_default_path,GraphPattern(null,Map(B -> (B:FilmDirector), C -> (C:FilmWriter), A -> (A:Film)),Map(B -> Set((B)<->[E3:workmates]-(C))), A -> Set((A)<->[E2:writerOfFilm]-(C)), (A)<->[E1:directFilm]-(B)))),Map(E3 -> Set(), A -> Set(), E2 -> Set(), E1 -> Set(), B -> Set(birthDate, gender, name), C -> Set(gender, name))),false)))
                 *                └─SourceBlock(graph=KG(Map(A -> IRNode(A,Set()), C -> IRNode(C,Set(gender, name)), B -> IRNode(B,Set(birthDate, gender, name))),Map(E1 -> IREdge(E1,Set()), E3 -> IREdge(E3,Set()), E2 -> IREdge(E2,Set()))))""".stripMargin('*')
    block.pretty should equal(text)
  }

  it("product") {
    val dsl = """GraphStructure {
                |    // 保险产品
                |    Product [InsProduct.Product,__start__='true']
                |    // 产品渠道
                |    ProductChannel[InsProduct.InsProductChannel]
                |    // 责任
                |    Liability01, Liability02[InsProduct.Liability]
                |    // 保障期间，xx年
                |    InsurancePeriod[InsProduct.InsurancePeriod]
                |    // 最大投保年龄
                |    MaxInsureAge[InsProduct.MaxInsureAge]
                |    // 疾病分类
                |    DiseasesType[InsProduct.DiseasesType]
                |
                |    // 必须包含的责任
                |    Product->Liability01[includeLiability]
                |    Liability01->DiseasesType[liabilityProperty]
                |
                |    // 可能包含的责任
                |    Product->Liability02 [includeLiability,__optional__='true']
                |    Liability02->PaymentAmountType[liabilityProperty]
                |
                |    Product->ProductChannel[channel]
                |    Product->InsurancePeriod[productProperty]
                |    Product->MaxInsureAge[productProperty]
                |  }
                |
                |  Rule {
                |    // 单次给付
                |    R1: Liability01.standardLiabilityUnit in ["特定疾病单次给付保险金"] && DiseasesType.value ==  "重度疾病"
                |
                |    // 身故保险金
                |    R2: Liability02.standardLiabilityUnit in ['身故保险金']
                |
                |    // 身故保险金，返保费
                |    R3: Liability02.standardLiabilityUnit in ['身故保险金'] && PaymentAmountType.value = "累计已交保费"
                |
                |    // 身故保险金，返保额
                |    R4("身故保险金，返保额"): Liability02.standardLiabilityUnit in ['身故保险金'] && PaymentAmountType.value = "基本保额"
                |
                |    // 保障期间>=70岁 或 终身(999岁、999年)
                |    R5("保障期间>=70岁 或 终身(999岁、999年)"): (StrContains(InsurancePeriod.value, "岁") && Cast(SubStr(InsurancePeriod.value,1,InStr(InsurancePeriod.value, "岁")-1) ,'int') >= 70) || (StrContains(InsurancePeriod.value, "年") && Cast(SubStr(InsurancePeriod.value,1,InStr(InsurancePeriod.value, "年")-1) ,'int') > 200)
                |
                |    // 最大投保年龄>22岁
                |   R6("最大投保年龄>22岁"): Cast(MaxInsureAge.value ,'int') > 22
                |   res1 = rule_value(R1 && R4 && R5 && R6, '终身储蓄型重疾', '其他重疾')
                |   // 计算结果
                |   res = rule_value(R1 && R3 && R5 && R6, '终身消费型重疾', res1)
                |
                |  }
                |
                |  Action {
                |    get(Product.prodNo As  prodno,  Product.name As  prodname, ProductChannel.name As channel,  res As labelname)
                |  }""".stripMargin
    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
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

    val parser = new KgDslParser()
    val block = parser.parse(dsl)
    print(block.pretty)
    block.pretty should equal(
      """└─DDLBlock(ddlOp=Set(AddProperty((s:User),redPacket,KTInteger)))
        *    └─ProjectBlock(projects=ProjectFields(Map(IRProperty(s,redPacket) -> ProjectRule(IRProperty(s,redPacket),KTInteger,Ref(refName=o)))))
        *        └─ProjectBlock(projects=ProjectFields(Map(IRVariable(o) -> ProjectRule(IRVariable(o),KTObject,FunctionExpr(name=get_first_notnull)))))
        *            └─ProjectBlock(projects=ProjectFields(Map(IRVariable(HistoricallyPayCount) -> ProjectRule(IRVariable(HistoricallyPayCount),KTObject,FunctionExpr(name=size)))))
        *                └─ProjectBlock(projects=ProjectFields(Map(IRVariable(Latest3060DayPayDayCount) -> ProjectRule(IRVariable(Latest3060DayPayDayCount),KTObject,FunctionExpr(name=size)))))
        *                    └─ProjectBlock(projects=ProjectFields(Map(IRVariable(Latest30DayPayDayCount) -> ProjectRule(IRVariable(Latest30DayPayDayCount),KTObject,FunctionExpr(name=size)))))
        *                        └─ProjectBlock(projects=ProjectFields(Map(IRVariable(HighReduceValue) -> ProjectRule(IRVariable(HighReduceValue),KTObject,BinaryOpExpr(name=BDiv)))))
        *                            └─MatchBlock(patterns=Map(unresolved_default_path -> GraphPath(unresolved_default_path,GraphPattern(s,Map(s -> (s:User)),Map(),Map(s -> Set(ngfe_tag__pay_cnt_d, ngfe_tag__pay_cnt_total, ngfe_tag__pay_cnt_m))),false)))
        *                                └─SourceBlock(graph=KG(Map(s -> IRNode(s,Set(ngfe_tag__pay_cnt_d, ngfe_tag__pay_cnt_total, ngfe_tag__pay_cnt_m))),Map()))"""
        .stripMargin('*'))
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
    val parser = new KgDslParser()
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
