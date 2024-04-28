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

package com.antgroup.openspg.reasoner.lube.utils.transformer.impl

import com.antgroup.openspg.reasoner.common.trees.Transform
import com.antgroup.openspg.reasoner.common.types.KTString
import com.antgroup.openspg.reasoner.common.utils.JavaFunctionCaller
import com.antgroup.openspg.reasoner.lube.common.expr._
import com.antgroup.openspg.reasoner.lube.common.graph.IRNode
import com.antgroup.openspg.reasoner.lube.common.rule.Rule
import com.antgroup.openspg.reasoner.lube.utils.ExprUtils
import com.antgroup.openspg.reasoner.lube.utils.transformer.ExprTransformer


class Expr2QlexpressTransformer(
                                 fieldNameTransFunc: JavaFunctionCaller[String, String]
                                 = new JavaFunctionCaller[String, String] {
                                   override def apply(t: String): String = t
                                 }
                               )
  extends ExprTransformer[String] {

  val binaryOpSetTrans: PartialFunction[BinaryOpSet, String] = {
    case BAdd => " + "
    case BSub => " - "
    case BMul => " * "
    case BDiv => " / "
    case BAnd => " && "
    case BEqual => " == "
    case BNotEqual => " != "
    case BGreaterThan => " > "
    case BNotGreaterThan => " >= "
    case BSmallerThan => " < "
    case BNotSmallerThan => " <= "
    case BOr => " || "
    case BIn => " in "
    case BLike => " like "
    case BRLike => " rlike "
    case BMod => " % "
    case BAssign => " = "
  }

  val unaryOpSetTrans: PartialFunction[UnaryOpSet, String] = {
    case Not => "!(%s)"
    case Neg => "-%s"
    case Exists => "exists(%s)"
    case Abs => "abs(%s)"
    case Floor => "floor(%s)"
    case Ceil => "ceil(%s)"
    case GetField(fieldName) => "%s." + fieldNameTransFunc(fieldName)
  }

  def lambdaFuncParse(curVariableSet: Set[String], lambdaFunc: Expr): (String, String) = {
    val qlExpress = transform(lambdaFunc).head
    val fields = ExprUtils.getAllInputFieldInRule(lambdaFunc, Set.empty, Set.empty)
    val params = fields.map(f => {
      if (curVariableSet.contains(f.name)) {
        null
      } else {
        f match {
          case IRNode(name, fields) => fields.map(attr => name + "." + attr).toList
          case _ => List.apply(f.name)
        }
      }
    }).filter(_ != null).flatten
    if (params.nonEmpty) {
      (qlExpress, "context_capturer([" + params.map(v => "\"" + v +"\"").mkString(",") +
        "],[" + params.mkString(",") + "])")
    } else {
      (qlExpress, null)
    }
  }


  def trans(e: Expr, params: List[String]): String = {
    val opTrans: PartialFunction[Expr, String] = {
      case BinaryOpExpr(name, l, r) =>
        val opStr = binaryOpSetTrans(name)
        name match {
          case BIn | BLike | BRLike | BAssign | BEqual | BNotEqual | BGreaterThan |
               BNotGreaterThan | BSmallerThan | BNotSmallerThan =>
            params.head + opStr + params(1)
          case _ =>
            val leftStr = l match {
              case c: BinaryOpExpr => "(" + params.head + ")"
              case _ => params.head
            }
            val rightStr = r match {
              case c: BinaryOpExpr => "(" + params(1) + ")"
              case _ => params(1)
            }
            leftStr + opStr + rightStr
        }
      case UnaryOpExpr(name, arg) => unaryOpSetTrans(name).format(params.head)
      case FunctionExpr(name, funcArgs) => "%s(%s)".format(name, params.mkString(","))
      case Ref(refName) => refName
      case ListOpExpr(name, _) =>
        name match {
          case Constraint(pre, cur, reduceFunc) =>
            val curVariableSet = Set.apply(pre, cur)
            val lambdaFuncRst = lambdaFuncParse(curVariableSet, reduceFunc)
            if (lambdaFuncRst._2 == null) {
              "repeat_constraint(__slot__, \"%s\", \"%s\", '%s')"
                .format(pre, cur, lambdaFuncRst._1)
            } else {
              "repeat_constraint(__slot__, \"%s\", \"%s\", '%s', %s)"
                .format(pre, cur, lambdaFuncRst._1, lambdaFuncRst._2)
            }
          case Reduce(ele, res, reduceFunc, initValue) =>
            val curVariableSet = Set.apply(ele, res)
            val initValueStr = transform(initValue).head
            val lambdaFuncRst = lambdaFuncParse(curVariableSet, reduceFunc)
            if (lambdaFuncRst._2 == null) {
              "repeat_reduce(__slot__, %s, '%s', '%s', '%s')"
                .format(initValueStr, res, ele, lambdaFuncRst._1)
            } else {
              "repeat_reduce(__slot__, %s, '%s', '%s', '%s', %s)"
                .format(initValueStr, res, ele, lambdaFuncRst._1, lambdaFuncRst._2)

            }
          case Slice(start, end) => "slice(__slot__, %s, %s)".format(start, end)
          case Get(index) => "get_list(__slot__, %s)".format(index)
        }
      case PathOpExpr(name, _) =>
        name match {
          case GetNodesExpr => params.head + ".nodes"
          case GetEdgesExpr => params.head + ".edges"
        }
      case OpChainExpr(_, _) =>
        if (params.size == 2) {
          params.head.replace("__slot__", params(1))
        } else {
          params.head
        }

      case AggOpExpr(name, _) =>
        name match {
          case StrJoin(tok) => "StrJoin('%s', %s)".format(tok, params.mkString(","))
          case Accumulate(op) => "Accumulate('%s', %s)".format(op, params.mkString(","))
          case _ => "%s(%s)".format(name, params.mkString(","))
        }
      case AggIfOpExpr(op, _) =>
        "%s(%s)".format(op.name.toString, params.mkString(","))
      case VNull => "null"
      case VString(value) => "\"%s\"".format(value)
      case VLong(value) => "%s".format(value)
      case VDouble(value) => "%s".format(value)
      case VBoolean(value) => value
      case VList(list, listType) =>
        val l = list
          .map(x =>
            if (listType.equals(KTString)) {
              "\"%s\"".format(x)
            } else {
              x
            })
          .mkString(",")
        "[" + l + "]"
      case Parameter(paramName) =>
        paramName
    }
    opTrans(e)
  }

  /**
   * Transform Expr to other express
   *
   * @param expr
   * @return
   */
  override def transform(expr: Expr): List[String] = {
    List.apply(Transform((e: Expr, c: List[String]) => {
      trans(e, c)
    }).transform(expr))
  }

  /**
   * transform rule format to qlExpress script
   * @param rule
   * @return
   */
  override def transform(rule: Rule): List[String] = {
    if (rule.getDependencies == null || rule.getDependencies.isEmpty) {
      transform(rule.getExpr)
    } else {
      var express = List[String]()
      for (depRule <- rule.getDependencies) {
        val depExpress = transform(depRule)
        val tail = depExpress.last
        if (depExpress.size > 1) {
          express ++= depExpress.dropRight(1)
        }
        express = express :+ "%s = %s".format(depRule.getName, tail)
      }
      express ++= transform(rule.getExpr)
      express
    }
  }

}
