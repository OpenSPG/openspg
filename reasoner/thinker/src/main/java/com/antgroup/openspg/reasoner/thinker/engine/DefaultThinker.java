package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.thinker.Thinker;
import com.antgroup.openspg.reasoner.thinker.logic.graph.*;
import java.util.*;

public class DefaultThinker<K> implements Thinker<K> {
  private GraphState<K> graphState;

  public DefaultThinker(GraphState<K> graphState) {
    this.graphState = graphState;
  }

  @Override
  public void init(Map<String, String> params) {
    this.graphState.init(params);
  }

  @Override
  public List<Triple> find(Element s, Element p, Element o) {
    if (s instanceof Entity) {
      return match((Entity<K>) s, p, o, Direction.OUT);
    } else if (o instanceof Entity) {
      return match((Entity<K>) o, p, s, Direction.IN);
    } else {
      throw new RuntimeException("Cannot support " + s);
    }
  }

  private List<Triple> match(Entity<K> s, Element p, Element o, Direction direction) {
    IVertex<K, IProperty> vertex = this.graphState.getVertex(s.getId(), null);
    List<IEdge<K, IProperty>> edges;
    if (p instanceof Any) {
      edges = this.graphState.getEdges(s.getId(), null, null, null, direction);
    } else if (p instanceof Predicate) {
      Set<String> types = new HashSet<>();
      types.add(((Predicate) p).getName());
      edges = this.graphState.getEdges(s.getId(), null, null, types, direction);
    } else {
      throw new RuntimeException("Cannot support " + p);
    }

    return match(s, p, o, vertex, edges);
  }

  private List<Triple> match(
      Entity s,
      Element p,
      Element o,
      IVertex<K, IProperty> vertex,
      List<IEdge<K, IProperty>> edges) {
    List<Triple> triples = new LinkedList<>();
    for (String key : vertex.getValue().getKeySet()) {
      if (p instanceof Any || ((Predicate) p).getName().equalsIgnoreCase(key)) {
        triples.add(new Triple(s, new Predicate(key), new Value(key, vertex.getValue().get(key))));
      }
    }
    for (IEdge<K, IProperty> edge : edges) {
      if (p instanceof Any || ((Predicate) p).getName().equalsIgnoreCase(edge.getType())) {
        if (o instanceof Entity) {
          if (edge.getTargetId().equals(((Entity<?>) o).getId())) {
            triples.add(edgeToTriple(edge));
          }
        } else if (o instanceof Node) {
          if (edge.getValue().get(Constants.EDGE_TO_ID_TYPE_KEY).equals(((Node) o).getType())) {
            triples.add(edgeToTriple(edge));
          }
        } else if (o instanceof Any) {
          triples.add(edgeToTriple(edge));
        }
      }
    }
    return triples;
  }

  private Triple edgeToTriple(IEdge<K, IProperty> edge) {
    if (edge.getDirection() == Direction.OUT) {
      return new Triple(
          new Entity(edge.getSourceId()),
          new Predicate(edge.getType()),
          new Entity(edge.getTargetId()));
    } else {
      return new Triple(
          new Entity(edge.getTargetId()),
          new Predicate(edge.getType()),
          new Entity(edge.getSourceId()));
    }
  }
}
