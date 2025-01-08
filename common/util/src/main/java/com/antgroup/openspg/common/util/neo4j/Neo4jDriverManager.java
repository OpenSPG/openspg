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
package com.antgroup.openspg.common.util.neo4j;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class Neo4jDriverManager {
  private static Map<String, Driver> instanceMap = Maps.newConcurrentMap();
  private static Lock lock = new ReentrantLock();

  public static Driver getNeo4jDriver(String uri, String user, String password) {
    String uniqueKey = getInstanceUniqueKey(uri, user, password);
    if (instanceMap.get(uniqueKey) == null) {
      lock.lock();
      try {
        if (instanceMap.get(uniqueKey) == null) {
          Driver driver;
          try {
            Config config =
                Config.builder()
                    .withMaxConnectionPoolSize(200)
                    .withMaxConnectionLifetime(4, TimeUnit.HOURS)
                    .withMaxTransactionRetryTime(300, TimeUnit.SECONDS)
                    .withConnectionAcquisitionTimeout(300, TimeUnit.SECONDS)
                    .build();
            driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password), config);
            driver.verifyConnectivity();
          } catch (Exception e) {
            throw new RuntimeException("init Neo4j Client failed :" + uri + "，" + user, e);
          }
          instanceMap.put(uniqueKey, driver);
        }
      } finally {
        lock.unlock();
      }
    }
    return instanceMap.get(uniqueKey);
  }

  private static String getInstanceUniqueKey(String uri, String user, String password) {
    return uri + "#" + user + "#" + password;
  }
}
