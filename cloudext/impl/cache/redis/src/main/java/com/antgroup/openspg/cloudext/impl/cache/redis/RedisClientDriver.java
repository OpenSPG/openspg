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

package com.antgroup.openspg.cloudext.impl.cache.redis;

import com.antgroup.openspg.cloudext.interfaces.cache.CacheClient;
import com.antgroup.openspg.cloudext.interfaces.cache.CacheClientDriver;
import com.antgroup.openspg.cloudext.interfaces.cache.CacheClientDriverManager;

public class RedisClientDriver implements CacheClientDriver {

  static {
    CacheClientDriverManager.registerDriver(new RedisClientDriver());
  }

  @Override
  public String driverScheme() {
    return "redis";
  }

  @Override
  public CacheClient connect(String url) {
    return new RedisClient(url);
  }
}
