/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common.graph.vertex;

import com.antgroup.openspg.reasoner.common.graph.vertex.impl.VertexId;
import java.io.Serializable;

/**
 * @author donghai.ydh
 * @version IVertexId.java, v 0.1 2023年03月15日 15:12 donghai.ydh
 */
public interface IVertexId extends Serializable, Comparable<IVertexId> {
  static IVertexId from(long internalId, String type) {
    return new VertexId(internalId, type);
  }

  static IVertexId from(String bizId, String type) {
    return new VertexId(bizId, type);
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
