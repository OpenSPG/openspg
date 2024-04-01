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

package com.antgroup.openspg.reasoner.lube.block

import scala.collection.mutable.ListBuffer

import com.antgroup.openspg.reasoner.lube.common.graph.IRField
import com.antgroup.openspg.reasoner.lube.common.rule.Rule

/**
 * a project block，to convert variable to another form
 * @param dependencies
 * @param projects project expression
 * @param graph
 */
final case class ProjectBlock(
    dependencies: List[Block],
    projects: ProjectFields = ProjectFields())
    extends BasicBlock[Fields](BlockType("project")) {

  override def binds: Fields = {
    val fields = new ListBuffer[IRField]
    fields ++= dependencies.head.binds.fields
    fields ++= this.projects.fields
    Fields(fields.toList)
  }

  override def withNewChildren(newChildren: Array[Block]): Block = {
    this.copy(dependencies = newChildren.toList)
  }

}

/**
 * to explain how to convert variable to another form by express
 * @param items
 */
final case class ProjectFields(items: Map[IRField, Rule] = Map.empty) extends Binds {
  override def fields: List[IRField] = items.keySet.toList
}
