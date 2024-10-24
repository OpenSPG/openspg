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

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.LPGPropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class Neo4jDataDefinitionTest {

  /** offline neo4j database. */
  private String connUrl =
      "neo4j://6.3.176.118:7687?user=neo4j&password=neo4j@openspg&database=bctest";

  private Neo4jStoreClient client = new Neo4jStoreClient(connUrl);

  private static final String TEST_NODE_1 = "BCTest.Node1";
  private static final String TEST_NODE_2 = "BCTest.Node2";
  private static final String TEST_NODE_3 = "BCTest.Node3";
  private static final String TEST_EDGE = "testEdge";
  private static final Long TS = System.currentTimeMillis();
  private static final String TEST_NODE_1_ID = String.valueOf(TS);
  private static final String TEST_NODE_2_ID = String.valueOf(TS + 1);
  private static final String TEST_NODE_3_ID = String.valueOf(TS + 2);
  private static final String TEST_NODE_4_ID = String.valueOf(TS + 3);
  private static final String TEST_NODE_5_ID = String.valueOf(TS + 4);
  private static final String TEST_NODE_6_ID = String.valueOf(TS + 5);
  private static final String TEST_NODE_7_ID = String.valueOf(TS + 6);

  private static final String TYPE_ENTITY = "Entity";

  @Test
  public void testManipulateNode() throws Exception {
    Assert.assertFalse(Neo4jTestUtil.nodeExists(client, TEST_NODE_1, TEST_NODE_1_ID));
    Assert.assertFalse(Neo4jTestUtil.nodeExists(client, TYPE_ENTITY, TEST_NODE_1_ID));

    // test upsert single vertex
    VertexRecord vertexRecord = new VertexRecord(TEST_NODE_1_ID, TEST_NODE_1, mockPropertyList());
    client.upsertVertex(TEST_NODE_1, Lists.newArrayList(vertexRecord));

    Assert.assertTrue(Neo4jTestUtil.nodeExists(client, TEST_NODE_1, TEST_NODE_1_ID));
    Assert.assertTrue(Neo4jTestUtil.nodeExists(client, TYPE_ENTITY, TEST_NODE_1_ID));

    client.deleteVertex(TEST_NODE_1, Lists.newArrayList(vertexRecord));
    Assert.assertFalse(Neo4jTestUtil.nodeExists(client, TEST_NODE_1, TEST_NODE_1_ID));
    Assert.assertFalse(Neo4jTestUtil.nodeExists(client, TYPE_ENTITY, TEST_NODE_1_ID));

    // test upsert batch vertices
    VertexRecord vertexRecord2 = new VertexRecord(TEST_NODE_6_ID, TEST_NODE_1, mockPropertyList());
    VertexRecord vertexRecord3 = new VertexRecord(TEST_NODE_7_ID, TEST_NODE_1, mockPropertyList());
    client.upsertVertex(TEST_NODE_1, Lists.newArrayList(vertexRecord2, vertexRecord3));
    Assert.assertTrue(Neo4jTestUtil.nodeExists(client, TEST_NODE_1, TEST_NODE_6_ID));
    Assert.assertTrue(Neo4jTestUtil.nodeExists(client, TYPE_ENTITY, TEST_NODE_7_ID));
    client.deleteVertex(TEST_NODE_1, Lists.newArrayList(vertexRecord2, vertexRecord3));
    Assert.assertFalse(Neo4jTestUtil.nodeExists(client, TEST_NODE_1, TEST_NODE_6_ID));
    Assert.assertFalse(Neo4jTestUtil.nodeExists(client, TYPE_ENTITY, TEST_NODE_6_ID));
    Assert.assertFalse(Neo4jTestUtil.nodeExists(client, TEST_NODE_1, TEST_NODE_7_ID));
    Assert.assertFalse(Neo4jTestUtil.nodeExists(client, TYPE_ENTITY, TEST_NODE_7_ID));
  }

  @Test
  public void testQueryAllVertexLabels() throws Exception {
    List<String> labels = client.queryAllVertexLabels();
    Assert.assertFalse(labels.isEmpty());
    Assert.assertTrue(labels.contains(TYPE_ENTITY));
  }

  @Test
  public void testManipulateEdge() throws Exception {
    Assert.assertFalse(Neo4jTestUtil.nodeExists(client, TEST_NODE_2, TEST_NODE_2_ID));
    Assert.assertFalse(Neo4jTestUtil.nodeExists(client, TEST_NODE_3, TEST_NODE_3_ID));
    Assert.assertFalse(
        Neo4jTestUtil.edgeExists(
            client, TEST_NODE_2, TEST_NODE_2_ID, TEST_EDGE, TEST_NODE_3, TEST_NODE_3_ID));

    EdgeTypeName edgeTypeName = new EdgeTypeName(TEST_NODE_2, TEST_EDGE, TEST_NODE_3);
    EdgeRecord edgeRecord =
        new EdgeRecord(TEST_NODE_2_ID, TEST_NODE_3_ID, edgeTypeName, mockPropertyList());
    client.upsertEdge(edgeTypeName.toString(), Lists.newArrayList(edgeRecord), true);

    EdgeRecord edgeRecord1 =
        new EdgeRecord(TEST_NODE_2_ID, TEST_NODE_4_ID, edgeTypeName, mockPropertyList());
    EdgeRecord edgeRecord2 =
        new EdgeRecord(TEST_NODE_2_ID, TEST_NODE_5_ID, edgeTypeName, mockPropertyList());
    client.upsertEdge(edgeTypeName.toString(), Lists.newArrayList(edgeRecord1, edgeRecord2), true);

    Assert.assertTrue(Neo4jTestUtil.nodeExists(client, TEST_NODE_2, TEST_NODE_2_ID));
    Assert.assertTrue(Neo4jTestUtil.nodeExists(client, TEST_NODE_3, TEST_NODE_3_ID));
    Assert.assertTrue(
        Neo4jTestUtil.edgeExists(
            client, TEST_NODE_2, TEST_NODE_2_ID, TEST_EDGE, TEST_NODE_3, TEST_NODE_3_ID));
    Assert.assertTrue(
        Neo4jTestUtil.edgeExists(
            client, TEST_NODE_2, TEST_NODE_2_ID, TEST_EDGE, TEST_NODE_3, TEST_NODE_4_ID));
    Assert.assertTrue(
        Neo4jTestUtil.edgeExists(
            client, TEST_NODE_2, TEST_NODE_2_ID, TEST_EDGE, TEST_NODE_3, TEST_NODE_5_ID));

    client.deleteEdge(edgeTypeName.toString(), Lists.newArrayList(edgeRecord));

    Assert.assertTrue(Neo4jTestUtil.nodeExists(client, TEST_NODE_2, TEST_NODE_2_ID));
    Assert.assertTrue(Neo4jTestUtil.nodeExists(client, TEST_NODE_3, TEST_NODE_3_ID));
    Assert.assertFalse(
        Neo4jTestUtil.edgeExists(
            client, TEST_NODE_2, TEST_NODE_2_ID, TEST_EDGE, TEST_NODE_3, TEST_NODE_3_ID));

    client.deleteEdge(edgeTypeName.toString(), Lists.newArrayList(edgeRecord1, edgeRecord2));
    Assert.assertFalse(
        Neo4jTestUtil.edgeExists(
            client, TEST_NODE_2, TEST_NODE_2_ID, TEST_EDGE, TEST_NODE_3, TEST_NODE_4_ID));
    Assert.assertFalse(
        Neo4jTestUtil.edgeExists(
            client, TEST_NODE_2, TEST_NODE_2_ID, TEST_EDGE, TEST_NODE_3, TEST_NODE_5_ID));
  }

  private List<LPGPropertyRecord> mockPropertyList() {
    List<LPGPropertyRecord> list = new ArrayList<>();
    list.add(new LPGPropertyRecord("textProp", "text"));
    list.add(new LPGPropertyRecord("doubleProp", 1.0d));
    list.add(new LPGPropertyRecord("longProp", 1L));
    float[] doubleValue = new float[] {1.0f, 0.5f, 0.0f};
    list.add(new LPGPropertyRecord("doubleArrayProp", doubleValue));
    return list;
  }
}
