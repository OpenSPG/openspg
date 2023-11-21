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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.model;

import com.alibaba.fastjson.annotation.JSONField;
import lgraph.Lgraph.LoadPluginRequest.CodeType;
import lgraph.Lgraph.PluginRequest.PluginType;
import lombok.Data;

/** Config of plugin. */
@Data
public class PluginConfig {

  /** Type of plugin Either "CPP" or "PYTHON" */
  @JSONField(name = "type")
  private PluginType type;

  /** Name of plugin */
  @JSONField(name = "name")
  private String name;

  /** File name of plugin */
  @JSONField(name = "filePath")
  private String filePath;

  /** Code type of plugin Such as: PY, SO, CPP, ZIP */
  @JSONField(name = "codeType")
  private CodeType codeType;

  /** Description of plugin */
  @JSONField(name = "description")
  private String description;

  /**
   * Plugin is used for reading only If plugin used for modified data, the property value is 'false'
   */
  @JSONField(name = "readOnly")
  private boolean readOnly;

  /** Version of plugin Either "v1" or "v2" */
  @JSONField(name = "version")
  private String version = "v1";
}
