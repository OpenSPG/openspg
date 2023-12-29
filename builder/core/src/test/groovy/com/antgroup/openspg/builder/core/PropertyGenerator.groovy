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

package com.antgroup.openspg.builder.core

import com.antgroup.openspg.core.schema.model.BasicInfo
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier
import com.antgroup.openspg.core.schema.model.predicate.Property
import com.antgroup.openspg.core.schema.model.predicate.PropertyAdvancedConfig
import com.antgroup.openspg.core.schema.model.predicate.SubProperty
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef

class PropertyGenerator {

    static def getProperty(String propertyName = 'aPropertyName',
                           SPGTypeEnum sType = SPGTypeEnum.ENTITY_TYPE,
                           String sName = 'RiskMining.App',
                           SPGTypeEnum oType = SPGTypeEnum.BASIC_TYPE, String oName = "Text") {
        return new Property(
                new BasicInfo<PredicateIdentifier>(new PredicateIdentifier(propertyName)),
                new SPGTypeRef(
                        new BasicInfo<>(SPGTypeIdentifier.parse(sName)),
                        sType
                ),
                new SPGTypeRef(
                        new BasicInfo<SPGTypeIdentifier>(SPGTypeIdentifier.parse(oName)),
                        oType
                ),
                Boolean.FALSE,
                new PropertyAdvancedConfig()
        )
    }

    static def getSubProperty(String subPropertyName = 'aSubPropertyName',
                              Property property = getProperty(),
                              SPGTypeEnum oType = SPGTypeEnum.BASIC_TYPE,
                              String oName = 'Text') {
        return new SubProperty(
                new BasicInfo<PredicateIdentifier>(new PredicateIdentifier(subPropertyName)),
                property.toRef(),
                new SPGTypeRef(
                        new BasicInfo<>(SPGTypeIdentifier.parse(oName)),
                        oType
                ),
                new PropertyAdvancedConfig()
        )
    }
}
