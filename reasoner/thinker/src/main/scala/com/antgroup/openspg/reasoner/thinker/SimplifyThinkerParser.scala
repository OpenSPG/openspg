package com.antgroup.openspg.reasoner.thinker

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import com.antgroup.openspg.reasoner.KGDSLParser._
import com.antgroup.openspg.reasoner.parser.{LexerInit, OpenSPGDslParser}
import com.antgroup.openspg.reasoner.thinker.logic.graph.{Element, Entity, Triple}
import com.antgroup.openspg.reasoner.thinker.logic.rule.{
  ClauseEntry,
  EntityPattern,
  Node,
  Rule,
  TriplePattern
}
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.{Condition, Or}

class SimplifyThinkerParser {
  var param: Map[String, Object] = Map.empty
  var thinkerRuleParser: ThinkerRuleParser = new ThinkerRuleParser()

  private var conditionToElementMap: mutable.HashMap[Condition, mutable.HashSet[ClauseEntry]] =
    new mutable.HashMap()

  def parseSimplifyDsl(
      simplifyDSL: String,
      param: Map[String, Object] = Map.empty): List[Rule] = {
    val parser = new LexerInit().initKGReasonerParser(simplifyDSL)
    this.param = param
    conditionToElementMap = mutable.HashMap()
    thinkerRuleParser = new ThinkerRuleParser()
    parseScript(parser.thinker_script())
  }

  def parseScript(ctx: Thinker_scriptContext): List[Rule] = {
    val ruleResult: mutable.ListBuffer[Rule] = mutable.ListBuffer.empty
    if (ctx.define_rule_on_concept() != null && ctx.define_rule_on_concept().size() > 0) {
      ctx
        .define_rule_on_concept()
        .asScala
        .foreach(rule => {
          ruleResult += parseDefineRuleOnConcept(rule)
        })
    }
    if (ctx.define_rule_on_relation_to_concept() != null
      && ctx.define_rule_on_relation_to_concept().size() > 0) {
      ctx
        .define_rule_on_relation_to_concept()
        .asScala
        .foreach(rule => {
          ruleResult += parseDefineRuleOnRelationToConcept(rule)
        })
    }
    if (ctx.define_proiority_rule_on_concept() != null
      && ctx.define_proiority_rule_on_concept().size() > 0) {
      ctx
        .define_proiority_rule_on_concept()
        .asScala
        .foreach(rule => {
          ruleResult += parseDefinePriorityRuleOnConcept(rule)
        })
    }
    ruleResult.toList
  }

  def parseDefineRuleOnConcept(ctx: Define_rule_on_conceptContext): Rule = {
    val rule = new Rule()
    rule.setHead(
      new EntityPattern[Void](
        new Entity(
          null,
          ctx
            .define_rule_on_concept_structure()
            .concept_declaration()
            .concept_name()
            .getText)))
    if (null != ctx.description()) {
      rule.setDesc(ctx.description().unbroken_character_string_literal().getText)
    }
    val ruleAndAction: Rule_and_action_bodyContext =
      ctx.define_rule_on_concept_structure().rule_and_action_body()
    parseRuleAndAction(ruleAndAction, rule)
    rule
  }

  def parseRuleAndAction(ruleAndAction: Rule_and_action_bodyContext, rule: Rule): Unit = {
    if (ruleAndAction.rule_body_content() != null) {
      val multiLogicalStatementContext = ruleAndAction.rule_body_content().logical_statement()
      if (multiLogicalStatementContext != null && multiLogicalStatementContext.size() > 0) {
        val (root, body) = parseMultiLogicalStatement(multiLogicalStatementContext.asScala.toList)
        rule.setRoot(root)
        rule.setBody(body.asJava)
      }
    }
  }

  def parseMultiLogicalStatement(
      ctx: List[Logical_statementContext]): (Node, List[ClauseEntry]) = {
    val body: ListBuffer[ClauseEntry] = new mutable.ListBuffer[ClauseEntry]()
    if (ctx.length > 1) {
      val orChildrenList: ListBuffer[Node] = new mutable.ListBuffer[Node]()
      ctx.foreach(logicalStatement => {
        orChildrenList += parseOneLogicalStatement(logicalStatement, body)
      })

      val or = new Or()
      or.setChildren(orChildrenList.toList.asJava)
      (or, body.distinct.toList)
    } else {
      (parseOneLogicalStatement(ctx.head, body), body.distinct.toList)
    }
  }

  def parseOneLogicalStatement(
      ctx: Logical_statementContext,
      body: ListBuffer[ClauseEntry]): Node = {
    val node = thinkerRuleParser.thinkerParseValueExpression(ctx.value_expression(), body)
    conditionToElementMap ++= thinkerRuleParser.conditionToElementMap
    node
  }

  def parseDefineRuleOnRelationToConcept(ctx: Define_rule_on_relation_to_conceptContext): Rule = {
    val rule = new Rule()
    val subject = ctx
      .define_rule_on_relation_to_concept_structure()
      .variable_declaration()
      .entity_type()
      .getText
    val predicate = ctx
      .define_rule_on_relation_to_concept_structure()
      .rule_name_declaration()
      .identifier()
      .getText
    val o = ctx
      .define_rule_on_relation_to_concept_structure()
      .concept_declaration()
      .concept_name()
      .getText

    rule.setHead(
      new TriplePattern(
        new Triple(
          new Entity[Void](null, subject),
          new Entity[Void](null, predicate),
          new Entity[Void](null, o))))
    if (ctx.description() != null) {
      rule.setDesc(ctx.description().unbroken_character_string_literal().getText)
    }

    val ruleAndAction: Rule_and_action_bodyContext =
      ctx.define_rule_on_relation_to_concept_structure().rule_and_action_body()
    parseRuleAndAction(ruleAndAction, rule)
    rule
  }

  def parseDefinePriorityRuleOnConcept(ctx: Define_proiority_rule_on_conceptContext): Rule = {
    throw new UnsupportedOperationException("DefinePriority not support yet")
  }

  def getConditionToElementMap(): Map[Condition, Set[ClauseEntry]] = {
    conditionToElementMap.toMap.map(x => (x._1, x._2.toSet))
  }

}
