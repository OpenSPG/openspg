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

package com.antgroup.openspg.reasoner.rdg.common;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphImpl;
import com.antgroup.openspg.reasoner.lube.common.pattern.Connection;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KgGraphRightJoinImpl implements Serializable {

  private final List<Connection> joinNoneEdgeOrder;

  public KgGraphRightJoinImpl(List<String> onAlias, Pattern leftSchema) {
    String leftAlias = onAlias.get(0);
    this.joinNoneEdgeOrder = RunnerUtil.getJoinNoneEdgeOrder(leftAlias, leftSchema);
  }

  public List<KgGraph<IVertexId>> join(Collection<KgGraph<IVertexId>> right) {
    List<KgGraph<IVertexId>> rst = new ArrayList<>();
    for (KgGraph<IVertexId> kgGraph : right) {
      RunnerUtil.kgGraphJoinNone((KgGraphImpl) kgGraph, this.joinNoneEdgeOrder);
      rst.add(kgGraph);
    }
    return rst;
  }
}
