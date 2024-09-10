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

import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException;
import java.util.Objects;
import lombok.Data;

@Data
public class Predicate extends Element {
  public static final Predicate CONCLUDE = new Predicate("conclude");

  private String name;
  private String alias;

  public Predicate() {}

  public Predicate(String name) {
    this.name = name;
  }

  public Predicate(String name, String alias) {
    this.name = name;
    this.alias = alias;
  }

  /**
   * Getter method for property <tt>name</tt>.
   *
   * @return property value of name
   */
  public String getName() {
    return name;
  }

  /**
   * Setter method for property <tt>name</tt>.
   *
   * @param name value to be assigned to property name
   */
  public void setName(String name) {
    this.name = name;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Predicate)) {
      return false;
    }
    Predicate predicate = (Predicate) o;
    return Objects.equals(name, predicate.name) && Objects.equals(alias, predicate.alias);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, alias);
  }

  public boolean matches(Element other) {
    if (other == null) {
      return false;
    }
    if (other instanceof Predicate) {
      return Objects.equals(name, ((Predicate) other).getName());
    }
    return equals(other);
  }

  @Override
  public boolean canInstantiated() {
    return false;
  }

  public String alias() {
    return alias;
  }

  @Override
  public Element cleanAlias() {
    return new Predicate(this.name);
  }

  @Override
  public Element bind(Element pattern) {
    if (pattern instanceof Predicate) {
      return new Predicate(name, pattern.alias());
    } else {
      throw new UnsupportedOperationException("Triple cannot bind " + pattern.toString(), null);
    }
  }
}
