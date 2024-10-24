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

package com.antgroup.openspg.cloudext.impl.cache.redis.util;

import com.antgroup.openspg.cloudext.impl.cache.redis.RedisConstants;
import com.antgroup.openspg.cloudext.impl.cache.redis.model.RedisCacheConfig;
import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.KeyValue;
import io.lettuce.core.Limit;
import io.lettuce.core.Range;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/** The type Redis util */
@Slf4j
public class RedisUtil {

  private final RedisCacheConfig config;

  private final StatefulConnection<String, Object> objectConnection;
  private final AbstractRedisClient objectRedisClient;
  private final RedisClusterCommands<String, Object> objectCommands;

  private volatile StatefulConnection<String, Long> numberConnection;
  private volatile AbstractRedisClient numberRedisClient;
  private volatile RedisClusterCommands<String, Long> numberCommands;

  private final String OK = "OK";

  public RedisUtil(RedisCacheConfig config) {
    this.config = config;
    if (RedisConstants.CLUSTER.equalsIgnoreCase(config.getModel())) {
      List<RedisURI> redisUris =
          buildRedisUriList(config.getClusterNodes(), config.getRedisUser(), config.getRedisAuth());
      RedisClusterClient client = RedisClusterClient.create(redisUris);
      StatefulRedisClusterConnection<String, Object> connection =
          client.connect(new ObjectRedisCodec());
      this.objectConnection = connection;
      this.objectRedisClient = client;
      this.objectCommands = connection.sync();
    } else {
      RedisURI redisUri =
          buildRedisUri(
              config.getRedisHost(),
              config.getRedisPort(),
              config.getRedisUser(),
              config.getRedisAuth());
      RedisClient client = RedisClient.create(redisUri);
      StatefulRedisConnection<String, Object> connection = client.connect(new ObjectRedisCodec());
      this.objectConnection = connection;
      this.objectRedisClient = client;
      this.objectCommands = connection.sync();
    }
  }

  private synchronized void tryInitNumberCommands() {
    if (numberCommands != null) {
      return;
    }
    if (RedisConstants.CLUSTER.equalsIgnoreCase(config.getModel())) {
      List<RedisURI> redisUris =
          buildRedisUriList(config.getClusterNodes(), config.getRedisUser(), config.getRedisAuth());
      RedisClusterClient client = RedisClusterClient.create(redisUris);
      StatefulRedisClusterConnection<String, Long> connection =
          client.connect(new StringLongRedisCodec());
      this.numberConnection = connection;
      this.numberRedisClient = client;
      this.numberCommands = connection.sync();
    } else {
      RedisURI redisUri =
          buildRedisUri(
              config.getRedisHost(),
              config.getRedisPort(),
              config.getRedisUser(),
              config.getRedisAuth());
      RedisClient client = RedisClient.create(redisUri);
      StatefulRedisConnection<String, Long> connection = client.connect(new StringLongRedisCodec());
      this.numberConnection = connection;
      this.numberRedisClient = client;
      this.numberCommands = connection.sync();
    }
  }

  public boolean expire(String key, long time) {
    try {
      if (time > 0) {
        return objectCommands.expire(key, time);
      }
      return true;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  public boolean exists(String key) {
    try {
      return objectCommands.exists(key) > 0;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  public Boolean del(String... key) {
    return objectCommands.del(key) > 0;
  }

  // ============================String=============================

  public Object get(String key) {
    return objectCommands.get(key);
  }

  public boolean set(String key, Object value) {
    try {
      return OK.equalsIgnoreCase(objectCommands.set(key, value));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  public boolean set(String key, Object value, long time) {
    try {
      if (time > 0) {
        return OK.equalsIgnoreCase(
            objectCommands.set(key, value, SetArgs.Builder.ex(Duration.ofSeconds(time))));
      } else {
        return set(key, value);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  public boolean setnxex(String key, Object value, long time) {
    try {
      if (time > 0) {
        return OK.equalsIgnoreCase(
            objectCommands.set(key, value, SetArgs.Builder.nx().ex(Duration.ofSeconds(time))));
      } else {
        return set(key, value);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  public boolean setLong(String key, Long value) {
    tryInitNumberCommands();
    return OK.equalsIgnoreCase(numberCommands.set(key, value));
  }

  public long incr(String key, long delta) {
    return objectCommands.incrby(key, delta);
  }

  public Long getLong(String key) {
    tryInitNumberCommands();
    return numberCommands.get(key);
  }

  public long decr(String key, long delta) {
    objectCommands.eval("", ScriptOutputType.VALUE, "");
    return objectCommands.decrby(key, delta);
  }

  // ================================Map=================================

  public Object hget(String key, String item) {
    return objectCommands.hget(key, item);
  }

  public Map<String, Object> hgetAll(String key) {
    return objectCommands.hgetall(key);
  }

  public Long hset(String key, String item, Object value) {
    try {
      Boolean b = objectCommands.hset(key, item, value);
      return b ? 1L : 0L;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return 0L;
    }
  }

  public Boolean hmsetex(String key, Map<String, ? extends Serializable> map, Integer expire) {
    if (map == null) {
      return false;
    }
    Map<String, Object> objectMap = new HashMap<>(map);
    String result = objectCommands.hmset(key, objectMap);
    if (!OK.equalsIgnoreCase(result)) {
      return false;
    }
    expireByTbaseRule(key, expire);
    return true;
  }

  public Set<String> hkeys(String key) {
    List<String> list = objectCommands.hkeys(key);
    return new HashSet<>(list);
  }

  public List<Serializable> hmget(String key, String[] fields) {
    List<KeyValue<String, Object>> result = objectCommands.hmget(key, fields);
    if (result == null) {
      return null;
    }
    if (result.isEmpty()) {
      return new ArrayList<>();
    }
    List<Serializable> list = new ArrayList<>();
    for (KeyValue<String, Object> keyValue : result) {
      list.add((Serializable) keyValue.getValue());
    }
    return list;
  }

  public Long hdel(String key, String... item) {
    return objectCommands.hdel(key, item);
  }

  // ============================set=============================

  public Set<Object> smembers(String key) {
    try {
      return objectCommands.smembers(key);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public long sadd(String key, Object... values) {
    try {
      return objectCommands.sadd(key, values);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return 0;
    }
  }

  public Serializable spop(String key) {
    return (Serializable) objectCommands.spop(key);
  }

  public Set<Serializable> spop(String key, Long count) {
    Set<Object> objects = objectCommands.spop(key, count);
    if (objects == null) {
      return null;
    }
    if (objects.isEmpty()) {
      return new LinkedHashSet<>();
    }
    Set<Serializable> set = new LinkedHashSet<>();
    for (Object o : objects) {
      set.add((Serializable) o);
    }
    return set;
  }

  public long srem(String key, Object... values) {
    try {
      return objectCommands.srem(key, values);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return 0;
    }
  }
  // ===============================list=================================

  public List<Object> getList(String key, long start, long end) {
    try {
      return objectCommands.lrange(key, start, end);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public boolean rpush(String key, Object value, long time) {
    try {
      objectCommands.rpush(key, value);
      if (time > 0) {
        expire(key, time);
      }
      return true;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  public Long zaddex(String key, Double score, Serializable value, Integer expire) {
    if (objectCommands.exists(key) <= 0) {
      return 0L;
    }

    Long l = objectCommands.zadd(key, score, value);
    if (l <= 0) {
      return 0L;
    }

    expireByTbaseRule(key, expire);
    return 1L;
  }

  public Long zremex(String key, Serializable value, Integer expire) {
    Long result = objectCommands.zrem(key, value);
    if (result == 0) {
      return 0L;
    }
    expireByTbaseRule(key, expire);
    return result;
  }

  /** zrange */
  public Set<Serializable> zrange(String key, Long start, Long end) {
    List<Object> zrange = objectCommands.zrange(key, start, end);
    if (zrange == null) {
      return null;
    }
    if (zrange.isEmpty()) {
      return new LinkedHashSet<>();
    }

    LinkedHashSet<Serializable> set = new LinkedHashSet<>();
    for (Object o : zrange) {
      set.add((Serializable) o);
    }
    return set;
  }

  /** zrem */
  public Long zrem(String key, Serializable... member) {
    return objectCommands.zrem(key, member);
  }

  /** zrangeByScore */
  public Set<Serializable> zrangeByScore(
      String key, Double min, Double max, Integer offset, Integer count) {
    List<Object> zrange =
        objectCommands.zrangebyscore(key, Range.create(min, max), Limit.create(offset, count));
    if (zrange == null) {
      return null;
    }
    if (zrange.isEmpty()) {
      return new LinkedHashSet<>();
    }

    LinkedHashSet<Serializable> set = new LinkedHashSet<>();
    for (Object o : zrange) {
      set.add((Serializable) o);
    }
    return set;
  }

  public List<Serializable> lmpopex(String key, Integer count, Integer timeout) {
    List<Object> objects = null;
    try {
      objects = objectCommands.lpop(key, count);
    } catch (Exception e) {
      // ERR wrong number of arguments for 'LPOP' command
      if (e.getMessage().contains("wrong number of arguments")) {
        throw new RuntimeException("the target redis version does not support this usage", e);
      }
    }

    if (objects == null) {
      return null;
    }
    if (objects.isEmpty()) {
      return new ArrayList<>();
    }

    expireByTbaseRule(key, timeout);
    List<Serializable> result = new ArrayList<>();
    for (Object o : objects) {
      result.add((Serializable) o);
    }
    return result;
  }

  /** llen */
  public Long llen(String key) {
    return objectCommands.llen(key);
  }

  /** Close. */
  public void close() {
    objectConnection.flushCommands();
    objectConnection.close();
    objectRedisClient.shutdown();

    if (numberConnection != null) {
      numberConnection.flushCommands();
      numberConnection.close();
      numberRedisClient.shutdown();
    }
  }

  private RedisURI buildRedisUri(String host, int port, String username, String password) {
    RedisURI.Builder builder = RedisURI.builder().withHost(host).withPort(port);
    if (StringUtils.isBlank(username)) {
      builder = builder.withPassword(password.toCharArray());
    } else {
      builder = builder.withAuthentication(username, password.toCharArray());
    }
    return builder.build();
  }

  private List<RedisURI> buildRedisUriList(
      String clusterNodes, String redisUser, String redisAuth) {
    List<RedisURI> list = new ArrayList<>();
    String[] split = clusterNodes.split(",");
    for (String s : split) {
      String[] hp = s.split(":");
      String host = hp[0];
      int port = Integer.parseInt(hp[1]);
      RedisURI redisUri = buildRedisUri(host, port, redisUser, redisAuth);
      list.add(redisUri);
    }
    return list;
  }

  private void expireByTbaseRule(String key, Integer expire) {
    if (expire < 0) {
      return;
    }

    if (expire > 0) {
      objectCommands.expire(key, expire);
    } else {
      objectCommands.persist(key);
    }
  }
}
