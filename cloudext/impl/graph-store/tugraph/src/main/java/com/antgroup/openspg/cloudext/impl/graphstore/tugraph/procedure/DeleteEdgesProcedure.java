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

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/** Procedure of deleting edges. */
public class DeleteEdgesProcedure extends BaseTuGraphProcedure {

  /** Cypher template */
  private static final String DELETE_EDGES_CYPHER_TEMPLATE =
      "MATCH (n:${srcVertexType})-[e:${edgeType}]->(m:${dstVertexType}) ${edgeFilter} DELETE e;";

  /** Type of edge */
  private final String edgeType;

  /** Type of source vertex */
  private final String srcVertexType;

  /** Type of destination vertex */
  private final String dstVertexType;

  /** Vertex id list json string */
  private final String edgeFilter;

  /** Constructor. */
  private DeleteEdgesProcedure(
      String cypher,
      String edgeType,
      String srcVertexType,
      String dstVertexType,
      String edgeFilter) {
    super(cypher);
    this.edgeType = edgeType;
    this.srcVertexType = srcVertexType;
    this.dstVertexType = dstVertexType;
    this.edgeFilter = edgeFilter;
  }

  /** DeleteEdgesProcedure of edges. */
  public static DeleteEdgesProcedure of(Map.Entry<EdgeTypeName, List<EdgeRecord>> edgeMap) {
    EdgeTypeName edgeType = edgeMap.getKey();

    List<String> edgeDesc =
        edgeMap.getValue().stream()
            .filter(Objects::nonNull)
            .map(
                edgeRecord ->
                    String.format(
                        "(e.srcId = \"%s\" AND e.dstId = \"%s\" AND e.version = %s)",
                        edgeRecord.getSrcId(), edgeRecord.getDstId(), edgeRecord.getVersion()))
            .collect(Collectors.toList());

    return new DeleteEdgesProcedure(
        DELETE_EDGES_CYPHER_TEMPLATE,
        edgeType.getEdgeLabel(),
        edgeType.getStartVertexType(),
        edgeType.getEndVertexType(),
        "WHERE " + String.join(" OR ", edgeDesc));
  }

  /** DeleteEdgesProcedure of edges. */
  public static DeleteEdgesProcedure of(EdgeTypeName edgeType, List<EdgeRecord> edgeRecords) {
    List<String> edgeDesc =
        edgeRecords.stream()
            .filter(Objects::nonNull)
            .map(
                edgeRecord ->
                    String.format(
                        "(e.srcId = \"%s\" AND e.dstId = \"%s\" AND e.version = %s)",
                        edgeRecord.getSrcId(), edgeRecord.getDstId(), edgeRecord.getVersion()))
            .collect(Collectors.toList());

    return new DeleteEdgesProcedure(
        DELETE_EDGES_CYPHER_TEMPLATE,
        edgeType.getEdgeLabel(),
        edgeType.getStartVertexType(),
        edgeType.getEndVertexType(),
        "WHERE " + String.join(" OR ", edgeDesc));
  }

  @Override
  public String toString() {
    return "{\"procedure\":\"DeleteEdgesProcedure\", "
        + "\"edgeType\":\""
        + edgeType
        + "\", "
        + "\"srcVertexType\":\""
        + srcVertexType
        + "\", "
        + "\"dstVertexType\":\""
        + dstVertexType
        + "\", "
        + "\"edgeFilter\":\""
        + edgeFilter
        + "\", "
        + "\"cypherTemplate\":\""
        + getCypherTemplate()
        + "\"}";
  }
}
