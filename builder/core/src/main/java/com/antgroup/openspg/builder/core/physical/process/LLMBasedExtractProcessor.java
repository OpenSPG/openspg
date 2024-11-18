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

package com.antgroup.openspg.builder.core.physical.process;

import com.antgroup.openspg.builder.model.pipeline.config.LLMBasedExtractNodeConfig;

public class LLMBasedExtractProcessor extends BasePythonProcessor<LLMBasedExtractNodeConfig> {

  public LLMBasedExtractProcessor(String id, String name, LLMBasedExtractNodeConfig config) {
    super(id, name, config);
  }
}
