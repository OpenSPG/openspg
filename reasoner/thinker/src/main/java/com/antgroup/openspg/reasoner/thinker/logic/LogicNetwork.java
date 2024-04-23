package com.antgroup.openspg.reasoner.thinker.logic;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import com.antgroup.openspg.reasoner.thinker.logic.rule.ClauseEntry;
import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class LogicNetwork {
  private Map<Element, Map<Element, Rule>> forwardRules;
  private Map<Element, Map<List<Element>, Rule>> backwardRules;

  public LogicNetwork() {
    this.forwardRules = new HashMap<>();
    this.backwardRules = new HashMap<>();
  }

  public void addRule(Rule rule) {
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
    if (forwardRules.containsKey(e)) {
      return forwardRules.get(e).values();
    } else {
      return new ArrayList<>();
    }
  }

  public Collection<Rule> getBackwardRules(Element e) {
    if (backwardRules.containsKey(e)) {
      return backwardRules.get(e).values();
    } else {
      return new ArrayList<>();
    }
  }
}
