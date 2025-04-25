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
package com.antgroup.openspg.server.core.scheduler.service.common;

import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.api.SchedulerService;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MemoryTaskServer {

  @Autowired private SchedulerService schedulerService;

  private final ConcurrentMap<String, SchedulerTask> taskMap = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Future<?>> futureMap = new ConcurrentHashMap<>();

  private final ExecutorService executorService =
      new ThreadPoolExecutor(
          3,
          10,
          60 * 60,
          TimeUnit.SECONDS,
          new LinkedBlockingQueue<>(1000),
          new ThreadPoolExecutor.CallerRunsPolicy());

  public String submit(MemoryTaskCallable<String> taskCallable, String taskId, Long instanceId) {
    SchedulerTask taskInfo = new SchedulerTask();
    taskInfo.setNodeId(taskId);
    taskInfo.setStatus(SchedulerEnum.TaskStatus.WAIT);
    taskInfo.setInstanceId(instanceId);
    taskMap.put(taskId, taskInfo);
    taskCallable.setTask(taskInfo);

    Future<?> future =
        CompletableFuture.supplyAsync(
            () -> executeTask(taskId, instanceId, taskCallable), executorService);
    futureMap.put(taskId, future);

    return taskId;
  }

  private String executeTask(
      String taskId, Long instanceId, MemoryTaskCallable<String> taskCallable) {
    SchedulerTask taskInfo = taskMap.get(taskId);
    taskInfo.setStatus(SchedulerEnum.TaskStatus.RUNNING);
    taskInfo.setBeginTime(new Date());
    try {
      String result = taskCallable.call();
      taskInfo.setStatus(SchedulerEnum.TaskStatus.FINISH);
      taskInfo.setOutput(result);
    } catch (Exception e) {
      taskInfo.setStatus(SchedulerEnum.TaskStatus.ERROR);
      taskInfo.setTraceLog(ExceptionUtils.getStackTrace(e));
      log.error("executeTask Exception instanceId:" + instanceId, e);
    } finally {
      taskInfo.setFinishTime(new Date());
      schedulerService.triggerInstance(instanceId);
    }
    return taskId;
  }

  public SchedulerTask getTask(String taskId) {
    return taskMap.get(taskId);
  }

  public boolean stopTask(String taskId) {
    try {
      Future<?> future = futureMap.get(taskId);
      if (future != null && !future.isDone()) {
        boolean cancelled = future.cancel(true);
        if (cancelled) {
          futureMap.remove(taskId);
          return true;
        }
      }
      return false;
    } catch (Exception e) {
      log.error("stopTask Exception", e);
      return false;
    } finally {
      taskMap.remove(taskId);
      futureMap.remove(taskId);
    }
  }

  public ConcurrentMap<String, SchedulerTask> getAllTasks() {
    return taskMap;
  }

  public abstract static class MemoryTaskCallable<T> implements Callable<T> {

    private SchedulerTask task;

    public SchedulerTask getTask() {
      return task;
    }

    public void setTask(SchedulerTask task) {
      this.task = task;
    }

    public void addTraceLog(String message, Object... args) {
      StringBuffer log = new StringBuffer("    >> ");
      log.append(DateTimeUtils.getDate2Str("HH:mm:ss", new Date()))
          .append(": ")
          .append(String.format(message, args))
          .append(System.getProperty("line.separator"));
      String traceLog = (task.getTraceLog() == null) ? "" : task.getTraceLog();
      task.setTraceLog(log + traceLog);
    }

    @Override
    public abstract T call() throws Exception;
  }
}
