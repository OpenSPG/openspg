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

public enum PythonInvokeMethod {
  BRIDGE_READER("kag.bridge.spg_server_bridge", "SPGServerBridge", "run_reader", ""),
  BRIDGE_COMPONENT("kag.bridge.spg_server_bridge", "SPGServerBridge", "run_component", ""),
  BATCH_VECTORIZER(
      "kag.builder.component.vectorizer.batch_vectorizer", "BatchVectorizer", "_handle", "**"),
  LLM_CONFIG_CHECKER("kag.common.llm.llm_config_checker", "LLMConfigChecker", "check", ""),
  VECTORIZER_CONFIG_CHECKER(
      "kag.common.vectorize_model.vectorize_model_config_checker",
      "VectorizeModelConfigChecker",
      "check",
      ""),
  SOLVER_MAIN("kag.solver.main_solver", "SolverMain", "invoke", "");

  String modulePath;

  String className;

  String method;

  String paramsPrefix;

  PythonInvokeMethod(String modulePath, String className, String method, String paramsPrefix) {
    this.modulePath = modulePath;
    this.className = className;
    this.method = method;
    this.paramsPrefix = paramsPrefix;
  }

  public String getModulePath() {
    return modulePath;
  }

  public String getClassName() {
    return className;
  }

  public String getMethod() {
    return method;
  }

  public String getParamsPrefix() {
    return paramsPrefix;
  }
}
