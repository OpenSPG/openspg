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
import com.antgroup.openspg.reasoner.lube.catalog.SemanticPropertyGraph;
import com.antgroup.openspg.reasoner.lube.catalog.struct.Field;
import com.antgroup.openspg.reasoner.thinker.SimplifyThinkerParser;
import com.antgroup.openspg.reasoner.thinker.logic.LogicNetwork;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import scala.collection.JavaConversions;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;

public class ResourceLogicCatalog extends LogicCatalog {
  private SimplifyThinkerParser parser;
  private String path;

  public ResourceLogicCatalog(String path) {
    this.path = path;
    this.parser = new SimplifyThinkerParser();
  }

  @Override
  public LogicNetwork loadLogicNetwork() {
    InputStream inputStream = this.getClass().getResourceAsStream(path);
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    StringBuilder sb = new StringBuilder();
    try {
      String line = null;
      while ((line = reader.readLine()) != null) {
        sb.append(line).append("\n");
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    List<Rule> rules = JavaConversions.seqAsJavaList(parser.parseSimplifyDsl(sb.toString(), null));
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
