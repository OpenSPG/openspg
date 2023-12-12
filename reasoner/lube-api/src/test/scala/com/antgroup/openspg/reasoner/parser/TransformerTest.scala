package com.antgroup.openspg.reasoner.lube.parser

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.graph.edge.Direction
import com.antgroup.openspg.reasoner.common.types.{KTInteger, KTLong, KTObject}
import com.antgroup.openspg.reasoner.lube.block.{MatchBlock, ProjectBlock, ProjectFields, SourceBlock}
import com.antgroup.openspg.reasoner.lube.common.expr._
import com.antgroup.openspg.reasoner.lube.common.graph._
import com.antgroup.openspg.reasoner.lube.common.pattern.{GraphPath, GraphPattern, PatternConnection, PatternElement}
import com.antgroup.openspg.reasoner.lube.common.rule.{LogicRule, ProjectRule, Rule}
import com.antgroup.openspg.reasoner.lube.utils.{BlockUtils, ExprUtils, RuleUtils}
import com.antgroup.openspg.reasoner.lube.utils.transformer.impl._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{contain, convertToAnyShouldWrapper, equal}

class TransformerTest extends AnyFunSpec {
  it("transMatchBlock") {
    val block = ProjectBlock(
      List.apply(
        MatchBlock(
          List.apply(SourceBlock(KG())),
          Map.apply("t" -> GraphPath(
            "s",
            GraphPattern(
              null,
              Map.apply(
                "s" -> PatternElement("s", Set.apply("DomainFamily"), null),
                "d" -> PatternElement("s", Set.apply("Domain"), null)),
              Map.apply(
                "d" -> Set.apply(
                  new PatternConnection(
                    "anonymous_7",
                    "s",
                    Set.apply("belong"),
                    "d",
                    Direction.IN,
                    null)))),
            false)),
          KG())),
      ProjectFields(
        Map.apply(IRVariable("total_domain_num") ->
          ProjectRule(IRVariable("total_domain_num"), KTInteger, Ref("o")))),
      KG())
    val p = BlockUtils.transBlock2Graph(block)
    p.size should equal(1)
    p.head.graphPattern.nodes.size should equal(2)
  }
  it("mock12") {
    val mockTransformer = new Expr2QlexpressTransformer()
    val result: List[String] = mockTransformer.transform(
      BinaryOpExpr(BAssign, Ref("a"), UnaryOpExpr(GetField("birthDate"), Ref("B"))))
    result.size should equal(1)
    result.head should equal("a = B.birthDate")
  }
  it("mock1") {
    val mockTransformer = new Expr2QlexpressTransformer()
    val result: List[String] = mockTransformer.transform(
      BinaryOpExpr(BGreaterThan, UnaryOpExpr(GetField("birthDate"), Ref("B")), VString("1980")))
    result.size should equal(1)
    result(0) should equal("B.birthDate > \"1980\"")
  }

  it("mock2") {
    val mockTransformer = new Expr2QlexpressTransformer()
    val result: List[String] = mockTransformer.transform(
      BinaryOpExpr(
        BEqual,
        UnaryOpExpr(GetField("birthDate"), Ref("B")),
        UnaryOpExpr(GetField("gender"), Ref("C"))))
    result.size should equal(1)
    result(0) should equal("B.birthDate == C.gender")
  }
  it("ref") {
    val result: List[String] = ExprUtils.getRefVariableByExpr(
      BinaryOpExpr(
        BEqual,
        UnaryOpExpr(GetField("birthDate"), Ref("B")),
        UnaryOpExpr(GetField("gender"), Ref("C"))))
    result.size should equal(2)
  }

  val replaceMap = Map.apply("a" -> "b")

  def replaceFunc(input: String): String = {
    if (replaceMap.contains(input)) {
      replaceMap(input)
    } else {
      input
    }
  }

  it("get_all_in_expr") {
    val result =
      ExprUtils.getRefVariableByExpr(OpChainExpr(AggOpExpr(Accumulate("+"), Ref("abc")), null))
    result.head should equal("abc")
  }
  it("rename") {
    // a.birthDate == C.gender
    val result: Expr = ExprUtils.renameVariableInExpr(
      BinaryOpExpr(
        BEqual,
        UnaryOpExpr(GetField("birthDate"), Ref("a")),
        UnaryOpExpr(GetField("gender"), Ref("C"))),
      replaceFunc _)
    val variables = ExprUtils.getRefVariableByExpr(result)
    // b.birthDate == C.gender
    variables.contains("b") should equal(true)
  }

  it("rename_rule") {
    val rule = ProjectRule(
      IRVariable("a"),
      KTObject,
      BinaryOpExpr(
        BEqual,
        UnaryOpExpr(GetField("birthDate"), Ref("e")),
        UnaryOpExpr(GetField("gender"), Ref("C"))))
    val result = RuleUtils.renameVariableInRule(rule, replaceFunc _)
    result.getName should equal("b")
  }

  it("rename_rule_var") {
    val rule = getDependenceRule()
    val replaceVar: Map[IRField, IRProperty] =
      Map.apply((IRProperty("user", "sex") -> IRProperty("userA", "prop")))
    val result = RuleUtils.renameVariableInRule(rule, replaceVar)
    val mockTransformer = new Expr2QlexpressTransformer()
    val ruleStr: List[String] = mockTransformer.transform(result)
    ruleStr.size should equal(5)
    ruleStr should contain("R1 = userA.prop == \"男\"")
  }

  it("rename_rule_alias") {
    val rule = getDependenceRule()
    val replaceVar: Map[String, String] = Map.apply(("user" -> "userA"))
    val result = RuleUtils.renameAliasInRule(rule, replaceVar)
    val mockTransformer = new Expr2QlexpressTransformer()
    val ruleStr: List[String] = mockTransformer.transform(result)
    ruleStr.size should equal(5)
    ruleStr should contain("R1 = userA.sex == \"男\"")
  }

  it("variable_rule") {
    val rule = ProjectRule(
      IRVariable("a"),
      KTObject,
      BinaryOpExpr(BEqual, UnaryOpExpr(GetField("birthDate"), Ref("e")), Ref("b")))

    val rule2 = ProjectRule(
      IRVariable("b"),
      KTObject,
      BinaryOpExpr(
        BEqual,
        UnaryOpExpr(GetField("attr1"), Ref("e")),
        UnaryOpExpr(GetField("attr2"), Ref("C"))))
    rule.addDependency(rule2)
    val res = RuleUtils.getAllInputFieldInRule(rule, Set.empty, Set.empty)
    res.size should equal(2)
    res.head.isInstanceOf[IRNode] should equal(true)
    res.head.name should equal("e")
    res.head.asInstanceOf[IRNode].fields.size should equal(2)
  }

  def getDependenceRule(): Rule = {
    val r0 = ProjectRule(
      IRVariable("r0"),
      KTLong,
      BinaryOpExpr(BAssign, Ref("r0"), VLong("123"))
    )
    val r1 = LogicRule(
      "R1",
      "xx",
      BinaryOpExpr(BEqual, UnaryOpExpr(GetField("sex"), Ref("user")), VString("男")))
    r1.addDependency(r0)
    val r3 = LogicRule("R3", "xx", BinaryOpExpr(BGreaterThan, Ref("DayliyAmount"), VLong("300")))
    val r4 = LogicRule("R4", "xx", BinaryOpExpr(BSmallerThan, Ref("MonthAmount"), VLong("500")))
    val r5 = LogicRule(
      "R5",
      "x",
      BinaryOpExpr(
        BAnd,
        BinaryOpExpr(BAnd, Ref("R3"), Ref("R1")),
        UnaryOpExpr(Not, BinaryOpExpr(BAnd, Ref("R4"), Ref("R1")))))
    r3.addDependency(r1)
    r5.addDependency(r3)
    r5.addDependency(r4)
    r5
  }

  it("variable_rule2") {
    val r5 = getDependenceRule()
    val res = RuleUtils.getAllInputFieldInRule(r5, Set.empty, Set.empty)
    res.size should equal(4)
    res.head should equal(IRVariable("R1"))

    res(1) should equal(IRVariable("DayliyAmount"))
    res(2) should equal(IRVariable("MonthAmount"))
    res(3) should equal(IRNode("user", mutable.Set.apply("sex")))

    val qlTransformer = new Expr2QlexpressTransformer()
    val qlExpress = qlTransformer.transform(r5)
    qlExpress.size should equal(5)

    qlExpress.head should equal("r0 = r0 = 123")
    qlExpress.last should equal("R3 && R1 && !(R4 && R1)")
  }

  it("variable_rule2_to_expr") {
    val r5 = getDependenceRule()
    val transformer = new Rule2ExprTransformer()
    val expr = transformer.transform(r5)
    val qlTransformer = new Expr2QlexpressTransformer()
    val qlExpress = qlTransformer.transform(expr)
    print(qlExpress)
    qlExpress.head should
      equal("DayliyAmount > 300 && R1 && !(MonthAmount < 500 && R1)")
  }

  it("null ql") {
    val mockTransformer = new Expr2QlexpressTransformer()
    val result: List[String] = mockTransformer.transform(BinaryOpExpr(BEqual, Ref("a"), VNull))
    result.size should equal(1)
    print(result.head)
    result.head should equal("a == null")
  }

  it("agg_ql") {
    val mockTransformer = new Expr2QlexpressTransformer()
    val result: List[String] = mockTransformer.transform(AggOpExpr(Accumulate("+"), Ref("a")))
    result.size should equal(1)
    print(result.head)
  }
}
