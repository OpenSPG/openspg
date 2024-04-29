package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.thinker.logic.graph.*;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
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
  public List<Element> find(Triple pattern, TreeLogger treeLogger, Map<String, Object> context) {
    List<Triple> data;
    if (pattern.getSubject() instanceof Entity) {
      data = getTriple((Entity) pattern.getSubject(), Direction.OUT);
    } else if (pattern.getObject() instanceof Entity) {
      data = getTriple((Entity) pattern.getObject(), Direction.IN);
    } else {
      throw new RuntimeException("Cannot support " + pattern);
    }
    return matchInGraph(pattern, data);
  }

  protected List<Triple> getTriple(Entity s, Direction direction) {
    List<Triple> triples = new LinkedList<>();
    if (direction == Direction.OUT) {
      IVertex<IVertexId, IProperty> vertex =
          this.graphState.getVertex(IVertexId.from(s.getId(), s.getType()), null);
      for (String key : vertex.getValue().getKeySet()) {
        triples.add(new Triple(s, new Predicate(key), new Value(key, vertex.getValue().get(key))));
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

  private List<Element> matchInGraph(Triple pattern, List<Triple> data) {
    List<Element> rst = new LinkedList<>();
    for (Triple tri : data) {
      if (pattern.matches(tri)) {
        rst.add(tri);
      }
    }
    return rst;
  }
}
