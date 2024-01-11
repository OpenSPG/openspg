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

package com.antgroup.openspg.reasoner.lube.block

import scala.collection.mutable.ListBuffer

import com.antgroup.openspg.reasoner.lube.common.expr.Aggregator
import com.antgroup.openspg.reasoner.lube.common.graph.IRField

final case class AggregationBlock(
    dependencies: List[Block],
    aggregations: Aggregations,
    group: List[IRField])
    extends BasicBlock[Fields](BlockType("aggregation")) {

  override def binds: Fields = {
    val fields = new ListBuffer[IRField]
    fields ++= dependencies.head.binds.fields
    fields ++= aggregations.fields
    Fields(fields.toList)
  }

}

final case class Aggregations(pairs: Map[IRField, Aggregator]) extends Binds {
  override def fields: List[IRField] = pairs.keySet.toList

}
