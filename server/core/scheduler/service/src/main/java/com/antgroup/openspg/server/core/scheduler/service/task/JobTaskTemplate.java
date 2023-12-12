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
package com.antgroup.openspg.server.core.scheduler.service.task;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.server.common.model.scheduler.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.common.WorkflowDag;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerCommonService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yangjin
 * @version : JobTaskTemplate.java, v 0.1 2023年12月04日 19:26 yangjin Exp $
 */
public abstract class JobTaskTemplate implements JobTask {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobTaskTemplate.class);

  /** lock max time */
  public static final Integer LOCK_TIME_MINUTES = 15;

  @Autowired SchedulerTaskService schedulerTaskService;
  @Autowired SchedulerCommonService schedulerCommonService;

  @Override
  public void executeEntry(JobTaskContext context) {
    TaskStatus status = null;
    boolean lock = true;
    try {
      lock = lockTask(context);
      if (lock) {
        // 前置处理
        before(context);
        // 处理核心类
        status = process(context);
        context.getTask().setStatus(status.name());
      }
    } catch (Throwable e) {
      context.getTask().setStatus(TaskStatus.ERROR.name());
      context.addTraceLog("任务调度执行异常：%s", CommonUtils.getExceptionToString(e));
      LOGGER.warn(
          String.format(
              "JobProcessTemplate process error uniqueId:%s", context.getInstance().getUniqueId()),
          e);
    }

    processStatus(context, status, lock);
  }

  @Transactional
  public void processStatus(JobTaskContext context, TaskStatus status, boolean lock) {
    try {
      if (status == null) {
        status = TaskStatus.WAIT;
      }
      switch (status) {
        case FINISH:
          finish(context);
          break;
        case SKIP:
          skip(context);
          break;
        case ERROR:
        default:
          break;
      }
    } catch (Throwable e) {
      context.addTraceLog("任务调度存储异常：%s", CommonUtils.getExceptionToString(e));
      LOGGER.error(
          String.format("process status error uniqueId:%s", context.getInstance().getUniqueId()),
          e);
    } finally {
      unlockTask(context, lock);
      finallyFunc(context);
    }
  }

  private boolean lockTask(JobTaskContext context) {
    SchedulerTask task = context.getTask();
    if (task.getLockTime() == null) {
      int count = schedulerTaskService.updateLock(task.getId());
      if (count < 1) {
        context.addTraceLog("抢占调度执行锁失败，锁已被占用！");
        return false;
      }
      context.addTraceLog("抢占调度执行锁成功！");
      return true;
    }
    Date now = new Date();
    Date unLockTime = DateUtils.addMinutes(task.getLockTime(), LOCK_TIME_MINUTES);
    if (now.before(unLockTime)) {
      context.addTraceLog(
          "上次调度执行锁抢锁时间：%s，未超过执行阈值。请等待调度执行完成。", DateTimeUtils.getDate2LongStr(task.getLockTime()));
      return false;
    }
    context.addTraceLog(
        "上次调度执行锁抢锁时间：%s，超过执行阈值。当前流程直接抢占调度锁进行执行。",
        DateTimeUtils.getDate2LongStr(task.getLockTime()));
    unlockTask(context, true);
    int count = schedulerTaskService.updateLock(task.getId());
    if (count < 1) {
      context.addTraceLog("重新抢占调度执行锁失败，锁已被占用！");
      return false;
    }
    context.addTraceLog("重新抢占调度执行锁成功！");
    return true;
  }

  private void unlockTask(JobTaskContext context, boolean lock) {
    if (!lock) {
      return;
    }
    schedulerTaskService.updateUnlock(context.getTask().getId());
    context.addTraceLog("释放调度执行锁成功！");
  }

  public void before(JobTaskContext context) {
    context.addTraceLog("开始执行任务!");
  }

  public void finish(JobTaskContext context) {
    setTaskFinish(context);
  }

  public void skip(JobTaskContext context) {
    setTaskFinish(context);
  }

  public void finallyFunc(JobTaskContext context) {
    context.addTraceLog("任务调度完成。耗时：%s ms !!", System.currentTimeMillis() - context.getStartTime());
    // 每次结束必须更新Workflow
    SchedulerTask task = context.getTask();
    SchedulerTask oldTask = schedulerTaskService.getById(task.getId());
    if (TaskStatus.isFinish(oldTask.getStatus())) {
      context.addTraceLog("任务已被其他线程执行完成，当前状态：%s。当前调度不做数据变更，只进行调度日志保存!!", oldTask.getStatus());
      task = oldTask;
    } else {
      task.setGmtModified(oldTask.getGmtModified());
    }
    task.setExecuteNum(oldTask.getExecuteNum() + 1);
    context.getTraceLog().insert(0, System.getProperty("line.separator"));
    task.setRemark(CommonUtils.setRemarkLimit(oldTask.getRemark(), context.getTraceLog()));
    task.setLockTime(null);
    if (schedulerTaskService.replace(task) <= 0) {
      LOGGER.error(String.format("finally replace task error task:%s", task));
      throw new RuntimeException(String.format("finally replace task error task:%s", task));
    }
  }

  public void setTaskFinish(JobTaskContext context) {
    SchedulerInstance instance = context.getInstance();
    SchedulerTask task = context.getTask();
    task.setFinishTime(new Date());

    WorkflowDag workflowDag = JSON.parseObject(instance.getWorkflowConfig(), WorkflowDag.class);

    List<WorkflowDag.Node> nextNodes = workflowDag.getNextNodes(task.getNodeId());

    if (CollectionUtils.isEmpty(nextNodes)) {
      checkAllNodesFinish(context);
      return;
    }

    for (WorkflowDag.Node nextNode : nextNodes) {
      startNextNode(context, workflowDag, nextNode);
    }
  }

  private void startNextNode(
      JobTaskContext context, WorkflowDag workflowDag, WorkflowDag.Node nextNode) {
    SchedulerJob job = context.getJob();
    SchedulerInstance instance = context.getInstance();
    SchedulerTask task = context.getTask();

    List<WorkflowDag.Node> preNodes = workflowDag.getPreNodes(nextNode.getId());
    boolean allPreFinish = true;

    for (WorkflowDag.Node preNode : preNodes) {
      if (preNode.getId().equals(task.getNodeId())) {
        continue;
      }
      SchedulerTask preTask =
          schedulerTaskService.queryByInstanceIdAndType(task.getInstanceId(), preNode.getType());
      if (!TaskStatus.isFinish(preTask.getStatus())) {
        allPreFinish = false;
        break;
      }
    }

    if (!allPreFinish) {
      return;
    }
    SchedulerTask updateTask = new SchedulerTask();
    SchedulerTask nextTask =
        schedulerTaskService.queryByInstanceIdAndType(task.getInstanceId(), nextNode.getType());
    if (nextTask != null) {
      updateTask.setId(nextTask.getId());
    } else {
      updateTask =
          new SchedulerTask(
              task.getCreateUser(), job.getId(), instance.getId(), TaskStatus.WAIT, nextNode);
      nextTask = updateTask;
    }
    context.addTraceLog("当前节点执行完成，开始触发后续节点：%s", nextNode.getName());
    if (!TaskStatus.WAIT.name().equals(nextTask.getStatus())) {
      context.addTraceLog("后续节点：%s 状态为：%s，只有WAIT状态才能修改", nextNode.getName(), nextTask.getStatus());
      return;
    }
    updateTask.setStatus(TaskStatus.RUNNING.name());
    updateTask.setBeginTime(new Date());
    if (schedulerTaskService.replace(updateTask) <= 0) {
      task.setStatus(TaskStatus.ERROR.name());
      throw new RuntimeException(String.format("replace task error task:%s", updateTask));
    }
    context.setTaskFinish(true);
  }

  private boolean checkAllNodesFinish(JobTaskContext context) {
    boolean allFinish = true;
    List<SchedulerTask> taskList =
        schedulerTaskService.queryByInstanceId(context.getInstance().getId());
    for (SchedulerTask task : taskList) {
      if (task.getId().equals(task.getId())) {
        continue;
      }
      if (!TaskStatus.isFinish(task.getStatus())) {
        allFinish = false;
        break;
      }
    }
    if (allFinish) {
      setInstanceFinish(context, TaskStatus.FINISH, InstanceStatus.FINISH);
    }
    return allFinish;
  }

  public void setInstanceFinish(
      JobTaskContext context, TaskStatus taskStatus, InstanceStatus instanceStatus) {
    SchedulerInstance instance = context.getInstance();
    context.addTraceLog(
        "完成整个流程，后续节点状态将全部变更为：%s。任务状态置为：%s", taskStatus.name(), instanceStatus.name());
    schedulerCommonService.setInstanceFinish(instance, instanceStatus, taskStatus);
  }
}
