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

import com.antgroup.openspg.reasoner.common.trees.AbstractTreeNode
import com.antgroup.openspg.reasoner.lube.common.graph.{IRField, IRGraph}

/**
 * Unresolved Logical Plan
 */
abstract class Block extends AbstractTreeNode[Block] {
  /**
   * list of dependent blocks
   * @return
   */
  def dependencies: List[Block]

  /**
   * The type of the block
   * @return
   */
  def blockType: BlockType = BlockType(this.getClass.getSimpleName)

  /**
   * The metadata output by the current block
   * @return
   */
  def binds: Binds

  /**
   * The binding graph of this block
   * @return
   */
  def graph: IRGraph
}

final case class BlockType(name: String)

object Binds {

  def empty: Binds = new Binds {
    override def fields: List[IRField] = List.empty
  }

}

trait Binds {
  def fields: List[IRField]
}

final case class Fields(fields: List[IRField]) extends Binds

object Fields {
  def empty: Fields = new Fields(List.empty)
}

