package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.thinker.Thinker;
import com.antgroup.openspg.reasoner.thinker.catalog.LogicCatalog;
import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import java.util.*;

public class DefaultThinker<K> implements Thinker<K> {
  private GraphStore<K> graphStore;
  private InfGraph infGraph;

  public DefaultThinker(GraphState<K> graphState, LogicCatalog logicCatalog) {
    this.graphStore = new GraphStore<>(graphState);
    this.infGraph = new InfGraph(logicCatalog.getLogicNetwork(), graphStore);
  }

  @Override
  public void init(Map<String, String> params) {
    this.infGraph.init(params);
  }

  @Override
  public Result find(Element s, Element p, Element o) {
    return find(s, p, o, new HashMap<>());
  }

  @Override
  public Result find(Element s, Element p, Element o, Map<String, Object> context) {
    this.infGraph.clear();
    Triple pattern = Triple.create(s, p, o);
    Result result = new Result();
    List<Element> data = this.infGraph.find(pattern, result.getTraceLog(), context);
    result.setData(data);
    return result;
  }
}
