package com.antgroup.openspg.reasoner.thinker.logic;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import java.util.List;
import lombok.Data;

@Data
public class Evidence {
  private List<Element> evidence;

  public Evidence(List<Element> evidence) {
    this.evidence = evidence;
  }
}
