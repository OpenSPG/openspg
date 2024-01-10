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

package com.antgroup.openspg.reasoner.parser.expr

import com.antgroup.openspg.reasoner.common.exception.KGDSLGrammarException
import com.antgroup.openspg.reasoner.common.types.{KTObject, KTString}
import com.antgroup.openspg.reasoner.lube.common.expr.{BinaryOpExpr, _}
import com.antgroup.openspg.reasoner.lube.utils.transformer.impl.Expr2QlexpressTransformer
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class ExprTest extends AnyFunSpec {
  it("test remove front and tailQuote") {
    val exprParser = new RuleExprParser()
    try {
      exprParser.removeFrontAndTailQuote("abc")
      true should equal(false)
    } catch {
      case ex: KGDSLGrammarException =>
        ex.getMessage.contains("need quote") should equal(true)
    }
  }

  it("test expressionTreeBuilder") {
    val exprParser = new RuleExprParser()
    try {
      exprParser.expressionTreeBuilder(List.apply(Ref("a"), Ref("b")), List.empty)
      true should equal(false)
    } catch {
      case ex: KGDSLGrammarException =>
        ex.getMessage.contains("expr list length not equal op list + 1") should equal(true)
    }
  }

  it("test parseListConstValue") {
    val exprParser = new RuleExprParser()
    try {
      exprParser.parseListConstValue(List.apply(VLong("1"), VDouble("1.2")))
    } catch {
      case ex: KGDSLGrammarException =>
        println(ex.getMessage)
        ex.getMessage should equal("KTLong is not in [KTDouble]")
    }

    try {
      exprParser.parseListConstValue(List.apply(VString("1.2"), VLong("1")))
    } catch {
      case ex: KGDSLGrammarException =>
        println(ex.getMessage)
        ex.getMessage should equal("KTString is not in [KTLong, KTDouble]")
    }

    try {
      exprParser.parseListConstValue(List.apply(VLong("1"), VString("1.2")))
    } catch {
      case ex: KGDSLGrammarException =>
        println(ex.getMessage)
        ex.getMessage should equal("KTLong is not in [KTString]")
    }

    try {
      exprParser.parseListConstValue(List.apply(VList(List.empty, KTObject)))
    } catch {
      case ex: NotImplementedError =>
        println(ex.getMessage)
        ex.getMessage should equal("not impl list")
    }

    try {
      exprParser.parseListConstValue(List.apply(VLong("1"), VBoolean("true")))
    } catch {
      case ex: KGDSLGrammarException =>
        println(ex.getMessage)
        ex.getMessage should equal("KTLong is not in [KTBoolean]")
    }

    try {
      exprParser.parseListConstValue(List.apply(Ref("abc")))
    } catch {
      case ex: NotImplementedError =>
        println(ex.getMessage)
        ex.getMessage should equal("not impl other types")
    }
  }

  it("a+b") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("a+b")
    print(expr.pretty)
    expr should equal(BinaryOpExpr(BAdd, Ref("a"), Ref("b")))
  }

  it("a>b") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("a>=b")
    val mockTransformer = new Expr2QlexpressTransformer()
    val result: List[String] = mockTransformer.transform(expr)
    result.size should equal(1)
    result(0) should equal("a >= b")
  }

  it("a and b") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("a and b")
    print(expr.pretty)
    expr should equal(BinaryOpExpr(BAnd, Ref("a"), Ref("b")))
  }

  it("a.name rlike '(123-)'") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("a.name rlike '(123-)'")
    print(expr.pretty)
    expr should equal(BinaryOpExpr(BRLike,
      UnaryOpExpr(GetField("name"), Ref("a")), VString("(123-)")))
    val mockTransformer = new Expr2QlexpressTransformer()
    val result: List[String] = mockTransformer.transform(expr)
    result.size should equal(1)
    result(0) should equal("a.name rlike \"(123-)\"")
  }

  it("a and not b") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("a and not b")
    print(expr.pretty)
    expr should equal(BinaryOpExpr(BAnd, Ref("a"), UnaryOpExpr(Not, Ref("b"))))
  }

  it("a in ['a','b']") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("a in ['a','b']")
    print(expr.pretty)
    expr should equal(BinaryOpExpr(BIn, Ref("a"), VList(List.apply("a", "b"), KTString)))
  }


  it("a != null") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("a != null")
    print(expr.pretty)
    expr should equal(BinaryOpExpr(BNotEqual, Ref("a"), VNull))
  }


  it("func(a,b)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("func(a,b)")
    print(expr.pretty)
    expr should equal(FunctionExpr("func", List.apply(Ref("a"), Ref("b"))))
  }

  it("(a or c) and (not b)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("(a or c) and (not b)")
    print(expr.pretty)
    val mockTransformer = new Expr2QlexpressTransformer()
    val result: List[String] = mockTransformer.transform(expr)
    result.size should equal(1)
    result(0) should equal("(a || c) && !(b)")
  }

  it("(a or c) and d rlike '(a)|(b)'") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("(a or c) and d rlike '(a)|(b)'")
    print(expr.pretty)
    val mockTransformer = new Expr2QlexpressTransformer()
    val result: List[String] = mockTransformer.transform(expr)
    result.size should equal(1)
    result(0) should equal("(a || c) && (d rlike \"(a)|(b)\")")
  }

  it("(a or c) and d in ['a','b']") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("(a or c) and d in ['a','b']")
    print(expr.pretty)
    val mockTransformer = new Expr2QlexpressTransformer()
    val result: List[String] = mockTransformer.transform(expr)
    result.size should equal(1)
    result(0) should equal("(a || c) && (d in [\"a\",\"b\"])")
  }

  it("func(a,b) == true") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("func(a,b) == true")
    print(expr.pretty)
    expr should equal(
      BinaryOpExpr(
        BEqual,
        FunctionExpr("func", List.apply(Ref("a"), Ref("b"))),
        VBoolean("true")))

  }

  it("a.sum()") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("a.sum()")
    print(expr.pretty)
    expr should equal(OpChainExpr(AggOpExpr(Sum, Ref("a")), null))
  }

  it("a.if(a>1).count()") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("a.if(a>1).count()")
    print(expr.pretty)
    expr should equal(
      OpChainExpr(
        AggIfOpExpr(
          AggOpExpr(
            Count,
            Ref("a")
          ),
          BinaryOpExpr(BGreaterThan, Ref("a"), VLong("1"))
        ),
        null
      )

    )
  }

  it("a.get(1)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("a.get(1)")
    print(expr.pretty)
    expr should equal(
      OpChainExpr(
        ListOpExpr(
          Get(1),
          Ref("a")
        ),
        null
      )
    )
  }

  it("a.slice(1,4)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("a.slice(1,4)")
    print(expr.pretty)
    expr should equal(
      OpChainExpr(
        ListOpExpr(
          Slice(1, 4),
          Ref("a")
        ),
        null
      )
    )
  }

  it("a.if(a> 10).accumulate(+)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("a.if(a> 10).accumulate(+)")
    print(expr.pretty)
    expr should equal(
      OpChainExpr(
        AggOpExpr(
          Accumulate("+"),
          Ref("a")
        ),
        OpChainExpr(
          Filter(
            BinaryOpExpr(BGreaterThan, Ref("a"), VLong("10"))
          ),
          null
        )
      )
    )
  }

  it("a.if(a> 10).desc().limit(10).sum()") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("a.if(a> 10).desc().limit(10).sum()")
    print(expr.pretty)
    expr should equal(
      OpChainExpr(
        AggOpExpr(
          Sum,
          Ref("a")
        ),
        OpChainExpr(
          OrderAndLimit(
            DescExpr,
            Limit(Ref("a"), 10)
          ),
          OpChainExpr(
            Filter(BinaryOpExpr(BGreaterThan, Ref("a"), VLong("10"))),
            null
          )
        )
      )
    )
  }

  it("a.maxIf(a > 10, a.id)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("a.maxIf(a > 10, a.id)")
    val expectResult = OpChainExpr(
      AggIfOpExpr(
        AggOpExpr(
          Max,
          UnaryOpExpr(
            GetField("id"),
            Ref("a")
          )
        ),
        BinaryOpExpr(BGreaterThan, Ref("a"), VLong("10"))
      ),
      null
    ).pretty
    expr.pretty should equal(
      expectResult
    )
  }

  it("a.desc().limit(10).sumIf(a > 10)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("a.desc().limit(10).sumIf(a > 10)")
    val expectResult = OpChainExpr(
      AggIfOpExpr(
        AggOpExpr(
          Sum,
          Ref("a")
        ),
        BinaryOpExpr(BGreaterThan, Ref("a"), VLong("10"))
      ),
      OpChainExpr(
        OrderAndLimit(
          DescExpr,
          Limit(Ref("a"), 10)
        ),
        null
      )
    ).pretty
    expr.pretty should equal(
      expectResult
    )
  }

  it("a.desc().limit(10).sumIf(a > 10, a.id)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("a.desc().limit(10).sumIf(a > 10, a.id)")
    print(expr.pretty)
    val expectResult = OpChainExpr(
      AggIfOpExpr(
        AggOpExpr(
          Sum,
          UnaryOpExpr(GetField("id"), Ref("a"))
        ),
        BinaryOpExpr(BGreaterThan, Ref("a"), VLong("10"))
      ),
      OpChainExpr(
        OrderAndLimit(
          DescExpr,
          Limit(Ref("a"), 10)
        ),
        null
      )
    ).pretty
    expr.pretty should equal(
      expectResult
    )
  }

  it("group(A,B).if(E1.amount > 10).count(E1)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("group(A,B).if(E1.amount > 10).count(E1)")
    print(expr.pretty)
    expr should equal(
      OpChainExpr(
        GraphAggregatorExpr(
          "unresolved_default_path",
          List.apply(Ref("A"), Ref("B")),
          null
        ),
        OpChainExpr(
          AggIfOpExpr(
            AggOpExpr(
              Count,
              Ref("E1")
            ),
            BinaryOpExpr(BGreaterThan, UnaryOpExpr(GetField("amount"), Ref("E1")), VLong("10"))
          ),
          null
        )
      )
    )
  }

  it("group(A,B).desc(E1.amount).limit(10).sum(E1.num)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("group(A,B).desc(E1.amount).limit(10).sum(E1.num)")
    print(expr.pretty)
    expr should equal(
      OpChainExpr(
        GraphAggregatorExpr(
          "unresolved_default_path",
          List.apply(Ref("A"), Ref("B")),
          null
        ),
        OpChainExpr(
          AggOpExpr(
            Sum,
            UnaryOpExpr(GetField("num"), Ref("E1"))
          ),
          OpChainExpr(
            OrderAndLimit(DescExpr, Limit(UnaryOpExpr(GetField("amount"), Ref("E1")), 10)),
            null
          )
        )
      )
    )
  }

  it("group(A,B).if(E1.amount > E2.amount).sum(E1.num)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("group(A,B).if(E1.amount > E2.amount).sum(E1.num)")
    print(expr.pretty)
    expr should equal(
      OpChainExpr(
        GraphAggregatorExpr(
          "unresolved_default_path",
          List.apply(Ref("A"), Ref("B")),
          null
        ),
        OpChainExpr(
          AggIfOpExpr(
            AggOpExpr(
              Sum,
              UnaryOpExpr(GetField("num"), Ref("E1"))
            ),
            BinaryOpExpr(BGreaterThan,
              UnaryOpExpr(GetField("amount"), Ref("E1")),
              UnaryOpExpr(GetField("amount"), Ref("E2")))
          ),
          null
        )
      )
    )
  }

  it("group(A,B).top(E1.amount, 10)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("group(A,B).top(E1.amount, 10)")
    print(expr.pretty)
    expr should equal(
      OpChainExpr(
        GraphAggregatorExpr(
          "unresolved_default_path",
          List.apply(Ref("A"), Ref("B")),
          null
        ),
        OpChainExpr(
          OrderAndLimit(DescExpr, Limit(UnaryOpExpr(GetField("amount"), Ref("E1")), 10)),
          null
        )
      )
    )
  }

  it("top(E1.amount, 10)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("top(E1.amount, 10)")
    print(expr.pretty)
    expr should equal(
      OrderAndLimit(DescExpr, Limit(UnaryOpExpr(GetField("amount"), Ref("E1")), 10))
    )
  }

  it("group(A,B).sumIf(E1.amount > E2.amount, E1.num)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("group(A,B).sumIf(E1.amount > E2.amount, E1.num)")
    print(expr.pretty)
    val expectResult = OpChainExpr(
      GraphAggregatorExpr(
        "unresolved_default_path",
        List.apply(Ref("A"), Ref("B")),
        null
      ),
      OpChainExpr(
        AggIfOpExpr(
          AggOpExpr(
            Sum,
            UnaryOpExpr(GetField("num"), Ref("E1"))
          ),
          BinaryOpExpr(BGreaterThan,
            UnaryOpExpr(GetField("amount"), Ref("E1")),
            UnaryOpExpr(GetField("amount"), Ref("E2")))
        ),
        null
      )
    ).pretty
    expr.pretty should equal(expectResult)
  }

  it("group(A,B).countIf(E1.amount > E2.amount, E1)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("group(A,B).countIf(E1.amount > E2.amount, E1)")
    print(expr.pretty)

    val expectResult = OpChainExpr(
      GraphAggregatorExpr(
        "unresolved_default_path",
        List.apply(Ref("A"), Ref("B")),
        null
      ),
      OpChainExpr(
        AggIfOpExpr(
          AggOpExpr(
            Count,
            Ref("E1")
          ),
          BinaryOpExpr(BGreaterThan,
            UnaryOpExpr(GetField("amount"), Ref("E1")),
            UnaryOpExpr(GetField("amount"), Ref("E2")))
        ),
        null
      )
    ).pretty
    expr.pretty should equal(expectResult)
  }

  it ("e.nodes()") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("e.nodes()")
    print(expr.pretty)
    val expectResult = OpChainExpr(
      PathOpExpr(
        GetNodesExpr,
        Ref("e")
      ),
      null
    ).pretty
    expr.pretty should equal(expectResult)
  }

  it ("e.edges()") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("e.edges()")
    print(expr.pretty)
    val expectResult = OpChainExpr(
      PathOpExpr(
        GetEdgesExpr,
        Ref("e")
      ), null).pretty
    expr.pretty should equal(expectResult)
  }

  it ("e.nodes().reduce((x, y) => x + y.times, 0)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("e.nodes().reduce((x, y) => x + y.times, 0)")
    print(expr.pretty)
    val expectResult = OpChainExpr(
      ListOpExpr(
        Reduce(
          "y",
          "x",
          BinaryOpExpr(BAdd, Ref("x"), UnaryOpExpr(GetField("times"), Ref("y"))),
          VLong("0")
        ),
        Ref("e")
      ),
      OpChainExpr(
        PathOpExpr(
          GetNodesExpr,
          Ref("e")
        ),
        null
      )
    ).pretty
    expr.pretty should equal(expectResult)
  }

  it ("e.nodes().constraint((cur, pre) => cur.logId == pre.logId)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("e.nodes().constraint((cur, pre) => cur.logId == pre.logId)")
    print(expr.pretty)
    val expectResult = OpChainExpr(
      ListOpExpr(
        Constraint(
          "cur",
          "pre",
          BinaryOpExpr(BEqual,
            UnaryOpExpr(GetField("logId"), Ref("cur")), UnaryOpExpr(GetField("logId"), Ref("pre")))
        ),
        Ref("e")
      ),
      OpChainExpr(
        PathOpExpr(
          GetNodesExpr,
          Ref("e")
        ), null)
    ).pretty
    expr.pretty should equal(expectResult)
  }


  it ("e.nodes().head()") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("e.nodes().head()")
    print(expr.pretty)
    val expectResult = OpChainExpr(
      ListOpExpr(
        Get(0),
        Ref("e")),
      OpChainExpr(
        PathOpExpr(
          GetNodesExpr,
          Ref("e")
        ), null)
    ).pretty
    expr.pretty should equal(expectResult)
  }

  it ("e.nodes().tail()") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("e.nodes().tail()")
    print(expr.pretty)
    val expectResult = OpChainExpr(
      ListOpExpr(
        Get(-1),
        Ref("e")),
      OpChainExpr(
        PathOpExpr(
          GetNodesExpr,
          Ref("e")
        ), null)
    ).pretty
    expr.pretty should equal(expectResult)
  }

  it ("e.nodes().tail(-1)") {
    val exprParser = new RuleExprParser()
    val expr = exprParser.parse("e.nodes().tail(-1)")
    print(expr.pretty)
    val expectResult = OpChainExpr(
      ListOpExpr(
        Get(-2),
        Ref("e")),
      OpChainExpr(
        PathOpExpr(
          GetNodesExpr,
          Ref("e")
        ), null)
    ).pretty
    expr.pretty should equal(expectResult)
  }
}
