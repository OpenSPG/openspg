package com.antgroup.openspg.reasoner.thinker.util

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.trees.BottomUp
import com.antgroup.openspg.reasoner.lube.common.expr.{Expr, FunctionExpr, VString}
import com.antgroup.openspg.reasoner.parser.expr.RuleExprParser
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity

object ThinkerConditionUtil {
  val ruleExprParser = new RuleExprParser()

  def parseAllConceptInCondition(condition: String): Set[Entity] = {
    val conceptList: mutable.ListBuffer[Entity] = new mutable.ListBuffer()
    val trans: PartialFunction[Expr, Expr] = {
      case f @ FunctionExpr(name, args) =>
        if (name.equals("get_value") && args.length == 1 && args.head.isInstanceOf[VString]) {
          val conceptName: String = args.head.asInstanceOf[VString].value
          val splits = conceptName.split("/", 2)
          val metaConcept = splits(0)
          val conceptInstance = splits(1)
          conceptList += new Entity(conceptInstance, metaConcept)
        }
        f
      case x => x
    }
    BottomUp(trans).transform(ruleExprParser.parse(condition))
    conceptList.toSet
  }

}
