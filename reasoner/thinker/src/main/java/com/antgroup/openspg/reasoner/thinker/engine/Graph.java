package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import java.util.List;
import java.util.Map;

public interface Graph {
  void init(Map<String, String> param);

  List<Element> find(Triple pattern, TreeLogger treeLogger, Map<String, Object> context);
}
