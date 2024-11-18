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

package com.antgroup.openspg.server.biz.common.util;

import com.antgroup.openspg.common.util.logger.LoggerConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/** Business thread variables. */
@Slf4j
public class BizThreadLocal {

  /** Local business thread variables. */
  private static final ThreadLocal<BizContext> BIZ_LOCAL_CONTEXT = new ThreadLocal<>();

  /**
   * Retrieves the current business thread variable from the local thread.
   *
   * @return The business thread context
   */
  public static BizContext get() {
    return BIZ_LOCAL_CONTEXT.get();
  }

  public static String getTraceId() {
    BizContext bizContext = BIZ_LOCAL_CONTEXT.get();
    if (bizContext != null) {
      return bizContext.getTraceId();
    }
    return null;
  }

  /** Initialize business thread variables. */
  public static BizContext enter() {
    BizContext context = BIZ_LOCAL_CONTEXT.get();

    if (context == null) {
      context = new BizContext();
      BIZ_LOCAL_CONTEXT.set(context);
      injectMdc(context.getTraceId(), context.getRpcId());
    }
    context.enter();
    return context;
  }

  /** Set the business thread variables. */
  public static void put(BizContext context) {
    BIZ_LOCAL_CONTEXT.set(context);
  }

  /** Release current business thread variables. */
  public static void exit() {
    final BizContext context = BIZ_LOCAL_CONTEXT.get();
    if (context != null) {
      int release = context.release();
      if (release < 0) {
        log.warn(
            "The BizContext enter/exit calls are not properly paired, and there are multiple exit calls.");
      }

      if (release <= 0) {
        BIZ_LOCAL_CONTEXT.remove();
        clearMdc();
      }
    }
  }

  /** Injecting MDC */
  public static void injectMdc(String traceId, String rpcId) {
    MDC.put(LoggerConstants.TRACE_ID, traceId);
    MDC.put(LoggerConstants.RPC_ID, rpcId);
  }

  /** Clearing MDC */
  public static void clearMdc() {
    MDC.remove(LoggerConstants.TRACE_ID);
    MDC.remove(LoggerConstants.RPC_ID);
  }
}
