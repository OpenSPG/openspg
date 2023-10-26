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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Procedure of deleting vertices.
 */
public class DeleteVerticesProcedure extends BaseTuGraphProcedure {

    /**
     * Cypher template
     */
    private static final String DELETE_VERTICES_CYPHER_TEMPLATE = "MATCH (n:${vertexType}) ${vertexFilter} DELETE n";

    /**
     * Type of vertex
     */
    private final String vertexType;

    /**
     * Filter of vertex
     */
    private final String vertexFilter;

    /**
     * Constructor.
     */
    private DeleteVerticesProcedure(String cypher, String vertexType, String vertexFilter) {
        super(cypher);
        this.vertexType = vertexType;
        this.vertexFilter = vertexFilter;
    }

    /**
     * DeleteVerticesProcedure of vertices.
     */
    public static DeleteVerticesProcedure of(Map.Entry<String, List<VertexRecord>> vertexMap) {
        String vertexType = vertexMap.getKey();

        List<String> vertexIds = vertexMap.getValue().stream()
            .filter(Objects::nonNull)
            .map(VertexRecord::getId)
            .collect(Collectors.toList());
        return new DeleteVerticesProcedure(
            DELETE_VERTICES_CYPHER_TEMPLATE,
            vertexType,
            "WHERE n.id IN " + "['" + String.join("','", vertexIds) + "']"
        );
    }


    /**
     * DeleteVerticesProcedure of vertices.
     */
    public static DeleteVerticesProcedure of(String vertexLabel, List<VertexRecord> vertexRecords) {
        List<String> vertexIds = vertexRecords.stream()
            .filter(Objects::nonNull)
            .map(VertexRecord::getId)
            .collect(Collectors.toList());

        return new DeleteVerticesProcedure(
            DELETE_VERTICES_CYPHER_TEMPLATE,
            vertexLabel,
            "WHERE n.id IN " + "['" + String.join("','", vertexIds) + "']"
        );
    }

    @Override
    public String toString() {
        return "{\"procedure\":\"DeleteVerticesProcedure\", "
            + "\"vertexType\":\"" + vertexType + "\", "
            + "\"vertexFilter\":\"" + vertexFilter + "\", "
            + "\"cypherTemplate\":\"" + getCypherTemplate() + "\"}";
    }

}
