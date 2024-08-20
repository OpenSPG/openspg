/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.reasoner.thinker.catalog;

import com.antgroup.openspg.reasoner.lube.catalog.AbstractConnection;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.catalog.SemanticPropertyGraph;
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field;
import com.antgroup.openspg.reasoner.thinker.logic.LogicNetwork;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import java.util.ArrayList;
import java.util.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;

public class DefaultLogicCatalog extends LogicCatalog {
  private Catalog kgCatalog;
  private List<Rule> rules;

  private DefaultLogicCatalog() {
    rules = new ArrayList<>();
  }

  public DefaultLogicCatalog(List<Rule> rules, Catalog kgCatalog) {
    this.rules = rules;
    this.kgCatalog = kgCatalog;
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
    return kgCatalog.getKnowledgeGraph();
  }

  @Override
  public Map<AbstractConnection, Set<String>> getConnections() {
    return kgCatalog.getConnections();
  }

  @Override
  public Set<Field> getDefaultNodeProperties() {
    return kgCatalog.getDefaultNodeProperties();
  }

  @Override
  public Set<Field> getDefaultEdgeProperties() {
    return kgCatalog.getDefaultEdgeProperties();
  }
}
