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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.common.TaskDag;
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

  /** remark */
  private String remark;

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

  /** constructor */
  public SchedulerTask(SchedulerInstance instance, TaskStatus status, TaskDag.Node node) {
    this.executeNum = 0;
    this.beginTime = new Date();
    this.status = status;
    this.jobId = instance.getJobId();
    this.instanceId = instance.getId();
    this.nodeId = node.getId();
    this.type = node.getType();
    this.title = StringUtils.isNotBlank(node.getName()) ? node.getName() : node.getType();

    if (node.getProperties() != null) {
      this.extension = JSON.parseObject(JSON.toJSONString(node.getProperties()));
    }

    StringBuffer log = new StringBuffer(DateTimeUtils.getDate2LongStr(new Date()));
    log.append("Create new Task，Wait for the execution of the preceding node to complete.....")
        .append(System.getProperty("line.separator"));

    this.remark = log.toString();
  }
}
