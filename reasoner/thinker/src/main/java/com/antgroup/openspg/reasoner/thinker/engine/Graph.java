package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.thinker.logic.Result;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import java.util.List;
import java.util.Map;

public interface Graph {
  void init(Map<String, String> param);

  List<Result> find(Triple pattern, Map<String, Object> context);

  List<Result> find(Element s, Map<String, Object> context);
}
