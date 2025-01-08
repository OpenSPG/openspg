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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.text.StringSubstitutor;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

  private static Pattern humpPattern = Pattern.compile("[A-Z]");

  public static final String UNDERLINE_SEPARATOR = "_";

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

  public static String humpToLine(String str) {
    Matcher matcher = humpPattern.matcher(str);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(sb, UNDERLINE_SEPARATOR + matcher.group(0).toLowerCase());
    }
    matcher.appendTail(sb);
    return sb.toString();
  }
}
