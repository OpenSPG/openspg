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
package com.antgroup.openspg.server.core.scheduler.service.task.sync.builder;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClient;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClientDriverManager;
import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.common.util.pemja.PythonInvokeMethod;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.service.builder.BuilderJobService;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.antgroup.openspg.server.common.service.project.ProjectService;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteContext;
import com.antgroup.openspg.server.core.scheduler.service.task.sync.SyncTaskExecuteTemplate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("kagScannerSyncTask")
public class KagScannerSyncTask extends SyncTaskExecuteTemplate {

  @Autowired private DefaultValue value;

  @Autowired private BuilderJobService builderJobService;

  @Autowired private ProjectService projectService;

  private ObjectStorageClient objectStorageClient;

  @Override
  public SchedulerEnum.TaskStatus submit(TaskExecuteContext context) {
    SchedulerJob job = context.getJob();
    BuilderJob builderJob = builderJobService.getById(Long.valueOf(job.getInvokerId()));
    List<Map<String, Object>> datas = readSource(context, builderJob);
    SchedulerTask task = context.getTask();
    String fileKey =
        CommonUtils.getTaskStorageFileKey(
            task.getProjectId(), task.getInstanceId(), task.getId(), task.getType());
    objectStorageClient = ObjectStorageClientDriverManager.getClient(value.getObjectStorageUrl());
    objectStorageClient.saveString(value.getBuilderBucketName(), JSON.toJSONString(datas), fileKey);
    context.addTraceLog(
        "Store the results of the scan operator. file:%s/%s",
        value.getBuilderBucketName(), fileKey);
    task.setOutput(fileKey);
    return SchedulerEnum.TaskStatus.FINISH;
  }

  public List<Map<String, Object>> readSource(TaskExecuteContext context, BuilderJob builderJob) {
    Long projectId = context.getInstance().getProjectId();
    Date bizDate = context.getInstance().getSchedulerDate();
    Project project = projectService.queryById(projectId);
    context.addTraceLog("Invoke scan operator:%s", PythonInvokeMethod.BRIDGE_READER.getMethod());
    List<Map<String, Object>> datas =
        com.antgroup.openspg.builder.core.physical.utils.CommonUtils.scanSource(
            value.getPythonExec(),
            value.getPythonPaths(),
            value.getPythonEnv(),
            value.getSchemaUrlHost(),
            project,
            builderJob,
            bizDate);
    context.addTraceLog("The scan operator was invoked successfully. data size:%s", datas.size());

    return datas;
  }
}
