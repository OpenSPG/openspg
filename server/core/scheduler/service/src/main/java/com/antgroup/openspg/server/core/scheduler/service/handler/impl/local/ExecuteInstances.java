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

/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.server.core.scheduler.service.handler.impl.local;

import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerExecuteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Execute Instances Runnable. To generate instances by all period Job
 */
public class ExecuteInstances implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteInstances.class);

  SchedulerExecuteService schedulerExecuteService;

  public ExecuteInstances(SchedulerExecuteService schedulerExecuteService) {
    this.schedulerExecuteService = schedulerExecuteService;
  }

  @Override
  public void run() {
    try {
      Long startTime = System.currentTimeMillis();
      LOGGER.info("run ExecuteInstances start");
      schedulerExecuteService.executeInstances();
      Long time = System.currentTimeMillis() - startTime;
      LOGGER.info(String.format("run ExecuteInstances end time:%s", time));
    } catch (Exception e) {
      LOGGER.error("run ExecuteInstances Exception", e);
    }
  }
}
