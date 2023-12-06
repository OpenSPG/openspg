/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.reasoner.common.graph.edge;

/**
 * @author chengqiang.cq
 * @version $Id: Direction.java, v 0.1 2023-02-01 11:45 chengqiang.cq Exp $$
 */
public enum Direction {

  /** OUT edge */
  OUT(0),

  /** IN edge */
  IN(1),

  /** Both direction edge */
  BOTH(2);

  private int value;

  Direction(int value) {
    this.value = value;
  }

  /**
   * Getter method for property <tt>value</tt>.
   *
   * @return property value of value
   */
  public int getValue() {
    return value;
  }

  /**
   * Setter method for property <tt>value</tt>.
   *
   * @param value value to be assigned to property value
   */
  public void setValue(int value) {
    this.value = value;
  }
}
