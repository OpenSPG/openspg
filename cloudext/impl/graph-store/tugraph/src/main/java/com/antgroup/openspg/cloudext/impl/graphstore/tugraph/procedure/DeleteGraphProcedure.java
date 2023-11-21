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

/** Delete graph procedure. */
public class DeleteGraphProcedure extends BaseTuGraphProcedure {

  /** Cypher template */
  private static final String GET_GRAPH_LIST_CYPHER_TEMPLATE =
      "CALL dbms.graph.deleteGraph('${graphName}')";

  /** Graph name. */
  private final String graphName;

  /** Constructor. */
  private DeleteGraphProcedure(String cypher, String graphName) {
    super(cypher);
    this.graphName = graphName;
  }

  public static DeleteGraphProcedure of(String graphName) {
    return new DeleteGraphProcedure(GET_GRAPH_LIST_CYPHER_TEMPLATE, graphName);
  }

  /**
   * Getter method for property <tt>graphName</tt>.
   *
   * @return property value of graphName
   */
  public String getGraphName() {
    return graphName;
  }

  @Override
  public String toString() {
    return "{\"procedure\":\"DeleteGraphProcedure\", "
        + "\"graphName\":\""
        + graphName
        + "\", "
        + "\"cypherTemplate\":\""
        + getCypherTemplate()
        + "\"}";
  }
}
