package com.antgroup.openspg.reasoner.lube.utils.transformer

import com.antgroup.openspg.reasoner.lube.block.Block

trait BlockTransformer[A] {
  /**
   * Transform Block to other format list
   * @param block
   * @return
   */
  def transform(block: Block): List[A]
}
