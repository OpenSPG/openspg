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

package com.antgroup.openspg.builder.model.pipeline.config;

import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import lombok.Getter;

@Getter
public class ParagraphSplitNodeConfig extends BasePythonNodeConfig {

  private final String nl;
  private final Boolean semanticSplit;
  private final Long splitLength;
  private final Boolean builderIndex;
  private final String token;

  public ParagraphSplitNodeConfig(
      OperatorConfig operatorConfig,
      String nl,
      Boolean semanticSplit,
      Long splitLength,
      Boolean builderIndex,
      String token) {
    super(NodeTypeEnum.PARAGRAPH_SPLIT, operatorConfig);
    this.nl = nl;
    this.semanticSplit = semanticSplit;
    this.splitLength = splitLength;
    this.builderIndex = builderIndex;
    this.token = token;
  }
}
