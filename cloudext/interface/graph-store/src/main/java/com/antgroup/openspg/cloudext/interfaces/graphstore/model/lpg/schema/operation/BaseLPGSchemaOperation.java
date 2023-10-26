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

import lombok.Getter;

import java.util.List;


@Getter
public abstract class BaseLPGSchemaOperation {

    private long gmtTimestamp = System.currentTimeMillis();

    private String operator;

    protected final VertexEdgeTypeOperationEnum operationTypeEnum;

    protected final List<BaseSchemaAtomicOperation> atomicOperations;

    protected BaseLPGSchemaOperation(
        VertexEdgeTypeOperationEnum operationTypeEnum,
        List<BaseSchemaAtomicOperation> atomicOperations) {
        this.operationTypeEnum = operationTypeEnum;
        this.atomicOperations = atomicOperations;
    }

    public abstract void checkSchemaAtomicOperations();

    public abstract String getTargetTypeName();

    public void setGmtTimestamp(long gmtTimestamp) {
        this.gmtTimestamp = gmtTimestamp;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
