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
package com.antgroup.openspg.server.core.scheduler.service.task.async.builder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.builder.model.BuilderConstants;
import com.antgroup.openspg.builder.model.record.RecordAlterOperationEnum;
import com.antgroup.openspg.cloudext.interfaces.computingengine.ComputingEngineClient;
import com.antgroup.openspg.cloudext.interfaces.computingengine.ComputingEngineClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.computingengine.ComputingEngineConstants;
import com.antgroup.openspg.cloudext.interfaces.computingengine.model.ComputingStatusEnum;
import com.antgroup.openspg.cloudext.interfaces.computingengine.model.ComputingTask;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.service.builder.BuilderJobService;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.antgroup.openspg.server.common.service.project.ProjectService;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteContext;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.task.async.AsyncTaskExecuteTemplate;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component("computingEngineAsyncTask")
public class ComputingEngineAsyncTask extends AsyncTaskExecuteTemplate {

  @Autowired private DefaultValue value;

  @Autowired private BuilderJobService builderJobService;

  @Autowired private SchedulerInstanceService instanceService;

  @Autowired private SchedulerTaskService taskService;

  @Autowired private ProjectService projectManager;

  @Override
  public String submit(TaskExecuteContext context) {
    SchedulerJob job = context.getJob();
    BuilderJob builderJob = builderJobService.getById(Long.valueOf(job.getInvokerId()));
    ComputingEngineClient client =
        ComputingEngineClientDriverManager.getClient(value.getComputingEngineUrl());
    context.addTraceLog("Start assembling the computing engine configuration information");
    JSONObject extension = initExtension(builderJob);
    long startTime = System.currentTimeMillis();
    ComputingTask computingTask = client.submitBuilderJob(builderJob, extension);
    long time = System.currentTimeMillis() - startTime;
    context.addTraceLog(
        "submit builder job succeed. resource：%s, cost：%s ms, details：%s",
        computingTask.getTaskId(), time, computingTask.getLogUrl());
    return computingTask.getTaskId();
  }

  public static void putOrDefault(JSONObject extension, String key, Object defaultValue) {
    if (extension == null) {
      extension = new JSONObject();
    }
    if (!extension.containsKey(key) || extension.get(key) == null) {
      extension.put(key, defaultValue);
      return;
    }
    return;
  }

  public JSONObject initExtension(BuilderJob builderJob) {
    JSONObject extension = JSONObject.parseObject(builderJob.getComputingConf());
    putOrDefault(extension, BuilderConstants.PYTHON_EXEC_OPTION, value.getPythonExec());
    putOrDefault(extension, BuilderConstants.PYTHON_PATHS_OPTION, value.getPythonPaths());
    putOrDefault(extension, BuilderConstants.SCHEMA_URL_OPTION, value.getSchemaUrlHost());
    putOrDefault(extension, BuilderConstants.PARALLELISM_OPTION, 1);
    String jobAction =
        (builderJob.getAction() != null)
            ? builderJob.getAction()
            : RecordAlterOperationEnum.UPSERT.name();
    putOrDefault(extension, BuilderConstants.ALTER_OPERATION_OPTION, jobAction);
    putOrDefault(extension, BuilderConstants.LEAD_TO_OPTION, false);
    putOrDefault(extension, BuilderConstants.GRAPH_STORE_URL_OPTION, value.getGraphStoreUrl());
    putOrDefault(extension, BuilderConstants.SEARCH_ENGINE_URL_OPTION, value.getSearchEngineUrl());
    putOrDefault(extension, BuilderConstants.MODEL_EXECUTE_NUM_OPTION, value.getModelExecuteNum());
    putOrDefault(
        extension,
        BuilderConstants.PROJECT_OPTION,
        JSON.toJSONString(projectManager.queryById(builderJob.getProjectId())));
    return extension;
  }

  @Override
  public SchedulerEnum.TaskStatus getStatus(TaskExecuteContext context, String resource) {
    ComputingEngineClient client =
        ComputingEngineClientDriverManager.getClient(value.getComputingEngineUrl());
    context.addTraceLog("Get the computing engine task status based on taskId:%s", resource);
    ComputingStatusEnum statusEnum = client.queryStatus(new JSONObject(), resource);
    context.addTraceLog(
        "The computing engine status was obtained successfully. The status is:%s",
        statusEnum.name());
    switch (statusEnum) {
      case RUNNING:
        return processByRunning(context);
      case SUCCESS:
        return processByFinished(context, resource);
      case FAILED:
      case STOP:
        return processByFailed(context, resource);
      case NOTFOUND:
        return processNotFound(context, resource);
      default:
        context.addTraceLog(
            "The computing engine status is: %s, no operation is performed", statusEnum.name());
        break;
    }
    return SchedulerEnum.TaskStatus.RUNNING;
  }

  public SchedulerEnum.TaskStatus processByRunning(TaskExecuteContext context) {
    SchedulerInstance kgJobInstance = context.getInstance();
    SchedulerInstance updateInstance = new SchedulerInstance();
    updateInstance.setId(kgJobInstance.getId());
    updateInstance.setStatus(SchedulerEnum.InstanceStatus.RUNNING);
    instanceService.update(updateInstance);
    if (SchedulerEnum.LifeCycle.REAL_TIME.equals(kgJobInstance.getLifeCycle())) {
      context.addTraceLog(
          "The current build task is a real-time task, and the computing engine task is running continuously. Task scheduling "
              + "has been completed!");
      taskService.setStatusByInstanceId(kgJobInstance.getId(), SchedulerEnum.TaskStatus.SKIP);
      return SchedulerEnum.TaskStatus.FINISH;
    } else {
      context.addTraceLog(
          "The computing engine task is still running. Please wait for the execution to complete.");
      return SchedulerEnum.TaskStatus.RUNNING;
    }
  }

  public SchedulerEnum.TaskStatus processByFailed(TaskExecuteContext context, String resource) {
    SchedulerTask task = context.getTask();
    context.addTraceLog(
        "The computing engine task failed to run. Please check the task log. taskId:%s", resource);
    int retryNum = 10;
    if (task.getExecuteNum() % retryNum == 0) {
      context.addTraceLog(
          "The computing engine task has been in a failed state. The program automatically resubmits the task");
      String taskId = submit(context);
      context.addTraceLog("The computing engine task resubmit successful! taskId:%s", taskId);
      SchedulerTask updateTask = new SchedulerTask();
      updateTask.setId(task.getId());
      updateTask.setResource(taskId);
      taskService.update(updateTask);
      return SchedulerEnum.TaskStatus.RUNNING;
    }
    return SchedulerEnum.TaskStatus.ERROR;
  }

  public SchedulerEnum.TaskStatus processNotFound(TaskExecuteContext context, String resource) {
    SchedulerTask task = context.getTask();
    int retryNum = 5;
    if (task.getExecuteNum() % retryNum != 0) {
      context.addTraceLog(
          "The computing engine task is being submitted, taskId: %s. Waiting for the next scheduling",
          resource);
      return SchedulerEnum.TaskStatus.ERROR;
    }
    context.addTraceLog(
        "The computing engine task has been manually offline or deleted. Automatic execution recovery mechanism");
    String taskId = submit(context);
    context.addTraceLog("The computing engine task resubmit successful! taskId:%s", taskId);
    SchedulerTask updateTask = new SchedulerTask();
    updateTask.setId(task.getId());
    updateTask.setResource(taskId);
    taskService.update(updateTask);
    return SchedulerEnum.TaskStatus.RUNNING;
  }

  public SchedulerEnum.TaskStatus processByFinished(TaskExecuteContext context, String resource) {
    SchedulerInstance instance = context.getInstance();
    SchedulerInstance updateInstance = new SchedulerInstance();
    updateInstance.setId(instance.getId());
    updateInstance.setFinishTime(new Date());
    updateInstance.setProgress(100L);
    Long count = instanceService.update(updateInstance);
    Assert.isTrue(
        count > 0, "Data update failed, instance: " + JSONObject.toJSONString(updateInstance));
    context.addTraceLog("All calculation engine tasks have been completed! taskId：%s", resource);
    return SchedulerEnum.TaskStatus.FINISH;
  }

  @Override
  public Boolean stop(TaskExecuteContext context, String resource) {
    ComputingEngineClient client =
        ComputingEngineClientDriverManager.getClient(value.getComputingEngineUrl());
    SchedulerJob job = context.getJob();
    BuilderJob builderJob = builderJobService.getById(Long.valueOf(job.getInvokerId()));
    JSONObject extension = new JSONObject();
    extension.put(ComputingEngineConstants.USER_NUMBER, builderJob.getModifyUser());
    return client.stop(extension, resource);
  }
}
