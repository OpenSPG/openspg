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

package com.antgroup.openspg.reasoner.parser.expr

import java.util.Locale

import scala.collection.JavaConverters._

import com.antgroup.openspg.reasoner.KGDSLParser._
import com.antgroup.openspg.reasoner.common.exception.KGDSLGrammarException
import com.antgroup.openspg.reasoner.common.types._
import com.antgroup.openspg.reasoner.lube.common.expr._
import com.antgroup.openspg.reasoner.lube.common.graph.{IRProperty, IRVariable}
import com.antgroup.openspg.reasoner.lube.common.rule.{LogicRule, ProjectRule, Rule}
import com.antgroup.openspg.reasoner.parser.LexerInit
import com.antgroup.openspg.reasoner.udf.UdfMngFactory


/**
 * This is the ANTLR parsing class for expressions
 */
class RuleExprParser extends Serializable {
  var parameters = Set[String]()
  var idFilterParameters: Map[String, String] = Map.empty
  var udfMetas = UdfMngFactory.getUdfMng.getAllUdfMeta.asScala

  /**
   * parse one line rule to Expr
   * @param s
   * @return
   */
  def parse(s: String): Expr = {
    val parser = new LexerInit().initKGReasonerParser(s)
    val ctx = parser.expression_set()
    parseExpressionSet(ctx)
  }

  /**
   * helper function: translate function name to udf name
   * @param name
   * @return
   */
  def getFunctionNames(name: String): String = {
    for (udfMeta <- udfMetas) {
      if (udfMeta.getCompatibleNames.contains(name)) {
        return udfMeta.getName
      }
      if (udfMeta.getName.equals(name)) {
        return udfMeta.getName
      }
    }
    name
  }

  /**
   * helper function: translate string boolean to VBoolean
   * @param s
   * @return
   */
  def str2bool(s: String): Expr = {
    s.toUpperCase(Locale.ROOT) match {
      case "TRUE" => VBoolean("true")
      case "FALSE" => VBoolean("false")
      case "NULL" => VNull
    }
  }

  /**
   * helper function: remove front and tail quote
   * @param s
   * @return
   */
  def removeFrontAndTailQuote(s: String): String = {
    val isQuote = (c: Char) => c == '\'' || c == '"'
    if (isQuote(s(0)) && isQuote(s(s.length - 1))) {
      s.substring(1, s.length - 1)
    } else {
      throw new KGDSLGrammarException(s + " need quote, like `" + s + "`")
    }
  }

  // All functions below are parsing and processing functions for ANTLR

  def expressionTreeBuilder(exprList: List[Expr], operList: List[String]): Expr = {
    if (exprList.length != operList.length + 1) {
      throw new KGDSLGrammarException("expr list length not equal op list + 1")
    }
    val expr1 = exprList.head
    val tupleList = (exprList.slice(1, exprList.length), operList: List[String]).zipped.toList

    tupleList.foldLeft(expr1: Expr)((B: Expr, A: (Expr, String)) =>
      A._2 match {
        case "+" => BinaryOpExpr(BAdd, B, A._1)
        case "-" => BinaryOpExpr(BSub, B, A._1)
        case "*" => BinaryOpExpr(BMul, B, A._1)
        case "/" => BinaryOpExpr(BDiv, B, A._1)
        case "%" => BinaryOpExpr(BMod, B, A._1)
      })
  }

  def parseBindingVariable(ctx: Binding_variableContext): Expr = {
    Ref(ctx.binding_variable_name().getText)
  }

  def parseUNumericLiteral(context: Unsigned_numeric_literalContext): Expr = {
    context.oC_NumberLiteral().getChild(0) match {
      case c: OC_DoubleLiteralContext =>
        VDouble(c.getText)
      case c: OC_IntegerLiteralContext =>
        VLong(c.getText)
    }
  }

  def parseUnbrokenCharacterStringLiteral(
      ctx: Unbroken_character_string_literalContext): String = {
    removeFrontAndTailQuote(ctx.StringLiteral().getText)
  }

  def parseCharacterStrLiteral(ctx: Character_string_literalContext): VString = {
    val s = ctx
      .unbroken_character_string_literal()
      .asScala
      .map(x => parseUnbrokenCharacterStringLiteral(x))
      .reduce(_ + _)
    VString(s)
  }

  def parsePreTypeLiteral(context: Predefined_type_literalContext): Expr = {
    context.getChild(0) match {
      case c: Boolean_literalContext =>
        str2bool(c.getText)
      case c: Character_string_literalContext =>
        parseCharacterStrLiteral(c)
    }
  }

  def parseListConstValue(exprList: List[Expr]): VList = {
    var listType: KgType = null
    val strList = exprList.map {
      case VLong(value) =>
        if (listType == null) {
          listType = KTLong
        }
        if (listType != KTLong && listType != KTDouble) {
          throw new KGDSLGrammarException(listType + " is not in [KTLong, KTDouble]")
        }
        value
      case VString(value) =>
        if (listType == null) {
          listType = KTString
        }
        if (listType != KTString) {
          throw new KGDSLGrammarException(listType + " is not in [KTString]")
        }
        value
      case VDouble(value) =>
        if (listType == null || listType == KTDouble) {
          listType = KTDouble
        }
        if (listType != KTDouble) {
          throw new KGDSLGrammarException(listType + " is not in [KTDouble]")
        }
        value
      case VBoolean(value) =>
        if (listType == null) {
          listType = KTBoolean
        }
        if (listType != KTBoolean) {
          throw new KGDSLGrammarException(listType + " is not in [KTBoolean]")
        }
        value
      case VList(list, listType) =>
        // scalastyle:off throwerror
        throw new NotImplementedError("not impl list")
      // scalastyle:off throwerror
      case _ => throw new NotImplementedError("not impl other types")
    }
    VList(strList, listType)
  }

  def parseListLiteral(context: List_literalContext): Expr = {
    val exprList: List[Expr] =
      context
        .list_element_list()
        .list_element()
        .asScala
        .toList
        .map(x => parseValueExpression(x.value_expression()))
    parseListConstValue(exprList)
  }

  def parseUValueSpecification(ctx: Unsigned_value_specificationContext): Expr = {
    ctx.getChild(0) match {
      case c: Unsigned_literalContext =>
        c.getChild(0) match {
          case c: Unsigned_numeric_literalContext =>
            parseUNumericLiteral(c)
          case c: General_literalContext =>
            c.getChild(0) match {
              case c: Predefined_type_literalContext =>
                parsePreTypeLiteral(c)
              case c: List_literalContext =>
                parseListLiteral(c)
            }
        }
      case c: Parameter_value_specificationContext =>
        val parName = c.identifier().oC_SymbolicName().getText
        parameters += parName
        Parameter(c.identifier().oC_SymbolicName().getText)
    }
  }

  def parseNonParenthesizedValueExpressionPrimary(
      ctx: Non_parenthesized_value_expression_primaryContext): Expr = {
    ctx.getChild(0) match {
      case c: Binding_variableContext =>
        parseBindingVariable(c)
      case c: Unsigned_value_specificationContext =>
        parseUValueSpecification(c)
      case c: Function_exprContext => parseFunctionExpr(c)
    }
  }

  def parseNonParentValueExpressionPrimaryWithProperty(
      ctx: Non_parenthesized_value_expression_primary_with_propertyContext): Expr = {
    val expr = parseNonParenthesizedValueExpressionPrimary(
      ctx.non_parenthesized_value_expression_primary())
    val plist = ctx.property_name().asScala.toList.map(x => x.getText)
    plist.foldLeft(expr)((expr: Expr, x: String) => UnaryOpExpr(GetField(x), expr))
  }

  def parseValueExpressionPrimary(ctx: Value_expression_primaryContext): Expr = {
    ctx.getChild(0) match {
      case c: Parenthesized_value_expressionContext =>
        parseValueExpression(c.value_expression())
      case c: Non_parenthesized_value_expression_primary_with_propertyContext =>
        parseNonParentValueExpressionPrimaryWithProperty(c)
    }
  }

  def parseProjectPrimary(ctx: Project_primaryContext): Expr = {
    ctx.getChild(0) match {
      case c: Value_expression_primaryContext =>
        parseValueExpressionPrimary(c)
      case c: Numeric_value_functionContext =>
        parseNumericFunction(c)
    }
  }

  def parseNumericFunction(ctx: Numeric_value_functionContext): Expr = {
    ctx.getChild(0) match {
      case c: Absolute_value_expressionContext => parseAbsoluteValueExpression(c)
      case c: Floor_functionContext => parseFloorFunction(c)
      case c: Ceiling_functionContext => parseCeilingFunction(c)
    }
  }

  def parseFloorFunction(ctx: Floor_functionContext): Expr = {
    val ref = parseProjectValueExpression(ctx.project_value_expression())
    UnaryOpExpr(Floor, ref)
  }

  def parseCeilingFunction(ctx: Ceiling_functionContext): Expr = {
    val ref = parseProjectValueExpression(ctx.project_value_expression())
    UnaryOpExpr(Ceil, ref)
  }

  def parseAbsoluteValueExpression(ctx: Absolute_value_expressionContext): Expr = {
    val ref = parseProjectValueExpression(ctx.project_value_expression())
    UnaryOpExpr(Abs, ref)
  }

  def parseFactor(ctx: FactorContext): Expr = {
    val expr = parseProjectPrimary(ctx.project_primary())
    Option(ctx.sign()) match {
      case Some(x) =>
        x.getChild(0) match {
          case c: Plus_signContext =>
            expr
          case c: Minus_signContext =>
            UnaryOpExpr(Neg, expr)
        }
      case None =>
        expr
    }
  }

  def parseTerm(ctx: TermContext): Expr = {
    val exprList = ctx.factor().asScala.map(x => parseFactor(x))
    val operList = scala.collection.mutable.ListBuffer[String]()
    for (i <- 1 until ctx.getChildCount by 2) {
      ctx.getChild(i) match {
        case c: AsteriskContext =>
          operList += "*"
        case c: SolidusContext =>
          operList += "/"
        case c: PercentContext =>
          operList += "%"
      }
    }
    expressionTreeBuilder(exprList.toList, operList.toList)
  }

  def parseProjectValueExpression(ctx: Project_value_expressionContext): Expr = {
    val exprList = ctx.term().asScala.map(x => parseTerm(x))
    var operList = scala.collection.mutable.ListBuffer[String]()
    for (i <- 1 until ctx.getChildCount by 2) {
      ctx.getChild(i) match {
        case c: Plus_signContext =>
          operList += "+"
        case c: Minus_signContext =>
          operList += "-"
      }
    }
    expressionTreeBuilder(exprList.toList, operList.toList)
  }

  def parseIdFilterParameters(expr1: Expr, variableParam: String): Map[String, String] = {
    if (variableParam == null) {
      return idFilterParameters
    }
    if (!expr1.isInstanceOf[UnaryOpExpr]) {
      return idFilterParameters
    }
    val unaryExpr = expr1.asInstanceOf[UnaryOpExpr]
    if (!unaryExpr.name.isInstanceOf[GetField]) {
      return idFilterParameters;
    }
    val fieldId = unaryExpr.name.asInstanceOf[GetField].fieldName
    if (!"id".equals(fieldId)) {
      return idFilterParameters
    }

    if (!unaryExpr.arg.isInstanceOf[Ref]) {
      return idFilterParameters
    }
    val refName = unaryExpr.arg.asInstanceOf[Ref].refName
    if (idFilterParameters.contains(refName) &&
      !idFilterParameters(refName).equals(variableParam)) {
      throw new KGDSLGrammarException(refName + " has more than one id filter condition")
    }
    idFilterParameters = idFilterParameters + (refName -> variableParam)
    idFilterParameters
  }

  def parseBinaryExpr(ctx: Binary_exprContext): Expr = {
    val expr1 = parseProjectValueExpression(ctx.project_value_expression(0))
    Option(ctx.binary_op()) match {
      case None => expr1
      case Some(op) =>
        val expr2 = parseProjectValueExpression(ctx.project_value_expression(1))
        val variableParam = expr2 match {
          case Parameter(paramName) => paramName
          case _ => null
        }
        val opStr = op.getChild(0) match {
          case c: Equals_operatorContext =>
            parseIdFilterParameters(expr1, variableParam)
            BEqual
          case c: Not_equals_operatorContext => BNotEqual
          case c: Less_than_operatorContext => BSmallerThan
          case c: Greater_than_operatorContext => BGreaterThan
          case c: Less_than_or_equals_operatorContext => BNotSmallerThan
          case c: Greater_than_or_equals_operatorContext => BNotGreaterThan
          case c: Like_operatorContext => BLike
          case c: Rlike_operatorContext => BRLike
          case c: In_operatorContext =>
            parseIdFilterParameters(expr1, variableParam)
            BIn
          case c: Assignment_operatorContext => BAssign
        }
        BinaryOpExpr(opStr, expr1, expr2)
    }
  }

  def parseUnaryExpr(ctx: Unary_exprContext): Expr = {
    val expr = parseValueExpression(ctx.value_expression())
    Option(ctx.unary_op()) match {
      case None => expr
      case Some(op) =>
        val opStr = op.getChild(0) match {
          case _: Exist_operatorContext => Exists
          case _: Abs_operatorContext => Abs
          case _: Floor_operatorContext => Floor
          case _: Ceiling_operatorContext => Ceil
        }
        UnaryOpExpr(opStr, expr)
    }
  }

  def parseFunctionExpr(ctx: Function_exprContext): Expr = {
    parseFunctionExprDetail(ctx.function_name().getText, parseFunctionArgs(ctx.function_args()))
  }

  def parseFunctionArgs(args: Function_argsContext): List[Expr] = {
    if (args != null) {
      args
        .list_element_list()
        .list_element()
        .asScala
        .toList
        .map(x => parseValueExpression(x.value_expression()))
    } else {
      List.empty
    }
  }

  def parseFunctionExprDetail(funcNameTxt: String, function_args: List[Expr]): Expr = {
    val funcName = getFunctionNames(funcNameTxt)
    funcName match {
      case "top" | "bottom" =>
        val op = funcName match {
          case "top" => DescExpr
          case "bottom" => AscExpr
        }
        var limit = 1
        if (function_args.nonEmpty) {
          limit = function_args(1) match {
            case VLong(value) => Integer.valueOf(value)
            case _ =>
              throw new KGDSLGrammarException(funcName + " limit argas is not integer")
          }
        }
        OrderAndLimit(op, Limit(function_args.head, limit))
      case "keep_shortest_path" | "keep_longest_path" =>
        val op = funcName match {
          case "keep_shortest_path" => AscExpr
          case "keep_longest_path" => DescExpr
        }
        OrderAndLimit(op, Limit(FunctionExpr("repeat_edge_length", function_args), 1))
      case _ => FunctionExpr(funcName, function_args)
    }
  }

  def parseExpr(ctx: ExprContext): Expr = {
    ctx.getChild(0) match {
      case c: Binary_exprContext => parseBinaryExpr(c)
      case c: Unary_exprContext => parseUnaryExpr(c)
      case c: Function_exprContext => parseFunctionExpr(c)
    }
  }

  def parseComplexExpr(ctx: Complex_obj_exprContext): Map[String, Expr] = {
    if (ctx == null) {
      Map.empty
    } else {
      ctx
        .assignment_expression()
        .asScala
        .toList
        .map(x => parseAssignmentExpression(x))
        .toMap
    }
  }

  def parseAssignmentExpression(ctx: Assignment_expressionContext): (String, Expr) = {
    (ctx.identifier().getText, parseExpressionSet(ctx.expression_set()))
  }

  def parseLogicTest(ctx: Logic_testContext): Expr = {
    val bExpr: Expr = parseExpr(ctx.expr())
    Option(ctx.getChild(1)) match {
      case Some(x) =>
        val tExpr: Expr = str2bool(ctx.truth_value().getText)
        val compareStr = x match {
          case _: Equals_operatorContext => BEqual
          case _: Not_equals_operatorContext => BNotEqual
          case _ =>
            ctx.getChild(2).getText.toUpperCase(Locale.ROOT) match {
              case "NOT" => BNotEqual
              case _ => BEqual
            }
        }
        BinaryOpExpr(compareStr, bExpr, tExpr)
      case None =>
        bExpr
    }
  }

  def parseLogicFactor(ctx: Logic_factorContext): Expr = {
    val expr = parseLogicTest(ctx.logic_test())
    ctx.getChild(0).getText.toUpperCase(Locale.ROOT) match {
      case "NOT" => UnaryOpExpr(Not, expr)
      case _ => expr
    }
  }

  def parseLogicTerm(ctx: Logic_termContext): Expr = {
    val andlist = ctx.logic_factor().asScala.toList.map(x => parseLogicFactor(x))
    andlist.reduce((A: Expr, B: Expr) => BinaryOpExpr(BAnd, A, B))
  }

  def parseLogicValueExpression(ctx: Logic_value_expressionContext): Expr = {
    val orList = ctx.logic_term().asScala.toList.map(x => parseLogicTerm(x))
    orList.reduce((A: Expr, B: Expr) => BinaryOpExpr(BOr, A, B))
  }

  def parseValueExpression(ctx: Value_expressionContext): Expr = {
    ctx.getChild(0) match {
      case c: Logic_value_expressionContext => parseLogicValueExpression(c)
      case c: Project_value_expressionContext =>
        parseProjectValueExpression(c)
    }
  }

  def parseListOp(ctx: List_opContext, opEle: Ref): Expr = {
    ctx.getChild(0) match {
      case c: List_common_agg_expressContext => parseListCommonAggOpExpress(c, opEle)
      case c: List_common_agg_if_expressContext =>
        parseListCommonAggIfOpExpress(c, opEle)
      case c: List_limit_opContext => parseListLimitOp(c, opEle)
      case c: List_filter_op_nameContext => parseListFilterOp(c)
      case c: List_get_opContext => parseListGetOp(c, opEle)
      case c: List_slice_opContext => parseListSliceOp(c, opEle)
      case c: List_accumulate_opContext => parseListAccumulateOp(c, opEle)
      case c: List_str_join_opContext => parseListStrJoinOp(c, opEle)
      case c: List_head_ele_opContext => parseListHeadEleOp(c, opEle)
      case c: List_tail_ele_opContext => parseListTailEleOp(c, opEle)
      case c: List_nodes_opContext => parseListNodesOp(c, opEle)
      case c: List_edges_opContext => parseListEdgesOp(c, opEle)
      case c: List_reduce_opContext => parseListReduceOp(c, opEle)
      case c: List_constraint_opContext => parseListConstraintOp(c, opEle)
      case _ => throw new UnsupportedOperationException(ctx.getChild(0).toString + " not impl")
    }
  }

  def parseIntegerFull(ctx: IntegerLiteral_fullContext): Integer = {
    if (ctx == null) {
      return 0
    }
    val value = ctx.oC_IntegerLiteral().getText.toInt
    if (ctx.minus_sign() != null) {
      -1 * value
    } else {
      value
    }
  }

  def parseListHeadEleOp(ctx: List_head_ele_opContext, opEle: Ref): Expr = {
    ListOpExpr(Get(parseIntegerFull(ctx.integerLiteral_full())), opEle)
  }

  def parseListTailEleOp(ctx: List_tail_ele_opContext, opEle: Ref): Expr = {
    ListOpExpr(Get(parseIntegerFull(ctx.integerLiteral_full()) - 1), opEle)
  }

  def parseListNodesOp(ctx: List_nodes_opContext, opEle: Ref): Expr = {
    PathOpExpr(GetNodesExpr, opEle)
  }

  def parseListEdgesOp(ctx: List_edges_opContext, opEle: Ref): Expr = {
    PathOpExpr(GetEdgesExpr, opEle)
  }

  def parseLambdaExprOp(ctx: Lambda_exprContext): (List[String], Expr) = {
    val args = ctx
      .binary_lambda_args()
      .identifier()
      .asScala
      .map(arg => {
        arg.getText
      })
      .toList
    val expr = parseValueExpression(ctx.value_expression())
    (args, expr)
  }

  def parseListReduceOp(ctx: List_reduce_opContext, opEle: Ref): Expr = {
    val lambdaArgs = parseLambdaExprOp(ctx.lambda_expr())
    ListOpExpr(
      Reduce(
        lambdaArgs._1.last,
        lambdaArgs._1.head,
        lambdaArgs._2,
        parseValueExpression(ctx.value_expression())),
      opEle)
  }

  def parseListConstraintOp(ctx: List_constraint_opContext, opEle: Ref): Expr = {
    val lambdaArgs = parseLambdaExprOp(ctx.lambda_expr())
    ListOpExpr(Constraint(lambdaArgs._1.head, lambdaArgs._1.last, lambdaArgs._2), opEle)
  }

  def parseListLimitOp(ctx: List_limit_opContext, opEle: Ref): Expr = {
    ctx.getChild(0) match {
      case c: List_limit_op_allContext => parseListLimitAllOp(c, opEle)
      case c: List_order_and_limitContext => parseListOrderAndSliceOp(c, opEle)
    }
  }

  def parseListLimitAllOp(ctx: List_limit_op_allContext, column: Expr): Limit = {
    Limit(column, ctx.oC_IntegerLiteral().getText.toInt)
  }

  def parseListOrderAndSliceOp(ctx: List_order_and_limitContext, opEle: Ref): OrderAndLimit = {
    val opName = ctx.list_order_op().getChild(0).getText.toUpperCase() match {
      case "ASC" => AscExpr
      case "DESC" => DescExpr
    }
    var lambdaExpr: Expr = opEle
    if (null != ctx.list_order_op().list_op_args().value_expression()) {
      lambdaExpr = parseValueExpression(ctx.list_order_op().list_op_args().value_expression())
    }
    OrderAndLimit(opName, parseListLimitAllOp(ctx.list_limit_op_all(), lambdaExpr))
  }

  def parseIndexParameter(ctx: Index_parameterContext): Int = {
    ctx.oC_IntegerLiteral().getText.toInt
  }

  def parseListGetOp(ctx: List_get_opContext, opEle: Ref): ListOpExpr = {
    ListOpExpr(Get(parseIndexParameter(ctx.index_parameter())), opEle)
  }

  def parseListSliceOp(ctx: List_slice_opContext, opEle: Ref): ListOpExpr = {
    ListOpExpr(
      Slice(
        parseIndexParameter(ctx.index_parameter().get(0)),
        parseIndexParameter(ctx.index_parameter().get(1))),
      opEle)
  }

  def parseListAccumulateOp(ctx: List_accumulate_opContext, opEle: Ref): Expr = {
    AggOpExpr(Accumulate(ctx.accumulate_support_op().getText), opEle)
  }

  def parseListFilterOp(ctx: List_filter_op_nameContext): Filter = {
    Filter(parseValueExpression(ctx.list_op_args().value_expression()))
  }

  def parseListStrJoinOp(ctx: List_str_join_opContext, opEle: Ref): Expr = {
    throw new UnsupportedOperationException(ctx.getText)
  }

  def parseListOpExpression(ctx: List_op_expressContext): Expr = {

    val originListId = parseValueExpression(ctx.value_expression())
    var opExpr: Expr = null
    val listOp: Ref = originListId match {
      case c: Ref => c
      case _ =>
        opExpr = BinaryOpExpr(BAssign, VString("list_tmp" + ctx.getRuleIndex), originListId)
        Ref("list_tmp" + ctx.getRuleIndex)
    }
    val reverse_op = ctx.list_op().asScala
    var last_op: OpChainExpr = null
    if (opExpr != null) {
      last_op = OpChainExpr(opExpr, null)
    }
    for (op <- reverse_op) {
      last_op = OpChainExpr(parseListOp(op, listOp), last_op)
    }
    last_op
  }

  def parseGraphAliasElementList(ctx: Graph_alias_element_listContext): List[Expr] = {
    val aliasSet = ctx.graph_alias_with_property()
      .asScala.map(k => parseRefExpr(k.graph_alias(), k.property_name()))
    aliasSet.toList
  }

  def parseGraphOp(ctx: Graph_opContext): Expr = {
    ctx.getChild(0) match {
      case c: Graph_common_agg_expressContext => parseGraphCommonAggExpress(c)
      case c: Graph_common_agg_if_expressContext => parseGraphCommonAggIfExpress(c)
      case c: Graph_order_and_slice_opContext => parseGraphOrderAndSlice(c)
      case c: Graph_filter_opContext => parseGraphFilterOp(c)
      case c: Graph_common_agg_udf_expressContext => parseGraphCommonAggUdfExpress(c)
    }
  }

  def parseGraphCommonAggUdfExpress(ctx: Graph_common_agg_udf_expressContext): Expr = {
    var function_args: List[Expr] = List.empty
    if (ctx.function_args() != null) {
      function_args = ctx
        .function_args()
        .list_element_list()
        .list_element()
        .asScala
        .toList
        .map(x => parseValueExpression(x.value_expression()))
    }
    val funcArgs = parseFunctionArgs(ctx.function_args())
    val refExpr: Expr = parseRefExpr(ctx.graph_alias(), ctx.property_name())

    val passArgs: List[Expr] = refExpr +: funcArgs
    val functionExpr = parseFunctionExprDetail(ctx.function_name().getText, passArgs)
    functionExpr match {
      case c: FunctionExpr => AggOpExpr(AggUdf(c.name, funcArgs), refExpr)
      case c => c
    }
  }

  def parseGraphFilterOp(ctx: Graph_filter_opContext): Filter = {
    Filter(parseValueExpression(ctx.value_expression()))
  }

  def parseGraphOrderAndSlice(ctx: Graph_order_and_slice_opContext): OrderAndLimit = {
    val opName = ctx.graph_order_op().getChild(0).getText.toUpperCase() match {
      case "DESC" => DescExpr
      case "ASC" => AscExpr
    }
    val orderEle = {
      if (ctx.graph_order_op().property_name() == null) {
        Ref(ctx.graph_order_op().graph_alias().getText)
      } else {
        UnaryOpExpr(
          GetField(ctx.graph_order_op().property_name.getText),
          Ref(ctx.graph_order_op().graph_alias().getText))
      }
    }
    val limit = ctx.graph_limit_op().oC_IntegerLiteral().getText.toInt
    OrderAndLimit(opName, Limit(orderEle, limit))
  }

  def parseAggFunc(opName: String, ele: Expr): AggOpExpr = {
    // Aggregator match
    val opExpr = opName.toUpperCase(Locale.ROOT) match {
      case "AVG" => Avg
      case "COUNT" => Count
      case "SUM" => Sum
      case "MIN" => Min
      case "MAX" => Max
      case _ => throw new UnsupportedOperationException(opName + " not impl")
    }
    AggOpExpr(opExpr, ele)
  }

  def parseListCommonAggIfOpExpress(
      ctx: List_common_agg_if_expressContext,
      opEle: Ref): AggIfOpExpr = {
    ctx.getChild(0) match {
      case c: List_common_agg_if_chain_expressContext =>
        parseListCommonAggIfChainExpress(c, opEle)
      case c: List_common_agg_if_one_expressContext => parseListCommonAggIfOneExpress(c, opEle)
    }
  }

  def parseListCommonAggIfOneExpress(
      ctx: List_common_agg_if_one_expressContext,
      opEle: Ref): AggIfOpExpr = {
    var opEleExpr: Expr = opEle
    val filterExpr = parseValueExpression(ctx.list_op_args().get(0).value_expression())
    if (ctx.list_op_args().size() > 1 && ctx.list_op_args().get(1).value_expression() != null) {
      opEleExpr = parseValueExpression(ctx.list_op_args().get(1).value_expression())
    }

    val opAggNameStr = ctx.list_common_agg_if_name().getText.toUpperCase(Locale.ROOT)
    val opAggNameExpr = opAggNameStr match {
      case "SUMIF" => Sum
      case "COUNTIF" => Count
      case "AVGIF" => Avg
      case "MINIF" => Min
      case "MAXIF" => Max
    }
    AggIfOpExpr(AggOpExpr(opAggNameExpr, opEleExpr), filterExpr)
  }

  def parseListCommonAggIfChainExpress(
      ctx: List_common_agg_if_chain_expressContext,
      opEle: Ref): AggIfOpExpr = {
    val filterExpr = parseListFilterOp(ctx.list_filter_op_name())
    val aggExpr = parseListCommonAggOpExpress(ctx.list_common_agg_express(), opEle)
    AggIfOpExpr(aggExpr.asInstanceOf[AggOpExpr], filterExpr.condition)
  }

  def parseListCommonAggOpExpress(ctx: List_common_agg_expressContext, opEle: Ref): AggOpExpr = {
    var lambdaExpr: Expr = opEle
    if (null != ctx.list_op_args().value_expression()) {
      lambdaExpr = parseValueExpression(ctx.list_op_args().value_expression())
    }
    val opName = ctx.list_common_agg_name().getText
    parseAggFunc(opName, lambdaExpr)
  }

  def parseGraphCommonAggIfExpress(ctx: Graph_common_agg_if_expressContext): Aggregator = {
    ctx.getChild(0) match {
      case c: Graph_common_agg_if_chain_expressContext => parseGraphCommonAggIfChainExpress(c)
      case c: Graph_common_agg_if_one_expressContext => parseGraphCommonAggIfOneExpress(c)
    }
  }

  def parseRefExpr(graphAlias: Graph_aliasContext, propertyName: Property_nameContext): Expr = {
    if (propertyName == null) {
      Ref(graphAlias.getText)
    } else {
      UnaryOpExpr(GetField(propertyName.getText), Ref(graphAlias.getText))
    }
  }

  def parseGraphCommonAggIfOneExpress(ctx: Graph_common_agg_if_one_expressContext): Aggregator = {
    val filterExpr = parseValueExpression(ctx.value_expression())
    val expr = parseRefExpr(ctx.graph_alias(), ctx.property_name())
    val opName = ctx.graph_common_agg_if_name().getText
    val opExpr = opName.toUpperCase(Locale.ROOT) match {
      case "SUMIF" => Sum
      case "COUNTIF" => Count
      case "AVGIF" => Avg
      case "MINIF" => Min
      case "MAXIF" => Max
    }
    AggIfOpExpr(AggOpExpr(opExpr, expr), filterExpr)
  }

  def parseGraphCommonAggIfChainExpress(
      ctx: Graph_common_agg_if_chain_expressContext): Aggregator = {
    val filter = parseGraphFilterOp(ctx.graph_filter_op())
    val aggFunc = parseGraphCommonAggExpress(ctx.graph_common_agg_express())
    AggIfOpExpr(aggFunc, filter.condition)
  }

  def parseGraphCommonAggExpress(ctx: Graph_common_agg_expressContext): AggOpExpr = {
    val expr = parseRefExpr(ctx.graph_alias(), ctx.property_name())
    val opName = ctx.graph_common_agg_name().getText
    parseAggFunc(opName, expr)
  }

  def parseGraphGroupExpression(ctx: Graph_group_op_expressContext): OpChainExpr = {
    val group_alias = parseGraphAliasElementList(ctx.graph_alias_element_list())
    val reverse_op = ctx.graph_op().asScala
    var last_op: OpChainExpr = null
    for (op <- reverse_op) {
      last_op = OpChainExpr(parseGraphOp(op), last_op)
    }
    if (!last_op.curExpr.isInstanceOf[Aggregator] &&
      !last_op.curExpr.isInstanceOf[OrderAndLimit]) {
      throw new KGDSLGrammarException("graph group last op must be aggregator or topK operator")
    }
    OpChainExpr(GraphAggregatorExpr("unresolved_default_path", group_alias, null), last_op)
  }

  def parseExpressionSet(ctx: Expression_setContext): Expr = {
    ctx.getChild(0) match {
      case c: Value_expressionContext => parseValueExpression(c)
      case c: List_op_expressContext => parseListOpExpression(c)
      case c: Graph_group_op_expressContext => parseGraphGroupExpression(c)
      case _ => throw new UnsupportedOperationException(ctx.getChild(0).toString + " not impl")
    }
  }

  def parseRuleExpression(ctx: Rule_expressionContext): Rule = {
    ctx.getChild(0) match {
      case c: Logic_rule_expressionContext => parseLogicRuleExpression(c)
      case c: Project_rule_expressionContext => parseProjectRuleExpression(c)
    }
  }

  def parseLogicRuleExpression(ctx: Logic_rule_expressionContext): Rule = {
    val expr = parseExpressionSet(ctx.expression_set())
    val symbol = ctx.identifier().getText
    if (ctx.explain() == null) {
      LogicRule(symbol, "", expr)
    } else {
      LogicRule(
        symbol,
        parseUnbrokenCharacterStringLiteral(ctx.explain().unbroken_character_string_literal()),
        expr)
    }
  }

  def parseProjectRuleExpression(ctx: Project_rule_expressionContext): Rule = {
    val expr = parseExpressionSet(ctx.expression_set())
    if (ctx.property_name() != null) {
      ProjectRule(
        IRProperty(ctx.identifier().getText, ctx.property_name().getText),
        expr)
    } else {
      ProjectRule(IRVariable(ctx.identifier().getText), expr)
    }

  }

}
