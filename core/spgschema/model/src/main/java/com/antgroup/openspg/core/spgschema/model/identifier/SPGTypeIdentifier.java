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

package com.antgroup.openspg.core.spgschema.model.identifier;

import java.util.Objects;


public class SPGTypeIdentifier extends BaseSPGIdentifier {

    private static final long serialVersionUID = 815110692255122360L;

    /**
     * The namespace of the schema type
     */
    private final String namespace;

    /**
     * The English name of the schema type
     */
    private final String nameEn;

    public SPGTypeIdentifier(String namespace, String nameEn) {
        super(SPGIdentifierTypeEnum.SPG_TYPE);
        this.namespace = namespace;
        this.nameEn = nameEn;
    }

    public static SPGTypeIdentifier parse(String uniqueName) {
        String[] splits = uniqueName.trim().split("\\.");
        if (splits.length == 1) {
            return new SPGTypeIdentifier(null, splits[0]);
        } else if (splits.length == 2) {
            return new SPGTypeIdentifier(splits[0], splits[1]);
        } else {
            throw new IllegalArgumentException("invalid uniqueName: " + uniqueName);
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public String getNameEn() {
        return nameEn;
    }

    @Override
    public String toString() {
        if (namespace == null) {
            return nameEn;
        }
        return String.format("%s.%s", namespace, nameEn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SPGTypeIdentifier)) {
            return false;
        }
        SPGTypeIdentifier that = (SPGTypeIdentifier) o;
        return Objects.equals(getNamespace(), that.getNamespace()) &&
            Objects.equals(getNameEn(), that.getNameEn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNamespace(), getNameEn());
    }
}
