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

package com.antgroup.openspg.reasoner.common.graph.edge;

import java.io.Serializable;

public interface IEdge<K, EV> extends Serializable {
  /**
   * Getter method for srcId of a edge
   *
   * @return
   */
  K getSourceId();

  /**
   * Setter method for srcId of a edge
   *
   * @return
   */
  void setSourceId(K sourceId);

  /**
   * Getter method for targetId of a edge
   *
   * @return
   */
  K getTargetId();

  /**
   * Setter method for targetId of a edge
   *
   * @return
   */
  void setTargetId(K targetId);

  /**
   * Getter method for value of a edge
   *
   * @return
   */
  default EV getValue() {
    return null;
  }

  /**
   * Setter method for value of a edge
   *
   * @return
   */
  void setValue(EV value);

  /**
   * Getter method for direction of a edge
   *
   * @return
   */
  Direction getDirection();

  /**
   * Setter method for direction of a edge
   *
   * @return
   */
  void setDirection(Direction direction);

  /**
   * Getter method for type of a edge
   *
   * @return
   */
  String getType();

  /**
   * Setter method for type of a edge
   *
   * @return
   */
  void setType(String edgeType);

  /**
   * Setter method for version of a edge
   *
   * @return
   */
  default void setVersion(long version) {}

  /**
   * Getter method for version of a edge
   *
   * @return
   */
  default Long getVersion() {
    return 0L;
  }

  IEdge<K, EV> clone();
}
