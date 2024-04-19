package com.antgroup.openspg.reasoner.thinker.logic.rule.exact;

import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import com.antgroup.openspg.reasoner.warehouse.common.VertexSubGraph;
import java.util.Map;
import java.util.Objects;
import lombok.Data;

@Data
public class QlExpressCondition extends Condition {
  private String qlExpress;

  public QlExpressCondition(String qlExpress) {
    this.qlExpress = qlExpress;
  }

  @Override
  public boolean execute(
      VertexSubGraph vertexGraph, Map<String, Object> context, TreeLogger logger) {
    return false;
  }

  @Override
  public String getExpress() {
    return qlExpress;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof QlExpressCondition)) {
      return false;
    }
    QlExpressCondition that = (QlExpressCondition) o;
    return Objects.equals(qlExpress, that.qlExpress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(qlExpress);
  }
}
