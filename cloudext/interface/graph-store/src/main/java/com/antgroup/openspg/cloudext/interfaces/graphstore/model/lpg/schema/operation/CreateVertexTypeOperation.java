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

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.LPGProperty;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.VertexType;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;

@Getter
public class CreateVertexTypeOperation extends BaseCreateTypeOperation {

  @Setter private String vertexTypeName;

  public CreateVertexTypeOperation(String vertexTypeName) {
    this(vertexTypeName, Lists.newArrayList());
  }

  public CreateVertexTypeOperation(
      String vertexTypeName, List<BaseSchemaAtomicOperation> atomicOperations) {
    super(VertexEdgeTypeOperationEnum.CREATE_VERTEX_TYPE, atomicOperations);
    this.vertexTypeName = vertexTypeName;
  }

  @Override
  public void checkSchemaAtomicOperations() {
    Map<String, LPGProperty> addPropertyMap =
        atomicOperations.stream()
            .filter(
                operation ->
                    SchemaAtomicOperationEnum.ADD_PROPERTY.equals(operation.getOperationTypeEnum()))
            .map(operation -> ((AddPropertyOperation) operation).getProperty())
            .collect(Collectors.toMap(LPGProperty::getName, Function.identity()));
    if (MapUtils.isEmpty(addPropertyMap) || addPropertyMap.get(VertexType.ID) == null) {
      throw new IllegalArgumentException(
          String.format(
              "%s must in the property of vertex, " + "and now the property of vertex(%s) is %s",
              VertexType.ID, getTargetTypeName(), addPropertyMap.keySet()));
    }
    addPropertyMap.get(VertexType.ID).setPrimaryKey(true);
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
    return this.vertexTypeName;
  }
}
