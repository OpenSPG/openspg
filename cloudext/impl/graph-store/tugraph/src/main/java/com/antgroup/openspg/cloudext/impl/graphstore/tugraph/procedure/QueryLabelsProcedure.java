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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure;

import com.antgroup.openspg.cloudext.impl.graphstore.tugraph.model.TypeEnum;

/** Query label procedure. */
public class QueryLabelsProcedure extends BaseTuGraphProcedure {

  /** The cypher template for querying vertex labels */
  private static final String QUERY_VERTEX_LABELS_CYPHER_TEMPLATE =
      "CALL db.vertexLabels() YIELD label return label as labelName";

  /** The cypher template for querying edge labels */
  private static final String QUERY_EDGE_LABELS_CYPHER_TEMPLATE =
      "CALL db.edgeLabels() YIELD label return label as labelName";

  /** The constructor. */
  private QueryLabelsProcedure(String cypherTemplate) {
    super(cypherTemplate);
  }

  /**
   * Query labels procedure of data type.
   *
   * @param tuGraphDataTypeEnum the data type in TuGraph, either "vertex" or "edge"
   */
  public static QueryLabelsProcedure of(TypeEnum tuGraphDataTypeEnum) {
    switch (tuGraphDataTypeEnum) {
      case VERTEX:
        return new QueryLabelsProcedure(QUERY_VERTEX_LABELS_CYPHER_TEMPLATE);
      case EDGE:
        return new QueryLabelsProcedure(QUERY_EDGE_LABELS_CYPHER_TEMPLATE);
      default:
        throw new IllegalArgumentException(
            "unexpected tugraph data type enum:" + tuGraphDataTypeEnum);
    }
  }

  @Override
  public String toString() {
    return "{\"procedure\":\"QueryLabelsProcedure\", "
        + "\"cypherTemplate\":\""
        + getCypherTemplate()
        + "\"}";
  }
}
