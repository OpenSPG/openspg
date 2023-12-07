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


package com.antgroup.openspg.reasoner.common.graph.property.impl;

import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.IVersionProperty;
import com.antgroup.openspg.reasoner.common.utils.PropertyUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import scala.Tuple2;

/**
 * @author kejian
 * @version VertexVersionProperty.java, v 0.1 2023-02-10 11:30 AM kejian
 */
public class VertexVersionProperty implements IVersionProperty {
  private final Map<String, TreeMap<Long, Object>> props;

  /** default constructor */
  public VertexVersionProperty() {
    this.props = new HashMap<>();
  }

  /** constructor */
  public VertexVersionProperty(Map<String, TreeMap<Long, Object>> props) {
    this.props = props;
  }

  @Override
  public Object get(String key) {
    return get(key, null);
  }

  @Override
  public Object getVersionValue(String key) {
    return props.get(key);
  }

  @Override
  public void put(String key, Object value) {
    put(key, value, 0L);
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
    return props.values().stream().map(v -> (Object) v).collect(Collectors.toSet());
  }

  @Override
  public int getSize() {
    return props.size();
  }

  @Override
  public IProperty clone() {
    Map<String, TreeMap<Long, Object>> newProps = new HashMap<>();
    for (Map.Entry<String, TreeMap<Long, Object>> entry : this.props.entrySet()) {
      newProps.put(entry.getKey(), new TreeMap<>(entry.getValue()));
    }
    return new VertexVersionProperty(newProps);
  }

  @Override
  public Object get(String key, Long version) {
    TreeMap<Long, Object> versionValue = props.get(key);
    return PropertyUtil.getVersionValue(version, versionValue);
  }

  @Override
  public List<Tuple2<Long, Object>> get(String key, Long startVersion, Long endVersion) {
    List<Tuple2<Long, Object>> result = new ArrayList<>();
    TreeMap<Long, Object> versionValueMap = props.get(key);
    for (Map.Entry<Long, Object> entry : versionValueMap.entrySet()) {
      long version = entry.getKey();
      if (version > endVersion) {
        break;
      }
      if (version >= startVersion) {
        result.add(new Tuple2<>(entry.getKey(), entry.getValue()));
      }
    }
    return result;
  }

  @Override
  public void put(String key, Object value, Long version) {
    if (null == version) {
      if (null == value) {
        props.remove(key);
        return;
      }
      version = 0L;
    }
    TreeMap<Long, Object> sortedVersionMap = props.computeIfAbsent(key, k -> new TreeMap<>());
    sortedVersionMap.put(version, value);
  }

  @Override
  public void remove(String key, Long version) {
    if (null == version) {
      this.remove(key);
      return;
    }
    TreeMap<Long, Object> versionValueMap = props.get(key);
    if (null == versionValueMap) {
      return;
    }
    versionValueMap.remove(version);
    if (versionValueMap.isEmpty()) {
      this.remove(key);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    boolean first1 = true;
    for (Map.Entry<String, TreeMap<Long, Object>> entry : props.entrySet()) {
      if (first1) {
        first1 = false;
      } else {
        sb.append(",");
      }
      sb.append(entry.getKey()).append(":");
      sb.append("{");
      boolean first2 = true;
      for (Map.Entry<Long, Object> entry2 : entry.getValue().entrySet()) {
        if (first2) {
          first2 = false;
        } else {
          sb.append(",");
        }
        sb.append(entry2.getKey()).append(":").append(entry2.getValue());
      }
      sb.append("}");
    }
    sb.append("}");
    return sb.toString();
  }
}
