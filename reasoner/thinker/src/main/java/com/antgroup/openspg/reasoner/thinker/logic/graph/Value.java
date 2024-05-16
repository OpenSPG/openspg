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

package com.antgroup.openspg.reasoner.thinker.logic.graph;

import lombok.Data;

@Data
public class Value extends Element {
  private Object val;
  private String alias;

  public Value() {}

  public Value(Object val) {
    this.val = val;
  }

  public Value(Object val, String alias) {
    this.val = val;
    this.alias = alias;
  }

  @Override
  public String alias() {
    return this.alias;
  }

  public boolean matches(Element other) {
    if (other != null && other instanceof Value) {
      return true;
    }
    return false;
  }

  @Override
  public Element bind(Element pattern) {
    if (pattern instanceof Value) {
      return new Value(val, pattern.alias());
    } else {
      return this;
    }
  }

  /**
   * Getter method for property <tt>alias</tt>.
   *
   * @return property value of alias
   */
  public String getAlias() {
    return alias;
  }

  /**
   * Setter method for property <tt>alias</tt>.
   *
   * @param alias value to be assigned to property alias
   */
  public void setAlias(String alias) {
    this.alias = alias;
  }

  /**
   * Getter method for property <tt>val</tt>.
   *
   * @return property value of val
   */
  public Object getVal() {
    return val;
  }

  /**
   * Setter method for property <tt>val</tt>.
   *
   * @param val value to be assigned to property val
   */
  public void setVal(Object val) {
    this.val = val;
  }
}
