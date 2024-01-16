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

package com.antgroup.openspg.common.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DriverManagerUtils {

  public static <T> void loadDrivers(String systemProperty, Class<T> clazz) {
    String drivers = null;
    try {
      drivers =
          AccessController.doPrivileged(
              (PrivilegedAction<String>) () -> System.getProperty(systemProperty));
    } catch (Exception ex) {
      // do nothing
    }

    AccessController.doPrivileged(
        (PrivilegedAction<Void>)
            () -> {
              ServiceLoader<T> loadedDrivers = ServiceLoader.load(clazz);
              Iterator<T> driversIterator = loadedDrivers.iterator();

              try {
                while (driversIterator.hasNext()) {
                  driversIterator.next();
                }
              } catch (Throwable t) {
                // do nothing
              }
              return null;
            });

    log.info("DriverManager.initialize: {} = {}", systemProperty, drivers);

    if (drivers == null || "".equals(drivers)) {
      return;
    }
    for (String driver : drivers.split(":")) {
      try {
        log.info("DriverManager.initialize: loading {}", driver);
        Class.forName(driver, true, ClassLoader.getSystemClassLoader());
      } catch (Exception e) {
        log.error("DriverManager.initialize: load failed", e);
      }
    }
  }
}
