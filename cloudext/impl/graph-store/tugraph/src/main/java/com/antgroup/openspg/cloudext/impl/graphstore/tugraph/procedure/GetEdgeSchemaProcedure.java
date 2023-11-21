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

/** Get edge schema procedure. */
public class GetEdgeSchemaProcedure extends BaseTuGraphProcedure {

  /** The cypher template */
  private static final String GET_EDGE_SCHEMA_CYPHER_TEMPLATE =
      "CALL db.getEdgeSchema('${labelName}')";

  /** Name of the label */
  private final String labelName;

  /** The constructor */
  private GetEdgeSchemaProcedure(String cypherTemplate, String labelName) {
    super(cypherTemplate);
    this.labelName = labelName;
  }

  /** Get edge schema procedure of label name. */
  public static GetEdgeSchemaProcedure of(String labelName) {
    return new GetEdgeSchemaProcedure(GET_EDGE_SCHEMA_CYPHER_TEMPLATE, labelName);
  }

  @Override
  public String toString() {
    return "{\"procedure\":\"GetEdgeSchemaProcedure\", "
        + "\"labelName\":\""
        + labelName
        + "\", "
        + "\"cypherTemplate\":\""
        + getCypherTemplate()
        + "\"}";
  }
}
