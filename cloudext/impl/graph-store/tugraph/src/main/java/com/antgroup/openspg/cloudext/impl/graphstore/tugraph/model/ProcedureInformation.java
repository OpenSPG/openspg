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
import lombok.Data;

/** Information of procedure. */
@Data
public class ProcedureInformation {

  @JSONField(name = "plugin_description")
  private PluginDescription pluginDescription;

  @Data
  public static class PluginDescription {

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "description")
    private String description;

    @JSONField(name = "read_only")
    private Boolean readOnly;

    @JSONField(name = "signature")
    private String signature;

    @JSONField(name = "type")
    private String type;

    @JSONField(name = "version")
    private String version;
  }
}
