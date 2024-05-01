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

package com.antgroup.openspg.reasoner.lube.logical

import java.util.Locale

import scala.collection.JavaConverters._
import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException
import com.antgroup.openspg.reasoner.common.trees.BottomUp
import com.antgroup.openspg.reasoner.common.types._
import com.antgroup.openspg.reasoner.lube.common.expr._
import com.antgroup.openspg.reasoner.lube.common.graph.{IRField, IRProperty, IRVariable}
import com.antgroup.openspg.reasoner.lube.common.rule.Rule
import com.antgroup.openspg.reasoner.udf.UdfMng

object ExprUtil {

  def getReferProperties(rule: Rule): List[Tuple2[String, String]] = {
    if (rule == null) {
      List.empty
    } else {
      getReferProperties(rule.getExpr)
    }
  }

  /**
   * @param rule
   * @return
   */
  def getReferProperties(rule: Expr): List[Tuple2[String, String]] = {
    if (rule == null) {
      List.empty
    } else {
      rule.transform[List[Tuple2[String, String]]] {
        case (Ref(name), _) => List.apply((null, name))
        case (UnaryOpExpr(GetField(name), Ref(alis)), _) => List.apply((alis, name))
        case (BinaryOpExpr(_, Ref(left), Ref(right)), _) =>
          List.apply((null, left), (null, right))
        case (_, tupleList) => tupleList.flatten
      }
    }

  }

  def needResolved(rule: Expr): Boolean = {
    !getReferProperties(rule).filter(_._1 == null).isEmpty
  }

  def transExpr(rule: Expr, replaceVar: Map[String, PropertyVar]): Expr = {

    def rewriter: PartialFunction[Expr, Expr] = { case Ref(refName) =>
      if (replaceVar.contains(refName)) {
        val propertyVar = replaceVar(refName)
        UnaryOpExpr(GetField(propertyVar.field.name), Ref(propertyVar.name))
      } else {
        Ref(refName)
      }

    }

    BottomUp(rewriter).transform(rule)
  }

  def transExpr(rule: Rule, replaceVar: Map[String, PropertyVar]): Rule = {
    val newRule = rule.updateExpr(transExpr(rule.getExpr, replaceVar))
    newRule.cleanDependencies
    for (dependency <- rule.getDependencies) {
      newRule.addDependency(transExpr(dependency, replaceVar))
    }
    newRule
  }

  def getTargetType(expr: Expr, referVars: Map[IRField, KgType], udfRepo: UdfMng): KgType = {
    expr match {
      case Ref(name) =>
        if (referVars.contains(IRVariable(name))) {
          referVars(IRVariable(name))
        } else {
          KTObject
        }
      case UnaryOpExpr(GetField(name), Ref(alis)) =>
        val kgType = referVars(IRProperty(alis, name))
        if (kgType.isInstanceOf[BasicKgType]) {
          kgType
        } else {
          kgType match {
            case KTStd(_, basicType, _) => basicType
            case _ => KTObject
          }
        }
      case BinaryOpExpr(name, l, r) =>
        name match {
          case BAnd | BEqual | BNotEqual | BGreaterThan | BNotGreaterThan | BSmallerThan |
              BNotSmallerThan | BOr | BIn | BLike | BRLike | BAssign =>
            KTBoolean
          case BAdd | BSub | BMul | BDiv | BMod =>
            val left = getTargetType(l, referVars, udfRepo)
            val right = getTargetType(r, referVars, udfRepo)
            getUpperType(left, right)
          case _ => throw UnsupportedOperationException(s"express cannot support ${name}")
        }
      case UnaryOpExpr(name, arg) =>
        name match {
          case Not | Exists => KTBoolean
          case Abs | Neg => getTargetType(arg, referVars, udfRepo)
          case Floor | Ceil => KTDouble
          case _ => throw UnsupportedOperationException(s"express cannot support ${name}")
        }
      case FunctionExpr(name, funcArgs) =>
        val types = funcArgs.map(getTargetType(_, referVars, udfRepo))
        name match {
          case "rule_value" => types(1)
          case "cast_type" | "Cast" =>
            funcArgs(1).asInstanceOf[VString].value.toLowerCase(Locale.getDefault) match {
              case "int" | "bigint" | "long" => KTLong
              case "float" | "double" => KTDouble
              case "varchar" | "string" => KTString
              case _ =>
                throw UnsupportedOperationException(s"cannot support ${name} to ${funcArgs(1)}")
            }
          case _ =>
            val udf = udfRepo.getUdfMeta(name, types.asJava)
            if (udf != null) {
              udf.getResultType
            } else {
              throw UnsupportedOperationException(s"cannot find UDF: ${name}")
            }
        }

      case AggOpExpr(name, args) =>
        name match {
          case Min | Max | Sum | Avg | First | Accumulate(_) =>
            getTargetType(args, referVars, udfRepo)
          case StrJoin(_) => KTString
          case Count => KTLong
          case ConcatAgg => KTString
          case AggUdf(name, _) =>
            val types = getTargetType(args.head, referVars, udfRepo)
            val udf = udfRepo.getUdafMeta(name, types)
            if (udf != null) {
              udf.getResultType
            } else {
              throw UnsupportedOperationException(s"cannot find UDAF ${name}")
            }
          case _ => throw UnsupportedOperationException(s"express cannot support ${name}")
        }
      case OpChainExpr(curExpr, _) => getTargetType(curExpr, referVars, udfRepo)
      case ListOpExpr(name, _) =>
        name match {
          case Reduce(_, _, _, initValue) => getTargetType(initValue, referVars, udfRepo)
          case Constraint(_, _, _) => KTBoolean
          case Get(_) | Slice(_, _) => KTObject
        }
      case AggIfOpExpr(op, _) => getTargetType(op, referVars, udfRepo)
      case VNull | VString(_) => KTString
      case VLong(_) => KTLong
      case VDouble(_) => KTDouble
      case VBoolean(_) => KTBoolean
      case VList(_, listType) => KTList(listType)
      case _ => throw UnsupportedOperationException(s"express cannot support ${expr.pretty}")
    }
  }

  def getTargetType(rule: Rule, referVars: Map[IRField, KgType], udfRepo: UdfMng): KgType = {
    val newReferVars = new mutable.HashMap[IRField, KgType]
    newReferVars.++=(referVars)
    for (r <- rule.getDependencies) {
      newReferVars.put(r.getOutput, getTargetType(r, referVars, udfRepo))
    }
    getTargetType(rule.getExpr, newReferVars.toMap, udfRepo)
  }

  private def getUpperType(left: KgType, right: KgType): KgType = {
    val l = KgType.getNumberSeq(left)
    val r = KgType.getNumberSeq(right)
    if (l >= r) {
      left
    } else {
      right
    }
  }

}
