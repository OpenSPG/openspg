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

package com.antgroup.openspg.builder.protocol;

import com.antgroup.openspg.server.schema.core.model.predicate.Property;
import com.antgroup.openspg.server.schema.core.model.type.SPGTypeEnum;
import com.antgroup.openspg.server.schema.core.model.type.SPGTypeRef;
import com.antgroup.openspg.server.schema.core.model.type.WithSPGTypeEnum;

public class SPGPropertyRecord extends BasePropertyRecord implements WithSPGTypeEnum {

  private final Property propertyType;

  public SPGPropertyRecord(Property propertyType, SPGPropertyValue value) {
    super(value);
    this.propertyType = propertyType;
  }

  public Property getPropertyType() {
    return propertyType;
  }

  public String getName() {
    return propertyType.getName();
  }

  public SPGPropertyValue getValue() {
    return value;
  }

  @Override
  public SPGTypeRef getObjectTypeRef() {
    return getPropertyType().getObjectTypeRef();
  }

  @Override
  public boolean isSemanticProperty() {
    return propertyType.getObjectTypeRef().isAdvancedType();
  }

  @Override
  public SPGTypeEnum getSpgTypeEnum() {
    return getPropertyType().getObjectTypeRef().getSpgTypeEnum();
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("SPGPropertyRecord{");
    sb.append("propertyType=").append(propertyType.getName());
    sb.append(", value=").append(value);
    sb.append('}');
    return sb.toString();
  }
}
