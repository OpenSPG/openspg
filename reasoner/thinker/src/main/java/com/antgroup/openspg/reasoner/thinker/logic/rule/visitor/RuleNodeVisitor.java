package com.antgroup.openspg.reasoner.thinker.logic.rule.visitor;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.And;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Condition;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Not;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Or;
import java.util.List;
import java.util.Map;

public interface RuleNodeVisitor<R> {
  abstract R visit(Or node, List<Element> spoList, Map<String, Object> context, TreeLogger logger);

  abstract R visit(And node, List<Element> spoList, Map<String, Object> context, TreeLogger logger);

  abstract R visit(Not node, List<Element> spoList, Map<String, Object> context, TreeLogger logger);

  abstract R visit(
      Condition node, List<Element> spoList, Map<String, Object> context, TreeLogger logger);
}
