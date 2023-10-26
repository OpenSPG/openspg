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

package com.antgroup.openspg.cloudext.interfaces.graphstore.adapter.record.impl.convertor;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGPropertyRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.BasePropertyRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.SPGPropertyRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.SPGPropertyValue;
import com.antgroup.openspg.core.spgbuilder.model.record.SPGSubPropertyRecord;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.predicate.SubProperty;
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType;

import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Convertor for {@link LPGPropertyRecord} and {@link SPGPropertyRecord}.
 * </P>
 */
public class PropertyRecordConvertor {

    public static List<LPGPropertyRecord> toLPGProperties(List<? extends BasePropertyRecord> propertyRecords) {
        List<LPGPropertyRecord> resultProperties = new ArrayList<>(propertyRecords.size());

        for (BasePropertyRecord propertyRecord : propertyRecords) {
            resultProperties.add(new LPGPropertyRecord(
                propertyRecord.getName(), propertyRecord.getValue().getStd()
            ));
        }
        return resultProperties;
    }

    public static List<SPGPropertyRecord> toSPGProperties(Map<String, String> properties, BaseSPGType spgType) {
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

    public static List<SPGSubPropertyRecord> toSPGProperties(Map<String, String> properties, Property spgProperty) {
        if (!spgProperty.hasSubProperty()) {
            return new ArrayList<>(0);
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
