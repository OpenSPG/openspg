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

import com.antgroup.openspg.builder.model.pipeline.Pipeline;
import com.antgroup.openspg.builder.model.record.RecordAlterOperationEnum;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import com.antgroup.openspg.server.common.model.job.JobInfoStateEnum;
import java.util.HashMap;
import java.util.Map;

public class BuilderJobInfo extends BaseModel {

  public static final String BATCH_SIZE = "batch_size";
  public static final String PARALLELISM = "parallelism";
  public static final String LEAD_TO = "lead_to";
  public static final String OPERATION_TYPE = "operation_type";

  public static final String SEARCH_ENGINE = "searchEngine";

  private final String jobName;
  private final Long projectId;
  private final Pipeline pipeline;
  private final String cron;
  private final JobInfoStateEnum status;
  private final Map<String, Object> params;
  private Long jobId;
  private String externalJobInfoId;

  public BuilderJobInfo(
      String jobName,
      Long projectId,
      Pipeline pipeline,
      String cron,
      JobInfoStateEnum status,
      Map<String, Object> params) {
    this.jobName = jobName;
    this.projectId = projectId;
    this.pipeline = pipeline;
    this.cron = cron;
    this.status = status;
    this.params = params == null ? new HashMap<>(5) : params;
  }

  public Long getJobId() {
    return jobId;
  }

  public BuilderJobInfo setJobId(Long jobId) {
    this.jobId = jobId;
    return this;
  }

  public String getJobName() {
    return jobName;
  }

  public Long getProjectId() {
    return projectId;
  }

  public Pipeline getPipeline() {
    return pipeline;
  }

  public String getCron() {
    return cron;
  }

  public JobInfoStateEnum getStatus() {
    return status;
  }

  public Map<String, Object> getParams() {
    return params;
  }

  public int getParallelism() {
    return ((Long) params.getOrDefault(PARALLELISM, 1L)).intValue();
  }

  public int getBatchSize() {
    return (int) params.getOrDefault(BATCH_SIZE, 1);
  }

  public RecordAlterOperationEnum getOperationType() {
    Object operationType = params.get(OPERATION_TYPE);
    if (operationType == null) {
      return RecordAlterOperationEnum.UPSERT;
    }
    return RecordAlterOperationEnum.valueOf(operationType.toString());
  }

  public String getExternalJobInfoId() {
    return externalJobInfoId;
  }

  public BuilderJobInfo setExternalJobInfoId(String externalJobInfoId) {
    this.externalJobInfoId = externalJobInfoId;
    return this;
  }
}
