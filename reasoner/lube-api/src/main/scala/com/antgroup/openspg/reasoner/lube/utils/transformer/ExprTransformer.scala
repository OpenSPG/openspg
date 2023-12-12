package com.antgroup.openspg.reasoner.lube.utils.transformer

import com.antgroup.openspg.reasoner.lube.common.expr.Expr
import com.antgroup.openspg.reasoner.lube.common.rule.Rule

/**
 * Transforms an expression into a different style
 *
 * @tparam A
 */
trait ExprTransformer[A] {

  /**
   * Transform Expr to other format list
   * @param expr
   * @return
   */
  def transform(expr: Expr): List[A]

  /**
   * Transform Expr to other format list
   * @param expr
   * @return
   */
  def transform2OtherFormat(expr: Expr): A = {
    transform(expr).head
  }

  /**
   * transform rule format
   * @param rule
   * @return
   */
  def transform(rule: Rule): List[A]

  /**
   * transform rule to another format
   * @param rule
   * @return
   */
  def transform2OtherFormat(rule: Rule): A = {
    transform(rule).head
  }

}
