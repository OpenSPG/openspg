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

import java.util.Collections;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DropVertexTypeOperation extends BaseLPGSchemaOperation {

  @Setter private String vertexTypeName;

  public DropVertexTypeOperation(String vertexTypeName) {
    super(VertexEdgeTypeOperationEnum.DROP_VERTEX_TYPE, Collections.EMPTY_LIST);
    this.vertexTypeName = vertexTypeName;
  }

  @Override
  public String getTargetTypeName() {
    return this.vertexTypeName;
  }

  @Override
  public void checkSchemaAtomicOperations() {}
}
