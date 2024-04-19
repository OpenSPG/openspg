package com.antgroup.openspg.reasoner.thinker.logic.rule;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;

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
}
