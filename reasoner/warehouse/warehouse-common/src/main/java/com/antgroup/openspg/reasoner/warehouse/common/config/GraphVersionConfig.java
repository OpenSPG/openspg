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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class GraphVersionConfig implements Serializable {
  private final Long snapshotVersion;
  private final Long startVersion;
  private final Long endVersion;

  public GraphVersionConfig() {
    this.snapshotVersion = null;
    this.startVersion = 0L;
    this.endVersion = System.currentTimeMillis();
  }

  public GraphVersionConfig(Long snapshot, Long start, Long end) {
    this.snapshotVersion = snapshot;
    this.startVersion = start;
    this.endVersion = end;
  }

  public GraphVersionConfig(String configStr) {
    List<Long> versionList = new ArrayList<>(3);
    Splitter.on(",")
        .split(configStr)
        .forEach(
            s -> {
              s = s.trim();
              if (StringUtils.isEmpty(s) || "null".equals(s.toLowerCase(Locale.ROOT))) {
                versionList.add(null);
                return;
              }
              versionList.add(Long.parseLong(s));
            });
    Long snapshot = null;
    Long start = null;
    Long end = null;
    for (int i = 0; i < versionList.size(); ++i) {
      if (0 == i) {
        snapshot = versionList.get(i);
      } else if (1 == i) {
        start = versionList.get(i);
      } else if (2 == i) {
        end = versionList.get(i);
      }
    }
    this.snapshotVersion = snapshot;
    this.startVersion = start;
    this.endVersion = end;
  }
}
