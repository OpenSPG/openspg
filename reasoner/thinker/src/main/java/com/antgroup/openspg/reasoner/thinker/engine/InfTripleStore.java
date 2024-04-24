package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.thinker.TripleStore;
import com.antgroup.openspg.reasoner.thinker.logic.LogicNetwork;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InfTripleStore implements TripleStore {
  private LogicNetwork logicNetwork;
  private MemTripleStore tripleStore;

  public InfTripleStore(LogicNetwork logicNetwork, MemTripleStore tripleStore) {
    this.logicNetwork = logicNetwork;
    this.tripleStore = tripleStore;
  }

  @Override
  public void init(Map<String, String> param) {}

  @Override
  public List<Triple> find(Element s, Element p, Element o) {
    Collection<Rule> rules = logicNetwork.getForwardRules(s);
    if (rules.isEmpty()) {
      return new LinkedList<>();
    }
    return null;
  }

  @Override
  public void addEntity(Entity entity) {
    this.tripleStore.addEntity(entity);
  }

  public void addTriple(Triple triple) {
    this.tripleStore.addTriple(triple);
  }

  @Override
  public void clear() {
    this.tripleStore.clear();
  }
}
