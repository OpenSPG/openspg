/*
 * Copyright 2023 OpenSPG Authors
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

import com.antgroup.openspg.reasoner.lube.common.expr.AggIfOpExpr;
import com.antgroup.openspg.reasoner.lube.common.expr.Aggregator;
import com.antgroup.openspg.reasoner.lube.common.expr.AggregatorOpSet;
import com.antgroup.openspg.reasoner.lube.common.expr.Expr;
import com.antgroup.openspg.reasoner.lube.logical.Var;
import com.antgroup.openspg.reasoner.warehouse.utils.WareHouseUtils;
import java.io.Serializable;
import java.util.List;

public class AggIfOpProcessBaseGroupProcess extends BaseGroupProcess implements Serializable {

  /**
   * constructor
   *
   * @param taskId
   * @param var
   * @param aggregator
   */
  public AggIfOpProcessBaseGroupProcess(String taskId, Var var, Aggregator aggregator) {
    super(taskId, var, aggregator);
  }

  /**
   * get op
   *
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
