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

import com.antgroup.openspg.reasoner.common.types.KTDouble$;
import com.antgroup.openspg.reasoner.common.types.KTInteger$;
import com.antgroup.openspg.reasoner.common.types.KTLong$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.model.BaseUdaf;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;

public class Max {

  @UdfDefine(name = "max", compatibleName = "Max")
  public static class MaxInt extends BaseMax<Integer> {
    @Override
    public KgType getInputRowType() {
      return KTInteger$.MODULE$;
    }

    @Override
    public KgType getResultType() {
      return KTInteger$.MODULE$;
    }
  }

  @UdfDefine(name = "max", compatibleName = "Max")
  public static class MaxLong extends BaseMax<Long> {
    @Override
    public KgType getInputRowType() {
      return KTLong$.MODULE$;
    }

    @Override
    public KgType getResultType() {
      return KTLong$.MODULE$;
    }
  }

  @UdfDefine(name = "max", compatibleName = "Max")
  public static class MaxDouble extends BaseMax<Double> {
    @Override
    public KgType getInputRowType() {
      return KTDouble$.MODULE$;
    }

    @Override
    public KgType getResultType() {
      return KTDouble$.MODULE$;
    }
  }

  @UdfDefine(name = "max", compatibleName = "Max")
  public static class MaxString extends BaseMax<String> {
    @Override
    public KgType getInputRowType() {
      return KTString$.MODULE$;
    }

    @Override
    public KgType getResultType() {
      return KTString$.MODULE$;
    }
  }

  private abstract static class BaseMax<T extends Comparable<T>> implements BaseUdaf {
    private T max = null;

    @Override
    public void initialize(Object... params) {}

    @Override
    public void update(Object row) {
      T comparable = (T) row;
      if (null == max) {
        max = comparable;
        return;
      }
      if (max.compareTo(comparable) < 0) {
        max = comparable;
      }
    }

    @Override
    public void merge(BaseUdaf function) {
      BaseMax<T> other = (BaseMax<T>) function;
      if (max.compareTo(other.max) < 0) {
        max = other.max;
      }
    }

    @Override
    public T evaluate() {
      return max;
    }
  }
}
