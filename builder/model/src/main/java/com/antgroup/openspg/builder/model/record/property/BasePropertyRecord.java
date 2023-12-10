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

import com.antgroup.openspg.core.schema.model.constraint.Constraint;
import com.antgroup.openspg.core.schema.model.constraint.ConstraintTypeEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import com.antgroup.openspg.server.common.model.base.BaseValObj;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public abstract class BasePropertyRecord extends BaseValObj {

  protected final SPGPropertyValue value;

  public BasePropertyRecord(SPGPropertyValue value) {
    this.value = value;
  }

  public abstract String getName();

  public abstract SPGTypeRef getObjectTypeRef();

  public abstract boolean isSemanticProperty();

  public abstract Constraint getConstraint();

  public List<String> getRawValues() {
    List<String> rawValues = null;
    Constraint constraint = getConstraint();
    String rawValue = value.getRaw();
    if (constraint != null && constraint.contains(ConstraintTypeEnum.MULTI_VALUE)) {
      rawValues = Arrays.stream(rawValue.split(",")).collect(Collectors.toList());
    } else {
      rawValues = new ArrayList<>(1);
      rawValues.add(rawValue);
    }
    return rawValues;
  }
}
