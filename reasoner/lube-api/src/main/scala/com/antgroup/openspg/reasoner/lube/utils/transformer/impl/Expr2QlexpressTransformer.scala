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

package com.antgroup.openspg.reasoner.lube.utils.transformer.impl

import com.antgroup.openspg.reasoner.common.trees.Transform
import com.antgroup.openspg.reasoner.common.types.KTString
import com.antgroup.openspg.reasoner.lube.common.expr._
import com.antgroup.openspg.reasoner.lube.common.rule.Rule
import com.antgroup.openspg.reasoner.lube.utils.transformer.ExprTransformer

class Expr2QlexpressTransformer extends ExprTransformer[String] {

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
    case GetField(fieldName) => "%s." + fieldName
  }

  def trans(e: Expr, params: List[String]): String = {
    val opTrans: PartialFunction[Expr, String] = {
      case BinaryOpExpr(name, l, r) => params.head + binaryOpSetTrans(name) + params(1)
      case UnaryOpExpr(name, arg) => unaryOpSetTrans(name).format(params.head)
      case FunctionExpr(name, funcArgs) => "%s(%s)".format(name, params.mkString(","))
      case Ref(refName) => refName
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
              "'%s'".format(x)
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
