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
package com.antgroup.openspg.server.core.scheduler.model.service;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.Dependence;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.LifeCycle;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteDag;
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

  /** unique id = jobId+yyyyMMddHHmmss */
  private String uniqueId;

  /** project id */
  private Long projectId;

  /** SchedulerJob Id */
  private Long jobId;

  /** instance type */
  private String type;

  /** status */
  private InstanceStatus status;

  /** progress [0-100] */
  private Long progress;

  /** create User */
  private String createUser;

  /** create time */
  private Date gmtCreate;

  /** modify time */
  private Date gmtModified;

  /** instance begin Running Time */
  private Date beginRunningTime;

  /** instance finish Time */
  private Date finishTime;

  /** job Life Cycle：PERIOD,ONCE,REAL_TIME Enum:LifeCycle */
  private LifeCycle lifeCycle;

  /** Dependent pre task completion */
  private Dependence dependence;

  /** scheduler Date */
  private Date schedulerDate;

  /** version */
  private String version;

  /** extension */
  private JSONObject extension;

  /** task dag Config */
  private TaskExecuteDag taskDag;
}
