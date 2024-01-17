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
import com.antgroup.openspg.reasoner.lube.common.graph.{IREdge, IRNode, IRVariable}
import com.antgroup.openspg.reasoner.lube.common.rule.Rule
import com.antgroup.openspg.reasoner.lube.logical.{EdgeVar, NodeVar, SolvedModel, Var}
import com.antgroup.openspg.reasoner.lube.utils.RuleUtils

final case class Filter(in: LogicalOperator, rule: Rule) extends StackingLogicalOperator {
  override def fields: List[Var] = in.fields

  override def refFields: List[Var] = {
    val refPair =
      RuleUtils.getAllInputFieldInRule(rule, solved.getNodeAliasSet, solved.getEdgeAliasSet)
    val fieldsMap = new mutable.HashMap[String, Var]()
    for (ref <- refPair) {
      ref match {
        case IRNode(name, fields) =>
          fieldsMap.put(name, NodeVar(name, fields.map(f => new Field(f, KTObject, true)).toSet))
        case IREdge(name, fields) =>
          fieldsMap.put(name, EdgeVar(name, fields.map(f => new Field(f, KTObject, true)).toSet))
        case _ =>
      }
    }
    for (ref <- refPair) {
      ref match {
        case irVar @ IRVariable(name) =>
          fieldsMap.put(name, fieldsMap(name).merge(Option.apply(solved.getField(irVar))))
        case _ =>
      }
    }
    fieldsMap.values.toList
  }

  override def solved: SolvedModel = in.solved.solve
}
