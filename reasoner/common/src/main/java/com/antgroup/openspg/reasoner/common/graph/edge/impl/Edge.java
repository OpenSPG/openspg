/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.reasoner.common.graph.edge.impl;

import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import java.util.Objects;

/**
 * @author chengqiang.cq
 * @version $Id: Edge.java, v 0.1 2023-02-01 11:37 chengqiang.cq Exp $$
 */
public class Edge<K, EV> implements IEdge<K, EV> {

  protected K sourceId;
  protected K targetId;
  protected EV value;
  protected long version;
  protected Direction direction;
  protected String type;

  public Edge() {}

  public Edge(K srcId, K targetId) {
    this(srcId, targetId, null);
  }

  public Edge(K srcId, K targetId, EV value) {
    this(srcId, targetId, value, Direction.OUT);
  }

  public Edge(K srcId, K targetId, EV value, Direction type) {
    this.sourceId = srcId;
    this.targetId = targetId;
    this.value = value;
    this.direction = type;
  }

  public Edge(K srcId, K targetId, EV value, long version) {
    this(srcId, targetId, value, Direction.OUT);
    this.version = version;
  }

  public Edge(K srcId, K targetId, EV value, long version, Direction type) {
    this(srcId, targetId, value, type);
    this.version = version;
  }

  public Edge(K srcId, K targetId, EV value, long time, Direction type, String edgeType) {
    this(srcId, targetId, value, time, type);
    this.type = edgeType;
  }

  @Override
  public K getSourceId() {
    return sourceId;
  }

  @Override
  public void setSourceId(K srcId) {
    this.sourceId = srcId;
  }

  @Override
  public K getTargetId() {
    return targetId;
  }

  @Override
  public void setTargetId(K targetId) {
    this.targetId = targetId;
  }

  @Override
  public void setValue(EV value) {
    this.value = value;
  }

  @Override
  public Direction getDirection() {
    return direction;
  }

  @Override
  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(String edgeType) {
    this.type = edgeType;
  }

  @Override
  public EV getValue() {
    return this.value;
  }

  @Override
  public void setVersion(long version) {
    this.version = version;
  }

  @Override
  public Long getVersion() {
    return version;
  }

  @Override
  public Edge<K, EV> clone() {
    return new Edge<>(
        this.sourceId, this.targetId, this.value, this.version, this.direction, this.type);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Edge)) {
      return false;
    }
    Edge<?, ?> edge = (Edge<?, ?>) o;
    return version == edge.version
        && Objects.equals(sourceId, edge.sourceId)
        && Objects.equals(targetId, edge.targetId)
        && direction == edge.direction
        && Objects.equals(type, edge.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceId, targetId, version, direction, type);
  }

  @Override
  public String toString() {
    return "Edge(s="
        + getSourceId()
        + ",p="
        + getType()
        + ",o="
        + getTargetId()
        + ",direction="
        + direction
        + ",version="
        + version
        + ",property="
        + getValue()
        + ")";
  }
}
