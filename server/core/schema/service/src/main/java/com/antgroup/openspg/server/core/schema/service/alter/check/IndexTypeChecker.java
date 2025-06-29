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

package com.antgroup.openspg.server.core.schema.service.alter.check;

import com.antgroup.openspg.core.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.schema.model.type.IndexType;

public class IndexTypeChecker extends BaseSpgTypeChecker {
  @Override
  public void checkAdvancedConfig(BaseAdvancedType advancedType, SchemaCheckContext context) {
    IndexType indexType = (IndexType) advancedType;
    String schemaTypeName = indexType.getName();
    OperatorChecker.check(schemaTypeName, indexType.getAdvancedConfig().getLinkOperator());
    OperatorChecker.check(schemaTypeName, indexType.getAdvancedConfig().getFuseOperator());
  }
}
