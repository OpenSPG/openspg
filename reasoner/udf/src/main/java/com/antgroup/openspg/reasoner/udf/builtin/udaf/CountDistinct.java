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

import com.antgroup.openspg.reasoner.common.types.KTLong$;
import com.antgroup.openspg.reasoner.common.types.KTObject$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.model.BaseUdaf;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import java.util.HashSet;
import java.util.Set;

@UdfDefine(name = "count_distinct", compatibleName = "CountDistinct,Count_Distinct")
public class CountDistinct implements BaseUdaf {
  private long count = 0;
  private Set<Object> visitedObjectSet;

  @Override
  public KgType getInputRowType() {
    return KTObject$.MODULE$;
  }

  @Override
  public KgType getResultType() {
    return KTLong$.MODULE$;
  }

  @Override
  public void initialize(Object... params) {
    this.count = 0;
    this.visitedObjectSet = new HashSet<>();
  }

  @Override
  public void update(Object row) {
    if (visitedObjectSet.contains(row)) {
      return;
    }
    visitedObjectSet.add(row);
    count++;
  }

  @Override
  public void merge(BaseUdaf function) {
    CountDistinct other = (CountDistinct) function;
    visitedObjectSet.addAll(other.visitedObjectSet);
    count = visitedObjectSet.size();
  }

  @Override
  public Long evaluate() {
    return count;
  }
}
