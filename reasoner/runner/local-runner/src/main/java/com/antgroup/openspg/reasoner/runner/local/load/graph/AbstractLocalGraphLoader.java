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

package com.antgroup.openspg.reasoner.runner.local.load.graph;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.graphstate.generator.AbstractGraphGenerator;
import java.util.List;
import java.util.Map;
import scala.Tuple2;

public abstract class AbstractLocalGraphLoader extends AbstractGraphGenerator {

  private GraphState<IVertexId> graphState;

  /** set graph state */
  public void setGraphState(GraphState<IVertexId> graphState) {
    this.graphState = graphState;
  }

  /** load graph */
  public void load() {
    List<IVertex<IVertexId, IProperty>> vertexList = getVertexList();
    for (IVertex<IVertexId, IProperty> vertex : vertexList) {
      this.graphState.addVertex(vertex);
    }

    Map<IVertexId, Tuple2<List<IEdge<IVertexId, IProperty>>, List<IEdge<IVertexId, IProperty>>>>
        edgeMap = getEdgeAggregated();
    for (IVertexId id : edgeMap.keySet()) {
      List<IEdge<IVertexId, IProperty>> inEdgeList = edgeMap.get(id)._1();
      List<IEdge<IVertexId, IProperty>> outEdgeList = edgeMap.get(id)._2();
      this.graphState.addEdges(id, inEdgeList, outEdgeList);
    }
  }
}
