package com.antgroup.openspg.reasoner.lube.utils

import com.antgroup.openspg.reasoner.lube.common.expr._
import com.antgroup.openspg.reasoner.lube.common.graph._
import com.antgroup.openspg.reasoner.lube.common.rule.{LogicRule, ProjectRule, Rule}

object RuleUtils {

  /**
   * rename Rule contains variable name by renameFunc
   * @param rule
   * @param renameFunc
   * @return
   */
  def renameVariableInRule(rule: Rule, renameFunc: (String) => String): Rule = {
    if (rule.getDependencies == null || rule.getDependencies.isEmpty) {
      updateRuleExpr(rule, ExprUtils.renameVariableInExpr(rule.getExpr, renameFunc), renameFunc)
    } else {
      var express = Set[Rule]()
      for (depRule <- rule.getDependencies) {
        express += renameVariableInRule(depRule, renameFunc)
      }
      // update dependence
      rule.cleanDependencies
      for (r <- express) {
        rule.addDependency(r)
      }
      updateRuleExpr(rule, ExprUtils.renameVariableInExpr(rule.getExpr, renameFunc), renameFunc)
    }
  }

  /**
   * rename Rule contains variable name
   *
   * @param rule
   * @param renameFunc
   * @return
   */
  def renameVariableInRule(rule: Rule, replaceVar: Map[IRField, IRProperty]): Rule = {
    if (null == rule) {
      return null
    }
    if (rule.getDependencies == null || rule.getDependencies.isEmpty) {
      updateRuleExpr(rule, ExprUtils.renameVariableInExpr(rule.getExpr, replaceVar))
    } else {
      var express = Set[Rule]()
      for (depRule <- rule.getDependencies) {
        express += renameVariableInRule(depRule, replaceVar)
      }
      // update dependence
      rule.cleanDependencies
      for (r <- express) {
        rule.addDependency(r)
      }
      updateRuleExpr(rule, ExprUtils.renameVariableInExpr(rule.getExpr, replaceVar))
    }
  }

  def renameAliasInRule(oldRule: Rule, replaceVar: Map[String, String]): Rule = {
    if (null == oldRule) {
      return null
    }
    val rule = oldRule.clone().asInstanceOf[Rule]
    if (rule.getDependencies == null || rule.getDependencies.isEmpty) {
      updateRuleExpr(rule, ExprUtils.renameAliasInExpr(rule.getExpr, replaceVar))
    } else {
      var express = Set[Rule]()
      for (depRule <- rule.getDependencies) {
        express += renameAliasInRule(depRule, replaceVar)
      }
      // update dependence
      rule.cleanDependencies
      for (r <- express) {
        rule.addDependency(r)
      }
      updateRuleExpr(rule, ExprUtils.renameAliasInExpr(rule.getExpr, replaceVar))
    }
  }

  /**
   * get all input IR Field from rule object
   * @param rule
   * @param nodesAlias
   * @param edgeAlias
   * @return
   */
  def getAllInputFieldInRule(
      rule: Rule,
      nodesAlias: Set[String],
      edgeAlias: Set[String]): List[IRField] = {
    var fields = ExprUtils.getAllInputFieldInRule(rule.getExpr, nodesAlias, edgeAlias)
    if (rule.getDependencies == null || rule.getDependencies.isEmpty) {
      fields
    } else {
      for (depRule <- rule.getDependencies) {
        fields ++= getAllInputFieldInRule(depRule, nodesAlias, edgeAlias)
        // rmv temp variable
        fields = fields
          .map(x =>
            x match {
              case c: IRVariable =>
                if (c.name.equals(depRule.getName)) {
                  null
                } else {
                  c
                }
              case _ => x
            })
          .filter(Option(_).isDefined)
      }
      ExprUtils.mergeListIRField(fields)
    }
  }

  /**
   * helper: rename rule name and expr
   * @param rule
   * @param expr
   * @param renameFunc
   * @return
   */
  private def updateRuleExpr(rule: Rule, expr: Expr, renameFunc: (String) => String): Rule = {
    val ruleNameStr = renameFunc(rule.getName)
    val newRule = rule match {
      case logicRule: LogicRule =>
        LogicRule(ruleNameStr, logicRule.ruleExplain, expr)
      case _ =>
        ProjectRule(IRVariable(ruleNameStr), rule.getLvalueType, expr)
    }
    val oldDependencies = rule.getDependencies
    if (oldDependencies != null) {
      for (d <- oldDependencies) {
        newRule.addDependency(d)
      }
    }
    newRule
  }

  /**
   * helper: rename rule name and expr
   *
   * @param rule
   * @param expr
   * @param renameFunc
   * @return
   */
  private def updateRuleExpr(rule: Rule, expr: Expr): Rule = {
    val newRule = rule match {
      case logicRule: LogicRule =>
        LogicRule(rule.getName, logicRule.ruleExplain, expr)
      case _ =>
        ProjectRule(rule.getOutput, rule.getLvalueType, expr)
    }
    val oldDependencies = rule.getDependencies
    if (oldDependencies != null) {
      for (d <- oldDependencies) {
        newRule.addDependency(d)
      }
    }
    newRule
  }

}
