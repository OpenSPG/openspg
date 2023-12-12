package com.antgroup.openspg.reasoner.lube.block

import com.antgroup.openspg.reasoner.lube.common.graph.IRGraph

case class SourceBlock(graph: IRGraph) extends BasicBlock[Binds](BlockType("source")) {
  override val dependencies: List[Block] = List.empty
  override val binds: Binds = Binds.empty
}
