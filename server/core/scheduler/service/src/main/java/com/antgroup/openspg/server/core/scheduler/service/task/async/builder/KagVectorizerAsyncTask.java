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
import com.antgroup.openspg.server.core.scheduler.service.common.MemoryTaskServer;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.task.async.AsyncTaskExecuteTemplate;
import com.antgroup.openspg.server.core.scheduler.service.utils.SchedulerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

@Component("kagVectorizerAsyncTask")
public class KagVectorizerAsyncTask extends AsyncTaskExecuteTemplate {

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
      context.addTraceLog("Vectorizer task has been created!");
      return memoryTask.getNodeId();
    }

    List<String> inputs = SchedulerUtils.getTaskInputs(taskService, instance, task);
    String taskId =
        memoryTaskServer.submit(
            new VectorizerTaskCallable(value, projectService, context, inputs),
            key,
            instance.getId());
    context.addTraceLog("Vectorizer task has been successfully created!");
    return taskId;
  }

  @Override
  public SchedulerEnum.TaskStatus getStatus(TaskExecuteContext context, String resource) {
    SchedulerTask task = memoryTaskServer.getTask(resource);
    SchedulerTask schedulerTask = context.getTask();
    if (task == null) {
      context.addTraceLog("Vectorizer task not found, recreating……");
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
      case WAIT:
        return SchedulerEnum.TaskStatus.RUNNING;
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
      addTraceLog("Start vectorized document!");
      SchedulerTask task = context.getTask();
      Long projectId = context.getInstance().getProjectId();
      String pathKey =
          CommonUtils.getTaskStoragePathKey(
              task.getProjectId(), task.getInstanceId(), task.getId(), task.getType());

      JSONObject pyConfig = getPyConfig(projectId);
      PemjaConfig pemjaConfig = getPemjaConfig(projectId);
      AtomicLong nodes = new AtomicLong(0);
      AtomicLong edges = new AtomicLong(0);

      List<Future<?>> futures = new ArrayList<>();

      for (Integer i = 0; i < inputs.size(); i++) {
        final Integer inputIndex = i;
        List<String> files =
            objectStorageClient.getAllFilesRecursively(value.getBuilderBucketName(), inputs.get(i));

        for (Integer f = 0; f < files.size(); f++) {
          final Integer fileIndex = f;
          final String filePath = files.get(f);
          addTraceLog(
              "Vector ThreadPool. size:%s active:%s completed:%s total:%s queue:%s",
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
                    addTraceLog(
                        "Invoke the vector operator. index:%s/%s", fileIndex + 1, files.size());
                    subGraph = vectorizer(pyConfig, pemjaConfig, subGraph);
                    addTraceLog(
                        "Vector operator was invoked successfully index:%s/%s nodes:%s edges:%s",
                        fileIndex + 1,
                        files.size(),
                        subGraph.getResultNodes().size(),
                        subGraph.getResultEdges().size());
                    SchedulerUtils.getGraphSize(subGraph, nodes, edges);
                    String fileKey =
                        CommonUtils.getTaskStorageFileKey(pathKey, inputIndex + "_" + fileIndex);
                    String results = JSON.toJSONString(subGraph);
                    Long start = System.currentTimeMillis();
                    byte[] bytes = results.getBytes(StandardCharsets.UTF_8);
                    objectStorageClient.saveData(value.getBuilderBucketName(), bytes, fileKey);
                    addTraceLog(
                        "Store the results of the vector operator. file:%s/%s length:%s cons:%s",
                        value.getBuilderBucketName(),
                        fileKey,
                        bytes.length,
                        System.currentTimeMillis() - start);
                  });
          futures.add(future);
        }
      }
      for (Future<?> future : futures) {
        try {
          future.get();
        } catch (InterruptedException | ExecutionException e) {
          throw new RuntimeException("invoke vector Exception", e);
        }
      }

      addTraceLog("Vectorized document complete. nodes:%s. edges:%s", nodes.get(), edges.get());
      return pathKey;
    }

    public SubGraphRecord vectorizer(
        JSONObject pyConfig, PemjaConfig pemjaConfig, SubGraphRecord subGraph) {
      SubGraphRecord record = new SubGraphRecord(Lists.newArrayList(), Lists.newArrayList());
      Map map = new ObjectMapper().convertValue(subGraph, Map.class);
      List<Object> result =
          (List<Object>)
              PemjaUtils.invoke(
                  pemjaConfig, BuilderConstant.VECTORIZER_ABC, pyConfig.toJSONString(), map);
      List<SubGraphRecord> records =
          JSON.parseObject(JSON.toJSONString(result), new TypeReference<List<SubGraphRecord>>() {});
      for (SubGraphRecord subGraphRecord : records) {
        if (CollectionUtils.isNotEmpty(subGraphRecord.getResultNodes())) {
          record.getResultNodes().addAll(subGraphRecord.getResultNodes());
        }
        if (CollectionUtils.isNotEmpty(subGraphRecord.getResultEdges())) {
          record.getResultEdges().addAll(subGraphRecord.getResultEdges());
        }
      }
      return record;
    }

    private PemjaConfig getPemjaConfig(Long projectId) {
      PythonInvokeMethod vectorizer = PythonInvokeMethod.BRIDGE_COMPONENT;
      PemjaConfig pemjaConfig =
          new PemjaConfig(
              value.getPythonExec(),
              value.getPythonPaths(),
              value.getPythonEnv(),
              value.getSchemaUrlHost(),
              projectId,
              vectorizer,
              Maps.newHashMap());
      return pemjaConfig;
    }

    private JSONObject getPyConfig(Long projectId) {
      String projectConfig = projectService.queryById(projectId).getConfig();
      JSONObject vec =
          JSONObject.parseObject(projectConfig).getJSONObject(CommonConstants.VECTORIZER);
      JSONObject pyConfig = new JSONObject();
      pyConfig.put(BuilderConstant.TYPE, BuilderConstant.BATCH);
      pyConfig.put(BuilderConstant.VECTORIZE_MODEL, vec);
      pyConfig.put(CommonConstants.TASK_ID, context.getInstance().getId());
      return pyConfig;
    }
  }
}
