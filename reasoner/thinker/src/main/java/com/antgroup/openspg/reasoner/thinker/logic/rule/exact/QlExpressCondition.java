package com.antgroup.openspg.reasoner.thinker.logic.rule.exact;

import com.antgroup.openspg.reasoner.warehouse.common.VertexSubGraph;
import lombok.Data;

import java.util.Map;

@Data
public class QlExpressCondition extends Condition {
    private String qlExpress;

    @Override
    public boolean execute(VertexSubGraph vertexGraph, Map<String, Object> context) {
        return false;
    }
}
