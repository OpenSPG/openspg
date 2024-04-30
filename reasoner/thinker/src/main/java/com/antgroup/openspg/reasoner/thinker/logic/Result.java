package com.antgroup.openspg.reasoner.thinker.logic;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import lombok.Data;

@Data
public class Result {
  private Element data;
  private TreeLogger traceLog;

  public Result() {}

  public Result(Element data, TreeLogger traceLog) {
    this.data = data;
    this.traceLog = traceLog;
  }
}
