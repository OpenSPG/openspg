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

/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.udf.builtin.udaf;

import com.antgroup.openspg.reasoner.common.types.KTDouble$;
import com.antgroup.openspg.reasoner.common.types.KTInteger$;
import com.antgroup.openspg.reasoner.common.types.KTLong$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.model.BaseUdaf;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;

public class Min {

  @UdfDefine(name = "min", compatibleName = "Min")
  public static class MinInt extends BaseMin<Integer> {
    @Override
    public KgType getInputRowType() {
      return KTInteger$.MODULE$;
    }

    @Override
    public KgType getResultType() {
      return KTInteger$.MODULE$;
    }
  }

  @UdfDefine(name = "min", compatibleName = "Min")
  public static class MinLong extends BaseMin<Long> {
    @Override
    public KgType getInputRowType() {
      return KTLong$.MODULE$;
    }

    @Override
    public KgType getResultType() {
      return KTLong$.MODULE$;
    }
  }

  @UdfDefine(name = "min", compatibleName = "Min")
  public static class MinDouble extends BaseMin<Double> {
    @Override
    public KgType getInputRowType() {
      return KTDouble$.MODULE$;
    }

    @Override
    public KgType getResultType() {
      return KTDouble$.MODULE$;
    }
  }

  @UdfDefine(name = "min", compatibleName = "Min")
  public static class MinString extends BaseMin<String> {
    @Override
    public KgType getInputRowType() {
      return KTString$.MODULE$;
    }

    @Override
    public KgType getResultType() {
      return KTString$.MODULE$;
    }
  }

  private abstract static class BaseMin<T extends Comparable<T>> implements BaseUdaf {
    private T min = null;

    @Override
    public void initialize(Object... params) {}

    @Override
    public void update(Object row) {
      T comparable = (T) row;
      if (null == min) {
        min = comparable;
        return;
      }
      if (min.compareTo(comparable) > 0) {
        min = comparable;
      }
    }

    @Override
    public void merge(BaseUdaf function) {
      BaseMin<T> other = (BaseMin<T>) function;
      if (min.compareTo(other.min) > 0) {
        min = other.min;
      }
    }

    @Override
    public T evaluate() {
      return min;
    }
  }
}
