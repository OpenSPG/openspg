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

package com.antgroup.openspg.cloudext.interfaces.graphstore;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.EdgeTypeName;
import java.util.List;

/** Provides data manipulation service for <tt>LPG</tt>. */
public interface LPGDataManipulationService {

  /**
   * Batch upsert vertex by {@link VertexRecord}s which have the same type name.
   *
   * @param vertexTypeName type name of vertex
   * @param vertexRecords {@link VertexRecord}s with the same type name to upsert.
   */
  void upsertVertex(String vertexTypeName, List<VertexRecord> vertexRecords) throws Exception;

  /**
   * Batch delete vertex by {@link VertexRecord}s which have the same type name.
   *
   * @param vertexTypeName type name of vertex
   * @param vertexRecords {@link VertexRecord}s with the same type name to delete.
   */
  void deleteVertex(String vertexTypeName, List<VertexRecord> vertexRecords) throws Exception;

  /**
   * Batch upsert edge by {@link EdgeRecord}s which have the same {@link EdgeTypeName EdgeTypeName}.
   *
   * @param edgeTypeName type name of edge
   * @param edgeRecords {@link EdgeRecord}s with the same {@link EdgeTypeName EdgeTypeName} to
   *     upsert.
   */
  void upsertEdge(String edgeTypeName, List<EdgeRecord> edgeRecords) throws Exception;

  /**
   * Batch delete edge by {@link EdgeRecord}s which have the same {@link EdgeTypeName EdgeTypeName}.
   *
   * @param edgeTypeName type name of edge
   * @param edgeRecords {@link EdgeRecord}s with the same {@link EdgeTypeName EdgeTypeName} to
   *     delete.
   */
  void deleteEdge(String edgeTypeName, List<EdgeRecord> edgeRecords) throws Exception;
}
