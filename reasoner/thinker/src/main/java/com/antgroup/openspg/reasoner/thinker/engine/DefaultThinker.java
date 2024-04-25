package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.thinker.Thinker;
import com.antgroup.openspg.reasoner.thinker.TripleStore;
import com.antgroup.openspg.reasoner.thinker.catalog.LogicCatalog;
import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.*;
import java.util.*;

public class DefaultThinker<K> implements Thinker<K> {
  private GraphStore<K> graphStore;
  private TripleStore tripleStore;
  private LogicCatalog logicCatalog;

  public DefaultThinker(GraphState<K> graphState, LogicCatalog logicCatalog) {
    this.graphStore = new GraphStore<>(graphState);
    this.tripleStore = new MemTripleStore();
    this.logicCatalog = logicCatalog;
  }

  @Override
  public void init(Map<String, String> params) {
    this.graphStore.init(params);
  }

  @Override
  public Result find(Element s, Element p, Element o) {
    return find(s, p, o, new HashMap<>());
  }

  @Override
  public Result find(Element s, Element p, Element o, Map<String, Object> context) {
    this.tripleStore.clear();
    List<Triple> data;
    if (s instanceof Entity) {
      data = this.graphStore.getTriple((Entity) s, Direction.OUT);
    } else if (o instanceof Entity) {
      data = this.graphStore.getTriple((Entity) o, Direction.IN);
    } else {
      throw new RuntimeException("Cannot support " + s);
    }
    Triple pattern = Triple.create(s, p, o);
    return matchInGraph(pattern, data);
  }

  private Result matchInGraph(Triple pattern, List<Triple> data) {
    Result result = new Result();
    List<Element> rst = new LinkedList<>();
    for (Triple tri : data) {
      if (pattern.matches(tri)) {
        rst.add(tri);
      }
    }
    result.setData(rst);
    return result;
  }


}
