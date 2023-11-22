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

package com.antgroup.openspg.server.api.http.client;

import com.google.gson.Gson;
import java.lang.reflect.Type;

public class JSON {

  public static final String DATA_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String DEFAULT_TYPE_FIELD_NAME = "@type";

  public static Gson gson = null;

  static {
    gson = null;
  }

  /**
   * Serialize the given Java object into JSON string.
   *
   * @param obj Object
   * @return String representation of the JSON
   */
  public static String serialize(Object obj) {
    return gson.toJson(obj);
  }

  /**
   * Deserialize the given JSON string to Java object.
   *
   * @param <T> Type
   * @param body The JSON string
   * @param type The class to deserialize into
   * @return The deserialized Java object
   */
  public static <T> T deserialize(String body, Type type) {
    return gson.fromJson(body, type);
  }

  /**
   * Deserialize the given JSON string to Java object.
   *
   * @param <T> Type
   * @param body The JSON string
   * @param clazz The class to deserialize into
   * @return The deserialized Java object
   */
  public static <T> T deserialize(String body, Class<T> clazz) {
    return gson.fromJson(body, clazz);
  }
}
