package com.antgroup.openspg.reasoner.lube.logical.optimizer.rules

import com.antgroup.openspg.reasoner.common.exception.InvalidGraphException
import com.antgroup.openspg.reasoner.common.types.KTString
import com.antgroup.openspg.reasoner.lube.common.expr.{BinaryOpExpr, _}
import com.antgroup.openspg.reasoner.lube.common.pattern.{NodePattern, PartialGraphPattern, PatternElement}
import com.antgroup.openspg.reasoner.lube.common.rule.LogicRule
import com.antgroup.openspg.reasoner.lube.logical.operators.{ExpandInto, LogicalOperator, PatternScan}
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, SimpleRule, Up}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext
import scala.collection.mutable

object ConvertToMetaConcept extends SimpleRule {

  override def rule(implicit
      context: LogicalPlannerContext): PartialFunction[LogicalOperator, LogicalOperator] = {
    case patternScan @ PatternScan(_, pattern) =>
        pattern match {
          case nodePattern @ NodePattern(node) =>
            val newPattern = nodePattern.copy(node = toMetaConcept(node))
            patternScan.copy(pattern = newPattern)
          case graphPattern @ PartialGraphPattern(_, nodes, edges) =>
            var newNodes = nodes
            val needConvert = mutable.HashSet[String]()
            edges.foreach(e =>
              e._2.filter(_.relTypes.contains("belongTo")).foreach(c => needConvert.add(c.target)))
            needConvert.foreach(alias => {
              newNodes = newNodes.updated(alias, toMetaConcept(nodes(alias)))
            })
            val newPattern = graphPattern.copy(nodes = newNodes)
            patternScan.copy(pattern = newPattern)
          case _ =>
            patternScan
        }

    case expandInto @ ExpandInto(_, target, pattern) =>
      val newTarget = toMetaConcept(target)
      val newPattern = pattern match {
        case nodePattern @ NodePattern(node) =>
          nodePattern.copy(node = toMetaConcept(node))
        case graphPattern @ PartialGraphPattern(_, nodes, edges) =>
          var newNodes = nodes
          val needConvert = mutable.HashSet[String]()
          edges.foreach(e =>
            e._2.filter(_.relTypes.contains("belongTo")).foreach(c => needConvert.add(c.target)))
          needConvert.foreach(alias => {
            newNodes = newNodes.updated(alias, toMetaConcept(nodes(alias)))
          })
          graphPattern.copy(nodes = newNodes)
        case _ =>
          pattern
      }
      expandInto.copy(target = newTarget, pattern = newPattern)
  }

  private def toMetaConcept(patternElement: PatternElement): PatternElement = {
    val types = patternElement.typeNames.filter(_.contains('/'))
    if (types.isEmpty) {
      patternElement
    } else {
      val metaConcepts = types.map(_.split("/").head)
      if (metaConcepts.size > 1) {
        throw InvalidGraphException("Entities types must belong to the same meta concept")
      }
      val Ids = types.map(_.split(metaConcepts.head + "/").last)
      val rule =
        LogicRule(
          "metaConceptRule",
          "belongToConcept",
          BinaryOpExpr(BIn, Ref(patternElement.alias + ".id"), VList(Ids.toList, KTString)))
      patternElement.copy(typeNames = metaConcepts, rule = rule)

    }
  }
  override def direction: Direction = Up

  override def maxIterations: Int = 1

}
