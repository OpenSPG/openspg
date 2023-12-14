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
package com.antgroup.openspg.server.core.scheduler.service.engine.impl;

import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.LifeCycle;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.Status;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerCommonService;
import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerGenerateService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerJobService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Scheduler Generate Service implementation class. generate instances by all period Job */
@Service
@Slf4j
public class SchedulerGenerateServiceImpl implements SchedulerGenerateService {

  @Autowired SchedulerJobService schedulerJobService;
  @Autowired SchedulerCommonService schedulerCommonService;

  @Override
  public void generateInstances() {
    List<SchedulerJob> allJob = getAllPeriodJobs();
    log.info("getAllPeriodJob successful size:{}", allJob.size());
    if (CollectionUtils.isEmpty(allJob)) {
      return;
    }
    generatePeriodInstance(allJob);
  }

  /** generate all Period Instance */
  public void generatePeriodInstance(List<SchedulerJob> allJob) {
    for (SchedulerJob job : allJob) {
      try {
        List<SchedulerInstance> instances = schedulerCommonService.generatePeriodInstance(job);
        log.info("generate successful jobId:{} size:{}", job.getId(), instances.size());
      } catch (Exception e) {
        log.error("generate error jobId:{}", job.getId(), e);
      }
    }
  }

  /** get all Period Jobs */
  private List<SchedulerJob> getAllPeriodJobs() {
    SchedulerJob record = new SchedulerJob();
    record.setLifeCycle(LifeCycle.PERIOD);
    record.setStatus(Status.ONLINE);
    List<SchedulerJob> allJob = schedulerJobService.query(record);
    return allJob;
  }
}
