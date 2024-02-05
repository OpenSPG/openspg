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

import com.antgroup.openspg.reasoner.common.types.KTObject
import com.antgroup.openspg.reasoner.lube.block.{AddPredicate, AddProperty, AddVertex, DDLOp}
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.common.expr.{Expr, VConstant}
import com.antgroup.openspg.reasoner.lube.common.graph.{IREdge, IRNode, IRVariable}
import com.antgroup.openspg.reasoner.lube.logical.{EdgeVar, NodeVar, SolvedModel, Var}
import com.antgroup.openspg.reasoner.lube.utils.ExprUtils

final case class DDL(in: LogicalOperator, ddlOp: Set[DDLOp])
    extends StackingLogicalOperator
    with EmptyFields {

  override def refFields: List[Var] = {
    val solvedModel = solved
    val fieldList = new mutable.ListBuffer[Var]()
    for (ddl <- ddlOp) {
      ddl match {
        case addPropOp: AddProperty =>
          fieldList.append(
            NodeVar(
              addPropOp.s.alias,
              Set.apply(new Field(addPropOp.propertyName, addPropOp.propertyType, true))))
        case addPredicate: AddPredicate =>
          fieldList.append(NodeVar(addPredicate.predicate.source.alias, Set.empty))
          addPredicate.predicate.fields.foreach(tuple => {
            val field = getRefFields(tuple._2, solvedModel)
            if (field != null) {
              fieldList.append(field)
            }
          })
        case addVertex: AddVertex =>
          addVertex.props.foreach(tuple => {
            val field = getRefFields(tuple._2, solvedModel)
            if (field != null) {
              fieldList.append(field)
            }
          })
        case _ =>
      }
    }
    val fieldsMap = new mutable.HashMap[String, Var]()
    for (field <- fieldList) {
      if (!fieldsMap.contains(field.name)) {
        fieldsMap.put(field.name, field)
      } else {
        fieldsMap.put(field.name, fieldsMap(field.name).merge(Option.apply(field)))
      }
    }
    fieldsMap.values.toList
  }

  private def getRefFields(expr: Expr, solvedModel: SolvedModel): Var = {
    expr match {
      case expr: VConstant => null
      case _ =>
        val ref = ExprUtils
          .getAllInputFieldInRule(expr, solvedModel.getNodeAliasSet, solvedModel.getEdgeAliasSet)
          .head
        ref match {
          case IRNode(name, fields) =>
            NodeVar(name, fields.map(f => new Field(f, KTObject, true)))
          case IREdge(name, fields) =>
             EdgeVar(name, fields.map(f => new Field(f, KTObject, true)))
          case _ => null
        }
    }
  }

  override def solved: SolvedModel = in.solved.solve
}
