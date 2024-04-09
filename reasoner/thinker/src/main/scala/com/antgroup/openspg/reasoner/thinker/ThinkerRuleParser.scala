package com.antgroup.openspg.reasoner.thinker

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

import com.antgroup.openspg.reasoner.lube.utils.transformer.impl.Expr2QlexpressTransformer
import com.antgroup.openspg.reasoner.thinker.SimplifyDSLParser._
import com.antgroup.openspg.reasoner.thinker.logic.graph
import com.antgroup.openspg.reasoner.thinker.logic.graph.{Concept, Element, Variable}
import com.antgroup.openspg.reasoner.thinker.logic.rule.Node
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.{And, Not, Or, QlExpressCondition}

class ThinkerRuleParser extends OpenSPGRuleExprParser {
  val expr2StringTransformer = new Expr2QlexpressTransformer()

  def thinkerParseValueExpression(
      ctx: Value_expressionContext,
      body: ListBuffer[Element]): Node = {
    ctx.getChild(0) match {
      case c: Logic_value_expressionContext => thinkerParseLogicValueExpression(c, body)
      case c: Project_value_expressionContext => thinkerParseProjectValueExpression(c, body)
    }

    new Or()
  }

  def thinkerParseLogicValueExpression(
      ctx: Logic_value_expressionContext,
      body: ListBuffer[Element]): Node = {
    val orNodeList: List[Node] =
      ctx.logic_term().asScala.toList.map(x => thinkerParseLogicTerm(x, body))
    if (orNodeList.length > 1) {
      new Or(orNodeList.asJava)
    } else {
      orNodeList.head
    }
  }

  def thinkerParseLogicTerm(ctx: Logic_termContext, body: ListBuffer[Element]): Node = {
    val andNodeList: List[Node] =
      ctx.logic_factor().asScala.toList.map(x => thinkerParseLogicFactor(x, body))
    if (andNodeList.length > 1) {
      new And(andNodeList.asJava)
    } else {
      andNodeList.head
    }
  }

  def thinkerParseLogicFactor(ctx: Logic_factorContext, body: ListBuffer[Element]): Node = {
    var resultNode = thinkerParseLogicTest(ctx.logic_test(), body)
    if (ctx.not() != null) {
      resultNode = new Not(resultNode)
    }
    resultNode
  }

  def thinkerParseLogicTest(ctx: Logic_testContext, body: ListBuffer[Element]): Node = {
    ctx.getChild(0) match {
      case c: Concept_nameContext =>
        body += new Concept(c.getText)
        new QlExpressCondition(
          expr2StringTransformer
            .transform(super.parseLogicTest(ctx))
            .head)
      case c: ExprContext => thinkerParseExpr(c, body)
    }
  }

  def replaceConceptName(conceptName: String): String = {
    conceptName.replace("/", "_")
  }

  def thinkerParseExpr(ctx: ExprContext, body: ListBuffer[Element]): Node = {
    ctx.getChild(0) match {
      case c: Binary_exprContext => thinkerParseBinaryExpr(c, body)
      case c: Unary_exprContext => thinkerParseUnaryExpr(c, body)
      case c: Function_exprContext => thinkerParseFunctionExpr(c, body)
    }
  }

  def thinkerParseBinaryExpr(ctx: Binary_exprContext, body: ListBuffer[Element]): Node = {
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
      body: ListBuffer[Element]): Node = {
    ctx.term().asScala.foreach(term => thinkerParseTerm(term, body))
    new QlExpressCondition(
      expr2StringTransformer.transform(super.parseProjectValueExpression(ctx)).head)
  }

  def thinkerParseTerm(ctx: TermContext, body: ListBuffer[Element]): Unit = {
    ctx.factor().asScala.foreach(factor => thinkerParseFactor(factor, body))
  }

  def thinkerParseFactor(ctx: FactorContext, body: ListBuffer[Element]): Unit = {
    thinkerParseProjectPrimary(ctx.project_primary(), body)
  }

  def thinkerParseProjectPrimary(ctx: Project_primaryContext, body: ListBuffer[Element]): Unit = {
    ctx.getChild(0) match {
      case c: Concept_nameContext => body += new Concept(c.getText)
      case c: Value_expression_primaryContext =>
        thinkerParseValueExpressionPrimary(c, body)
      case c: Numeric_value_functionContext =>
        thinkerParseNumericFunction(c, body)
    }
  }

  def thinkerParseValueExpressionPrimary(
      ctx: Value_expression_primaryContext,
      body: ListBuffer[Element]): Unit = {
    ctx.getChild(0) match {
      case c: Parenthesized_value_expressionContext =>
        thinkerParseValueExpression(c.value_expression(), body)
      case c: Non_parenthesized_value_expression_primary_with_propertyContext =>
        thinkerParseNonParentValueExpressionPrimaryWithProperty(c, body)
    }
  }

  def thinkerParseNonParentValueExpressionPrimaryWithProperty(
      ctx: Non_parenthesized_value_expression_primary_with_propertyContext,
      body: ListBuffer[Element]): Unit = {
    val hasProperty: Boolean = ctx.property_name() != null && !ctx.property_name().isEmpty
    ctx.non_parenthesized_value_expression_primary().getChild(0) match {
      case c: Binding_variableContext =>
        thinkParseBindingVariable(c, body, hasProperty)
      case c: Unsigned_value_specificationContext =>
        thinkParseUValueSpecification(c, body)
      case c: Function_exprContext => thinkerParseFunctionExpr(c, body)
    }
  }

  def thinkParseBindingVariable(
      ctx: Binding_variableContext,
      body: ListBuffer[Element],
      hasProperty: Boolean): Unit = {
    if (hasProperty) {
      body += new graph.Node(ctx.binding_variable_name().getText)
    } else {
      body += new Variable(ctx.binding_variable_name().getText)
    }
  }

  def thinkParseUValueSpecification(
      ctx: Unsigned_value_specificationContext,
      body: ListBuffer[Element]): Unit = {
    ctx.getChild(0) match {
      case c: Parameter_value_specificationContext =>
        body += new Variable(ctx.parameter_value_specification().getText)
      case _ =>
    }
  }

  def thinkerParseFunctionExpr(ctx: Function_exprContext, body: ListBuffer[Element]): Node = {
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
      body: ListBuffer[Element]): Unit = {
    ctx.getChild(0) match {
      case c: Absolute_value_expressionContext =>
        thinkerParseProjectValueExpression(c.project_value_expression(), body)
      case c: Floor_functionContext =>
        thinkerParseProjectValueExpression(c.project_value_expression(), body)
      case c: Ceiling_functionContext =>
        thinkerParseProjectValueExpression(c.project_value_expression(), body)
    }
  }

  def thinkerParseUnaryExpr(ctx: Unary_exprContext, body: ListBuffer[Element]): Node = {
    this.thinkerParseValueExpression(ctx.value_expression(), body)
  }

}
