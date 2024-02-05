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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema;

import com.antgroup.openspg.server.common.model.base.BaseValObj;
import java.util.List;
import lombok.Getter;

/**
 * {@link LPGSchema LPGSchema} constants a list of {@link VertexType VertexTypes} and the other list
 * of {@link EdgeType EdgeTypes}, and represents schema information of <tt>LPG</tt>,
 */
@Getter
public class LPGSchema extends BaseValObj {

  private final List<VertexType> vertexTypes;

  private final List<EdgeType> edgeTypes;

  public LPGSchema(List<VertexType> vertexTypes, List<EdgeType> edgeTypes) {
    this.vertexTypes = vertexTypes;
    this.edgeTypes = edgeTypes;
  }
}
