/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common.graph.type;

/**
 * @author kejian
 * @version MapType2IdFactory.java, v 0.1 2023年03月27日 8:01 PM kejian
 */
public class MapType2IdFactory {

  /**
   * Factory mode
   *
   * @return
   */
  public static MapType2Id getMapType2Id() {
    return MemMapType2Id.getInstance();
  }
}
