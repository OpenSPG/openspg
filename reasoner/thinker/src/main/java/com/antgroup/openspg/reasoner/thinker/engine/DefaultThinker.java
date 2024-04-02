package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.thinker.Thinker;
import com.antgroup.openspg.reasoner.thinker.logic.graph.*;
import java.util.*;

public class DefaultThinker implements Thinker {
  private GraphState<String> graphState;

  public DefaultThinker(GraphState<String> graphState) {
    this.graphState = graphState;
  }

  @Override
  public void init(Map<String, String> params) {
    this.graphState.init(params);
  }

  @Override
  public List<Triple> find(Element s, Element p, Element o) {
    if (s instanceof Entity) {
      return match((Entity) s, p, o, Direction.OUT);
    } else if (o instanceof Entity) {
      return match((Entity) s, p, o, Direction.IN);
    } else {
      throw new RuntimeException("Cannot support " + s);
    }
  }

  private List<Triple> match(Entity s, Element p, Element o, Direction direction) {
    IVertex<String, IProperty> vertex = this.graphState.getVertex(s.getId(), null);
    List<IEdge<String, IProperty>> edges = new LinkedList<>();
    if (direction == Direction.OUT) {
      if (p instanceof NodeAny) {
        edges = this.graphState.getEdges(s.getId(), null, null, null, direction);
      } else if (p instanceof Predicate) {
        Set<String> types = new HashSet<>();
        types.add(((Predicate) p).getName());
        edges = this.graphState.getEdges(s.getId(), null, null, types, direction);
      } else {
        throw new RuntimeException("Cannot support " + p);
      }
    } else {
      if (p instanceof Predicate) {
        Set<String> types = new HashSet<>();
        types.add(((Predicate) p).getName());
        edges = this.graphState.getEdges(s.getId(), null, null, null, Direction.IN);
      } else {
        throw new RuntimeException("Cannot support " + p);
      }
    }
    return match(s, p, o, vertex, edges);
  }

  private List<Triple> match(
      Entity s,
      Element p,
      Element o,
      IVertex<String, IProperty> vertex,
      List<IEdge<String, IProperty>> edges) {
    List<Triple> triples = new LinkedList<>();
    for (String key : vertex.getValue().getKeySet()) {
      if (p instanceof NodeAny || ((Predicate) p).getName().equalsIgnoreCase(key)) {
        triples.add(new Triple(s, p, new Value(key, vertex.getValue().get(key))));
      }
    }
    for (IEdge<String, IProperty> edge : edges) {
      if (p instanceof NodeAny || ((Predicate) p).getName().equalsIgnoreCase(edge.getType())) {
        triples.add(new Triple(s, p, new Entity(edge.getTargetId())));
      }
    }
    return triples;
  }
}
