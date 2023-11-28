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

package com.antgroup.openspg.server.infra.dao.repository.scheduler.convertor;

import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.SchedulerJobInst;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.common.model.job.JobInstStatusEnum;
import com.antgroup.openspg.server.infra.dao.dataobject.JobInstDO;

public class SchedulerJobInstConvertor {

  public static JobInstDO toDO(SchedulerJobInst jobInst) {
    JobInstDO jobInstDO = new JobInstDO();

    if (StringUtils.isNotBlank(jobInst.getJobInstId())) {
      jobInstDO.setId(Long.valueOf(jobInst.getJobInstId()));
    }
    jobInstDO.setJobId(Long.valueOf(jobInst.getJobId()));
    jobInstDO.setType(jobInst.getJobType());
    jobInstDO.setStatus(jobInst.getStatus().name());
    jobInstDO.setIdempotentId(jobInst.getIdempotentId());
    return jobInstDO;
  }

  public static SchedulerJobInst toModel(JobInstDO jobInstDO) {
    if (jobInstDO == null) {
      return null;
    }
    return new SchedulerJobInst(
        String.valueOf(jobInstDO.getId()),
        String.valueOf(jobInstDO.getJobId()),
        jobInstDO.getType(),
        JobInstStatusEnum.valueOf(jobInstDO.getStatus()),
        jobInstDO.getHost(),
        jobInstDO.getIdempotentId());
  }
}
