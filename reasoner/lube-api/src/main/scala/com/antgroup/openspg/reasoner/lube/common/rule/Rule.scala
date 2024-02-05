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

package com.antgroup.openspg.reasoner.lube.common.rule

import com.antgroup.openspg.reasoner.common.types.KgType
import com.antgroup.openspg.reasoner.lube.common.expr.Expr
import com.antgroup.openspg.reasoner.lube.common.graph.IRField

/**
 * parse one line rule from "Rule"
 */
trait Rule extends Cloneable{
  /**
   * get the rule name field
   * @return
   */
  def getOutput: IRField

  /**
   * get the rule name
   * @return
   */
  def getName: String

  /**
   * get the expr expression
   * @return
   */
  def getExpr: Expr


  /**
   * get lvalue type
   * @return
   */
  def getLvalueType: KgType

  /**
   * get dependencies
   * @return
   */
  def getDependencies: Set[Rule]

  /**
   * clean all dependencise
   */
  def cleanDependencies: Unit

  /**
   * add dependency rule
   * @param rule
   */
  def addDependency(rule: Rule): Unit

  def andRule(rule: Rule): Rule

  def updateExpr(newExpr: Expr): Rule

  override def clone(): AnyRef = super.clone()
}
