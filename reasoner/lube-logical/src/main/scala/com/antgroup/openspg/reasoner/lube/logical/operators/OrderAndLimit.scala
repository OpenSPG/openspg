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
import com.antgroup.openspg.reasoner.lube.block.SortItem
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field
import com.antgroup.openspg.reasoner.lube.logical._

final case class OrderAndLimit(
    in: LogicalOperator,
    group: List[Var],
    sortItem: Seq[SortItem],
    limit: Option[Int])
    extends StackingLogicalOperator {

  override def refFields: List[Var] = {
    val refPair = sortItem.map(x => ExprUtil.getReferProperties(x.expr)).toList.flatten
    val fieldsMap = new mutable.HashMap[String, Var]()
    for (ref <- refPair) {
      ref match {
        case (null, name) =>
          solved.fields(name) match {
            case NodeVar(_, _) =>
              val node = NodeVar(name, Set.empty)
              fieldsMap.put(name, node.merge(fieldsMap.get(name)))
            case EdgeVar(_, _) =>
              val edge = EdgeVar(name, Set.empty)
              fieldsMap.put(name, edge.merge(fieldsMap.get(name)))
            case _ =>
          }
        case (alias, filedName) =>
          solved.fields(ref._1) match {
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
    group.map(x => fieldsMap.put(x.name, NodeVar(x.name, Set.empty)))
    fieldsMap.values.toList
  }

  /**
   * the output fields of current operator
   *
   * @return
   */
  override def fields: List[Var] = in.fields

  override def solved: SolvedModel = in.solved.solve
}
