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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import java.util.Collections;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DropEdgeTypeOperation extends BaseLPGSchemaOperation {

  @Setter private EdgeTypeName edgeTypeName;

  public DropEdgeTypeOperation(EdgeTypeName edgeTypeName) {
    super(VertexEdgeTypeOperationEnum.DROP_EDGE_TYPE, Collections.EMPTY_LIST);
    this.edgeTypeName = edgeTypeName;
  }

  @Override
  public String getTargetTypeName() {
    return this.edgeTypeName.toString();
  }

  @Override
  public void checkSchemaAtomicOperations() {}
}
