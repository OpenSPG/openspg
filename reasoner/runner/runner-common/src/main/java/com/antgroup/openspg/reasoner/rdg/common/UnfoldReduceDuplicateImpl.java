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

package com.antgroup.openspg.reasoner.rdg.common;

import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnfoldReduceDuplicateImpl implements KgGraphListProcess {
  private final Pattern schema;
  private final String edgeAlias;

  public UnfoldReduceDuplicateImpl(Pattern schema, String edgeAlias) {
    this.schema = schema;
    this.edgeAlias = edgeAlias;
  }

  @Override
  public List<KgGraph<IVertexId>> reduce(Collection<KgGraph<IVertexId>> kgGraphs) {
    Set<IEdge<IVertexId, IProperty>> pathEdgeSet = new HashSet<>();
    List<KgGraph<IVertexId>> result = new ArrayList<>();
    for (KgGraph<IVertexId> kgGraph : kgGraphs) {
      IEdge<IVertexId, IProperty> edge = kgGraph.getEdge(edgeAlias).get(0);
      if (pathEdgeSet.contains(edge)) {
        continue;
      }
      pathEdgeSet.add(edge);
      result.add(kgGraph);
    }
    return result;
  }
}
