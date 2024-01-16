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

package com.antgroup.openspg.reasoner.udf.builtin.udf;

import com.antgroup.openspg.reasoner.udf.model.UdfDefine;

public class Cast {
  @UdfDefine(name = "cast_type", compatibleName = "Cast")
  public Object cast(Object op1, Object targetType) {
    Object opdata1Obj = op1;
    // 如果opdata1本身就是数字，也不要报错
    String opdata1 = String.valueOf(opdata1Obj);
    String castType = String.valueOf(targetType);
    if ("long".equalsIgnoreCase(castType)
        || "bigint".equalsIgnoreCase(castType)
        || "int".equalsIgnoreCase(castType)) {
      return Long.valueOf(opdata1);
    } else if ("double".equalsIgnoreCase(castType) || "float".equalsIgnoreCase(castType)) {
      return Double.valueOf(opdata1);
    } else {
      return opdata1;
    }
  }
}
