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
package com.antgroup.openspg.server.core.scheduler.service.engine;

import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.TaskStatus;
import com.antgroup.openspg.server.common.service.spring.SpringContextHolder;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteContext;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteDag;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerCommonService;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerValue;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerJobService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.task.TaskExecute;
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
public class SchedulerExecuteService {

  public static final String UNDERLINE_SEPARATOR = "_";

  private ConcurrentHashMap<String, ThreadPoolExecutor> instances = new ConcurrentHashMap<>();
  private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(10);

  @Autowired SchedulerValue schedulerValue;
  @Autowired SchedulerJobService schedulerJobService;
  @Autowired SchedulerInstanceService schedulerInstanceService;
  @Autowired SchedulerTaskService schedulerTaskService;
  @Autowired SchedulerCommonService schedulerCommonService;

  /** generate instances by period job */
  public void generateInstances() {
    SchedulerJob record = new SchedulerJob();
    record.setLifeCycle(SchedulerEnum.LifeCycle.PERIOD);
    record.setStatus(SchedulerEnum.Status.ENABLE);
    List<SchedulerJob> allJob = schedulerJobService.query(record);
    log.info("getAllPeriodJob successful size:{}", allJob.size());

    if (CollectionUtils.isEmpty(allJob)) {
      return;
    }

    for (SchedulerJob job : allJob) {
      try {
        schedulerCommonService.generatePeriodInstance(job);
      } catch (Exception e) {
        log.error("generate error jobId:{}", job.getId(), e);
      }
    }
  }

  /** execute all not finish instances */
  public void executeInstances() {
    List<SchedulerInstance> allInstance = getAllNotFinishInstances();
    log.info("getAllNotFinishInstance successful size:{}", allInstance.size());
    if (CollectionUtils.isEmpty(allInstance)) {
      return;
    }

    for (SchedulerInstance instance : allInstance) {
      Runnable instanceRunnable = () -> executeInstance(instance.getId());
      getInstanceExecutor(instance.getType()).execute(instanceRunnable);
      log.info("add instanceExecutor successful {}", instance.getUniqueId());
    }
  }

  /** execute instance by id */
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

  /** execute instance by all tasks */
  private void executeInstance(SchedulerInstance instance, List<SchedulerTask> tasks) {
    if (InstanceStatus.isFinished(instance.getStatus())) {
      log.info("instance:{} status is {} ignore execute", instance.getId(), instance.getStatus());
      return;
    }
    tasks.forEach(task -> executeTask(instance, task));
  }

  /** execute instance by task */
  private void executeTask(SchedulerInstance instance, SchedulerTask task) {
    try {
      SchedulerJob job = schedulerJobService.getById(instance.getJobId());
      TaskExecuteContext context = new TaskExecuteContext(job, instance, task);
      if (StringUtils.isBlank(task.getType())) {
        log.error("task type is null uniqueId:{} taskId:{}", instance.getUniqueId(), task.getId());
        return;
      }

      String type = task.getType().split(UNDERLINE_SEPARATOR)[0];
      TaskExecute jobTask = SpringContextHolder.getBean(type, TaskExecute.class);
      if (jobTask != null) {
        jobTask.executeEntry(context);
        executeNextTask(context);
      } else {
        log.error("get bean is null uniqueId:{} type:{}", instance.getUniqueId(), type);
      }
    } catch (Exception e) {
      log.error("process task error task:{}", task.getId(), e);
    }
  }

  /** execute next task */
  private void executeNextTask(TaskExecuteContext context) {
    SchedulerInstance instance = context.getInstance();
    SchedulerTask task = context.getTask();
    List<TaskExecuteDag.Node> nextNodes =
        instance.getTaskDag().getRelatedNodes(task.getNodeId(), true);
    if (!context.isTaskFinish() || CollectionUtils.isEmpty(nextNodes)) {
      return;
    }
    List<SchedulerTask> taskList = Lists.newArrayList();
    for (TaskExecuteDag.Node nextNode : nextNodes) {
      taskList.add(
          schedulerTaskService.queryByInstanceIdAndType(
              instance.getId(), nextNode.getTaskComponent()));
    }
    SchedulerInstance ins = schedulerInstanceService.getById(instance.getId());
    Runnable instanceRunnable = () -> executeInstance(ins, taskList);

    long delay = 10;
    executorService.schedule(instanceRunnable, delay, TimeUnit.SECONDS);
    log.info("executeNextTask successful {}", instance.getUniqueId());
  }

  /** check next task Status is WAIT to RUNNING */
  private List<SchedulerTask> checkAndUpdateWaitStatus(
      SchedulerInstance instance, List<SchedulerTask> tasks) {

    List<SchedulerTask> result = Lists.newArrayList();
    for (SchedulerTask task : tasks) {
      if (!TaskStatus.WAIT.equals(task.getStatus())) {
        continue;
      }
      List<TaskExecuteDag.Node> preNodes =
          instance.getTaskDag().getRelatedNodes(task.getNodeId(), false);
      if (checkAllNodesFinished(instance.getId(), preNodes)) {
        SchedulerTask updateTask = new SchedulerTask();
        updateTask.setId(task.getId());
        updateTask.setStatus(TaskStatus.RUNNING);
        schedulerTaskService.update(updateTask);
        task.setStatus(TaskStatus.RUNNING);
        result.add(task);
      }
    }

    return result;
  }

  /** check all nodes is finished */
  private boolean checkAllNodesFinished(Long instanceId, List<TaskExecuteDag.Node> nodes) {
    for (TaskExecuteDag.Node node : nodes) {
      SchedulerTask t =
          schedulerTaskService.queryByInstanceIdAndType(instanceId, node.getTaskComponent());
      if (!TaskStatus.isFinished(t.getStatus())) {
        return false;
      }
    }
    return true;
  }

  /** get all not finish instances */
  private List<SchedulerInstance> getAllNotFinishInstances() {
    SchedulerInstance record = new SchedulerInstance();
    Integer maxDays = schedulerValue.getExecuteMaxDay() + 1;
    Date startDate = DateUtils.addDays(new Date(), -maxDays);
    record.setStartCreateTime(startDate);
    List<SchedulerInstance> allInstance = schedulerInstanceService.getNotFinishInstance(record);
    return allInstance;
  }

  /** get instance ThreadPoolExecutor by type */
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
