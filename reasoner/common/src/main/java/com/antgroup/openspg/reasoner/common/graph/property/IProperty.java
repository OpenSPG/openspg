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

import java.io.Serializable;
import java.util.Collection;

/**
 * @author chengqiang.cq
 * @version $Id: IProperty.java, v 0.1 2023-02-01 11:13 chengqiang.cq Exp $$
 */
public interface IProperty extends Serializable {
  /**
   * Get the value from properties
   *
   * @param key
   * @return
   */
  Object get(String key);

  /**
   * Add or update a property
   *
   * @param key
   * @return
   */
  void put(String key, Object value);

  /**
   * Delete a property
   *
   * @param key
   */
  void remove(String key);

  /**
   * Is properties contains key
   *
   * @param key
   * @return
   */
  boolean isKeyExist(String key);

  /**
   * Return all the property names.
   *
   * @return
   */
  Collection<String> getKeySet();

  /**
   * Return all the property values
   *
   * @return
   */
  Collection<Object> getValues();

  /**
   * Return the size of property
   *
   * @return
   */
  int getSize();

  /** clone property */
  IProperty clone();
}
