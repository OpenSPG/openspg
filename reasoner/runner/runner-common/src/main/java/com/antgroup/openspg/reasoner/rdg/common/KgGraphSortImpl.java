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

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.kggraph.KgGraph;
import com.antgroup.openspg.reasoner.kggraph.impl.KgGraphSplitStaticParameters;
import com.antgroup.openspg.reasoner.lube.block.SortItem;
import com.antgroup.openspg.reasoner.lube.common.pattern.Pattern;
import com.antgroup.openspg.reasoner.lube.logical.NodeVar;
import com.antgroup.openspg.reasoner.lube.logical.Var;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class KgGraphSortImpl implements KgGraphListProcess {
  private final scala.collection.immutable.List<Var> groupKey;
  private final scala.collection.immutable.List<SortItem> sortItems;
  private final int limit;
  private final KgGraphSplitStaticParameters staticParameters;

  public KgGraphSortImpl(
      scala.collection.immutable.List<Var> groupKey,
      scala.collection.immutable.List<SortItem> sortItems,
      Pattern schema,
      int limit) {
    this.groupKey = groupKey;
    this.sortItems = sortItems;
    this.limit = limit;
    Set<String> splitVertexAliases = new HashSet<>();
    for (int i = 0; i < groupKey.size(); ++i) {
      Var var = groupKey.apply(i);
      if (var instanceof NodeVar) {
        splitVertexAliases.add(var.name());
      }
    }
    this.staticParameters = new KgGraphSplitStaticParameters(splitVertexAliases, schema);
  }

  @Override
  public List<KgGraph<IVertexId>> reduce(Collection<KgGraph<IVertexId>> values) {
    Map<GroupByKeyItem, TreeMap<KgGraphSortItem, List<KgGraph<IVertexId>>>> groupByMap =
        new HashMap<>();
    Iterator<KgGraph<IVertexId>> valuesIt = values.iterator();
    while (valuesIt.hasNext()) {
      KgGraph<IVertexId> graph = valuesIt.next();
      valuesIt.remove();
      Iterator<KgGraph<IVertexId>> it = graph.getPath(this.staticParameters, null);
      while (it.hasNext()) {
        KgGraph<IVertexId> kgGraph = it.next();
        if (null == kgGraph) {
          continue;
        }
        Object[] keys = RunnerUtil.getVarFromKgGraph(kgGraph, groupKey);
        TreeMap<KgGraphSortItem, List<KgGraph<IVertexId>>> sortedTreeMap =
            groupByMap.computeIfAbsent(new GroupByKeyItem(keys), k -> new TreeMap<>());
        KgGraphSortItem newItem = new KgGraphSortItem(this.sortItems, kgGraph);
        int allSize = 0;
        for (List<KgGraph<IVertexId>> kgGraphList : sortedTreeMap.values()) {
          allSize += kgGraphList.size();
        }
        if (allSize < limit) {
          List<KgGraph<IVertexId>> kgGraphList =
              sortedTreeMap.computeIfAbsent(newItem, k -> new ArrayList<>());
          kgGraphList.add(kgGraph);
        } else {
          // Exceeding the threshold, truncate or retain the minimum value
          Map.Entry<KgGraphSortItem, List<KgGraph<IVertexId>>> lastEntry =
              sortedTreeMap.lastEntry();
          int compareValue = newItem.compareTo(lastEntry.getKey());
          if (compareValue > 0) {
            continue;
          } else if (0 == compareValue) {
            lastEntry.getValue().add(kgGraph);
            continue;
          }
          List<KgGraph<IVertexId>> kgGraphList =
              sortedTreeMap.computeIfAbsent(newItem, k -> new ArrayList<>());
          kgGraphList.add(kgGraph);
          sortedTreeMap.remove(sortedTreeMap.lastKey());
        }
      }
    }

    List<KgGraph<IVertexId>> result = new ArrayList<>();
    for (TreeMap<KgGraphSortItem, List<KgGraph<IVertexId>>> mapValues : groupByMap.values()) {
      int count = 0;
      for (List<KgGraph<IVertexId>> kgGraphList : mapValues.values()) {
        if (count >= limit) {
          break;
        } else if (kgGraphList.size() + count > limit) {
          TreeSet<KgGraphSortItem> sortItemTreeSet = new TreeSet<>();
          for (KgGraph<IVertexId> kgGraph : kgGraphList) {
            sortItemTreeSet.add(new KgGraphSortItem(this.sortItems, kgGraph));
          }
          for (int i = sortItemTreeSet.size(); i > (limit - count); --i) {
            sortItemTreeSet.remove(sortItemTreeSet.last());
          }
          kgGraphList = new ArrayList<>(sortItemTreeSet.size());
          for (KgGraphSortItem item : sortItemTreeSet) {
            kgGraphList.add(item.getKgGraph());
          }
        }
        count += kgGraphList.size();
        result.addAll(kgGraphList);
      }
    }
    return result;
  }
}
