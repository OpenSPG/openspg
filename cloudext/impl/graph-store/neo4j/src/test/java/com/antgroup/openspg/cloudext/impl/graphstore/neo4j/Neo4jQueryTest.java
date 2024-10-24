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
package com.antgroup.openspg.cloudext.impl.graphstore.neo4j;

import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.*;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.ComputeResultRow;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.Direction;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct.GraphLPGRecordStruct;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

public class Neo4jQueryTest {

  /** offline neo4j database. */
  private String connUrl =
      "neo4j://6.3.176.118:7687?user=neo4j&password=neo4j@openspg&database=bctest";

  private Neo4jStoreClient client = new Neo4jStoreClient(connUrl);

  @Test
  public void testQuerySingleVertex() {
    GraphLPGRecordStruct struct1 =
        (GraphLPGRecordStruct)
            client.queryRecord(new VertexLPGRecordQuery("node_1_1", "BCTest.TestNode1"));
    Assert.assertEquals(1, struct1.getVertices().size());
    Assert.assertEquals(0, struct1.getEdges().size());
    VertexRecord vertexRecord = struct1.getVertices().get(0);
    Assert.assertEquals("BCTest.TestNode1", vertexRecord.getVertexType());
    Assert.assertEquals("node_1_1", vertexRecord.getId());
    Map<String, Object> properties = vertexRecord.toPropertyMapWithId();
    Assert.assertEquals(6, properties.size());
    Assert.assertEquals("node_1_1", properties.get("id"));
    Assert.assertEquals("node_1_1", properties.get("name"));
    Assert.assertEquals("1", properties.get("stringProp"));
    Assert.assertEquals(1.0d, properties.get("floatProp"));
    Assert.assertEquals(1L, properties.get("integerProp"));
    Assert.assertTrue(properties.containsKey("_name_vector"));
  }

  @Test
  public void testBatchQuery() {
    GraphLPGRecordStruct struct =
        (GraphLPGRecordStruct)
            client.queryRecord(
                new BatchVertexLPGRecordQuery(
                    Sets.newHashSet("node_1_1", "node_1_2", "node_1_3"), "BCTest.TestNode1"));
    Assert.assertNotNull(struct);
    Assert.assertEquals(3, struct.getVertices().size());
  }

  @Test
  public void testGetPageRank() {
    VertexRecord node1 = new VertexRecord("node_1_1", "BCTest.TestNode1", Lists.newArrayList());
    VertexRecord node2 = new VertexRecord("node_1_2", "BCTest.TestNode1", Lists.newArrayList());
    VertexRecord node3 = new VertexRecord("node_2_1", "BCTest.TestNode2", Lists.newArrayList());
    List<VertexRecord> startNodoList = Lists.newArrayList(node1, node2, node3);
    PageRankCompete compete = new PageRankCompete(startNodoList, "BCTest.TestNode1");
    List<ComputeResultRow> result = client.runPageRank(compete);
    Assert.assertFalse(result.isEmpty());
  }

  @Test
  public void testScanVertices() {
    // scan vertices without limit
    GraphLPGRecordStruct struct2 =
        (GraphLPGRecordStruct) client.queryRecord(new ScanLPGRecordQuery("BCTest.TestNode2", null));
    Assert.assertEquals(100, struct2.getVertices().size());
    Assert.assertEquals(0, struct2.getEdges().size());

    // set limit "10" to scan
    GraphLPGRecordStruct struct3 =
        (GraphLPGRecordStruct) client.queryRecord(new ScanLPGRecordQuery("BCTest.TestNode2", 10));
    Assert.assertEquals(10, struct3.getVertices().size());
    Assert.assertEquals(0, struct3.getEdges().size());
  }

  @Test
  public void testQueryOneHopByOutDirection() {
    EdgeTypeName edgeType1 = new EdgeTypeName("BCTest.TestNode1", "nextTo", "BCTest.TestNode1");
    EdgeTypeName edgeType2 = new EdgeTypeName("BCTest.TestNode1", "linkTo", "BCTest.TestNode2");

    GraphLPGRecordStruct struct1 =
        (GraphLPGRecordStruct)
            client.queryRecord(
                new OneHopLPGRecordQuery(
                    "node_1_1",
                    "BCTest.TestNode1",
                    Sets.newHashSet(edgeType1, edgeType2),
                    Direction.OUT));

    Assert.assertEquals(3, struct1.getVertices().size());
    Map<String, VertexRecord> actualVertexRecords =
        struct1.getVertices().stream()
            .collect(Collectors.toMap(VertexRecord::generateUniqueString, Function.identity()));
    VertexRecord node_1_1 = actualVertexRecords.get("BCTest.TestNode1|node_1_1");
    Assert.assertNotNull(node_1_1);
    Assert.assertEquals("BCTest.TestNode1", node_1_1.getVertexType());
    Assert.assertEquals("node_1_1", node_1_1.getId());
    Map<String, Object> node_1_1_properties = node_1_1.toPropertyMapWithId();
    Assert.assertEquals(6, node_1_1_properties.size());
    Assert.assertEquals("node_1_1", node_1_1_properties.get("id"));
    Assert.assertEquals("node_1_1", node_1_1_properties.get("name"));
    Assert.assertEquals("1", node_1_1_properties.get("stringProp"));
    Assert.assertEquals(1.0d, node_1_1_properties.get("floatProp"));
    Assert.assertEquals(1L, node_1_1_properties.get("integerProp"));
    Assert.assertTrue(node_1_1_properties.containsKey("_name_vector"));

    VertexRecord node_1_2 = actualVertexRecords.get("BCTest.TestNode1|node_1_2");
    Assert.assertNotNull(node_1_2);
    Assert.assertEquals("BCTest.TestNode1", node_1_2.getVertexType());
    Assert.assertEquals("node_1_2", node_1_2.getId());
    Map<String, Object> node_1_2_properties = node_1_2.toPropertyMapWithId();
    Assert.assertEquals(6, node_1_2_properties.size());
    Assert.assertEquals("node_1_2", node_1_2_properties.get("id"));
    Assert.assertEquals("node_1_2", node_1_2_properties.get("name"));
    Assert.assertEquals("2", node_1_2_properties.get("stringProp"));
    Assert.assertEquals(2.0d, node_1_2_properties.get("floatProp"));
    Assert.assertEquals(2L, node_1_2_properties.get("integerProp"));
    Assert.assertTrue(node_1_2_properties.containsKey("_name_vector"));

    VertexRecord node_2_1 = actualVertexRecords.get("BCTest.TestNode2|node_2_1");
    Assert.assertNotNull(node_2_1);
    Assert.assertEquals("BCTest.TestNode2", node_2_1.getVertexType());
    Assert.assertEquals("node_2_1", node_2_1.getId());
    Map<String, Object> node_2_1_properties = node_2_1.toPropertyMapWithId();
    Assert.assertEquals(2, node_2_1_properties.size());
    Assert.assertEquals("node_2_1", node_2_1_properties.get("id"));
    Assert.assertEquals("node_2_1", node_2_1_properties.get("name"));

    Assert.assertEquals(2, struct1.getEdges().size());
    Map<String, EdgeRecord> actualEdgeRecords =
        struct1.getEdges().stream()
            .collect(Collectors.toMap(EdgeRecord::generateUniqueString, Function.identity()));
    EdgeRecord linkTo =
        actualEdgeRecords.get("node_1_1|BCTest.TestNode1_linkTo_BCTest.TestNode2|node_2_1|0");
    Assert.assertNotNull(linkTo);
    Assert.assertEquals("BCTest.TestNode1", linkTo.getEdgeType().getStartVertexType());
    Assert.assertEquals("BCTest.TestNode2", linkTo.getEdgeType().getEndVertexType());
    Assert.assertEquals("linkTo", linkTo.getEdgeType().getEdgeLabel());
    Assert.assertEquals("node_1_1", linkTo.getSrcId());
    Assert.assertEquals("node_2_1", linkTo.getDstId());
    Map<String, Object> linkToProperties = linkTo.toPropertyMap();
    Assert.assertEquals(3, linkToProperties.size());
    Assert.assertEquals("1", linkToProperties.get("stringProp"));
    Assert.assertEquals(1.0d, linkToProperties.get("floatProp"));
    Assert.assertEquals(1L, linkToProperties.get("integerProp"));

    EdgeRecord nextTo =
        actualEdgeRecords.get("node_1_1|BCTest.TestNode1_nextTo_BCTest.TestNode1|node_1_2|0");
    Assert.assertNotNull(nextTo);
    Assert.assertEquals("BCTest.TestNode1", nextTo.getEdgeType().getStartVertexType());
    Assert.assertEquals("BCTest.TestNode1", nextTo.getEdgeType().getEndVertexType());
    Assert.assertEquals("nextTo", nextTo.getEdgeType().getEdgeLabel());
    Assert.assertEquals("node_1_1", nextTo.getSrcId());
    Assert.assertEquals("node_1_2", nextTo.getDstId());
    Map<String, Object> nextToProperties = nextTo.toPropertyMap();
    Assert.assertEquals(0, nextToProperties.size());

    GraphLPGRecordStruct struct2 =
        (GraphLPGRecordStruct)
            client.queryRecord(
                new OneHopLPGRecordQuery("node_1_1", "BCTest.TestNode1", null, Direction.OUT));

    Assert.assertEquals(3, struct2.getVertices().size());
    Assert.assertEquals(2, struct2.getEdges().size());

    GraphLPGRecordStruct struct3 =
        (GraphLPGRecordStruct)
            client.queryRecord(
                new OneHopLPGRecordQuery(
                    "node_1_1", "BCTest.TestNode1", Sets.newHashSet(), Direction.OUT));

    Assert.assertEquals(1, struct3.getVertices().size());
    Assert.assertEquals(0, struct3.getEdges().size());
  }

  @Test
  public void testQueryOneHopByInDirection() {
    EdgeTypeName edgeType1 = new EdgeTypeName("BCTest.TestNode1", "nextTo", "BCTest.TestNode1");
    EdgeTypeName edgeType2 = new EdgeTypeName("BCTest.TestNode1", "linkTo", "BCTest.TestNode2");

    GraphLPGRecordStruct struct1 =
        (GraphLPGRecordStruct)
            client.queryRecord(
                new OneHopLPGRecordQuery(
                    "node_1_1",
                    "BCTest.TestNode1",
                    Sets.newHashSet(edgeType1, edgeType2),
                    Direction.IN));

    Assert.assertEquals(2, struct1.getVertices().size());
    Assert.assertEquals(1, struct1.getEdges().size());

    GraphLPGRecordStruct struct2 =
        (GraphLPGRecordStruct)
            client.queryRecord(
                new OneHopLPGRecordQuery("node_1_1", "BCTest.TestNode1", null, Direction.IN));

    Assert.assertEquals(2, struct2.getVertices().size());
    Assert.assertEquals(1, struct2.getEdges().size());

    GraphLPGRecordStruct struct3 =
        (GraphLPGRecordStruct)
            client.queryRecord(
                new OneHopLPGRecordQuery(
                    "node_1_1", "BCTest.TestNode1", Sets.newHashSet(), Direction.IN));

    Assert.assertEquals(1, struct3.getVertices().size());
    Assert.assertEquals(0, struct3.getEdges().size());
  }

  @Test
  public void testQueryOneHopByBothDirection() {
    EdgeTypeName edgeType1 = new EdgeTypeName("BCTest.TestNode1", "nextTo", "BCTest.TestNode1");
    EdgeTypeName edgeType2 = new EdgeTypeName("BCTest.TestNode1", "linkTo", "BCTest.TestNode2");

    GraphLPGRecordStruct struct1 =
        (GraphLPGRecordStruct)
            client.queryRecord(
                new OneHopLPGRecordQuery(
                    "node_1_1",
                    "BCTest.TestNode1",
                    Sets.newHashSet(edgeType1, edgeType2),
                    Direction.BOTH));

    Assert.assertEquals(4, struct1.getVertices().size());
    Assert.assertEquals(3, struct1.getEdges().size());

    GraphLPGRecordStruct struct2 =
        (GraphLPGRecordStruct)
            client.queryRecord(
                new OneHopLPGRecordQuery("node_1_1", "BCTest.TestNode1", null, Direction.BOTH));

    Assert.assertEquals(4, struct2.getVertices().size());
    Assert.assertEquals(3, struct2.getEdges().size());

    GraphLPGRecordStruct struct3 =
        (GraphLPGRecordStruct)
            client.queryRecord(
                new OneHopLPGRecordQuery(
                    "node_1_1", "BCTest.TestNode1", Sets.newHashSet(), Direction.BOTH));

    Assert.assertEquals(1, struct3.getVertices().size());
    Assert.assertEquals(0, struct3.getEdges().size());
  }
}
