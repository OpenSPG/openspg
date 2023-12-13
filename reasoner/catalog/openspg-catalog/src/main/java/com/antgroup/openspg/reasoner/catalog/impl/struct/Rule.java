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

package com.antgroup.openspg.reasoner.catalog.impl.struct;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class Rule {
  @JSONField(name = "ruleId")
  private String ruleId;

  @JSONField(name = "ruleContent")
  private String ruleContent;

  /**
   * Getter method for property ruleId.
   *
   * @return property value of ruleId
   */
  public String getRuleId() {
    return ruleId;
  }

  /**
   * Setter method for property ruleId.
   *
   * @param ruleId value to be assigned to property ruleId
   */
  public void setRuleId(String ruleId) {
    this.ruleId = ruleId;
  }

  /**
   * Getter method for property ruleContent.
   *
   * @return property value of ruleContent
   */
  public String getRuleContent() {
    return ruleContent;
  }

  /**
   * Setter method for property ruleContent.
   *
   * @param ruleContent value to be assigned to property ruleContent
   */
  public void setRuleContent(String ruleContent) {
    this.ruleContent = ruleContent;
  }
}
