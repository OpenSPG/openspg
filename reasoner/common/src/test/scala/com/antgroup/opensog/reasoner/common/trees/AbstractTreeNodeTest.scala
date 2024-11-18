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

import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException
import com.antgroup.openspg.reasoner.common.trees.{AbstractTreeNode, BottomUpWithContext, TopDownWithContext}
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

  it("rewrites with context up") {
    val calculation = Add(Number(5), Add(Number(4), Number(3)))
    val sumOnce: PartialFunction[(CalcExpr, Boolean), (CalcExpr, Boolean)] = {
      case (Add(n1: Number, n2: Number), false) => Number(n1.v + n2.v) -> true
    }

    val expected = Add(Number(5), Number(7)) -> true

    val up = BottomUpWithContext(sumOnce).transform(calculation, false)
    up should equal(expected)

    val down = TopDownWithContext(sumOnce).transform(calculation, false)
    down should equal(expected)
  }

}

case class Number(v: Int) extends CalcExpr {
  def eval: Int = v

  override def withNewChildren(newChildren: Array[CalcExpr]): CalcExpr =
    throw UnsupportedOperationException("unsupported")

  override def children: Array[CalcExpr] = Array.empty
}

abstract class CalcExpr extends AbstractTreeNode[CalcExpr] {
  def eval: Int
}

case class Add(left: CalcExpr, right: CalcExpr) extends CalcExpr {
  def eval: Int = left.eval + right.eval

  override def children: Array[CalcExpr] = Array.apply(left, right)
  override def withNewChildren(newChildren: Array[CalcExpr]): CalcExpr = {
    Add(newChildren.apply(0), newChildren.apply(1))
  }
}
