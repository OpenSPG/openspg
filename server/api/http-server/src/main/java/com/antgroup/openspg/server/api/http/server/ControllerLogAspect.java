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

package com.antgroup.openspg.server.api.http.server;

import com.antgroup.openspg.biz.common.util.BaseLogAspect;
import com.antgroup.openspg.biz.common.util.BizThreadLocal;
import com.antgroup.openspg.common.util.logger.LoggerConstants;
import com.google.common.base.Stopwatch;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

/** Logging Aspect of Controller */
@Slf4j
public class ControllerLogAspect implements BaseLogAspect {

  /** Controller Layer Logging */
  private static final String CONTROLLER_DIGEST_LOGGER_NAME = "CONTROLLER-DIGEST";

  private static final Logger CONTROLLER_DIGEST_LOGGER =
      LoggerFactory.getLogger(CONTROLLER_DIGEST_LOGGER_NAME);

  /** Controller Around Point */
  public Object doAround(ProceedingJoinPoint point) throws Throwable {
    Stopwatch stopwatch = Stopwatch.createStarted();

    // Method Class, Method Name, and Arguments
    final String className = point.getTarget().getClass().getSimpleName();
    final String methodName = ((MethodSignature) (point.getSignature())).getMethod().getName();
    Object[] methodArgs = point.getArgs();
    if (methodArgs != null) {
      methodArgs =
          Arrays.stream(methodArgs)
              .filter(x -> !(x instanceof HttpServletRequest))
              .filter(x -> !(x instanceof HttpServletResponse))
              .toArray();
    }

    Object result = null;
    try {
      // Start executing the business method.
      BizThreadLocal.enter();
      result = point.proceed();
      return result;
    } catch (Throwable e) {
      // Under normal circumstances, this exception should not occur,
      // and the exception should be handled by BizTemplate.
      log.error(
          "An error occurred while the Controller intercepted and printed the system service logs.",
          e);
      throw e;
    } finally {
      try {
        final long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        Boolean isSuccess = isSuccess(result);
        // Print summary logs.
        if (CONTROLLER_DIGEST_LOGGER.isInfoEnabled()) {
          CONTROLLER_DIGEST_LOGGER.info(
              constructLog(
                  className,
                  methodName,
                  elapsedTime,
                  methodArgs,
                  result,
                  !BooleanUtils.isTrue(isSuccess),
                  true));
        }
      } catch (Exception e) {
        log.error(
            "An error occurred while the Controller intercepted and printed the system service logs.",
            e);
      }
      // The business method has completed execution.
      BizThreadLocal.exit();
    }
  }

  @Override
  public Boolean isSuccess(Object result) {
    Boolean isSuccess = Boolean.TRUE;
    if (result instanceof ResponseEntity) {
      ResponseEntity<?> response = (ResponseEntity<?>) result;
      isSuccess = response.getStatusCode().is2xxSuccessful();
    } else if (result instanceof Boolean) {
      isSuccess = (Boolean) result;
    }
    return isSuccess;
  }

  @Override
  public String getResultSize(Object result) {
    if (result instanceof ResponseEntity) {
      ResponseEntity<?> response = (ResponseEntity<?>) result;
      if (isSuccess(result)) {
        return obj2Size(response);
      }
    }
    return LoggerConstants.LOG_DEFAULT;
  }
}
