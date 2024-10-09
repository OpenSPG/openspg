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

package com.antgroup.openspg.reasoner.thinker.logic;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import com.antgroup.openspg.reasoner.thinker.logic.rule.ClauseEntry;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class LogicNetwork {
  private static final Logger logger = LoggerFactory.getLogger(LogicNetwork.class);

  private Map<Triple, Map<Triple, List<Rule>>> forwardRules;
  private Map<Triple, Map<List<Triple>, List<Rule>>> backwardRules;

  public LogicNetwork() {
    this.forwardRules = new HashMap<>();
    this.backwardRules = new HashMap<>();
  }

  public void addRule(Rule rule) {
    for (ClauseEntry body : rule.getBody()) {
      Map<Triple, List<Rule>> rules =
          forwardRules.computeIfAbsent(body.toTriple(), (key) -> new HashMap<>());
      List<Rule> rList =
          rules.computeIfAbsent(rule.getHead().toTriple(), (k) -> new LinkedList<>());
      rList.add(rule);
    }
    Map<List<Triple>, List<Rule>> rules =
        backwardRules.computeIfAbsent(rule.getHead().toTriple(), (key) -> new HashMap<>());
    List<Rule> rList =
        rules.computeIfAbsent(
            rule.getBody().stream().map(ClauseEntry::toTriple).collect(Collectors.toList()),
            (k) -> new LinkedList<>());
    rList.add(rule);
  }

  public Collection<Rule> getForwardRules(Triple e) {
    Set<Rule> rules = new HashSet<>();
    for (Map.Entry<Triple, Map<Triple, List<Rule>>> entry : forwardRules.entrySet()) {
      if (entry.getKey().matches(e)) {
        entry.getValue().values().stream().forEach(list -> rules.addAll(list));
      }
    }
    return rules;
  }

  public Collection<Rule> getBackwardRules(Triple triple) {
    Set<Rule> rules = new HashSet<>();
    for (Map.Entry<Triple, Map<List<Triple>, List<Rule>>> entry : backwardRules.entrySet()) {
      if (entry.getKey().matches(triple)) {
        entry.getValue().values().stream().forEach(list -> rules.addAll(list));
      }
    }
    logger.info("LogicNetwork getBackwardRules, pattern={}, rules={}", triple, rules.size());
    return rules;
  }
}
