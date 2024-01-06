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

package com.antgroup.openspg.reasoner.common.graph.vertex;

import com.antgroup.openspg.reasoner.common.graph.vertex.impl.VertexBizId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.VertexId;
import java.io.Serializable;

public interface IVertexId extends Serializable, Comparable<IVertexId> {
  static IVertexId from(long internalId, String type) {
    return new VertexId(internalId, type);
  }

  static IVertexId from(String bizId, String type) {
    return new VertexBizId(bizId, type);
  }

  static IVertexId from(byte[] bytes) {
    if (bytes.length != Long.BYTES * 2) {
      throw new RuntimeException("vertex id must be 16 bytes");
    }
    return new VertexId(bytes);
  }

  /** get kgstate internal id */
  long getInternalId();

  /** get vertex type id */
  long getTypeId();

  /** get vertex type */
  String getType();

  /** get vertex id bytes */
  byte[] getBytes();

  /** get vertex id base 64 string */
  String getByteBase64String();
}
