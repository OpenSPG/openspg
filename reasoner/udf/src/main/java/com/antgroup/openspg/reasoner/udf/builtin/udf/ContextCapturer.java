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
import java.util.HashMap;
import java.util.Map;

public class ContextCapturer {

  @UdfDefine(name = "context_capturer")
  public Object reduce(Object[] keyList, Object[] valueList) {
    Map<String, Object> context = new HashMap<>();
    for (int i = 0; i < keyList.length && i < valueList.length; ++i) {
      context.put(String.valueOf(keyList[i]), valueList[i]);
    }
    return context;
  }
}
