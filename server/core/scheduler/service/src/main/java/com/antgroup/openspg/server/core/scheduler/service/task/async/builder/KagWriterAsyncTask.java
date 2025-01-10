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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.pipeline.config.Neo4jSinkNodeConfig;
import com.antgroup.openspg.builder.model.record.RecordAlterOperationEnum;
import com.antgroup.openspg.builder.model.record.SubGraphRecord;
import com.antgroup.openspg.builder.runner.local.physical.sink.impl.Neo4jSinkWriter;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClient;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClientDriverManager;
import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.common.util.StringUtils;
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
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("kagWriterAsyncTask")
public class KagWriterAsyncTask extends AsyncTaskExecuteTemplate {

  @Autowired private DefaultValue value;

  @Autowired private ProjectService projectManager;

  @Autowired private MemoryTaskServer memoryTaskServer;

  @Autowired private SchedulerTaskService taskService;

  @Override
  public String submit(TaskExecuteContext context) {
    SchedulerInstance instance = context.getInstance();
    SchedulerTask task = context.getTask();
    String key =
        CommonUtils.getTaskStorageFileKey(
            task.getProjectId(), task.getInstanceId(), task.getId(), task.getType());
    SchedulerTask memoryTask = memoryTaskServer.getTask(key);
    if (memoryTask != null) {
      context.addTraceLog("Writer task has been created!");
      return memoryTask.getNodeId();
    }

    List<String> inputs = getInputs(instance, task);
    String taskId =
        memoryTaskServer.submit(
            new WriterTaskCallable(value, projectManager, context, inputs), key);
    context.addTraceLog("Writer task has been successfully created!");
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
      context.addTraceLog("Writer task not found, recreate it");
      submit(context);
      return SchedulerEnum.TaskStatus.RUNNING;
    }
    context.addTraceLog("Writer task status is %s", task.getStatus());
    if (StringUtils.isNotBlank(task.getTraceLog())) {
      context.addTraceLog(
          "Writer task trace log:%s%s", System.getProperty("line.separator"), task.getTraceLog());
      task.setTraceLog("");
    }
    switch (task.getStatus()) {
      case RUNNING:
        break;
      case ERROR:
        int retryNum = 3;
        if (schedulerTask.getExecuteNum() % retryNum == 0) {
          context.addTraceLog("Writer task execute failed, recreating……");
          memoryTaskServer.stopTask(resource);
          submit(context);
          return SchedulerEnum.TaskStatus.RUNNING;
        }
        break;
      case FINISH:
        memoryTaskServer.stopTask(resource);
        schedulerTask.setOutput(resource);
        removeInputs(context);
        break;
      default:
        context.addTraceLog(
            "Writer task status is %s. wait for the next scheduling", task.getStatus());
        break;
    }
    return task.getStatus();
  }

  public void removeInputs(TaskExecuteContext context) {
    SchedulerInstance instance = context.getInstance();
    SchedulerTask task = context.getTask();
    List<String> inputs = getInputs(instance, task);
    ObjectStorageClient objectStorageClient =
        ObjectStorageClientDriverManager.getClient(value.getObjectStorageUrl());
    for (String input : inputs) {
      objectStorageClient.removeObject(value.getBuilderBucketName(), input);
    }
  }

  @Override
  public Boolean stop(TaskExecuteContext context, String resource) {
    return memoryTaskServer.stopTask(resource);
  }

  private static class WriterTaskCallable extends MemoryTaskServer.MemoryTaskCallable<String> {

    private DefaultValue value;

    private ProjectService projectManager;

    private ObjectStorageClient objectStorageClient;

    private TaskExecuteContext context;

    private List<String> inputs;

    private static final String VECTOR = "_vector";

    public WriterTaskCallable(
        DefaultValue value,
        ProjectService projectManager,
        TaskExecuteContext context,
        List<String> inputs) {
      this.value = value;
      this.projectManager = projectManager;
      this.objectStorageClient =
          ObjectStorageClientDriverManager.getClient(value.getObjectStorageUrl());
      this.context = context;
      this.inputs = inputs;
    }

    @Override
    public String call() throws Exception {
      List<SubGraphRecord> subGraphList = Lists.newArrayList();
      addTraceLog("Start write task!");
      for (String input : inputs) {
        String data = objectStorageClient.getString(value.getBuilderBucketName(), input);
        List<SubGraphRecord> subGraphs =
            JSON.parseObject(data, new TypeReference<List<SubGraphRecord>>() {});
        writer(value, projectManager, context, subGraphs);
        subGraphList.addAll(simpleSubGraph(subGraphs));
      }
      AtomicLong nodes = new AtomicLong(0);
      AtomicLong edges = new AtomicLong(0);
      SchedulerUtils.getGraphSize(subGraphList, nodes, edges);
      addTraceLog("Write task complete. nodes:%s. edges:%s", nodes.get(), edges.get());
      SchedulerTask task = context.getTask();
      String fileKey =
          CommonUtils.getTaskStorageFileKey(
              task.getProjectId(), task.getInstanceId(), task.getId(), task.getType());
      objectStorageClient.saveString(
          value.getBuilderBucketName(), JSON.toJSONString(subGraphList), fileKey);
      addTraceLog(
          "Store the results of the write operator. file:%s/%s",
          value.getBuilderBucketName(), fileKey);
      return fileKey;
    }

    public List<SubGraphRecord> simpleSubGraph(List<SubGraphRecord> subGraphs) {
      for (SubGraphRecord subGraph : subGraphs) {
        subGraph
            .getResultNodes()
            .forEach(
                node -> {
                  Iterator<Map.Entry<String, Object>> iterator =
                      node.getProperties().entrySet().iterator();
                  while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = iterator.next();
                    if (entry.getKey().endsWith(VECTOR)) {
                      iterator.remove();
                    }
                  }
                });
      }
      return subGraphs;
    }

    public void writer(
        DefaultValue value,
        ProjectService projectManager,
        TaskExecuteContext context,
        List<SubGraphRecord> subGraphs) {
      Neo4jSinkWriter writer =
          new Neo4jSinkWriter(
              UUID.randomUUID().toString(), "writer", new Neo4jSinkNodeConfig(true));
      BuilderContext builderContext =
          new BuilderContext()
              .setProjectId(context.getInstance().getProjectId())
              .setJobName("writer")
              .setPythonExec(value.getPythonExec())
              .setPythonPaths(value.getPythonPaths())
              .setOperation(RecordAlterOperationEnum.UPSERT)
              .setEnableLeadTo(false)
              .setProject(
                  JSON.toJSONString(projectManager.queryById(context.getInstance().getProjectId())))
              .setGraphStoreUrl(
                  projectManager.getGraphStoreUrl(context.getInstance().getProjectId()));
      writer.init(builderContext);
      int index = 0;
      for (SubGraphRecord subGraph : subGraphs) {
        addTraceLog("Invoke the write operator. index:%s/%s", ++index, subGraphs.size());
        writer.writeToNeo4j(subGraph);
        int nodes =
            CollectionUtils.isEmpty(subGraph.getResultNodes())
                ? 0
                : subGraph.getResultNodes().size();
        int edges =
            CollectionUtils.isEmpty(subGraph.getResultEdges())
                ? 0
                : subGraph.getResultEdges().size();
        addTraceLog("Write operator was invoked successfully nodes:%s edges:%s", nodes, edges);
      }
    }
  }
}
