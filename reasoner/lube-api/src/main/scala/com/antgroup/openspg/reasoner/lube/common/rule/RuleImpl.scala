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

package com.antgroup.openspg.reasoner.lube.common.rule

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException
import com.antgroup.openspg.reasoner.common.types.{KgType, KTBoolean}
import com.antgroup.openspg.reasoner.lube.common.expr.{BAnd, BinaryOpExpr, Expr}
import com.antgroup.openspg.reasoner.lube.common.graph.{IRField, IRProperty, IRVariable}

/**
 * this is rule expression class with dependencies
 */
abstract class DependencyRule extends Rule {
  val dependencies = mutable.Set[Rule]()

  /**
   * get dependencies
   *
   * @return
   */
  override def getDependencies: Set[Rule] = dependencies.toSet

  /**
   * add dependency rule
   *
   * @param rule
   */
  override def addDependency(rule: Rule): Unit = dependencies += rule

  /**
   * clean all dependencise
   */
  override def cleanDependencies: Unit = dependencies.clear()
}

/**
 * logic compute rule, like "R1('explain'): a and b"
 *
 * @param ruleName
 * @param ruleExplain
 * @param lvalueType
 * @param expr
 */
final case class LogicRule(ruleName: String, ruleExplain: String, expr: Expr)
    extends DependencyRule {

  /**
   * get the rule name
   *
   * @return
   */
  override def getName: String = ruleName

  /**
   * get the expr expression
   *
   * @return
   */
  override def getExpr: Expr = expr

  /**
   * get lvalue type
   *
   * @return
   */
  override def getLvalueType: KgType = KTBoolean

  override def andRule(rule: Rule): Rule = {
    val andExpr = BinaryOpExpr(BAnd, getExpr, rule.getExpr)

    val newRuleExplain = rule match {
      case logicRule: LogicRule =>
        ruleExplain + " and " + logicRule.ruleExplain
      case _ => ruleExplain
    }
    val newRule = LogicRule(ruleName + " and " + rule.getName, newRuleExplain, andExpr)

    for (r <- dependencies) {
      newRule.addDependency(r)
    }
    for (r <- rule.getDependencies) {
      newRule.addDependency(r)
    }
    newRule
  }

  /**
   * get the rule name
   *
   * @return
   */
  override def getOutput: IRField = IRVariable(ruleName)

  override def updateExpr(newExpr: Expr): Rule = {
    val newRule = this.copy(expr = newExpr)
    for (dependency <- this.dependencies) {
      newRule.addDependency(dependency)
    }
    newRule
  }

  override def clone(): LogicRule = {
    val newRule = this.copy()
    for (rule <- this.getDependencies) {
      newRule.addDependency(rule)
    }
    newRule
  }

}

/**
 * compute rule, like "a = A.age + 1"
 *
 * @param lvalueName
 * @param lvalueType
 * @param expr
 */
final case class ProjectRule(output: IRField, lvalueType: KgType, expr: Expr)
    extends DependencyRule {

  /**
   * get the rule name
   *
   * @return
   */
  override def getName: String = {
    getOutput match {
      case IRProperty(name, field) => "tmp_property2variable_prefix_" + name + "_" + field
      case _ => getOutput.name
    }
  }

  /**
   * get the rule name
   *
   * @return
   */
  override def getOutput: IRField = output

  /**
   * get the expr expression
   *
   * @return
   */
  override def getExpr: Expr = expr

  /**
   * get lvalue type
   *
   * @return
   */
  override def getLvalueType: KgType = lvalueType

  override def andRule(rule: Rule): Rule = {
    throw UnsupportedOperationException("ProjectRule cannot support andRule")
  }

  override def updateExpr(newExpr: Expr): Rule = {
    val newRule = this.copy(expr = newExpr)
    for (dependency <- this.dependencies) {
      newRule.addDependency(dependency)
    }
    newRule
  }

  override def clone(): ProjectRule = {
    val newRule = this.copy()
    for (rule <- this.getDependencies) {
      newRule.addDependency(rule)
    }
    newRule
  }

}
