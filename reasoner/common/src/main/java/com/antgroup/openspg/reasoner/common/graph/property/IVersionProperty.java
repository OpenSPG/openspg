/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common.graph.property;

import java.util.List;
import scala.Tuple2;

/**
 * @author kejian
 * @version IVersionProperty.java, v 0.1 2023年02月09日 8:15 PM kejian
 */
public interface IVersionProperty extends IProperty {
  /**
   * Get the value from properties
   *
   * @param key
   * @param version if null return newest value
   * @return
   */
  Object get(String key, Long version);

  /**
   * Get multi-version value from properties
   *
   * @param key
   * @param startVersion if null return all version
   * @param endVersion if null return all version
   * @return
   */
  List<Tuple2<Long, Object>> get(String key, Long startVersion, Long endVersion);

  /**
   * Get all version values of key
   *
   * @param key
   * @return
   */
  Object getVersionValue(String key);

  /**
   * Add or update a property
   *
   * @param key
   * @param value
   * @param version
   * @return
   */
  void put(String key, Object value, Long version);

  /**
   * Delete a property
   *
   * @param key
   * @param version
   */
  void remove(String key, Long version);
}
