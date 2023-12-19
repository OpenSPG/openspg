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

package com.antgroup.openspg.server.core.scheduler.model.task;

import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.common.util.NetworkAddressUtils;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Scheduler Task Context */
@Getter
@Setter
@ToString
public class TaskExecuteContext {

  /** Scheduler Job */
  private SchedulerJob job;

  /** Scheduler Instance */
  private SchedulerInstance instance;

  /** Scheduler task */
  private SchedulerTask task;

  /** trace Log */
  private StringBuffer traceLog;

  /** start Time */
  private long startTime;

  /** task is Finish */
  private boolean taskFinish;

  public TaskExecuteContext(SchedulerJob job, SchedulerInstance instance, SchedulerTask task) {
    task.setRemark(null);
    this.job = job;
    this.instance = instance;
    this.task = task;
    this.traceLog = new StringBuffer();
    this.startTime = System.currentTimeMillis();
    this.taskFinish = false;
  }

  /**
   * insert TraceLog
   *
   * @param
   * @return
   * @date 2020/9/14 09:26
   */
  public void addTraceLog(String message, Object... args) {
    int dstOffset = 0;
    StringBuffer log = new StringBuffer(DateTimeUtils.getDate2LongStr(new Date()));
    log.append("(")
        .append(NetworkAddressUtils.LOCAL_IP)
        .append("): ")
        .append(String.format(message, args))
        .append(System.getProperty("line.separator"));

    traceLog.insert(dstOffset, log);
  }
}
