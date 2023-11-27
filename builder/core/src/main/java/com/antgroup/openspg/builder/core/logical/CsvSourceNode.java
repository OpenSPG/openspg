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

package com.antgroup.openspg.builder.core.logical;

import com.antgroup.openspg.server.core.builder.model.pipeline.NodeTypeEnum;
import com.antgroup.openspg.server.core.builder.model.pipeline.config.CsvSourceNodeConfig;

public class CsvSourceNode extends BaseNode<CsvSourceNodeConfig> {

  public CsvSourceNode(String id, String name, CsvSourceNodeConfig nodeConfig) {
    super(id, name, NodeTypeEnum.CSV_SOURCE, nodeConfig);
  }
}
