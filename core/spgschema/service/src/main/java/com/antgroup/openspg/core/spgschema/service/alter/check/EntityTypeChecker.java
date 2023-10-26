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

package com.antgroup.openspg.core.spgschema.service.alter.check;

import com.antgroup.openspg.core.spgschema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.spgschema.model.type.EntityType;


public class EntityTypeChecker extends BaseSpgTypeChecker {

    @Override
    public void checkAdvancedConfig(BaseAdvancedType advancedType, SchemaCheckContext context) {
        EntityType entityType = (EntityType) advancedType;
        String schemaTypeName = entityType.getName();

        OperatorChecker.check(schemaTypeName, entityType.getAdvancedConfig().getLinkOperator());
        OperatorChecker.check(schemaTypeName, entityType.getAdvancedConfig().getFuseOperator());
    }
}
