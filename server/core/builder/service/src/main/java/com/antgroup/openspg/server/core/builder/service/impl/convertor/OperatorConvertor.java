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

package com.antgroup.openspg.server.core.builder.service.impl.convertor;

import com.antgroup.openspg.core.spgbuilder.model.operator.OperatorOverview;
import com.antgroup.openspg.core.spgbuilder.model.operator.OperatorVersion;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.config.OperatorConfig;

public class OperatorConvertor {

  public static OperatorConfig toOperatorConfig(
      OperatorOverview overview, OperatorVersion version) {
    return new OperatorConfig(
        overview.getName(),
        version.getVersion(),
        version.getFilePath(),
        version.getMainClass(),
        overview.getLangType(),
        overview.getType(),
        null);
  }
}
