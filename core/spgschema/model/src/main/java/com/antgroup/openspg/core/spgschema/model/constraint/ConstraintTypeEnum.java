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

package com.antgroup.openspg.core.spgschema.model.constraint;

/** The enum class of constraint item. */
public enum ConstraintTypeEnum {
  /** nullable value constraint. */
  NOT_NULL(1),

  /** multi value constraint. */
  MULTI_VALUE(2),

  /** regular constraint. */
  REGULAR(3),

  /** enum values constraint. */
  ENUM(4),

  /** number values constraint */
  RANGE(5),

  /** unique value constraint. */
  UNIQUE(6),
  ;

  private final int priority;

  ConstraintTypeEnum(int priority) {
    this.priority = priority;
  }

  public static ConstraintTypeEnum toEnum(String value) {
    for (ConstraintTypeEnum item : ConstraintTypeEnum.values()) {
      if (item.name().equalsIgnoreCase(value)) {
        return item;
      }
    }
    throw new IllegalArgumentException("unknown type: " + value);
  }

  public int getPriority() {
    return priority;
  }
}
