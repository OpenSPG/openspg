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
package com.antgroup.openspg.server.core.scheduler.service.api.impl;

import com.antgroup.openspg.common.util.SchedulerUtils;
import com.antgroup.openspg.server.common.model.exception.OpenSPGException;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.LifeCycle;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.Status;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.api.SchedulerService;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerCommonService;
import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerExecuteService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerJobService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/** Scheduler Service implementation class */
@Service
public class SchedulerServiceImpl implements SchedulerService {

  public static final String DEFAULT_VERSION = "V3";

  private ThreadPoolExecutor instanceExecutor =
      new ThreadPoolExecutor(1, 20, 30, TimeUnit.MINUTES, new LinkedBlockingQueue<>(100));

  @Autowired SchedulerJobService schedulerJobService;
  @Autowired SchedulerInstanceService schedulerInstanceService;
  @Autowired SchedulerTaskService schedulerTaskService;
  @Autowired SchedulerCommonService schedulerCommonService;
  @Autowired SchedulerExecuteService schedulerExecuteService;

  @Override
  public SchedulerJob submitJob(SchedulerJob job) {
    setJobPropertyDefaultValue(job);
    checkJobPropertyValidity(job);
    Long id = job.getId();
    id = (id == null ? schedulerJobService.insert(job) : schedulerJobService.update(job));
    this.executeJob(id);
    return job;
  }

  /** set Job Property Default Value */
  private void setJobPropertyDefaultValue(SchedulerJob job) {
    job.setGmtModified(new Date());
    if (job.getGmtCreate() == null) {
      job.setGmtCreate(new Date());
    }

    job.setStatus(Status.ENABLE);
    job.setVersion(DEFAULT_VERSION);
  }

  /** check Job Property is validity */
  private void checkJobPropertyValidity(SchedulerJob job) {
    Assert.notNull(job, "job not null");
    Assert.notNull(job.getProjectId(), "ProjectId not null");
    Assert.hasText(job.getName(), "Name not null");
    Assert.hasText(job.getCreateUser(), "CreateUser not null");
    Assert.notNull(job.getLifeCycle(), "LifeCycle not null");
    Assert.notNull(job.getTranslateType(), "TranslateType not null");
    Assert.notNull(job.getDependence(), "MergeMode not null");

    if (LifeCycle.PERIOD.equals(job.getLifeCycle())) {
      Assert.hasText(job.getSchedulerCron(), "SchedulerCron not null");
      SchedulerUtils.getCronExpression(job.getSchedulerCron());
    }
  }

  @Override
  public Boolean executeJob(Long jobId) {
    List<SchedulerInstance> instances = Lists.newArrayList();
    SchedulerJob job = schedulerJobService.getById(jobId);

    if (LifeCycle.REAL_TIME.equals(job.getLifeCycle())) {
      stopJobAllInstance(jobId);
      instances.add(schedulerCommonService.generateInstance(job));
    } else if (LifeCycle.PERIOD.equals(job.getLifeCycle())) {
      instances.addAll(schedulerCommonService.generatePeriodInstance(job));
    } else if (LifeCycle.ONCE.equals(job.getLifeCycle())) {
      instances.add(schedulerCommonService.generateInstance(job));
    }

    if (CollectionUtils.isEmpty(instances)) {
      return false;
    }

    for (SchedulerInstance ins : instances) {
      Long instanceId = ins.getId();
      Runnable instanceRunnable = () -> schedulerExecuteService.executeInstance(instanceId);
      instanceExecutor.execute(instanceRunnable);
    }
    return true;
  }

  /** stop all not finish instance by job id */
  private void stopJobAllInstance(Long jobId) {
    SchedulerInstance query = new SchedulerInstance();
    query.setJobId(jobId);
    List<SchedulerInstance> instances = schedulerInstanceService.getNotFinishInstance(query);
    if (CollectionUtils.isEmpty(instances)) {
      return;
    }

    for (SchedulerInstance instance : instances) {
      stopInstance(instance.getId());
    }
  }

  @Override
  public Boolean enableJob(Long jobId) {
    SchedulerJob job = schedulerJobService.getById(jobId);
    SchedulerJob updateJob = new SchedulerJob();
    updateJob.setId(jobId);
    updateJob.setStatus(Status.ENABLE);
    Long flag = schedulerJobService.update(updateJob);
    if (flag <= 0) {
      return false;
    }

    if (LifeCycle.REAL_TIME.equals(job.getLifeCycle())) {
      this.executeJob(jobId);
    }
    return true;
  }

  @Override
  public Boolean disableJob(Long jobId) {
    SchedulerJob updateJob = new SchedulerJob();
    updateJob.setId(jobId);
    updateJob.setStatus(Status.DISABLE);
    Long flag = schedulerJobService.update(updateJob);
    if (flag <= 0) {
      return false;
    }

    stopJobAllInstance(jobId);
    return true;
  }

  @Override
  public Boolean deleteJob(Long jobId) {
    stopJobAllInstance(jobId);
    schedulerJobService.deleteById(jobId);
    schedulerInstanceService.deleteByJobId(jobId);
    schedulerTaskService.deleteByJobId(jobId);
    return true;
  }

  @Override
  public boolean updateJob(SchedulerJob job) {
    Long id = schedulerJobService.update(job);
    return id > 0;
  }

  @Override
  public SchedulerJob getJobById(Long jobId) {
    return schedulerJobService.getById(jobId);
  }

  @Override
  public List<SchedulerJob> searchJobs(SchedulerJob query) {
    return schedulerJobService.query(query);
  }

  @Override
  public SchedulerInstance getInstanceById(Long instanceId) {
    return schedulerInstanceService.getById(instanceId);
  }

  @Override
  public Boolean stopInstance(Long instanceId) {
    SchedulerInstance instance = schedulerInstanceService.getById(instanceId);
    schedulerCommonService.setInstanceFinish(
        instance, InstanceStatus.TERMINATE, TaskStatus.TERMINATE);
    return true;
  }

  @Override
  public Boolean setFinishInstance(Long instanceId) {
    SchedulerInstance instance = schedulerInstanceService.getById(instanceId);
    schedulerCommonService.setInstanceFinish(
        instance, InstanceStatus.SET_FINISH, TaskStatus.SET_FINISH);
    return true;
  }

  @Override
  public Boolean restartInstance(Long instanceId) {
    SchedulerInstance instance = schedulerInstanceService.getById(instanceId);
    SchedulerJob job = schedulerJobService.getById(instance.getJobId());
    SchedulerInstance reRunInstance = schedulerCommonService.generateInstance(job);
    Long id = reRunInstance.getId();
    Runnable instanceRunnable = () -> schedulerExecuteService.executeInstance(id);
    instanceExecutor.execute(instanceRunnable);
    return true;
  }

  @Override
  public Boolean triggerInstance(Long instanceId) {
    SchedulerInstance instance = schedulerInstanceService.getById(instanceId);
    if (InstanceStatus.isFinished(instance.getStatus())) {
      throw new OpenSPGException("The instance has been finished");
    }
    schedulerExecuteService.executeInstance(instanceId);
    return true;
  }

  @Override
  public List<SchedulerInstance> searchInstances(SchedulerInstance query) {
    return schedulerInstanceService.query(query);
  }

  @Override
  public List<SchedulerTask> searchTasks(SchedulerTask query) {
    return schedulerTaskService.query(query);
  }
}
