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


package com.antgroup.openspg.reasoner.common.graph.vertex.impl;

import com.antgroup.openspg.reasoner.common.Utils;
import com.antgroup.openspg.reasoner.common.exception.IllegalArgumentException;
import com.antgroup.openspg.reasoner.common.graph.type.MapType2IdFactory;
import com.antgroup.openspg.reasoner.common.graph.vertex.IInternalIdGenerator;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.google.common.primitives.UnsignedBytes;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;

/**
 * @author donghai.ydh
 * @version VertexId.java, v 0.1 2023-03-15 15:12 donghai.ydh
 */
public class VertexId implements IVertexId {
  /**
   * vertex id consists of a type and an internal ID where the type is converted to a long type ID
   * through the catalog interface and the internal ID is already a long type Therefore, the vertex
   * id bytes is composed of two long-type binary numbers.
   */
  private final byte[] vertexIdBytes;

  /** create vertex id */
  public VertexId(long internalId, String type) {
    this.vertexIdBytes = generateIdBytes(internalId, type);
  }

  public VertexId(String bizId, String type) {
    this.vertexIdBytes = generateIdBytes(generateInternalId(bizId, type), type);
  }

  public VertexId(byte[] bytes) {
    this.vertexIdBytes = bytes;
  }

  private byte[] generateIdBytes(long internalId, String type) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES * 2);
    Long typeId = MapType2IdFactory.getMapType2Id().getIdByType(type);
    if (null == typeId) {
      throw new IllegalArgumentException("valid vertex type", type, "", null);
    }
    byteBuffer.putLong(typeId);
    byteBuffer.putLong(internalId);
    return byteBuffer.array();
  }

  /** get kgstate internal id */
  public long getInternalId() {
    ByteBuffer byteBuffer = ByteBuffer.wrap(vertexIdBytes, Long.BYTES, Long.BYTES);
    return byteBuffer.getLong();
  }

  /** get vertex type id */
  @Override
  public long getTypeId() {
    ByteBuffer byteBuffer = ByteBuffer.wrap(vertexIdBytes, 0, Long.BYTES);
    return byteBuffer.getLong();
  }

  /** get vertex type */
  @Override
  public String getType() {
    return MapType2IdFactory.getMapType2Id().getTypeById(getTypeId());
  }

  @Override
  public byte[] getBytes() {
    return this.vertexIdBytes;
  }

  @Override
  public String getByteBase64String() {
    return Base64.getEncoder().encodeToString(this.vertexIdBytes);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(vertexIdBytes);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof VertexId)) {
      return false;
    }
    VertexId that = (VertexId) obj;
    return Arrays.equals(this.vertexIdBytes, that.vertexIdBytes);
  }

  @Override
  public String toString() {
    return getType() + "_" + getInternalId();
  }

  @Override
  public int compareTo(IVertexId that) {
    Comparator<byte[]> comparator = UnsignedBytes.lexicographicalComparator();
    return comparator.compare(this.getBytes(), that.getBytes());
  }

  public static volatile IInternalIdGenerator internalIdGenerator = null;

  /** generate internal id */
  public static long generateInternalId(String bizId, String type) {
    if (null != internalIdGenerator) {
      return internalIdGenerator.gen(bizId, type);
    }
    // for mock graph
    byte[] bizBytes = bizId.getBytes(StandardCharsets.UTF_8);
    byte[] typeBytes = type.getBytes(StandardCharsets.UTF_8);
    ByteBuffer byteBuffer = ByteBuffer.allocate(bizBytes.length + typeBytes.length);
    byteBuffer.put(bizBytes);
    byteBuffer.put(typeBytes);
    byte[] allBytes = byteBuffer.array();
    return Utils.hash64(allBytes, allBytes.length);
  }
}
