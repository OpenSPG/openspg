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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.enums;

public enum ConstraintEnum {

  /** 非空约束 */
  REQUIRE("Required", "值非空"),

  /** 唯一约束 */
  UNIQUE("Unique", "值唯一"),

  /** 枚举约束 */
  ENUM("Enum", "枚举"),

  /** 区间约束：大于 */
  MINIMUM_GT("Greater than", "大于"),

  /** 区间约束：大于等于 */
  MINIMUM_GT_OE("Greater than or equal", "大于等于"),

  /** 区间约束：小于 */
  MAXIMUM_LT("Less than", "小于"),

  /** 区间约束：小于等于 */
  MAXIMUM_LT_OE("Less than or equal", "小于等于"),

  /** 正则约束 */
  REGULAR("Regular match", "正则匹配"),

  /** 多值 */
  MULTIVALUE("Multi value", "多值"),

  /** number values constraint */
  RANGE("range", "区间");

  private String name;
  private String nameZh;

  ConstraintEnum(String name, String nameZh) {
    this.name = name;
    this.nameZh = nameZh;
  }

  /**
   * 获取约束枚举
   *
   * @param value
   * @return
   */
  public static ConstraintEnum getConstraint(String value) {
    for (ConstraintEnum constraintEnum : ConstraintEnum.values()) {
      if (constraintEnum.name().equalsIgnoreCase(value)) {
        return constraintEnum;
      }
    }

    throw new IllegalArgumentException("Unsupported constraint value:" + value);
  }

  public String getName() {
    return this.name;
  }

  public String getNameZh() {
    return this.nameZh;
  }
}
