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

package com.antgroup.openspg.core.schema.model.semantic;

import com.antgroup.openspg.server.common.model.base.BaseToString;
import java.util.Objects;
import java.util.Random;

/** The rule id. */
public class RuleCode extends BaseToString {

  private static final long serialVersionUID = 2722952911591368565L;

  /** The unique id of the rule. */
  private final String code;

  public RuleCode(String code) {
    this.code = code;
  }

  public static String genRuleCode() {
    String charcter = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    Random random = new Random();
    StringBuilder sb = new StringBuilder();
    sb.append(charcter.charAt(random.nextInt(charcter.length())));

    for (int i = 0; i < 6; ++i) {
      int number = random.nextInt(base.length());
      sb.append(base.charAt(number));
    }
    return "RULE_" + sb + "_" + System.currentTimeMillis();
  }

  public String getCode() {
    return code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RuleCode ruleId = (RuleCode) o;
    return Objects.equals(code, ruleId.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }
}
