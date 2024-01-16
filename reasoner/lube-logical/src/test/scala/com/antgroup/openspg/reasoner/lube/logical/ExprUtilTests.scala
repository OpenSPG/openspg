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

import com.antgroup.openspg.reasoner.common.types.KTInteger
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.common.expr.{GetField, Ref, UnaryOpExpr}
import com.antgroup.openspg.reasoner.lube.common.graph.IRVariable
import com.antgroup.openspg.reasoner.lube.common.rule.{ProjectRule, Rule}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class ExprUtilTests extends AnyFunSpec {
  it("test expr trans") {
    val replaceMap = Map.apply(
      "a" -> PropertyVar("b", new Field("id", KTInteger, true)),
      "c" -> PropertyVar("d", new Field("id", KTInteger, true)))
    val r1 = ProjectRule(IRVariable("test"),
      KTInteger, Ref("a"))
    val r2 = ProjectRule(IRVariable("test"),
      KTInteger, Ref("c"))
    r1.addDependency(r2)
    val newRule: Rule = ExprUtil.transExpr(r1, replaceMap)
    print(newRule.getExpr.pretty)
    newRule.getExpr.isInstanceOf[UnaryOpExpr] should equal(true)
    newRule.getExpr.asInstanceOf[UnaryOpExpr].arg.isInstanceOf[Ref] should equal(true)
    newRule.getExpr.asInstanceOf[UnaryOpExpr]
      .arg.asInstanceOf[Ref].refName should equal("b")
  }
}
