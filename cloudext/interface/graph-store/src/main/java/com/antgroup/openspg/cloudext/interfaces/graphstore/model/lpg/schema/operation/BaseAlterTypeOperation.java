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
import java.util.List;

public abstract class BaseAlterTypeOperation extends BaseLPGSchemaOperation {

  protected BaseAlterTypeOperation(
      VertexEdgeTypeOperationEnum operationTypeEnum,
      List<BaseSchemaAtomicOperation> atomicOperations) {
    super(operationTypeEnum, atomicOperations);
  }

  public abstract void addProperty(LPGProperty property);

  public abstract void dropProperty(String propertyName);

  public abstract void createIndex(String propertyName);

  public abstract void createIndex(String propertyName, boolean isUnique);

  public abstract void createIndex(String propertyName, boolean isUnique, boolean isGlobal);

  public abstract void dropIndex(String propertyName);

  public abstract void setTTL(String propertyName, long ts);

  public abstract void unsetTTL(String propertyName);
}
