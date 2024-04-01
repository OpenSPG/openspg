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

package com.antgroup.openspg.reasoner.lube.utils

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException
import com.antgroup.openspg.reasoner.common.trees.{BottomUp, TopDown, Transform}
import com.antgroup.openspg.reasoner.lube.common.expr._
import com.antgroup.openspg.reasoner.lube.common.graph._

/**
 * Utils for Expr
 */
object ExprUtils {

  /**
   * get all ref name from Expr
   * @param expr
   * @return
   */
  def getRefVariableByExpr(expr: Expr): List[String] = {
    Transform((e: Expr, c: List[List[String]]) => {
      if (c.nonEmpty) {
        c.flatten
      } else {
        e match {
          case Ref(refName) => List.apply(refName)
          case _ => List.empty
        }
      }
    }).transform(expr)
  }

  def getRepeatPathInputFieldInRule(expr: Expr,
                                    repeatPathMap: Map[String, IRPath]): List[IRField] = {
    expr match {
      case OpChainExpr(ListOpExpr(listOp, _), OpChainExpr(PathOpExpr(name, ref), _)) =>
        val irPath = repeatPathMap(ref.refName)
        val props = listOp match {
          case constraint: Constraint =>
            getAllInputFieldInRule(constraint.reduceFunc, Set.empty, Set.empty).filter(
              ir => ir.name.equals(constraint.cur) && ir.name.equals(constraint.pre))
              .flatMap(t => t match {
                case IRNode(_, fields) => fields.toList
                case IREdge(_, fields) => fields.toList
                case _ => List.empty
              })

          case compute: Reduce =>
            getAllInputFieldInRule(compute.reduceFunc, Set.empty, Set.empty).filter(
              ir => ir.name.equals(compute.ele)
            ).flatMap(t => t match {
              case IRNode(_, fields) => fields.toList
              case IREdge(_, fields) => fields.toList
              case _ => List.empty
            })

          case _ => List.empty
        }
        name match {
          case GetNodesExpr =>
            irPath.elements.filter(ele => ele.isInstanceOf[IRNode]).map {
              case IRNode(irName, fields) => IRNode(irName, fields ++ props)
              case _ => null
            }.filter(_ != null)
          case GetEdgesExpr =>
            irPath.elements.filter(ele => ele.isInstanceOf[IREdge]).map {
              case IREdge(irName, fields) => IREdge(irName, fields ++ props)
              case _ => null
            }.filter(_ != null)
        }
      case _ => List.empty
    }
  }

  def getAllInputFieldInRule(
      expr: Expr,
      nodesAlias: Set[String],
      edgeAlias: Set[String]): List[IRField] = {
    Transform((e: Expr, c: List[List[IRField]]) => {
      if (c.nonEmpty) {
        e match {
          case UnaryOpExpr(name, arg) =>
            name match {
              case GetField(fieldName) =>
                val refName = arg.asInstanceOf[Ref].refName
                if (edgeAlias != null && edgeAlias.contains(refName)) {
                  List.apply(IREdge(refName, Set.apply(fieldName)))
                } else {
                  // other as ir node
                  List.apply(IRNode(refName, Set.apply(fieldName)))
                }
              case _ => c.filter(Option(_).isDefined).flatten
            }
          case ListOpExpr(name, _) =>
            name match {
              case constraint: Constraint =>
                val irList =
                  getAllInputFieldInRule(constraint.reduceFunc, nodesAlias, edgeAlias).filter(
                    ir => !ir.name.equals(constraint.cur) && !ir.name.equals(constraint.pre))
                mergeListIRField(c.flatten ++ irList)
              case compute: Reduce =>
                val irList =
                  getAllInputFieldInRule(compute.reduceFunc, nodesAlias, edgeAlias).filter(
                    ir => !ir.name.equals(compute.ele) && !ir.name.equals(compute.res)
                  )
                mergeListIRField(c.flatten ++ irList)
              case _ =>
                mergeListIRField(c.flatten)
            }
          case _ =>
            // merge list ir
            mergeListIRField(c.flatten)
        }
      } else {
        e match {
          case Ref(refName) =>
            if (nodesAlias != null && nodesAlias.contains(refName)) {
              List.apply(IRNode(refName, Set.empty))
            } else if (edgeAlias != null && edgeAlias.contains(refName)) {
              List.apply(IREdge(refName, Set.empty))
            } else {
              List.apply(IRVariable(refName))
            }
          case _ => List.empty
        }
      }
    }).transform(expr)

  }

  /**
   * rename rule contains variable name by renameFunc
   * @param expr
   * @param renameFunc
   * @return
   */
  def renameVariableInExpr(expr: Expr, renameFunc: (String) => String): Expr = {
    val trans: PartialFunction[Expr, Expr] = {
      case Ref(refName) =>
        Ref(renameFunc(refName))
      case x => x
    }
    BottomUp(trans).transform(expr)
  }

  /**
   * rename rule contains variable
   *
   * @param expr
   * @param renameFunc
   * @return
   */
  def renameVariableInExpr(expr: Expr, replaceVar: Map[IRField, IRField]): Expr = {
    val trans: PartialFunction[Expr, Expr] = {
      case expr @ UnaryOpExpr(GetField(name), Ref(alis)) =>
        if (replaceVar.contains(IRProperty(alis, name))) {
          val newProp = replaceVar.get(IRProperty(alis, name)).get.asInstanceOf[IRProperty]
          UnaryOpExpr(GetField(newProp.field), Ref(newProp.name))
        } else {
          expr
        }
      case expr @ Ref(name) =>
        if (replaceVar.contains(IRVariable(name))) {
          val newProp = replaceVar.get(IRVariable(name)).get
          newProp match {
            case IRVariable(name) => Ref(name)
            case IRProperty(name, field) => UnaryOpExpr(GetField(field), Ref(name))
            case _ =>
              throw UnsupportedOperationException(
                s"rename unsupported expr=${expr}, replaceVar=${replaceVar}")
          }

        } else {
          expr
        }
      case x => x
    }
    TopDown(trans).transform(expr)
  }

  def renameAliasInExpr(expr: Expr, replaceVar: Map[String, String]): Expr = {
    val trans: PartialFunction[Expr, Expr] = {
      case x @ Ref(name) =>
        if (replaceVar.contains(name)) {
          val newAlias = replaceVar.get(name).get
          Ref(newAlias)
        } else {
          x
        }
      case x => x
    }
    BottomUp(trans).transform(expr)
  }

  /**
   * helper: merge same node and same edges
   *
   * @param c
   * @return
   */
  def mergeListIRField(c: List[IRField]): List[IRField] = {
    var nodesMap = Map[String, Set[String]]()
    var edgesMap = Map[String, Set[String]]()
    var refSet = mutable.Set[String]()
    var variable = c
      .filter(Option(_).isDefined)
      .map {
        case IRNode(name, fields) =>
          if (nodesMap.contains(name)) {
            nodesMap += (name -> (nodesMap(name) ++ fields))
          } else {
            nodesMap += (name -> fields)
          }
          null
        case IREdge(name, fields) =>
          if (edgesMap.contains(name)) {
            edgesMap += (name -> (edgesMap(name) ++ fields))
          } else {
            edgesMap += (name -> fields)
          }
          null
        case x =>
          if (!refSet.contains(x.name)) {
            refSet += x.name
            x
          } else {
            null
          }
      }
      .filter(Option(_).isDefined)
    if (nodesMap.nonEmpty) {
      variable = variable ++ nodesMap.map(x => IRNode(x._1, x._2))
    }
    if (edgesMap.nonEmpty) {
      variable = variable ++ edgesMap.map(x => IREdge(x._1, x._2))
    }
    variable
  }

}
