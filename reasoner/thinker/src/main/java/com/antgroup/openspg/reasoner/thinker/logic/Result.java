package com.antgroup.openspg.reasoner.thinker.logic;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import java.util.List;
import lombok.Data;

@Data
public class Result {
  private List<Element> data;
  private TreeLogger traceLog;
}
