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

import com.alipay.common.tracer.core.utils.TracerUtils;
import com.antgroup.openspg.common.util.JsonUtils;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.logger.LoggerConstants;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/** Base class for a logging aspect. */
public interface BaseLogAspect extends InitializingBean {

  Logger log = LoggerFactory.getLogger(BaseLogAspect.class);

  /** Maximum log length */
  int MAX_LOG_LENGTH = 5000;

  /**
   * Constructs the log string
   *
   * @param className The class name
   * @param methodName The method name
   * @param elapsedTime The execution time
   * @param args The method arguments
   * @param result The method execution result
   * @param isLogDetail Whether to print detailed result
   * @param trimLog Whether to trim the log if it is too long
   * @return The string to be printed
   */
  default String constructLog(
      String className,
      String methodName,
      long elapsedTime,
      Object[] args,
      Object result,
      boolean isLogDetail,
      boolean trimLog) {

    StringBuilder sb = new StringBuilder();

    // Concatenating class and method names.
    sb.append(className);
    sb.append(LoggerConstants.LOG_SEP_POINT);
    sb.append(StringUtils.defaultIfEmpty(methodName, LoggerConstants.LOG_DEFAULT));
    sb.append(LoggerConstants.LOG_SEP);

    // Concatenating execution result and execution time.
    sb.append(getIsSuccess(result));
    sb.append(LoggerConstants.LOG_SEP);
    sb.append(elapsedTime);
    sb.append(LoggerConstants.TIME_UNIT);
    sb.append(LoggerConstants.LOG_SEP);
    sb.append(getResultSize(result));
    sb.append(LoggerConstants.LOG_SEP);

    // Adding trace-related logs.
    sb.append(constructTracerMsg());

    sb.append(LoggerConstants.LOG_SEP);

    // Adding service invocation parameter information.
    sb.append(constructObjectMsg(args));

    // Adding service invocation result information.
    if (isLogDetail) {
      sb.append(LoggerConstants.LOG_SEP);
      sb.append(constructObjectMsg(result));
    }

    // Checking whether to truncate the log when it is too long (currently printing the full log).
    if (trimLog && sb.length() > MAX_LOG_LENGTH) {
      sb.delete(MAX_LOG_LENGTH, sb.length());
      sb.append("...");
    }
    return sb.toString();
  }

  /**
   * Constructs tracer information, output format example: (traceId, appName, hostName)
   *
   * @return Tracer information
   */
  default String constructTracerMsg() {
    String traceId = null;
    String callerAppName = null;
    String callerIp = null;

    try {
      // todo
      traceId = TracerUtils.getTraceId();
      callerAppName = "";
      callerIp = "";
    } catch (Exception e) {
      log.error("Exception from the source application: ", e);
    }

    final StringBuilder sb = new StringBuilder();
    sb.append(StringUtils.defaultIfEmpty(traceId, LoggerConstants.LOG_DEFAULT));
    sb.append(LoggerConstants.LOG_SEP);
    sb.append(StringUtils.defaultIfEmpty(callerAppName, LoggerConstants.LOG_DEFAULT));
    sb.append(LoggerConstants.LOG_SEP);
    sb.append(StringUtils.defaultIfEmpty(callerIp, LoggerConstants.LOG_DEFAULT));
    return sb.toString();
  }

  /**
   * Constructs log information for a single object.
   *
   * @param object The object.
   * @return The log information.
   */
  default String constructObjectMsg(Object object) {
    if (object == null) {
      return LoggerConstants.LOG_DEFAULT;
    } else {
      return JsonUtils.toJsonString(object);
    }
  }

  /**
   * Determines if the interface service is successful.
   *
   * @param result The execution result.
   * @return "Y" if successful, "N" otherwise.
   */
  default String getIsSuccess(Object result) {
    return boolean2Str(isSuccess(result));
  }

  /**
   * Determines if the result is successful.
   *
   * @param result The execution result.
   * @return true if successful.
   */
  Boolean isSuccess(Object result);

  /**
   * Get the size of the result.
   *
   * @param result The execution result
   * @return
   */
  String getResultSize(Object result);

  /**
   * Converts an object to its string representation.
   *
   * @param object The object.
   * @return The string representation.
   */
  default String obj2Str(Object object) {
    if (object instanceof String) {
      return object.toString();
    }
    return StringUtils.toString(object);
  }

  /**
   * Converts a List object to its string representation.
   *
   * @param list The list object
   * @return The string representation.
   */
  default String list2Str(List<?> list) {
    if (CollectionUtils.isEmpty(list)) {
      return LoggerConstants.LOG_DEFAULT;
    }

    final StringBuilder sb = new StringBuilder("[");
    list.forEach(
        o -> {
          sb.append(obj2Str(o));
          sb.append(",");
        });
    sb.append("]");
    return sb.toString();
  }

  /**
   * Converts a Map object to its string representation.
   *
   * @param map The map object
   * @return The string representation
   */
  default String map2Str(Map<?, ?> map) {
    if (MapUtils.isEmpty(map)) {
      return LoggerConstants.LOG_DEFAULT;
    }

    final StringBuilder sb = new StringBuilder("{");
    map.forEach(
        (k, v) -> {
          sb.append(obj2Str(k));
          sb.append(LoggerConstants.LOG_SEP_EQUAL);
          sb.append(obj2Str(v));
          sb.append(",");
        });
    sb.append("}");
    return sb.toString();
  }

  /**
   * Converts a Boolean object to its string representation.
   *
   * @param isSuccess The boolean object
   * @return The string representation
   */
  default String boolean2Str(Boolean isSuccess) {
    if (isSuccess == null) {
      return LoggerConstants.NAN;
    }
    return isSuccess ? LoggerConstants.YES : LoggerConstants.NO;
  }

  /**
   * Returns the size of an object.
   *
   * @param object The object
   * @return The size of the object
   */
  default String obj2Size(Object object) {
    if (object == null) {
      return LoggerConstants.LOG_DEFAULT;
    }

    if (object.getClass().isArray()) {
      return String.valueOf(((Object[]) object).length);
    } else if (object instanceof List) {
      return String.valueOf(((List<?>) object).size());
    } else if (object instanceof Collection) {
      return String.valueOf(((Collection<?>) object).size());
    } else if (object instanceof Map) {
      return String.valueOf(((Map<?, ?>) object).size());
    }
    return LoggerConstants.LOG_DEFAULT;
  }

  /** @throws Exception */
  @Override
  default void afterPropertiesSet() throws Exception {
    log.info("{} Init.", this.getClass().getName());
  }
}
