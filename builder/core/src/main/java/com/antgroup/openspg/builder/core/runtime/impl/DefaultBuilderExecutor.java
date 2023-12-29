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

package com.antgroup.openspg.builder.core.runtime.impl;

import com.antgroup.openspg.builder.core.physical.BasePhysicalNode;
import com.antgroup.openspg.builder.core.physical.PhysicalPlan;
import com.antgroup.openspg.builder.core.physical.process.BaseProcessor;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.runtime.BuilderExecutor;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.BuilderRecordException;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;

public class DefaultBuilderExecutor implements BuilderExecutor {

  private PhysicalPlan plan;

  @Override
  public void init(PhysicalPlan plan, BuilderContext context) throws BuilderException {
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
    for (BaseProcessor<?> processor : plan.sourceNodes()) {
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
