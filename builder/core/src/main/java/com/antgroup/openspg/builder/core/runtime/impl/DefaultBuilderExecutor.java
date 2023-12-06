package com.antgroup.openspg.builder.core.runtime.impl;

import com.antgroup.openspg.builder.core.BuilderException;
import com.antgroup.openspg.builder.core.physical.BasePhysicalNode;
import com.antgroup.openspg.builder.core.physical.PhysicalPlan;
import com.antgroup.openspg.builder.core.physical.process.BaseProcessor;
import com.antgroup.openspg.builder.core.runtime.BuilderExecutor;
import com.antgroup.openspg.builder.core.runtime.BuilderRecordException;
import com.antgroup.openspg.builder.core.runtime.RuntimeContext;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;

public class DefaultBuilderExecutor implements BuilderExecutor {

  private PhysicalPlan plan;

  @Override
  public void init(PhysicalPlan plan, RuntimeContext context) throws BuilderException {
    this.plan = plan;
    for (BasePhysicalNode node : plan.nodes()) {
      node.init(context);
    }
  }

  @Override
  public List<BaseRecord> eval(List<BaseRecord> inputRecords) throws BuilderRecordException {
    if (CollectionUtils.isEmpty(inputRecords)) {
      return new ArrayList<>(0);
    }

    List<BaseRecord> results = new ArrayList<>(inputRecords.size());
    for (BaseProcessor<?> processor : plan.startProcessor()) {
      processRecursively(processor, inputRecords, results);
    }
    return results;
  }

  private void processRecursively(
      BaseProcessor<?> processor, List<BaseRecord> inputRecords, List<BaseRecord> results) {
    if (CollectionUtils.isEmpty(inputRecords)) {
      return;
    }
    List<BaseRecord> outputRecords = processor.process(inputRecords);
    Set<BaseProcessor<?>> nextProcessors = plan.successors(processor);
    if (CollectionUtils.isEmpty(nextProcessors)) {
      results.addAll(outputRecords);
    } else {
      for (BaseProcessor<?> nextProcessor : plan.successors(processor)) {
        processRecursively(nextProcessor, outputRecords, results);
      }
    }
  }
}
