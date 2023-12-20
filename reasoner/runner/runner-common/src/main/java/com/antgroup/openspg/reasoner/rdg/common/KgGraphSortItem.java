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
import com.antgroup.openspg.reasoner.lube.common.expr.GetField;
import com.antgroup.openspg.reasoner.lube.common.expr.Ref;
import com.antgroup.openspg.reasoner.lube.common.expr.UnaryOpExpr;
import com.antgroup.openspg.reasoner.utils.RunnerUtil;

public class KgGraphSortItem implements Comparable<KgGraphSortItem> {
  private final scala.collection.immutable.List<SortItem> sortItems;
  private final KgGraph<IVertexId> kgGraph;

  public KgGraphSortItem(
      scala.collection.immutable.List<SortItem> sortItems, KgGraph<IVertexId> kgGraph) {
    this.sortItems = sortItems;
    this.kgGraph = kgGraph;
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
    Object[] values = new Object[this.sortItems.size()];
    for (int i = 0; i < this.sortItems.size(); ++i) {
      SortItem sortItem = this.sortItems.apply(i);
      UnaryOpExpr unaryOpExpr = (UnaryOpExpr) sortItem.expr();
      GetField name = (GetField) unaryOpExpr.name();
      Ref ref = (Ref) unaryOpExpr.arg();
      String alias = ref.refName();
      String propertyName = name.fieldName();
      values[i] = RunnerUtil.getProperty(this.kgGraph, alias, propertyName);
    }
    return values;
  }
}
