/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rdg.common.groupProcess;

import java.io.Serializable;
import java.util.List;

import com.antgroup.openspg.reasoner.lube.common.expr.AggOpExpr;
import com.antgroup.openspg.reasoner.lube.common.expr.Aggregator;
import com.antgroup.openspg.reasoner.lube.common.expr.AggregatorOpSet;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.logical.Var;

/**
 * @author peilong.zpl
 * @version $Id: AggOpProcess.java, v 0.1 2023-10-07 16:08 peilong.zpl Exp $$
 */
public class AggOpProcessBaseGroupProcess extends BaseGroupProcess implements Serializable {

    public AggOpProcessBaseGroupProcess(String taskId, Var var, Aggregator aggregator) {
        super(taskId, var, aggregator);
    }

    public AggOpExpr getAggOpExpr() {
        return (AggOpExpr) this.aggOp;
    }

    @Override
    protected List<String> parseRuleList() {
        return null;
    }

    @Override
    public AggregatorOpSet getAggOpSet() {
        return getAggOpExpr().name();
    }

    @Override
    public Expr getAggEle() {
        return getAggOpExpr().aggEleExpr();
    }

}