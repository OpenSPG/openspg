package com.antgroup.openspg.reasoner.thinker.logic.rule.visitor;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Node;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.And;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Condition;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Not;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Or;
import java.util.List;
import java.util.Map;

public class RuleExecutor implements RuleNodeVisitor<Boolean> {

  @Override
  public Boolean visit(
      Or node, List<Element> spoList, Map<String, Object> context, TreeLogger logger) {
    Boolean ret = null;
    for (Node child : node.getChildren()) {
      Boolean c = child.accept(spoList, context, this, logger.addChild(child.toString()));
      c = c == null ? false : c;
      if (ret == null) {
        ret = c;
      } else {
        ret = ret || c;
      }
    }
    logger.log(ret);
    logger.setCurrentNodeRst(ret);
    return ret;
  }

  @Override
  public Boolean visit(
      And node, List<Element> spoList, Map<String, Object> context, TreeLogger logger) {
    Boolean ret = null;
    for (Node child : node.getChildren()) {
      Boolean c = child.accept(spoList, context, this, logger.addChild(child.toString()));
      c = c == null ? false : c;
      if (ret == null) {
        ret = c;
      } else {
        ret = ret && c;
      }
    }
    logger.log(ret);
    logger.setCurrentNodeRst(ret);
    return ret;
  }

  @Override
  public Boolean visit(
      Not node, List<Element> spoList, Map<String, Object> context, TreeLogger logger) {
    Boolean ret = null;
    Node child = node.getChild();
    Boolean r = child.accept(spoList, context, this, logger);
    r = r == null ? false : r;
    ret = !r;
    logger.log(ret);
    logger.setCurrentNodeRst(ret);
    return ret;
  }

  @Override
  public Boolean visit(
      Condition node, List<Element> spoList, Map<String, Object> context, TreeLogger logger) {
    Boolean ret = node.execute(spoList, context, logger);
    ret = ret == null ? false : ret;
    logger.log(ret);
    logger.setCurrentNodeRst(ret);
    return ret;
  }
}
