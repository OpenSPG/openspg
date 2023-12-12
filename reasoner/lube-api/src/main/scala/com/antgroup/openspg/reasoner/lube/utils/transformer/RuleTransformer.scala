package com.antgroup.openspg.reasoner.lube.utils.transformer

import com.antgroup.openspg.reasoner.lube.common.rule.Rule

/**
 * Transforms an rule expression into a different style
 *
 * @tparam A
 */
trait RuleTransformer[A] {

  /**
   * transform rule format
   * @param rule
   * @return
   */
  def transform(rule: Rule): A
}
