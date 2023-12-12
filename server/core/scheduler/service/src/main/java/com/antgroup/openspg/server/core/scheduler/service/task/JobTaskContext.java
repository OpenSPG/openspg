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

/** Alipay.com Inc. Copyright (c) 2004-2021 All Rights Reserved. */
package com.antgroup.openspg.server.core.scheduler.service.task;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.common.util.IpUtils;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import java.util.Date;

/**
 * Scheduler Task Context
 *
 * @author yangjin @Title: JobTaskContext.java @Description:
 */
public class JobTaskContext {

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

  public JobTaskContext() {
    this.traceLog = new StringBuffer();
    this.startTime = System.currentTimeMillis();
    this.taskFinish = false;
  }

  public JobTaskContext(SchedulerJob job, SchedulerInstance instance, SchedulerTask task) {
    this.job = job;
    this.instance = instance;
    this.task = task;
    this.traceLog = new StringBuffer();
    this.startTime = System.currentTimeMillis();
    this.taskFinish = false;
  }

  /**
   * 往TraceLog 添加日志，用于页面展示
   *
   * @param
   * @return
   * @author 庄舟
   * @date 2020/9/14 09:26
   */
  public void addTraceLog(String message, Object... args) {
    message = String.format(message, args);
    StringBuffer log = new StringBuffer(DateTimeUtils.getDate2LongStr(new Date()));
    log.append("(")
        .append(IpUtils.IP_LIST)
        .append(")：")
        .append(message)
        .append(System.getProperty("line.separator"));
    traceLog.insert(0, log);
  }

  public SchedulerJob getJob() {
    return job;
  }

  public void setJob(SchedulerJob job) {
    this.job = job;
  }

  public SchedulerInstance getInstance() {
    return instance;
  }

  public void setInstance(SchedulerInstance instance) {
    this.instance = instance;
  }

  public SchedulerTask getTask() {
    return task;
  }

  public void setTask(SchedulerTask task) {
    this.task = task;
  }

  public StringBuffer getTraceLog() {
    return traceLog;
  }

  public void setTraceLog(StringBuffer traceLog) {
    this.traceLog = traceLog;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public boolean isTaskFinish() {
    return taskFinish;
  }

  public void setTaskFinish(boolean taskFinish) {
    this.taskFinish = taskFinish;
  }

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }
}
