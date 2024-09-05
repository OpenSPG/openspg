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

import java.util.Objects;
import lombok.Data;

@Data
public class Node extends Element {
  private String type;
  private String alias;

  private Node() {}

  public Node(String type) {
    this.type = type;
  }

  public Node(String type, String alias) {
    this.type = type;
    this.alias = alias;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Node)) {
      return false;
    }
    Node node = (Node) o;
    return Objects.equals(type, node.type) && Objects.equals(alias, node.alias);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }

  public boolean matches(Element other) {
    if (other == null) {
      return false;
    }
    if (other instanceof Any) {
      return true;
    }
    if (other instanceof Node) {
      if (type.equalsIgnoreCase("thing") || ((Node) other).getType().equalsIgnoreCase("thing")) {
        return true;
      } else {
        return Objects.equals(type, ((Node) other).getType());
      }
    }
    if (other instanceof Entity) {
      return Objects.equals(type, ((Entity) other).getType());
    }
    return equals(other);
  }

  @Override
  public Element bind(Element pattern) {
    if (pattern instanceof Entity) {
      return new Entity(((Entity) pattern).getId(), this.type, ((Entity) pattern).getAlias());
    } else if (pattern instanceof CombinationEntity) {
      Entity entity = ((CombinationEntity) pattern).getEntityList().get(0);
      return new Entity(entity.getId(), entity.getType(), entity.getAlias());
    } else if (pattern instanceof Node) {
      return new Node(this.type, ((Node) pattern).getAlias());
    } else {
      return this;
    }
  }

  @Override
  public String alias() {
    return this.alias;
  }

  @Override
  public Element cleanAlias() {
    return new Node(this.type);
  }

  /**
   * Getter method for property <tt>type</tt>.
   *
   * @return property value of type
   */
  public String getType() {
    return type;
  }

  /**
   * Setter method for property <tt>type</tt>.
   *
   * @param type value to be assigned to property type
   */
  public void setType(String type) {
    this.type = type;
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
