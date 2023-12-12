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

/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.server.core.scheduler.service.api.impl;

import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.common.model.scheduler.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.LifeCycle;
import com.antgroup.openspg.server.common.model.scheduler.MergeMode;
import com.antgroup.openspg.server.common.model.scheduler.Status;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerJobQuery;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerTaskQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.api.SchedulerService;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerCommonService;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerConstant;
import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerExecuteService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerJobService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.translate.TranslateEnum;
import com.google.common.collect.Lists;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Scheduler Service implementation class
 *
 * @version : SchedulerJobServiceImpl.java, v 0.1 2023-11-30 14:09 $
 */
@Service
public class SchedulerServiceImpl implements SchedulerService {

  private static final int corePoolSize = 1;
  private static final int maximumPoolSize = 20;
  private static final int keepAliveTime = 30;
  private static final int capacity = 100;

  private ThreadPoolExecutor instanceExecutor =
      new ThreadPoolExecutor(
          corePoolSize,
          maximumPoolSize,
          keepAliveTime,
          TimeUnit.MINUTES,
          new LinkedBlockingQueue<>(capacity),
          runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.setName("instanceExecutor" + thread.getId());
            return thread;
          },
          new ThreadPoolExecutor.DiscardOldestPolicy());

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

    if (id == null || id < 0) {
      schedulerJobService.insert(job);
    } else {
      schedulerJobService.update(job);
    }

    this.executeJob(job.getId());

    return job;
  }

  private void setJobPropertyDefaultValue(SchedulerJob job) {
    job.setGmtModified(new Date());
    if (job.getId() == null) {
      job.setGmtCreate(new Date());
    }
    if (job.getMergeMode() == null) {
      job.setMergeMode(MergeMode.MERGE.name());
    }

    job.setStatus(Status.ONLINE.name());
    job.setVersion(
        SchedulerConstant.JOB_DEFAULT_VERSION
            + SchedulerConstant.UNDERLINE_SEPARATOR
            + DateTimeUtils.getDate2Str(DateTimeUtils.YYYY_MM_DD_HH_MM_SS2, new Date()));
  }

  private void checkJobPropertyValidity(SchedulerJob job) {
    Assert.notNull(job, "job not null");
    Assert.notNull(job.getProjectId(), "ProjectId not null");
    Assert.hasText(job.getName(), "Name not null");
    Assert.hasText(job.getCreateUser(), "CreateUser not null");
    Assert.hasText(job.getLifeCycle(), "LifeCycle not null");
    Assert.notNull(
        LifeCycle.getByName(job.getLifeCycle()),
        String.format("LifeCycle:%s not in enum", job.getLifeCycle()));
    Assert.hasText(job.getTranslate(), "Type not null");
    Assert.notNull(
        TranslateEnum.getByName(job.getTranslate()),
        String.format("Type:%s not in enum", job.getTranslate()));
    Assert.notNull(
        MergeMode.getByName(job.getMergeMode()),
        String.format("MergeMode:%s not in enum", job.getMergeMode()));

    if (LifeCycle.PERIOD.name().equalsIgnoreCase(job.getLifeCycle())) {
      Assert.hasText(job.getSchedulerCron(), "SchedulerCron not null");
      try {
        new CronExpression(job.getSchedulerCron());
      } catch (ParseException e) {
        new RuntimeException(
            String.format("Cron(%s) ParseException:%s", job.getSchedulerCron(), e.getMessage()));
      }
    }
  }

  @Override
  public Boolean executeJob(Long id) {
    SchedulerInstance instance = null;
    List<SchedulerInstance> instances = Lists.newArrayList();
    SchedulerJob job = schedulerJobService.getById(id);
    Assert.notNull(job, String.format("job not find id:%s", id));
    LifeCycle lifeCycle = LifeCycle.valueOf(job.getLifeCycle());
    switch (lifeCycle) {
      case REAL_TIME:
        stopJobAllInstance(id);
        instance = schedulerCommonService.generateRealTimeInstance(job);
        break;
      case PERIOD:
        instances.addAll(schedulerCommonService.generatePeriodInstance(job));
        break;
      case ONCE:
        instance = schedulerCommonService.generateOnceInstance(job);
        break;
      default:
        break;
    }
    if (instance != null) {
      instances.add(instance);
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

  private void stopJobAllInstance(Long jobId) {
    SchedulerInstanceQuery query = new SchedulerInstanceQuery();
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
    Assert.notNull(job, String.format("job not find id:%s", id));
    SchedulerJob updateJob = new SchedulerJob();
    updateJob.setId(id);
    updateJob.setStatus(Status.ONLINE.name());
    schedulerJobService.update(updateJob);
    if (LifeCycle.REAL_TIME.name().equals(job.getLifeCycle())) {
      this.executeJob(id);
    }
    return true;
  }

  @Override
  public Boolean disableJob(Long id) {
    SchedulerJob job = schedulerJobService.getById(id);
    Assert.notNull(job, String.format("job not find id:%s", id));
    SchedulerJob updateJob = new SchedulerJob();
    updateJob.setId(id);
    updateJob.setStatus(Status.OFFLINE.name());
    schedulerJobService.update(updateJob);
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
  public Page<List<SchedulerJob>> searchJobs(SchedulerJobQuery query) {
    return schedulerJobService.query(query);
  }

  @Override
  public SchedulerInstance getInstanceById(Long id) {
    return schedulerInstanceService.getById(id);
  }

  @Override
  public Boolean stopInstance(Long id) {
    SchedulerInstance instance = schedulerInstanceService.getById(id);
    Assert.notNull(instance, String.format("instance not find id:%s", id));
    schedulerCommonService.setInstanceFinish(
        instance, InstanceStatus.TERMINATE, TaskStatus.TERMINATE);
    return true;
  }

  @Override
  public Boolean setFinishInstance(Long id) {
    SchedulerInstance instance = schedulerInstanceService.getById(id);
    Assert.notNull(instance, String.format("instance not find id:%s", id));
    schedulerCommonService.setInstanceFinish(
        instance, InstanceStatus.SET_FINISH, TaskStatus.SET_FINISH);
    return true;
  }

  @Override
  public Boolean restartInstance(Long id) {
    SchedulerInstance instance = schedulerInstanceService.getById(id);
    Assert.notNull(instance, String.format("instance not find id:%s", id));
    SchedulerJob job = schedulerJobService.getById(instance.getJobId());
    Assert.notNull(job, String.format("job not find id:%s", id));
    SchedulerInstance reRunInstance = schedulerCommonService.generateOnceInstance(job);
    if (reRunInstance == null) {
      return false;
    }
    Long instanceId = reRunInstance.getId();
    Runnable instanceRunnable = () -> schedulerExecuteService.executeInstance(instanceId);
    instanceExecutor.execute(instanceRunnable);
    return true;
  }

  @Override
  public Boolean triggerInstance(Long id) {
    SchedulerInstance instance = schedulerInstanceService.getById(id);
    Assert.notNull(instance, String.format("instance not find id:%s", id));
    if (InstanceStatus.isFinish(instance.getStatus())) {
      throw new RuntimeException("The instance has been finished");
    }
    schedulerExecuteService.executeInstance(id);
    return true;
  }

  @Override
  public Page<List<SchedulerInstance>> searchInstances(SchedulerInstanceQuery query) {
    return schedulerInstanceService.query(query);
  }

  @Override
  public Page<List<SchedulerTask>> searchTasks(SchedulerTaskQuery query) {
    return schedulerTaskService.query(query);
  }
}
