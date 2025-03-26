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
package com.antgroup.openspg.server.core.scheduler.service.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerHandlerResult;
import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerExecuteService;
import com.antgroup.openspg.server.core.scheduler.service.handler.SchedulerHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component("generateInstanceScheduleHandler")
public class GenerateInstanceScheduleHandler implements SchedulerHandler {

  @Autowired SchedulerExecuteService schedulerExecuteService;

  @Override
  public SchedulerHandlerResult process(JSONObject jobContext) {
    long startTime = System.currentTimeMillis();
    try {
      schedulerExecuteService.generateInstances();
      Long time = System.currentTimeMillis() - startTime;
      log.info("run {} end time:{}", this.getClass().getSimpleName(), time);
      return new SchedulerHandlerResult(
          SchedulerEnum.TaskStatus.FINISH, SchedulerEnum.TaskStatus.FINISH.name());
    } catch (Exception e) {
      log.error("run {} Exception", this.getClass().getSimpleName(), e);
      return new SchedulerHandlerResult(
          SchedulerEnum.TaskStatus.ERROR, ExceptionUtils.getStackTrace(e));
    }
  }

  @Override
  public Long getPeriod() {
    return 60L;
  }
}
