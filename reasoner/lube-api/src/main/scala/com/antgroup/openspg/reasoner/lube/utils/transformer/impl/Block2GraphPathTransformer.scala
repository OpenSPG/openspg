package com.antgroup.openspg.reasoner.lube.utils.transformer.impl

import com.antgroup.openspg.reasoner.lube.block.{Block, MatchBlock}
import com.antgroup.openspg.reasoner.lube.common.pattern.GraphPath
import com.antgroup.openspg.reasoner.lube.utils.transformer.BlockTransformer

class Block2GraphPathTransformer extends BlockTransformer[GraphPath] {
  def transformBlock2GraphPath(block: Block): List[GraphPath] = {
    block match {
      case MatchBlock(_, patterns, _) => patterns.values.toList
      case _ => null
    }
  }
  /**
   * Transform Block to other format list
   *
   * @param block
   * @return
   */
  override def transform(block: Block): List[GraphPath] = {
    if (block.dependencies == null || block.dependencies.isEmpty) {
      transformBlock2GraphPath(block)
    } else {
      var graphPaths = List[GraphPath]()
      for (depRule <- block.dependencies) {
        val graphPath = transform(depRule)
        if (graphPath != null) {
          graphPaths = graphPaths ++ graphPath
        }
      }
      val cur = transformBlock2GraphPath(block)
      if (cur != null) {
        graphPaths = graphPaths ++ cur
      }
      graphPaths
    }
  }
}
