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

package com.antgroup.openspg.common.util.pemja.model;

import com.antgroup.openspg.common.util.constants.CommonConstant;
import com.antgroup.openspg.common.util.pemja.PythonInvokeMethod;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class PemjaConfig {

  private String pythonExec;

  private String pythonPaths;

  private String modulePath;

  private String className;

  private String method;

  private Long projectId;

  private String hostAddr;

  private Map<String, String> params;

  private String paramsPrefix;

  public PemjaConfig(
      String pythonExec,
      String pythonPaths,
      String pythonEnv,
      String hostAddr,
      Long projectId,
      String modulePath,
      String className,
      String method,
      Map<String, String> params,
      String paramsPrefix) {
    this.pythonExec = pythonExec;
    this.pythonPaths = pythonPaths;
    pythonEnv = (pythonEnv == null) ? CommonConstant.KAG : pythonEnv;
    this.modulePath = pythonEnv + modulePath;
    this.className = className;
    this.method = method;
    this.params = params;
    this.paramsPrefix = paramsPrefix;
    this.projectId = projectId;
    this.hostAddr = hostAddr;
  }

  public PemjaConfig(
      String pythonExec,
      String pythonPaths,
      String pythonEnv,
      String hostAddr,
      Long projectId,
      PythonInvokeMethod pythonInvoke,
      Map<String, String> params) {
    this(
        pythonExec,
        pythonPaths,
        pythonEnv,
        hostAddr,
        projectId,
        pythonInvoke.getModulePath(),
        pythonInvoke.getClassName(),
        pythonInvoke.getMethod(),
        params,
        pythonInvoke.getParamsPrefix());
  }
}
