/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.util;

import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyValue;
import com.antgroup.openspg.builder.model.record.property.SPGSubPropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGPropertyRecord;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.SubProperty;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;

/** Convertor for {@link LPGPropertyRecord} and {@link SPGPropertyRecord}. */
public class PropertyRecordConvertor {

  /** Convert the SPG record to an LPG record. */
  public static List<LPGPropertyRecord> toLPGProperties(
      List<? extends BasePropertyRecord> propertyRecords) {
    List<LPGPropertyRecord> resultProperties = new ArrayList<>(propertyRecords.size());

    for (BasePropertyRecord propertyRecord : propertyRecords) {
      Object value = null;
      if (propertyRecord.getObjectTypeRef().isBasicType()) {
        value = propertyRecord.getValue().getStds().get(0);
      } else {
        value = propertyRecord.getValue().getStdValue();
      }
      resultProperties.add(new LPGPropertyRecord(propertyRecord.getName(), value));
    }
    return resultProperties;
  }

  /** Convert the LPG record to an SPG record. */
  public static List<SPGPropertyRecord> toSPGProperties(
      Map<String, String> properties, BaseSPGType spgType) {
    if (MapUtils.isEmpty(properties)) {
      return new ArrayList<>(0);
    }
    Map<String, Property> propertyMap = spgType.getPropertyMap();

    List<SPGPropertyRecord> spgPropertyRecords = new ArrayList<>(properties.size());
    for (Property spgProperty : propertyMap.values()) {
      String propertyValue = properties.get(spgProperty.getName());
      if (propertyValue == null) {
        continue;
      }
      SPGPropertyValue spgPropertyValue = new SPGPropertyValue(propertyValue);
      spgPropertyRecords.add(new SPGPropertyRecord(spgProperty, spgPropertyValue));
    }
    return spgPropertyRecords;
  }

  /** Convert the LPG record to an SPG record. */
  public static List<SPGSubPropertyRecord> toSPGProperties(
      Map<String, String> properties, Property spgProperty) {
    if (!spgProperty.hasSubProperty()) {
      return new ArrayList<>(0);
    }
    if (properties == null) {
      properties = Collections.emptyMap();
    }
    Map<String, SubProperty> subPropertyMap = spgProperty.getSubPropertyMap();

    List<SPGSubPropertyRecord> spgPropertyRecords = new ArrayList<>(properties.size());
    for (SubProperty subProperty : subPropertyMap.values()) {
      String propertyValue = properties.get(subProperty.getName());
      if (propertyValue == null) {
        continue;
      }
      SPGPropertyValue spgPropertyValue = new SPGPropertyValue(propertyValue);
      spgPropertyRecords.add(new SPGSubPropertyRecord(subProperty, spgPropertyValue));
    }
    return spgPropertyRecords;
  }
}
