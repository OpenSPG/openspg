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

package com.antgroup.openspg.reasoner.thinker.logic.rule.exact;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner;
import java.util.*;
import lombok.Data;

@Data
public class QlExpressCondition extends Condition {
  private String qlExpress;

  public QlExpressCondition(String qlExpress) {
    this.qlExpress = qlExpress;
  }

  @Override
  public Boolean execute(List<Element> spoList, Map<String, Object> context, TreeLogger logger) {
    Map<String, Object> ruleCtx = new HashMap<>();
    ruleCtx.putAll(context);
    for (Element element : spoList) {
      ruleCtx.put(element.shortString(), true);
    }
    Object rst = RuleRunner.getInstance().executeExpression(ruleCtx, Arrays.asList(qlExpress), "");
    return (Boolean) rst;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof QlExpressCondition)) {
      return false;
    }
    QlExpressCondition that = (QlExpressCondition) o;
    return Objects.equals(qlExpress, that.qlExpress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(qlExpress);
  }

  @Override
  public String toString() {
    return qlExpress;
  }
}
