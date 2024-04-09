/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.thinker.logic.graph;

import java.util.Objects;
import lombok.Data;

@Data
public class Concept implements Element {
  String name;

  public Concept() {}

  public Concept(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Concept)) {
      return false;
    }
    Concept concept = (Concept) o;
    return Objects.equals(name, concept.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
