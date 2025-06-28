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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClient;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClientDriverManager;
import com.antgroup.openspg.common.constants.BuilderConstant;
import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.common.util.pemja.PemjaUtils;
import com.antgroup.openspg.common.util.pemja.PythonInvokeMethod;
import com.antgroup.openspg.common.util.pemja.model.PemjaConfig;
import com.antgroup.openspg.server.common.model.exception.SchedulerException;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.TaskStatus;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.antgroup.openspg.server.common.service.spring.SpringContextHolder;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteContext;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteDag;
import com.antgroup.openspg.server.core.scheduler.service.config.SchedulerConfig;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerJobService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.task.TaskExecute;
import com.antgroup.openspg.server.core.scheduler.service.task.async.AsyncTaskExecute;
import com.antgroup.openspg.server.core.scheduler.service.translate.TranslatorFactory;
import com.antgroup.openspg.server.core.scheduler.service.utils.SchedulerUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/** Scheduler internal common service, include generate Instance and Modify Instance status etc. */
@Service
@Slf4j
public class SchedulerCommonService {

  public static final String UNDERLINE_SEPARATOR = "_";
  public static final Long FINISH = 100L;

  @Autowired DefaultValue value;

  @Autowired SchedulerJobService schedulerJobService;

  @Autowired SchedulerInstanceService schedulerInstanceService;
  @Autowired SchedulerTaskService schedulerTaskService;
  @Autowired SchedulerConfig schedulerConfig;

  /** set Instance To Finish, set Status,Progress field to Finish and stop all running tasks */
  public void setInstanceFinish(
      SchedulerInstance instance, InstanceStatus instanceStatus, TaskStatus taskStatus) {
    Date finishTime = (instance.getFinishTime() == null ? new Date() : instance.getFinishTime());
    costStatistics(instance);
    SchedulerInstance updateInstance = new SchedulerInstance();
    updateInstance.setId(instance.getId());
    updateInstance.setStatus(instanceStatus);
    updateInstance.setProgress(FINISH);
    updateInstance.setFinishTime(finishTime);
    Long updateNum = schedulerInstanceService.update(updateInstance);
    Assert.isTrue(updateNum > 0, "update instance failed " + updateInstance);

    stopRunningTasks(instance);
    schedulerTaskService.setStatusByInstanceId(instance.getId(), taskStatus);
    SchedulerJob job = schedulerJobService.getById(instance.getJobId());
    TranslatorFactory.getTranslator(job.getTranslateType())
        .statusCallback(job, instance, instanceStatus);
  }

  /** Rerun all tasks after the specified task */
  public void RerunFromTask(SchedulerInstance instance, String taskType) {
    TaskExecuteDag taskDag = instance.getTaskDag();
    List<TaskExecuteDag.Node> nodes = taskDag.getNodesByType(taskType);
    List<SchedulerTask> tasks = schedulerTaskService.queryByInstanceId(instance.getId());
    Map<String, SchedulerTask> taskMap =
        tasks.stream()
            .collect(Collectors.toMap(SchedulerTask::getNodeId, SchedulerTask -> SchedulerTask));
    for (TaskExecuteDag.Node node : nodes) {
      SchedulerTask task = taskMap.get(node.getId());
      SchedulerTask runningTask = new SchedulerTask(instance, TaskStatus.RUNNING, node);
      runningTask.setId(task.getId());
      schedulerTaskService.update(runningTask);
      List<TaskExecuteDag.Node> subsequentNodes = taskDag.getSuccessorNodes(node.getId());
      for (TaskExecuteDag.Node subsequentNode : subsequentNodes) {
        SchedulerTask subsequentTask = taskMap.get(subsequentNode.getId());
        SchedulerTask waitTask = new SchedulerTask(instance, TaskStatus.WAIT, node);
        waitTask.setId(subsequentTask.getId());
        schedulerTaskService.update(waitTask);
      }
    }
  }

  /** stop all running tasks by instance */
  private void stopRunningTasks(SchedulerInstance instance) {
    List<SchedulerTask> taskList = schedulerTaskService.queryByInstanceId(instance.getId());

    SchedulerJob job = schedulerJobService.getById(instance.getJobId());

    for (SchedulerTask task : taskList) {
      stopRunningTask(job, instance, task);
    }
  }

  /** stop running task */
  private void stopRunningTask(SchedulerJob job, SchedulerInstance instance, SchedulerTask task) {
    // Filter non-running tasks
    if (!TaskStatus.isRunning(task.getStatus()) || StringUtils.isBlank(task.getType())) {
      return;
    }
    // get AsyncTaskExecute by type
    String type = task.getType().split(UNDERLINE_SEPARATOR)[0];
    TaskExecute jobTask = SpringContextHolder.getBean(type, TaskExecute.class);
    boolean isAsyncTask = (jobTask != null && jobTask instanceof AsyncTaskExecute);
    if (!isAsyncTask) {
      log.warn("get bean is null or not instance of JobAsyncTask id: {}", task.getId());
      return;
    }
    // transform to jobAsyncTask trigger stop
    AsyncTaskExecute jobAsyncTask = (AsyncTaskExecute) jobTask;
    TaskExecuteContext context = new TaskExecuteContext(job, instance, task);
    jobAsyncTask.stop(context, task.getResource());
  }

  /** check Instance is Running within 24H */
  private void checkInstanceRunning(SchedulerJob job) {
    SchedulerInstanceQuery query = new SchedulerInstanceQuery();
    query.setJobId(job.getId());
    query.setStartCreateTime(DateUtils.addDays(new Date(), -1));

    List<SchedulerInstance> instances = schedulerInstanceService.query(query).getResults();
    for (SchedulerInstance instance : instances) {
      if (!InstanceStatus.isFinished(instance.getStatus())) {
        throw new SchedulerException("Running {} exist within 24H", instance.getUniqueId());
      }
    }
  }

  /** generate Period Instance by Cron */
  public List<SchedulerInstance> generatePeriodInstance(SchedulerJob job) {
    List<SchedulerInstance> instances = Lists.newArrayList();
    Date pre = SchedulerUtils.getPreviousValidTime(job.getSchedulerCron(), new Date());
    pre = SchedulerUtils.getPreviousValidTime(job.getSchedulerCron(), pre);

    // get period instance all execution Dates
    List<Date> executionDates =
        SchedulerUtils.getCronExecutionDates(job.getSchedulerCron(), pre, new Date());
    if (CollectionUtils.isEmpty(executionDates)) {
      executionDates = Lists.newArrayList();
      executionDates.add(SchedulerUtils.getPreviousValidTime(job.getSchedulerCron(), new Date()));
    }
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
    Date schedulerDate = new Date();
    String uniqueId = SchedulerUtils.getUniqueId(job.getId(), schedulerDate);
    return generateInstance(job, uniqueId, schedulerDate);
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

    // Create tasks based on the DAG generated by the translation
    for (TaskExecuteDag.Node node : taskDag.getNodes()) {
      List<TaskExecuteDag.Node> pres = taskDag.getRelatedNodes(node.getId(), false);
      TaskStatus status = CollectionUtils.isEmpty(pres) ? TaskStatus.RUNNING : TaskStatus.WAIT;
      schedulerTaskService.insert(new SchedulerTask(instance, status, node));
    }

    // set job last execute time
    SchedulerJob updateJob = new SchedulerJob();
    updateJob.setId(job.getId());
    updateJob.setLastExecuteTime(schedulerDate);
    schedulerJobService.update(updateJob);

    return instance;
  }

  public void costStatistics(SchedulerInstance instance) {
    SchedulerInstance old = schedulerInstanceService.getById(instance.getId());
    JSONObject extension = old.getExtension();
    extension = (extension == null) ? new JSONObject() : extension;
    PemjaConfig pemjaConfig =
        new PemjaConfig(
            value.getPythonExec(),
            value.getPythonPaths(),
            value.getPythonEnv(),
            value.getSchemaUrlHost(),
            null,
            PythonInvokeMethod.BRIDGE_GET_LLM_TOKEN_INFO,
            Maps.newHashMap());
    Object result = PemjaUtils.invoke(pemjaConfig, instance.getId());
    JSONObject tokens = JSON.parseObject(JSON.toJSONString(result));
    if (tokens != null) {
      Integer token = tokens.getInteger(BuilderConstant.COMPLETION_TOKENS);
      extension.put(BuilderConstant.TOKENS_COST, token.toString());
    }

    Date finishTime = (instance.getFinishTime() == null ? new Date() : instance.getFinishTime());
    long diffInMillis = finishTime.getTime() - instance.getGmtCreate().getTime();
    long hours = diffInMillis / (1000 * 60 * 60);
    long minutes = (diffInMillis % (1000 * 60 * 60)) / (1000 * 60);
    long seconds = (diffInMillis % (1000 * 60)) / 1000;
    String time = String.format("%sh %sm %ss", hours, minutes, seconds);
    extension.put(BuilderConstant.TIME_COST, time);

    ObjectStorageClient objectStorageClient =
        ObjectStorageClientDriverManager.getClient(value.getObjectStorageUrl());
    long totalSizeInBytes =
        objectStorageClient.getStorageSize(
            value.getBuilderBucketName(),
            CommonUtils.getInstanceStorageFileKey(instance.getProjectId(), instance.getId()));
    extension.put(BuilderConstant.STORAGE_COST, formatBytes(totalSizeInBytes));

    SchedulerInstance updateInstance = new SchedulerInstance();
    updateInstance.setId(instance.getId());
    updateInstance.setExtension(extension);
    schedulerInstanceService.update(updateInstance);
  }

  public static String formatBytes(long sizeInBytes) {
    final String[] units = {"Bytes", "KB", "MB", "GB", "TB", "PB"};
    final double base = 1024.0;
    if (sizeInBytes < base) {
      return sizeInBytes + " Bytes";
    }
    int unitIndex = 0;
    double size = sizeInBytes;
    while (size >= base && unitIndex < units.length - 1) {
      size /= base;
      unitIndex++;
    }
    return String.format("%.2f %s", size, units[unitIndex]);
  }
}
