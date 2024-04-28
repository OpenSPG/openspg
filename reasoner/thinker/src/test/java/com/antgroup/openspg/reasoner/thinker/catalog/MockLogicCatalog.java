package com.antgroup.openspg.reasoner.thinker.catalog;

import com.antgroup.openspg.reasoner.lube.catalog.AbstractConnection;
import com.antgroup.openspg.reasoner.lube.catalog.SemanticPropertyGraph;
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field;
import com.antgroup.openspg.reasoner.thinker.logic.LogicNetwork;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;

public class MockLogicCatalog extends LogicCatalog {
  @Override
  public LogicNetwork loadLogicNetwork() {
    return null;
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
