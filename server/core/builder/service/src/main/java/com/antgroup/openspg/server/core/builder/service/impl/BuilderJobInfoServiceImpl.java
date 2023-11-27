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

package com.antgroup.openspg.server.core.builder.service.impl;

import com.antgroup.openspg.cloudext.interfaces.jobscheduler.JobSchedulerClient;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.JobTypeEnum;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.SchedulerJobInfo;
import com.antgroup.openspg.server.common.service.datasource.DataSourceService;
import com.antgroup.openspg.core.spgbuilder.service.BuilderJobInfoService;
import com.antgroup.openspg.core.spgbuilder.service.repo.BuilderJobInfoRepository;
import com.antgroup.openspg.server.api.facade.dto.builder.request.BuilderJobInfoQuery;
import com.antgroup.openspg.server.core.builder.model.service.BuilderJobInfo;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuilderJobInfoServiceImpl implements BuilderJobInfoService {

  @Autowired private DataSourceService dataSourceService;

  @Autowired private BuilderJobInfoRepository builderJobInfoRepository;

  @Override
  public Long create(BuilderJobInfo builderJobInfo) {
    JobSchedulerClient jobSchedulerClient = dataSourceService.buildSharedJobSchedulerClient();
    Long builderJobInfoId = builderJobInfoRepository.save(builderJobInfo);

    SchedulerJobInfo schedulerJobInfo =
        new SchedulerJobInfo(
            null,
            builderJobInfo.getJobName(),
            JobTypeEnum.BUILDING.name(),
            builderJobInfo.getCron(),
            builderJobInfo.getStatus(),
            String.valueOf(builderJobInfoId));
    String schedulerJobInfoId = jobSchedulerClient.createJobInfo(schedulerJobInfo);

    builderJobInfo.setExternalJobInfoId(schedulerJobInfoId);
    builderJobInfoRepository.updateExternalJobId(builderJobInfoId, schedulerJobInfoId);
    return builderJobInfoId;
  }

  @Override
  public BuilderJobInfo queryById(Long jobId) {
    List<BuilderJobInfo> builderJobInfos =
        query(new BuilderJobInfoQuery().setBuildingJobInfoId(jobId));
    if (CollectionUtils.isNotEmpty(builderJobInfos)) {
      return builderJobInfos.get(0);
    }
    return null;
  }

  @Override
  public List<BuilderJobInfo> query(BuilderJobInfoQuery query) {
    return builderJobInfoRepository.query(query);
  }
}
