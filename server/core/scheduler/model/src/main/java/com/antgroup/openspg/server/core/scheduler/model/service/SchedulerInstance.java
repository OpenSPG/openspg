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

import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/** Scheduler Instance Model */
@Getter
@Setter
public class SchedulerInstance extends BaseModel {

  private static final long serialVersionUID = -2574666198428196663L;

  /** primary key */
  private Long id;

  /** instance id = jobId+yyyyMMddHHmmss */
  private String uniqueId;

  /** project id */
  private Long projectId;

  /** SchedulerJob Id */
  private Long jobId;

  /** instance type */
  private String type;

  /** status */
  private String status;

  /** progress [0-100] */
  private Long progress;

  /** create User */
  private String createUser;

  /** modify User */
  private String modifyUser;

  /** create time */
  private Date gmtCreate;

  /** modify time */
  private Date gmtModified;

  /** instance begin Running Time */
  private Date beginRunningTime;

  /** instance finish Time */
  private Date finishTime;

  /** instance estimate End Time */
  private Date estimateEndTime;

  /** job Life Cycle：PERIOD,ONCE,REAL_TIME Enum:LifeCycle */
  private String lifeCycle;

  /** scheduler Date */
  private Date schedulerDate;

  /** external Instance Id */
  private String externalInstanceId;

  /** Dependent upstream partition-MERGE，independent-SNAPSHOT */
  private String mergeMode;

  /** env:prod,prepub,dev */
  private String env;

  /** version */
  private String version;

  /** job dag config */
  private String config;

  /** workflow dag Config */
  private String workflowConfig;
}
