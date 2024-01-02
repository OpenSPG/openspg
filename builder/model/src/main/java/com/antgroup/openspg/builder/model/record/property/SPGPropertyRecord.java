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
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import com.antgroup.openspg.core.schema.model.type.WithSPGTypeEnum;
import lombok.Getter;

@Getter
public class SPGPropertyRecord extends BasePropertyRecord implements WithSPGTypeEnum {

  private final Property property;

  public SPGPropertyRecord(Property property, SPGPropertyValue value) {
    super(value);
    this.property = property;
  }

  public String getName() {
    return property.getName();
  }

  @Override
  public SPGTypeRef getObjectTypeRef() {
    return getProperty().getObjectTypeRef();
  }

  @Override
  public boolean isSemanticProperty() {
    return property.getObjectTypeRef().isAdvancedType();
  }

  @Override
  public Constraint getConstraint() {
    return property.getConstraint();
  }

  @Override
  public SPGTypeEnum getSpgTypeEnum() {
    return getProperty().getObjectTypeRef().getSpgTypeEnum();
  }

  public void setStdAndIdFromRaw() {}
}
