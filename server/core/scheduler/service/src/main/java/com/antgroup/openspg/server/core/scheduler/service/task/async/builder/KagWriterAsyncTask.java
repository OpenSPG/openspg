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
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.pipeline.config.Neo4jSinkNodeConfig;
import com.antgroup.openspg.builder.model.record.RecordAlterOperationEnum;
import com.antgroup.openspg.builder.model.record.SubGraphRecord;
import com.antgroup.openspg.builder.runner.local.physical.sink.impl.Neo4jSinkWriter;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClient;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClientDriverManager;
import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.service.builder.BuilderJobService;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.antgroup.openspg.server.common.service.project.ProjectService;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.model.task.TaskExecuteContext;
import com.antgroup.openspg.server.core.scheduler.service.common.MemoryTaskServer;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.task.async.AsyncTaskExecuteTemplate;
import com.antgroup.openspg.server.core.scheduler.service.utils.SchedulerUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("kagWriterAsyncTask")
public class KagWriterAsyncTask extends AsyncTaskExecuteTemplate {

  private static final RejectedExecutionHandler handler =
      (r, executor) -> {
        try {
          executor.getQueue().put(r);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      };

  private static ThreadPoolExecutor executor;

  @Autowired private DefaultValue value;

  @Autowired private ProjectService projectManager;

  @Autowired private MemoryTaskServer memoryTaskServer;

  @Autowired private SchedulerTaskService taskService;

  @Autowired private BuilderJobService builderJobService;

  @PostConstruct
  public void init() {
    if (executor == null) {
      executor =
          new ThreadPoolExecutor(
              value.getModelExecuteNum(),
              value.getModelExecuteNum(),
              60 * 60,
              TimeUnit.SECONDS,
              new LinkedBlockingQueue<>(1000),
              handler);
    }
  }

  @Override
  public String submit(TaskExecuteContext context) {
    SchedulerInstance instance = context.getInstance();
    SchedulerTask task = context.getTask();
    String key =
        CommonUtils.getTaskStoragePathKey(
            task.getProjectId(), task.getInstanceId(), task.getId(), task.getType());
    SchedulerTask memoryTask = memoryTaskServer.getTask(key);
    if (memoryTask != null) {
      context.addTraceLog("Writer task has been created!");
      return memoryTask.getNodeId();
    }
    SchedulerJob job = context.getJob();
    BuilderJob builderJob = builderJobService.getById(Long.valueOf(job.getInvokerId()));

    List<String> inputs = SchedulerUtils.getTaskInputs(taskService, instance, task);
    String taskId =
        memoryTaskServer.submit(
            new WriterTaskCallable(value, projectManager, context, builderJob.getAction(), inputs),
            key,
            instance.getId());
    context.addTraceLog("Writer task has been successfully created!");
    return taskId;
  }

  @Override
  public SchedulerEnum.TaskStatus getStatus(TaskExecuteContext context, String resource) {
    SchedulerTask task = memoryTaskServer.getTask(resource);
    SchedulerTask schedulerTask = context.getTask();
    if (task == null) {
      context.addTraceLog("Writer task not found, recreating……");
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
      case WAIT:
        return SchedulerEnum.TaskStatus.RUNNING;
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
        task.setFinishTime(new Date());
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
    List<String> inputs = SchedulerUtils.getTaskInputs(taskService, instance, task);
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

    private String action;

    private List<String> inputs;

    private static final String VECTOR = "_vector";

    public WriterTaskCallable(
        DefaultValue value,
        ProjectService projectManager,
        TaskExecuteContext context,
        String action,
        List<String> inputs) {
      this.value = value;
      this.projectManager = projectManager;
      this.objectStorageClient =
          ObjectStorageClientDriverManager.getClient(value.getObjectStorageUrl());
      this.context = context;
      this.action = action;
      this.inputs = inputs;
    }

    @Override
    public String call() throws Exception {
      addTraceLog("Start write task!");
      SchedulerTask task = context.getTask();
      String pathKey =
          CommonUtils.getTaskStoragePathKey(
              task.getProjectId(), task.getInstanceId(), task.getId(), task.getType());

      AtomicLong nodes = new AtomicLong(0);
      AtomicLong edges = new AtomicLong(0);

      List<Future<?>> futures = new ArrayList<>();

      RecordAlterOperationEnum action = RecordAlterOperationEnum.UPSERT;
      if (RecordAlterOperationEnum.DELETE.name().equalsIgnoreCase(this.action)) {
        action = RecordAlterOperationEnum.DELETE;
      }
      Neo4jSinkWriter writer = getNeo4jSinkWriter(value, projectManager, context, action);
      for (Integer i = 0; i < inputs.size(); i++) {
        final Integer inputIndex = i;
        List<String> files =
            objectStorageClient.getAllFilesRecursively(value.getBuilderBucketName(), inputs.get(i));

        for (Integer f = 0; f < files.size(); f++) {
          final Integer fileIndex = f;
          final String filePath = files.get(f);
          addTraceLog(
              "Write ThreadPool. size:%s active:%s completed:%s total:%s queue:%s",
              executor.getPoolSize(),
              executor.getActiveCount(),
              executor.getCompletedTaskCount(),
              executor.getTaskCount(),
              executor.getQueue().size());
          Future<?> future =
              executor.submit(
                  () -> {
                    String data =
                        objectStorageClient.getString(value.getBuilderBucketName(), filePath);
                    SubGraphRecord subGraph = JSON.parseObject(data, SubGraphRecord.class);
                    String indexStr = (fileIndex + 1) + "/" + files.size();
                    addTraceLog("Invoke the write operator. index:%s", indexStr);
                    writer(writer, subGraph, indexStr);
                    simpleSubGraph(subGraph);
                    SchedulerUtils.getGraphSize(subGraph, nodes, edges);
                    String fileKey =
                        CommonUtils.getTaskStorageFileKey(pathKey, inputIndex + "_" + fileIndex);
                    objectStorageClient.saveString(
                        value.getBuilderBucketName(), JSON.toJSONString(subGraph), fileKey);
                    addTraceLog(
                        "Store the results of the alignment operator. file:%s/%s",
                        value.getBuilderBucketName(), fileKey);
                  });
          futures.add(future);
        }
      }
      for (Future<?> future : futures) {
        try {
          future.get();
        } catch (InterruptedException | ExecutionException e) {
          throw new RuntimeException("invoke write Exception", e);
        }
      }

      addTraceLog("Write task complete. nodes:%s. edges:%s", nodes.get(), edges.get());
      return pathKey;
    }

    public void simpleSubGraph(SubGraphRecord subGraph) {
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

      return;
    }

    public void writer(Neo4jSinkWriter writer, SubGraphRecord subGraph, String indexStr) {
      writer.writeToNeo4j(subGraph);
      int nodes =
          CollectionUtils.isEmpty(subGraph.getResultNodes()) ? 0 : subGraph.getResultNodes().size();
      int edges =
          CollectionUtils.isEmpty(subGraph.getResultEdges()) ? 0 : subGraph.getResultEdges().size();
      addTraceLog(
          "Write operator was invoked successfully index:%s nodes:%s edges:%s",
          indexStr, nodes, edges);
    }
  }

  private static Neo4jSinkWriter getNeo4jSinkWriter(
      DefaultValue value,
      ProjectService projectManager,
      TaskExecuteContext context,
      RecordAlterOperationEnum action) {
    Neo4jSinkWriter writer =
        new Neo4jSinkWriter(UUID.randomUUID().toString(), "writer", new Neo4jSinkNodeConfig(true));
    BuilderContext builderContext =
        new BuilderContext()
            .setProjectId(context.getInstance().getProjectId())
            .setJobName("writer")
            .setPythonExec(value.getPythonExec())
            .setPythonPaths(value.getPythonPaths())
            .setPythonEnv(value.getPythonEnv())
            .setOperation(action)
            .setEnableLeadTo(false)
            .setProject(
                JSON.toJSONString(projectManager.queryById(context.getInstance().getProjectId())))
            .setGraphStoreUrl(
                projectManager.getGraphStoreUrl(context.getInstance().getProjectId()));
    writer.init(builderContext);
    return writer;
  }
}
