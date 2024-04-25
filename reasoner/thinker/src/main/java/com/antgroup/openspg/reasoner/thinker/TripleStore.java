package com.antgroup.openspg.reasoner.thinker;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import java.util.List;
import java.util.Map;

public interface TripleStore {
  void init(Map<String, String> param);

  List<Triple> find(Element s, Element p, Element o, TreeLogger treeLogger);

  void addEntity(Entity entity);

  void addTriple(Triple triple);

  void clear();
}
