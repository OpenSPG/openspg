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

import com.antgroup.openspg.reasoner.common.types.{KgType, KTBoolean, KTDouble, KTInteger, KTLong, KTString}
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.common.expr.{Expr, Ref, UnaryOpExpr}
import com.antgroup.openspg.reasoner.lube.common.graph.{IRField, IRProperty, IRVariable}
import com.antgroup.openspg.reasoner.lube.common.rule.{ProjectRule, Rule}
import com.antgroup.openspg.reasoner.parser.expr.RuleExprParser
import com.antgroup.openspg.reasoner.udf.UdfMngFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class ExprUtilTests extends AnyFunSpec {
  it("test expr trans") {
    val replaceMap = Map.apply(
      "a" -> PropertyVar("b", new Field("id", KTInteger, true)),
      "c" -> PropertyVar("d", new Field("id", KTInteger, true)))
    val r1 = ProjectRule(IRVariable("test"), Ref("a"))
    val r2 = ProjectRule(IRVariable("test"), Ref("c"))
    r1.addDependency(r2)
    val newRule: Rule = ExprUtil.transExpr(r1, replaceMap)
    print(newRule.getExpr.pretty)
    newRule.getExpr.isInstanceOf[UnaryOpExpr] should equal(true)
    newRule.getExpr.asInstanceOf[UnaryOpExpr].arg.isInstanceOf[Ref] should equal(true)
    newRule.getExpr.asInstanceOf[UnaryOpExpr].arg.asInstanceOf[Ref].refName should equal("b")
  }

  it("test expr output type") {
    val parser = new RuleExprParser()
    val udfRepo = UdfMngFactory.getUdfMng
    val map = Map.apply(IRProperty("A", "age") -> KTInteger).asInstanceOf[Map[IRField, KgType]]

    var expr: Expr = parser.parse("A.age")
    ExprUtil.getTargetType(expr, map, udfRepo) should equal(KTInteger)

    expr = parser.parse("A.age + 1")
    ExprUtil.getTargetType(expr, map, udfRepo) should equal(KTLong)

    expr = parser.parse("A.age > 10")
    ExprUtil.getTargetType(expr, map, udfRepo) should equal(KTBoolean)

    expr = parser.parse("floor(A.age)")
    ExprUtil.getTargetType(expr, map, udfRepo) should equal(KTDouble)

    expr = parser.parse("abs(A.age)")
    ExprUtil.getTargetType(expr, map, udfRepo) should equal(KTInteger)

    expr = parser.parse("concat(A.age, \",\")")
    ExprUtil.getTargetType(expr, map, udfRepo) should equal(KTString)

    expr = parser.parse("cast_type(A.age, 'string')")
    ExprUtil.getTargetType(expr, map, udfRepo) should equal(KTString)
  }

  it("test rule output type") {
    val parser = new RuleExprParser()
    val udfRepo = UdfMngFactory.getUdfMng
    val map = Map.apply(IRProperty("A", "age") -> KTInteger).asInstanceOf[Map[IRField, KgType]]

    val rule = ProjectRule(IRVariable("newAge"), parser.parse("age * 10"))
    val r1 = ProjectRule(IRVariable("age"), parser.parse("A.age"))
    rule.addDependency(r1)

    ExprUtil.getTargetType(rule, map, udfRepo) should equal(KTLong)
  }
}
