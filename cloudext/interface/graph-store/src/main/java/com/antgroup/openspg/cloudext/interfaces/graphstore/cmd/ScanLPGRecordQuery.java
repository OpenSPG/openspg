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
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;

public class ScanLPGRecordQuery extends BaseLPGRecordQuery {

  private final Object typeName;
  private final Integer limit;

  public ScanLPGRecordQuery(Object typeName, Integer limit) {
    super(LpgRecordQueryType.SCAN);
    this.typeName = typeName;
    this.limit = limit;
  }

  @Override
  public String toScript(LPGTypeNameConvertor lpgTypeNameConvertor) {
    StringBuilder sb = new StringBuilder();
    if (typeName instanceof EdgeTypeName) {
      EdgeTypeName edgeTypeName = (EdgeTypeName) typeName;
      sb.append(
          String.format(
              "MATCH (s:%s)-[p:%s]->(o:%s) RETURN p",
              lpgTypeNameConvertor.convertVertexTypeName(edgeTypeName.getStartVertexType()),
              lpgTypeNameConvertor.convertEdgeTypeName(edgeTypeName),
              lpgTypeNameConvertor.convertVertexTypeName(edgeTypeName.getEndVertexType())));
    } else {
      sb.append(
          String.format(
              "MATCH (s:%s) RETURN s",
              lpgTypeNameConvertor.convertVertexTypeName(typeName.toString())));
    }
    if (limit != null) {
      sb.append(" LIMIT ").append(limit);
    }
    return sb.toString();
  }
}
