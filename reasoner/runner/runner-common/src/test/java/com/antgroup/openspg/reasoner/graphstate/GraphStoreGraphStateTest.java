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
package com.antgroup.openspg.reasoner.graphstate;

import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.impl.GraphStoreGraphState;
import com.antgroup.openspg.reasoner.warehouse.common.AbstractGraphLoader;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;

public class GraphStoreGraphStateTest {
  @Test
  public void testReadVertex() {
    GraphStoreGraphState graphStoreGraphState = new GraphStoreGraphState();
    AbstractGraphLoader abstractGraphLoader = new MockGraphLoader(null);
    graphStoreGraphState.setGraphStoreQuery(abstractGraphLoader);
    IVertex<IVertexId, IProperty> v =
        graphStoreGraphState.getVertex(IVertexId.from("abc", "Test"), null);
    Assert.assertTrue(v != null);

    IVertex<IVertexId, IProperty> v1 =
        graphStoreGraphState.getVertex(IVertexId.from("abc2", "Test"), null);
    Assert.assertTrue(v1 == null);
  }

  @Test
  public void testException() {
    GraphStoreGraphState graphStoreGraphState = new GraphStoreGraphState();

    try {
      graphStoreGraphState.getVertexIterator(new HashSet<>());
      Assert.fail();
    } catch (Exception e) {
      Assert.assertTrue(true);
    }

    try {
      graphStoreGraphState.getVertexIterator(vertex1 -> vertex1.getId().equals("abc"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertTrue(true);
    }

    try {
      graphStoreGraphState.getEdgeIterator(new HashSet<>());
      Assert.fail();
    } catch (Exception e) {
      Assert.assertTrue(true);
    }

    try {
      graphStoreGraphState.getEdgeIterator(edge1 -> edge1.getSourceId().equals("abc"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertTrue(true);
    }
  }
}
