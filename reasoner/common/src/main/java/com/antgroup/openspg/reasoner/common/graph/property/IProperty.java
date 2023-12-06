/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
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
