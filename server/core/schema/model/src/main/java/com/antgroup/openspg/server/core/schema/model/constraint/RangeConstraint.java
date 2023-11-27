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

package com.antgroup.openspg.server.core.schema.model.constraint;

/**
 * Interval range constraints are applicable to numeric attributes, such as age, height and other
 * attributes. The constraints include conditions such as minimum value, maximum value, and interval
 * openness or closure.
 */
public class RangeConstraint extends BaseConstraintItem {

  private static final long serialVersionUID = 7399875532038028219L;

  /** The minimum value of number. */
  private String minimumValue;

  /** The maximum value of number */
  private String maximumValue;

  /** If the left of range is open. */
  private Boolean leftOpen;

  /** If the right of range is open */
  private Boolean rightOpen;

  public RangeConstraint() {}

  public RangeConstraint(String minValue, String maxValue, Boolean leftOpen, Boolean rightOpen) {
    this.minimumValue = minValue;
    this.maximumValue = maxValue;
    this.leftOpen = leftOpen;
    this.rightOpen = rightOpen;
  }

  @Override
  public ConstraintTypeEnum getConstraintTypeEnum() {
    return ConstraintTypeEnum.RANGE;
  }

  @Override
  public boolean checkIsLegal(Object value) {
    // todo
    return true;
  }

  public String getMinimumValue() {
    return minimumValue;
  }

  public void setMinimumValue(String minimumValue) {
    this.minimumValue = minimumValue;
  }

  public String getMaximumValue() {
    return maximumValue;
  }

  public void setMaximumValue(String maximumValue) {
    this.maximumValue = maximumValue;
  }

  public Boolean getLeftOpen() {
    return leftOpen;
  }

  public void setLeftOpen(Boolean leftOpen) {
    this.leftOpen = leftOpen;
  }

  public Boolean getRightOpen() {
    return rightOpen;
  }

  public void setRightOpen(Boolean rightOpen) {
    this.rightOpen = rightOpen;
  }
}
