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
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.common.expr.Aggregator
import com.antgroup.openspg.reasoner.lube.logical._

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
    val refPair = aggregations.values.map(ExprUtil.getReferProperties(_)).toList.flatten
    val fieldsMap = new mutable.HashMap[String, Var]()
    for (ref <- refPair) {
      ref match {
        case (null, name) =>
          solved.fields.get(name).get match {
            case NodeVar(_, _) =>
              val node = NodeVar(name, Set.empty)
              fieldsMap.put(name, node.merge(fieldsMap.get(name)))
            case EdgeVar(_, _) =>
              val edge = EdgeVar(name, Set.empty)
              fieldsMap.put(name, edge.merge(fieldsMap.get(name)))
            case _ =>
          }
        case (alias, filedName) =>
          solved.fields.get(ref._1).get match {
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
    fieldsMap.values.toList
  }

}
