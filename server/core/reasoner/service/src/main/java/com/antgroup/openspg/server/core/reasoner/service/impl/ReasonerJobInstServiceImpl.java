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

package com.antgroup.openspg.server.core.reasoner.service.impl;

import com.antgroup.openspg.api.facade.dto.reasoner.request.ReasonerJobInstQuery;
import com.antgroup.openspg.cloudext.interfaces.computing.ComputingClient;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.ReasonerJobCanSubmitQuery;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.ReasonerJobProcessQuery;
import com.antgroup.openspg.cloudext.interfaces.computing.cmd.ReasonerJobSubmitCmd;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.JobSchedulerClient;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.JobTypeEnum;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.SchedulerJobInst;
import com.antgroup.openspg.common.model.datasource.connection.GraphStoreConnectionInfo;
import com.antgroup.openspg.common.model.datasource.connection.TableStoreConnectionInfo;
import com.antgroup.openspg.common.model.job.JobInstStatusEnum;
import com.antgroup.openspg.common.service.config.AppEnvConfig;
import com.antgroup.openspg.common.service.datasource.DataSourceService;
import com.antgroup.openspg.core.spgreasoner.model.service.FailureReasonerResult;
import com.antgroup.openspg.core.spgreasoner.model.service.ReasonerJobInfo;
import com.antgroup.openspg.core.spgreasoner.model.service.ReasonerJobInst;
import com.antgroup.openspg.core.spgreasoner.model.service.ReasonerStatusWithProgress;
import com.antgroup.openspg.core.spgreasoner.service.ReasonerJobInfoService;
import com.antgroup.openspg.core.spgreasoner.service.ReasonerJobInstService;
import com.antgroup.openspg.core.spgreasoner.service.repo.ReasonerJobInstRepository;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReasonerJobInstServiceImpl implements ReasonerJobInstService {

  @Autowired private AppEnvConfig appEnvConfig;

  @Autowired private ReasonerJobInstRepository reasonerJobInstRepository;

  @Autowired private DataSourceService dataSourceService;

  @Autowired private ReasonerJobInfoService reasonerJobInfoService;

  @Override
  public Long create(ReasonerJobInfo reasonerJobInfo, ReasonerJobInst reasonerJobInst) {
    JobSchedulerClient jobSchedulerClient = dataSourceService.buildSharedJobSchedulerClient();
    Long buildingJobInstId = reasonerJobInstRepository.save(reasonerJobInst);

    SchedulerJobInst schedulerJobInst =
        new SchedulerJobInst(
            null,
            reasonerJobInfo.getExternalJobInfoId(),
            JobTypeEnum.REASONING.name(),
            reasonerJobInst.getStatus(),
            null,
            String.valueOf(buildingJobInstId));
    String schedulerJobInstId = jobSchedulerClient.createJobInst(schedulerJobInst);

    reasonerJobInst.setExternalJobInstId(schedulerJobInstId);
    reasonerJobInstRepository.updateExternalJobId(buildingJobInstId, schedulerJobInstId);
    return buildingJobInstId;
  }

  @Override
  public List<ReasonerJobInst> query(ReasonerJobInstQuery query) {
    return reasonerJobInstRepository.query(query);
  }

  @Override
  public ReasonerJobInst pollingReasonerJob(SchedulerJobInst jobInst) {
    ComputingClient computingClient = dataSourceService.buildSharedComputingClient();

    ReasonerJobInst reasonerJobInst = queryByExternalJobInstId(jobInst.getJobInstId());
    if (reasonerJobInst.isFinished()) {
      return reasonerJobInst;
    } else if (reasonerJobInst.isRunning()) {
      ReasonerStatusWithProgress progress =
          computingClient.query(
              new ReasonerJobProcessQuery(String.valueOf(reasonerJobInst.getJobInstId())));
      if (progress == null) {
        // if status is null, rerun it
        progress = new ReasonerStatusWithProgress(JobInstStatusEnum.QUEUE);
        reasonerJobInst.setProgress(progress);
        reasonerJobInstRepository.updateStatus(reasonerJobInst.getJobInstId(), progress);
        return reasonerJobInst;
      } else if (progress.getStatus().isRunning()) {
        reasonerJobInstRepository.updateStatus(reasonerJobInst.getJobInstId(), progress);
        return reasonerJobInst;
      } else if (progress.getStatus().isFinished()) {
        reasonerJobInst.setProgress(progress);
        reasonerJobInstRepository.updateStatus(reasonerJobInst.getJobInstId(), progress);
        return reasonerJobInst;
      }
    }

    // The task is not in finished or running status, try to submit the task
    if (!computingClient.canSubmit(new ReasonerJobCanSubmitQuery())) {
      return reasonerJobInst;
    }

    ReasonerJobInfo reasonerJobInfo = reasonerJobInfoService.queryById(reasonerJobInst.getJobId());
    String submit =
        computingClient.submit(
            new ReasonerJobSubmitCmd(
                reasonerJobInst,
                reasonerJobInfo,
                (GraphStoreConnectionInfo)
                    dataSourceService.buildSharedKgStoreClient().getConnInfo(),
                (TableStoreConnectionInfo)
                    dataSourceService.buildSharedTableStoreClient().getConnInfo(),
                appEnvConfig.getSchemaUri(),
                new HashMap<>(5)));
    if (submit != null) {
      // The task is submitted successfully and the task is set to running status.
      ReasonerStatusWithProgress progress =
          new ReasonerStatusWithProgress(JobInstStatusEnum.RUNNING, null, null);
      reasonerJobInstRepository.updateStatus(reasonerJobInst.getJobInstId(), progress);
      return reasonerJobInst;
    }
    return reasonerJobInst;
  }

  @Override
  public ReasonerJobInst queryByExternalJobInstId(String externalJobInstId) {
    ReasonerJobInstQuery query = new ReasonerJobInstQuery().setExternalJobInstId(externalJobInstId);
    List<ReasonerJobInst> jobInsts = reasonerJobInstRepository.query(query);
    return CollectionUtils.isNotEmpty(jobInsts) ? jobInsts.get(0) : null;
  }

  @Override
  public int updateToFailure(Long jobInstId, FailureReasonerResult result) {
    return reasonerJobInstRepository.updateToFailure(jobInstId, result);
  }
}
