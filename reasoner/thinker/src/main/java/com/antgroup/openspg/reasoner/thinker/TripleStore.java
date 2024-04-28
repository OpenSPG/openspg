package com.antgroup.openspg.reasoner.thinker;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import java.util.Collection;
import java.util.Map;

public interface TripleStore<K> {
  void init(Map<String, String> param);

  Collection<Element> find(final Triple tripleMatch);

  void addEntity(Entity<K> entity);

  void addTriple(Triple triple);

  void clear();
}
