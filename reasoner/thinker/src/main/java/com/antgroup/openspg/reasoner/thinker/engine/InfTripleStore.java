package com.antgroup.openspg.reasoner.thinker.engine;

import com.antgroup.openspg.reasoner.thinker.TripleStore;
import com.antgroup.openspg.reasoner.thinker.logic.LogicNetwork;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class InfTripleStore implements TripleStore {
  private LogicNetwork logicNetwork;

  public InfTripleStore(LogicNetwork logicNetwork) {
    this.logicNetwork = logicNetwork;
  }

  @Override
  public void init(Map<String, String> param) {}

  @Override
  public List<Triple> find(Element s, Element p, Element o) {
    return Collections.emptyList();
  }

  public void addTriple(Triple triple) {}
}
