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

package com.antgroup.openspg.core.spgschema.model;

import com.antgroup.openspg.common.model.exception.OpenSPGException;

import org.apache.commons.lang3.StringUtils;


public class SchemaException extends OpenSPGException {

    private SchemaException(Throwable cause, String messagePattern, Object... args) {
        super(cause, true, true, messagePattern, args);
    }

    private SchemaException(String messagePattern, Object... args) {
        this(null, messagePattern, args);
    }

    public static SchemaException alterError(Throwable cause) {
        return new SchemaException(cause, StringUtils.isBlank(cause.getMessage())
            ? "alter schema unknown error" : cause.getMessage());
    }

    public static SchemaException queryError(Throwable cause) {
        return new SchemaException(cause, StringUtils.isBlank(cause.getMessage())
            ? "query schema unknown error" : cause.getMessage());
    }

    public static SchemaException uniqueIdNotExist(Long uniqueId) {
        return new SchemaException("there is no spg type with uniqueId={}", uniqueId);
    }

    public static SchemaException spgTypeAlreadyExists(String spgTypeName) {
        return new SchemaException("exist spg type with name={}", spgTypeName);
    }

    public static SchemaException spgTypeNotExist(String spgTypeName) {
        return new SchemaException("there is no spg type with name={}", spgTypeName);
    }

    public static SchemaException propertyNotExist(String propertyName) {
        return new SchemaException("there is no property with name={}", propertyName);
    }

    public static SchemaException relationNotExist(String relationName) {
        return new SchemaException("there is no relation with name={}", relationName);
    }
}
