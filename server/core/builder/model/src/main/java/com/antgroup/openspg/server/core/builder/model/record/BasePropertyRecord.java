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

package com.antgroup.openspg.server.core.builder.model.record;

import com.antgroup.openspg.server.common.model.base.BaseValObj;
import com.antgroup.openspg.server.core.schema.model.type.BasicTypeEnum;
import com.antgroup.openspg.server.core.schema.model.type.SPGTypeRef;

public abstract class BasePropertyRecord extends BaseValObj {

  protected final SPGPropertyValue value;

  public BasePropertyRecord(SPGPropertyValue value) {
    this.value = value;
  }

  public abstract String getName();

  public SPGPropertyValue getValue() {
    return value;
  }

  public abstract SPGTypeRef getObjectTypeRef();

  public abstract boolean isSemanticProperty();

  public void setStdValue() {
    Object rawValue = value.getStdOrRawValue();
    if (rawValue == null) {
      // if rawValue is null, this property value will be set to null.
      return;
    }
    Object stdValue = null;
    SPGTypeRef objectTypeRef = getObjectTypeRef();
    if (objectTypeRef.isBasicType()) {
      BasicTypeEnum basicType = BasicTypeEnum.from(objectTypeRef.getName());
      switch (basicType) {
        case LONG:
          stdValue = rawValue instanceof Long ? rawValue : Long.valueOf((String) rawValue);
          break;
        case DOUBLE:
          stdValue = rawValue instanceof Double ? rawValue : Double.valueOf((String) rawValue);
          break;
        default:
          stdValue = rawValue.toString();
          break;
      }
    } else {
      stdValue = rawValue.toString();
    }
    value.setStd(stdValue);
  }

  public void setIdsValue() {
    Object stdValue = value.getStd();
    SPGTypeRef objectTypeRef = getObjectTypeRef();
    if (value.getIds() == null && objectTypeRef.isAdvancedType()) {
      // 只有没有链指上时且属性类型是高级类型时走idEqual策略
      value.setIds(stdValue == null ? null : stdValue.toString());
    }
  }
}
