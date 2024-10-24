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
package com.antgroup.openspg.cloudext.impl.cache.redis.model;

import com.antgroup.openspg.cloudext.impl.cache.redis.RedisConstants;
import lombok.Getter;

@Getter
public class RedisCacheConfig {

  private final String model;

  private String clusterNodes = "127.0.0.1:6379";

  private String redisHost = "127.0.0.1";

  private int redisPort = 6379;

  private String redisUser;

  private String redisAuth;

  private RedisCacheConfig(String model) {
    this.model = model;
  }

  /**
   * Single redis distributed cache config.
   *
   * @param redisHost the redis host
   * @param redisPort the redis port
   * @param redisUser the redis user
   * @param redisAuth the redis auth
   * @return the redis distributed cache config
   */
  public static RedisCacheConfig single(
      String redisHost, Integer redisPort, String redisUser, String redisAuth) {
    RedisCacheConfig config = new RedisCacheConfig(RedisConstants.SINGLE);
    config.redisHost = redisHost;
    config.redisPort = redisPort;
    config.redisUser = redisUser;
    config.redisAuth = redisAuth;
    return config;
  }

  /**
   * Cluster redis distributed cache config.
   *
   * @param clusterNodes the cluster nodes
   * @param redisUser the redis user
   * @param redisAuth the redis auth
   * @return the redis distributed cache config
   */
  public static RedisCacheConfig cluster(String clusterNodes, String redisUser, String redisAuth) {
    RedisCacheConfig config = new RedisCacheConfig(RedisConstants.CLUSTER);
    config.clusterNodes = clusterNodes;
    config.redisUser = redisUser;
    config.redisAuth = redisAuth;
    return config;
  }
}
