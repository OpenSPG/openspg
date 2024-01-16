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
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphSplitStaticParameters;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;

public class GroupByKgGraphImpl implements Serializable {

  private static final long serialVersionUID = 5982445397315165348L;
  protected final List<String> byAliasList;
  protected final Set<String> byAliasSet;
  protected final Pattern schema;
  protected final Long limit;

  private final KgGraphSplitStaticParameters staticParameters;

  private final KgGraphListProcess kgGraphListProcess;

  /** impl */
  public GroupByKgGraphImpl(
      List<String> byAliasList, KgGraphListProcess kgGraphListProcess, Pattern schema, Long limit) {
    this.byAliasList = byAliasList;
    this.byAliasSet = Sets.newHashSet(byAliasList);
    this.schema = schema;
    this.limit = limit;
    this.staticParameters = new KgGraphSplitStaticParameters(this.byAliasSet, this.schema);
    this.kgGraphListProcess = kgGraphListProcess;
  }

  /** return list */
  public List<KgGraph<IVertexId>> groupReduce(Collection<KgGraph<IVertexId>> sameRootKgGraphList) {
    if (CollectionUtils.isEmpty(this.byAliasList)) {
      return doReduce(sameRootKgGraphList);
    }
    return doSplitThenReduce(sameRootKgGraphList);
  }

  private List<KgGraph<IVertexId>> doReduce(Collection<KgGraph<IVertexId>> sameRootKgGraphList) {
    return new ArrayList<>(this.kgGraphListProcess.reduce(sameRootKgGraphList));
  }

  private List<KgGraph<IVertexId>> doSplitThenReduce(Collection<KgGraph<IVertexId>> values) {
    Map<ByKey, List<KgGraph<IVertexId>>> kgGraphMap = new HashMap<>();
    for (KgGraph<IVertexId> value : values) {
      List<KgGraph<IVertexId>> kgGraphList =
          value.split(byAliasSet, schema, staticParameters, null, limit);
      for (KgGraph<IVertexId> kgGraph : kgGraphList) {
        ByKey byKey = getByKey(kgGraph);
        List<KgGraph<IVertexId>> existKgGraphList =
            kgGraphMap.computeIfAbsent(byKey, k -> new ArrayList<>());
        existKgGraphList.add(kgGraph);
      }
    }
    List<KgGraph<IVertexId>> result = new ArrayList<>(kgGraphMap.size());
    for (List<KgGraph<IVertexId>> kgGraphList : kgGraphMap.values()) {
      result.addAll(this.kgGraphListProcess.reduce(kgGraphList));
    }
    return result;
  }

  protected ByKey getByKey(KgGraph<IVertexId> kgGraph) {
    IVertexId[] vertexIds = new IVertexId[this.byAliasList.size()];
    for (int i = 0; i < this.byAliasList.size(); ++i) {
      String alias = this.byAliasList.get(i);
      vertexIds[i] = kgGraph.getVertex(alias).get(0).getId();
    }
    return new ByKey(vertexIds);
  }

  /** vertex id array */
  public static class ByKey {
    protected final IVertexId[] vertexIds;

    /** init */
    public ByKey(IVertexId[] vertexIds) {
      this.vertexIds = vertexIds;
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(vertexIds);
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof ByKey)) {
        return false;
      }
      ByKey other = (ByKey) obj;
      return Arrays.equals(this.vertexIds, other.vertexIds);
    }
  }
}
