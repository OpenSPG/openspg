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

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.udf.impl.UdfMngImpl;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ContainsAny {

  public ContainsAny() {}

  /**
   * Contains any function. Return true if the first element of the input array (a string) contains
   * one or more of the remaining elements, otherwise return false. Such as: 1."adfvg", ["as, "gv"]
   * false 2."adfvg", ["a, "gv"] true 3.null, ["adfvg"] false 4."adfvg", null false 5."a", [] false
   *
   * @param toCheckedStr the string to be checked.
   * @param keywords keywords
   * @return the Result
   */
  @UdfDefine(name = "contains_any", compatibleName = "contains")
  public boolean containsAny(String toCheckedStr, String[] keywords) {
    if (StringUtils.isEmpty(toCheckedStr) || null == keywords || 0 == keywords.length) {
      return false;
    }

    int parametersLength = keywords.length;
    for (int i = 0; i < parametersLength; i++) {
      if (null != keywords[i] && toCheckedStr.contains(keywords[i])) {
        return true;
      }
    }

    return false;
  }

  /**
   * Contains any function. Return true if the first element of the input string contains one or
   * more of the remaining elements,
   *
   * @param toCheckedStr
   * @param keyword
   * @return
   */
  @UdfDefine(name = "contains_any", compatibleName = "contains")
  public boolean contains(String toCheckedStr, String keyword) {
    Map<String, Object> configMap = UdfMngImpl.sceneConfigMap.get();
    Boolean allowUDFThrowException =
        (Boolean) configMap.getOrDefault(Constants.ALLOW_UDF_EXCEPTION, false);
    if ((toCheckedStr == null || keyword == null) && allowUDFThrowException) {
      throw new RuntimeException("contains_any arguments is null");
    }
    if (StringUtils.isEmpty(toCheckedStr) || null == keyword) {
      return false;
    }
    return toCheckedStr.contains(keyword);
  }
}
