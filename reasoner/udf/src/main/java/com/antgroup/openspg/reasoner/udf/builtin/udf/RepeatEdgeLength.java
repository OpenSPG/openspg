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
import com.antgroup.openspg.reasoner.common.graph.edge.impl.PathEdge;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import java.util.List;
import java.util.Map;

public class RepeatEdgeLength {

  @UdfDefine(name = "repeat_edge_length")
  public int constraint(Object obj) {
    if (obj instanceof OptionalEdge) {
      return 0;
    } else if (obj instanceof PathEdge) {
      PathEdge pathEdge = (PathEdge) obj;
      return pathEdge.getEdgeList().size();
    } else if (obj instanceof Map) {
      Map<String, Object> objectMap = (Map<String, Object>) obj;
      if (objectMap.containsKey(Constants.OPTIONAL_EDGE_FLAG)) {
        return 0;
      }
      List edgeList = (List) objectMap.get("edges");
      return edgeList.size();
    }
    return 0;
  }
}
