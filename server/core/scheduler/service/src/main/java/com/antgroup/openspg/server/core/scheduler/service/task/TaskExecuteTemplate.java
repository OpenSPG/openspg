/*
 * Copyright 2023 OpenSPG Authors
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
package com.antgroup.openspg.server.core.scheduler.service.task;

import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.server.common.model.exception.SchedulerException;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteContext;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteDag;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerCommonService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.utils.SchedulerUtils;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

/** JobTask Template class. execute before,process,finally and other functions */
@Slf4j
public abstract class TaskExecuteTemplate implements TaskExecute {

  /** lock max time */
  public static final Integer LOCK_TIME_MINUTES = 10;

  @Autowired SchedulerTaskService schedulerTaskService;
  @Autowired SchedulerCommonService schedulerCommonService;

  @Override
  public final void executeEntry(TaskExecuteContext context) {
    TaskStatus status = null;
    boolean lock = true;
    try {
      lock = lockTask(context);
      if (lock) {
        before(context);
        status = execute(context);
        context.getTask().setStatus(status);
      }
    } catch (Throwable e) {
      context.getTask().setStatus(TaskStatus.ERROR);
      context.addTraceLog(
          "Scheduler execute failed with error:%s", ExceptionUtils.getStackTrace(e));
      log.error(
          "JobTask process error projectId:{} uniqueId:{}",
          context.getInstance().getProjectId(),
          context.getInstance().getUniqueId(),
          e);
    }

    processStatus(context, status, lock);
  }

  public void processStatus(TaskExecuteContext context, TaskStatus status, boolean lock) {
    try {
      if (TaskStatus.isFinished(status)) {
        setTaskFinish(context);
      }
    } catch (Throwable e) {
      context.addTraceLog("Scheduler save failed with error:%s", ExceptionUtils.getStackTrace(e));
      log.error("process status error uniqueId:{}", context.getInstance().getUniqueId(), e);
    } finally {
      unlockTask(context, lock);
      finallyFunc(context);
    }
  }

  /** lock task before scheduling */
  private boolean lockTask(TaskExecuteContext context) {
    SchedulerTask task = context.getTask();
    if (task.getLockTime() == null) {
      if (schedulerTaskService.updateLock(task.getId()) < 1) {
        context.addTraceLog("Failed to preempt lock, the lock is already occupied!");
        return false;
      }
      context.addTraceLog("Lock preempted successfully!");
      return true;
    }

    Date now = new Date();
    Date unLockTime = DateUtils.addMinutes(task.getLockTime(), LOCK_TIME_MINUTES);
    if (now.before(unLockTime)) {
      context.addTraceLog(
          "Last lock preempt time：%s,The threshold was not exceeded. Wait for the execution to complete",
          DateTimeUtils.getDate2LongStr(task.getLockTime()));
      return false;
    }

    // Timeout release lock
    context.addTraceLog(
        "Last lock preempt time：%s, The threshold was exceeded. The current process is executed directly",
        DateTimeUtils.getDate2LongStr(task.getLockTime()));
    unlockTask(context, true);
    if (schedulerTaskService.updateLock(task.getId()) < 1) {
      context.addTraceLog("Failed to re-preempt lock!");
      return false;
    }
    context.addTraceLog("Re-preempt lock successfully!");
    return true;
  }

  /** Release lock after scheduling is completed */
  private void unlockTask(TaskExecuteContext context, boolean lock) {
    if (!lock) {
      return;
    }
    schedulerTaskService.updateUnlock(context.getTask().getId());
    context.addTraceLog("Lock released successfully!");
  }

  public void before(TaskExecuteContext context) {}

  /** the finally Func */
  public void finallyFunc(TaskExecuteContext context) {
    long cost = System.currentTimeMillis() - context.getStartTime();
    context.addTraceLog("Task scheduling completed. cost:%s ms !", cost);

    SchedulerTask task = context.getTask();
    SchedulerTask old = schedulerTaskService.getById(task.getId());
    if (TaskStatus.isFinished(old.getStatus())) {
      context.addTraceLog("Task has been completed by other threads,status:%s!", old.getStatus());
      if (StringUtils.isBlank(old.getOutput())) {
        old.setOutput(task.getOutput());
      }
      task = old;
    }

    // update task execute num and trace log
    task.setGmtModified(old.getGmtModified());
    task.setExecuteNum(old.getExecuteNum() + 1);
    context.getTraceLog().insert(0, System.getProperty("line.separator"));
    task.setTraceLog(SchedulerUtils.setRemarkLimit(old.getTraceLog(), context.getTraceLog()));
    task.setLockTime(null);

    if (schedulerTaskService.replace(task) <= 0) {
      throw new SchedulerException("finally replace task error task {}", task);
    }
  }

  /** set task to finished */
  public void setTaskFinish(TaskExecuteContext context) {
    SchedulerInstance instance = context.getInstance();
    SchedulerTask task = context.getTask();
    task.setFinishTime(new Date());

    List<TaskExecuteDag.Node> nextNodes =
        instance.getTaskDag().getRelatedNodes(task.getNodeId(), true);

    // Set the instance to complete if all tasks are completed
    if (CollectionUtils.isEmpty(nextNodes)) {
      List<SchedulerTask> tasks = schedulerTaskService.queryByInstanceId(instance.getId());
      if (checkAllTasksFinished(task, tasks)) {
        setInstanceFinished(context, TaskStatus.FINISH, InstanceStatus.FINISH);
      }
      return;
    }
    nextNodes.forEach(node -> startNextNode(context, instance.getTaskDag(), node));
  }

  /** start next wait node */
  private void startNextNode(
      TaskExecuteContext context, TaskExecuteDag taskDag, TaskExecuteDag.Node nextNode) {
    SchedulerTask task = context.getTask();

    if (!checkAllNodesFinished(task, taskDag.getRelatedNodes(nextNode.getId(), false))) {
      return;
    }
    SchedulerTask nextTask =
        schedulerTaskService.queryByInstanceIdAndNodeId(task.getInstanceId(), nextNode.getId());
    SchedulerTask updateTask = new SchedulerTask();
    updateTask.setId(nextTask.getId());
    String name = nextNode.getName();
    if (!TaskStatus.WAIT.equals(nextTask.getStatus())) {
      context.addTraceLog(
          "current status of %s is %s, and can not be modified", name, nextTask.getStatus());
      return;
    }
    updateTask.setStatus(TaskStatus.RUNNING);
    updateTask.setBeginTime(new Date());
    if (schedulerTaskService.replace(updateTask) <= 0) {
      task.setStatus(TaskStatus.ERROR);
      throw new SchedulerException("replace task error task {}", updateTask);
    }
    context.setTaskFinish(true);
  }

  /** check all tasks is finished */
  private boolean checkAllTasksFinished(SchedulerTask task, List<SchedulerTask> taskList) {
    for (SchedulerTask t : taskList) {
      if (!t.getId().equals(task.getId()) && !TaskStatus.isFinished(t.getStatus())) {
        return false;
      }
    }
    return true;
  }

  /** Check that all nodes are complete */
  private boolean checkAllNodesFinished(SchedulerTask task, List<TaskExecuteDag.Node> nodes) {
    for (TaskExecuteDag.Node node : nodes) {
      SchedulerTask t =
          schedulerTaskService.queryByInstanceIdAndNodeId(task.getInstanceId(), node.getId());
      if (!node.getId().equals(task.getNodeId()) && !TaskStatus.isFinished(t.getStatus())) {
        return false;
      }
    }
    return true;
  }

  /** set instance status to finished */
  public void setInstanceFinished(
      TaskExecuteContext context, TaskStatus taskStatus, InstanceStatus instanceStatus) {
    SchedulerInstance instance = context.getInstance();
    context.addTraceLog(
        "Set instance status to: %s, and all task statuses to: %s",
        taskStatus.name(), instanceStatus.name());
    schedulerCommonService.setInstanceFinish(instance, instanceStatus, taskStatus);
  }
}
