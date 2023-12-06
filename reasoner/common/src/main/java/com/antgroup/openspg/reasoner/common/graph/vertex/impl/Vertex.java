/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.reasoner.common.graph.vertex.impl;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import java.util.Objects;

/**
 * @author chengqiang.cq
 * @version $Id: Vertex.java, v 0.1 2023-02-01 11:30 chengqiang.cq Exp $$
 */
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
