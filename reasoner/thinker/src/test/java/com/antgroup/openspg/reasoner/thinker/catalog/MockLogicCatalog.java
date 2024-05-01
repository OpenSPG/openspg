package com.antgroup.openspg.reasoner.thinker.catalog;

import com.antgroup.openspg.reasoner.lube.catalog.AbstractConnection;
import com.antgroup.openspg.reasoner.lube.catalog.SemanticPropertyGraph;
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field;
import com.antgroup.openspg.reasoner.thinker.logic.LogicNetwork;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import java.util.ArrayList;
import java.util.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;

public class MockLogicCatalog extends LogicCatalog {
  private List<Rule> rules;

  public MockLogicCatalog() {
    rules = new ArrayList<>();
  }

  public MockLogicCatalog(List<Rule> rules) {
    this.rules = rules;
  }

  @Override
  public LogicNetwork loadLogicNetwork() {
    LogicNetwork logicNetwork = new LogicNetwork();
    for (Rule r : rules) {
      logicNetwork.addRule(r);
    }
    return logicNetwork;
  }

  @Override
  public SemanticPropertyGraph getKnowledgeGraph() {
    return null;
  }

  @Override
  public Map<AbstractConnection, Set<String>> getConnections() {
    return null;
  }

  @Override
  public Set<Field> getDefaultNodeProperties() {
    return null;
  }

  @Override
  public Set<Field> getDefaultEdgeProperties() {
    return null;
  }
}
