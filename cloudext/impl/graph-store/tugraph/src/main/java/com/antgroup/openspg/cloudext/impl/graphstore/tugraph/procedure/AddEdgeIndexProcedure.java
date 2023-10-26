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

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateIndexOperation;


public class AddEdgeIndexProcedure extends BaseTuGraphProcedure {

    /**
     * The template of cypher
     */
    public static final String ADD_EDGE_INDEX_CYPHER_TEMPLATE
        = "CALL db.addEdgeIndex('${labelName}', '${fieldName}', ${isUnique}, ${isGlobal})";

    private final String labelName;

    private final String fieldName;

    private final boolean isUnique;

    private final boolean isGlobal;

    private AddEdgeIndexProcedure(String cypherTemplate, String labelName, String fieldName,
        boolean isUnique, boolean isGlobal) {
        super(cypherTemplate);
        this.labelName = labelName;
        this.fieldName = fieldName;
        this.isUnique = isUnique;
        this.isGlobal = isGlobal;
    }

    public static AddEdgeIndexProcedure of(String labelName, CreateIndexOperation createIndexOperation) {
        return new AddEdgeIndexProcedure(
            ADD_EDGE_INDEX_CYPHER_TEMPLATE,
            labelName,
            createIndexOperation.getPropertyName(),
            createIndexOperation.getIsUnique(),
            createIndexOperation.getIsGlobal());
    }
}

