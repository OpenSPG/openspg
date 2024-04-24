package com.antgroup.openspg.reasoner.thinker.catalog;

import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.thinker.logic.LogicNetwork;

public abstract class LogicCatalog extends Catalog {
  private LogicNetwork logicNetwork;

  public void init() {
    super.init();
    logicNetwork = loadLogicNetwork();
  }

  public abstract LogicNetwork loadLogicNetwork();
}
