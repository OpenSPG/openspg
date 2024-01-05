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

import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;
import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.semantic.SystemPredicateEnum;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.schema.model.type.WithBasicInfo;
import com.antgroup.openspg.core.schema.model.type.WithSPGTypeEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class BaseAdvancedRecord extends BaseSPGRecord
    implements WithBasicInfo<SPGTypeIdentifier>, WithSPGTypeEnum {

  @Setter private List<RelationRecord> relationRecords;

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

  public List<SPGPropertyRecord> getSemanticPropertyRecords() {
    return getSpgProperties().stream()
        .filter(BasePropertyRecord::isSemanticProperty)
        .collect(Collectors.toList());
  }

  public void mergePropertyValue(SPGPropertyRecord otherRecord) {
    boolean find = false;
    for (SPGPropertyRecord existRecord : getSpgProperties()) {
      if (otherRecord.getProperty().equals(existRecord.getProperty())) {
        existRecord.getValue().merge(otherRecord.getValue());
        find = true;
      }
    }
    if (!find) {
      addSpgProperties(otherRecord);
    }
  }

  public void addRelationRecord(RelationRecord relationRecord) {
    if (relationRecords == null) {
      relationRecords = new ArrayList<>();
    }
    relationRecords.add(relationRecord);
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
      if (predicate.getName().equals(propertyRecord.getProperty().getName())) {
        return propertyRecord;
      }
    }
    return null;
  }
}
