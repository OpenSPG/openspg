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

package com.antgroup.openspg.cloudext.interfaces.graphstore.util;

import com.antgroup.openspg.cloudext.interfaces.graphstore.LPGTypeNameConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.BaseLPGRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.LPGSchema;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.AlterVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.BaseLPGSchemaOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateVertexTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropEdgeTypeOperation;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.DropVertexTypeOperation;
import com.antgroup.openspg.common.model.exception.GraphStoreException;

import java.util.List;


public class TypeNameUtils {

    public static <T extends BaseLPGRecord> void convertTypeName(List<T> lpgRecords, LPGTypeNameConvertor convertor) {
        lpgRecords.forEach(record -> convertTypeName(record, convertor));
    }

    public static void convertTypeName(BaseLPGSchemaOperation schemaOperationRecord,
        LPGTypeNameConvertor convertor) {
        EdgeTypeName edgeTypeName;
        switch (schemaOperationRecord.getOperationTypeEnum()) {
            case CREATE_EDGE_TYPE:
                CreateEdgeTypeOperation createEdgeTypeOperation = (CreateEdgeTypeOperation) schemaOperationRecord;
                edgeTypeName = createEdgeTypeOperation.getEdgeTypeName();
                createEdgeTypeOperation.setEdgeTypeName(
                    new EdgeTypeName(
                        convertor.convertVertexTypeName(edgeTypeName.getStartVertexType()),
                        convertor.convertEdgeTypeName(edgeTypeName),
                        convertor.convertVertexTypeName(edgeTypeName.getEndVertexType())
                    )
                );
                break;
            case DROP_EDGE_TYPE:
                DropEdgeTypeOperation dropEdgeTypeOperation = (DropEdgeTypeOperation) schemaOperationRecord;
                edgeTypeName = dropEdgeTypeOperation.getEdgeTypeName();
                dropEdgeTypeOperation.setEdgeTypeName(
                    new EdgeTypeName(
                        convertor.convertVertexTypeName(edgeTypeName.getStartVertexType()),
                        convertor.convertEdgeTypeName(edgeTypeName),
                        convertor.convertVertexTypeName(edgeTypeName.getEndVertexType())
                    )
                );
                break;
            case ALTER_EDGE_TYPE:
                AlterEdgeTypeOperation alterEdgeTypeOperation = (AlterEdgeTypeOperation) schemaOperationRecord;
                edgeTypeName = alterEdgeTypeOperation.getEdgeTypeName();
                alterEdgeTypeOperation.setEdgeTypeName(
                    new EdgeTypeName(
                        convertor.convertVertexTypeName(edgeTypeName.getStartVertexType()),
                        convertor.convertEdgeTypeName(edgeTypeName),
                        convertor.convertVertexTypeName(edgeTypeName.getEndVertexType())
                    )
                );
                break;
            case ALTER_VERTEX_TYPE:
                AlterVertexTypeOperation alterVertexTypeOperation = (AlterVertexTypeOperation) schemaOperationRecord;
                alterVertexTypeOperation.setVertexTypeName(
                    convertor.convertVertexTypeName(alterVertexTypeOperation.getVertexTypeName())
                );
                break;
            case CREATE_VERTEX_TYPE:
                CreateVertexTypeOperation createVertexTypeOperation = (CreateVertexTypeOperation) schemaOperationRecord;
                createVertexTypeOperation.setVertexTypeName(
                    convertor.convertVertexTypeName(createVertexTypeOperation.getVertexTypeName())
                );
                break;
            case DROP_VERTEX_TYPE:
                DropVertexTypeOperation dropVertexTypeOperation = (DropVertexTypeOperation) schemaOperationRecord;
                dropVertexTypeOperation.setVertexTypeName(
                    convertor.convertVertexTypeName(dropVertexTypeOperation.getVertexTypeName())
                );
                break;
            default:
                throw GraphStoreException.unexpectedVertexEdgeTypeOperationEnum(
                    schemaOperationRecord.getOperationTypeEnum());
        }
    }

    public static void restoreTypeName(LPGSchema lpgSchema, LPGTypeNameConvertor convertor) {
        lpgSchema.getVertexTypes().forEach(vertexType ->
            vertexType.setVertexTypeName(convertor.restoreVertexTypeName(vertexType.getVertexTypeName()))
        );
        lpgSchema.getEdgeTypes().forEach(edgeType ->
            edgeType.setEdgeTypeName(convertor.restoreEdgeTypeName(edgeType.getEdgeTypeName().getEdgeLabel()))
        );
    }

    private static void convertTypeName(BaseLPGRecord lpgRecord, LPGTypeNameConvertor convertor) {
        switch (lpgRecord.getRecordType()) {
            case VERTEX:
                VertexRecord vertexRecord = (VertexRecord) lpgRecord;
                vertexRecord.setVertexType(convertor.convertVertexTypeName(vertexRecord.getVertexType()));
                break;
            case EDGE:
                EdgeRecord edgeRecord = (EdgeRecord) lpgRecord;
                edgeRecord.setEdgeType(new EdgeTypeName(
                    convertor.convertVertexTypeName(edgeRecord.getEdgeType().getStartVertexType()),
                    convertor.convertEdgeTypeName(edgeRecord.getEdgeType()),
                    convertor.convertVertexTypeName(edgeRecord.getEdgeType().getEndVertexType())
                ));
                break;
            default:
                throw GraphStoreException.unexpectedLPGRecordTypeEnum(lpgRecord.getRecordType());
        }
    }
}
