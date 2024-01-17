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

package com.antgroup.openspg.reasoner.udf.builtin.udaf;

import com.antgroup.openspg.reasoner.common.types.KTDouble$;
import com.antgroup.openspg.reasoner.common.types.KTInteger$;
import com.antgroup.openspg.reasoner.common.types.KTLong$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.model.BaseUdaf;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;

public class Sum {
  @UdfDefine(name = "sum", compatibleName = "Sum")
  public static class SumLong implements BaseUdaf {
    private long sum = 0L;

    @Override
    public KgType getInputRowType() {
      return KTLong$.MODULE$;
    }

    @Override
    public KgType getResultType() {
      return KTLong$.MODULE$;
    }

    @Override
    public void initialize(Object... params) {}

    @Override
    public void update(Object row) {
      sum += (long) row;
    }

    @Override
    public void merge(BaseUdaf function) {
      SumLong other = (SumLong) function;
      sum += other.sum;
    }

    @Override
    public Object evaluate() {
      return sum;
    }
  }

  @UdfDefine(name = "sum", compatibleName = "Sum")
  public static class SumInt implements BaseUdaf {
    private int sum = 0;

    @Override
    public KgType getInputRowType() {
      return KTInteger$.MODULE$;
    }

    @Override
    public KgType getResultType() {
      return KTInteger$.MODULE$;
    }

    @Override
    public void initialize(Object... params) {}

    @Override
    public void update(Object row) {
      sum += (int) row;
    }

    @Override
    public void merge(BaseUdaf function) {
      SumInt other = (SumInt) function;
      sum += other.sum;
    }

    @Override
    public Object evaluate() {
      return sum;
    }
  }

  @UdfDefine(name = "sum", compatibleName = "Sum")
  public static class SumDouble implements BaseUdaf {
    private double sum = 0.0;

    @Override
    public KgType getInputRowType() {
      return KTDouble$.MODULE$;
    }

    @Override
    public KgType getResultType() {
      return KTDouble$.MODULE$;
    }

    @Override
    public void initialize(Object... params) {}

    @Override
    public void update(Object row) {
      sum += (double) row;
    }

    @Override
    public void merge(BaseUdaf function) {
      SumDouble other = (SumDouble) function;
      sum += other.sum;
    }

    @Override
    public Object evaluate() {
      return sum;
    }
  }

  @UdfDefine(name = "sum", compatibleName = "Sum")
  public static class SumString implements BaseUdaf {
    private double sum = 0.0;

    @Override
    public KgType getInputRowType() {
      return KTString$.MODULE$;
    }

    @Override
    public KgType getResultType() {
      return KTDouble$.MODULE$;
    }

    @Override
    public void initialize(Object... params) {}

    @Override
    public void update(Object row) {
      sum += Double.parseDouble(String.valueOf(row));
    }

    @Override
    public void merge(BaseUdaf function) {
      SumLong other = (SumLong) function;
      sum += other.sum;
    }

    @Override
    public Object evaluate() {
      return sum;
    }
  }
}
