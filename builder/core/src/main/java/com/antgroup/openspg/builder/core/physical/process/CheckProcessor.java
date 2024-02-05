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

import com.antgroup.openspg.builder.model.pipeline.config.BaseNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import java.util.List;

public class CheckProcessor extends BaseProcessor<CheckProcessor.CheckNodeConfig> {

  public static class CheckNodeConfig extends BaseNodeConfig {
    public CheckNodeConfig() {
      super(NodeTypeEnum.CHECK);
    }
  }

  private static final String PROCESSOR_NAME = "CHECK";

  public CheckProcessor() {
    super(PROCESSOR_NAME, PROCESSOR_NAME, null);
  }

  @Override
  public void close() throws Exception {}

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    return inputs;
  }
}
