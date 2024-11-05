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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

@Getter
public class GraphLPGRecordStruct extends BaseLPGRecordStruct {

  private final List<VertexRecord> vertices;

  private final List<EdgeRecord> edges;

  public GraphLPGRecordStruct() {
    this(new ArrayList<>(), new ArrayList<>());
  }

  public GraphLPGRecordStruct(List<VertexRecord> vertices, List<EdgeRecord> edges) {
    super(LPGRecordStructEnum.GRAPH);
    this.edges = edges;
    this.vertices = vertices;
  }

  public void addVertexRecord(VertexRecord vertexRecord) {
    vertices.add(vertexRecord);
  }

  public void addEdgeRecord(EdgeRecord edgeRecord) {
    edges.add(edgeRecord);
  }

  public boolean isEmpty() {
    return CollectionUtils.isEmpty(vertices) && CollectionUtils.isEmpty(edges);
  }

  /**
   * Add all record from the other graph lpg record struct.
   *
   * @param other the other graph lpg record struct
   */
  public void addAll(GraphLPGRecordStruct other) {
    if (other == null || other.isEmpty()) {
      return;
    }
    // add vertex record if absent.
    if (CollectionUtils.isNotEmpty(other.getVertices())) {
      Set<String> uniqueStringOfVertices =
          this.vertices.stream()
              .map(VertexRecord::generateUniqueString)
              .collect(Collectors.toSet());
      for (VertexRecord vertexRecordToAdd : other.getVertices()) {
        String uniqueString = vertexRecordToAdd.generateUniqueString();
        if (!uniqueStringOfVertices.contains(uniqueString)) {
          this.vertices.add(vertexRecordToAdd);
          uniqueStringOfVertices.add(uniqueString);
        }
      }
    }
    // add edge record if absent
    if (CollectionUtils.isNotEmpty(other.getEdges())) {
      Set<String> uniqueStringOfEdges =
          this.edges.stream().map(EdgeRecord::generateUniqueString).collect(Collectors.toSet());
      for (EdgeRecord edgeRecordToAdd : other.getEdges()) {
        String uniqueString = edgeRecordToAdd.generateUniqueString();
        if (!uniqueStringOfEdges.contains(uniqueString)) {
          this.edges.add(edgeRecordToAdd);
          uniqueStringOfEdges.add(uniqueString);
        }
      }
    }
  }
}
