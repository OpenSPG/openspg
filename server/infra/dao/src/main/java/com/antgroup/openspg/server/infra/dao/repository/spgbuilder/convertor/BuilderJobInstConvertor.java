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

package com.antgroup.openspg.server.infra.dao.repository.spgbuilder.convertor;

import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.JobTypeEnum;
import com.antgroup.openspg.common.model.job.JobInstStatusEnum;
import com.antgroup.openspg.schema.http.client.JSON;
import com.antgroup.openspg.server.core.builder.model.service.BaseBuilderResult;
import com.antgroup.openspg.server.core.builder.model.service.BuilderJobInst;
import com.antgroup.openspg.server.core.builder.model.service.BuilderProgress;
import com.antgroup.openspg.server.infra.dao.dataobject.SPGJobInstDOWithBLOBs;

public class BuilderJobInstConvertor {

  public static BuilderJobInst toModel(SPGJobInstDOWithBLOBs jobInstDO) {
    if (jobInstDO == null) {
      return null;
    }
    if (!JobTypeEnum.BUILDING.name().equals(jobInstDO.getType())) {
      return null;
    }
    return new BuilderJobInst(
            jobInstDO.getJobId(),
            jobInstDO.getProjectId(),
            JobInstStatusEnum.valueOf(jobInstDO.getStatus()),
            JSON.deserialize(jobInstDO.getResult(), BaseBuilderResult.class),
            jobInstDO.getStartTime(),
            jobInstDO.getEndTime(),
            JSON.deserialize(jobInstDO.getProgress(), BuilderProgress.class),
            jobInstDO.getLogInfo())
        .setJobInstId(jobInstDO.getId())
        .setExternalJobInstId(jobInstDO.getExternalJobInstId());
  }

  public static SPGJobInstDOWithBLOBs toDO(BuilderJobInst jobInst) {
    SPGJobInstDOWithBLOBs jobInstDO = new SPGJobInstDOWithBLOBs();

    jobInstDO.setId(jobInst.getJobInstId());
    jobInstDO.setJobId(jobInst.getJobId());
    jobInstDO.setType(JobTypeEnum.BUILDING.name());
    jobInstDO.setProjectId(jobInst.getProjectId());
    jobInstDO.setStatus(jobInst.getStatus().name());
    jobInstDO.setStartTime(jobInst.getStartTime());
    jobInstDO.setEndTime(jobInst.getEndTime());
    jobInstDO.setProgress(JSON.serialize(jobInst.getProgress()));
    jobInstDO.setExternalJobInstId(jobInst.getExternalJobInstId());
    jobInstDO.setResult(JSON.serialize(jobInst.getResult()));
    jobInstDO.setLogInfo(jobInst.getLogInfo());
    jobInstDO.setExternalJobInstId(jobInst.getExternalJobInstId());
    return jobInstDO;
  }
}
