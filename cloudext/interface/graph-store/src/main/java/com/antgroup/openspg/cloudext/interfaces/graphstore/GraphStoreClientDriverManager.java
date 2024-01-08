/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.cloudext.interfaces.graphstore;

import com.antgroup.openspg.common.util.DriverManagerUtils;
import com.antgroup.openspg.server.common.model.exception.CloudExtException;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class GraphStoreClientDriverManager {

  private static final CopyOnWriteArrayList<GraphStoreClientDriver> registeredDrivers =
      new CopyOnWriteArrayList<>();

  private GraphStoreClientDriverManager() {}

  static {
    DriverManagerUtils.loadDrivers("cloudext.graphstore.drivers", GraphStoreClientDriver.class);
    log.info("graph-store DriverManager initialized");
  }

  public static synchronized void registerDriver(GraphStoreClientDriver driver) {
    if (driver != null) {
      registeredDrivers.addIfAbsent(driver);
    } else {
      throw new NullPointerException();
    }
    log.info("registerDriver: {}", driver);
  }

  public static GraphStoreClient getClient(String connUrl) {
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(connUrl).build();
    for (GraphStoreClientDriver driver : registeredDrivers) {
      if (driver.acceptsConfig(uriComponents.getScheme())) {
        return driver.connect(connUrl);
      }
    }
    throw CloudExtException.driverNotExist(connUrl);
  }
}
