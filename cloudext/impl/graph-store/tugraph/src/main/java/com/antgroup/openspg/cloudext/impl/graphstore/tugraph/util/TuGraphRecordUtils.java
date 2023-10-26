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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.util;

import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.convertor.TuGraphRecordConvertor;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure.DeleteEdgesProcedure;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure.DeleteVerticesProcedure;
import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure.ExtendedProcedure;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;

import com.alibaba.fastjson.JSON;
import com.antgroup.tugraph.TuGraphDbRpcClient;
import lgraph.Lgraph;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;


public class TuGraphRecordUtils {

    public static void upsertVertexRecords(List<VertexRecord> vertexRecords,
        TuGraphDbRpcClient client, String graphName, Double timeout) throws Exception {

        if (CollectionUtils.isEmpty(vertexRecords)) {
            return;
        }

        String vertexTypeName = vertexRecords.get(0).getVertexType();

        Map<String, Object> params = TuGraphRecordConvertor.toUpsertTuGraphVertices(
            vertexTypeName, vertexRecords);
        String cypher = ExtendedProcedure
            .of(Lgraph.PluginRequest.PluginType.CPP, "upsertVertices", JSON.toJSONString(params))
            .getCypher();
        client.callCypher(cypher, graphName, timeout);
    }

    public static void deleteVertexRecords(List<VertexRecord> vertexRecords,
        TuGraphDbRpcClient client, String graphName, Double timeout) throws Exception {

        if (CollectionUtils.isEmpty(vertexRecords)) {
            return;
        }

        String vertexTypeName = vertexRecords.get(0).getVertexType();

        String cypher = DeleteVerticesProcedure.of(vertexTypeName, vertexRecords).getCypher();
        client.callCypher(cypher, graphName, timeout);
    }

    public static void upsertEdgeRecords(List<EdgeRecord> edgeRecords,
        TuGraphDbRpcClient client, String graphName, Double timeout) throws Exception {
        if (CollectionUtils.isEmpty(edgeRecords)) {
            return;
        }

        EdgeTypeName edgeTypeName = edgeRecords.get(0).getEdgeType();

        Map<String, Object> params = TuGraphRecordConvertor.toUpsertTuGraphEdges(edgeTypeName, edgeRecords);
        String cypher = ExtendedProcedure
            .of(Lgraph.PluginRequest.PluginType.CPP, "upsertEdges", JSON.toJSONString(params))
            .getCypher();
        client.callCypher(cypher, graphName, timeout);
    }

    public static void deleteEdgeRecords(List<EdgeRecord> edgeRecords,
        TuGraphDbRpcClient client, String graphName, Double timeout) throws Exception {
        if (CollectionUtils.isEmpty(edgeRecords)) {
            return;
        }

        EdgeTypeName edgeTypeName = edgeRecords.get(0).getEdgeType();

        String cypher = DeleteEdgesProcedure.of(edgeTypeName, edgeRecords).getCypher();
        client.callCypher(cypher, graphName, timeout);
    }
}
