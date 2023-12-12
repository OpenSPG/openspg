package com.antgroup.openspg.reasoner.lube.utils

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.trees.{BottomUp, Transform}
import com.antgroup.openspg.reasoner.lube.common.expr.{Expr, GetField, Ref, UnaryOpExpr}
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
                  List.apply(IREdge(refName, mutable.Set.apply(fieldName)))
                } else {
                  // other as ir node
                  List.apply(IRNode(refName, mutable.Set.apply(fieldName)))
                }
              case _ => c.filter(Option(_).isDefined).flatten
            }
          case _ =>
            // merge list ir
            mergeListIRField(c.flatten)
        }
      } else {
        e match {
          case Ref(refName) => List.apply(IRVariable(refName))
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
  def renameVariableInExpr(expr: Expr, replaceVar: Map[IRField, IRProperty]): Expr = {
    val trans: PartialFunction[Expr, Expr] = {
      case expr @ UnaryOpExpr(GetField(name), Ref(alis)) =>
        if (replaceVar.contains(IRProperty(alis, name))) {
          val newProp = replaceVar.get(IRProperty(alis, name)).get
          UnaryOpExpr(GetField(newProp.field), Ref(newProp.name))
        } else {
          expr
        }
      case expr @ Ref(name) =>
        if (replaceVar.contains(IRVariable(name))) {
          val newProp = replaceVar.get(IRVariable(name)).get
          UnaryOpExpr(GetField(newProp.field), Ref(newProp.name))
        } else {
          expr
        }
      case x => x
    }
    BottomUp(trans).transform(expr)
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
    var nodesMap = Map[String, mutable.Set[String]]()
    var edgesMap = Map[String, mutable.Set[String]]()
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
