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

import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.TuGraphConstants;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropPropertyOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.SchemaAtomicOperationEnum;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


public class AlterLabelDelFieldsProcedure extends BaseTuGraphProcedure {

    /**
     * The template of cypher
     */
    private static final String ALTER_LABEL_DEL_FIELDS_CYPHER_TEMPLATE
        = "CALL db.alterLabelDelFields('${labelType}', '${labelName}', ${fieldSpec});";

    private final String labelType;

    private final String labelName;

    private final String fieldSpec;

    private AlterLabelDelFieldsProcedure(String cypherTemplate, String labelType, String labelName, String fieldSpec) {
        super(cypherTemplate);
        this.labelType = labelType;
        this.labelName = labelName;
        this.fieldSpec = fieldSpec;
    }

    public static AlterLabelDelFieldsProcedure of(AlterEdgeTypeOperation alterEdgeTypeOperation) {
        List<DropPropertyOperation> deletePropertyOperations = alterEdgeTypeOperation.getAtomicOperations()
            .stream()
            .filter(atomicOperation ->
                SchemaAtomicOperationEnum.DROP_PROPERTY.equals(atomicOperation.getOperationTypeEnum()))
            .map(operation -> (DropPropertyOperation) operation)
            .collect(Collectors.toList());
        return CollectionUtils.isEmpty(deletePropertyOperations)
            ? new AlterLabelDelFieldsProcedure(
            TuGraphConstants.SCRIPT_NO_ALTER_TO_SCHEMA,
            TuGraphConstants.LABEL_TYPE_EDGE,
            alterEdgeTypeOperation.getEdgeTypeName().getEdgeLabel(),
            "[]")
            : new AlterLabelDelFieldsProcedure(
                ALTER_LABEL_DEL_FIELDS_CYPHER_TEMPLATE,
                TuGraphConstants.LABEL_TYPE_EDGE,
                alterEdgeTypeOperation.getEdgeTypeName().getEdgeLabel(),
                getFieldSpec(deletePropertyOperations)
            );
    }

    public static AlterLabelDelFieldsProcedure of(AlterVertexTypeOperation alterVertexTypeOperation) {
        List<DropPropertyOperation> deletePropertyOperations = alterVertexTypeOperation.getAtomicOperations()
            .stream()
            .filter(atomicOperation ->
                SchemaAtomicOperationEnum.DROP_PROPERTY.equals(atomicOperation.getOperationTypeEnum()))
            .map(atomicOperation -> (DropPropertyOperation) atomicOperation)
            .collect(Collectors.toList());
        return CollectionUtils.isEmpty(deletePropertyOperations)
            ? new AlterLabelDelFieldsProcedure(
            TuGraphConstants.SCRIPT_NO_ALTER_TO_SCHEMA,
            TuGraphConstants.LABEL_TYPE_VERTEX,
            alterVertexTypeOperation.getVertexTypeName(),
            getFieldSpec(deletePropertyOperations))
            : new AlterLabelDelFieldsProcedure(
                ALTER_LABEL_DEL_FIELDS_CYPHER_TEMPLATE,
                TuGraphConstants.LABEL_TYPE_VERTEX,
                alterVertexTypeOperation.getVertexTypeName(),
                getFieldSpec(deletePropertyOperations)
            );
    }

    private static String getFieldSpec(List<DropPropertyOperation> deletePropertyOperations) {
        List<String> propertyNames = deletePropertyOperations.stream()
            .map(operation -> "'" + operation.getPropertyName() + "'")
            .collect(Collectors.toList());
        return "[" + String.join(", ", propertyNames) + "]";
    }

    @Override
    public String toString() {
        return "{\"procedure\":\"AlterLabelDelFieldsProcedure\", "
            + "\"labelType\":\"" + labelType + "\", "
            + "\"labelName\":\"" + labelName + "\", "
            + "\"fieldSpec\":\"" + fieldSpec + "\", "
            + "\"cypherTemplate\":\"" + getCypherTemplate() + "\"}";
    }

}
