package com.antgroup.openspg.builder.runner.local;

import com.antgroup.openspg.builder.core.BuilderException;
import com.antgroup.openspg.builder.core.logical.LogicalPlan;
import com.antgroup.openspg.builder.core.physical.PhysicalPlan;
import com.antgroup.openspg.builder.core.runtime.BuilderExecutor;
import com.antgroup.openspg.builder.core.runtime.BuilderRunner;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.builder.core.runtime.impl.DefaultBuilderExecutor;
import com.antgroup.openspg.builder.model.pipeline.Pipeline;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.runner.local.sink.BaseSinkWriter;
import com.antgroup.openspg.builder.runner.local.sink.SinkWriterFactory;
import com.antgroup.openspg.builder.runner.local.source.BaseSourceReader;
import com.antgroup.openspg.builder.runner.local.source.SourceReaderFactory;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/** 本地的知识构建runner，在本地执行构建任务 */
public class LocalBuilderRunner implements BuilderRunner {

  private BuilderExecutor builderExecutor = null;
  private BaseSourceReader<?> sourceReader = null;
  private BaseSinkWriter<?> sinkWriter = null;

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
  }

  @Override
  public void execute() {
    List<BaseRecord> records = Collections.unmodifiableList(sourceReader.read());

    while (CollectionUtils.isNotEmpty(records)) {
      List<BaseRecord> results = null;
      try {
        results = builderExecutor.eval(records);
      } catch (Exception e) {
        // todo
      }
      sinkWriter.write(records);
      records = Collections.unmodifiableList(sourceReader.read());
    }
  }
}
