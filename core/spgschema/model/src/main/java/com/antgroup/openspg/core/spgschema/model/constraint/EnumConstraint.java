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

package com.antgroup.openspg.core.spgschema.model.constraint;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Enumeration constraints, including a list of enum values.
 */
public class EnumConstraint extends BaseConstraintItem {

    private static final long serialVersionUID = -1612364893947138991L;

    /**
     * List of enum value.
     */
    private final List<String> enumValues;

    public EnumConstraint(List<String> enumValues) {
        this.enumValues = enumValues;
    }

    @Override
    public ConstraintTypeEnum getConstraintTypeEnum() {
        return ConstraintTypeEnum.ENUM;
    }

    @Override
    public boolean checkIsLegal(Object value) {
        if (value == null || CollectionUtils.isEmpty(enumValues)) {
            return true;
        }
        return enumValues.contains(value.toString());
    }

    public List<String> getEnumValues() {
        return enumValues;
    }
}
