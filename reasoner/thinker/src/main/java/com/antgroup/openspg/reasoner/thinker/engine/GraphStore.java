package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.thinker.logic.graph.*;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphStore<K> implements Graph<K> {
  private GraphState<K> graphState;

  public GraphStore(GraphState<K> graphState) {
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

  protected List<Triple> getTriple(Entity<K> s, Direction direction) {
    List<Triple> triples = new LinkedList<>();
    if (direction == Direction.OUT) {
      IVertex<K, IProperty> vertex = this.graphState.getVertex(s.getId(), null);
      for (String key : vertex.getValue().getKeySet()) {
        triples.add(new Triple(s, new Predicate(key), new Value(key, vertex.getValue().get(key))));
      }
    }
    List<IEdge<K, IProperty>> edges =
        this.graphState.getEdges(s.getId(), null, null, null, direction);
    for (IEdge<K, IProperty> edge : edges) {
      triples.add(edgeToTriple(edge));
    }
    return triples;
  }

  private Triple edgeToTriple(IEdge<K, IProperty> edge) {
    if (edge.getDirection() == Direction.OUT) {
      return new Triple(
          new Entity(
              edge.getSourceId(), (String) edge.getValue().get(Constants.EDGE_FROM_ID_TYPE_KEY)),
          new Predicate(edge.getType()),
          new Entity(
              edge.getTargetId(), (String) edge.getValue().get(Constants.EDGE_TO_ID_TYPE_KEY)));
    } else {
      return new Triple(
          new Entity(
              edge.getTargetId(), (String) edge.getValue().get(Constants.EDGE_FROM_ID_TYPE_KEY)),
          new Predicate(edge.getType()),
          new Entity(
              edge.getSourceId(), (String) edge.getValue().get(Constants.EDGE_TO_ID_TYPE_KEY)));
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
