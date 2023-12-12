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

/**
 * @author yangjin
 * @version : SchedulerJob.java, v 0.1 2023年11月30日 09:50 yangjin Exp $
 */
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCreateUser() {
    return createUser;
  }

  public void setCreateUser(String createUser) {
    this.createUser = createUser;
  }

  public String getModifyUser() {
    return modifyUser;
  }

  public void setModifyUser(String modifyUser) {
    this.modifyUser = modifyUser;
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public Date getGmtModified() {
    return gmtModified;
  }

  public void setGmtModified(Date gmtModified) {
    this.gmtModified = gmtModified;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public String getInvokerId() {
    return invokerId;
  }

  public void setInvokerId(String invokerId) {
    this.invokerId = invokerId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLifeCycle() {
    return lifeCycle;
  }

  public void setLifeCycle(String lifeCycle) {
    this.lifeCycle = lifeCycle;
  }

  public String getTranslate() {
    return translate;
  }

  public void setTranslate(String translate) {
    this.translate = translate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getSchedulerCron() {
    return schedulerCron;
  }

  public void setSchedulerCron(String schedulerCron) {
    this.schedulerCron = schedulerCron;
  }

  public Long getPreJobId() {
    return preJobId;
  }

  public void setPreJobId(Long preJobId) {
    this.preJobId = preJobId;
  }

  public Date getLastExecuteTime() {
    return lastExecuteTime;
  }

  public void setLastExecuteTime(Date lastExecuteTime) {
    this.lastExecuteTime = lastExecuteTime;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getMergeMode() {
    return mergeMode;
  }

  public void setMergeMode(String mergeMode) {
    this.mergeMode = mergeMode;
  }

  public String getConfig() {
    return config;
  }

  public void setConfig(String config) {
    this.config = config;
  }
}
