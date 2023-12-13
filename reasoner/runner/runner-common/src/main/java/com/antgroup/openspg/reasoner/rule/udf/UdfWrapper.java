/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rule.udf;

import com.antgroup.openspg.reasoner.udf.model.RuntimeUdfMeta;
import com.ql.util.express.Operator;

/**
 * @author donghai.ydh
 * @version UdfWrapper.java, v 0.1 2023年02月24日 14:35 donghai.ydh
 */
public class UdfWrapper extends Operator {

    private final RuntimeUdfMeta runtimeUdfMeta;

    /**
     * udf wrapper for qlexpress
     *
     * @param runtimeUdfMeta
     */
    public UdfWrapper(RuntimeUdfMeta runtimeUdfMeta) {
        this.runtimeUdfMeta = runtimeUdfMeta;
    }

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        return this.runtimeUdfMeta.invoke(objects);
    }

    @Override
    public String toString() {
        return "qlexpress udf " + this.runtimeUdfMeta.getName();
    }
}