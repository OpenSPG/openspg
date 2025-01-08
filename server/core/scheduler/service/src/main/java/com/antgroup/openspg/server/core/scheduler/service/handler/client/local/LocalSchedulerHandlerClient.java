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
package com.antgroup.openspg.server.core.scheduler.service.handler.client.local;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.server.common.service.spring.SpringContextHolder;
import com.antgroup.openspg.server.core.scheduler.service.config.SchedulerConfig;
import com.antgroup.openspg.server.core.scheduler.service.handler.SchedulerHandler;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/** Scheduler Handler Local implementation class. To generate and execute Instances */
@Service
@Slf4j
@ConditionalOnProperty(name = "scheduler.handler.type", havingValue = "local")
public class LocalSchedulerHandlerClient {

  private static final int corePoolSize = 1;
  private static final long initialDelay = 0;

  @Autowired SchedulerConfig schedulerConfig;

  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    log.info("Init Local Scheduler Handler");
    List<SchedulerHandler> schedulerHandlers = SpringContextHolder.getBeans(SchedulerHandler.class);
    log.info("Number of registered schedulerHandler:{}", schedulerHandlers.size());
    if (CollectionUtils.isEmpty(schedulerHandlers)) {
      log.info("The schedulerHandler has not been scanned, no registration is required!");
      return;
    }
    for (SchedulerHandler handler : schedulerHandlers) {
      try {
        Long period =
            (handler.getPeriod() == null || handler.getPeriod() <= 0)
                ? schedulerConfig.getHandlerProcessPeriod()
                : handler.getPeriod();
        ScheduledThreadPoolExecutor executor =
            new ScheduledThreadPoolExecutor(
                corePoolSize,
                (runnable) -> {
                  Thread thread = new Thread(runnable);
                  thread.setDaemon(true);
                  thread.setName("localSchedule-" + thread.getId());
                  return thread;
                });
        executor.scheduleAtFixedRate(
            new SchedulerHandlerRunnable(handler), initialDelay, period, TimeUnit.SECONDS);
      } catch (Exception e) {
        log.error("Executor SchedulerHandler Exception:" + handler.getClass().getSimpleName(), e);
      }
    }
  }

  class SchedulerHandlerRunnable implements Runnable {
    SchedulerHandler handler;

    public SchedulerHandlerRunnable(SchedulerHandler handler) {
      this.handler = handler;
    }

    @Override
    public void run() {
      try {
        log.info("start SchedulerHandler:{}", handler.getClass().getSimpleName());
        handler.process(new JSONObject());
      } catch (Exception e) {
        log.error("SchedulerHandler process Exception", e);
      }
    }
  }
}
