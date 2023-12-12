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

