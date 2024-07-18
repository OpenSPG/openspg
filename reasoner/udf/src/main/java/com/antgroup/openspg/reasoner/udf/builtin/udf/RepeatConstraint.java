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

import com.antgroup.openspg.reasoner.udf.builtin.CommonUtils;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import com.antgroup.openspg.reasoner.udf.rule.RuleRunner;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class RepeatConstraint {

  @UdfDefine(name = "repeat_constraint")
  public boolean constraint(
      List<Object> itemList, String preName, String curName, String express, Object context) {
    Map<String, Object> contextMap = CommonUtils.getParentContext(context);
    int processIndex = 1;
    if (StringUtils.isEmpty(preName) || !express.contains(preName)) {
      processIndex = 0;
    }
    for (int i = processIndex; i < itemList.size(); ++i) {
      Object pre = 0 == i ? null : itemList.get(i - 1);
      Object cur = itemList.get(i);
      Map<String, Object> subContext = new HashMap<>(contextMap);
      subContext.put(preName, CommonUtils.getRepeatItemContext(pre));
      subContext.put(curName, CommonUtils.getRepeatItemContext(cur));
      boolean rst = RuleRunner.getInstance().check(subContext, Lists.newArrayList(express), "");
      if (!rst) {
        return false;
      }
    }
    return true;
  }

  @UdfDefine(name = "repeat_constraint")
  public boolean constraint(List<Object> itemList, String preName, String curName, String express) {
    return constraint(itemList, preName, curName, express, null);
  }
}
