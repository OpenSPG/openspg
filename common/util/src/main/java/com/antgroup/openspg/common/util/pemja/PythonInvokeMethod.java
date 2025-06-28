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
  BRIDGE_READER("bridge.spg_server_bridge", "SPGServerBridge", "run_reader", ""),
  BRIDGE_SCANNER("bridge.spg_server_bridge", "SPGServerBridge", "run_scanner", ""),
  BRIDGE_COMPONENT("bridge.spg_server_bridge", "SPGServerBridge", "run_component", ""),
  BRIDGE_GET_LLM_TOKEN_INFO(
      "bridge.spg_server_bridge", "SPGServerBridge", "get_llm_token_info", ""),
  BRIDGE_GET_INDEX_MANAGER_NAMES(
      "bridge.spg_server_bridge", "SPGServerBridge", "get_index_manager_names", ""),
  BRIDGE_GET_INDEX_MANAGER_INFO(
      "bridge.spg_server_bridge", "SPGServerBridge", "get_index_manager_info", ""),
  BRIDGE_LLM_CHECKER("bridge.spg_server_bridge", "SPGServerBridge", "run_llm_config_check", ""),
  BRIDGE_VECTORIZER_CHECKER(
      "bridge.spg_server_bridge", "SPGServerBridge", "run_vectorizer_config_check", ""),
  BRIDGE_SOLVER_MAIN("bridge.spg_server_bridge", "SPGServerBridge", "run_solver", ""),
  BRIDGE_BUILDER_MAIN("bridge.spg_server_bridge", "SPGServerBridge", "run_builder", "");

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
