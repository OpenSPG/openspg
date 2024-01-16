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

package com.antgroup.openspg.cloudext.interfaces.graphstore.cmd;

import com.antgroup.openspg.cloudext.interfaces.graphstore.LPGTypeNameConvertor;
import lombok.Getter;

@Getter
public class VertexLPGRecordQuery extends BaseLPGRecordQuery {

  private final String vertexId;
  private final String vertexName;

  public VertexLPGRecordQuery(String vertexId, String vertexName) {
    super(LpgRecordQueryType.VERTEX);
    this.vertexId = vertexId;
    this.vertexName = vertexName;
  }

  @Override
  public String toScript(LPGTypeNameConvertor lpgTypeNameConvertor) {
    String convertedVertexName = lpgTypeNameConvertor.convertVertexTypeName(vertexName);
    return String.format("MATCH (s:%s) WHERE s.id='%s' RETURN s", convertedVertexName, vertexId);
  }
}
