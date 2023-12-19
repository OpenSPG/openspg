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
package com.antgroup.openspg.reasoner.batching;

import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicBatchSize {
  private static final Logger log = LoggerFactory.getLogger(DynamicBatchSize.class);

  private final List<Integer> costs = new ArrayList<>();
  private final List<Long> batchSizes = new ArrayList<>();
  private long preBatchTime = 0;
  private final double step = 1.0;
  private long processSize = 0;
  private int disturb = 10;
  private long minBatchSize = 10;
  private long maxBatchSize = 5000000;

  private int nowRound = 0;
  private long allSize = 0;

  private final int expectRounds;

  public DynamicBatchSize(long allSize, long minBatchSize, long maxBatchSize, int expectRounds) {
    this.allSize = allSize;
    this.minBatchSize = minBatchSize;
    if (this.minBatchSize > this.allSize) {
      this.minBatchSize = this.allSize;
    }
    this.maxBatchSize = maxBatchSize;
    this.expectRounds = expectRounds;
    initNewDisturb();
  }

  private int computeProcessTime() {
    long curTime = System.currentTimeMillis();
    int cost = (int) (curTime - preBatchTime);
    preBatchTime = curTime;
    return cost;
  }

  public long remainSize() {
    long remain = allSize - processSize;
    if (remain < 0) {
      return 0;
    }
    return remain;
  }

  public long getAllSize() {
    return allSize;
  }

  public long getProcessSize() {
    return processSize;
  }

  private void initNewDisturb() {
    disturb = (int) (allSize * 0.0005);
    if (disturb <= 0) {
      disturb = 10;
    }
  }

  /**
   * Retrieve the next batch size. The dynamic batch size calculation formula is N^i = N^(i-1) - p *
   * s, where p is the step size, and s is the gradient.
   *
   * @return
   */
  public long getNextBatchSize() {
    if (allSize - processSize <= 0) {
      return 0;
    }
    if (nowRound == 0) {
      if (expectRounds <= 1) {
        processSize = allSize;
        return allSize;
      }
      // Set the first round to the 500th percentile.
      preBatchTime = System.currentTimeMillis();
      long curSize = (long) (allSize / (expectRounds * 2.5));
      curSize = uniformBatchSize(curSize);
      processSize = processSize + curSize;
      batchSizes.add(curSize);

      nowRound++;
      return curSize;
    } else if (nowRound == 1) {
      // Set the second round to the 200th percentile.
      long curSize = allSize / expectRounds;
      curSize = uniformBatchSize(curSize);
      costs.add(computeProcessTime());
      processSize = processSize + curSize;
      batchSizes.add(curSize);

      nowRound++;
      return curSize;
    } else {
      int nowCost = computeProcessTime();
      costs.add(nowCost);
      int size = costs.size();
      long curSize = 0;
      if (batchSizes.get(size - 1) - batchSizes.get(size - 2) == 0) {
        // To avoid being restricted to a certain value when the denominator is zero, introduce a
        // perturbation to the batchSize.
        curSize = batchSizes.get(size - 1) + disturb;
      } else {
        double tN1 = costs.get(size - 1) * allSize * 1.0 / batchSizes.get(size - 1);
        double tN2 = costs.get(size - 2) * allSize * 1.0 / batchSizes.get(size - 2);
        double gradient = (tN1 - tN2) / (batchSizes.get(size - 1) - batchSizes.get(size - 2));
        curSize = Double.valueOf(batchSizes.get(size - 1) - step * gradient).intValue();
        if (curSize <= 0) {
          // As a fallback, if the calculated value is abnormal, take the size of the previous
          // batch.
          curSize = batchSizes.get(size - 1);
        }
      }
      curSize = (int) Math.min(allSize - processSize, curSize);
      curSize = uniformBatchSize(curSize);

      // Gradient control: an increase of no more than 30% is allowed at a time.
      final double gradient = 1.3;
      if (batchSizes.size() > 0) {
        long lastBatchSize = batchSizes.get(batchSizes.size() - 1);
        if (1.0 * curSize / lastBatchSize > gradient) {
          curSize = (long) (lastBatchSize * gradient);
        }
      }

      if (nowCost < 30 * 1000) {
        // If each round of calculation is less than 30 seconds, do not allow the batch size to be
        // reduced,
        // as the gradient is not significant when the load is very low.
        long lastSize = batchSizes.get(batchSizes.size() - 1);
        if (curSize < lastSize) {
          curSize = lastSize + disturb;
        }
        if (curSize > remainSize()) {
          curSize = remainSize();
        }
      }

      batchSizes.add(curSize);
      processSize = processSize + curSize;

      log.info(
          "costList="
              + JSONObject.toJSONString(costs)
              + ", batchSizes="
              + JSONObject.toJSONString(batchSizes)
              + ", nowRound="
              + nowRound);
      nowRound++;
      return curSize;
    }
  }

  private long uniformBatchSize(long batchSize) {
    long size = batchSize;
    if (batchSize < minBatchSize) {
      size = minBatchSize;
    } else if (batchSize > maxBatchSize) {
      size = maxBatchSize;
    }
    long remain = remainSize();
    if (size > remain) {
      return remain;
    }
    return size;
  }
}
