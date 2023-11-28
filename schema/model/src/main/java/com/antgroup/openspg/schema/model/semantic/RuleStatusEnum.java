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

package com.antgroup.openspg.schema.model.semantic;

/** Status enum of semantic rule. */
public enum RuleStatusEnum {
  /** The rule is init. */
  INIT,

  /** The rule is used in gray env. */
  GRAY,

  /** The rule is used in product env. */
  PROD,

  /** The rule is offline */
  OFF,

  /** The rule is deleted */
  DEL;

  public static RuleStatusEnum toEnum(String val) {
    for (RuleStatusEnum ruleStatusEnum : RuleStatusEnum.values()) {
      if (ruleStatusEnum.name().equalsIgnoreCase(val)) {
        return ruleStatusEnum;
      }
    }

    throw new IllegalArgumentException("unknown type: " + val);
  }
}
