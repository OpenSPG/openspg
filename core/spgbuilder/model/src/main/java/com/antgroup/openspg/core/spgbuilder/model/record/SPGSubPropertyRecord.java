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

package com.antgroup.openspg.core.spgbuilder.model.record;

import com.antgroup.openspg.core.spgschema.model.predicate.SubProperty;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeRef;
import com.antgroup.openspg.core.spgschema.model.type.WithSPGTypeEnum;


public class SPGSubPropertyRecord extends BasePropertyRecord
    implements WithSPGTypeEnum {

    private final SubProperty subPropertyType;

    public SPGSubPropertyRecord(
        SubProperty subPropertyType,
        SPGPropertyValue value) {
        super(value);
        this.subPropertyType = subPropertyType;
        if (!subPropertyType.getObjectTypeRef().isBasicType()) {
            throw new IllegalStateException("object of subPropertyType must be basicType");
        }
    }

    public SubProperty getSubPropertyType() {
        return subPropertyType;
    }

    @Override
    public String getName() {
        return subPropertyType.getName();
    }

    public SPGPropertyValue getValue() {
        return value;
    }

    @Override
    public SPGTypeRef getObjectTypeRef() {
        return getSubPropertyType().getObjectTypeRef();
    }

    @Override
    public boolean isSemanticProperty() {
        return false;
    }

    @Override
    public SPGTypeEnum getSpgTypeEnum() {
        return getSubPropertyType().getObjectTypeRef().getSpgTypeEnum();
    }
}
