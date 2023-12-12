package com.antgroup.openspg.reasoner.lube.block

import com.antgroup.openspg.reasoner.lube.common.graph.IRGraph
import com.antgroup.openspg.reasoner.lube.common.rule.Rule

/**
 * a filter block，to filter data that meets the rules
 *
 * @param dependencies
 * @param rules
 * @param graph
 */
final case class FilterBlock(dependencies: List[Block], rules: Rule, graph: IRGraph)
    extends BasicBlock[Binds](BlockType("filter")) {

  override def binds: Binds = {
    dependencies.head.binds
  }

}
