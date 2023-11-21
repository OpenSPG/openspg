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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.struct;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import java.util.ArrayList;
import java.util.List;
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
}
