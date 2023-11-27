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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record;

import com.antgroup.openspg.api.facade.ApiConstants;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.VertexType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * {@link VertexRecord VertexRecord} represents the persistent record of a {@link VertexType
 * VertexType} in <tt>LPG</tt>, identified by vertex id <tt>(id)</tt>.
 */
@Getter
public class VertexRecord extends BaseLPGRecord {

  @Setter private String vertexType;

  private final String id;

  public VertexRecord(String id, String vertexType, List<LPGPropertyRecord> properties) {
    super(LPGRecordTypeEnum.VERTEX, properties);
    this.id = id;
    this.vertexType = vertexType;
  }

  @Override
  public Map<String, Object> toPropertyMapWithId() {
    Map<String, Object> otherProperties = toPropertyMap();
    otherProperties.put(VertexType.ID, id);
    return otherProperties;
  }

  public Map<String, Map<Long, Object>> toPropertyMapWithIdAndVersion() {
    Map<String, Object> otherProperties = toPropertyMapWithId();

    Map<String, Map<Long, Object>> results = new HashMap<>(otherProperties.size());
    otherProperties.forEach(
        (key, value) -> {
          Map<Long, Object> propertyVersion = new HashMap<>(1);
          propertyVersion.put(ApiConstants.DEFAULT_VERSION, value);
          results.put(key, propertyVersion);
        });
    return results;
  }
}
