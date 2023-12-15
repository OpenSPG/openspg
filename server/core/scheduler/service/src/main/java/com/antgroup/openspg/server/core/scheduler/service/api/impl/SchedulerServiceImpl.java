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

import com.antgroup.openspg.common.util.CommonUtils;
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

    job.setStatus(Status.ONLINE);
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
    Assert.notNull(job.getMergeMode(), "MergeMode not null");

    if (LifeCycle.PERIOD.equals(job.getLifeCycle())) {
      Assert.hasText(job.getSchedulerCron(), "SchedulerCron not null");
      CommonUtils.getCronExpression(job.getSchedulerCron());
    }
  }

  @Override
  public Boolean executeJob(Long id) {
    List<SchedulerInstance> instances = Lists.newArrayList();
    SchedulerJob job = schedulerJobService.getById(id);

    if (LifeCycle.REAL_TIME.equals(job.getLifeCycle())) {
      stopJobAllInstance(id);
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
  public Boolean enableJob(Long id) {
    SchedulerJob job = schedulerJobService.getById(id);
    SchedulerJob updateJob = new SchedulerJob();
    updateJob.setId(id);
    updateJob.setStatus(Status.ONLINE);
    Long flag = schedulerJobService.update(updateJob);
    if (flag <= 0) {
      return false;
    }

    if (LifeCycle.REAL_TIME.equals(job.getLifeCycle())) {
      this.executeJob(id);
    }
    return true;
  }

  @Override
  public Boolean disableJob(Long id) {
    SchedulerJob updateJob = new SchedulerJob();
    updateJob.setId(id);
    updateJob.setStatus(Status.OFFLINE);
    Long flag = schedulerJobService.update(updateJob);
    if (flag <= 0) {
      return false;
    }

    stopJobAllInstance(id);
    return true;
  }

  @Override
  public Boolean deleteJob(Long id) {
    stopJobAllInstance(id);
    schedulerJobService.deleteById(id);
    schedulerInstanceService.deleteByJobId(id);
    schedulerTaskService.deleteByJobId(id);
    return true;
  }

  @Override
  public boolean updateJob(SchedulerJob job) {
    Long id = schedulerJobService.update(job);
    return id > 0;
  }

  @Override
  public SchedulerJob getJobById(Long id) {
    return schedulerJobService.getById(id);
  }

  @Override
  public List<SchedulerJob> searchJobs(SchedulerJob query) {
    return schedulerJobService.query(query);
  }

  @Override
  public SchedulerInstance getInstanceById(Long id) {
    return schedulerInstanceService.getById(id);
  }

  @Override
  public Boolean stopInstance(Long id) {
    SchedulerInstance instance = schedulerInstanceService.getById(id);
    schedulerCommonService.setInstanceFinish(
        instance, InstanceStatus.TERMINATE, TaskStatus.TERMINATE);
    return true;
  }

  @Override
  public Boolean setFinishInstance(Long id) {
    SchedulerInstance instance = schedulerInstanceService.getById(id);
    schedulerCommonService.setInstanceFinish(
        instance, InstanceStatus.SET_FINISH, TaskStatus.SET_FINISH);
    return true;
  }

  @Override
  public Boolean restartInstance(Long id) {
    SchedulerInstance instance = schedulerInstanceService.getById(id);
    SchedulerJob job = schedulerJobService.getById(instance.getJobId());
    SchedulerInstance reRunInstance = schedulerCommonService.generateInstance(job);
    Long instanceId = reRunInstance.getId();
    Runnable instanceRunnable = () -> schedulerExecuteService.executeInstance(instanceId);
    instanceExecutor.execute(instanceRunnable);
    return true;
  }

  @Override
  public Boolean triggerInstance(Long id) {
    SchedulerInstance instance = schedulerInstanceService.getById(id);
    if (InstanceStatus.isFinished(instance.getStatus())) {
      throw new OpenSPGException("The instance has been finished");
    }
    schedulerExecuteService.executeInstance(id);
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
