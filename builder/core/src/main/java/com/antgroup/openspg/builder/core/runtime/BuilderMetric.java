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

package com.antgroup.openspg.builder.core.runtime;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Slf4jReporter;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BuilderMetric implements Serializable {

  private ScheduledReporter reporter;
  private final MetricRegistry metricRegistry;

  @Getter private final Meter totalCnt;

  @Getter private final Counter errorCnt;

  public BuilderMetric(String jobName) {
    metricRegistry = new MetricRegistry();
    totalCnt = metricRegistry.meter(jobName + "_total");
    errorCnt = metricRegistry.counter(jobName + "_error");
  }

  public void reportToLog() {
    reporter = Slf4jReporter.forRegistry(metricRegistry).outputTo(log).build();
    reporter.start(1, TimeUnit.SECONDS);
  }

  public void close() throws Exception {
    if (reporter != null) {
      reporter.close();
    }
  }
}
