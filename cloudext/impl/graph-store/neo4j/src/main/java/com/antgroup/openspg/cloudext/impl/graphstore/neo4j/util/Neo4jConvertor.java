/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.cloudext.impl.graphstore.neo4j.util;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGPropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.antgroup.openspg.common.util.neo4j.model.RelationLabelConstraint;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

public class Neo4jConvertor {

  public static VertexRecord convert2VertexRecord(Node neo4jNode) {
    String nodeLabel = getNodeLabelExcludeEntity(neo4jNode);
    String vertexId = null;
    List<LPGPropertyRecord> properties = Lists.newArrayList();
    Map<String, Object> propertyMap = neo4jNode.asMap();
    for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
      String propertyName = entry.getKey();
      Object propertyValue = entry.getValue();
      if (propertyName.equals("id")) {
        vertexId = (String) propertyValue;
      } else {
        properties.add(new LPGPropertyRecord(propertyName, propertyValue));
      }
    }
    return new VertexRecord(vertexId, nodeLabel, properties);
  }

  private static String getNodeLabelExcludeEntity(Node neo4jNode) {
    Iterator<String> labelIterator = neo4jNode.labels().iterator();
    String label = labelIterator.next();
    if ("Entity".equals(label)) {
      label = labelIterator.next();
    }
    return label;
  }

  public static EdgeRecord buildEdgeRecord(
      VertexRecord startVertex, Relationship neo4jRelation, VertexRecord endVertex) {
    String edgeLabel = neo4jRelation.type();
    EdgeTypeName edgeTypeName =
        new EdgeTypeName(startVertex.getVertexType(), edgeLabel, endVertex.getVertexType());
    List<LPGPropertyRecord> properties = Lists.newArrayList();
    Map<String, Object> propertyMap = neo4jRelation.asMap();
    for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
      String propertyName = entry.getKey();
      Object propertyValue = entry.getValue();
      properties.add(new LPGPropertyRecord(propertyName, propertyValue));
    }
    return new EdgeRecord(startVertex.getId(), endVertex.getId(), edgeTypeName, properties);
  }

  public static RelationLabelConstraint convert2RelationLabelConstraint(EdgeTypeName edgeTypeName) {
    return new RelationLabelConstraint(
        edgeTypeName.getStartVertexType(),
        edgeTypeName.getEdgeLabel(),
        edgeTypeName.getEndVertexType());
  }
}
