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

package com.antgroup.openspg.reasoner.lube.utils.transformer.impl

import com.antgroup.openspg.reasoner.common.trees.BottomUp
import com.antgroup.openspg.reasoner.lube.common.expr._
import com.antgroup.openspg.reasoner.lube.common.rule.Rule
import com.antgroup.openspg.reasoner.lube.utils.transformer.RuleTransformer

/**
 * Transforms an rule expression into expr
 */
class Rule2ExprTransformer extends RuleTransformer[Expr] {

  /**
   * transform rule format
   *
   * @param rule
   * @return
   */
  override def transform(rule: Rule): Expr = {
    if (rule.getDependencies == null || rule.getDependencies.isEmpty) {
      rule.getExpr
    } else {
      var exprMap = Map[String, Expr]()
      for (depRule <- rule.getDependencies) {
        val depExpress = transform(depRule)
        exprMap += (depRule.getName -> depExpress)
      }
      def replaceExpr(refName: String): Expr = {
        if (exprMap.contains(refName)) {
          exprMap(refName)
        } else {
          Ref(refName)
        }
      }
      val trans: PartialFunction[Expr, Expr] = {
        case Ref(refName) =>
          replaceExpr(refName)
        case x => x
      }
      BottomUp(trans).transform(rule.getExpr)
    }
  }

}
