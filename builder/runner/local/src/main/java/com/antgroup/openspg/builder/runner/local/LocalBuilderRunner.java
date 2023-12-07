package com.antgroup.openspg.builder.runner.local;

import com.antgroup.openspg.builder.model.BuilderException;
import com.antgroup.openspg.builder.core.logical.LogicalPlan;
import com.antgroup.openspg.builder.core.physical.PhysicalPlan;
import com.antgroup.openspg.builder.core.runtime.BuilderExecutor;
import com.antgroup.openspg.builder.core.runtime.BuilderRecordException;
import com.antgroup.openspg.builder.core.runtime.BuilderRunner;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.builder.core.runtime.impl.DefaultBuilderExecutor;
import com.antgroup.openspg.builder.model.pipeline.Pipeline;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.runner.local.runtime.BuilderMetric;
import com.antgroup.openspg.builder.runner.local.runtime.DefaultRecordCollector;
import com.antgroup.openspg.builder.runner.local.runtime.ErrorRecordCollector;
import com.antgroup.openspg.builder.runner.local.sink.BaseSinkWriter;
import com.antgroup.openspg.builder.runner.local.sink.SinkWriterFactory;
import com.antgroup.openspg.builder.runner.local.source.BaseSourceReader;
import com.antgroup.openspg.builder.runner.local.source.SourceReaderFactory;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/** 本地的知识构建runner，在本地执行构建任务 */
public class LocalBuilderRunner implements BuilderRunner {

  private BuilderExecutor builderExecutor = null;
  private BaseSourceReader<?> sourceReader = null;
  private BaseSinkWriter<?> sinkWriter = null;
  private BuilderMetric builderMetric = null;
  private ErrorRecordCollector errorRecordCollector = null;

  @Override
  public void init(Pipeline pipeline, RuntimeContext context) throws BuilderException {
    LogicalPlan logicalPlan = LogicalPlan.parse(pipeline);
    sourceReader =
        logicalPlan.startNodes().stream()
            .map(SourceReaderFactory::getSourceReader)
            .findFirst()
            .get();
    sinkWriter =
        logicalPlan.endNodes().stream().map(SinkWriterFactory::getSinkWriter).findFirst().get();

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
  public void execute() {
    Meter totalMeter = builderMetric.getTotalCnt();
    Counter errorCnt = builderMetric.getErrorCnt();
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
      sinkWriter.write(records);
      records = Collections.unmodifiableList(sourceReader.read());
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
  }
}
