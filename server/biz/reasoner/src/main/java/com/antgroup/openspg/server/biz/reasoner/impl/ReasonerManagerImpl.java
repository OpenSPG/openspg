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

package com.antgroup.openspg.server.biz.reasoner.impl;

import com.antgroup.openspg.cloudext.interfaces.computing.ComputingClient;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.ReasonerJobRunCmd;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.api.facade.dto.reasoner.request.ReasonerDslRunRequest;
import com.antgroup.openspg.server.api.facade.dto.reasoner.request.ReasonerJobInstQuery;
import com.antgroup.openspg.server.api.facade.dto.reasoner.request.ReasonerJobSubmitRequest;
import com.antgroup.openspg.server.biz.reasoner.ReasonerManager;
import com.antgroup.openspg.server.common.model.datasource.connection.GraphStoreConnectionInfo;
import com.antgroup.openspg.server.common.model.job.JobInfoStateEnum;
import com.antgroup.openspg.server.common.model.job.JobInstStatusEnum;
import com.antgroup.openspg.server.common.service.config.AppEnvConfig;
import com.antgroup.openspg.server.common.service.datasource.DataSourceService;
import com.antgroup.openspg.server.core.reasoner.model.service.JobReasonerReceipt;
import com.antgroup.openspg.server.core.reasoner.model.service.ReasonerJobInfo;
import com.antgroup.openspg.server.core.reasoner.model.service.ReasonerJobInst;
import com.antgroup.openspg.server.core.reasoner.model.service.TableReasonerReceipt;
import com.antgroup.openspg.server.core.reasoner.service.ReasonerJobInfoService;
import com.antgroup.openspg.server.core.reasoner.service.ReasonerJobInstService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReasonerManagerImpl implements ReasonerManager {

  @Autowired private AppEnvConfig appEnvConfig;

  @Autowired private ReasonerJobInfoService reasonerJobInfoService;

  @Autowired private ReasonerJobInstService reasonerJobInstService;

  @Autowired private DataSourceService dataSourceService;

  @Override
  public TableReasonerReceipt runDsl(ReasonerDslRunRequest request) {
    ComputingClient computingClient = dataSourceService.buildSharedComputingClient();
    return computingClient.run(
        new ReasonerJobRunCmd(
            request.getProjectId(),
            appEnvConfig.getSchemaUri(),
            (GraphStoreConnectionInfo) dataSourceService.buildSharedKgStoreClient().getConnInfo(),
            request.getContent()));
  }

  @Override
  public JobReasonerReceipt submitJob(ReasonerJobSubmitRequest request) {
    ReasonerJobInfo reasonerJobInfo =
        new ReasonerJobInfo(
            request.getJobName(),
            request.getProjectId(),
            request.getContent(),
            request.getCron(),
            JobInfoStateEnum.ENABLE,
            request.getParams());

    // create a reasoner job
    Long reasonerJobInfoId = reasonerJobInfoService.create(reasonerJobInfo);

    // if the cron expression is empty, create a reasoner job instance
    Long reasonerJobInstId = null;
    if (StringUtils.isBlank(request.getCron())) {
      ReasonerJobInst reasonerJobInst =
          new ReasonerJobInst(
              reasonerJobInfoId,
              reasonerJobInfo.getProjectId(),
              JobInstStatusEnum.INIT,
              null,
              null,
              null,
              null,
              null);
      reasonerJobInstId = reasonerJobInstService.create(reasonerJobInfo, reasonerJobInst);
    }
    return new JobReasonerReceipt(reasonerJobInfoId, reasonerJobInstId);
  }

  @Override
  public List<ReasonerJobInst> queryJobInst(ReasonerJobInstQuery query) {
    return reasonerJobInstService.query(query);
  }
}
