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

import com.antgroup.openspg.reasoner.common.exception.InvalidRefVariable
import com.antgroup.openspg.reasoner.lube.common.expr.Aggregator
import com.antgroup.openspg.reasoner.lube.common.graph.{IREdge, IRNode}
import com.antgroup.openspg.reasoner.lube.logical._
import com.antgroup.openspg.reasoner.lube.utils.ExprUtils

final case class Aggregate(
    in: LogicalOperator,
    group: List[Var],
    aggregations: Map[Var, Aggregator],
    solved: SolvedModel)
    extends StackingLogicalOperator {

  override def fields: List[Var] = {
    val fieldsMap = new mutable.HashMap[String, Var]
    for (field <- in.fields) {
      fieldsMap.put(field.name, field)
    }
    for (pair <- aggregations) {
      pair._1 match {
        case PropertyVar(name, field) =>
          fieldsMap.put(pair._1.name, fieldsMap(pair._1.name).merge(Option.apply(pair._1)))
        case _ =>
      }
    }
    fieldsMap.values.toList
  }

  override def refFields: List[Var] = {
    val refPair =
      aggregations.values.map(ExprUtils.getAllInputFieldInRule(_, null, null)).toList.flatten
    val fieldsMap = new mutable.HashMap[String, Var]()
    for (ref <- refPair) {
      val fieldName = ref.name
      if (!solved.fields.contains(fieldName)) {
        throw InvalidRefVariable(s"cannot find ref ${ref}")
      }
      solved.fields(fieldName) match {
        case nodeVar: NodeVar =>
          val node = if (ref.isInstanceOf[IRNode]) {
            NodeVar(
              fieldName,
              nodeVar.fields.filter(f => ref.asInstanceOf[IRNode].fields.contains(f.name)))
          } else {
            NodeVar(fieldName, Set.empty)
          }
          fieldsMap.put(fieldName, node.merge(fieldsMap.get(fieldName)))
        case edgeVar: EdgeVar =>
          val edge = if (ref.isInstanceOf[IRNode]) {
            EdgeVar(
              fieldName,
              edgeVar.fields.filter(f => ref.asInstanceOf[IRNode].fields.contains(f.name)))
          } else {
            EdgeVar(fieldName, Set.empty)
          }
          fieldsMap.put(fieldName, edge.merge(fieldsMap.get(fieldName)))
        case pathVar: PathVar =>
          fieldsMap.put(fieldName, pathVar)
        case repeatPathVar: RepeatPathVar =>
          fieldsMap.put(fieldName, repeatPathVar)
        case _ =>
      }

    }
    fieldsMap.values.toList
  }

}
