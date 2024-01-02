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
package com.antgroup.openspg.server.core.scheduler.model.service;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteDag;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/** Scheduler Task Model */
@Getter
@Setter
public class SchedulerTask extends BaseModel {

  private static final long serialVersionUID = -5515352651327338741L;

  /** primary key */
  private Long id;

  /** Create time */
  private Date gmtCreate;

  /** Modified time */
  private Date gmtModified;

  /** type */
  private String type;

  /** title */
  private String title;

  /** status */
  private TaskStatus status;

  /** SchedulerJob Id */
  private Long jobId;

  /** instance id */
  private Long instanceId;

  /** execute Num */
  private Integer executeNum;

  /** execute begin Time */
  private Date beginTime;

  /** execute finish Time */
  private Date finishTime;

  /** estimate Finish Time */
  private Date estimateFinishTime;

  /** traceLog */
  private String traceLog;

  /** lock Time */
  private Date lockTime;

  /** resource */
  private String resource;

  /** input */
  private String input;

  /** output */
  private String output;

  /** node id */
  private String nodeId;

  /** extension，JSON */
  private JSONObject extension;

  public SchedulerTask() {}

  /** constructor by instance and dag node */
  public SchedulerTask(SchedulerInstance instance, TaskStatus status, TaskExecuteDag.Node node) {
    this.executeNum = 0;
    this.beginTime = new Date();
    this.status = status;
    this.jobId = instance.getJobId();
    this.instanceId = instance.getId();
    this.nodeId = node.getId();
    this.type = node.getTaskComponent();
    this.title = StringUtils.isNotBlank(node.getName()) ? node.getName() : node.getTaskComponent();

    if (node.getProperties() != null) {
      this.extension = node.getProperties();
    }

    StringBuffer log = new StringBuffer(DateTimeUtils.getDate2LongStr(new Date()));
    log.append("Create new Task, Waiting preceding node to complete.....");
    log.append(System.getProperty("line.separator"));

    this.traceLog = log.toString();
  }
}
