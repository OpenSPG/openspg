package com.antgroup.openspg.reasoner.thinker.logic.rule.exact;

import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import com.antgroup.openspg.reasoner.warehouse.common.VertexSubGraph;
import java.util.Map;
import lombok.Data;

@Data
public class QlExpressCondition extends Condition {
  private String qlExpress;

  @Override
  public boolean execute(
      VertexSubGraph vertexGraph, Map<String, Object> context, TreeLogger logger) {
    return false;
  }
}
