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

package com.antgroup.openspg.cloudext.impl.jobscheduler.local;

import com.antgroup.openspg.cloudext.impl.jobscheduler.local.scheduler.JobExecuteScheduler;
import com.antgroup.openspg.cloudext.impl.jobscheduler.local.service.SchedulerJobInfoService;
import com.antgroup.openspg.cloudext.impl.jobscheduler.local.service.SchedulerJobInstService;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.JobSchedulerClient;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.SchedulerCallback;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.SchedulerJobInfo;
import com.antgroup.openspg.cloudext.interfaces.jobscheduler.model.SchedulerJobInst;
import com.antgroup.openspg.server.common.model.datasource.connection.JobSchedulerConnectionInfo;
import com.antgroup.openspg.server.common.service.spring.SpringContextHolder;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class LocalJobSchedulerClient implements JobSchedulerClient {

  @Getter private final JobSchedulerConnectionInfo connInfo;
  @Getter private final List<SchedulerCallback> schedulerCallbacks;

  @Setter private SchedulerJobInfoService jobInfoService;
  @Setter private SchedulerJobInstService jobInstService;

  public LocalJobSchedulerClient(JobSchedulerConnectionInfo connInfo) {
    this.connInfo = connInfo;
    jobInfoService = SpringContextHolder.getBean(SchedulerJobInfoService.class);
    jobInstService = SpringContextHolder.getBean(SchedulerJobInstService.class);
    schedulerCallbacks = SpringContextHolder.getBeans(SchedulerCallback.class);
    new JobExecuteScheduler(jobInstService, schedulerCallbacks).init();
  }

  @Override
  public String createJobInfo(SchedulerJobInfo jobInfo) {
    return jobInfoService.create(jobInfo);
  }

  @Override
  public String createJobInst(SchedulerJobInst jobInst) {
    return jobInstService.create(jobInst);
  }

  @Override
  public void close() throws Exception {}
}
