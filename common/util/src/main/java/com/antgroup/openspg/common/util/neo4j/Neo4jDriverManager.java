/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.common.util.neo4j;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.neo4j.driver.AuthTokens;
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
            driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
            driver.verifyConnectivity();
          } catch (Exception e) {
            throw new RuntimeException("init Neo4j Client failed :" + uri, e);
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
