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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgbuilder.convertor;

import com.antgroup.openspg.api.facade.JSON;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.SPGJobInfoDO;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.JobTypeEnum;
import com.antgroup.openspg.common.model.job.JobInfoStateEnum;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.Pipeline;
import com.antgroup.openspg.core.spgbuilder.model.service.BuilderJobInfo;

import com.google.gson.reflect.TypeToken;

import java.util.Map;


public class BuilderJobInfoConvertor {

    public static SPGJobInfoDO toDO(BuilderJobInfo builderJobInfo) {
        SPGJobInfoDO jobInfoDO = new SPGJobInfoDO();
        jobInfoDO.setId(builderJobInfo.getJobId());
        jobInfoDO.setName(builderJobInfo.getJobName());
        jobInfoDO.setType(JobTypeEnum.BUILDING.name());
        jobInfoDO.setProjectId(builderJobInfo.getProjectId());
        jobInfoDO.setCron(builderJobInfo.getCron());
        jobInfoDO.setStatus(builderJobInfo.getStatus().name());
        jobInfoDO.setExtInfo(JSON.serialize(builderJobInfo.getParams()));
        jobInfoDO.setContent(JSON.serialize(builderJobInfo.getPipeline()));
        jobInfoDO.setExternalJobInfoId(builderJobInfo.getExternalJobInfoId());
        return jobInfoDO;
    }

    public static BuilderJobInfo toModel(SPGJobInfoDO jobInfoDO) {
        if (jobInfoDO == null) {
            return null;
        }
        if (!JobTypeEnum.BUILDING.name().equals(jobInfoDO.getType())) {
            return null;
        }
        return new BuilderJobInfo(
            jobInfoDO.getName(),
            jobInfoDO.getProjectId(),
            JSON.deserialize(jobInfoDO.getContent(), Pipeline.class),
            jobInfoDO.getCron(),
            JobInfoStateEnum.valueOf(jobInfoDO.getStatus()),
            JSON.deserialize(jobInfoDO.getExtInfo(),
                new TypeToken<Map<String, Object>>() {
                }.getType())
        ).setJobId(jobInfoDO.getId());
    }
}
