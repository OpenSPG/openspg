package com.antgroup.openspg.reasoner.thinker.logic.rule.exact;

import com.antgroup.openspg.reasoner.thinker.logic.rule.Node;
import com.antgroup.openspg.reasoner.thinker.logic.rule.visitor.RuleNodeVisitor;
import com.antgroup.openspg.reasoner.warehouse.common.VertexSubGraph;

import java.util.Map;

public abstract class Condition implements Node {
    public final <R> R accept(VertexSubGraph vertexGraph, Map<String, Object> context, RuleNodeVisitor<R> visitor) {
        return visitor.visitCondition(this, vertexGraph, context);
    }

    public abstract boolean execute(VertexSubGraph vertexGraph, Map<String, Object> context);
}
