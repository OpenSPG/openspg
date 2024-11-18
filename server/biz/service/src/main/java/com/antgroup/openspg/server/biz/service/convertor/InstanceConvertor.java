/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.server.biz.service.convertor;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGPropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.server.api.facade.dto.service.request.EdgeRecordInstance;
import com.antgroup.openspg.server.api.facade.dto.service.request.VertexRecordInstance;
import com.antgroup.openspg.server.api.facade.dto.service.response.RelationInstance;
import com.antgroup.openspg.server.api.facade.dto.service.response.SPGTypeInstance;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;

public class InstanceConvertor {

  public static SPGTypeInstance toInstance(VertexRecord vertexRecord) {
    SPGTypeInstance instance = new SPGTypeInstance();
    instance.setId(vertexRecord.getId());
    instance.setSpgType(vertexRecord.getVertexType());
    instance.setProperties(vertexRecord.toPropertyMapWithId());
    return instance;
  }

  public static RelationInstance toInstance(EdgeRecord edgeRecord) {
    RelationInstance instance = new RelationInstance();
    instance.setSrcId(edgeRecord.getSrcId());
    instance.setDstId(edgeRecord.getDstId());
    instance.setRelationType(edgeRecord.getEdgeType().getEdgeLabel());
    instance.setProperties(edgeRecord.toPropertyMapWithId());
    return instance;
  }

  public static VertexRecord toVertexRecord(VertexRecordInstance instance) {
    List<LPGPropertyRecord> propertyList = Lists.newArrayList();
    instance.getProperties().entrySet().stream()
        .map(entry -> new LPGPropertyRecord(entry.getKey(), entry.getValue()))
        .forEach(propertyList::add);
    instance.getVectors().entrySet().stream()
        .map(entry -> new LPGPropertyRecord(entry.getKey(), entry.getValue()))
        .forEach(propertyList::add);
    return new VertexRecord(instance.getId(), instance.getType(), propertyList);
  }

  public static EdgeRecord toEdgeRecord(EdgeRecordInstance instance) {
    EdgeTypeName edgeTypeName =
        new EdgeTypeName(instance.getSrcType(), instance.getLabel(), instance.getDstType());
    List<LPGPropertyRecord> propertyList =
        instance.getProperties().entrySet().stream()
            .map(entry -> new LPGPropertyRecord(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    return new EdgeRecord(instance.getSrcId(), instance.getDstId(), edgeTypeName, propertyList);
  }
}
