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

package com.antgroup.openspg.reasoner.local;

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.EdgeProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.antgroup.openspg.reasoner.local.model.LocalReasonerResult;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class LocalReasonerResultTest {

  @Test
  public void localReasonerResultTest() {
    java.util.List<IVertex<IVertexId, IProperty>> vertexList =
        Lists.newArrayList(
            new Vertex<>(IVertexId.from("id1", "type1"), new VertexProperty()),
            new Vertex<>(IVertexId.from("id2", "type1"), new VertexProperty()));
    List<IEdge<IVertexId, IProperty>> edgeList =
        Lists.newArrayList(
            new Edge<>(
                IVertexId.from("id1", "type1"),
                IVertexId.from("id2", "type1"),
                new EdgeProperty(),
                Direction.OUT));
    boolean ddlResult = true;
    LocalReasonerResult localReasonerResult =
        new LocalReasonerResult(vertexList, edgeList, ddlResult);
    Assert.assertTrue(localReasonerResult.isGraphResult());

    System.out.println(localReasonerResult);
  }
}
