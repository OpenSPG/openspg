package com.antgroup.openspg.builder.runner.local;

import com.antgroup.openspg.builder.core.logical.LogicalPlan;
import com.antgroup.openspg.builder.core.physical.PhysicalPlan;
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
import com.antgroup.openspg.builder.runner.local.runtime.DefaultRecordCollector;
import com.antgroup.openspg.builder.runner.local.runtime.ErrorRecordCollector;
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

/** 本地的知识构建runner，在本地执行构建任务 */
@Slf4j
public class LocalBuilderRunner implements BuilderRunner {

  private BuilderExecutor builderExecutor = null;
  private BaseSourceReader<?> sourceReader = null;
  private BaseSinkWriter<?> sinkWriter = null;
  private BuilderMetric builderMetric = null;
  private ErrorRecordCollector errorRecordCollector = null;

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

    // 错误记录收集，将构建错误的记录收集到csv文件中
    errorRecordCollector = new DefaultRecordCollector(context.getJobName(), null);
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
                    // todo
                  }
                  sinkWriter.write(results);
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
    if (errorRecordCollector != null) {
      errorRecordCollector.close();
    }
    if (threadPoolExecutor != null) {
      threadPoolExecutor.shutdownNow();
    }
  }
}
