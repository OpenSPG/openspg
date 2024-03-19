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

package com.antgroup.openspg.reasoner.batching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticBatchSize implements IBatchSize {
  private static final Logger log = LoggerFactory.getLogger(StaticBatchSize.class);

  private final long allSize;
  private final long staticBatchSize;
  private long processSize;
  private int round;

  public StaticBatchSize(long allSize, long staticBatchSize) {
    this.allSize = allSize;
    this.staticBatchSize = staticBatchSize;
    this.processSize = 0;
  }

  @Override
  public long remainSize() {
    long remain = allSize - processSize;
    if (remain < 0) {
      return 0;
    }
    return remain;
  }

  @Override
  public long getAllSize() {
    return allSize;
  }

  @Override
  public long getProcessSize() {
    return processSize;
  }

  /** 获取下一个batch大小 */
  @Override
  public long getNextBatchSize() {
    if (allSize - processSize <= 0) {
      return 0;
    }
    this.round++;
    long nextBatchSize = Math.min(remainSize(), staticBatchSize);
    processSize += nextBatchSize;
    return nextBatchSize;
  }

  @Override
  public String toString() {
    return "allSize="
        + allSize
        + ",processSize="
        + processSize
        + ",round="
        + round
        + ",staticBatchSize="
        + staticBatchSize;
  }
}
