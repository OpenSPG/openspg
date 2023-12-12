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

import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerGenerateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yangjin
 * @version : GenerateInstances.java, v 0.1 2023年12月01日 10:42 yangjin Exp $
 */
public class GenerateInstances implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteInstances.class);

  SchedulerGenerateService schedulerGenerateService;

  public GenerateInstances(SchedulerGenerateService schedulerGenerateService) {
    this.schedulerGenerateService = schedulerGenerateService;
  }

  @Override
  public void run() {
    try {
      Long startTime = System.currentTimeMillis();
      LOGGER.info("====== run GenerateInstances start %s======");
      schedulerGenerateService.generateInstances();
      Long time = System.currentTimeMillis() - startTime;
      LOGGER.info(String.format("====== run GenerateInstances end time:%s======", time));
    } catch (Exception e) {
      LOGGER.error("run GenerateInstances Exception", e);
    }
  }
}
