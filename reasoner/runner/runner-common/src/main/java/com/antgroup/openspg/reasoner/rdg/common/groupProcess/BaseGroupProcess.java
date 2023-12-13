/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rdg.common.groupProcess;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import com.antgroup.openspg.reasoner.common.exception.NotImplementedException;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.lube.common.expr.AggUdf;
import com.antgroup.openspg.reasoner.lube.common.expr.Aggregator;
import com.antgroup.openspg.reasoner.lube.common.expr.AggregatorOpSet;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.logical.PropertyVar;
import com.antgroup.openspg.reasoner.lube.logical.Var;
import com.antgroup.openspg.reasoner.rule.RuleRunner;
import com.antgroup.openspg.reasoner.udf.UdfMngFactory;
import com.antgroup.openspg.reasoner.udf.model.UdafMeta;
import com.antgroup.openspg.reasoner.warehouse.utils.WareHouseUtils;
import scala.collection.JavaConversions;

/**
 * @author peilong.zpl
 * @version $Id: BaseGroupProcess.java, v 0.1 2023-10-07 16:06 peilong.zpl Exp $$
 */
public abstract class BaseGroupProcess implements Serializable {
    protected     Var          var;
    protected     UdafMeta     udafMeta;
    protected     Object[]     udfInitParams;
    protected     List<String> ruleList;
    protected     Aggregator   aggOp;
    protected     String       taskId;

    /**
     * Construct from var and aggregator
     * @param taskId
     * @param var
     * @param aggregator
     */
    public BaseGroupProcess(String taskId, Var var, Aggregator aggregator) {
        this.taskId = taskId;
        this.var = var;
        this.aggOp = aggregator;
        this.ruleList = parseRuleList();
        this.udfInitParams = parseUdfInitParams();
        this.udafMeta = parseUdafMeta();
    }

    /**
     * judge is first agg function
     * @return
     */
    public boolean isFirstAgg() {
        return (!(var instanceof PropertyVar));
    }

    /**
     * get udaf str name from op
     * @param op
     * @return
     */
    public String getUdafStrName(AggregatorOpSet op) {
        if (op instanceof AggUdf) {
            AggUdf aggUdf = (AggUdf) op;
            return aggUdf.name();
        } else {
            return op.toString();
        }
    }

    private Object[] getUdafInitializeParams(List<Expr> exprList) {
        Object[] params = new Object[exprList.size()];
        for (int i = 0; i < exprList.size(); ++i) {
            Expr expr = exprList.get(i);
            List<String> paramRuleList = WareHouseUtils.getRuleList(expr);
            Object value = RuleRunner.getInstance().executeExpression(new HashMap<>(), paramRuleList, this.taskId);
            params[i] = value;
        }
        return params;
    }

    protected Object[] parseUdfInitParams() {
        Object[] udfInitParams = null;
        AggregatorOpSet aggregatorOpSet = getAggOpSet();
        if (aggregatorOpSet instanceof AggUdf) {
            AggUdf aggUdf = (AggUdf) aggregatorOpSet;
            udfInitParams = getUdafInitializeParams(JavaConversions.seqAsJavaList(aggUdf.funcArgs()));
        }
        return udfInitParams;
    }

    protected UdafMeta parseUdafMeta() {
        String udafName = getUdafStrName(getAggOpSet());
        UdafMeta udafMeta = UdfMngFactory.getUdfMng().getUdafMeta(udafName, KTString$.MODULE$);
        if (udafMeta == null) {
            throw new NotImplementedException("unsupported aggregator function, type=" + udafName, null);
        }
        return udafMeta;
    }

    /**
     * parse rule list by op
     * @return
     */
    abstract protected List<String> parseRuleList();

    /**
     * get agg op set by op
     * @return
     */
    abstract public AggregatorOpSet getAggOpSet();

    /**
     * get agg ele by op
     * @return
     */
    abstract public Expr getAggEle();

    /**
     * getter
     * @return
     */
    public Var getVar() {
        return var;
    }

    /**
     * getter
     * @return
     */
    public UdafMeta getUdafMeta() {
        return udafMeta;
    }

    /**
     * getter
     * @return
     */
    public Object[] getUdfInitParams() {
        return udfInitParams;
    }

    /**
     * getter
     * @return
     */
    public List<String> getRuleList() {
        return ruleList;
    }
}