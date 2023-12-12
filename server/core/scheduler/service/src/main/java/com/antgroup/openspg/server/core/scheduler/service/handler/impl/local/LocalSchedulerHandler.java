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

import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerValue;
import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerExecuteService;
import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerGenerateService;
import com.antgroup.openspg.server.core.scheduler.service.handler.SchedulerHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @version : QuartzSchedulerHandler.java, v 0.1 2023-11-30 19:05 $
 */
@Service
public class LocalSchedulerHandler implements SchedulerHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocalSchedulerHandler.class);

  private static final String HANDLER_TYPE = "local";
  private static final int corePoolSize = 1;

  private static ScheduledExecutorService SCHEDULER_EXECUTOR =
      new ScheduledThreadPoolExecutor(corePoolSize);
  private static ScheduledExecutorService GENERATE_INSTANCES_EXECUTOR =
      new ScheduledThreadPoolExecutor(corePoolSize);

  @Autowired SchedulerValue schedulerValue;
  @Autowired SchedulerExecuteService schedulerExecuteService;
  @Autowired SchedulerGenerateService schedulerGenerateService;

  @Override
  @PostConstruct
  public void executeInstances() {
    if (!HANDLER_TYPE.equalsIgnoreCase(schedulerValue.getHandlerType())) {
      LOGGER.warn(
          String.format(
              "ignore executeInstances inconsistent handlerType:%s",
              schedulerValue.getHandlerType()));
      return;
    }

    try {
      LOGGER.info("start executeInstances");
      SCHEDULER_EXECUTOR.scheduleAtFixedRate(
          new ExecuteInstances(schedulerExecuteService),
          0,
          schedulerValue.getExecuteInstancesPeriod(),
          schedulerValue.getExecuteInstancesUnit());
      LOGGER.info("end executeInstances");
    } catch (Exception e) {
      LOGGER.error("executeInstances Exception", e);
    }
  }

  @Override
  @PostConstruct
  public void generateInstances() {
    if (!HANDLER_TYPE.equalsIgnoreCase(schedulerValue.getHandlerType())) {
      LOGGER.warn(
          String.format(
              "=== ignore generateInstances inconsistent handlerType:%s ===",
              schedulerValue.getHandlerType()));
      return;
    }
    try {
      LOGGER.info("====== start generateInstances ======");
      GENERATE_INSTANCES_EXECUTOR.scheduleAtFixedRate(
          new GenerateInstances(schedulerGenerateService),
          0,
          schedulerValue.getGenerateInstancesPeriod(),
          schedulerValue.getGenerateInstancesUnit());
      LOGGER.info("====== end generateInstances ======");
    } catch (Exception e) {
      LOGGER.error("generateInstances Exception", e);
    }
  }
}
