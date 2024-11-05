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

package com.antgroup.openspg.builder.core.logical;

import com.antgroup.openspg.builder.model.pipeline.config.Neo4jSinkNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;

public class Neo4jSinkNode extends BaseLogicalNode<Neo4jSinkNodeConfig> {

  public Neo4jSinkNode(String id, String name, Neo4jSinkNodeConfig nodeConfig) {
    super(id, name, NodeTypeEnum.NEO4J_SINK, nodeConfig);
  }
}
