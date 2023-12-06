/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.reasoner.common.graph.edge;

import java.io.Serializable;

/**
 * @author chengqiang.cq
 * @version $Id: IVertex.java, v 0.1 2023-02-01 10:22 chengqiang.cq Exp $$
 */
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
