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

package com.antgroup.openspg.builder.runner.local.runtime;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

@Getter
public class BuilderStat implements Serializable {

  private final BuilderMetric metric;
  private final ErrorRecordCollector collector;

  private final int parallelism;
  private final Set<Integer> successSplitIds;
  private final Set<Integer> failedSplitIds;

  public BuilderStat(BuilderMetric metric, ErrorRecordCollector collector, int parallelism) {
    this.metric = metric;
    this.collector = collector;
    this.parallelism = parallelism;
    this.successSplitIds = Collections.newSetFromMap(new ConcurrentHashMap<>());
    this.failedSplitIds = Collections.newSetFromMap(new ConcurrentHashMap<>());
  }

  public void success(Integer splitId) {
    successSplitIds.add(splitId);
  }

  public void failure(Integer splitId) {
    failedSplitIds.add(splitId);
  }

  public boolean isFailure() {
    return CollectionUtils.isNotEmpty(failedSplitIds);
  }

  public boolean isFinished() {
    return successSplitIds.size() + failedSplitIds.size() == parallelism;
  }
}
