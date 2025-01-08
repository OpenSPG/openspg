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
import com.antgroup.openspg.common.util.pemja.PemjaUtils;
import com.antgroup.openspg.common.util.pemja.model.PemjaConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PythonOperatorFactory implements OperatorFactory {

  private String pythonExec;
  private String pythonPaths;
  private String hostAddr;
  private Long projectId;

  private PythonOperatorFactory() {}

  public static OperatorFactory getInstance() {
    return new PythonOperatorFactory();
  }

  @Override
  public void init(BuilderContext context) {
    pythonExec = context.getPythonExec();
    pythonPaths = context.getPythonPaths();
    hostAddr = context.getSchemaUrl();
    projectId = context.getProjectId();
    log.info("pythonExec={}, pythonPaths={}", pythonExec, pythonPaths);
  }

  @Override
  public void loadOperator(OperatorConfig config) {}

  @Override
  public Object invoke(OperatorConfig config, Object... input) {
    PemjaConfig pemjaConfig =
        new PemjaConfig(
            pythonExec,
            pythonPaths,
            hostAddr,
            projectId,
            config.getModulePath(),
            config.getClassName(),
            config.getMethod(),
            config.getParams(),
            config.getParamsPrefix());
    return PemjaUtils.invoke(pemjaConfig, input);
  }
}
