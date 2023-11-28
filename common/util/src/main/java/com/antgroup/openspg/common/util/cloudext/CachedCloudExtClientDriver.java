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

package com.antgroup.openspg.common.util.cloudext;

import com.antgroup.openspg.server.common.model.datasource.connection.BaseConnectionInfo;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CachedCloudExtClientDriver<
        C extends CloudExtClient, I extends BaseConnectionInfo>
    implements CloudExtClientDriver<C, I> {

  private final Map<I, C> CACHE = new ConcurrentHashMap<>();

  @Override
  public C connect(I connInfo) {
    if (!CACHE.containsKey(connInfo)) {
      synchronized (this) {
        if (!CACHE.containsKey(connInfo)) {
          C client = innerConnect(connInfo);
          CACHE.put(connInfo, client);
        }
      }
    }
    return CACHE.get(connInfo);
  }

  protected abstract C innerConnect(I connInfo);
}
