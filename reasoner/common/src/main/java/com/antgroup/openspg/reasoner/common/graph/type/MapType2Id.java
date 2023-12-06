/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common.graph.type;

/**
 * @author kejian
 * @version Type2IdMapping.java, v 0.1 2023年03月27日 7:39 PM kejian
 */
public interface MapType2Id {

  /**
   * get string type by type id
   *
   * @param typeId
   * @return
   */
  String getTypeById(Long typeId);

  /**
   * get type id by string type
   *
   * @param type
   * @return
   */
  Long getIdByType(String type);
}
