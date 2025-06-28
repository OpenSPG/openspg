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

package com.antgroup.openspg.common.util.pemja;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.Md5Utils;
import com.antgroup.openspg.common.util.RetryerUtil;
import com.antgroup.openspg.common.util.SimpleThreadFactory;
import com.antgroup.openspg.common.util.TraceCallableWrapper;
import com.antgroup.openspg.common.util.pemja.model.PemjaConfig;
import com.github.rholder.retry.Retryer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pemja.core.PythonInterpreter;
import pemja.core.PythonInterpreterConfig;

@Slf4j
public class PemjaUtils {

  private static Retryer<Boolean> retry = RetryerUtil.getRetryer(50L, 2L, 3);

  public static Object invoke(PemjaConfig config, Object... input) {
    String md5 = Md5Utils.md5Of(UUID.randomUUID().toString());
    String uniqueKey = config.getClassName() + "_" + md5;
    log.info(
        "PemjaUtils.invoke uniqueKey:{} config:{} input:{}",
        uniqueKey,
        JSONObject.toJSONString(config),
        JSONObject.toJSONString(input));
    AtomicReference<Object> result = new AtomicReference<>();
    AtomicReference<RuntimeException> throwable = new AtomicReference<>();
    try {
      retry.call(
          () -> {
            log.info("PemjaUtils.invokeMethod uniqueKey:{}", uniqueKey);
            ThreadPoolExecutor executor =
                new ThreadPoolExecutor(
                    1,
                    1,
                    5L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1000),
                    new SimpleThreadFactory("pemja"));
            executor.allowCoreThreadTimeOut(true);
            Long start = System.currentTimeMillis();
            Future<Object> future =
                executor.submit(
                    TraceCallableWrapper.of(
                        () -> {
                          PythonInterpreter interpreter = null;
                          try {
                            interpreter = getPythonInterpreter(config, uniqueKey);
                            return interpreter.invokeMethod(uniqueKey, config.getMethod(), input);
                          } finally {
                            if (interpreter != null) {
                              interpreter.close();
                              log.info(
                                  "PemjaUtils.invokeMethod interpreter close uniqueKey:"
                                      + uniqueKey);
                            }
                          }
                        }));
            try {
              result.set(future.get(9, TimeUnit.MINUTES));
              log.info(
                  "PemjaUtils.invoke succeed cons:{} uniqueKey:{} config:{}",
                  System.currentTimeMillis() - start,
                  uniqueKey,
                  JSONObject.toJSONString(config));
              return true;
            } catch (TimeoutException e) {
              log.error("PemjaUtils.invoke TimeoutException uniqueKey:" + uniqueKey, e);
              future.cancel(true);
              RuntimeException exception =
                  new RuntimeException("PemjaUtils.invoke TimeoutException:" + e.getMessage(), e);
              throwable.set(exception);
              throw exception;
            } catch (Exception e) {
              log.error(
                  String.format(
                      "PemjaUtils.invoke Exception cons:%s uniqueKey:%s config:%s",
                      System.currentTimeMillis() - start,
                      uniqueKey,
                      JSONObject.toJSONString(config)),
                  e);
              RuntimeException exception =
                  new RuntimeException("PemjaUtils.invoke Exception:" + e.getMessage(), e);
              throwable.set(exception);
              return true;
            } finally {
              executor.shutdown();
              log.info(
                  String.format(
                      "PemjaUtils.invoke executor shutdown: activeCount=%s",
                      executor.getActiveCount()));
            }
          });
    } catch (Exception e) {
      log.error("PemjaUtils.invoke retry Exception uniqueKey:" + uniqueKey, e);
      throw new RuntimeException("PemjaUtils.invoke retry Exception:" + e.getMessage(), e);
    }
    if (result.get() == null) {
      throw throwable.get();
    }
    return result.get();
  }

  public static void invokeAsync(PemjaConfig config, Object... input) {
    String uniqueKey = config.getClassName() + "_" + Md5Utils.md5Of(UUID.randomUUID().toString());
    log.info(
        "PemjaUtils.invokeAsync uniqueKey:{} config:{} input:{}",
        uniqueKey,
        JSONObject.toJSONString(config),
        JSONObject.toJSONString(input));
    PythonInterpreter interpreter = null;
    try {
      Long start = System.currentTimeMillis();
      interpreter = getPythonInterpreter(config, uniqueKey);
      interpreter.invokeMethod(uniqueKey, config.getMethod(), input);
      log.info(
          "PemjaUtils.invokeAsync succeed cons:{} uniqueKey:{} config:{}",
          System.currentTimeMillis() - start,
          uniqueKey,
          JSONObject.toJSONString(config));
    } finally {
      if (interpreter != null) {
        interpreter.close();
        log.info("PemjaUtils.invokeAsync interpreter close uniqueKey:" + uniqueKey);
      }
    }
  }

  private static PythonInterpreter getPythonInterpreter(PemjaConfig config, String uniqueKey) {
    log.info("PemjaUtils.getPythonInterpreter start uniqueKey:{}", uniqueKey);
    PythonInterpreter interpreter =
        newPythonInterpreter(config.getPythonExec(), config.getPythonPaths());
    log.info("PemjaUtils.getPythonInterpreter newPythonInterpreter uniqueKey:{}", uniqueKey);
    if (config.getProjectId() != null) {
      interpreter.exec("from kag.bridge.spg_server_bridge import init_kag_config");
      interpreter.exec(
          String.format(
              "init_kag_config(\"%s\",\"%s\")", config.getProjectId(), config.getHostAddr()));
    }
    Long start = System.currentTimeMillis();
    log.info("PemjaUtils.getPythonInterpreter before exec uniqueKey:{}", uniqueKey);
    interpreter.exec(
        String.format("from %s import %s", config.getModulePath(), config.getClassName()));
    log.info(
        String.format(
            "PemjaUtils.getPythonInterpreter exec1 uniqueKey：%s cost:%s",
            uniqueKey, System.currentTimeMillis() - start));
    interpreter.exec(
        String.format(
            "%s=%s(%s)",
            uniqueKey,
            config.getClassName(),
            paramToPythonString(config.getParams(), config.getParamsPrefix())));
    log.info(
        String.format(
            "PemjaUtils.getPythonInterpreter exec2 uniqueKey：%s cost:%s",
            uniqueKey, System.currentTimeMillis() - start));
    return interpreter;
  }

  public static PythonInterpreter newPythonInterpreter(String pythonExec, String pythonPaths) {
    log.info(
        String.format(
            "PemjaUtils.newPythonInterpreter start pythonExec：%s pythonPaths:%s",
            pythonExec, pythonPaths));
    PythonInterpreterConfig.PythonInterpreterConfigBuilder builder =
        PythonInterpreterConfig.newBuilder();
    log.info("PemjaUtils.newPythonInterpreter newBuilder");
    if (StringUtils.isNotBlank(pythonExec)) {
      builder.setPythonExec(pythonExec);
    }
    log.info("PemjaUtils.newPythonInterpreter setPythonExec");
    if (StringUtils.isNotBlank(pythonPaths)) {
      String[] pythonPathList = pythonPaths.split(";");
      builder.addPythonPaths(pythonPathList);
    }
    log.info("PemjaUtils.newPythonInterpreter addPythonPaths");
    return new PythonInterpreter(builder.build());
  }

  private static String paramToPythonString(Map<String, String> params, String paramsPrefix) {
    if (MapUtils.isEmpty(params)) {
      return "";
    }
    if (StringUtils.isBlank(paramsPrefix)) {
      paramsPrefix = "**";
    }
    String keyValue =
        params.entrySet().stream()
            .map(entry -> String.format("'%s': '%s'", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(","));
    return String.format("%s{%s}", paramsPrefix, keyValue);
  }
}
