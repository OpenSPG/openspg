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
package com.antgroup.openspg.server.core.scheduler.service.common;

import com.antgroup.openspg.server.common.model.scheduler.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import java.util.Date;
import java.util.List;

/** Scheduler Common Service */
public interface SchedulerCommonService {

  /** set Instance To Finish */
  void setInstanceFinish(
      SchedulerInstance instance, InstanceStatus instanceStatus, TaskStatus taskStatus);

  /** generate Once Instance */
  SchedulerInstance generateOnceInstance(SchedulerJob job);

  /** generate Period Instance by Cron */
  List<SchedulerInstance> generatePeriodInstance(SchedulerJob job);

  /** generate RealTime Instance */
  SchedulerInstance generateRealTimeInstance(SchedulerJob job);

  /** generate Instance by schedulerDate */
  SchedulerInstance generateInstance(SchedulerJob job, String uniqueId, Date schedulerDate);
}
