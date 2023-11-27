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

package com.antgroup.openspg.cloudext.impl.jobscheduler.local.scheduler;

import com.antgroup.openspg.cloudext.impl.jobscheduler.local.service.SchedulerJobInstService;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.SchedulerCallback;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.CallbackResult;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.JobTypeEnum;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.SchedulerJobInst;
import com.antgroup.openspg.common.model.job.JobInstStatusEnum;
import com.antgroup.openspg.common.util.thread.SPGThread;
import com.antgroup.openspg.common.util.thread.ThreadUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

@Slf4j
public class JobExecuteScheduler extends BaseScheduler {

  private final SchedulerJobInstService schedulerJobInstService;
  private final Map<String, SchedulerCallback> schedulerCallbacks;

  public JobExecuteScheduler(
      SchedulerJobInstService schedulerJobInstService, List<SchedulerCallback> schedulerCallbacks) {
    this.schedulerJobInstService = schedulerJobInstService;

    this.schedulerCallbacks = new HashMap<>(JobTypeEnum.values().length);
    for (SchedulerCallback schedulerCallback : schedulerCallbacks) {
      for (JobTypeEnum jobType : schedulerCallback.accept()) {
        this.schedulerCallbacks.put(jobType.name(), schedulerCallback);
      }
    }
  }

  public void init() {
    log.info("init JobExecuteScheduler...");
    new SPGThread(
            "JobExecuteScheduler",
            () -> {
              while (true) {
                try {
                  doExecute();
                  ThreadUtils.sleep(3000);
                } catch (Throwable e) {
                  log.warn("JobExecuteScheduler execute fail", e);
                }
              }
            })
        .start();
  }

  private void doExecute() {
    List<SchedulerJobInst> runningJobInsts = schedulerJobInstService.queryRunningJobInsts();
    if (CollectionUtils.isNotEmpty(runningJobInsts)) {
      processJobInsts(runningJobInsts);
      return;
    }

    List<SchedulerJobInst> queuedJobInsts = schedulerJobInstService.queryToRunJobInsts();
    if (CollectionUtils.isNotEmpty(queuedJobInsts)) {
      processJobInsts(queuedJobInsts);
    }
  }

  private void processJobInsts(List<SchedulerJobInst> jobInsts) {
    for (SchedulerJobInst jobInst : jobInsts) {
      log.info(
          "polling jobType={} schedulerJobInstId={}", jobInst.getJobType(), jobInst.getJobInstId());

      SchedulerCallback callback = schedulerCallbacks.get(jobInst.getJobType());
      if (callback == null) {
        updateStatus(jobInst, JobInstStatusEnum.FAILURE);
        continue;
      }
      CallbackResult result = callback.polling(jobInst);
      JobInstStatusEnum newStatus = result.getStatus();
      updateStatus(jobInst, newStatus);
    }
  }

  private void updateStatus(SchedulerJobInst jobInst, JobInstStatusEnum newStatus) {
    if (!newStatus.equals(jobInst.getStatus())) {
      log.info(
          "update schedulerJobInstId={} from status={} to status={}",
          jobInst.getJobInstId(),
          jobInst.getStatus(),
          newStatus);
      schedulerJobInstService.updateStatus(jobInst.getJobInstId(), newStatus);
    }
  }
}
