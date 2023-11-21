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

package com.antgroup.openspg.biz.spgbuilder.impl;

import com.antgroup.openspg.api.facade.dto.builder.request.BuilderJobInstQuery;
import com.antgroup.openspg.api.facade.dto.builder.request.BuilderJobSubmitRequest;
import com.antgroup.openspg.biz.spgbuilder.BuilderManager;
import com.antgroup.openspg.common.model.job.JobInfoStateEnum;
import com.antgroup.openspg.common.model.job.JobInstStatusEnum;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.spgbuilder.model.service.BuilderJobInfo;
import com.antgroup.openspg.core.spgbuilder.model.service.BuilderJobInst;
import com.antgroup.openspg.core.spgbuilder.model.service.JobBuilderReceipt;
import com.antgroup.openspg.core.spgbuilder.service.BuilderJobInfoService;
import com.antgroup.openspg.core.spgbuilder.service.BuilderJobInstService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuilderManagerImpl implements BuilderManager {

  @Autowired private BuilderJobInfoService builderJobInfoService;

  @Autowired private BuilderJobInstService builderJobInstService;

  @Override
  @Transactional
  public JobBuilderReceipt submitJobInfo(BuilderJobSubmitRequest request) {
    BuilderJobInfo builderJobInfo =
        new BuilderJobInfo(
            request.getJobName(),
            request.getProjectId(),
            request.getPipeline(),
            request.getCron(),
            JobInfoStateEnum.ENABLE,
            request.getParams());

    // create a builder job
    Long builderJobInfoId = builderJobInfoService.create(builderJobInfo);

    // if the cron expression is empty, create a builder job instance
    Long builderJobInstId = null;
    if (StringUtils.isBlank(request.getCron())) {
      BuilderJobInst builderJobInst =
          new BuilderJobInst(
              builderJobInfoId,
              builderJobInfo.getProjectId(),
              JobInstStatusEnum.INIT,
              null,
              null,
              null,
              null,
              null);
      builderJobInstId = builderJobInstService.create(builderJobInfo, builderJobInst);
    }
    return new JobBuilderReceipt(builderJobInfoId, builderJobInstId);
  }

  @Override
  public List<BuilderJobInst> queryJobInst(BuilderJobInstQuery query) {
    return builderJobInstService.query(query);
  }
}
