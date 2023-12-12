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
 * @version : SchedulerInstance.java, v 0.1 2023年11月30日 09:50 yangjin Exp $
 */
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public Long getJobId() {
    return jobId;
  }

  public void setJobId(Long jobId) {
    this.jobId = jobId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getProgress() {
    return progress;
  }

  public void setProgress(Long progress) {
    this.progress = progress;
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

  public Date getBeginRunningTime() {
    return beginRunningTime;
  }

  public void setBeginRunningTime(Date beginRunningTime) {
    this.beginRunningTime = beginRunningTime;
  }

  public Date getFinishTime() {
    return finishTime;
  }

  public void setFinishTime(Date finishTime) {
    this.finishTime = finishTime;
  }

  public Date getEstimateEndTime() {
    return estimateEndTime;
  }

  public void setEstimateEndTime(Date estimateEndTime) {
    this.estimateEndTime = estimateEndTime;
  }

  public String getLifeCycle() {
    return lifeCycle;
  }

  public void setLifeCycle(String lifeCycle) {
    this.lifeCycle = lifeCycle;
  }

  public Date getSchedulerDate() {
    return schedulerDate;
  }

  public void setSchedulerDate(Date schedulerDate) {
    this.schedulerDate = schedulerDate;
  }

  public String getExternalInstanceId() {
    return externalInstanceId;
  }

  public void setExternalInstanceId(String externalInstanceId) {
    this.externalInstanceId = externalInstanceId;
  }

  public String getMergeMode() {
    return mergeMode;
  }

  public void setMergeMode(String mergeMode) {
    this.mergeMode = mergeMode;
  }

  public String getEnv() {
    return env;
  }

  public void setEnv(String env) {
    this.env = env;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getConfig() {
    return config;
  }

  public void setConfig(String config) {
    this.config = config;
  }

  public String getWorkflowConfig() {
    return workflowConfig;
  }

  public void setWorkflowConfig(String workflowConfig) {
    this.workflowConfig = workflowConfig;
  }
}
