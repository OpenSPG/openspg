package com.antgroup.openspg.reasoner.thinker.logic.rule.visitor;

import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.And;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Condition;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Not;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Or;
import com.antgroup.openspg.reasoner.warehouse.common.VertexSubGraph;
import java.util.Map;

public abstract class RuleNodeVisitor<R> {
  private TreeLogger logger;

  protected RuleNodeVisitor() {}

  protected RuleNodeVisitor(TreeLogger logger) {
    this.logger = logger;
  }

  public abstract R visit(Or node, VertexSubGraph vertexGraph, Map<String, Object> context);

  public abstract R visit(And node, VertexSubGraph vertexGraph, Map<String, Object> context);

  public abstract R visit(Not node, VertexSubGraph vertexGraph, Map<String, Object> context);

  public abstract R visit(Condition node, VertexSubGraph vertexGraph, Map<String, Object> context);
}
