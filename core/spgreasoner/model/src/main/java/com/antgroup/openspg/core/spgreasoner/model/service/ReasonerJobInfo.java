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

package com.antgroup.openspg.core.spgreasoner.model.service;

import com.antgroup.openspg.common.model.base.BaseModel;
import com.antgroup.openspg.common.model.job.JobInfoStateEnum;
import java.util.HashMap;
import java.util.Map;

public class ReasonerJobInfo extends BaseModel {

  private Long jobId;

  private final String jobName;

  private final Long projectId;

  private final BaseReasonerContent content;

  private final String cron;

  private final JobInfoStateEnum status;

  private String externalJobInfoId;

  private final Map<String, Object> params;

  public ReasonerJobInfo(
      String jobName,
      Long projectId,
      BaseReasonerContent content,
      String cron,
      JobInfoStateEnum status,
      Map<String, Object> params) {
    this.jobName = jobName;
    this.projectId = projectId;
    this.content = content;
    this.cron = cron;
    this.status = status;
    this.params = params == null ? new HashMap<>(5) : params;
  }

  public Long getJobId() {
    return jobId;
  }

  public ReasonerJobInfo setJobId(Long jobId) {
    this.jobId = jobId;
    return this;
  }

  public String getJobName() {
    return jobName;
  }

  public Long getProjectId() {
    return projectId;
  }

  public BaseReasonerContent getContent() {
    return content;
  }

  public String getCron() {
    return cron;
  }

  public JobInfoStateEnum getStatus() {
    return status;
  }

  public String getExternalJobInfoId() {
    return externalJobInfoId;
  }

  public ReasonerJobInfo setExternalJobInfoId(String externalJobInfoId) {
    this.externalJobInfoId = externalJobInfoId;
    return this;
  }

  public Map<String, Object> getParams() {
    return params;
  }
}
