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

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {

  public static String toJsonString(Object object) {
    String result = null;
    try {
      result = SchemaJsonUtils.serialize(object);
    } catch (Exception e) {
      log.error("toJsonString error, object={}", object, e);
      result = StringUtils.toString(object);
    }
    return result;
  }

  /**
   * Flatten the json object
   *
   * @param jsonObject
   * @return
   */
  public static JSONObject flatten(JSONObject jsonObject) {
    JSONObject flattenedObject = new JSONObject();
    flatten(jsonObject, flattenedObject, "");
    return flattenedObject;
  }

  private static void flatten(JSONObject jsonObject, JSONObject flattenedObject, String prefix) {
    for (String key : jsonObject.keySet()) {
      Object value = jsonObject.get(key);
      if (value instanceof JSONObject) {
        flatten((JSONObject) value, flattenedObject, prefix);
      } else {
        flattenedObject.put(prefix.isEmpty() ? key : prefix + "." + key, value);
      }
    }
  }
}
