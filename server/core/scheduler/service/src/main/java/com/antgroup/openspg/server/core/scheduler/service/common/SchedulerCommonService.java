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
package com.antgroup.openspg.server.core.scheduler.service.common;

import com.antgroup.openspg.common.util.SchedulerUtils;
import com.antgroup.openspg.server.common.model.exception.SchedulerException;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.TaskStatus;
import com.antgroup.openspg.server.common.service.spring.SpringContextHolder;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteContext;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteDag;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerJobService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.task.TaskExecute;
import com.antgroup.openspg.server.core.scheduler.service.task.async.AsyncTaskExecute;
import com.antgroup.openspg.server.core.scheduler.service.translate.TranslatorFactory;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/** Scheduler Common Service */
@Service
@Slf4j
public class SchedulerCommonService {

  public static final String UNDERLINE_SEPARATOR = "_";

  @Autowired SchedulerJobService schedulerJobService;
  @Autowired SchedulerInstanceService schedulerInstanceService;
  @Autowired SchedulerTaskService schedulerTaskService;
  @Autowired SchedulerValue schedulerValue;

  /** set Instance To Finish */
  public void setInstanceFinish(
      SchedulerInstance instance, InstanceStatus instanceStatus, TaskStatus taskStatus) {
    Long finish = 100L;
    Date finishTime = (instance.getFinishTime() == null ? new Date() : instance.getFinishTime());
    SchedulerInstance updateInstance = new SchedulerInstance();
    updateInstance.setId(instance.getId());
    updateInstance.setStatus(instanceStatus);
    updateInstance.setProgress(finish);
    updateInstance.setFinishTime(finishTime);
    Long updateNum = schedulerInstanceService.update(updateInstance);
    Assert.isTrue(updateNum > 0, "update instance failed " + updateInstance);
    stopRunningTasks(instance);
    schedulerTaskService.setStatusByInstanceId(instance.getId(), taskStatus);
  }

  /** stop Running Tasks */
  private void stopRunningTasks(SchedulerInstance instance) {
    List<SchedulerTask> taskList = schedulerTaskService.queryByInstanceId(instance.getId());

    SchedulerJob job = schedulerJobService.getById(instance.getJobId());

    for (SchedulerTask task : taskList) {
      if (!TaskStatus.isRunning(task.getStatus()) || StringUtils.isBlank(task.getType())) {
        continue;
      }

      String type = task.getType().split(UNDERLINE_SEPARATOR)[0];
      TaskExecute jobTask = SpringContextHolder.getBean(type, TaskExecute.class);
      if (jobTask != null && jobTask instanceof AsyncTaskExecute) {
        AsyncTaskExecute jobAsyncTask = (AsyncTaskExecute) jobTask;
        TaskExecuteContext context = new TaskExecuteContext(job, instance, task);
        jobAsyncTask.stop(context, task.getResource());
      } else {
        log.warn("get bean is null or not instance of JobAsyncTask id: {}", task.getId());
      }
    }
  }

  /** check Instance is Running within 24H */
  private void checkInstanceRunning(SchedulerJob job) {
    SchedulerInstance query = new SchedulerInstance();
    query.setJobId(job.getId());
    query.setStartCreateTime(DateUtils.addDays(new Date(), -1));
    List<SchedulerInstance> instances = schedulerInstanceService.query(query);
    for (SchedulerInstance instance : instances) {
      if (!InstanceStatus.isFinished(instance.getStatus())) {
        throw new SchedulerException("Running {} exist within 24H", instance.getUniqueId());
      }
    }
  }

  /** generate Period Instance by Cron */
  public List<SchedulerInstance> generatePeriodInstance(SchedulerJob job) {
    List<SchedulerInstance> instances = Lists.newArrayList();
    List<Date> executionDates = SchedulerUtils.getCronExecutionDatesByToday(job.getSchedulerCron());
    for (Date schedulerDate : executionDates) {
      String uniqueId = SchedulerUtils.getUniqueId(job.getId(), schedulerDate);
      SchedulerInstance instance = generateInstance(job, uniqueId, schedulerDate);
      if (instance != null) {
        instances.add(instance);
      }
    }
    return instances;
  }

  /** generate Once/RealTime Instance */
  public SchedulerInstance generateInstance(SchedulerJob job) {
    checkInstanceRunning(job);
    String uniqueId = job.getId().toString() + System.currentTimeMillis();
    return generateInstance(job, uniqueId, new Date());
  }

  /** generate Instance by schedulerDate */
  public SchedulerInstance generateInstance(SchedulerJob job, String uniqueId, Date schedulerDate) {
    SchedulerInstance existInstance = schedulerInstanceService.getByUniqueId(uniqueId);
    if (existInstance != null) {
      log.error("generateInstance uniqueId exist jobId:{} uniqueId:{}", job.getId(), uniqueId);
      return null;
    }

    log.info("generateInstance start jobId:{} uniqueId:{}", job.getId(), uniqueId);
    Long progress = 0L;
    SchedulerInstance instance = new SchedulerInstance();
    instance.setUniqueId(uniqueId);
    instance.setProjectId(job.getProjectId());
    instance.setJobId(job.getId());
    instance.setType(job.getTranslateType().getType());
    instance.setStatus(InstanceStatus.WAITING);
    instance.setProgress(progress);
    instance.setCreateUser(job.getCreateUser());
    instance.setGmtCreate(new Date());
    instance.setGmtModified(new Date());
    instance.setLifeCycle(job.getLifeCycle());
    instance.setSchedulerDate(schedulerDate);
    instance.setDependence(job.getDependence());
    instance.setVersion(job.getVersion());
    TaskExecuteDag taskDag = TranslatorFactory.getTranslator(job.getTranslateType()).translate(job);
    instance.setTaskDag(taskDag);

    schedulerInstanceService.insert(instance);
    log.info("generateInstance successful jobId:{} uniqueId:{}", job.getId(), uniqueId);

    for (TaskExecuteDag.Node node : taskDag.getNodes()) {
      List<TaskExecuteDag.Node> pres = taskDag.getRelatedNodes(node.getId(), false);
      TaskStatus status = CollectionUtils.isEmpty(pres) ? TaskStatus.RUNNING : TaskStatus.WAIT;
      schedulerTaskService.insert(new SchedulerTask(instance, status, node));
    }

    SchedulerJob updateJob = new SchedulerJob();
    updateJob.setId(job.getId());
    updateJob.setLastExecuteTime(schedulerDate);
    schedulerJobService.update(updateJob);

    return instance;
  }
}
