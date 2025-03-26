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
package com.antgroup.openspg.server.infra.dao.dataobject;

import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.Status;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulerJobDO {

  /** primary key */
  private Long id;

  /** createUser */
  private String createUser;

  /** modifyUser */
  private String modifyUser;

  /** Create time */
  private Date gmtCreate;

  /** Modified time */
  private Date gmtModified;

  /** project id */
  private Long projectId;

  /** job name */
  private String name;

  /** job Life Cycle：PERIOD,ONCE,REAL_TIME */
  private String lifeCycle;

  /** translate type */
  private String translateType;

  /** job Status：ENABLE,DISABLE */
  private Status status;

  /** Dependent pre task completion */
  private String dependence;

  /** Scheduler Cron expression default:0 0 0 * * ? */
  private String schedulerCron;

  /** last Execute Time */
  private Date lastExecuteTime;

  /** invoker id, Primary key of the service table that triggers scheduler */
  private String invokerId;

  /** extension */
  private String extension;

  /** version */
  private String version;
}
