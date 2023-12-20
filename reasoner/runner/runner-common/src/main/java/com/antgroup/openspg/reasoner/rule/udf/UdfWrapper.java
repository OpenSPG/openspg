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

package com.antgroup.openspg.reasoner.rule.udf;

import com.antgroup.openspg.reasoner.udf.model.RuntimeUdfMeta;
import com.ql.util.express.Operator;

public class UdfWrapper extends Operator {

  private final RuntimeUdfMeta runtimeUdfMeta;

  /**
   * udf wrapper for qlexpress
   *
   * @param runtimeUdfMeta
   */
  public UdfWrapper(RuntimeUdfMeta runtimeUdfMeta) {
    this.runtimeUdfMeta = runtimeUdfMeta;
  }

  @Override
  public Object executeInner(Object[] objects) throws Exception {
    return this.runtimeUdfMeta.invoke(objects);
  }

  @Override
  public String toString() {
    return "qlexpress udf " + this.runtimeUdfMeta.getName();
  }
}
