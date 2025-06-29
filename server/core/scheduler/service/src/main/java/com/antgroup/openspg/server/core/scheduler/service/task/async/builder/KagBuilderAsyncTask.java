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

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.pemja.PemjaUtils;
import com.antgroup.openspg.common.util.pemja.PythonInvokeMethod;
import com.antgroup.openspg.common.util.pemja.model.PemjaConfig;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.service.builder.BuilderJobService;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.antgroup.openspg.server.common.service.project.ProjectService;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteContext;
import com.antgroup.openspg.server.core.scheduler.service.common.MemoryTaskServer;
import com.antgroup.openspg.server.core.scheduler.service.task.async.AsyncTaskExecuteTemplate;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("kagBuilderAsyncTask")
public class KagBuilderAsyncTask extends AsyncTaskExecuteTemplate {

  @Autowired private DefaultValue value;

  @Autowired private BuilderJobService builderJobService;

  @Autowired private MemoryTaskServer memoryTaskServer;

  @Autowired private ProjectService projectService;

  @Override
  public String submit(TaskExecuteContext context) {
    SchedulerTask task = context.getTask();
    String key =
        CommonUtils.getTaskStorageFileKey(
            task.getProjectId(), task.getInstanceId(), task.getId(), task.getType());
    SchedulerTask memoryTask = memoryTaskServer.getTask(key);
    if (memoryTask != null) {
      context.addTraceLog("Builder task has been created!");
      return memoryTask.getNodeId();
    }
    String taskId =
        memoryTaskServer.submit(
            new BuilderTaskCallable(value, builderJobService, projectService, context),
            key,
            context.getInstance().getId());
    context.addTraceLog("Builder task has been successfully created!");
    return taskId;
  }

  @Override
  public SchedulerEnum.TaskStatus getStatus(TaskExecuteContext context, String resource) {
    SchedulerTask task = memoryTaskServer.getTask(resource);
    SchedulerTask schedulerTask = context.getTask();
    if (task == null) {
      context.addTraceLog("Builder task not found, recreating……");
      submit(context);
      return SchedulerEnum.TaskStatus.RUNNING;
    }
    context.addTraceLog("Builder task status is %s", task.getStatus());
    if (StringUtils.isNotBlank(task.getTraceLog())) {
      context.addTraceLog(
          "Builder task trace log:%s%s", System.getProperty("line.separator"), task.getTraceLog());
      task.setTraceLog("");
    }
    switch (task.getStatus()) {
      case RUNNING:
        break;
      case WAIT:
        return SchedulerEnum.TaskStatus.RUNNING;
      case ERROR:
        int retryNum = 3;
        if (schedulerTask.getExecuteNum() % retryNum == 0) {
          context.addTraceLog("Builder task execute failed, recreating……");
          memoryTaskServer.stopTask(resource);
          submit(context);
          return SchedulerEnum.TaskStatus.RUNNING;
        }
        break;
      case FINISH:
        memoryTaskServer.stopTask(resource);
        break;
      default:
        context.addTraceLog(
            "Builder task status is %s. wait for the next scheduling", task.getStatus());
        break;
    }
    return task.getStatus();
  }

  @Override
  public Boolean stop(TaskExecuteContext context, String resource) {
    return memoryTaskServer.stopTask(resource);
  }

  private static class BuilderTaskCallable extends MemoryTaskServer.MemoryTaskCallable<String> {

    private DefaultValue value;

    private BuilderJobService builderJobService;

    private ProjectService projectService;

    private TaskExecuteContext context;

    public BuilderTaskCallable(
        DefaultValue value,
        BuilderJobService builderJobService,
        ProjectService projectService,
        TaskExecuteContext context) {
      this.value = value;
      this.builderJobService = builderJobService;
      this.projectService = projectService;
      this.context = context;
    }

    @Override
    public String call() throws Exception {
      addTraceLog("Start Builder document!");
      kagBuilder();
      addTraceLog("Builder document complete.");
      return "";
    }

    public void kagBuilder() {
      SchedulerJob job = context.getJob();
      SchedulerInstance instance = context.getInstance();
      Long projectId = instance.getProjectId();
      Project project = projectService.queryById(projectId);
      BuilderJob builderJob = builderJobService.getById(Long.valueOf(job.getInvokerId()));
      JSONObject config =
          CommonUtils.getKagBuilderConfig(project, builderJob, value.getSchemaUrlHost());
      String input = CommonUtils.getKagBuilderInput(builderJob, instance.getSchedulerDate());
      PemjaConfig pemjaConfig =
          new PemjaConfig(
              value.getPythonExec(),
              value.getPythonPaths(),
              value.getPythonEnv(),
              value.getSchemaUrlHost(),
              projectId,
              PythonInvokeMethod.BRIDGE_BUILDER_MAIN,
              Maps.newHashMap());
      PemjaUtils.invoke(pemjaConfig, config, input);
    }
  }
}
