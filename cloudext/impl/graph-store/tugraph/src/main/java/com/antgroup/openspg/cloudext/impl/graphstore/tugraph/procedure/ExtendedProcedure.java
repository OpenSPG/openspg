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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.procedure;

import lgraph.Lgraph;

/**
 * Extended procedure. PS1: Before use extended procedure, you should make sure that had uploaded
 * .cpp/.so file to TuGraph server.
 */
public class ExtendedProcedure extends BaseTuGraphProcedure {

  /** Cypher template */
  private static final String EXTENDED_PROCEDURE_CYPHER_TEMPLATE =
      "CALL plugin.${pluginType}.${pluginName}('${paramsJsonString}')";

  /** Type of plugin. either 'cpp' or 'python' */
  private final String pluginType;

  /** Name of plugin */
  private final String pluginName;

  /** JSON string of params */
  private final String paramsJsonString;

  /**
   * Constructor.
   *
   * @param cypherTemplate Template of cypher
   * @param pluginType Type of plugin
   * @param pluginName Name of plugin
   * @param paramsJsonString JSON string of params
   */
  private ExtendedProcedure(
      String cypherTemplate, String pluginType, String pluginName, String paramsJsonString) {
    super(cypherTemplate);
    this.pluginType = pluginType;
    this.pluginName = pluginName;
    this.paramsJsonString = paramsJsonString;
  }

  public static ExtendedProcedure of(
      Lgraph.PluginRequest.PluginType pluginType, String pluginName, String paramsJsonString) {
    if ((pluginType != Lgraph.PluginRequest.PluginType.CPP
        && pluginType != Lgraph.PluginRequest.PluginType.PYTHON)) {
      throw new IllegalArgumentException("unsupported type of plugin:" + pluginType);
    }
    return new ExtendedProcedure(
        EXTENDED_PROCEDURE_CYPHER_TEMPLATE,
        pluginType.name().toLowerCase(),
        pluginName,
        paramsJsonString);
  }

  @Override
  public String toString() {
    return "{\"procedure\":\"ExtendedProcedure\", "
        + "\"pluginType\":\""
        + pluginType
        + "\", "
        + "\"pluginName\":\""
        + pluginName
        + "\", "
        + "\"paramsJsonString\":\""
        + paramsJsonString
        + "\", "
        + "\"cypherTemplate\":\""
        + getCypherTemplate()
        + "\"}";
  }
}
