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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation.CreateIndexOperation;

public class AddVertexIndexProcedure extends BaseTuGraphProcedure {

  /** The template of cypher */
  public static final String ADD_VERTEX_INDEX_CYPHER_TEMPLATE =
      "CALL db.addIndex('${labelName}', '${fieldName}', ${isUnique})";

  private final String labelName;

  private final String fieldName;

  private final boolean isUnique;

  private AddVertexIndexProcedure(
      String cypherTemplate, String labelName, String fieldName, boolean isUnique) {
    super(cypherTemplate);
    this.labelName = labelName;
    this.fieldName = fieldName;
    this.isUnique = isUnique;
  }

  public static AddVertexIndexProcedure of(
      String labelName, CreateIndexOperation createIndexOperation) {
    return new AddVertexIndexProcedure(
        ADD_VERTEX_INDEX_CYPHER_TEMPLATE,
        labelName,
        createIndexOperation.getPropertyName(),
        createIndexOperation.getIsUnique());
  }
}
