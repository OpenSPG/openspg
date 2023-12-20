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

package com.antgroup.openspg.builder.model.record.property;

import com.antgroup.openspg.server.common.model.base.BaseValObj;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@SuppressWarnings({"unchecked", "rawtypes"})
public class SPGPropertyValue extends BaseValObj {

  protected static final String SEPARATOR = ",";

  /** Raw property value */
  private String raw;

  /**
   * When the property type is not a basic type, this field points to the instance ID of the
   * property type. Since the property may have multiple values, it is defined here as a list.
   */
  private List<String> ids;

  /**
   * The normalized value of the property's raw value, since the property might have multiple
   * values, it is defined here as a list.
   */
  private List<Object> stds;

  public SPGPropertyValue(String raw) {
    this.raw = raw;
  }

  public String getStdValue() {
    return stds.stream().map(Object::toString).collect(Collectors.joining(SEPARATOR));
  }

  public void setSingleStd(Object std) {
    stds = new ArrayList<>(1);
    stds.add(std);
  }

  public void setStrStds(List<String> stds) {
    this.stds = (List) stds;
  }

  public void merge(SPGPropertyValue otherValue) {
    if (raw == null) {
      raw = otherValue.getRaw();
    } else {
      raw = raw + SEPARATOR + otherValue.getRaw();
    }
  }
}
