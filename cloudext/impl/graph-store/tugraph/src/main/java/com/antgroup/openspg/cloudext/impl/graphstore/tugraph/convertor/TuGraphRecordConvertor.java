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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.convertor;

import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.TuGraphConstants;
import com.antgroup.openspg.cloudext.interfaces.graphstore.LPGTypeNameConvertor;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGPropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.TableLPGRecordStruct;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeType;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.VertexType;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class TuGraphRecordConvertor {

    public static TableLPGRecordStruct toTable(String gqlResult, LPGTypeNameConvertor typeNameConvertor) {
        JSONArray queryResults = JSON.parseArray(gqlResult);

        List<String> columnNames = toColumnNames(queryResults);
        List<List<Object>> cells = toCells(columnNames, queryResults, typeNameConvertor);
        return new TableLPGRecordStruct(columnNames, cells);
    }

    private static List<String> toColumnNames(JSONArray queryResults) {
        if (queryResults == null || queryResults.isEmpty()) {
            return new ArrayList<>(0);
        }
        JSONObject object = (JSONObject) queryResults.get(0);
        Set<String> columnNames = object.keySet();
        if (CollectionUtils.isEmpty(columnNames)) {
            return new ArrayList<>(0);
        }
        return new ArrayList<>(columnNames);
    }

    private static List<List<Object>> toCells(
        List<String> columnNames,
        JSONArray queryResults,
        LPGTypeNameConvertor typeNameConvertor) {
        if (CollectionUtils.isEmpty(columnNames)) {
            return new ArrayList<>(0);
        }

        List<List<Object>> resultTables = new ArrayList<>(queryResults.size());
        for (Object result : queryResults) {
            JSONObject object = (JSONObject) result;
            if (MapUtils.isEmpty(object)) {
                continue;
            }

            List<Object> row = new ArrayList<>(columnNames.size());
            for (String columnName : columnNames) {
                Object columnValue = object.get(columnName);
                row.add(toColumnValue(columnValue, typeNameConvertor));
            }
            resultTables.add(row);
        }
        return resultTables;
    }

    private static Object toColumnValue(Object object, LPGTypeNameConvertor typeNameConvertor) {
        if (object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            if (isEdge(jsonObject)) {
                return toEdgeRecord(jsonObject, typeNameConvertor);
            } else if (isVertex(jsonObject)) {
                return toVertexRecord(jsonObject, typeNameConvertor);
            } else {
                throw new RuntimeException("tugraph convert value error, object=" + object.toString());
            }
        } else {
            return object;
        }
    }

    private static boolean isEdge(JSONObject obj) {
        return obj.containsKey(TuGraphConstants.KEY_SRC) && obj.containsKey(TuGraphConstants.KEY_DST);
    }

    private static boolean isVertex(JSONObject obj) {
        return !isEdge(obj) && obj.containsKey(TuGraphConstants.KEY_INNER_ID);
    }

    private static EdgeRecord toEdgeRecord(JSONObject jsonObject, LPGTypeNameConvertor typeNameConvertor) {
        EdgeTypeName edgeName = typeNameConvertor
            .restoreEdgeTypeName(jsonObject.getString(TuGraphConstants.KEY_LABEL));
        JSONObject properties = jsonObject.getJSONObject(TuGraphConstants.KEY_PROPERTIES);
        String srcId = properties.getString(EdgeType.SRC_ID);
        String dstId = properties.getString(EdgeType.DST_ID);
        Long version = properties.getLong(EdgeType.VERSION);

        List<LPGPropertyRecord> lpgPropertyRecords = new ArrayList<>(properties.size());
        for (String key : properties.keySet()) {
            /** srcId, dstId, version, and edge internal id are not shown in {@link EdgeRecord#properties} **/
            if (!EdgeType.SRC_ID.equals(key) && !EdgeType.DST_ID.equals(key)
                && !EdgeType.VERSION.equals(key) && !TuGraphConstants.TUGRAPH_EDGE_INTERNAL_ID.equals(key)) {
                Object value = properties.get(key);
                if (value != null) {
                    lpgPropertyRecords.add(new LPGPropertyRecord(key, value));
                }
            }
        }
        return new EdgeRecord(srcId, dstId, edgeName, lpgPropertyRecords, version);
    }

    private static VertexRecord toVertexRecord(JSONObject jsonObject, LPGTypeNameConvertor typeNameConvertor) {
        String type = typeNameConvertor.restoreVertexTypeName(jsonObject.getString(TuGraphConstants.KEY_LABEL));
        JSONObject properties = jsonObject.getJSONObject(TuGraphConstants.KEY_PROPERTIES);
        String id = properties.getString(VertexType.ID);

        List<LPGPropertyRecord> lpgPropertyRecords = new ArrayList<>(properties.size());
        for (String key : properties.keySet()) {
            if (!VertexType.ID.equals(key)) {
                Object value = properties.get(key);
                if (value != null) {
                    lpgPropertyRecords.add(new LPGPropertyRecord(key, value));
                }
            }
        }
        return new VertexRecord(id, type, lpgPropertyRecords);
    }

    public static Map<String, Object> toUpsertTuGraphVertices(
        String vertexType, List<VertexRecord> vertexRecords) {
        if (CollectionUtils.isEmpty(vertexRecords)) {
            return Collections.emptyMap();
        }

        Map<String, Object> params = Maps.newHashMap();
        params.put(TuGraphConstants.KEY_TYPE, vertexType);
        params.put(TuGraphConstants.KEY_KEY, VertexType.ID);

        List<Map<String, Object>> recordParams = vertexRecords.stream()
            .filter(Objects::nonNull)
            .map(vertexRecord -> {
                Map<String, Object> node = new HashMap<>();
                node.put(VertexType.ID, vertexRecord.getId());
                node.put(TuGraphConstants.KEY_PROPERTIES, vertexRecord.toPropertyMap());
                return node;
            })
            .collect(Collectors.toList());
        params.put(TuGraphConstants.KEY_NODES, recordParams);
        return params;
    }

    public static Map<String, Object> toUpsertTuGraphEdges(EdgeTypeName edgeType, List<EdgeRecord> edgeRecords) {
        if (CollectionUtils.isEmpty(edgeRecords)) {
            return Collections.emptyMap();
        }

        Map<String, Object> params = Maps.newHashMap();
        params.put(TuGraphConstants.KEY_TYPE, edgeType.getEdgeLabel());
        params.put(TuGraphConstants.KEY_SRC_TYPE, edgeType.getStartVertexType());
        params.put(TuGraphConstants.KEY_SRC_KEY, VertexType.ID);
        params.put(TuGraphConstants.KEY_DST_TYPE, edgeType.getEndVertexType());
        params.put(TuGraphConstants.KEY_DST_KEY, VertexType.ID);

        List<Map<String, Object>> edgeRecordList = edgeRecords.stream()
            .filter(Objects::nonNull)
            .map(edgeRecord -> {
                Map<String, Object> edgeJson = new HashMap<>();
                edgeJson.put(EdgeType.SRC_ID, edgeRecord.getSrcId());
                edgeJson.put(EdgeType.DST_ID, edgeRecord.getDstId());
                edgeJson.put(EdgeType.VERSION, edgeRecord.getVersion());
                edgeJson.put(VertexType.ID, -1);

                // 这里需要将srcId与dstId作为属性存在边上
                Map<String, Object> edgeProperties = edgeRecord.toPropertyMap();
                edgeProperties.put(EdgeType.SRC_ID, edgeRecord.getSrcId());
                edgeProperties.put(EdgeType.DST_ID, edgeRecord.getDstId());
                edgeProperties.put(EdgeType.VERSION, edgeRecord.getVersion());
                edgeJson.put(TuGraphConstants.KEY_PROPERTIES, edgeProperties);
                return edgeJson;
            })
            .collect(Collectors.toList());
        params.put(TuGraphConstants.KEY_EDGES, edgeRecordList);
        return params;
    }
}
