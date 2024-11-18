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
