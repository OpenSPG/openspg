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

public class Entity<K> implements Element {
  private K id;
  private String type;

  public Entity() {}

  public Entity(K id, String type) {
    this.id = id;
    this.type = type;
  }

  /**
   * Getter method for property <tt>id</tt>.
   *
   * @return property value of id
   */
  public K getId() {
    return id;
  }

  /**
   * Setter method for property <tt>id</tt>.
   *
   * @param id value to be assigned to property id
   */
  public void setId(K id) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Entity)) {
      return false;
    }
    Entity<?> entity = (Entity<?>) o;
    return Objects.equals(id, entity.id) && Objects.equals(type, entity.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type);
  }
}
