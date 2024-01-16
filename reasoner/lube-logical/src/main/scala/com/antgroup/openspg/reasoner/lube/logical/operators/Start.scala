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

import com.antgroup.openspg.reasoner.lube.catalog.SemanticPropertyGraph
import com.antgroup.openspg.reasoner.lube.logical.{SolvedModel, Var}

abstract class Source extends LogicalLeafOperator {
  def alias: String
}

final case class Start(
    graph: SemanticPropertyGraph,
    alias: String,
    types: Set[String],
    solved: SolvedModel)
    extends Source {
  override def refFields: List[Var] = fields

  override def fields: List[Var] = solved.fields.values.toList
}

final case class Driving(graph: SemanticPropertyGraph, alias: String, solved: SolvedModel)
    extends Source {
  override def refFields: List[Var] = fields

  override def fields: List[Var] = solved.fields.values.toList
}
