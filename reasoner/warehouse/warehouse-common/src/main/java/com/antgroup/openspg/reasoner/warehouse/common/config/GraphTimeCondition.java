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

package com.antgroup.openspg.reasoner.warehouse.common.config;

import com.google.common.base.Splitter;
import java.io.Serializable;
import java.util.Map;
import lombok.Data;

@Data
public class GraphTimeCondition implements Serializable {
  private long startMs = 0;
  private long endMs = 0;
  private long intervalMs = 0;

  public GraphTimeCondition(String configStr) {
    Map<String, String> map = Splitter.on(",").withKeyValueSeparator(":").split(configStr);
    for (String key : map.keySet()) {
      String value = map.get(key);
      if ("startMs".equals(key)) {
        this.startMs = Long.parseLong(value);
      } else if ("endMs".equals(key)) {
        this.endMs = Long.parseLong(value);
      } else if ("intervalMs".equals(key)) {
        this.intervalMs = Long.parseLong(value);
      }
    }
    if (intervalMs > 0) {
      endMs = System.currentTimeMillis();
      startMs = endMs - intervalMs;
    }
  }
}
