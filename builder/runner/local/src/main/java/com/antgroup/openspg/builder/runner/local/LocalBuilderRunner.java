/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.builder.runner.local;

import com.antgroup.openspg.builder.core.logical.LogicalPlan;
import com.antgroup.openspg.builder.core.physical.PhysicalPlan;
import com.antgroup.openspg.builder.core.reason.ReasonProcessor;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.runtime.BuilderExecutor;
import com.antgroup.openspg.builder.core.runtime.BuilderRunner;
import com.antgroup.openspg.builder.core.runtime.impl.DefaultBuilderExecutor;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.BuilderRecordException;
import com.antgroup.openspg.builder.model.pipeline.Pipeline;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.runner.local.physical.sink.BaseSinkWriter;
import com.antgroup.openspg.builder.runner.local.physical.sink.SinkWriterFactory;
import com.antgroup.openspg.builder.runner.local.physical.source.BaseSourceReader;
import com.antgroup.openspg.builder.runner.local.physical.source.SourceReaderFactory;
import com.antgroup.openspg.builder.runner.local.runtime.BuilderMetric;
import com.antgroup.openspg.common.util.thread.ThreadUtils;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

/**
 * The local version of the knowledge builder runner, which performs knowledge builder tasks
 * locally.
 */
@Slf4j
public class LocalBuilderRunner implements BuilderRunner {

  private BuilderExecutor builderExecutor = null;
  private BaseSourceReader<?> sourceReader = null;
  private BaseSinkWriter<?> sinkWriter = null;
  private ReasonProcessor reasonProcessor = null;
  private BuilderMetric builderMetric = null;

  private final int parallelism;
  private final ThreadPoolExecutor threadPoolExecutor;

  public LocalBuilderRunner(int parallelism) {
    this.parallelism = parallelism;
    this.threadPoolExecutor =
        ThreadUtils.newDaemonFixedThreadPool(parallelism, "localBuilderRunner-");
  }

  @Override
  public void init(Pipeline pipeline, BuilderContext context) throws BuilderException {
    LogicalPlan logicalPlan = LogicalPlan.parse(pipeline);
    sourceReader =
        logicalPlan.sourceNodes().stream()
            .map(SourceReaderFactory::getSourceReader)
            .findFirst()
            .get();
    sourceReader.init(context);
    sinkWriter =
        logicalPlan.sinkNodes().stream().map(SinkWriterFactory::getSinkWriter).findFirst().get();
    sinkWriter.init(context);

    PhysicalPlan physicalPlan = PhysicalPlan.plan(logicalPlan);
    builderExecutor = new DefaultBuilderExecutor();
    builderExecutor.init(physicalPlan, context);

    // 构建指标统计，并将构建指标输出到log
    builderMetric = new BuilderMetric(context.getJobName());
    builderMetric.reportToLog();

    if (context.isEnableLeadTo()) {
      reasonProcessor = new ReasonProcessor();
      reasonProcessor.init(context);
    }
  }

  @Override
  public void execute() throws Exception {
    Meter totalMeter = builderMetric.getTotalCnt();
    Counter errorCnt = builderMetric.getErrorCnt();

    final List<CompletableFuture<Void>> futures = new ArrayList<>(parallelism);
    for (int i = 0; i < parallelism; i++) {
      CompletableFuture<Void> future =
          CompletableFuture.runAsync(
              () -> {
                List<BaseRecord> records = Collections.unmodifiableList(sourceReader.read());
                while (CollectionUtils.isNotEmpty(records)) {
                  totalMeter.mark(records.size());
                  List<BaseRecord> results = null;
                  try {
                    results = builderExecutor.eval(records);
                  } catch (BuilderRecordException e) {
                    errorCnt.inc(records.size());
                    log.error("builder record error", e);
                  }
                  if (CollectionUtils.isNotEmpty(results)) {
                    sinkWriter.write(results);
                  }
                  reason(results);
                  records = Collections.unmodifiableList(sourceReader.read());
                }
              },
              threadPoolExecutor);
      futures.add(future);
    }

    CompletableFuture<Void> joint =
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    failFast(futures, joint);
  }

  /**
   * if there is a reasoning executor, the reasoning process is initiated after writing to the graph
   * storage. it should be noted here that if the underlying graph storage does not make writes
   * immediately visible, the reasoning will not work well
   */
  private void reason(List<BaseRecord> records) {
    if (reasonProcessor != null && CollectionUtils.isNotEmpty(records)) {
      List<BaseRecord> reasonResults = reasonProcessor.process(records);
      if (CollectionUtils.isNotEmpty(reasonResults)) {
        sinkWriter.write(reasonResults);
      }
    }
  }

  private static <T> void failFast(List<CompletableFuture<T>> futures, CompletableFuture<T> joint)
      throws Exception {
    while (true) {
      if (joint.isDone()) {
        return;
      }
      for (CompletableFuture<T> future : futures) {
        if (future.isCompletedExceptionally()) {
          future.get();
          return;
        }
      }
    }
  }

  @Override
  public void close() throws Exception {
    if (builderMetric != null) {
      builderMetric.close();
    }
    if (threadPoolExecutor != null) {
      threadPoolExecutor.shutdownNow();
    }
  }
}
