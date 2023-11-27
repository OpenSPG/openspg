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

package com.antgroup.openspg.server.biz.common.util;

import com.alipay.common.tracer.core.generator.TraceIdGenerator;
import com.alipay.common.tracer.core.utils.TracerUtils;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/** 业务线程上下文 */
@Getter
@Slf4j
public final class BizContext {

  /** traceId */
  private String traceId;

  /** callerApp */
  private String callerApp = "";

  /** rpcId */
  private final String rpcId;

  /** level */
  private final AtomicInteger level = new AtomicInteger();

  public BizContext() {
    traceId = TracerUtils.getTraceId();
    if (StringUtils.isBlank(traceId)) {
      traceId = TraceIdGenerator.generate();
    }
    // todo
    callerApp = "";
    rpcId = "";
  }

  public int enter() {
    return level.incrementAndGet();
  }

  public int release() {
    return level.decrementAndGet();
  }
}
