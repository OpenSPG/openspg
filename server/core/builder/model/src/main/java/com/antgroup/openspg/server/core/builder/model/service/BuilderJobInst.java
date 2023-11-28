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

package com.antgroup.openspg.server.core.builder.model.service;

import com.antgroup.openspg.common.model.base.BaseModel;
import com.antgroup.openspg.common.model.job.JobInstStatusEnum;
import java.util.Date;

public class BuilderJobInst extends BaseModel {

  private final Long jobId;
  private final Long projectId;
  private final Date startTime;
  private final Date endTime;
  private final String logInfo;
  private BuilderProgress progress;
  private Long jobInstId;
  private BaseBuilderResult result;
  private JobInstStatusEnum status;
  private String externalJobInstId;

  public BuilderJobInst(
      Long jobId,
      Long projectId,
      JobInstStatusEnum status,
      BaseBuilderResult result,
      Date startTime,
      Date endTime,
      BuilderProgress progress,
      String logInfo) {
    this.jobId = jobId;
    this.projectId = projectId;
    this.result = result;
    this.status = status;
    this.startTime = startTime;
    this.endTime = endTime;
    this.progress = progress;
    this.logInfo = logInfo;
  }

  public boolean isFinished() {
    return status.isFinished();
  }

  public boolean isRunning() {
    return status.isRunning();
  }

  public Long getJobInstId() {
    return jobInstId;
  }

  public BuilderJobInst setJobInstId(Long jobInstId) {
    this.jobInstId = jobInstId;
    return this;
  }

  public Long getJobId() {
    return jobId;
  }

  public Long getProjectId() {
    return projectId;
  }

  public JobInstStatusEnum getStatus() {
    return status;
  }

  public void setProgress(BuilderStatusWithProgress progress) {
    this.status = progress.getStatus();
    this.result = progress.getResult();
    this.progress = progress.getProgress();
  }

  public Date getStartTime() {
    return startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public BaseBuilderResult getResult() {
    return result;
  }

  public BuilderProgress getProgress() {
    return progress;
  }

  public String getLogInfo() {
    return logInfo;
  }

  public String getExternalJobInstId() {
    return externalJobInstId;
  }

  public BuilderJobInst setExternalJobInstId(String externalJobInstId) {
    this.externalJobInstId = externalJobInstId;
    return this;
  }
}
