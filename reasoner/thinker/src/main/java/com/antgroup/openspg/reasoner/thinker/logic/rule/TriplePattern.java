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

package com.antgroup.openspg.reasoner.thinker.logic.rule;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import java.util.Objects;

public class TriplePattern implements ClauseEntry {
  private Triple triple;

  public TriplePattern() {}

  public TriplePattern(Triple triple) {
    this.triple = triple;
  }

  /**
   * Getter method for property <tt>triple</tt>.
   *
   * @return property value of triple
   */
  public Triple getTriple() {
    return triple;
  }

  /**
   * Setter method for property <tt>triple</tt>.
   *
   * @param triple value to be assigned to property triple
   */
  public void setTriple(Triple triple) {
    this.triple = triple;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TriplePattern)) {
      return false;
    }
    TriplePattern that = (TriplePattern) o;
    return Objects.equals(triple, that.triple);
  }

  @Override
  public int hashCode() {
    return Objects.hash(triple);
  }

  @Override
  public Triple toTriple() {
    return triple;
  }
}
