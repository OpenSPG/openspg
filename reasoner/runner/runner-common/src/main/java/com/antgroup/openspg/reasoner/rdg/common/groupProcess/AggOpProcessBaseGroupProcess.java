/*
 * Copyright 2023 Ant Group CO., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */
package com.antgroup.openspg.reasoner.rdg.common.groupProcess;

import java.io.Serializable;
import java.util.List;

import com.antgroup.openspg.reasoner.lube.common.expr.AggOpExpr;
import com.antgroup.openspg.reasoner.lube.common.expr.Aggregator;
import com.antgroup.openspg.reasoner.lube.common.expr.AggregatorOpSet;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.logical.Var;


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