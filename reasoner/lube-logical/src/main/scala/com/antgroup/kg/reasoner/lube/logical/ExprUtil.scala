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

package com.antgroup.openspg.reasoner.lube.logical

import com.antgroup.openspg.reasoner.common.trees.BottomUp
import com.antgroup.openspg.reasoner.lube.common.expr._
import com.antgroup.openspg.reasoner.lube.common.rule.Rule

object ExprUtil {

  def getReferProperties(rule: Rule): List[Tuple2[String, String]] = {
    if (rule == null) {
      List.empty
    } else {
      getReferProperties(rule.getExpr)
    }
  }

  /**
   * @param rule
   * @return
   */
  def getReferProperties(rule: Expr): List[Tuple2[String, String]] = {
    if (rule == null) {
      List.empty
    } else {
      rule.transform[List[Tuple2[String, String]]] {
        case (Ref(name), _) => List.apply((null, name))
        case (UnaryOpExpr(GetField(name), Ref(alis)), _) => List.apply((alis, name))
        case (BinaryOpExpr(_, Ref(left), Ref(right)), _) =>
          List.apply((null, left), (null, right))
        case (_, tupleList) => tupleList.flatten
      }
    }

  }


  def needResolved(rule: Expr): Boolean = {
    !getReferProperties(rule).filter(_._1 == null).isEmpty
  }

  def transExpr(rule: Expr, replaceVar: Map[String, PropertyVar]): Expr = {

    def rewriter: PartialFunction[Expr, Expr] = {
    case Ref(refName) =>
      if (replaceVar.contains(refName)) {
        val propertyVar = replaceVar(refName)
        UnaryOpExpr(GetField(propertyVar.field.name), Ref(propertyVar.name))
      } else {
        Ref(refName)
      }

    }

    BottomUp(rewriter).transform(rule)
  }

  def transExpr(rule: Rule, replaceVar: Map[String, PropertyVar]): Rule = {
    val newRule = rule.updateExpr(transExpr(rule.getExpr, replaceVar))
    newRule.cleanDependencies
    for (dependency <- rule.getDependencies) {
      newRule.addDependency(transExpr(dependency, replaceVar))
    }
    newRule
  }

}
