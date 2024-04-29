package com.antgroup.openspg.reasoner.thinker

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import com.antgroup.openspg.reasoner.KGDSLParser._
import com.antgroup.openspg.reasoner.lube.utils.transformer.impl.Expr2QlexpressTransformer
import com.antgroup.openspg.reasoner.parser.expr.RuleExprParser
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity
import com.antgroup.openspg.reasoner.thinker.logic.rule.{ClauseEntry, EntityPattern, Node}
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.{
  And,
  Condition,
  Not,
  Or,
  QlExpressCondition
}

class ThinkerRuleParser extends RuleExprParser {
  val expr2StringTransformer = new Expr2QlexpressTransformer()

  val conditionToElementMap: mutable.HashMap[Condition, mutable.HashSet[ClauseEntry]] =
    new mutable.HashMap()

  def thinkerParseValueExpression(
      ctx: Value_expressionContext,
      body: ListBuffer[ClauseEntry]): Node = {
    ctx.getChild(0) match {
      case c: Logic_value_expressionContext => thinkerParseLogicValueExpression(c, body)
      case c: Project_value_expressionContext => thinkerParseProjectValueExpression(c, body)
    }
  }

  def thinkerParseLogicValueExpression(
      ctx: Logic_value_expressionContext,
      body: ListBuffer[ClauseEntry]): Node = {
    val orNodeList: List[Node] =
      ctx.logic_term().asScala.toList.map(x => thinkerParseLogicTerm(x, body))
    if (orNodeList.length > 1) {
      new Or(orNodeList.asJava)
    } else {
      orNodeList.head
    }
  }

  def thinkerParseLogicTerm(ctx: Logic_termContext, body: ListBuffer[ClauseEntry]): Node = {
    val andNodeList: List[Node] =
      ctx.logic_factor().asScala.toList.map(x => thinkerParseLogicFactor(x, body))
    if (andNodeList.length > 1) {
      new And(andNodeList.asJava)
    } else {
      andNodeList.head
    }
  }

  def thinkerParseLogicFactor(ctx: Logic_factorContext, body: ListBuffer[ClauseEntry]): Node = {
    var resultNode = thinkerParseLogicTest(ctx.logic_test(), body)
    if (ctx.not() != null) {
      resultNode = new Not(resultNode)
    }
    resultNode
  }

  def insertIntoMap(condition: Condition, element: Set[ClauseEntry]): Unit = {
    val existElementSet: mutable.Set[ClauseEntry] =
      conditionToElementMap.getOrElseUpdate(condition, mutable.HashSet())
    existElementSet ++= element
  }

  def thinkerParseLogicTest(ctx: Logic_testContext, body: ListBuffer[ClauseEntry]): Node = {
    val newBody: ListBuffer[ClauseEntry] = new ListBuffer[ClauseEntry]()
    val resultNode: Node = ctx.getChild(0) match {
      case c: Concept_nameContext =>
        val conceptEntity =
          new Entity(c.concept_instance_id().getText, c.meta_concept_type().getText)
        newBody += new EntityPattern(conceptEntity)
        new QlExpressCondition(
          expr2StringTransformer
            .transform(super.parseLogicTest(ctx))
            .head)
      case c: ExprContext => thinkerParseExpr(c, newBody)
    }
    body ++= newBody
    if (resultNode.isInstanceOf[Condition]) {
      insertIntoMap(resultNode.asInstanceOf[Condition], newBody.toSet)
    }
    resultNode
  }

  def thinkerParseExpr(ctx: ExprContext, body: ListBuffer[ClauseEntry]): Node = {
    ctx.getChild(0) match {
      case c: Binary_exprContext => thinkerParseBinaryExpr(c, body)
      case c: Unary_exprContext => thinkerParseUnaryExpr(c, body)
      case c: Function_exprContext => thinkerParseFunctionExpr(c, body)
    }
  }

  def thinkerParseBinaryExpr(ctx: Binary_exprContext, body: ListBuffer[ClauseEntry]): Node = {
    ctx
      .project_value_expression()
      .asScala
      .foreach(x => thinkerParseProjectValueExpression(x, body))
    new QlExpressCondition(
      expr2StringTransformer
        .transform(super.parseBinaryExpr(ctx))
        .head)
  }

  def thinkerParseProjectValueExpression(
      ctx: Project_value_expressionContext,
      body: ListBuffer[ClauseEntry]): Node = {
    ctx.term().asScala.foreach(term => thinkerParseTerm(term, body))
    new QlExpressCondition(
      expr2StringTransformer.transform(super.parseProjectValueExpression(ctx)).head)
  }

  def thinkerParseTerm(ctx: TermContext, body: ListBuffer[ClauseEntry]): Unit = {
    ctx.factor().asScala.foreach(factor => thinkerParseFactor(factor, body))
  }

  def thinkerParseFactor(ctx: FactorContext, body: ListBuffer[ClauseEntry]): Unit = {
    thinkerParseProjectPrimary(ctx.project_primary(), body)
  }

  def thinkerParseProjectPrimary(
      ctx: Project_primaryContext,
      body: ListBuffer[ClauseEntry]): Unit = {
    ctx.getChild(0) match {
      case c: Concept_nameContext =>
        body += new EntityPattern(
          new Entity(c.concept_instance_id().getText, c.meta_concept_type().getText))
      case c: Value_expression_primaryContext =>
        thinkerParseValueExpressionPrimary(c, body)
      case c: Numeric_value_functionContext =>
        thinkerParseNumericFunction(c, body)
    }
  }

  def thinkerParseValueExpressionPrimary(
      ctx: Value_expression_primaryContext,
      body: ListBuffer[ClauseEntry]): Unit = {
    ctx.getChild(0) match {
      case c: Parenthesized_value_expressionContext =>
        thinkerParseValueExpression(c.value_expression(), body)
      case c: Non_parenthesized_value_expression_primary_with_propertyContext =>
        thinkerParseNonParentValueExpressionPrimaryWithProperty(c, body)
    }
  }

  def thinkerParseNonParentValueExpressionPrimaryWithProperty(
      ctx: Non_parenthesized_value_expression_primary_with_propertyContext,
      body: ListBuffer[ClauseEntry]): Unit = {
    ctx.non_parenthesized_value_expression_primary().getChild(0) match {
      case c: Function_exprContext => thinkerParseFunctionExpr(c, body)
      case c: Binding_variableContext =>
        thinkParseBindingVariable(c, body, ctx.property_name().asScala.toList)
      case _ =>
    }
  }

  def thinkParseBindingVariable(
      ctx: Binding_variableContext,
      body: ListBuffer[ClauseEntry],
      propertyNameList: List[Property_nameContext]): Unit = {
    if (propertyNameList != null && propertyNameList.nonEmpty) {
      /*
       * 可能是多级属性如 A.index.blood
      val subject = new logic.graph.Node(ctx.binding_variable_name().getText)
      val predicate = new Predicate(propertyNameList.head.getText)
      val o = new graph.Any()
      body += new logic.graph.Triple(subject, predicate, o)
       */
    }
  }

  def thinkerParseFunctionExpr(ctx: Function_exprContext, body: ListBuffer[ClauseEntry]): Node = {
    val argsContext: Function_argsContext = ctx.function_args()
    if (null != argsContext) {
      argsContext
        .list_element_list()
        .list_element()
        .asScala
        .foreach(x => {
          this.thinkerParseValueExpression(x.value_expression(), body)
        })
    }
    new QlExpressCondition(expr2StringTransformer.transform(super.parseFunctionExpr(ctx)).head)
  }

  def thinkerParseNumericFunction(
      ctx: Numeric_value_functionContext,
      body: ListBuffer[ClauseEntry]): Unit = {
    ctx.getChild(0) match {
      case c: Absolute_value_expressionContext =>
        thinkerParseProjectValueExpression(c.project_value_expression(), body)
      case c: Floor_functionContext =>
        thinkerParseProjectValueExpression(c.project_value_expression(), body)
      case c: Ceiling_functionContext =>
        thinkerParseProjectValueExpression(c.project_value_expression(), body)
    }
  }

  def thinkerParseUnaryExpr(ctx: Unary_exprContext, body: ListBuffer[ClauseEntry]): Node = {
    this.thinkerParseValueExpression(ctx.value_expression(), body)
  }

}
