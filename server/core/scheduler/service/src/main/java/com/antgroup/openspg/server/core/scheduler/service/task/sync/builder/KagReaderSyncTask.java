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
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.builder.model.record.ChunkRecord;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClient;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClientDriverManager;
import com.antgroup.openspg.common.constants.BuilderConstant;
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
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("kagReaderSyncTask")
public class KagReaderSyncTask extends SyncTaskExecuteTemplate {

  @Autowired private DefaultValue value;

  @Autowired private BuilderJobService builderJobService;

  @Autowired private ProjectService projectService;

  private ObjectStorageClient objectStorageClient;

  @Override
  public SchedulerEnum.TaskStatus submit(TaskExecuteContext context) {
    SchedulerJob job = context.getJob();
    BuilderJob builderJob = builderJobService.getById(Long.valueOf(job.getInvokerId()));
    String url = builderJob.getFileUrl();
    JSONObject config =
        JSON.parseObject(builderJob.getExtension()).getJSONObject(BuilderConstant.YU_QUE_CONFIG);
    String token = config == null ? null : config.getString(BuilderConstant.TOKEN);
    List<ChunkRecord.Chunk> chunks = readSource(context, url, token);
    SchedulerTask task = context.getTask();
    String fileKey =
        CommonUtils.getTaskStorageFileKey(
            task.getProjectId(), task.getInstanceId(), task.getId(), task.getType());
    objectStorageClient = ObjectStorageClientDriverManager.getClient(value.getObjectStorageUrl());
    objectStorageClient.saveString(
        value.getBuilderBucketName(), JSON.toJSONString(chunks), fileKey);
    context.addTraceLog(
        "Store the results of the read operator. file:%s/%s",
        value.getBuilderBucketName(), fileKey);
    task.setOutput(fileKey);
    return SchedulerEnum.TaskStatus.FINISH;
  }

  public List<ChunkRecord.Chunk> readSource(TaskExecuteContext context, String url, String token) {
    Long projectId = context.getInstance().getProjectId();
    Project project = projectService.queryById(projectId);
    context.addTraceLog("Invoke read operator:%s", PythonInvokeMethod.BRIDGE_READER.getMethod());
    List<ChunkRecord.Chunk> chunkList =
        com.antgroup.openspg.builder.core.physical.utils.CommonUtils.readSource(
            value.getPythonExec(),
            value.getPythonPaths(),
            value.getSchemaUrlHost(),
            project,
            url,
            token);
    context.addTraceLog(
        "The read operator was invoked successfully. chunk size:%s", chunkList.size());

    return chunkList;
  }
}
