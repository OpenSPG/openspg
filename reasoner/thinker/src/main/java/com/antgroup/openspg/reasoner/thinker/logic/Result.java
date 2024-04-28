package com.antgroup.openspg.reasoner.thinker.logic;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import java.util.LinkedList;
import java.util.List;
import lombok.Data;

@Data
public class Result {
  private List<Element> data;
  private TreeLogger traceLog;

  public Result() {
    data = new LinkedList<>();
    traceLog = new TreeLogger("result");
  }

  public void addElement(Element element) {
    data.add(element);
  }

  public void addElements(List<Element> elements) {
    data.addAll(elements);
  }
}
