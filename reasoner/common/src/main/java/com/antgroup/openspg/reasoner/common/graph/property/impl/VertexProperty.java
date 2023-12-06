/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.reasoner.common.graph.property.impl;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chengqiang.cq
 * @version $Id: MapProperty.java, v 0.1 2023-02-01 11:22 chengqiang.cq Exp $$
 */
public class VertexProperty implements IProperty {
  private final Map<String, Object> props;

  public VertexProperty() {
    this.props = new HashMap<>();
  }

  public VertexProperty(Map<String, Object> props) {
    this.props = props;
  }

  @Override
  public Object get(String key) {
    return props.get(key);
  }

  @Override
  public void put(String key, Object value) {
    props.put(key, value);
  }

  @Override
  public void remove(String key) {
    props.remove(key);
  }

  @Override
  public boolean isKeyExist(String key) {
    return props.containsKey(key);
  }

  @Override
  public Collection<String> getKeySet() {
    return props.keySet();
  }

  @Override
  public Collection<Object> getValues() {
    return props.values();
  }

  @Override
  public int getSize() {
    return props.size();
  }

  @Override
  public IProperty clone() {
    Map<String, Object> newProps = new HashMap<>(this.props);
    return new VertexProperty(newProps);
  }

  @Override
  public String toString() {
    return JSON.toJSONString(props);
  }
}
