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

import java.util.Map;
import java.util.concurrent.Callable;
import org.slf4j.MDC;

public abstract class TraceCallableWrapper<T> implements Callable<T> {

  private Map<String, String> contextMap = null;
  private long tid = Thread.currentThread().getId();

  public TraceCallableWrapper() {
    if (MDC.getMDCAdapter() != null) {
      this.contextMap = MDC.getCopyOfContextMap();
    }
  }

  @Override
  public T call() throws Exception {
    if (contextMap != null) {
      MDC.setContextMap(contextMap);
    }
    try {
      return doCall();
    } finally {
      if (MDC.getMDCAdapter() != null && Thread.currentThread().getId() != tid) {
        MDC.clear();
      }
    }
  }

  protected abstract T doCall() throws Exception;

  public static <T> TraceCallableWrapper<T> of(Callable<T> callable) {
    return new TraceCallableWrapper<T>() {
      @Override
      protected T doCall() throws Exception {
        return callable.call();
      }
    };
  }
}
