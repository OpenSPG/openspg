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

package com.antgroup.opensog.reasoner.common.trees

import com.antgroup.openspg.reasoner.common.trees.AbstractTreeNode
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class AbstractTreeNodeTest extends AnyFunSpec {
  it("pretty") {
    val t = Add(Add(Number(4), Number(3)), Add(Number(4), Number(3)))
    t.pretty should equal("""|└─Add
         |    ├─Add
         |    │   ├─Number(v=4)
         |    │   └─Number(v=3)
         |    └─Add
         |        ├─Number(v=4)
         |        └─Number(v=3)""".stripMargin)
  }
}

case class Number(v: Int) extends CalcExpr {
  def eval: Int = v
}

abstract class CalcExpr extends AbstractTreeNode[CalcExpr] {
  def eval: Int
}

case class Add(left: CalcExpr, right: CalcExpr) extends CalcExpr {
  def eval: Int = left.eval + right.eval
}
