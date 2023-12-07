/*
 * Copyright 2023 Ant Group CO., Ltd.
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
