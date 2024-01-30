package com.antgroup.openspg.reasoner.lube.logical.optimizer.rules

import com.antgroup.openspg.reasoner.lube.common.pattern.NodePattern
import com.antgroup.openspg.reasoner.lube.logical.operators.{BoundedVarLenExpand, LogicalOperator, PatternJoin, PatternScan}
import com.antgroup.openspg.reasoner.lube.logical.optimizer.{Direction, SimpleRule, Up}
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext

object PatternJoinPure extends SimpleRule {
  override def rule(implicit
      context: LogicalPlannerContext): PartialFunction[LogicalOperator, LogicalOperator] = {
    case patternJoin @ PatternJoin(
          boundedVarLenExpand: BoundedVarLenExpand,
          scan: PatternScan,
          _) =>
      if (scan.pattern.isInstanceOf[NodePattern]) {
        boundedVarLenExpand
      } else {
        patternJoin
      }
  }

  override def direction: Direction = Up

  override def maxIterations: Int = 1
}
