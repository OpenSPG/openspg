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

package com.antgroup.openspg.builder.core.physical.operator;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.pipeline.config.OperatorConfig;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pemja.core.PythonInterpreter;
import pemja.core.PythonInterpreterConfig;

@Slf4j
public class PythonOperatorFactory implements OperatorFactory {

  private String pythonExec;
  private String[] pythonPaths;
  private String pythonKnextPath;

  private PythonOperatorFactory() {}

  public static OperatorFactory getInstance() {
    return new PythonOperatorFactory();
  }

  private PythonInterpreter newPythonInterpreter() {

    PythonInterpreterConfig.PythonInterpreterConfigBuilder builder =
        PythonInterpreterConfig.newBuilder();
    if (pythonExec != null) {
      builder.setPythonExec(pythonExec);
    }
    if (pythonPaths != null) {
      builder.addPythonPaths(pythonPaths);
    }
    return new PythonInterpreter(builder.build());
  }

  @Override
  public void init(BuilderContext context) {
    pythonExec = context.getPythonExec();
    pythonPaths = (context.getPythonPaths() != null ? context.getPythonPaths().split(";") : null);
    pythonKnextPath = context.getPythonKnextPath();
    log.info("pythonExec={}, pythonPaths={}", pythonExec, Arrays.toString(pythonPaths));
  }

  public PythonInterpreter getPythonInterpreter(OperatorConfig config) {
    PythonInterpreter interpreter = newPythonInterpreter();
    loadOperatorObject(config, interpreter);
    return interpreter;
  }

  @Override
  public void loadOperator(OperatorConfig config) {}

  @Override
  public Object invoke(OperatorConfig config, Object... input) {
    PythonInterpreter interpreter = getPythonInterpreter(config);
    String pythonObject = getPythonOperatorObject(config);
    try {
      return interpreter.invokeMethod(pythonObject, config.getMethod(), input);
    } finally {
      interpreter.close();
    }
  }

  private void loadOperatorObject(OperatorConfig config, PythonInterpreter interpreter) {
    if (StringUtils.isNotBlank(pythonKnextPath)) {
      interpreter.exec(String.format("import sys; sys.path.append(\"%s\")", pythonKnextPath));
    }
    String pythonOperatorObject = getPythonOperatorObject(config);
    interpreter.exec(
        String.format("from %s import %s", config.getModulePath(), config.getClassName()));
    interpreter.exec(
        String.format(
            "%s=%s(%s)",
            pythonOperatorObject,
            config.getClassName(),
            paramToPythonString(config.getParams(), config.getParamsPrefix())));
  }

  private String getPythonOperatorObject(OperatorConfig config) {
    String pythonOperatorObject = config.getClassName() + "_" + config.getUniqueKey();
    return pythonOperatorObject;
  }

  private String paramToPythonString(Map<String, String> params, String paramsPrefix) {
    if (MapUtils.isEmpty(params)) {
      return "";
    }
    if (StringUtils.isBlank(paramsPrefix)) {
      paramsPrefix = "";
    }
    String keyValue =
        params.entrySet().stream()
            .map(entry -> String.format("'%s': '%s'", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(","));
    return String.format("%s{%s}", paramsPrefix, keyValue);
  }
}
