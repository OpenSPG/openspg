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
package com.antgroup.openspg.common.util;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class RetryerUtil {

  public static final String COLON = ":";
  private static final Long DEFAULT_MULTIPLIER = 100L;
  private static final Long DEFAULT_MAXIMUM_TIME = 5L;
  private static final Integer DEFAULT_ATTEMPT_NUMBER = 10;

  private static Map<String, Retryer<Boolean>> retryerMap = Maps.newConcurrentMap();

  private static ReentrantLock reentrantLock = new ReentrantLock();

  public static Retryer<Boolean> getRetryer(
      Long multiplier, Long maximumTime, Integer attemptNumber) {
    if (multiplier == null || multiplier < 0) {
      multiplier = DEFAULT_MULTIPLIER;
    }
    if (maximumTime == null || maximumTime < 0) {
      maximumTime = DEFAULT_MAXIMUM_TIME;
    }
    if (attemptNumber == null || attemptNumber < -1) {
      attemptNumber = DEFAULT_ATTEMPT_NUMBER;
    }

    String key = multiplier + COLON + maximumTime + COLON + attemptNumber;
    Retryer<Boolean> retryer = retryerMap.get(key);
    if (retryer == null) {
      reentrantLock.lock();
      try {
        if ((retryer = retryerMap.get(key)) == null) {
          RetryerBuilder<Boolean> retryerBuilder =
              RetryerBuilder.<Boolean>newBuilder()
                  .retryIfException()
                  .retryIfResult(Predicates.equalTo(false))
                  .withWaitStrategy(
                      WaitStrategies.exponentialWait(multiplier, maximumTime, TimeUnit.SECONDS));

          if (attemptNumber == -1) {
            retryerBuilder.withStopStrategy(StopStrategies.neverStop()).build();
          } else {
            retryerBuilder.withStopStrategy(StopStrategies.stopAfterAttempt(attemptNumber));
          }
          retryer = retryerBuilder.build();
          retryerMap.put(key, retryer);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      } finally {
        reentrantLock.unlock();
      }
    }
    return retryer;
  }
}
