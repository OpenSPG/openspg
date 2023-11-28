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

import com.antgroup.openspg.cloudext.interfaces.jobscheduler.SchedulerCallback;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.CallbackResult;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.JobTypeEnum;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.SchedulerJobInst;
import com.antgroup.openspg.core.spgreasoner.service.ReasonerJobInstService;
import com.antgroup.openspg.common.model.job.JobInstStatusEnum;
import com.antgroup.openspg.server.core.reasoner.model.service.FailureReasonerResult;
import com.antgroup.openspg.server.core.reasoner.model.service.ReasonerJobInst;
import com.google.common.collect.Sets;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReasonerSchedulerCallback implements SchedulerCallback {

  @Autowired private ReasonerJobInstService reasonerJobInstService;

  @Override
  public Set<JobTypeEnum> accept() {
    return Sets.newHashSet(JobTypeEnum.REASONING);
  }

  @Override
  public CallbackResult polling(SchedulerJobInst jobInst) {
    ReasonerJobInst reasonerJobInst = null;
    try {
      reasonerJobInst = reasonerJobInstService.pollingReasonerJob(jobInst);
    } catch (Throwable e) {
      log.warn("polling schedulerJobInstId={} for reasoner error", jobInst.getJobInstId(), e);
      reasonerJobInst = reasonerJobInstService.queryByExternalJobInstId(jobInst.getJobInstId());
      FailureReasonerResult result = new FailureReasonerResult(e.getMessage());
      if (reasonerJobInst != null) {
        reasonerJobInstService.updateToFailure(reasonerJobInst.getJobInstId(), result);
      }
      return new CallbackResult(JobInstStatusEnum.FAILURE, result);
    }
    return new CallbackResult(reasonerJobInst.getStatus(), reasonerJobInst.getResult());
  }
}
