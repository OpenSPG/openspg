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

import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerGenerateService;
import lombok.extern.slf4j.Slf4j;

/** Generate Instances Runnable. To execute all instances */
@Slf4j
public class GenerateInstances implements Runnable {
  SchedulerGenerateService schedulerGenerateService;

  public GenerateInstances(SchedulerGenerateService schedulerGenerateService) {
    this.schedulerGenerateService = schedulerGenerateService;
  }

  @Override
  public void run() {
    try {
      Long startTime = System.currentTimeMillis();
      log.info("run GenerateInstances start");
      schedulerGenerateService.generateInstances();
      Long time = System.currentTimeMillis() - startTime;
      log.info("run GenerateInstances end time:{}", time);
    } catch (Exception e) {
      log.error("run GenerateInstances Exception", e);
    }
  }
}
