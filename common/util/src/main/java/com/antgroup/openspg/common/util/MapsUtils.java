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

package com.antgroup.openspg.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.collections4.MapUtils;

public class MapsUtils {

  /** Map的key不变，value转化为新的value */
  public static <K, V, NV> Map<K, NV> mapValue(Map<K, V> originMap, Function<V, NV> valueFunc) {
    if (MapUtils.isEmpty(originMap)) {
      return new HashMap<>(0);
    }

    Map<K, NV> newMap = new HashMap<>(originMap.size());
    for (Map.Entry<K, V> entry : originMap.entrySet()) {
      newMap.put(entry.getKey(), valueFunc.apply(entry.getValue()));
    }
    return newMap;
  }

  /** Map的value不变，key转化为新的key */
  public static <K, V, NK> Map<NK, V> mapKey(Map<K, V> originMap, Function<K, NK> keyFunc) {
    if (MapUtils.isEmpty(originMap)) {
      return new HashMap<>(0);
    }

    Map<NK, V> newMap = new HashMap<>(originMap.size());
    for (Map.Entry<K, V> entry : originMap.entrySet()) {
      newMap.put(keyFunc.apply(entry.getKey()), entry.getValue());
    }
    return newMap;
  }
}
