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

public class Avg {

  /** avg double compute function */
  @UdfDefine(name = "avg", compatibleName = "Avg")
  public static class AvgDouble implements BaseUdaf {
    private double sum = 0.0;
    private int count = 0;

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
      sum += ((Number) row).doubleValue();
      count++;
    }

    @Override
    public void merge(BaseUdaf function) {
      AvgDouble other = (AvgDouble) function;
      this.sum += other.sum;
      this.count += other.count;
    }

    @Override
    public Object evaluate() {
      return this.sum / this.count;
    }
  }

  @UdfDefine(name = "avg", compatibleName = "Avg")
  public static class AvgInt extends AvgDouble {
    @Override
    public KgType getInputRowType() {
      return KTInteger$.MODULE$;
    }
  }

  @UdfDefine(name = "avg", compatibleName = "Avg")
  public static class AvgLong extends AvgDouble {
    @Override
    public KgType getInputRowType() {
      return KTLong$.MODULE$;
    }
  }

  /** avg string compute function */
  @UdfDefine(name = "avg", compatibleName = "Avg")
  public static class AvgString extends AvgDouble {
    @Override
    public KgType getInputRowType() {
      return KTString$.MODULE$;
    }
  }
}
