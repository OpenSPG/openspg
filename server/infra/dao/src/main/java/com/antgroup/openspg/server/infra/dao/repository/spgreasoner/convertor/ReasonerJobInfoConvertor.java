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

package com.antgroup.openspg.server.infra.dao.repository.spgreasoner.convertor;

import com.antgroup.openspg.api.facade.JSON;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.SPGJobInfoDO;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.JobTypeEnum;
import com.antgroup.openspg.common.model.job.JobInfoStateEnum;
import com.antgroup.openspg.core.spgreasoner.model.service.BaseReasonerContent;
import com.antgroup.openspg.core.spgreasoner.model.service.ReasonerJobInfo;
import com.google.gson.reflect.TypeToken;
import java.util.Map;

public class ReasonerJobInfoConvertor {

  public static SPGJobInfoDO toDO(ReasonerJobInfo reasonerJobInfo) {
    SPGJobInfoDO jobInfoDO = new SPGJobInfoDO();
    jobInfoDO.setId(reasonerJobInfo.getJobId());
    jobInfoDO.setName(reasonerJobInfo.getJobName());
    jobInfoDO.setType(JobTypeEnum.REASONING.name());
    jobInfoDO.setProjectId(reasonerJobInfo.getProjectId());
    jobInfoDO.setCron(reasonerJobInfo.getCron());
    jobInfoDO.setStatus(reasonerJobInfo.getStatus().name());
    jobInfoDO.setExtInfo(JSON.serialize(reasonerJobInfo.getParams()));
    jobInfoDO.setContent(JSON.serialize(reasonerJobInfo.getContent()));
    jobInfoDO.setExternalJobInfoId(reasonerJobInfo.getExternalJobInfoId());
    return jobInfoDO;
  }

  public static ReasonerJobInfo toModel(SPGJobInfoDO jobInfoDO) {
    if (jobInfoDO == null) {
      return null;
    }
    if (!JobTypeEnum.REASONING.name().equals(jobInfoDO.getType())) {
      return null;
    }

    return new ReasonerJobInfo(
            jobInfoDO.getName(),
            jobInfoDO.getProjectId(),
            JSON.deserialize(jobInfoDO.getContent(), BaseReasonerContent.class),
            jobInfoDO.getCron(),
            JobInfoStateEnum.valueOf(jobInfoDO.getStatus()),
            JSON.deserialize(
                jobInfoDO.getExtInfo(), new TypeToken<Map<String, Object>>() {}.getType()))
        .setJobId(jobInfoDO.getId());
  }
}
