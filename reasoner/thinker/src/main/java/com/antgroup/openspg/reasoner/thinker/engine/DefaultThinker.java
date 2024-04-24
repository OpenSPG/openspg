package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.thinker.Thinker;
import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import java.util.LinkedList;
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
  public Result find(Element s, Element p, Element o) {
    Result result = new Result();
    List<Element> data = new LinkedList<>();
    data.addAll(this.graphStore.find(s, p, o));
    result.setData(data);
    return result;
  }

  @Override
  public Result find(Element s, Element p, Element o, Map<String, Object> context) {
    return null;
  }
}
