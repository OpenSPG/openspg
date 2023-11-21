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

/** Get vertex schema procedure. */
public class GetVertexSchemaProcedure extends BaseTuGraphProcedure {

  /** The cypher template */
  static final String GET_VERTEX_SCHEMA_CYPHER_TEMPLATE = "CALL db.getVertexSchema('${labelName}')";

  /** Name of the label */
  private final String labelName;

  /** The constructor */
  private GetVertexSchemaProcedure(String cypherTemplate, String labelName) {
    super(cypherTemplate);
    this.labelName = labelName;
  }

  /** Get edge schema procedure of label name. */
  public static GetVertexSchemaProcedure of(String labelName) {
    return new GetVertexSchemaProcedure(GET_VERTEX_SCHEMA_CYPHER_TEMPLATE, labelName);
  }

  @Override
  public String toString() {
    return "{\"procedure\":\"GetVertexSchemaProcedure\", "
        + "\"labelName\":\""
        + labelName
        + "\", "
        + "\"cypherTemplate\":\""
        + getCypherTemplate()
        + "\"}";
  }
}
