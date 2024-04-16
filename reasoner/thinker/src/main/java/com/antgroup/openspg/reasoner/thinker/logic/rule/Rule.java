package com.antgroup.openspg.reasoner.thinker.logic.rule;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class Rule implements Serializable {
  private String triggerName;
  private List<Element> body;
  private Element head;
  private Node root;
  private String desc;
}
