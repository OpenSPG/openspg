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
package com.antgroup.openspg.server.core.scheduler.service.common.impl;

import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.server.common.model.exception.SchedulerException;
import com.antgroup.openspg.server.common.model.scheduler.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.common.service.spring.SpringContextHolder;
import com.antgroup.openspg.server.core.scheduler.model.common.TaskDag;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerCommonService;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerConstant;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerValue;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerJobService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTask;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTaskContext;
import com.antgroup.openspg.server.core.scheduler.service.task.async.JobAsyncTask;
import com.antgroup.openspg.server.core.scheduler.service.translate.TranslatorFactory;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Scheduler Common Service implementation class */
@Service
@Slf4j
public class SchedulerCommonServiceImpl implements SchedulerCommonService {

  @Autowired SchedulerJobService schedulerJobService;
  @Autowired SchedulerInstanceService schedulerInstanceService;
  @Autowired SchedulerTaskService schedulerTaskService;
  @Autowired SchedulerValue schedulerValue;

  @Override
  public void setInstanceFinish(
      SchedulerInstance instance, InstanceStatus instanceStatus, TaskStatus taskStatus) {
    SchedulerInstance updateInstance = new SchedulerInstance();
    updateInstance.setId(instance.getId());
    updateInstance.setStatus(instanceStatus);
    Long finish = 100L;
    updateInstance.setProgress(finish);
    Date finishTime = instance.getFinishTime() == null ? new Date() : instance.getFinishTime();
    updateInstance.setFinishTime(finishTime);

    Long updateNum = schedulerInstanceService.update(updateInstance);
    if (updateNum <= 0) {
      throw new SchedulerException("update instance failed {}", updateInstance);
    }
    stopRunningProcess(instance);

    schedulerTaskService.setStatusByInstanceId(instance.getId(), taskStatus);
  }

  /** stop Running Process */
  private void stopRunningProcess(SchedulerInstance instance) {
    List<SchedulerTask> taskList = schedulerTaskService.queryByInstanceId(instance.getId());
    List<SchedulerTask> processList =
        taskList.stream()
            .filter(s -> TaskStatus.isRunning(s.getStatus()))
            .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(processList)) {
      return;
    }

    SchedulerJob job = schedulerJobService.getById(instance.getJobId());
    processList.forEach(
        task -> {
          try {
            JobTaskContext context = new JobTaskContext(job, instance, task);
            String type = task.getType();
            if (StringUtils.isBlank(type)) {
              log.warn("stop task type is null id:{}", task.getId());
              return;
            }

            type = type.split(SchedulerConstant.UNDERLINE_SEPARATOR)[0];
            JobTask jobTask = SpringContextHolder.getBean(type, JobTask.class);
            if (jobTask == null) {
              log.error("stop task is null id:{}", task.getId());
              return;
            }

            if (jobTask instanceof JobAsyncTask) {
              JobAsyncTask jobAsyncTask = (JobAsyncTask) jobTask;
              jobAsyncTask.stop(context, task.getResource());
            }
          } catch (Exception e) {
            log.error("stop task error id:{}", task.getId());
          }
        });
  }

  /** check Instance is Running within 24H */
  private void checkInstanceRunning(SchedulerJob job) {
    SchedulerInstanceQuery query = new SchedulerInstanceQuery();
    query.setJobId(job.getId());
    query.setStartCreateTime(DateUtils.addDays(new Date(), -1));
    query.setEndCreateTime(new Date());
    List<SchedulerInstance> instances = schedulerInstanceService.query(query).getData();
    instances.stream()
        .forEach(
            instance -> {
              if (!InstanceStatus.isFinished(instance.getStatus())) {
                throw new SchedulerException(
                    "Running instances exist within 24H uniqueId {}", instance.getUniqueId());
              }
            });
  }

  @Override
  public SchedulerInstance generateOnceInstance(SchedulerJob job) {
    checkInstanceRunning(job);
    Date schedulerDate = new Date();
    String uniqueId = job.getId().toString() + System.currentTimeMillis();
    return generateInstance(job, uniqueId, schedulerDate);
  }

  @Override
  public List<SchedulerInstance> generatePeriodInstance(SchedulerJob job) {
    List<SchedulerInstance> instances = Lists.newArrayList();
    List<Date> executionDates = CommonUtils.getCronExecutionDatesByToday(job.getSchedulerCron());
    for (Date schedulerDate : executionDates) {
      String uniqueId = CommonUtils.getUniqueId(job.getId(), schedulerDate);
      SchedulerInstance instance = generateInstance(job, uniqueId, schedulerDate);
      if (instance == null) {
        continue;
      }

      instances.add(instance);
    }
    return instances;
  }

  @Override
  public SchedulerInstance generateRealTimeInstance(SchedulerJob job) {
    checkInstanceRunning(job);
    Date schedulerDate = new Date();
    String uniqueId = job.getId().toString() + System.currentTimeMillis();
    return generateInstance(job, uniqueId, schedulerDate);
  }

  @Override
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
    instance.setMergeMode(job.getMergeMode());
    instance.setVersion(SchedulerConstant.DEFAULT_VERSION);
    TaskDag taskDag = TranslatorFactory.getTranslator(job.getTranslateType()).translate(job);
    instance.setTaskDag(taskDag);

    schedulerInstanceService.insert(instance);
    log.info("generateInstance successful jobId:{} uniqueId:{}", job.getId(), uniqueId);

    List<TaskDag.Node> nodes = taskDag.getNodes();
    nodes.forEach(
        node -> {
          TaskStatus status =
              CollectionUtils.isEmpty(taskDag.getRelatedNodes(node.getId(), false))
                  ? TaskStatus.RUNNING
                  : TaskStatus.WAIT;
          schedulerTaskService.insert(new SchedulerTask(instance, status, node));
        });

    SchedulerJob updateJob = new SchedulerJob();
    updateJob.setId(job.getId());
    updateJob.setLastExecuteTime(schedulerDate);
    schedulerJobService.update(updateJob);

    return instance;
  }
}
