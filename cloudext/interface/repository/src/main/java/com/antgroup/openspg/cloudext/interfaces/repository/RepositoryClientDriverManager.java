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

package com.antgroup.openspg.cloudext.interfaces.repository;

import com.antgroup.openspg.common.model.datasource.connection.RepositoryConnectionInfo;
import com.antgroup.openspg.common.model.exception.CloudExtException;
import com.antgroup.openspg.common.util.DriverManagerUtils;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RepositoryClientDriverManager {

  private static final CopyOnWriteArrayList<RepositoryClientDriver> registeredDrivers =
      new CopyOnWriteArrayList<>();

  private RepositoryClientDriverManager() {}

  static {
    DriverManagerUtils.loadDrivers("cloudext.repository.drivers", RepositoryClientDriver.class);
    log.info("repository DriverManager initialized");
  }

  public static synchronized void registerDriver(RepositoryClientDriver driver) {
    if (driver != null) {
      registeredDrivers.addIfAbsent(driver);
    } else {
      throw new NullPointerException();
    }
    log.info("registerDriver: {}", driver);
  }

  public static RepositoryClient getClient(RepositoryConnectionInfo config) {
    for (RepositoryClientDriver driver : registeredDrivers) {
      if (driver.acceptsConfig(config)) {
        return driver.connect(config);
      }
    }
    throw CloudExtException.driverNotExist(config);
  }
}
