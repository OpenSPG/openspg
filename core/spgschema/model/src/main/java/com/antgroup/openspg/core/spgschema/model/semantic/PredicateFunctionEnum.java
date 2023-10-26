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
 * Enumeration of predicate functions
 */
public enum PredicateFunctionEnum {
    /**
     * Concept hierarchy, used between concept types
     */
    HYPERNYM("hypernym"),

    /**
     * Entity classification, such as belongTo
     */
    TAXONOMIC("taxonomic"),

    /**
     * Causal correlation, such as leadTo
     */
    REASONER("reasoner"),

    /**
     * Semantic, used to describe semantics between ordinary properties or relations.
     */
    SEMANTIC("semantic");

    /**
     * Function name
     */
    private final String name;

    PredicateFunctionEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PredicateFunctionEnum toEnum(String val) {
        for (PredicateFunctionEnum functionEnum : PredicateFunctionEnum.values()) {
            if (functionEnum.name().equalsIgnoreCase(val)) {
                return functionEnum;
            }
        }
        throw new IllegalArgumentException("unknown type: " + val);
    }
}
