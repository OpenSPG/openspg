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
import com.alibaba.fastjson.TypeReference;
import com.antgroup.openspg.builder.model.record.SubGraphRecord;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClient;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClientDriverManager;
import com.antgroup.openspg.common.constants.BuilderConstant;
import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.common.util.pemja.PemjaUtils;
import com.antgroup.openspg.common.util.pemja.PythonInvokeMethod;
import com.antgroup.openspg.common.util.pemja.model.PemjaConfig;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.service.builder.BuilderJobService;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteContext;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.task.sync.SyncTaskExecuteTemplate;
import com.antgroup.openspg.server.core.scheduler.service.utils.SchedulerUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("kagMappingSyncTask")
public class KagMappingSyncTask extends SyncTaskExecuteTemplate {

  private static final Integer BATCH_MAX_NUM = 2000;

  @Autowired private DefaultValue value;

  @Autowired private BuilderJobService builderJobService;

  @Autowired private SchedulerTaskService taskService;

  private ObjectStorageClient objectStorageClient;

  @Override
  public SchedulerEnum.TaskStatus submit(TaskExecuteContext context) {
    objectStorageClient = ObjectStorageClientDriverManager.getClient(value.getObjectStorageUrl());
    SchedulerJob job = context.getJob();
    BuilderJob builderJob = builderJobService.getById(Long.valueOf(job.getInvokerId()));
    List<SubGraphRecord> chunks = loadData(context, builderJob);
    SchedulerTask task = context.getTask();
    String fileKey =
        com.antgroup.openspg.common.util.CommonUtils.getTaskStorageFileKey(
            task.getProjectId(), task.getInstanceId(), task.getId(), task.getType());
    objectStorageClient.saveString(
        value.getBuilderBucketName(), JSON.toJSONString(chunks), fileKey);
    context.addTraceLog(
        "Store the results of the mapping operator. file:%s/%s",
        value.getBuilderBucketName(), fileKey);
    task.setOutput(fileKey);
    return SchedulerEnum.TaskStatus.FINISH;
  }

  public List<SubGraphRecord> loadData(TaskExecuteContext context, BuilderJob builderJob) {
    SchedulerInstance instance = context.getInstance();
    SchedulerTask task = context.getTask();
    List<String> inputs = SchedulerUtils.getTaskInputs(taskService, instance, task);
    List<SubGraphRecord> subGraphList = Lists.newArrayList();
    for (String input : inputs) {
      String data = objectStorageClient.getString(value.getBuilderBucketName(), input);
      List<Map<String, Object>> datas =
          JSON.parseObject(data, new TypeReference<List<Map<String, Object>>>() {});
      subGraphList.addAll(mapping(context, datas, builderJob));
    }
    AtomicLong nodes = new AtomicLong(0);
    AtomicLong edges = new AtomicLong(0);
    SchedulerUtils.getGraphSize(subGraphList, nodes, edges);
    context.addTraceLog("Data mapping complete. nodes:%s. edges:%s", nodes.get(), edges.get());
    return subGraphList;
  }

  public List<SubGraphRecord> mapping(
      TaskExecuteContext context, List<Map<String, Object>> datas, BuilderJob builderJob) {
    List<SubGraphRecord> subGraphList = Lists.newArrayList();
    JSONObject extension = JSON.parseObject(builderJob.getExtension());
    JSONObject pyConfig = CommonUtils.getMappingConfig(extension, null);

    Long projectId = context.getInstance().getProjectId();
    PythonInvokeMethod mapping = PythonInvokeMethod.BRIDGE_COMPONENT;

    PemjaConfig pemjaConfig =
        new PemjaConfig(
            value.getPythonExec(),
            value.getPythonPaths(),
            value.getPythonEnv(),
            value.getSchemaUrlHost(),
            projectId,
            mapping,
            Maps.newHashMap());

    for (int i = 0; i < datas.size(); i += BATCH_MAX_NUM) {
      int index = Math.min(i + BATCH_MAX_NUM, datas.size());
      context.addTraceLog("Invoke the mapping operator. index:%s/%s", index, datas.size());
      List<Map<String, Object>> batch = datas.subList(i, index);
      List<Object> result =
          (List<Object>)
              PemjaUtils.invoke(
                  pemjaConfig, BuilderConstant.MAPPING_ABC, pyConfig.toJSONString(), batch);
      List<SubGraphRecord> records =
          JSON.parseObject(JSON.toJSONString(result), new TypeReference<List<SubGraphRecord>>() {});
      subGraphList.addAll(records);
      int nodes = 0;
      int edges = 0;
      for (SubGraphRecord subGraphRecord : records) {
        nodes =
            nodes
                + (CollectionUtils.isEmpty(subGraphRecord.getResultNodes())
                    ? 0
                    : subGraphRecord.getResultNodes().size());
        edges =
            edges
                + (CollectionUtils.isEmpty(subGraphRecord.getResultEdges())
                    ? 0
                    : subGraphRecord.getResultEdges().size());
      }
      context.addTraceLog(
          "Mapping operator was invoked successfully nodes:%s edges:%s", nodes, edges);
    }
    return subGraphList;
  }
}
