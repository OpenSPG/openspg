/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.cloudext.impl.graphstore.neo4j;

import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.OneHopLPGRecordQuery;
import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.VertexLPGRecordQuery;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.Direction;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.GraphLPGRecordStruct;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.google.common.collect.Sets;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;

public class Neo4jTestUtil {

  public static boolean nodeExists(
      @NonNull Neo4jStoreClient client, @NonNull String type, @NonNull String id) {
    VertexLPGRecordQuery vertexQuery = new VertexLPGRecordQuery(id, type);
    GraphLPGRecordStruct queryResult = (GraphLPGRecordStruct) client.queryRecord(vertexQuery);
    if (queryResult == null) {
      return false;
    }
    return CollectionUtils.isNotEmpty(queryResult.getVertices());
  }

  public static boolean edgeExists(
      @NonNull Neo4jStoreClient client,
      @NonNull String sType,
      @NonNull String sId,
      @NonNull String predicate,
      @NonNull String oType,
      @NonNull String oId) {
    EdgeTypeName edgeTypeName = new EdgeTypeName(sType, predicate, oType);
    OneHopLPGRecordQuery oneHopQuery =
        new OneHopLPGRecordQuery(sId, sType, Sets.newHashSet(edgeTypeName), Direction.OUT);
    GraphLPGRecordStruct queryResult = (GraphLPGRecordStruct) client.queryRecord(oneHopQuery);
    if (queryResult == null || CollectionUtils.isEmpty(queryResult.getEdges())) {
      return false;
    }
    for (EdgeRecord edgeRecord : queryResult.getEdges()) {
      EdgeTypeName typeName = edgeRecord.getEdgeType();
      if (sType.equals(typeName.getStartVertexType())
          && predicate.equals(typeName.getEdgeLabel())
          && oType.equals(typeName.getEndVertexType())
          && sId.equals(edgeRecord.getSrcId())
          && oId.equals(edgeRecord.getDstId())) {
        return true;
      }
    }
    return false;
  }
}
