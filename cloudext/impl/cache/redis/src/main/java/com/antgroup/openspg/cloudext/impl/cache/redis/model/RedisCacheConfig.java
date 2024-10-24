/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
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
