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
import com.antgroup.openspg.builder.model.record.ChunkRecord;
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
import com.antgroup.openspg.server.common.model.project.Project;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
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

@Component("kagExtractorAsyncTask")
public class KagExtractorAsyncTask extends AsyncTaskExecuteTemplate {

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

  @Autowired private MemoryTaskServer memoryTaskServer;

  @Autowired private SchedulerTaskService taskService;

  @Autowired private ProjectService projectService;

  @PostConstruct
  public void init() {
    if (executor == null) {
      executor =
          new ThreadPoolExecutor(
              value.getModelExecuteNum(),
              value.getModelExecuteNum(),
              60 * 60,
              TimeUnit.SECONDS,
              new LinkedBlockingQueue<>(100),
              handler);
    }
  }

  @Override
  public String submit(TaskExecuteContext context) {
    SchedulerInstance instance = context.getInstance();
    SchedulerTask task = context.getTask();
    String key =
        CommonUtils.getTaskStorageFileKey(
            task.getProjectId(), task.getInstanceId(), task.getId(), task.getType());
    SchedulerTask memoryTask = memoryTaskServer.getTask(key);
    if (memoryTask != null) {
      context.addTraceLog("Extractor task has been created!");
      return memoryTask.getNodeId();
    }

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
    Project project = projectService.queryById(instance.getProjectId());
    String taskId =
        memoryTaskServer.submit(new ExtractorTaskCallable(value, project, context, inputs), key);
    context.addTraceLog("Extractor task has been successfully created!");
    return taskId;
  }

  @Override
  public SchedulerEnum.TaskStatus getStatus(TaskExecuteContext context, String resource) {
    SchedulerTask task = memoryTaskServer.getTask(resource);
    SchedulerTask schedulerTask = context.getTask();
    if (task == null) {
      context.addTraceLog("Extractor task not found, recreating……");
      submit(context);
      return SchedulerEnum.TaskStatus.RUNNING;
    }
    context.addTraceLog("Extractor task status is %s", task.getStatus());
    if (StringUtils.isNotBlank(task.getTraceLog())) {
      context.addTraceLog(
          "Extractor task trace log:%s%s",
          System.getProperty("line.separator"), task.getTraceLog());
      task.setTraceLog("");
    }
    switch (task.getStatus()) {
      case RUNNING:
        break;
      case ERROR:
        int retryNum = 3;
        if (schedulerTask.getExecuteNum() % retryNum == 0) {
          context.addTraceLog("Extractor task execute failed, recreating……");
          memoryTaskServer.stopTask(resource);
          submit(context);
          return SchedulerEnum.TaskStatus.RUNNING;
        }
        break;
      case FINISH:
        String fileKey =
            CommonUtils.getTaskStorageFileKey(
                schedulerTask.getProjectId(),
                schedulerTask.getInstanceId(),
                schedulerTask.getId(),
                schedulerTask.getType());
        memoryTaskServer.stopTask(resource);
        schedulerTask.setOutput(fileKey);
        break;
      default:
        context.addTraceLog(
            "Extractor task status is %s. wait for the next scheduling", task.getStatus());
        break;
    }
    return task.getStatus();
  }

  @Override
  public Boolean stop(TaskExecuteContext context, String resource) {
    return memoryTaskServer.stopTask(resource);
  }

  private static class ExtractorTaskCallable extends MemoryTaskServer.MemoryTaskCallable<String> {

    private DefaultValue value;

    private ObjectStorageClient objectStorageClient;

    private TaskExecuteContext context;

    private List<String> inputs;

    private Project project;

    public ExtractorTaskCallable(
        DefaultValue value, Project project, TaskExecuteContext context, List<String> inputs) {
      this.value = value;
      this.objectStorageClient =
          ObjectStorageClientDriverManager.getClient(value.getObjectStorageUrl());
      this.context = context;
      this.inputs = inputs;
      this.project = project;
    }

    @Override
    public String call() throws Exception {
      List<ChunkRecord.Chunk> chunkList = Lists.newArrayList();
      for (String input : inputs) {
        String data = objectStorageClient.getString(value.getBuilderBucketName(), input);
        List<ChunkRecord.Chunk> chunks =
            JSON.parseObject(data, new TypeReference<List<ChunkRecord.Chunk>>() {});
        chunkList.addAll(chunks);
      }
      addTraceLog("Start extract document. chunk size:%s", chunkList.size());

      List<Future<List<SubGraphRecord>>> futures = new ArrayList<>();
      List<SubGraphRecord> results = new ArrayList<>();
      for (ChunkRecord.Chunk chunk : chunkList) {
        Future<List<SubGraphRecord>> future =
            executor.submit(new ExtractTaskCallable(chunk, value, project));
        futures.add(future);
      }

      for (Future<List<SubGraphRecord>> future : futures) {
        try {
          List<SubGraphRecord> result = future.get();
          results.addAll(result);
        } catch (InterruptedException | ExecutionException e) {
          throw new RuntimeException("invoke extract Exception", e);
        }
      }
      AtomicLong nodes = new AtomicLong(0);
      AtomicLong edges = new AtomicLong(0);
      SchedulerUtils.getGraphSize(results, nodes, edges);
      addTraceLog("Extract document complete. nodes:%s. edges:%s", nodes.get(), edges.get());

      SchedulerTask task = context.getTask();
      String fileKey =
          CommonUtils.getTaskStorageFileKey(
              task.getProjectId(), task.getInstanceId(), task.getId(), task.getType());
      objectStorageClient.saveString(
          value.getBuilderBucketName(), JSON.toJSONString(results), fileKey);
      addTraceLog(
          "Store the results of the extract operator. file:%s/%s",
          value.getBuilderBucketName(), fileKey);
      return fileKey;
    }

    class ExtractTaskCallable implements Callable<List<SubGraphRecord>> {
      private final ChunkRecord.Chunk chunk;
      private final DefaultValue value;
      private final Project project;

      public ExtractTaskCallable(ChunkRecord.Chunk chunk, DefaultValue value, Project project) {
        this.chunk = chunk;
        this.value = value;
        this.project = project;
      }

      @Override
      public List<SubGraphRecord> call() throws Exception {
        PythonInvokeMethod extractor = PythonInvokeMethod.BRIDGE_COMPONENT;
        String projectConfig = project.getConfig();
        JSONObject llm = JSONObject.parseObject(projectConfig).getJSONObject(CommonConstants.LLM);
        JSONObject pyConfig = new JSONObject();
        pyConfig.put(BuilderConstant.TYPE, BuilderConstant.SCHEMA_FREE);
        pyConfig.put(BuilderConstant.LLM, llm);
        PemjaConfig pemjaConfig =
            new PemjaConfig(
                value.getPythonExec(),
                value.getPythonPaths(),
                value.getSchemaUrlHost(),
                project.getId(),
                extractor,
                Maps.newHashMap());
        addTraceLog("Start extract chunk(%s:%s)", chunk.getName(), chunk.getShortId());
        Map map = new ObjectMapper().convertValue(chunk, Map.class);
        List<Object> result =
            (List<Object>)
                PemjaUtils.invoke(
                    pemjaConfig, BuilderConstant.EXTRACTOR_ABC, pyConfig.toJSONString(), map);
        List<SubGraphRecord> records =
            JSON.parseObject(
                JSON.toJSONString(result), new TypeReference<List<SubGraphRecord>>() {});

        for (SubGraphRecord subGraphRecord : records) {
          int nodes =
              CollectionUtils.isEmpty(subGraphRecord.getResultNodes())
                  ? 0
                  : subGraphRecord.getResultNodes().size();
          int edges =
              CollectionUtils.isEmpty(subGraphRecord.getResultEdges())
                  ? 0
                  : subGraphRecord.getResultEdges().size();
          addTraceLog(
              "Extract chunk(%s:%s) successfully. nodes:%s. edges:%s",
              chunk.getName(), chunk.getShortId(), nodes, edges);
        }
        return records;
      }
    }
  }
}
