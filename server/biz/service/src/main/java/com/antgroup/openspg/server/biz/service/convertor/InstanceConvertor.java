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
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.server.api.facade.dto.service.response.RelationInstance;
import com.antgroup.openspg.server.api.facade.dto.service.response.SPGTypeInstance;

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
}
