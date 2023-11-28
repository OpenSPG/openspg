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

package com.antgroup.openspg.builder.model.record;

import com.antgroup.openspg.schema.model.BasicInfo;
import com.antgroup.openspg.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.schema.model.predicate.Property;
import com.antgroup.openspg.schema.model.semantic.SystemPredicateEnum;
import com.antgroup.openspg.schema.model.type.BaseSPGType;
import com.antgroup.openspg.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.schema.model.type.WithBasicInfo;
import com.antgroup.openspg.schema.model.type.WithSPGTypeEnum;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseAdvancedRecord extends BaseSPGRecord
    implements WithBasicInfo<SPGTypeIdentifier>, WithSPGTypeEnum {

  public BaseAdvancedRecord(SPGRecordTypeEnum recordType) {
    super(recordType);
  }

  public abstract BaseSPGType getSpgType();

  public abstract String getId();

  public abstract List<SPGPropertyRecord> getSpgProperties();

  public abstract void addSpgProperties(SPGPropertyRecord record);

  @Override
  public SPGTypeEnum getSpgTypeEnum() {
    return getSpgType().getSpgTypeEnum();
  }

  @Override
  public BasicInfo<SPGTypeIdentifier> getBasicInfo() {
    return getSpgType().getBasicInfo();
  }

  public SPGPropertyRecord getPropertyRecord(Property property) {
    for (SPGPropertyRecord record : getSpgProperties()) {
      if (record.getPropertyType().getName().equals(property.getName())) {
        return record;
      }
    }
    return null;
  }

  /** 获取语义属性的记录 */
  public List<SPGPropertyRecord> getSemanticPropertyRecords() {
    return getSpgProperties().stream()
        .filter(BasePropertyRecord::isSemanticProperty)
        .collect(Collectors.toList());
  }

  public void mergePropertyValue(SPGPropertyRecord otherRecord) {
    boolean find = false;
    for (SPGPropertyRecord existRecord : getSpgProperties()) {
      if (otherRecord.getPropertyType().equals(existRecord.getPropertyType())) {
        existRecord.getValue().merge(otherRecord.getValue());
        find = true;
      }
    }
    if (!find) {
      addSpgProperties(otherRecord);
    }
  }

  @Override
  public String toString() {
    String spgType = getSpgType().getName();
    String id = getId();
    Map<String, String> properties = getRawPropertyValueMap();
    return String.format("%s{id=%s, properties=%s}", spgType, id, properties);
  }

  public SPGPropertyRecord getPredicateProperty(SystemPredicateEnum predicate) {
    for (SPGPropertyRecord propertyRecord : getSpgProperties()) {
      if (predicate.getName().equals(propertyRecord.getPropertyType().getName())) {
        return propertyRecord;
      }
    }
    return null;
  }
}
