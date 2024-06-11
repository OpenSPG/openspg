/** Alipay.com Inc. Copyright (c) 2004-2024 All Rights Reserved. */
package com.antgroup.openspg.reasoner.recorder.action;

import com.antgroup.openspg.reasoner.lube.common.rule.Rule;

/**
 * @author peilong.zpl
 * @version $Id: DebugInfoWithRule.java, v 0.1 2024-06-06 13:37 peilong.zpl Exp $$
 */
public class DebugInfoWithRule {
  private Rule rule;
  private String runtimeValue;

  public DebugInfoWithRule(Rule rule, String runtimeValue) {
    this.rule = rule;
    this.runtimeValue = runtimeValue;
  }

  public Rule getRule() {
    return rule;
  }

  public void setRule(Rule rule) {
    this.rule = rule;
  }

  public String getRuntimeValue() {
    return runtimeValue;
  }

  public void setRuntimeValue(String runtimeValue) {
    this.runtimeValue = runtimeValue;
  }
}
