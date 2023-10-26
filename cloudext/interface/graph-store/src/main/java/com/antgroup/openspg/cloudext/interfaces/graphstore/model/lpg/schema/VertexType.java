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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents type of vertex in <tt>LPG</tt>, and is distinguished by
 * {@link VertexType#vertexTypeName, vertexTypeName}.
 */
@Getter
public class VertexType extends BaseLPGOntology {

    public final static String ID = "id";

    @Setter
    private String vertexTypeName;

    public VertexType(String vertexTypeName, List<LPGProperty> properties) {
        this(vertexTypeName, properties.stream().collect(
            Collectors.toMap(
                LPGProperty::getName,
                Function.identity()
            ))
        );
    }

    public VertexType(String vertexTypeName, Map<String, LPGProperty> properties) {
        super(LPGOntologyTypeEnum.VERTEX, properties);
        this.vertexTypeName = vertexTypeName;
    }

    @Override
    public String getTypeName() {
        return vertexTypeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VertexType)) {
            return false;
        }
        VertexType that = (VertexType) o;
        return Objects.equals(getVertexTypeName(), that.getVertexTypeName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVertexTypeName());
    }
}
