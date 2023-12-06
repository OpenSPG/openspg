/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common.graph.type;

import com.antgroup.openspg.reasoner.common.Utils;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kejian
 * @version MockMapType2Id.java, v 0.1 2023年03月27日 7:44 PM kejian
 */
public class MemMapType2Id implements MapType2Id, Serializable {

  private final Map<Long, String> ID_2_TYPE = new ConcurrentHashMap<>();

  private final Map<String, Long> TYPE_2_ID = new ConcurrentHashMap<>();

  private MemMapType2Id() {}

  private static volatile MemMapType2Id instance = null;

  /** 单例 */
  public static MapType2Id getInstance() {
    if (null == instance) {
      synchronized (MemMapType2Id.class) {
        if (null == instance) {
          instance = new MemMapType2Id();
        }
      }
    }
    return instance;
  }

  /**
   * get string type by type id
   *
   * @param typeId
   * @return
   */
  @Override
  public String getTypeById(Long typeId) {
    return ID_2_TYPE.get(typeId);
  }

  /**
   * get type id by string type
   *
   * @param type
   * @return
   */
  @Override
  public Long getIdByType(String type) {
    Long id = TYPE_2_ID.get(type);
    if (null != id) {
      return id;
    }
    id = generateTypeId(type);
    String oldType = getTypeById(id);
    if (null == oldType) {
      registerTypeMapping(type, id);
    } else if (!oldType.equals(type)) {
      // hash conflict
      throw new RuntimeException("type mapping conflict, " + type + " and " + oldType);
    }
    return id;
  }

  /** register type to typeId map */
  public void registerTypeMapping(String type, long id) {
    Preconditions.checkArgument(null != type, "type is empty");
    ID_2_TYPE.put(id, type);
    TYPE_2_ID.put(type, id);
  }

  private long generateTypeId(String type) {
    byte[] allBytes = type.getBytes(StandardCharsets.UTF_8);
    return Utils.hash64(allBytes, allBytes.length);
  }
}
