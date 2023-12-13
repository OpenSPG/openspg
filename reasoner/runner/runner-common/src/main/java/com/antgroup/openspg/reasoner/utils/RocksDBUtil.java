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

package com.antgroup.openspg.reasoner.utils;

import com.antgroup.openspg.reasoner.common.exception.IllegalArgumentException;
import com.antgroup.openspg.reasoner.common.graph.edge.Direction;
import com.antgroup.openspg.reasoner.common.graph.type.MapType2IdFactory;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import java.nio.ByteBuffer;
import lombok.extern.slf4j.Slf4j;
import scala.Tuple2;

@Slf4j(topic = "userlogger")
public class RocksDBUtil {
  private static final String PLACEHOLDER = "\01";

  public static final String VERTEX_FLAG = "V";

  public static final String EDGE_FLAG = "E";

  public static final Integer ROCKSDB_KEY_PREFIX_LENGTH = 18;

  /**
   * append window to rocksdb key prefix
   *
   * @param keyPrefix
   * @param window
   * @return
   */
  public static byte[] rocksdbKeyAppendWindow(byte[] keyPrefix, long window) {
    ByteBuffer buffer = ByteBuffer.allocate(keyPrefix.length + Long.BYTES);
    buffer.put(keyPrefix);
    buffer.putLong(window);
    return buffer.array();
  }

  /**
   * get window from rocksdb key
   *
   * @param key
   * @return
   */
  public static long getRocksdbKeyWindow(byte[] key) {
    ByteBuffer buffer = ByteBuffer.wrap(key);
    buffer.position(key.length - Long.BYTES);
    return buffer.getLong();
  }

  /**
   * construct vertex key on rocksdb V_w_id_fill_W
   *
   * @param vertexId
   * @return
   */
  public static byte[] buildRocksDBVertexKeyWithoutWindow(IVertexId vertexId) {
    ByteBuffer buffer = ByteBuffer.allocate(RocksDBUtil.ROCKSDB_KEY_PREFIX_LENGTH);
    buffer.put(VERTEX_FLAG.getBytes());
    buffer.putLong(vertexId.getTypeId());
    buffer.putLong(vertexId.getInternalId());
    buffer.put(PLACEHOLDER.getBytes());
    return buffer.array();
  }

  /**
   * construct edge key on rocksdb E_T_IN_vid_W
   *
   * @param edgeType
   * @param direction
   * @param vertexId
   * @return
   */
  public static byte[] buildRocksDBEdgeKeyWithoutWindow(
      String edgeType, Direction direction, IVertexId vertexId) {
    if (Direction.BOTH.equals(direction)) {
      throw new RuntimeException("Not allowed BOTH edge in RocksDB");
    }
    ByteBuffer buffer = ByteBuffer.allocate(RocksDBUtil.ROCKSDB_KEY_PREFIX_LENGTH);
    buffer.put(EDGE_FLAG.getBytes());
    Long edgeTypeId = MapType2IdFactory.getMapType2Id().getIdByType(edgeType);
    if (null == edgeTypeId) {
      throw new IllegalArgumentException("valid edge type", edgeType, "", null);
    }
    buffer.putLong(edgeTypeId);
    buffer.put(String.valueOf(direction.getValue()).getBytes());
    buffer.putLong(vertexId.getInternalId());
    return buffer.array();
  }

  public static Tuple2<String, IVertexId> splitVertexKey(byte[] vertexKey) {
    ByteBuffer vertex = ByteBuffer.wrap(vertexKey);
    byte[] prefix = new byte[1];
    vertex.get(prefix);
    String vertexFlag1 = String.valueOf((char) prefix[0]);
    byte[] typeAndId = new byte[16];
    vertex.get(typeAndId);

    return new Tuple2(vertexFlag1, IVertexId.from(typeAndId));
  }

  /**
   * Check whether the two vertex keys are the same point
   *
   * @param vertexKey1
   * @param vertexKey2
   * @return
   */
  public static Boolean checkSameVertexKey(byte[] vertexKey1, byte[] vertexKey2) {
    if (!checkIsVertex(vertexKey1) || !checkIsVertex(vertexKey2)) {
      return false;
    }
    IVertexId id1 = extractVertexId(vertexKey1);
    IVertexId id2 = extractVertexId(vertexKey2);
    return id1.equals(id2);
  }

  /**
   * extract vertexId from vertex key
   *
   * @param vertexKey
   * @return
   */
  public static IVertexId extractVertexId(byte[] vertexKey) {
    Tuple2<String, IVertexId> splitResult = splitVertexKey(vertexKey);
    return splitResult._2;
  }

  /**
   * check whether the vertexKey is a vertex
   *
   * @param vertexKey
   * @return
   */
  public static Boolean checkIsVertex(byte[] vertexKey) {
    Tuple2<String, IVertexId> splitResult = splitVertexKey(vertexKey);
    String prefix = splitResult._1;
    return VERTEX_FLAG.equals(prefix);
  }
}
