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

package com.antgroup.openspg.core.spgschema.model.semantic;

/**
 * Enumeration of predicate scopes
 */
public enum PredicateScopeEnum {

    /**
     * Defined on the concept.
     */
    CONCEPT,

    /**
     * Defined on the property.
     */
    PROPERTY,

    /**
     * Defined on the relation.
     */
    RELATION;

    public static PredicateScopeEnum toEnum(String val) {
        for (PredicateScopeEnum scopeEnum : PredicateScopeEnum.values()) {
            if (scopeEnum.name().equalsIgnoreCase(val)) {
                return scopeEnum;
            }
        }
        throw new IllegalArgumentException("unknown type: " + val);
    }
}
