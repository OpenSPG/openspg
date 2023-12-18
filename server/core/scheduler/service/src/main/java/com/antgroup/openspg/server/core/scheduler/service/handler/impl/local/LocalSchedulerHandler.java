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
package com.antgroup.openspg.server.core.scheduler.service.handler.impl.local;

import com.antgroup.openspg.server.core.scheduler.service.config.SchedulerConfig;
import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerExecuteService;
import com.antgroup.openspg.server.core.scheduler.service.handler.SchedulerHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Scheduler Handler Local implementation class. To generate and execute Instances */
@Service
@Slf4j
public class LocalSchedulerHandler implements SchedulerHandler {

  private static final String HANDLER_TYPE = "local";
  private static final int corePoolSize = 1;
  private static final long initialDelay = 0;

  private static ScheduledExecutorService EXECUTE = new ScheduledThreadPoolExecutor(corePoolSize);
  private static ScheduledExecutorService GENERATE = new ScheduledThreadPoolExecutor(corePoolSize);

  @Autowired SchedulerConfig schedulerConfig;
  @Autowired SchedulerExecuteService schedulerExecuteService;

  @Override
  @PostConstruct
  public void executeInstances() {
    if (!HANDLER_TYPE.equalsIgnoreCase(schedulerConfig.getHandlerType())) {
      log.warn("ignore execute handlerType:{}", schedulerConfig.getHandlerType());
      return;
    }
    log.info("start executeInstances");
    EXECUTE.scheduleAtFixedRate(
        new ExecuteRunnable(),
        initialDelay,
        schedulerConfig.getExecuteInstancesPeriod(),
        schedulerConfig.getExecuteInstancesUnit());
  }

  @Override
  @PostConstruct
  public void generateInstances() {
    if (!HANDLER_TYPE.equalsIgnoreCase(schedulerConfig.getHandlerType())) {
      log.warn("ignore generate handlerType:{}", schedulerConfig.getHandlerType());
      return;
    }
    log.info("start generateInstances");
    GENERATE.scheduleAtFixedRate(
        new GenerateRunnable(),
        initialDelay,
        schedulerConfig.getGenerateInstancesPeriod(),
        schedulerConfig.getGenerateInstancesUnit());
  }

  /** Execute Instances Runnable */
  class ExecuteRunnable implements Runnable {
    @Override
    public void run() {
      try {
        Long startTime = System.currentTimeMillis();
        schedulerExecuteService.executeInstances();
        Long time = System.currentTimeMillis() - startTime;
        log.info("run ExecuteInstances end time:{}", time);
      } catch (Exception e) {
        log.error("run ExecuteInstances Exception", e);
      }
    }
  }

  /** Generate Instances Runnable */
  class GenerateRunnable implements Runnable {
    @Override
    public void run() {
      try {
        Long startTime = System.currentTimeMillis();
        schedulerExecuteService.generateInstances();
        Long time = System.currentTimeMillis() - startTime;
        log.info("run GenerateInstances end time:{}", time);
      } catch (Exception e) {
        log.error("run GenerateInstances Exception", e);
      }
    }
  }
}
