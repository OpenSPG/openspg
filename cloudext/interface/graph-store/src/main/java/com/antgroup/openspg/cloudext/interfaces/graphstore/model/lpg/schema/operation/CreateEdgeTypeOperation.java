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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeType;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.LPGProperty;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Getter
public class CreateEdgeTypeOperation extends BaseCreateTypeOperation {

    @Setter
    private EdgeTypeName edgeTypeName;

    public CreateEdgeTypeOperation(EdgeTypeName edgeTypeName) {
        this(edgeTypeName, Lists.newArrayList());
    }

    public CreateEdgeTypeOperation(
        EdgeTypeName edgeTypeName, List<BaseSchemaAtomicOperation> atomicOperations) {
        super(VertexEdgeTypeOperationEnum.CREATE_EDGE_TYPE, atomicOperations);
        this.edgeTypeName = edgeTypeName;
    }

    @Override
    public void checkSchemaAtomicOperations() {
        Map<String, LPGProperty> addPropertyMap = atomicOperations
            .stream()
            .filter(operation ->
                SchemaAtomicOperationEnum.ADD_PROPERTY.equals(operation.getOperationTypeEnum()))
            .map(operation -> ((AddPropertyOperation) operation).getProperty())
            .collect(Collectors.toMap(LPGProperty::getName, Function.identity()));
        if (MapUtils.isEmpty(addPropertyMap)
            || addPropertyMap.get(EdgeType.SRC_ID) == null
            || addPropertyMap.get(EdgeType.DST_ID) == null
            || addPropertyMap.get(EdgeType.VERSION) == null) {
            throw new IllegalArgumentException(
                String.format("%s, %s and %s must in the property of edge,"
                        + "and now the property of edge(%s) is %s",
                    EdgeType.SRC_ID, EdgeType.DST_ID, EdgeType.VERSION,
                    getTargetTypeName(), addPropertyMap.keySet()
                )
            );
        }
        addPropertyMap.get(EdgeType.SRC_ID).setOptional(false);
        addPropertyMap.get(EdgeType.DST_ID).setOptional(false);
        addPropertyMap.get(EdgeType.VERSION).setOptional(false);
    }

    @Override
    public void addProperty(LPGProperty property) {
        atomicOperations.add(new AddPropertyOperation(property));
    }

    @Override
    public void createIndex(String propertyName) {
        createIndex(propertyName);
    }

    @Override
    public void createIndex(String propertyName, boolean isUnique) {
        atomicOperations.add(new CreateIndexOperation(propertyName, isUnique));
    }

    @Override
    public void createIndex(String propertyName, boolean isUnique, boolean isGlobal) {
        atomicOperations.add(new CreateIndexOperation(propertyName, isUnique, isGlobal));
    }

    @Override
    public void setTTL(String propertyName, long ts) {
        atomicOperations.add(new SetTtlOperation(propertyName, ts));
    }

    @Override
    public String getTargetTypeName() {
        return this.edgeTypeName.toString();
    }
}
