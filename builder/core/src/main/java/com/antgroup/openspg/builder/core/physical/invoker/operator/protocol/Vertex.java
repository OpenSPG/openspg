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

package com.antgroup.openspg.builder.core.physical.invoker.operator.protocol;

import java.util.HashMap;
import java.util.Map;

/** Python operator entity */
public class Vertex {

  private String bizId;
  private String vertexType;
  private Map<String, String> props;

  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>(3);
    map.put("bizId", bizId);
    map.put("vertexType", vertexType);
    map.put("props", props);
    return map;
  }

  public String getBizId() {
    return bizId;
  }

  public Vertex setBizId(String bizId) {
    this.bizId = bizId;
    return this;
  }

  public String getVertexType() {
    return vertexType;
  }

  public Vertex setVertexType(String vertexType) {
    this.vertexType = vertexType;
    return this;
  }

  public Map<String, String> getProps() {
    return props;
  }

  public Vertex setProps(Map<String, String> props) {
    this.props = props;
    return this;
  }
}
