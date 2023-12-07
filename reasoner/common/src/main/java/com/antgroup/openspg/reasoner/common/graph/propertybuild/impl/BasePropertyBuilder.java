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

/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common.graph.propertybuild.impl;

import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.IVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.EdgeProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.propertybuild.IPropertyBuilder;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author kejian
 * @version BasePropertyBuilder.java, v 0.1 2023-07-20 5:07 PM kejian
 */
public class BasePropertyBuilder implements IPropertyBuilder {
  /**
   * build vertex multi-version property
   *
   * @param vertexId
   * @param property
   * @return
   */
  @Override
  public IVersionProperty buildVertexProperty(
      IVertexId vertexId, Map<String, TreeMap<Long, Object>> property) {
    if (null == property) {
      property = new HashMap<>();
    }
    return new VertexVersionProperty(property);
  }

  /**
   * build edge property
   *
   * @param edgeType
   * @param property
   * @return
   */
  @Override
  public IProperty buildEdgeProperty(String edgeType, Map<String, Object> property) {
    if (null == property) {
      property = new HashMap<>();
    }
    return new EdgeProperty(property);
  }

  /** reset property */
  @Override
  public void reset() {}
}
