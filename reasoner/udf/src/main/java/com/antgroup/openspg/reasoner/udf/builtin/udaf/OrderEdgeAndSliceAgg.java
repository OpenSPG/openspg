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

/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.reasoner.udf.builtin.udaf;

import com.antgroup.openspg.reasoner.common.types.KTInteger$;
import com.antgroup.openspg.reasoner.common.types.KTLong$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.model.BaseUdaf;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "userlogger")
public class OrderEdgeAndSliceAgg {
  /** order edge and slice sum udf input with Long */
  @UdfDefine(name = "order_edge_and_slice_sum")
  public static class OrderEdgeAndSliceAggBase implements BaseUdaf {
    private String orderOp = null;
    private Integer limit = null;
    private List<Double> propertyList = new ArrayList<>();

    @Override
    public KgType getInputRowType() {
      return KTLong$.MODULE$;
    }

    @Override
    public KgType getResultType() {
      return KTLong$.MODULE$;
    }

    @Override
    public void initialize(Object... params) {
      orderOp = (String) params[0];
      limit = (Integer) params[1];
    }

    @Override
    public void update(Object row) {
      propertyList.add(Double.valueOf(row.toString()));
    }

    @Override
    public void merge(BaseUdaf function) {}

    @Override
    public Object evaluate() {
      Collections.sort(propertyList);
      if ("desc".equals(orderOp)) {
        Collections.reverse(propertyList);
      }
      Double result = 0.0;
      for (int i = 0; i < limit && i < propertyList.size(); i++) {
        result += propertyList.get(i);
      }
      return result;
    }
  }

  /** order edge and slice sum udf input with Int */
  @UdfDefine(name = "order_edge_and_slice_sum")
  public static class OrderEdgeAndSliceAggInt extends OrderEdgeAndSliceAggBase {
    @Override
    public KgType getInputRowType() {
      return KTInteger$.MODULE$;
    }
  }

  /** order edge and slice sum udf input with String */
  @UdfDefine(name = "order_edge_and_slice_sum")
  public static class OrderEdgeAndSliceAggString extends OrderEdgeAndSliceAggBase {
    @Override
    public KgType getInputRowType() {
      return KTString$.MODULE$;
    }
  }
}
