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

package com.antgroup.openspg.reasoner.util

import com.antgroup.openspg.reasoner.common.graph.edge.Direction
import com.antgroup.openspg.reasoner.lube.common.pattern.{
  Connection,
  PatternConnection,
  PatternElement
}
import com.antgroup.openspg.reasoner.lube.common.rule.Rule

case class PathConnection(
    alias: String,
    source: String,
    relTypes: Set[String],
    target: String,
    direction: Direction,
    rule: Rule,
    vertexSchemaList: List[PatternElement],
    edgeSchemaList: List[PatternConnection])
    extends Connection {

  override def update(source: String, target: String): Connection =
    copy(source = source, target = target)

  override def update(rule: Rule): Connection = copy(rule = rule)

  override def update(direction: Direction): Connection = copy(direction = direction)

  override def limit: Integer = 0

}
