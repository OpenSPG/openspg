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
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class Concat {

  @UdfDefine(name = "concat")
  public String concat(String str1, String str2) {
    return str1 + str2;
  }

  @UdfDefine(name = "concat")
  public String concat(Object o1, Object o2) {
    return String.valueOf(o1) + o2;
  }

  @UdfDefine(name = "concat")
  public String concat(Object o1, Object o2, Object o3) {
    return String.valueOf(o1) + o2 + o3;
  }

  @UdfDefine(name = "concat")
  public String concat(Object o1, Object o2, Object o3, Object o4) {
    return String.valueOf(o1) + o2 + o3 + o4;
  }

  @UdfDefine(name = "concat")
  public String concat(Object o1, Object o2, Object o3, Object o4, Object o5) {
    return String.valueOf(o1) + o2 + o3 + o4 + o5;
  }

  @UdfDefine(name = "concat")
  public String concat(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
    return String.valueOf(o1) + o2 + o3 + o4 + o5 + o6;
  }

  @UdfDefine(name = "concat")
  public String concat(
      Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
    return String.valueOf(o1) + o2 + o3 + o4 + o5 + o6 + o7;
  }

  @UdfDefine(name = "concat")
  public String concat(
      Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
    return String.valueOf(o1) + o2 + o3 + o4 + o5 + o6 + o7 + o8;
  }

  @UdfDefine(name = "concat")
  public String concat(
      Object o1,
      Object o2,
      Object o3,
      Object o4,
      Object o5,
      Object o6,
      Object o7,
      Object o8,
      Object o9) {
    return String.valueOf(o1) + o2 + o3 + o4 + o5 + o6 + o7 + o8 + o9;
  }

  @UdfDefine(name = "concat")
  public String concat(
      Object o1,
      Object o2,
      Object o3,
      Object o4,
      Object o5,
      Object o6,
      Object o7,
      Object o8,
      Object o9,
      Object o10) {
    return String.valueOf(o1) + o2 + o3 + o4 + o5 + o6 + o7 + o8 + o9 + o10;
  }

  @UdfDefine(name = "concat")
  public String concat(List<Object> objects) {
    return concat(objects.toArray());
  }

  @UdfDefine(name = "concat")
  public String concat(Object[] objects) {
    StringBuilder sb = new StringBuilder();
    for (Object obj : objects) {
      sb.append(obj);
    }
    return sb.toString();
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(String separator, Object[] objectArray) {
    List<String> values = new ArrayList<>();
    for (Object o : objectArray) {
      if (o != null) {
        values.add(String.valueOf(o));
      }
    }
    return StringUtils.join(values, separator);
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(Character separator, Object[] objectArray) {
    return concatWs(String.valueOf(separator), objectArray);
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(String separator, List<Object> objectList) {
    return concatWs(separator, objectList.toArray());
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(Character separator, List<Object> objectList) {
    return concatWs(separator, objectList.toArray());
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(String separator, Object o1, Object o2) {
    return concatWs(separator, new Object[] {o1, o2});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(Character separator, Object o1, Object o2) {
    return concatWs(separator, new Object[] {o1, o2});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(String separator, Object o1, Object o2, Object o3) {
    return concatWs(separator, new Object[] {o1, o2, o3});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(Character separator, Object o1, Object o2, Object o3) {
    return concatWs(separator, new Object[] {o1, o2, o3});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(String separator, Object o1, Object o2, Object o3, Object o4) {
    return concatWs(separator, new Object[] {o1, o2, o3, o4});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(Character separator, Object o1, Object o2, Object o3, Object o4) {
    return concatWs(separator, new Object[] {o1, o2, o3, o4});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(String separator, Object o1, Object o2, Object o3, Object o4, Object o5) {
    return concatWs(separator, new Object[] {o1, o2, o3, o4, o5});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(
      Character separator, Object o1, Object o2, Object o3, Object o4, Object o5) {
    return concatWs(separator, new Object[] {o1, o2, o3, o4, o5});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(
      String separator, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
    return concatWs(separator, new Object[] {o1, o2, o3, o4, o5, o6});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(
      Character separator, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
    return concatWs(separator, new Object[] {o1, o2, o3, o4, o5, o6});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(
      String separator,
      Object o1,
      Object o2,
      Object o3,
      Object o4,
      Object o5,
      Object o6,
      Object o7) {
    return concatWs(separator, new Object[] {o1, o2, o3, o4, o5, o6, o7});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(
      Character separator,
      Object o1,
      Object o2,
      Object o3,
      Object o4,
      Object o5,
      Object o6,
      Object o7) {
    return concatWs(separator, new Object[] {o1, o2, o3, o4, o5, o6, o7});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(
      String separator,
      Object o1,
      Object o2,
      Object o3,
      Object o4,
      Object o5,
      Object o6,
      Object o7,
      Object o8) {
    return concatWs(separator, new Object[] {o1, o2, o3, o4, o5, o6, o7, o8});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(
      Character separator,
      Object o1,
      Object o2,
      Object o3,
      Object o4,
      Object o5,
      Object o6,
      Object o7,
      Object o8) {
    return concatWs(separator, new Object[] {o1, o2, o3, o4, o5, o6, o7, o8});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(
      String separator,
      Object o1,
      Object o2,
      Object o3,
      Object o4,
      Object o5,
      Object o6,
      Object o7,
      Object o8,
      Object o9) {
    return concatWs(separator, new Object[] {o1, o2, o3, o4, o5, o6, o7, o8, o9});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(
      Character separator,
      Object o1,
      Object o2,
      Object o3,
      Object o4,
      Object o5,
      Object o6,
      Object o7,
      Object o8,
      Object o9) {
    return concatWs(separator, new Object[] {o1, o2, o3, o4, o5, o6, o7, o8, o9});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(
      String separator,
      Object o1,
      Object o2,
      Object o3,
      Object o4,
      Object o5,
      Object o6,
      Object o7,
      Object o8,
      Object o9,
      Object o10) {
    return concatWs(separator, new Object[] {o1, o2, o3, o4, o5, o6, o7, o8, o9, o10});
  }

  @UdfDefine(name = "concat_ws", compatibleName = "ConcatWs")
  public String concatWs(
      Character separator,
      Object o1,
      Object o2,
      Object o3,
      Object o4,
      Object o5,
      Object o6,
      Object o7,
      Object o8,
      Object o9,
      Object o10) {
    return concatWs(separator, new Object[] {o1, o2, o3, o4, o5, o6, o7, o8, o9, o10});
  }
}
