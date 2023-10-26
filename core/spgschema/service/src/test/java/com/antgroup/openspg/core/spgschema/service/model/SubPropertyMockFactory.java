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

package com.antgroup.openspg.core.spgschema.service.model;

import com.antgroup.openspg.core.spgschema.model.BasicInfo;
import com.antgroup.openspg.core.spgschema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.predicate.PropertyRef;
import com.antgroup.openspg.core.spgschema.model.predicate.SubProperty;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeRef;

import com.google.common.collect.Lists;

import java.util.List;


public class SubPropertyMockFactory {

    public static List<SubProperty> mock(PropertyRef propertyRef) {
        return Lists.newArrayList(
            mockText(propertyRef),
            mockInteger(propertyRef),
            mockFloat(propertyRef)
        );
    }

    public static SubProperty mockSingle(String objectType, PropertyRef propertyRef) {
        BasicInfo<PredicateIdentifier> basicInfo = new BasicInfo<>(
            new PredicateIdentifier("sub_proper" + objectType), "子属性", "desc");
        SPGTypeRef objectTypeRef = new SPGTypeRef(new BasicInfo<>(
            SPGTypeIdentifier.parse(objectType), "name", "desc"), SPGTypeEnum.BASIC_TYPE);
        return new SubProperty(basicInfo, propertyRef, objectTypeRef, null);
    }

    public static SubProperty mockText(PropertyRef propertyRef) {
        return mockSingle("Text", propertyRef);
    }

    public static SubProperty mockInteger(PropertyRef propertyRef) {
        return mockSingle("Integer", propertyRef);
    }

    public static SubProperty mockFloat(PropertyRef propertyRef) {
        return mockSingle("Float", propertyRef);
    }
}
