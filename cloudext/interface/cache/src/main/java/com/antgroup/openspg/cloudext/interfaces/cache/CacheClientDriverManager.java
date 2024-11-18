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

import com.antgroup.openspg.common.util.DriverManagerUtils;
import com.antgroup.openspg.server.common.model.exception.CloudExtException;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class CacheClientDriverManager {

  private static final CopyOnWriteArrayList<CacheClientDriver> registeredDrivers =
      new CopyOnWriteArrayList<>();

  private CacheClientDriverManager() {}

  static {
    DriverManagerUtils.loadDrivers("cloudext.cache.drivers", CacheClientDriver.class);
    log.info("cache DriverManager initialized");
  }

  public static synchronized void registerDriver(CacheClientDriver driver) {
    if (driver != null) {
      registeredDrivers.addIfAbsent(driver);
    } else {
      throw new NullPointerException();
    }
    log.info("registerDriver: {}", driver);
  }

  public static CacheClient getClient(String connUrl) {
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(connUrl).build();
    for (CacheClientDriver driver : registeredDrivers) {
      if (driver.acceptsConfig(uriComponents.getScheme())) {
        return driver.connect(connUrl);
      }
    }
    throw CloudExtException.driverNotExist(connUrl);
  }
}
