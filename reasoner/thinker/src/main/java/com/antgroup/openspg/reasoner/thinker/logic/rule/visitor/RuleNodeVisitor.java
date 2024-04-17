package com.antgroup.openspg.reasoner.thinker.logic.rule.visitor;

import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.And;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Condition;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Not;
import com.antgroup.openspg.reasoner.thinker.logic.rule.exact.Or;
import com.antgroup.openspg.reasoner.warehouse.common.VertexSubGraph;
import java.util.Map;

public interface RuleNodeVisitor<R> {

  R visit(Or node, VertexSubGraph vertexGraph, Map<String, Object> context, TreeLogger treeLogger);

  R visit(And and, VertexSubGraph vertexGraph, Map<String, Object> context, TreeLogger treeLogger);

  R visit(Not not, VertexSubGraph vertexGraph, Map<String, Object> context, TreeLogger treeLogger);

  R visit(
      Condition condition,
      VertexSubGraph vertexGraph,
      Map<String, Object> context,
      TreeLogger treeLogger);
}
