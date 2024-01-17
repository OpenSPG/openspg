/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.reasoner.udf.builtin;

import com.antgroup.openspg.reasoner.common.graph.edge.impl.Edge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.Vertex;
import java.util.HashMap;
import java.util.Map;

public class CommonUtils {

  /** get repeat item context */
  public static Map<String, Object> getRepeatItemContext(Object item) {
    Map<String, Object> result = new HashMap<>();
    if (null == item) {
      return result;
    }
    IProperty property = null;
    if (item instanceof Edge) {
      Edge<IVertexId, IProperty> edge = (Edge<IVertexId, IProperty>) item;
      property = edge.getValue();
    } else if (item instanceof Vertex) {
      Vertex<IVertexId, IProperty> vertex = (Vertex<IVertexId, IProperty>) item;
      property = vertex.getValue();
    }
    if (null != property) {
      for (String key : property.getKeySet()) {
        result.put(key, property.get(key));
      }
    }
    return result;
  }
}
