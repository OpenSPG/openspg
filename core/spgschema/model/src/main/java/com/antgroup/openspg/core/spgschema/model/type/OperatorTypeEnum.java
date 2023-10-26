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

package com.antgroup.openspg.core.spgschema.model.type;

/**
 * Enumeration of operator types.
 */
public enum OperatorTypeEnum {

    /**
     * The operator is used to extract knowledge.
     */
    KNOWLEDGE_EXTRACT,

    /**
     * The operator is used to find entity in graph.
     */
    ENTITY_LINK,

    /**
     * The operator is used to normalize property value.
     */
    PROPERTY_NORMALIZE,

    /**
     * The operator is used to fuse multi entity into one entity.
     */
    ENTITY_FUSE;

    /**
     * Get enum item that mapping to the val.
     *
     * @param val
     * @return
     */
    public static OperatorTypeEnum toEnum(String val) {
        for (OperatorTypeEnum operatorType : OperatorTypeEnum.values()) {
            if (operatorType.name().equalsIgnoreCase(val)) {
                return operatorType;
            }
        }
        throw new IllegalArgumentException("unknown type: " + val);
    }
}
