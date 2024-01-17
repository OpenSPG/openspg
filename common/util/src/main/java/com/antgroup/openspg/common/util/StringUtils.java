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

package com.antgroup.openspg.common.util;

import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.text.StringSubstitutor;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

  /**
   * 将object转化成string返回，常用于POJO对象未实现toString()场景，
   *
   * @param object 对象
   * @return 对象string表示
   */
  public static String toString(Object object) {
    if (object instanceof String) {
      return object.toString();
    }
    return ToStringBuilder.reflectionToString(object, ToStringStyle.SHORT_PREFIX_STYLE);
  }

  public static String dictFormat(Map<String, Object> vars, String template) {
    StringSubstitutor substitutor = new StringSubstitutor(vars, "${", "}");
    return substitutor.replace(template);
  }
}
