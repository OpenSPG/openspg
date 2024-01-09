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
import com.antgroup.openspg.reasoner.lube.block.Desc;
import com.antgroup.openspg.reasoner.lube.block.SortItem;
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KgGraphSortItem implements Comparable<KgGraphSortItem> {
  private final scala.collection.immutable.List<SortItem> sortItems;
  private final List<List<String>> ruleList;
  private final KgGraph<IVertexId> kgGraph;
  private final String taskId;

  public KgGraphSortItem(
      scala.collection.immutable.List<SortItem> sortItems,
      List<List<String>> ruleList,
      KgGraph<IVertexId> kgGraph,
      String taskId) {
    this.sortItems = sortItems;
    this.ruleList = ruleList;
    this.kgGraph = kgGraph;
    this.taskId = taskId;
  }

  /**
   * Getter method for property <tt>kgGraph</tt>.
   *
   * @return property value of kgGraph
   */
  public KgGraph<IVertexId> getKgGraph() {
    return kgGraph;
  }

  @Override
  public int compareTo(KgGraphSortItem o) {
    Object[] thisValues = getValues();
    Object[] otherValues = o.getValues();

    for (int i = 0; i < this.sortItems.size(); ++i) {
      SortItem sortItem = sortItems.apply(i);
      boolean desc = sortItem instanceof Desc;

      Object v1 = thisValues[i];
      Object v2 = otherValues[i];

      int r = RunnerUtil.compareTwoObject(v1, v2);
      if (0 == r) {
        r = this.hashCode() - o.hashCode();
      }
      if (desc) {
        return -r;
      }
      return r;
    }
    return 0;
  }

  private Object[] getValues() {
    Map<String, Object> context = RunnerUtil.kgGraph2Context(new HashMap<>(), this.kgGraph);
    Object[] values = new Object[this.sortItems.size()];
    for (int i = 0; i < this.sortItems.size(); ++i) {
      List<String> ruleList = this.ruleList.get(i);
      Object value = RuleRunner.getInstance().executeExpression(context, ruleList, this.taskId);
      values[i] = value;
    }
    return values;
  }
}
