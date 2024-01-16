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
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.common._
import com.antgroup.openspg.reasoner.lube.common.expr.{Directly, Expr}
import com.antgroup.openspg.reasoner.lube.common.graph.IRVariable
import com.antgroup.openspg.reasoner.lube.logical.{EdgeVar, ExprUtil, NodeVar, PropertyVar, SolvedModel, Var}

final case class Project(in: LogicalOperator, expr: Map[Var, Expr], solved: SolvedModel)
    extends StackingLogicalOperator {

  override def refFields: List[Var] = {
    val fieldsMap = new mutable.HashMap[String, Var]()
    for (pair <- expr) {
      pair._2 match {
        case Directly => fieldsMap.put(pair._1.name, pair._1)
        case e: Expr =>
          val refPair = ExprUtil.getReferProperties(e)
          for (ref <- refPair) {
            val alias = if (ref._1 != null) ref._1 else solved.getField(IRVariable(ref._2)).name
            solved.fields.get(alias).get match {
              case NodeVar(name, _) =>
                val node = NodeVar(name, Set.apply(new Field(ref._2, KTObject, true)))
                fieldsMap.put(name, node.merge(fieldsMap.get(name)))
              case EdgeVar(name, _) =>
                val edge = EdgeVar(name, Set.apply(new Field(ref._2, KTObject, true)))
                fieldsMap.put(name, edge.merge(fieldsMap.get(name)))
              case _ =>
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

}
