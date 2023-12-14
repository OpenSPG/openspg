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

import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.TaskStatus;
import com.antgroup.openspg.server.common.service.spring.SpringContextHolder;
import com.antgroup.openspg.server.core.scheduler.model.common.TaskDag;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerCommonService;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Scheduler Execute Service implementation class. execute all instances */
@Service
@Slf4j
public class SchedulerExecuteServiceImpl implements SchedulerExecuteService {

  public static final String UNDERLINE_SEPARATOR = "_";

  private ConcurrentHashMap<String, ThreadPoolExecutor> instances = new ConcurrentHashMap<>();
  private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(10);

  @Autowired SchedulerValue schedulerValue;
  @Autowired SchedulerJobService schedulerJobService;
  @Autowired SchedulerInstanceService schedulerInstanceService;
  @Autowired SchedulerTaskService schedulerTaskService;
  @Autowired SchedulerCommonService schedulerCommonService;

  @Override
  public void generateInstances() {
    SchedulerJob record = new SchedulerJob();
    record.setLifeCycle(SchedulerEnum.LifeCycle.PERIOD);
    record.setStatus(SchedulerEnum.Status.ONLINE);
    List<SchedulerJob> allJob = schedulerJobService.query(record);
    log.info("getAllPeriodJob successful size:{}", allJob.size());

    if (CollectionUtils.isEmpty(allJob)) {
      return;
    }

    for (SchedulerJob job : allJob) {
      try {
        List<SchedulerInstance> instances = schedulerCommonService.generatePeriodInstance(job);
        log.info("generate successful jobId:{} size:{}", job.getId(), instances.size());
      } catch (Exception e) {
        log.error("generate error jobId:{}", job.getId(), e);
      }
    }
  }

  @Override
  public void executeInstances() {
    List<SchedulerInstance> allInstance = getAllNotFinishInstance();
    log.info("getAllNotFinishInstance successful size:{}", allInstance.size());
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
      executor.execute(instanceRunnable);
      log.info("add instanceExecutor successful {}", instance.getUniqueId());
    }
  }

  /** execute Instance by id */
  @Override
  public void executeInstance(Long id) {
    try {
      SchedulerInstance instance = schedulerInstanceService.getById(id);
      List<SchedulerTask> tasks = schedulerTaskService.queryByInstanceId(id);

      List<SchedulerTask> runningTasks =
          tasks.stream()
              .filter(s -> TaskStatus.isRunning(s.getStatus()))
              .collect(Collectors.toList());

      if (CollectionUtils.isEmpty(runningTasks)) {
        runningTasks = checkAndUpdateWaitStatus(instance, tasks);
      }
      if (CollectionUtils.isEmpty(runningTasks)) {
        schedulerCommonService.setInstanceFinish(
            instance, InstanceStatus.FINISH, TaskStatus.FINISH);
        return;
      }
      executeInstance(instance, runningTasks);
    } catch (Exception e) {
      log.error("execute instance error id:", id, e);
    }
  }

  /** execute Instance by all tasks */
  public void executeInstance(SchedulerInstance instance, List<SchedulerTask> tasks) {
    if (InstanceStatus.isFinished(instance.getStatus())) {
      log.info(
          "instanceStatus is FINISH slip id:{} status:{}", instance.getId(), instance.getStatus());
      return;
    }
    tasks.forEach(task -> executeTask(instance, task));
  }

  /** execute Instance by task */
  public void executeTask(SchedulerInstance instance, SchedulerTask task) {
    try {
      SchedulerJob job = schedulerJobService.getById(instance.getJobId());
      JobTaskContext context = new JobTaskContext(job, instance, task);
      if (StringUtils.isBlank(task.getType())) {
        log.error("task type is null uniqueId:{} taskId:{}", instance.getUniqueId(), task.getId());
        return;
      }

      String type = task.getType().split(UNDERLINE_SEPARATOR)[0];
      JobTask jobTask = SpringContextHolder.getBean(type, JobTask.class);
      if (jobTask == null) {
        log.error("get bean error uniqueId:{} taskId:{}", instance.getUniqueId(), task.getId());
        return;
      }

      jobTask.executeEntry(context);

      executeNextTask(context);
    } catch (Exception e) {
      log.error("process task error task:{}", task.getId(), e);
    }
  }

  /** execute next task */
  public void executeNextTask(JobTaskContext context) {
    SchedulerInstance instance = context.getInstance();
    SchedulerTask task = context.getTask();
    List<TaskDag.Node> nextNodes = instance.getTaskDag().getRelatedNodes(task.getNodeId(), true);
    List<SchedulerTask> taskList = Lists.newArrayList();
    for (TaskDag.Node nextNode : nextNodes) {
      taskList.add(
          schedulerTaskService.queryByInstanceIdAndType(instance.getId(), nextNode.getType()));
    }
    if (context.isTaskFinish() && CollectionUtils.isNotEmpty(taskList)) {
      SchedulerInstance ins = schedulerInstanceService.getById(instance.getId());
      Runnable instanceRunnable = () -> executeInstance(ins, taskList);

      long delay = 10;
      executorService.schedule(instanceRunnable, delay, TimeUnit.SECONDS);
      log.info("executeNextTask successful {}", instance.getUniqueId());
    }
  }

  /** check next task Status is WAIT to RUNNING */
  private List<SchedulerTask> checkAndUpdateWaitStatus(
      SchedulerInstance instance, List<SchedulerTask> tasks) {
    List<SchedulerTask> result = Lists.newArrayList();
    List<SchedulerTask> waitList =
        tasks.stream()
            .filter(s -> TaskStatus.WAIT.equals(s.getStatus()))
            .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(waitList)) {
      return result;
    }

    for (SchedulerTask it : waitList) {
      List<TaskDag.Node> preNodes = instance.getTaskDag().getRelatedNodes(it.getNodeId(), false);
      if (checkAllNodesFinished(instance.getId(), preNodes)) {
        SchedulerTask updateTask = new SchedulerTask();
        updateTask.setId(it.getId());
        updateTask.setStatus(TaskStatus.RUNNING);
        schedulerTaskService.update(updateTask);
        it.setStatus(TaskStatus.RUNNING);
        result.add(it);
      }
    }

    return result;
  }

  /** check all nodes is finished */
  private boolean checkAllNodesFinished(Long instanceId, List<TaskDag.Node> nodes) {
    for (TaskDag.Node node : nodes) {
      SchedulerTask t = schedulerTaskService.queryByInstanceIdAndType(instanceId, node.getType());
      if (!TaskStatus.isFinished(t.getStatus())) {
        return false;
      }
    }
    return true;
  }

  /** get All Not Finish Instance */
  private List<SchedulerInstance> getAllNotFinishInstance() {
    SchedulerInstance record = new SchedulerInstance();
    Integer maxDays = schedulerValue.getExecuteMaxDay() + 1;
    Date startDate = DateUtils.addDays(new Date(), -maxDays);
    record.setStartCreateTime(startDate);
    List<SchedulerInstance> allInstance = schedulerInstanceService.getNotFinishInstance(record);
    return allInstance;
  }

  /** get Instance ThreadPoolExecutor by type */
  private ThreadPoolExecutor getInstanceExecutor(String type) {
    if (instances.containsKey(type)) {
      return instances.get(type);
    }
    ThreadPoolExecutor instanceExecutor =
        new ThreadPoolExecutor(20, 100, 30, TimeUnit.MINUTES, new LinkedBlockingQueue<>(100000));

    instances.put(type, instanceExecutor);
    return instanceExecutor;
  }
}
