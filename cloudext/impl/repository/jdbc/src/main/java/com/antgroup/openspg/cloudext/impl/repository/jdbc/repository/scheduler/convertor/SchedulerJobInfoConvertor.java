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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.scheduler.convertor;

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.JobInfoDO;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.SchedulerJobInfo;
import com.antgroup.openspg.common.model.job.JobInfoStateEnum;

public class SchedulerJobInfoConvertor {

  public static JobInfoDO toDO(SchedulerJobInfo jobInfo) {
    JobInfoDO jobInfoDO = new JobInfoDO();

    if (jobInfo.getJobId() != null) {
      jobInfoDO.setId(Long.valueOf(jobInfo.getJobId()));
    }
    jobInfoDO.setName(jobInfo.getJobName());
    jobInfoDO.setType(jobInfo.getJobType());
    jobInfoDO.setCron(jobInfo.getCron());
    jobInfoDO.setStatus(jobInfo.getStatus().name());
    jobInfoDO.setIdempotentId(jobInfo.getIdempotentId());
    return jobInfoDO;
  }

  public static SchedulerJobInfo toModel(JobInfoDO jobInfoDO) {
    if (jobInfoDO == null) {
      return null;
    }
    return new SchedulerJobInfo(
        String.valueOf(jobInfoDO.getId()),
        jobInfoDO.getName(),
        jobInfoDO.getType(),
        jobInfoDO.getCron(),
        JobInfoStateEnum.valueOf(jobInfoDO.getStatus()),
        jobInfoDO.getIdempotentId());
  }
}
