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

package com.antgroup.openspg.reasoner.lube.logical.operators

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException
import com.antgroup.openspg.reasoner.common.types.KTObject
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.common.expr.Expr
import com.antgroup.openspg.reasoner.lube.common.graph.{IREdge, IRNode}
import com.antgroup.openspg.reasoner.lube.logical._
import com.antgroup.openspg.reasoner.lube.utils.ExprUtils

final case class Project(in: LogicalOperator, expr: Map[Var, Expr], solved: SolvedModel)
    extends StackingLogicalOperator {

  override def refFields: List[Var] = {
    val fieldsMap = new mutable.HashMap[String, Var]()
    for (pair <- expr) {
      pair._2 match {
        case e: Expr =>
          val fields =
            ExprUtils.getAllInputFieldInRule(e, solved.getNodeAliasSet, solved.getEdgeAliasSet)
          for (ref <- fields) {
            ref match {
              case IRNode(name, fields) =>
                val node = NodeVar(name, fields.map(new Field(_, KTObject, true)))
                fieldsMap.put(name, node.merge(fieldsMap.get(name)))
              case IREdge(name, fields) =>
                val edge = EdgeVar(name, fields.map(new Field(_, KTObject, true)))
                fieldsMap.put(name, edge.merge(fieldsMap.get(name)))
              case _ => throw UnsupportedOperationException(s"unsupported $expr")
            }
          }
      }
    }
    fieldsMap.values.toList
  }

  override def fields: List[Var] = {
    val fieldsMap = new mutable.HashMap[String, Var]
    for (pair <- expr) {
      pair._1 match {
        case propertyVar: PropertyVar =>
        case _ =>
          fieldsMap.put(pair._1.name, pair._1)
      }
    }
    for (pair <- expr) {
      pair._1 match {
        case PropertyVar(name, field) =>
          if (!fieldsMap.contains(name)) {
            if (solved.getNodeAliasSet.contains(name)) {
              fieldsMap.put(name, NodeVar(name, Set.apply(field)))
            } else {
              fieldsMap.put(name, EdgeVar(name, Set.apply(field)))
            }
          } else {
            fieldsMap.put(
              pair._1.name,
              fieldsMap.get(pair._1.name).get.merge(Option.apply(pair._1)))
          }
        case _ =>
      }
    }
    fieldsMap.values.toList
  }


  override def withNewChildren(newChildren: Array[LogicalOperator]): LogicalOperator = {
    this.copy(in = newChildren.head)
  }
}
