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

import com.antgroup.openspg.cloudext.impl.cache.redis.model.RedisCacheConfig;
import com.antgroup.openspg.cloudext.impl.cache.redis.util.RedisUtil;
import com.antgroup.openspg.cloudext.interfaces.cache.CacheClient;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class RedisClient implements CacheClient {

  private final RedisUtil redisUtil;

  @Getter private final String connUrl;

  public RedisClient(String connUrl) {
    this.connUrl = connUrl;
    this.redisUtil = initRedisClient(UriComponentsBuilder.fromUriString(connUrl).build());
  }

  private RedisUtil initRedisClient(UriComponents uriComponents) {
    String host = uriComponents.getHost();
    Integer port = uriComponents.getPort();
    String clusterNodes = String.format("%s:%s", uriComponents.getHost(), uriComponents.getPort());
    String model = uriComponents.getQueryParams().getFirst(RedisConstants.MODEL);
    String accessId = uriComponents.getQueryParams().getFirst(RedisConstants.ACCESS_ID);
    String accessKey = uriComponents.getQueryParams().getFirst(RedisConstants.ACCESS_KEY);
    RedisUtil redisUtil;
    try {
      RedisCacheConfig cfg;
      if (RedisConstants.CLUSTER.equalsIgnoreCase(model)) {
        cfg = RedisCacheConfig.cluster(clusterNodes, accessId, accessKey);
      } else {
        cfg = RedisCacheConfig.single(host, port, accessId, accessKey);
      }
      redisUtil = new RedisUtil(cfg);
    } catch (Exception e) {
      throw new RuntimeException("init Redis Client failed", e);
    }
    return redisUtil;
  }

  @Override
  public Serializable getObject(String key) {
    return (Serializable) redisUtil.get(key);
  }

  @Override
  public Boolean putObject(String key, Serializable data) {
    return redisUtil.set(key, data);
  }

  @Override
  public Boolean setLong(String key, Long value) {
    return redisUtil.setLong(key, value);
  }

  @Override
  public Boolean putObjectWithExpire(String key, Serializable data, Integer expireSec) {
    return redisUtil.set(key, data, expireSec);
  }

  @Override
  public Boolean putObjectNxWithExpire(String key, Serializable data, Integer expireSec) {
    return redisUtil.setnxex(key, data, expireSec);
  }

  @Override
  public Boolean putListWithExpire(
      String key, List<? extends Serializable> data, Integer expireSec) {
    return redisUtil.rpush(key, data, expireSec);
  }

  @Override
  public <T> List<T> getList(String key) {
    return (List<T>) redisUtil.getList(key, 0, -1);
  }

  @Override
  public Long sadd(String key, Serializable... serializables) {
    return redisUtil.sadd(key, serializables);
  }

  @Override
  public <T> Set<T> smembers(String key) {
    return (Set<T>) redisUtil.smembers(key);
  }

  @Override
  public Long srem(String key, Serializable... member) {
    return redisUtil.srem(key, member);
  }

  @Override
  public Serializable spop(String key) {
    return redisUtil.spop(key);
  }

  @Override
  public Set<Serializable> spop(String key, Long count) {
    return redisUtil.spop(key, count);
  }

  @Override
  public Long zaddex(String key, Double score, Serializable value, Integer expire) {
    return redisUtil.zaddex(key, score, value, expire);
  }

  @Override
  public Long zremex(String key, Serializable value, Integer expire) {
    return redisUtil.zremex(key, value, expire);
  }

  @Override
  public Set<Serializable> zrange(String key, Long start, Long end) {
    return redisUtil.zrange(key, start, end);
  }

  @Override
  public Long zrem(String key, Serializable... member) {
    return redisUtil.zrem(key, member);
  }

  @Override
  public Set<Serializable> zrangeByScore(
      String key, Double min, Double max, Integer offset, Integer count) {
    return redisUtil.zrangeByScore(key, min, max, offset, count);
  }

  @Override
  public Map<String, Serializable> hgetAll(String key) {
    Map<String, Serializable> result = new HashMap<>();
    Map<String, Object> tmpResult = redisUtil.hgetAll(key);
    if (tmpResult != null) {
      for (String keyObj : tmpResult.keySet()) {
        result.put(keyObj, (Serializable) tmpResult.get(keyObj));
      }
    }
    return result;
  }

  @Override
  public Set<String> hkeys(String key) {
    return redisUtil.hkeys(key);
  }

  @Override
  public Serializable hget(String key, String field) {
    return (Serializable) redisUtil.hget(key, field);
  }

  @Override
  public Long hdel(String key, String... fields) {
    return redisUtil.hdel(key, fields);
  }

  @Override
  public Long hset(String key, String field, Serializable value) {
    return redisUtil.hset(key, field, value);
  }

  @Override
  public Boolean hmsetex(String key, Map<String, ? extends Serializable> map, Integer expire) {
    return redisUtil.hmsetex(key, map, expire);
  }

  @Override
  public List<Serializable> hmget(String key, String[] fields) {
    return redisUtil.hmget(key, fields);
  }

  @Override
  public Long incrBy(String key, Long delta) {
    return redisUtil.incr(key, delta);
  }

  @Override
  public Long getLong(String key) {
    return redisUtil.getLong(key);
  }

  @Override
  public List<Serializable> lmpopex(String key, Integer count, Integer timeout) {
    return redisUtil.lmpopex(key, count, timeout);
  }

  @Override
  public Long llen(String key) {
    return redisUtil.llen(key);
  }

  @Override
  public Boolean expire(String key, Integer expire) {
    return redisUtil.expire(key, expire);
  }

  @Override
  public Boolean exists(String key) {
    return redisUtil.exists(key);
  }

  @Override
  public Boolean delete(String key) {
    return redisUtil.del(key);
  }

  @Override
  public void close() throws Throwable {
    redisUtil.close();
  }
}
