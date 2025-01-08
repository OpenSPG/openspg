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

/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.config;

import com.antgroup.openspg.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Scheduler Common Value */
@Component
public class SchedulerConfig {

  @Value("${scheduler.handler.process.period:}")
  private String handlerProcessPeriod;

  @Value("${scheduler.execute.max.day:}")
  private String executeMaxDay;

  public Long getHandlerProcessPeriod() {
    if (StringUtils.isBlank(handlerProcessPeriod)) {
      return null;
    }
    return Long.valueOf(handlerProcessPeriod);
  }

  public Integer getExecuteMaxDay() {
    if (StringUtils.isBlank(executeMaxDay)) {
      return 10;
    }
    return Integer.valueOf(executeMaxDay);
  }
}
