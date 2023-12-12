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
