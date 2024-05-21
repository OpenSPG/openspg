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

package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphStore implements Graph {
  private GraphState<IVertexId> graphState;

  public GraphStore(GraphState<IVertexId> graphState) {
    this.graphState = graphState;
  }

  public void init(Map<String, String> param) {
    this.graphState.init(param);
  }

  @Override
  public List<Result> find(Triple pattern, Map<String, Object> context) {
    List<Triple> data;
    if (pattern.getSubject() instanceof Entity) {
      data = getTriple((Entity) pattern.getSubject(), Direction.OUT);
    } else if (pattern.getObject() instanceof Entity) {
      data = getTriple((Entity) pattern.getObject(), Direction.IN);
    } else if (pattern.getSubject() instanceof Triple ) {
      data = getTriple((Triple) pattern.getSubject(), (Predicate) pattern.getPredicate());
    } else {
      throw new RuntimeException("Cannot support " + pattern);
    }
    return matchInGraph(pattern, data);
  }

  @Override
  public List<Result> find(Node s, Map<String, Object> context) {
    return Collections.emptyList();
  }

  protected List<Triple> getTriple(Entity s, Direction direction) {
    List<Triple> triples = new LinkedList<>();
    if (direction == Direction.OUT) {
      IVertex<IVertexId, IProperty> vertex =
          this.graphState.getVertex(IVertexId.from(s.getId(), s.getType()), null);
      if (vertex == null) {
        return triples;
      }
      for (String key : vertex.getValue().getKeySet()) {
        triples.add(new Triple(s, new Predicate(key), new Value(vertex.getValue().get(key))));
      }
    }
    List<IEdge<IVertexId, IProperty>> edges =
        this.graphState.getEdges(
            IVertexId.from(s.getId(), s.getType()), null, null, null, direction);
    for (IEdge<IVertexId, IProperty> edge : edges) {
      triples.add(edgeToTriple(edge));
    }
    return triples;
  }

  protected List<Triple> getTriple(Triple triple, Predicate predicate) {
    List<Triple> triples = new LinkedList<>();
    Entity s = (Entity) triple.getSubject();
    Predicate p = (Predicate) triple.getPredicate();
    Entity o = (Entity) triple.getObject();
    List<IEdge<IVertexId, IProperty>> edges =
            this.graphState.getEdges(
                    IVertexId.from(s.getId(), s.getType()), null, null, null, Direction.OUT);
    for (IEdge<IVertexId, IProperty> edge : edges) {
      if (edge.getTargetId().equals(IVertexId.from(o.getId(), o.getType())) && edge.getType().equals(p.getName())) {
        triples.add(new Triple(triple, predicate, new Value(edge.getValue().get(predicate.getName()))));
      }
    }
    return triples;
  }

  private Triple edgeToTriple(IEdge<IVertexId, IProperty> edge) {
    if (edge.getDirection() == Direction.OUT) {
      return new Triple(
          new Entity(
              (String) edge.getValue().get(Constants.EDGE_FROM_ID_KEY),
              (String) edge.getValue().get(Constants.EDGE_FROM_ID_TYPE_KEY)),
          new Predicate(edge.getType()),
          new Entity(
              (String) edge.getValue().get(Constants.EDGE_TO_ID_KEY),
              (String) edge.getValue().get(Constants.EDGE_TO_ID_TYPE_KEY)));
    } else {
      return new Triple(
          new Entity(
              (String) edge.getValue().get(Constants.EDGE_FROM_ID_KEY),
              (String) edge.getValue().get(Constants.EDGE_FROM_ID_TYPE_KEY)),
          new Predicate(edge.getType()),
          new Entity(
              (String) edge.getValue().get(Constants.EDGE_TO_ID_KEY),
              (String) edge.getValue().get(Constants.EDGE_TO_ID_TYPE_KEY)));
    }
  }

  private List<Result> matchInGraph(Triple pattern, List<Triple> data) {
    List<Result> rst = new LinkedList<>();
    for (Triple tri : data) {
      if (pattern.matches(tri)) {
        rst.add(new Result(tri, null));
      }
    }
    return rst;
  }
}
