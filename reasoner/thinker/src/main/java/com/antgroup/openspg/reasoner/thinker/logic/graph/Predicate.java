package com.antgroup.openspg.reasoner.thinker.logic.graph;

import lombok.Data;

@Data
public class Predicate implements Element {
  private String name;

  public Predicate() {}

  public Predicate(String name) {
    this.name = name;
  }
}
