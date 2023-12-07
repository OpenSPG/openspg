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


package com.antgroup.openspg.reasoner.common.graph.property;

import java.util.List;
import scala.Tuple2;

/**
 * @author kejian
 * @version IVersionProperty.java, v 0.1 2023-02-09 8:15 PM kejian
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
