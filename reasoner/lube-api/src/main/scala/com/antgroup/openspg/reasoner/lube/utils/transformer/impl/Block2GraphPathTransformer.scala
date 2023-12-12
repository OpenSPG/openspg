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

package com.antgroup.openspg.reasoner.lube.utils.transformer.impl

import com.antgroup.openspg.reasoner.lube.block.{Block, MatchBlock}
import com.antgroup.openspg.reasoner.lube.common.pattern.GraphPath
import com.antgroup.openspg.reasoner.lube.utils.transformer.BlockTransformer

class Block2GraphPathTransformer extends BlockTransformer[GraphPath] {
  def transformBlock2GraphPath(block: Block): List[GraphPath] = {
    block match {
      case MatchBlock(_, patterns) => patterns.values.toList
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
