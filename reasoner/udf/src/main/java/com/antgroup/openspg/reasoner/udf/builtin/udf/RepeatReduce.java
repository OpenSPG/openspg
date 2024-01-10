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

import com.antgroup.openspg.reasoner.udf.builtin.CommonUtils;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepeatReduce {

  @UdfDefine(name = "repeat_reduce")
  public Object reduce(
      List<Object> itemList, Object defaultValue, String preName, String curName, String express) {
    Object preValue = defaultValue;
    for (int i = 0; i < itemList.size(); ++i) {
      Object cur = itemList.get(i);
      Map<String, Object> context = new HashMap<>();
      context.put(preName, preValue);
      context.put(curName, CommonUtils.getRepeatItemContext(cur));
      preValue =
          RuleRunner.getInstance().executeExpression(context, Lists.newArrayList(express), "");
    }
    return preValue;
  }
}
