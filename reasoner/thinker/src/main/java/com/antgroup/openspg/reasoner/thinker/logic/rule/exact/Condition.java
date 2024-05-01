package com.antgroup.openspg.reasoner.thinker.logic.rule.exact;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Node;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import com.antgroup.openspg.reasoner.thinker.logic.rule.visitor.RuleNodeVisitor;
import java.util.List;
import java.util.Map;

public abstract class Condition implements Node {
  @Override
  public <R> R accept(
      List<Element> spoList,
      Map<String, Object> context,
      RuleNodeVisitor<R> visitor,
      TreeLogger logger) {
    return visitor.visit(this, spoList, context, logger);
  }

  public abstract Boolean execute(
      List<Element> spoList, Map<String, Object> context, TreeLogger logger);
}
