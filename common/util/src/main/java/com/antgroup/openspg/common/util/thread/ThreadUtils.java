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

package com.antgroup.openspg.common.util.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadUtils {

  /** thread sleep */
  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Wrapper over newFixedThreadPool. Thread names are formatted as prefix-ID, where ID is a unique,
   * sequentially assigned integer.
   */
  public static ThreadPoolExecutor newDaemonFixedThreadPool(int nThreads, String prefix) {
    ThreadFactory threadFactory = namedThreadFactory(prefix);
    return (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads, threadFactory);
  }

  /**
   * Create a thread factory that names threads with a prefix and also sets the threads to daemon.
   */
  public static ThreadFactory namedThreadFactory(String prefix) {
    return new ThreadFactoryBuilder()
        .setUncaughtExceptionHandler(SPGThread.UncaughtExceptionHandler)
        .setDaemon(true)
        .setNameFormat(prefix + "-%d")
        .build();
  }

  /** Wrapper over ScheduledThreadPoolExecutor. */
  public static ScheduledExecutorService newDaemonSingleThreadScheduledExecutor(String threadName) {
    ThreadFactory threadFactory =
        new ThreadFactoryBuilder().setDaemon(true).setNameFormat(threadName).build();
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, threadFactory);
    // By default, a cancelled task is not automatically removed from the work queue until its delay
    // elapses. We have to enable it manually.
    executor.setRemoveOnCancelPolicy(true);
    return executor;
  }

  public static int nThreads(String nThreads) {
    if (nThreads.startsWith("*")) {
      return Runtime.getRuntime().availableProcessors() * Integer.parseInt(nThreads.substring(1));
    }
    return Integer.parseInt(nThreads);
  }
}
