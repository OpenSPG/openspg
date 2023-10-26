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

package com.antgroup.openspg.cloudext.interfaces.graphstore;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeType;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.LPGSchema;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.VertexType;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.BaseLPGSchemaOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropVertexTypeOperation;

import java.util.List;

/**
 * Provides data definition service for <tt>LPG</tt>.
 */
public interface LPGDataDefinitionService {

    /**
     * Query schema information (which lists <strong>ALL</strong> {@link VertexType VertexType}s,
     * and {@link EdgeType EdgeType}s) defined in <tt>LPG</tt>.
     *
     * @return {@link LPGSchema} with a list of {@link VertexType VertexType}
     *  and a list of {@link EdgeType EdgeType}
     */
    LPGSchema querySchema();

    /**
     * Create a new {@link VertexType VertexType}.
     *
     * @param operation operation to create {@link VertexType VertexType}.
     * @return <code>true</code> if successfully, <code>false</code> otherwise.
     */
    boolean createVertexType(CreateVertexTypeOperation operation);

    /**
     * Create a new {@link EdgeType EdgeType}.
     *
     * @param operation operation to create {@link EdgeType EdgeType}.
     * @return <code>true</code> if successfully, <code>false</code> otherwise.
     */
    boolean createEdgeType(CreateEdgeTypeOperation operation);

    /**
     * Alter a new {@link VertexType VertexType}.
     *
     * @param operation operation to alter {@link VertexType VertexType}.
     * @return <code>true</code> if successfully, <code>false</code> otherwise.
     */
    boolean alterVertexType(AlterVertexTypeOperation operation);

    /**
     * Alter a new {@link EdgeType EdgeType}.
     *
     * @param operation operation to alter {@link EdgeType EdgeType}.
     * @return <code>true</code> if successfully, <code>false</code> otherwise.
     */
    boolean alterEdgeType(AlterEdgeTypeOperation operation);

    /**
     * Drop a new {@link VertexType VertexType}.
     *
     * @param operation operation to drop {@link VertexType VertexType}.
     * @return <code>true</code> if successfully, <code>false</code> otherwise.
     */
    boolean dropVertexType(DropVertexTypeOperation operation);

    /**
     * Drop a new {@link EdgeType EdgeType}.
     *
     * @param operation operation to drop {@link EdgeType EdgeType}.
     * @return <code>true</code> if successfully, <code>false</code> otherwise.
     */
    boolean dropEdgeType(DropEdgeTypeOperation operation);

    boolean batchTransactionalSchemaOperations(List<BaseLPGSchemaOperation> operations);

}