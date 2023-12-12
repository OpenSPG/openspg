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
package com.antgroup.openspg.server.core.scheduler.service.engine.impl;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.server.common.model.scheduler.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.common.service.spring.SpringContextHolder;
import com.antgroup.openspg.server.core.scheduler.model.common.WorkflowDag;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerCommonService;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerConstant;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerValue;
import com.antgroup.openspg.server.core.scheduler.service.engine.SchedulerExecuteService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerJobService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTask;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTaskContext;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Scheduler Execute Service implementation class. execute all instances */
@Service
public class SchedulerExecuteServiceImpl implements SchedulerExecuteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerExecuteServiceImpl.class);

  private static final Logger METRIC_LOGGER = LoggerFactory.getLogger("SCHEDULER-METRIC");

  private static final int corePoolSize = 10;

  private ConcurrentHashMap<String, ThreadPoolExecutor> instanceExecutorMap =
      new ConcurrentHashMap<>();
  private ConcurrentHashMap<String, ThreadPoolExecutor> taskExecutorMap = new ConcurrentHashMap<>();
  private ScheduledExecutorService scheduledExecutor =
      new ScheduledThreadPoolExecutor(corePoolSize);

  @Autowired SchedulerValue schedulerValue;
  @Autowired SchedulerJobService schedulerJobService;
  @Autowired SchedulerInstanceService schedulerInstanceService;
  @Autowired SchedulerTaskService schedulerTaskService;
  @Autowired SchedulerCommonService schedulerCommonService;

  @Override
  public void executeInstances() {
    List<SchedulerInstance> allInstance = getAllNotFinishInstance();
    LOGGER.info(String.format("getAllNotFinishInstance succeed size:%s", allInstance.size()));
    if (CollectionUtils.isEmpty(allInstance)) {
      return;
    }
    executeInstances(allInstance);
  }

  /** execute all Instances */
  public void executeInstances(List<SchedulerInstance> allInstance) {
    for (SchedulerInstance instance : allInstance) {
      String type = instance.getType();
      ThreadPoolExecutor executor = getInstanceExecutor(type);
      Runnable instanceRunnable = () -> executeInstance(instance.getId());
      LOGGER.info(
          String.format(
              "instanceExecutor active:%s task:%s completed:%s remaining:%s",
              executor.getActiveCount(),
              executor.getTaskCount(),
              executor.getCompletedTaskCount(),
              executor.getQueue().remainingCapacity()));
      executor.execute(instanceRunnable);
      LOGGER.info(String.format("add instanceExecutor successful:%s", instance.getUniqueId()));
    }
  }

  /** execute Instance by id */
  @Override
  public void executeInstance(Long id) {
    try {
      List<SchedulerTask> tasks = schedulerTaskService.queryBaseColumnByInstanceId(id);
      List<SchedulerTask> processTasks =
          tasks.stream()
              .filter(s -> TaskStatus.isFinished(s.getStatus()))
              .collect(Collectors.toList());
      if (processTasks.size() == tasks.size()) {
        SchedulerInstance instance = schedulerInstanceService.getById(id);
        schedulerCommonService.setInstanceFinish(
            instance, InstanceStatus.FINISH, TaskStatus.FINISH);
        return;
      }
      executeInstance(id, tasks);
    } catch (Exception e) {
      LOGGER.error(String.format("execute instance error id:%s", id), e);
    }
  }

  /** execute Instance by all tasks */
  public void executeInstance(Long id, List<SchedulerTask> tasks) {
    SchedulerInstance instance = schedulerInstanceService.getById(id);
    if (InstanceStatus.isFinished(instance.getStatus())) {
      LOGGER.info(
          String.format("instanceStatus is FINISH slip id:%s status:%s", id, instance.getStatus()));
      return;
    }
    List<SchedulerTask> processList =
        tasks.stream()
            .filter(s -> TaskStatus.isRunning(s.getStatus()))
            .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(processList)) {
      processList = checkAndUpdateWaitStatus(instance, tasks);
    }

    String type = instance.getType();
    ThreadPoolExecutor executor = getTaskExecutor(type);
    processList.forEach(
        task -> {
          Runnable taskRunnable = () -> executeTask(instance, task);
          LOGGER.info(
              String.format(
                  "taskExecutor active:%s task:%s completed:%s remaining:%s",
                  executor.getActiveCount(),
                  executor.getTaskCount(),
                  executor.getCompletedTaskCount(),
                  executor.getQueue().remainingCapacity()));
          executor.execute(taskRunnable);
        });
  }

  /** execute Instance by task */
  public void executeTask(SchedulerInstance instance, SchedulerTask task) {

    long start = System.currentTimeMillis();
    try {
      SchedulerJob job = schedulerJobService.getById(instance.getJobId());
      JobTaskContext context = new JobTaskContext(job, instance, task);
      String type = task.getType();
      if (StringUtils.isBlank(type)) {
        LOGGER.error(
            String.format(
                "task type is null uniqueId:%s taskId:%s", instance.getUniqueId(), task.getId()));
        return;
      }

      type = type.split(SchedulerConstant.UNDERLINE_SEPARATOR)[0];

      JobTask jobTask = SpringContextHolder.getBean(type, JobTask.class);
      if (jobTask == null) {
        LOGGER.error(
            String.format(
                "get jobTask bean error uniqueId:%s taskId:%s",
                instance.getUniqueId(), task.getId()));
        return;
      }

      jobTask.executeEntry(context);

      executeNextTask(context);
    } catch (Exception e) {
      LOGGER.error(String.format("process task error task:%s", task.getId()), e);
    } finally {
      Long time = System.currentTimeMillis() - start;
      METRIC_LOGGER.info(
          String.format(
              "|process|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s",
              instance.getProjectId(),
              instance.getJobId(),
              instance.getUniqueId(),
              instance.getSchedulerDate(),
              task.getType(),
              task.getTitle(),
              task.getUpdateUser(),
              task.getExecuteNum(),
              task.getBeginTime().getTime(),
              task.getStatus(),
              time));
    }
  }

  /** execute next task */
  public void executeNextTask(JobTaskContext context) {
    SchedulerInstance instance = context.getInstance();
    SchedulerTask task = context.getTask();
    WorkflowDag workflowDag = JSON.parseObject(instance.getWorkflowConfig(), WorkflowDag.class);
    List<WorkflowDag.Node> nextNodes = workflowDag.getNextNodes(task.getNodeId());
    List<SchedulerTask> taskList = Lists.newArrayList();
    for (WorkflowDag.Node nextNode : nextNodes) {
      SchedulerTask nextTask =
          schedulerTaskService.queryByInstanceIdAndType(instance.getId(), nextNode.getType());
      taskList.add(nextTask);
    }
    if (context.isTaskFinish() && CollectionUtils.isNotEmpty(taskList)) {
      Runnable instanceRunnable =
          () -> {
            try {
              executeInstance(instance.getId(), taskList);
            } catch (Exception e) {
              LOGGER.error(
                  String.format("executeInstance error uniqueId:%s", instance.getUniqueId()), e);
            }
          };

      long delay = 10;
      scheduledExecutor.schedule(instanceRunnable, delay, TimeUnit.SECONDS);
      LOGGER.info(String.format("executeNextTask successful:%s", instance.getUniqueId()));
    }
  }

  /** check next task Status is WAIT to RUNNING */
  private List<SchedulerTask> checkAndUpdateWaitStatus(
      SchedulerInstance instance, List<SchedulerTask> tasks) {
    List<SchedulerTask> result = Lists.newArrayList();
    List<SchedulerTask> processList =
        tasks.stream()
            .filter(s -> TaskStatus.WAIT.equals(s.getStatus()))
            .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(processList)) {
      return result;
    }

    WorkflowDag workflowDag = JSON.parseObject(instance.getWorkflowConfig(), WorkflowDag.class);
    processList.forEach(
        it -> {
          List<WorkflowDag.Node> preNodes = workflowDag.getPreNodes(it.getNodeId());
          boolean allFinish = true;
          for (WorkflowDag.Node preNode : preNodes) {
            SchedulerTask preTask =
                schedulerTaskService.queryByInstanceIdAndType(instance.getId(), preNode.getType());
            if (!TaskStatus.isFinished(preTask.getStatus())) {
              allFinish = false;
              break;
            }
          }
          if (allFinish) {
            SchedulerTask updateTask = new SchedulerTask();
            updateTask.setId(it.getId());
            updateTask.setStatus(TaskStatus.RUNNING.name());
            schedulerTaskService.update(updateTask);
            it.setStatus(TaskStatus.RUNNING.name());
            result.add(it);
          }
        });
    return result;
  }

  /** get All Not Finish Instance */
  private List<SchedulerInstance> getAllNotFinishInstance() {
    SchedulerInstanceQuery record = new SchedulerInstanceQuery();
    Integer maxDays = schedulerValue.getExecuteMaxDay() + 1;
    Date startDate = DateUtils.addDays(new Date(), -maxDays);
    record.setStartCreateTime(startDate);
    record.setEnv(schedulerValue.getExecuteEnv());
    List<SchedulerInstance> allInstance = schedulerInstanceService.getNotFinishInstance(record);
    return allInstance;
  }

  /** get Instance ThreadPoolExecutor by type */
  private ThreadPoolExecutor getInstanceExecutor(String type) {
    if (instanceExecutorMap.containsKey(type)) {
      return instanceExecutorMap.get(type);
    }

    int corePoolSize = 20;
    int maximumPoolSize = 100;
    ThreadPoolExecutor instanceExecutor =
        getThreadPoolExecutor("instanceExecutor" + type, corePoolSize, maximumPoolSize);
    instanceExecutorMap.put(type, instanceExecutor);
    return instanceExecutor;
  }

  /** get Task ThreadPoolExecutor by type */
  private ThreadPoolExecutor getTaskExecutor(String type) {
    if (taskExecutorMap.containsKey(type)) {
      return taskExecutorMap.get(type);
    }

    int corePoolSize = 10;
    int maximumPoolSize = 50;
    ThreadPoolExecutor instanceExecutor =
        getThreadPoolExecutor("taskExecutor" + type, corePoolSize, maximumPoolSize);
    taskExecutorMap.put(type, instanceExecutor);
    return instanceExecutor;
  }

  /** get ThreadPoolExecutor by type */
  private ThreadPoolExecutor getThreadPoolExecutor(
      String type, int corePoolSize, int maximumPoolSize) {
    long keepAliveTime = 30;
    int capacity = 100000;

    ThreadPoolExecutor taskExecutor =
        new ThreadPoolExecutor(
            corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(capacity),
            runnable -> {
              Thread thread = new Thread(runnable);
              thread.setDaemon(true);
              thread.setName(type + thread.getId());
              return thread;
            },
            new ThreadPoolExecutor.DiscardOldestPolicy());

    return taskExecutor;
  }
}
