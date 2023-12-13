/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rdg.common.groupProcess;

import java.io.Serializable;
import java.util.List;

import com.antgroup.openspg.reasoner.lube.common.expr.AggIfOpExpr;
import com.antgroup.openspg.reasoner.lube.common.expr.Aggregator;
import com.antgroup.openspg.reasoner.lube.common.expr.AggregatorOpSet;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.logical.Var;
import com.antgroup.openspg.reasoner.warehouse.utils.WareHouseUtils;

/**
 * @author peilong.zpl
 * @version $Id: AggIfOpProcess.java, v 0.1 2023-10-07 16:07 peilong.zpl Exp $$
 */
public class AggIfOpProcessBaseGroupProcess extends BaseGroupProcess implements Serializable {

    /**
     * constructor
     * @param taskId
     * @param var
     * @param aggregator
     */
    public AggIfOpProcessBaseGroupProcess(String taskId, Var var, Aggregator aggregator) {
        super(taskId, var, aggregator);
    }

    /**
     * get op
     * @return
     */
    public AggIfOpExpr getAggIfOpExpr() {
        return (AggIfOpExpr) this.aggOp;
    }

    @Override
    protected List<String> parseRuleList() {
        return WareHouseUtils.getRuleList(getAggIfOpExpr().condition());
    }

    @Override
    public AggregatorOpSet getAggOpSet() {
        return getAggIfOpExpr().aggOpExpr().name();
    }

    @Override
    public Expr getAggEle() {
        return getAggIfOpExpr().aggOpExpr().aggEleExpr();
    }
}