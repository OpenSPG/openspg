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
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class ContainsTag {

  @UdfDefine(name = "contains_tag", compatibleName = "ContainsTag")
  public boolean containsTag(String value, String tagList) {
    return containsTag(value, tagList, ",");
  }

  @UdfDefine(name = "contains_tag", compatibleName = "ContainsTag")
  public boolean containsTag(String value, String tagList, Character delimiter) {
    return containsTag(value, tagList, String.valueOf(delimiter));
  }

  @UdfDefine(name = "contains_tag", compatibleName = "ContainsTag")
  public boolean containsTag(String value, String tagList, String delimiter) {
    if (StringUtils.isEmpty(value) || StringUtils.isEmpty(tagList)) {
      return false;
    }
    List<String> tags = Lists.newArrayList(Splitter.on(delimiter).split(tagList));
    Set<String> valueSet = Sets.newHashSet(Splitter.on(delimiter).split(value));
    for (String s : tags) {
      String tag = s.trim();
      if (valueSet.contains(tag)) {
        return true;
      }
    }
    return false;
  }
}
