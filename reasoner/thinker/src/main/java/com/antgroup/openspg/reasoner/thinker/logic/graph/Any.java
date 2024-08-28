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
public class Any extends Element {
  private String alias;

  public Any() {}

  public Any(String alias) {
    this.alias = alias;
  }


  @Override
  public boolean matches(Element other) {
    return other != null;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj instanceof Any) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public String alias() {
    return this.alias;
  }

  @Override
  public int hashCode() {
    return HASH_ANY;
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
}
