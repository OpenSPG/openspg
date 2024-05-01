package com.antgroup.openspg.reasoner.thinker.logic;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.rule.ClauseEntry;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class LogicNetwork {
  private Map<String, Rule> ruleMap;
  private Map<Element, Map<Element, Rule>> forwardRules;
  private Map<Element, Map<List<Element>, Rule>> backwardRules;

  public LogicNetwork() {
    this.forwardRules = new HashMap<>();
    this.backwardRules = new HashMap<>();
    this.ruleMap = new HashMap<>();
  }

  public void addRule(Rule rule) {
    if (!ruleMap.containsKey(rule.getName())) {
      ruleMap.put(rule.getName(), rule);
    }
    for (ClauseEntry body : rule.getBody()) {
      Map<Element, Rule> rules =
          forwardRules.computeIfAbsent(body.toElement(), (key) -> new HashMap<>());
      rules.put(rule.getHead().toElement(), rule);
    }
    Map<List<Element>, Rule> rules =
        backwardRules.computeIfAbsent(rule.getHead().toElement(), (key) -> new HashMap<>());
    rules.put(
        rule.getBody().stream().map(ClauseEntry::toElement).collect(Collectors.toList()), rule);
  }

  public Collection<Rule> getForwardRules(Element e) {
    Set<Rule> rules = new HashSet<>();
    for (Map.Entry<Element, Map<Element, Rule>> entry : forwardRules.entrySet()) {
      if (entry.getKey().matches(e)) {
        rules.addAll(entry.getValue().values());
      }
    }
    return rules;
  }

  public Collection<Rule> getBackwardRules(Element triple) {
    Set<Rule> rules = new HashSet<>();
    for (Map.Entry<Element, Map<List<Element>, Rule>> entry : backwardRules.entrySet()) {
      if (triple.matches(entry.getKey())) {
        rules.addAll(entry.getValue().values());
      }
    }
    return rules;
  }
}
