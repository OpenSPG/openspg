/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rule.op;

/**
 * like op without exception
 *
 * @author chengqiang.cq
 * @version $Id: OperatorLike.java, v 0.1 2022-11-20 10:56 chengqiang.cq Exp $$
 */
public class OperatorLike extends com.ql.util.express.instruction.op.OperatorLike {
    public OperatorLike(String name) {
        super(name);
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        if (list[0] == null || list[1] == null) {
            return false;
        } else {
            return executeInner(list[0], list[1]);
        }
    }
}