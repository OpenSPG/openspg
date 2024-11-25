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

package com.antgroup.openspg.builder.core.physical.process;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.antgroup.openspg.builder.core.physical.operator.OperatorFactory;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.pipeline.ExecuteNode;
import com.antgroup.openspg.builder.model.pipeline.config.LLMNlExtractNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.StatusEnum;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.ChunkRecord;
import com.antgroup.openspg.builder.model.record.SubGraphRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LLMNlExtractProcessor extends BasePythonProcessor<LLMNlExtractNodeConfig> {

  private ExecuteNode node;

  private static final RejectedExecutionHandler handler =
      (r, executor) -> {
        try {
          executor.getQueue().put(r);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      };

  private static ThreadPoolExecutor executor;

  public LLMNlExtractProcessor(String id, String name, LLMNlExtractNodeConfig config) {
    super(id, name, config);
  }

  @Override
  public void doInit(BuilderContext context) throws BuilderException {
    super.doInit(context);
    this.node = context.getExecuteNodes().get(getId());
    if (executor == null) {
      executor =
          new ThreadPoolExecutor(
              context.getModelExecuteNum(),
              context.getModelExecuteNum(),
              60 * 60,
              TimeUnit.SECONDS,
              new LinkedBlockingQueue<>(100),
              handler);
    }
  }

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    node.setStatus(StatusEnum.RUNNING);
    node.addTraceLog("Start extract document chunk. chunk size:%s", inputs.size());
    List<BaseRecord> results = new ArrayList<>();

    List<Future<List<SubGraphRecord>>> futures = new ArrayList<>();

    for (BaseRecord record : inputs) {
      ChunkRecord chunkRecord = (ChunkRecord) record;
      Future<List<SubGraphRecord>> future =
          executor.submit(new ExtractTaskCallable(node, chunkRecord, operatorFactory, config));
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
    node.addTraceLog("extract document complete.");
    node.setStatus(StatusEnum.FINISH);
    return results;
  }

  static class ExtractTaskCallable implements Callable<List<SubGraphRecord>> {
    private final ExecuteNode node;
    private final ChunkRecord chunkRecord;
    private final OperatorFactory operatorFactory;
    private final LLMNlExtractNodeConfig config;

    public ExtractTaskCallable(
        ExecuteNode node,
        ChunkRecord chunkRecord,
        OperatorFactory operatorFactory,
        LLMNlExtractNodeConfig config) {
      this.chunkRecord = chunkRecord;
      this.node = node;
      this.operatorFactory = operatorFactory;
      this.config = config;
    }

    @Override
    public List<SubGraphRecord> call() throws Exception {
      ChunkRecord.Chunk chunk = chunkRecord.getChunk();
      String names = chunk.getName();
      node.addTraceLog(
          "invoke extract operator:%s chunk:%s", config.getOperatorConfig().getClassName(), names);

      Map record = new ObjectMapper().convertValue(chunk, Map.class);

      log.info("LLMNlExtractProcessor invoke Chunks: {}", names);
      List<Object> result =
          (List<Object>) operatorFactory.invoke(config.getOperatorConfig(), record);
      List<SubGraphRecord> records =
          JSON.parseObject(JSON.toJSONString(result), new TypeReference<List<SubGraphRecord>>() {});
      node.addTraceLog(
          "invoke extract operator:%s chunk:%s succeed",
          config.getOperatorConfig().getClassName(), names);
      log.info("LLMNlExtractProcessor invoke succeed Chunks: {}", names);
      return records;
    }
  }
}
