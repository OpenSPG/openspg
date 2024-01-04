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

package com.antgroup.openspg.reasoner.udf.builtin.udaf;

import com.antgroup.openspg.reasoner.common.graph.edge.impl.OptionalEdge;
import com.antgroup.openspg.reasoner.common.graph.edge.impl.PathEdge;
import com.antgroup.openspg.reasoner.common.types.KTObject$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.model.BaseUdaf;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import org.apache.commons.collections4.CollectionUtils;

@UdfDefine(name = "keep_longest_path", compatibleName = "KeepLongestPath")
public class KeepLongestPath implements BaseUdaf {
  private Object longestPath = null;

  @Override
  public KgType getInputRowType() {
    return KTObject$.MODULE$;
  }

  @Override
  public KgType getResultType() {
    return KTObject$.MODULE$;
  }

  @Override
  public void initialize(Object... params) {}

  @Override
  public void update(Object row) {
    if (null == longestPath) {
      this.longestPath = row;
    } else {
      this.longestPath = longerPath(this.longestPath, row);
    }
  }

  private Object longerPath(Object o1, Object o2) {
    int len1 = 0;
    int len2 = 0;
    if (o1 instanceof PathEdge) {
      PathEdge pathEdge1 = (PathEdge) o1;
      if (CollectionUtils.isNotEmpty(pathEdge1.getEdgeList())) {
        len1 = pathEdge1.getEdgeList().size();
      }
    } else if (o1 instanceof OptionalEdge) {
      len1 = 0;
    }

    if (o2 instanceof PathEdge) {
      PathEdge pathEdge2 = (PathEdge) o2;
      if (CollectionUtils.isNotEmpty(pathEdge2.getEdgeList())) {
        len2 = pathEdge2.getEdgeList().size();
      }
    } else if (o1 instanceof OptionalEdge) {
      len2 = 0;
    }

    if (len1 >= len2) {
      return o1;
    }
    return o2;
  }

  @Override
  public void merge(BaseUdaf function) {
    this.longestPath = longerPath(this.longestPath, function);
  }

  @Override
  public Object evaluate() {
    return this.longestPath;
  }
}
