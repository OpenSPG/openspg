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

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.LPGProperty;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
public class AlterEdgeTypeOperation extends BaseAlterTypeOperation {

  @Setter private EdgeTypeName edgeTypeName;

  public AlterEdgeTypeOperation(EdgeTypeName edgeTypeName) {
    this(edgeTypeName, Lists.newArrayList());
  }

  public AlterEdgeTypeOperation(
      EdgeTypeName edgeTypeName, List<BaseSchemaAtomicOperation> atomicOperations) {
    super(VertexEdgeTypeOperationEnum.ALTER_EDGE_TYPE, atomicOperations);
    this.edgeTypeName = edgeTypeName;
  }

  @Override
  public void addProperty(LPGProperty property) {
    atomicOperations.add(new AddPropertyOperation(property));
  }

  @Override
  public void createIndex(String propertyName) {
    atomicOperations.add(new CreateIndexOperation(propertyName));
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
  public void dropProperty(String propertyName) {
    atomicOperations.add(new DropPropertyOperation(propertyName));
  }

  @Override
  public void dropIndex(String propertyName) {
    atomicOperations.add(new DropIndexOperation(propertyName));
  }

  @Override
  public void unsetTTL(String propertyName) {
    atomicOperations.add(new UnsetTtlOperation(propertyName));
  }

  @Override
  public void checkSchemaAtomicOperations() {}

  @Override
  public String getTargetTypeName() {
    return this.edgeTypeName.toString();
  }
}
