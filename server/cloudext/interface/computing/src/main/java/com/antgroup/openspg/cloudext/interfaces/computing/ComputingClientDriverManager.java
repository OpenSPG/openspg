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

package com.antgroup.openspg.cloudext.interfaces.computing;

import com.antgroup.openspg.common.util.DriverManagerUtils;
import com.antgroup.openspg.common.model.datasource.connection.ComputingConnectionInfo;
import com.antgroup.openspg.common.model.exception.CloudExtException;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComputingClientDriverManager {

  private static final CopyOnWriteArrayList<ComputingClientDriver> registeredDrivers =
      new CopyOnWriteArrayList<>();

  private ComputingClientDriverManager() {}

  static {
    DriverManagerUtils.loadDrivers("cloudext.computing.drivers", ComputingClientDriver.class);
    log.info("computing DriverManager initialized");
  }

  public static synchronized void registerDriver(ComputingClientDriver driver) {
    if (driver != null) {
      registeredDrivers.addIfAbsent(driver);
    } else {
      throw new NullPointerException();
    }
    log.info("registerDriver: {}", driver);
  }

  public static ComputingClient getClient(ComputingConnectionInfo config) {
    for (ComputingClientDriver driver : registeredDrivers) {
      if (driver.acceptsConfig(config)) {
        return driver.connect(config);
      }
    }
    throw CloudExtException.driverNotExist(config);
  }
}
