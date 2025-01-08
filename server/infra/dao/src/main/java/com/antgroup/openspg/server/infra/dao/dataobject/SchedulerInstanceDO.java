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

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulerInstanceDO {

  /** primary key */
  private Long id;

  /** unique id = jobId+yyyyMMddHHmmss */
  private String uniqueId;

  /** project id */
  private Long projectId;

  /** SchedulerJobDO Id */
  private Long jobId;

  /** instance type */
  private String type;

  /** status */
  private String status;

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
  private String lifeCycle;

  /** Dependent pre task completion */
  private String dependence;

  /** scheduler Date */
  private Date schedulerDate;

  /** version */
  private String version;

  /** extension */
  private String extension;

  /** task dag Config */
  private String taskDag;
}
