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
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class BatchVertexLPGRecordQuery extends BaseLPGRecordQuery {

  private final Set<String> vertexIds;
  private final String vertexName;

  public BatchVertexLPGRecordQuery(Set<String> vertexIds, String vertexName) {
    super(LpgRecordQueryType.BATCH_VERTEX);
    this.vertexIds = vertexIds;
    this.vertexName = vertexName;
  }

  @Override
  public String toScript(LPGTypeNameConvertor lpgTypeNameConvertor) {
    String convertedVertexName = lpgTypeNameConvertor.convertVertexTypeName(vertexName);
    return String.format(
        "Match (s:%s) WHERE s.id in [%s] RETURN s",
        convertedVertexName,
        vertexIds.stream().map(x -> String.format("'%s'", x)).collect(Collectors.joining(",")));
  }
}
