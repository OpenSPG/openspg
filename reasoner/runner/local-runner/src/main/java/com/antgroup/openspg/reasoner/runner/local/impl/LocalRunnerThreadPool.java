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

package com.antgroup.openspg.reasoner.runner.local.impl;

import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

public class LocalRunnerThreadPool {

  private LocalRunnerThreadPool() {}

  /**
   * when the number of threads is greater than the core, this is the maximum time that excess idle
   * threads will wait for new tasks before terminating.
   */
  private static final long KEEP_ALIVE_TIME_SECONDS = 5 * 60;

  private static final int DEFAULT_CORE_POOL_SIZE = 10;
  private static final int DEFAULT_MAXIMUM_POOL_SIZE = 300;

  private static final int WORK_QUEUE_SIZE = 1000;

  private static volatile ThreadPoolExecutor DEFAULT_THREAD_POOL_EXECUTOR = null;

  /** get thread pool */
  public static ThreadPoolExecutor getThreadPoolExecutor(LocalReasonerTask task) {
    if (null == task || null == task.getThreadPoolExecutor()) {
      return getDefaultThreadPoolExecutor();
    }
    return task.getThreadPoolExecutor();
  }

  private static ThreadPoolExecutor getDefaultThreadPoolExecutor() {
    if (null != DEFAULT_THREAD_POOL_EXECUTOR) {
      return DEFAULT_THREAD_POOL_EXECUTOR;
    }
    synchronized (LocalRunnerThreadPool.class) {
      if (null != DEFAULT_THREAD_POOL_EXECUTOR) {
        return DEFAULT_THREAD_POOL_EXECUTOR;
      }
      DEFAULT_THREAD_POOL_EXECUTOR =
          new ThreadPoolExecutor(
              DEFAULT_CORE_POOL_SIZE,
              DEFAULT_MAXIMUM_POOL_SIZE,
              KEEP_ALIVE_TIME_SECONDS,
              TimeUnit.SECONDS,
              new LinkedBlockingQueue<>(WORK_QUEUE_SIZE),
              runnable -> new Thread(runnable, "LocalRDG-" + nextThreadNum()),
              new CallerRunsPolicy());
    }
    return DEFAULT_THREAD_POOL_EXECUTOR;
  }

  private static int threadInitNumber = 0;

  private static synchronized int nextThreadNum() {
    return threadInitNumber++;
  }
}
