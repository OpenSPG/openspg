package com.antgroup.openspg.reasoner.rule.op;

/**
 * div op without exception
 * return null when exception
 */
public class OperatorMultiDiv extends com.ql.util.express.instruction.op.OperatorMultiplyDivide {
    public OperatorMultiDiv(String name) {
        super(name);
    }

    public Object executeInner(Object[] list) throws Exception {
        if (!(list[0] instanceof Number) || !(list[1] instanceof Number)) {
            return null;
        } else {
            return executeInner(list[0], list[1]);
        }
    }
}
