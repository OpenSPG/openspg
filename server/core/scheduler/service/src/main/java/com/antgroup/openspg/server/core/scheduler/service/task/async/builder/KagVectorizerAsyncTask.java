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
import com.alibaba.fastjson.TypeReference;
import com.antgroup.openspg.builder.model.record.SubGraphRecord;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClient;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClientDriverManager;
import com.antgroup.openspg.common.constants.BuilderConstant;
import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.pemja.PemjaUtils;
import com.antgroup.openspg.common.util.pemja.PythonInvokeMethod;
import com.antgroup.openspg.common.util.pemja.model.PemjaConfig;
import com.antgroup.openspg.server.common.model.CommonConstants;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.antgroup.openspg.server.common.service.project.ProjectService;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteContext;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteDag;
import com.antgroup.openspg.server.core.scheduler.service.common.MemoryTaskServer;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.task.async.AsyncTaskExecuteTemplate;
import com.antgroup.openspg.server.core.scheduler.service.utils.SchedulerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("kagVectorizerAsyncTask")
public class KagVectorizerAsyncTask extends AsyncTaskExecuteTemplate {

  @Autowired private DefaultValue value;

  @Autowired private MemoryTaskServer memoryTaskServer;

  @Autowired private SchedulerTaskService taskService;

  @Autowired private ProjectService projectService;

  @Override
  public String submit(TaskExecuteContext context) {
    SchedulerInstance instance = context.getInstance();
    SchedulerTask task = context.getTask();
    String key =
        CommonUtils.getTaskStorageFileKey(
            task.getProjectId(), task.getInstanceId(), task.getId(), task.getType());
    SchedulerTask memoryTask = memoryTaskServer.getTask(key);
    if (memoryTask != null) {
      context.addTraceLog("Vectorizer task has been created!");
      return memoryTask.getNodeId();
    }

    List<String> inputs = getInputs(instance, task);
    String taskId =
        memoryTaskServer.submit(
            new VectorizerTaskCallable(value, projectService, context, inputs), key);
    context.addTraceLog("Vectorizer task has been successfully created!");
    return taskId;
  }

  private List<String> getInputs(SchedulerInstance instance, SchedulerTask task) {
    List<TaskExecuteDag.Node> nodes =
        instance.getTaskDag().getRelatedNodes(task.getNodeId(), false);
    List<String> inputs = Lists.newArrayList();
    nodes.forEach(
        node -> {
          SchedulerTask preTask =
              taskService.queryByInstanceIdAndNodeId(task.getInstanceId(), node.getId());
          if (preTask != null && StringUtils.isNotBlank(preTask.getOutput())) {
            inputs.add(preTask.getOutput());
          }
        });
    return inputs;
  }

  @Override
  public SchedulerEnum.TaskStatus getStatus(TaskExecuteContext context, String resource) {
    SchedulerTask task = memoryTaskServer.getTask(resource);
    SchedulerTask schedulerTask = context.getTask();
    if (task == null) {
      context.addTraceLog("Vectorizer task not found, recreate it");
      submit(context);
      return SchedulerEnum.TaskStatus.RUNNING;
    }
    context.addTraceLog("Vectorizer task status is %s", task.getStatus());
    if (StringUtils.isNotBlank(task.getTraceLog())) {
      context.addTraceLog(
          "Vectorizer task trace log:%s%s",
          System.getProperty("line.separator"), task.getTraceLog());
      task.setTraceLog("");
    }
    switch (task.getStatus()) {
      case RUNNING:
        break;
      case ERROR:
        int retryNum = 3;
        if (schedulerTask.getExecuteNum() % retryNum == 0) {
          context.addTraceLog("Vectorizer task execute failed, recreating……");
          memoryTaskServer.stopTask(resource);
          submit(context);
          return SchedulerEnum.TaskStatus.RUNNING;
        }
        break;
      case FINISH:
        memoryTaskServer.stopTask(resource);
        schedulerTask.setOutput(resource);
        break;
      default:
        context.addTraceLog(
            "Vectorizer task status is %s. wait for the next scheduling", task.getStatus());
        break;
    }
    return task.getStatus();
  }

  @Override
  public Boolean stop(TaskExecuteContext context, String resource) {
    return memoryTaskServer.stopTask(resource);
  }

  private static class VectorizerTaskCallable extends MemoryTaskServer.MemoryTaskCallable<String> {

    private DefaultValue value;

    private ObjectStorageClient objectStorageClient;

    private ProjectService projectService;

    private TaskExecuteContext context;

    private List<String> inputs;

    public VectorizerTaskCallable(
        DefaultValue value,
        ProjectService projectService,
        TaskExecuteContext context,
        List<String> inputs) {
      this.value = value;
      this.projectService = projectService;
      this.objectStorageClient =
          ObjectStorageClientDriverManager.getClient(value.getObjectStorageUrl());
      this.context = context;
      this.inputs = inputs;
    }

    @Override
    public String call() throws Exception {
      List<SubGraphRecord> subGraphList = Lists.newArrayList();
      addTraceLog("Start vectorized document!");
      for (String input : inputs) {
        String data = objectStorageClient.getString(value.getBuilderBucketName(), input);
        List<SubGraphRecord> subGraphs =
            JSON.parseObject(data, new TypeReference<List<SubGraphRecord>>() {});
        subGraphList.addAll(vectorizer(context, subGraphs));
      }
      AtomicLong nodes = new AtomicLong(0);
      AtomicLong edges = new AtomicLong(0);
      SchedulerUtils.getGraphSize(subGraphList, nodes, edges);
      addTraceLog("Vectorized document complete. nodes:%s. edges:%s", nodes.get(), edges.get());
      SchedulerTask task = context.getTask();
      String fileKey =
          CommonUtils.getTaskStorageFileKey(
              task.getProjectId(), task.getInstanceId(), task.getId(), task.getType());
      objectStorageClient.saveString(
          value.getBuilderBucketName(), JSON.toJSONString(subGraphList), fileKey);
      addTraceLog(
          "Store the results of the vector operator. file:%s/%s",
          value.getBuilderBucketName(), fileKey);
      return fileKey;
    }

    public List<SubGraphRecord> vectorizer(
        TaskExecuteContext context, List<SubGraphRecord> subGraphs) {
      List<SubGraphRecord> subGraphList = Lists.newArrayList();
      Long projectId = context.getInstance().getProjectId();
      String projectConfig = projectService.queryById(projectId).getConfig();
      JSONObject vec =
          JSONObject.parseObject(projectConfig).getJSONObject(CommonConstants.VECTORIZER);
      PythonInvokeMethod vectorizer = PythonInvokeMethod.BRIDGE_COMPONENT;
      JSONObject pyConfig = new JSONObject();
      pyConfig.put(BuilderConstant.TYPE, BuilderConstant.BATCH);
      pyConfig.put(BuilderConstant.VECTORIZE_MODEL, vec);

      PemjaConfig pemjaConfig =
          new PemjaConfig(
              value.getPythonExec(),
              value.getPythonPaths(),
              value.getSchemaUrlHost(),
              projectId,
              vectorizer,
              Maps.newHashMap());
      int index = 0;
      for (SubGraphRecord subGraph : subGraphs) {
        addTraceLog("Invoke the vector operator. index:%s/%s", ++index, subGraphs.size());
        Map map = new ObjectMapper().convertValue(subGraph, Map.class);
        List<Object> result =
            (List<Object>)
                PemjaUtils.invoke(
                    pemjaConfig, BuilderConstant.VECTORIZER_ABC, pyConfig.toJSONString(), map);
        List<SubGraphRecord> records =
            JSON.parseObject(
                JSON.toJSONString(result), new TypeReference<List<SubGraphRecord>>() {});
        subGraphList.addAll(records);
        for (SubGraphRecord subGraphRecord : records) {
          int nodes =
              CollectionUtils.isEmpty(subGraphRecord.getResultNodes())
                  ? 0
                  : subGraphRecord.getResultNodes().size();
          int edges =
              CollectionUtils.isEmpty(subGraphRecord.getResultEdges())
                  ? 0
                  : subGraphRecord.getResultEdges().size();
          addTraceLog("Vector operator was invoked successfully nodes:%s edges:%s", nodes, edges);
        }
      }
      return subGraphList;
    }
  }
}
