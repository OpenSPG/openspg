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

import com.antgroup.openspg.reasoner.lube.common.pattern.{Pattern, PatternElement}
import com.antgroup.openspg.reasoner.lube.logical.{SolvedModel, Var}
import com.antgroup.openspg.reasoner.lube.logical.PatternOps.PatternOps

final case class ExpandInto(in: LogicalOperator, target: PatternElement, pattern: Pattern)
    extends StackingLogicalOperator {

  override def fields: List[Var] = {
    val list = new mutable.HashMap[String, Var]()
    for (v <- in.fields) {
      list.put(v.name, v)
    }
    val curVars = pattern.toVar(solved, graph)
    for (v <- curVars) {
      list.put(v.name, v)
    }
    list.values.toList
  }

  override def solved: SolvedModel = in.solved

  override def refFields: List[Var] = pattern.toVar(solved, graph)
}
