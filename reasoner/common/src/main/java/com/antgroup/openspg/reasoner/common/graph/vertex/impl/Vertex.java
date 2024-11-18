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

package com.antgroup.openspg.reasoner.common.graph.vertex.impl;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import java.util.Objects;

public class Vertex<K, VV> implements IVertex<K, VV> {
  protected K id;
  protected VV value;

  public Vertex() {}

  public Vertex(K id) {
    this.id = id;
  }

  public Vertex(K id, VV value) {
    this.id = id;
    this.value = value;
  }

  @Override
  public K getId() {
    return id;
  }

  @Override
  public void setId(K id) {
    this.id = id;
  }

  @Override
  public VV getValue() {
    return value;
  }

  @Override
  public void setValue(VV value) {
    this.value = value;
  }

  @Override
  public Vertex<K, VV> clone() {
    return new Vertex<>(id, value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Vertex)) {
      return false;
    }
    Vertex<?, ?> vertex = (Vertex<?, ?>) o;
    return Objects.equals(id, vertex.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Vertex(s=" + getId() + ",property=" + getValue() + ")";
  }
}
