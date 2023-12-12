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
package com.antgroup.openspg.server.core.scheduler.model.service;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/** Scheduler Job Model */
@Getter
@Setter
public class SchedulerJob extends BaseModel {

  private static final long serialVersionUID = 3050626766276089001L;

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

  /** invoker id */
  private String invokerId;

  /** job name */
  private String name;

  /** job Life Cycle：PERIOD,ONCE,REAL_TIME Enum:LifeCycle */
  private String lifeCycle;

  /** job translate */
  private String translate;

  /** job Status：ONLINE,OFFLINE */
  private String status;

  /** Scheduler Cron expression default:0 0 0 * * ? */
  private String schedulerCron;

  /** Upstream dependent Job */
  private Long preJobId;

  /** last Execute Time */
  private Date lastExecuteTime;

  /** extension */
  private String extension;

  /** version */
  private String version;

  /** Dependent upstream partition-MERGE，independent-SNAPSHOT */
  private String mergeMode;

  /** Scheduler dag config */
  private String config;
}
