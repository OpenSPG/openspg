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
 * <p>
 * {@link EdgeType EdgeType} represents an <strong>unidirectional</strong> relationship which is between two
 * {@link VertexType VertexType}s or between one {@link VertexType VertexType} and itself. Between two
 * {@link VertexType VertexType}s (or between one {@link VertexType VertexType} and itself), multiple
 * {@link EdgeType EdgeType}s can be defined, which are distinguished by
 * {@link EdgeTypeName#getEdgeLabel() edgeLabels}.
 * </P>
 */
@Getter
public class EdgeType extends BaseLPGOntology {

    public final static String SRC_ID = "srcId";
    public final static String DST_ID = "dstId";
    public final static String VERSION = "version";

    @Setter
    private EdgeTypeName edgeTypeName;

    public EdgeType(EdgeTypeName edgeTypeName, List<LPGProperty> properties) {
        this(edgeTypeName, properties.stream().collect(
            Collectors.toMap(
                LPGProperty::getName,
                Function.identity()
            ))
        );
    }

    public EdgeType(EdgeTypeName edgeTypeName, Map<String, LPGProperty> properties) {
        super(LPGOntologyTypeEnum.EDGE, properties);
        this.edgeTypeName = edgeTypeName;
    }

    @Override
    public String getTypeName() {
        return getEdgeTypeName().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EdgeType)) {
            return false;
        }
        EdgeType edgeType = (EdgeType) o;
        return Objects.equals(getEdgeTypeName(), edgeType.getEdgeTypeName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEdgeTypeName());
    }
}
