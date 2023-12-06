/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.reasoner.common.graph.vertex;

import java.io.Serializable;

/**
 * @author chengqiang.cq
 * @version $Id: IVertex.java, v 0.1 2023-02-01 10:22 chengqiang.cq Exp $$
 */
public interface IVertex<K, VV> extends Serializable {
  /**
   * Return the id of vertex
   *
   * @return
   */
  K getId();

  /**
   * Set id of a vertex
   *
   * @param id
   */
  void setId(K id);

  /**
   * Getter method for value of a vertex
   *
   * @return
   */
  VV getValue();

  /**
   * Setter method for value of a vertex
   *
   * @return
   */
  void setValue(VV value);

  /**
   * Getter method for version of a vertex
   *
   * @return
   */
  default Long getVersion() {
    return 0L;
  }

  /**
   * Setter method for version of a vertex
   *
   * @param version
   */
  default void setVersion(Long version) {};

  IVertex<K, VV> clone();
}
