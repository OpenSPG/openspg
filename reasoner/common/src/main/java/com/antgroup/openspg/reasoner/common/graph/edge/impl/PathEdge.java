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

package com.antgroup.openspg.reasoner.common.graph.edge.impl;

import com.antgroup.openspg.reasoner.common.exception.IllegalArgumentException;
import com.antgroup.openspg.reasoner.common.exception.NotImplementedException;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import scala.Tuple2;

public class PathEdge<K, VV, EV> implements IEdge<K, EV> {
  private final List<Edge<K, EV>> edgeList;
  private final List<Vertex<K, VV>> vertexList;

  protected EV value = null;

  public PathEdge(Edge<K, EV> edge) {
    this.edgeList = Lists.newArrayList(edge);
    this.vertexList = null;
  }

  public PathEdge(PathEdge<K, VV, EV> pathEdge, Vertex<K, VV> vertex, Edge<K, EV> edge) {
    if (!pathEdge.getTargetId().equals(edge.getSourceId())) {
      throw new IllegalArgumentException(
          "edge can connect",
          "pathEdgeEnd=" + pathEdge.getTargetId() + ",edgeSource=" + edge.getSourceId(),
          "",
          null);
    }
    if (!vertex.getId().equals(pathEdge.getTargetId())) {
      throw new IllegalArgumentException(
          "edge can connect",
          "pathEdgeEnd=" + pathEdge.getTargetId() + ",vertexId=" + vertex.getId(),
          "",
          null);
    }
    this.edgeList = Lists.newArrayList(pathEdge.edgeList);
    this.edgeList.add(edge);
    if (CollectionUtils.isEmpty(pathEdge.vertexList)) {
      this.vertexList = Lists.newArrayList(vertex);
    } else {
      this.vertexList = Lists.newArrayList(pathEdge.vertexList);
      this.vertexList.add(vertex);
    }
  }

  private PathEdge(List<Edge<K, EV>> edgeList, List<Vertex<K, VV>> vertexList, EV value) {
    this.edgeList = edgeList;
    this.vertexList = vertexList;
    this.value = value;
  }

  /** check path edge have looped or not */
  public boolean haveLoop() {
    Set<K> idSet = new HashSet<>();
    K lastEdgeTargetId = null;
    for (Edge<K, EV> edge : edgeList) {
      if (null == lastEdgeTargetId) {
        idSet.add(edge.getSourceId());
        idSet.add(edge.getTargetId());
        lastEdgeTargetId = edge.getTargetId();
        continue;
      }
      if (!edge.getSourceId().equals(lastEdgeTargetId)) {
        throw new RuntimeException("edge list not connected");
      }
      if (idSet.contains(edge.getTargetId())) {
        return true;
      }
      lastEdgeTargetId = edge.getTargetId();
    }
    return false;
  }

  @Override
  public K getSourceId() {
    return edgeList.get(0).getSourceId();
  }

  @Override
  public void setSourceId(K sourceId) {
    Edge<K, EV> edge = edgeList.get(0);
    Edge<K, EV> newEdge = edge.clone();
    newEdge.setSourceId(sourceId);
    edgeList.set(0, newEdge);
  }

  @Override
  public K getTargetId() {
    return edgeList.get(edgeList.size() - 1).getTargetId();
  }

  @Override
  public void setTargetId(K targetId) {
    int lastIndex = edgeList.size() - 1;
    Edge<K, EV> edge = edgeList.get(lastIndex);
    Edge<K, EV> newEdge = edge.clone();
    newEdge.setTargetId(targetId);
    edgeList.set(lastIndex, newEdge);
  }

  @Override
  public void setValue(EV value) {
    this.value = value;
  }

  @Override
  public Direction getDirection() {
    return edgeList.get(0).getDirection();
  }

  @Override
  public void setDirection(Direction direction) {
    throw new NotImplementedException("PathEdge is immutable", null);
  }

  @Override
  public String getType() {
    return edgeList.get(0).getType();
  }

  @Override
  public void setType(String edgeType) {
    throw new NotImplementedException("PathEdge is immutable", null);
  }

  @Override
  public PathEdge<K, VV, EV> clone() {
    if (null == this.vertexList) {
      return new PathEdge<>(Lists.newArrayList(this.edgeList), null, this.value);
    }
    return new PathEdge<>(
        Lists.newArrayList(this.edgeList), Lists.newArrayList(this.vertexList), this.value);
  }

  /** servers repeat edge tail */
  public Tuple2<Edge<K, EV>, Vertex<K, VV>> seversTail() {
    return new Tuple2<>(
        this.edgeList.remove(this.edgeList.size() - 1),
        this.vertexList.remove(this.vertexList.size() - 1));
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof PathEdge)) {
      return false;
    }
    PathEdge<?, ?, ?> other = (PathEdge<?, ?, ?>) obj;
    List<Object> thisKeyObjList = this.getKeyObjList();
    List<Object> otherKeyObjList = other.getKeyObjList();
    if (thisKeyObjList.size() != otherKeyObjList.size()) {
      return false;
    }
    for (int i = 0; i < thisKeyObjList.size(); ++i) {
      if (!Objects.equals(thisKeyObjList.get(i), otherKeyObjList.get(i))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getKeyObjList().toArray());
  }

  private List<Object> getKeyObjList() {
    List<Object> keyObjList = new ArrayList<>(edgeList.size() * 5);
    for (Edge<K, EV> edge : edgeList) {
      keyObjList.add(edge.getSourceId());
      keyObjList.add(edge.getTargetId());
      keyObjList.add(edge.getVersion());
      keyObjList.add(edge.getDirection());
      keyObjList.add(edge.getType());
    }
    return keyObjList;
  }

  /**
   * Getter method for property <tt>edgeList</tt>.
   *
   * @return property value of edgeList
   */
  public List<Edge<K, EV>> getEdgeList() {
    return edgeList;
  }

  /**
   * Getter method for property <tt>vertexList</tt>.
   *
   * @return property value of vertexList
   */
  public List<Vertex<K, VV>> getVertexList() {
    return vertexList;
  }

  /**
   * Getter method for property <tt>value</tt>.
   *
   * @return property value of value
   */
  @Override
  public EV getValue() {
    return value;
  }
}
