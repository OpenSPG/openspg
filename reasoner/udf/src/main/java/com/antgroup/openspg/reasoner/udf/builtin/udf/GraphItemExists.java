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

package com.antgroup.openspg.reasoner.udf.builtin.udf;

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.OptionalEdge;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.MirrorVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.impl.NoneVertex;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import java.util.Map;

public class GraphItemExists {
  @UdfDefine(name = "exists")
  public boolean exists(Object obj) {
    if (obj instanceof MirrorVertex) {
      return false;
    } else if (obj instanceof NoneVertex) {
      return false;
    } else if (obj instanceof OptionalEdge) {
      return false;
    } else if (obj instanceof Map) {
      Map<String, Object> objectMap = (Map<String, Object>) obj;
      if (objectMap.containsKey(Constants.OPTIONAL_EDGE_FLAG)) {
        return false;
      } else if (objectMap.containsKey(Constants.NONE_VERTEX_FLAG)) {
        return false;
      } else if (objectMap.containsKey(Constants.MIRROR_VERTEX_FLAG)) {
        return false;
      }
    }
    return true;
  }
}
