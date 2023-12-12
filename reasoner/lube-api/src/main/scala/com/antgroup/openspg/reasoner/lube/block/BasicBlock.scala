package com.antgroup.openspg.reasoner.lube.block

abstract class BasicBlock[B <: Binds](override val blockType: BlockType) extends Block {
  override def binds: B
}
