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

package com.antgroup.openspg.reasoner.graphstate;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MockSourceData extends LoadSourceData {
  /*
   *          vs1
   *         /  \
   *       vt1   vt2
   */

  @Override
  public List<IVertex<String, IProperty>> genVertexList() {

    return Lists.newArrayList(
        constructionVersionVertex("vs1", "Student", 0),
        constructionVertex("vt1", "Teacher"),
        constructionVertex("vt2", "Teacher"));
  }

  @Override
  public List<IEdge<String, IProperty>> genEdgeList() {
    return Lists.newArrayList(
        constructionVersionEdge("vs1", "STEdge", "vt1", 0),
        constructionEdge("vs1", "STEdge", "vt2"));
  }

  /**
   * Load alias to vertex map from source data
   *
   * @return
   */
  @Override
  public Map<String, Set<IVertex<IVertexId, IProperty>>> loadAlias2Vertex() {
    return null;
  }

  /**
   * Load alias to edge map from source data
   *
   * @return
   */
  @Override
  public Map<String, Set<IEdge<IVertexId, IProperty>>> loadAlias2Edge() {
    return null;
  }

  /**
   * Load source data schema
   *
   * @return
   */
  @Override
  public Pattern loadPattern() {
    return null;
  }
}
