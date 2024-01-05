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

package com.antgroup.openspg.reasoner.lube.logical.operators

import scala.collection.mutable

import com.antgroup.openspg.reasoner.common.types.KTObject
import com.antgroup.openspg.reasoner.lube.block.{AddPredicate, AddProperty, AddVertex, DDLOp}
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.common.expr.VConstant
import com.antgroup.openspg.reasoner.lube.common.graph.{IREdge, IRNode, IRVariable}
import com.antgroup.openspg.reasoner.lube.logical.{EdgeVar, NodeVar, SolvedModel, Var}
import com.antgroup.openspg.reasoner.lube.utils.ExprUtils

final case class DDL(in: LogicalOperator, ddlOp: Set[DDLOp])
    extends StackingLogicalOperator
    with EmptyFields {

  override def refFields: List[Var] = {
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
        case addVertex: AddVertex =>
          addVertex.props.foreach(tuple => {
            tuple._2 match {
              case expr: VConstant =>
              case _ =>
                val ref = ExprUtils
                  .getAllInputFieldInRule(tuple._2, solved.getNodeAliasSet, solved.getEdgeAliasSet)
                  .head
                ref match {
                  case IRNode(name, fields) =>
                    val nodeV = NodeVar(name, fields.map(f => new Field(f, KTObject, true)).toSet)
                    fieldList.append(nodeV)
                  case IREdge(name, fields) =>
                    val edgeV = EdgeVar(name, fields.map(f => new Field(f, KTObject, true)).toSet)
                    fieldList.append(edgeV)
                  case _ =>
                }
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

  override def solved: SolvedModel = in.solved.solve
}
