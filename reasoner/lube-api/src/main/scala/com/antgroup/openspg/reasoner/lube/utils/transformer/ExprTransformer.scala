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
