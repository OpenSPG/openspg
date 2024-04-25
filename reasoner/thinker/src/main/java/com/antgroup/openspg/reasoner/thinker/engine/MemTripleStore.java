package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.thinker.TripleStore;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MemTripleStore implements TripleStore {
  @Override
  public void init(Map<String, String> param) {}

  @Override
  public List<Triple> find(Element s, Element p, Element o, TreeLogger treeLogger) {
    return Collections.emptyList();
  }

  @Override
  public void addEntity(Entity entity) {}

  @Override
  public void addTriple(Triple triple) {}

  @Override
  public void clear() {}
}
