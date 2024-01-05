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

package com.antgroup.openspg.reasoner.udf.builtin.udf;

import com.antgroup.openspg.reasoner.udf.model.UdfDefine;

public class Abs {

  /**
   * Calculate the absolute value of a number
   *
   * @param obj
   * @return
   */
  @UdfDefine(name = "abs", compatibleName = "Abs")
  public Object abs(Object obj) {
    if (!(obj instanceof Number)) {
      return null;
    }
    if (obj instanceof Integer) {
      return Math.abs(Integer.parseInt(obj.toString()));
    }
    if (obj instanceof Long) {
      return Math.abs(Long.parseLong(obj.toString()));
    }
    if (obj instanceof Float) {
      return Math.abs(Float.parseFloat(obj.toString()));
    }
    if (obj instanceof Double) {
      return Math.abs(Double.parseDouble(obj.toString()));
    }
    return null;
  }
}
