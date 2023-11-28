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

package com.antgroup.openspg.schema.model.constraint;

import com.antgroup.openspg.common.model.base.BaseToString;

/**
 * Abstract class of constraint item, describes one kind of condition that property value should be
 * meet, the implemented class has such as {@link EnumConstraint}, {@link UniqueConstraint}, {@link
 * MultiValConstraint}, {@link NotNullConstraint}, {@link RangeConstraint}, {@link
 * RegularConstraint} etc.
 */
public abstract class BaseConstraintItem extends BaseToString
    implements Comparable<BaseConstraintItem> {

  private static final long serialVersionUID = 2504741600147528229L;

  /**
   * Get constraint type enum of subclass.
   *
   * @return constraint type
   */
  public abstract ConstraintTypeEnum getConstraintTypeEnum();

  /**
   * Check whether the value meets the constraints
   *
   * @param value value
   * @return Whether the constraints are met
   */
  public abstract boolean checkIsLegal(Object value);

  @Override
  public int compareTo(BaseConstraintItem o) {
    return Integer.compare(
        this.getConstraintTypeEnum().getPriority(), o.getConstraintTypeEnum().getPriority());
  }
}
