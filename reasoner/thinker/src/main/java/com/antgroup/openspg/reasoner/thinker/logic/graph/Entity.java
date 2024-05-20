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
public class Entity extends Element {
  private String id;
  private String type;
  private String alias;

  public Entity() {}

  public Entity(String id, String type) {
    this.id = id;
    this.type = type;
  }

  public Entity(String id, String type, String alias) {
    this.id = id;
    this.type = type;
    this.alias = alias;
  }

  /**
   * Getter method for property <tt>id</tt>.
   *
   * @return property value of id
   */
  public String getId() {
    return id;
  }

  /**
   * Setter method for property <tt>id</tt>.
   *
   * @param id value to be assigned to property id
   */
  public void setId(String id) {
    this.id = id;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Entity)) {
      return false;
    }
    Entity entity = (Entity) o;
    return Objects.equals(id, entity.id)
        && Objects.equals(type, entity.type)
        && Objects.equals(alias, entity.alias);
  }

  public boolean matches(Element other) {
    if (other == null) {
      return false;
    }
    if (other instanceof Node) {
      return Objects.equals(type, ((Node) other).getType());
    }
    if (other instanceof Entity) {
      return Objects.equals(type, ((Entity) other).getType());
    }
    return equals(other);
  }

  @Override
  public String alias() {
    return this.alias;
  }

  @Override
  public Element cleanAlias() {
    return new Entity(this.id, this.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type, alias);
  }

  @Override
  public Element bind(Element pattern) {
    if (pattern instanceof Entity || pattern instanceof Node) {
      return new Entity(this.id, this.type, pattern.alias());
    } else {
      return this;
    }
  }

  @Override
  public String shortString() {
    StringBuilder sb = new StringBuilder();
    sb.append(type).append("/").append(id);
    return sb.toString();
  }
}
