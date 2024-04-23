package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.thinker.Thinker;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import java.util.List;
import java.util.Map;

public class DefaultThinker<K> implements Thinker<K> {
  private GraphStore<K> graphStore;

  public DefaultThinker(GraphState<K> graphState) {
    this.graphStore = new GraphStore<>(graphState);
  }

  @Override
  public void init(Map<String, String> params) {
    this.graphStore.init(params);
  }

  @Override
  public List<Triple> find(Element s, Element p, Element o) {
    return this.graphStore.find(s, p, o);
  }
}
