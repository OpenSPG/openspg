package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.thinker.logic.LogicNetwork;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import com.antgroup.openspg.reasoner.thinker.logic.rule.TreeLogger;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InfTripleStore {
  private LogicNetwork logicNetwork;
  private MemTripleStore tripleStore;

  public InfTripleStore(LogicNetwork logicNetwork, MemTripleStore tripleStore) {
    this.logicNetwork = logicNetwork;
    this.tripleStore = tripleStore;
  }

  public void init(Map<String, String> param) {}

  public List<Triple> find(Element s, Element p, Element o, TreeLogger logger) {
    Collection<Rule> rules = logicNetwork.getForwardRules(s);
    if (rules.isEmpty()) {
      return new LinkedList<>();
    }
    return null;
  }

  public void addEntity(Entity entity) {
    this.tripleStore.addEntity(entity);
  }

  public void addTriple(Triple triple) {
    this.tripleStore.addTriple(triple);
  }

  public void clear() {
    this.tripleStore.clear();
  }
}
