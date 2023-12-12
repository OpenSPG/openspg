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

import com.antgroup.openspg.server.common.model.scheduler.LifeCycle;
import com.antgroup.openspg.server.common.model.scheduler.Status;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerJobQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerCommonService;
import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerGenerateService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerJobService;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Scheduler Generate Service implementation class. generate instances by all period Job */
@Service
public class SchedulerGenerateServiceImpl implements SchedulerGenerateService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerGenerateServiceImpl.class);

  @Autowired SchedulerJobService schedulerJobService;
  @Autowired SchedulerCommonService schedulerCommonService;

  @Override
  public void generateInstances() {
    List<SchedulerJob> allJob = getAllPeriodJobs();
    LOGGER.info(String.format("getAllPeriodJob succeed size:%s", allJob.size()));
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
        LOGGER.info(
            String.format(
                "generatePeriodInstance successful jobId:%s instances:%s",
                job.getId(), instances.size()));
      } catch (Exception e) {
        LOGGER.error(String.format("generatePeriodInstance error,job:%s", job.getId()), e);
      }
    }
  }

  /** get all Period Jobs */
  private List<SchedulerJob> getAllPeriodJobs() {
    SchedulerJobQuery record = new SchedulerJobQuery();
    record.setLifeCycle(LifeCycle.PERIOD.name());
    record.setStatus(Status.ONLINE.name());
    List<SchedulerJob> allJob = schedulerJobService.query(record).getData();
    return allJob;
  }
}
