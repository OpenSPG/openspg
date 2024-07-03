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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONValidator;


public class JsonStringGet {

  /**
   * @param jsonString
   * @return
   */
  private static Object parseJson(String jsonString) {
    JSONValidator validator = JSONValidator.from(jsonString);
    JSONValidator.Type type = validator.getType();
    if (type == JSONValidator.Type.Object) {
      return JSON.parseObject(jsonString);
    } else if (type == JSONValidator.Type.Array) {
      return JSON.parseArray(jsonString);
    }
    return null;
  }

  /**
   * @param plainJson
   * @param jsonPath
   * @return
   */
  @UdfDefine(name = "json_get", compatibleName = "UDF_JsonGet")
  public  Object jsonStrGet(String plainJson, String jsonPath) {
    try {
      // 可能存在传入的plainJson为null的情况
      if (plainJson == null ){
        return "";
      }
      Object jsonObject = parseJson(plainJson);
      if (jsonObject != null) {
        Object result = JSONPath.eval(jsonObject, jsonPath);
        if (result != null) {
          return result;
        }
      }
    } catch (Exception e) {
      return "";
    }
    return "";

  }

  @UdfDefine(name = "get_rdf_property")
  public Object getRdfProperty(Object properties, String propKey) {
    if (properties instanceof Map) {
      Map<String, Object> objectMap = (Map<String, Object>) properties;
      List<String> jsonStrList = new ArrayList<>();
      for (String key : objectMap.keySet()) {
        if (!key.contains("basicInfo")) {
          continue;
        }
        return jsonStrGet(objectMap.get(key).toString(), "$." + propKey);
      }
    }
    return null;
  }
}
