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

package com.antgroup.openspg.cloudext.interfaces.cache;

import com.antgroup.openspg.common.util.cloudext.CloudExtClient;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CacheClient extends CloudExtClient {

  Serializable getObject(String key);

  Boolean putObject(String key, Serializable data);

  Boolean setLong(String key, Long value);

  Boolean putObjectWithExpire(final String key, final Serializable data, Integer expireSec);

  Boolean putObjectNxWithExpire(final String key, final Serializable data, Integer expireSec);

  Boolean putListWithExpire(String key, List<? extends Serializable> data, Integer expireSec);

  <T> List<T> getList(String key);

  Long sadd(String key, Serializable... serializables);

  <T> Set<T> smembers(String key);

  Long srem(String key, Serializable... member);

  Serializable spop(String key);

  Set<Serializable> spop(String key, Long count);

  Long zaddex(String key, Double score, Serializable value, Integer expire);

  Long zremex(String key, Serializable value, Integer expire);

  Set<Serializable> zrange(String key, Long start, Long end);

  Long zrem(String key, Serializable... member);

  Set<Serializable> zrangeByScore(
      String key, Double min, Double max, Integer offset, Integer count);

  Map<String, Serializable> hgetAll(String key);

  Set<String> hkeys(String key);

  Serializable hget(String key, String field);

  Long hdel(String key, String... fields);

  Long hset(String key, String field, Serializable value);

  Boolean hmsetex(String key, Map<String, ? extends Serializable> map, Integer expire);

  List<Serializable> hmget(String key, String[] fields);

  Long incrBy(String key, Long delta);

  Long getLong(String key);

  List<Serializable> lmpopex(String key, Integer count, Integer timeout);

  Long llen(String key);

  Boolean expire(final String key, final Integer expire);

  Boolean exists(final String key);

  Boolean delete(final String key);

  void close() throws Throwable;
}
