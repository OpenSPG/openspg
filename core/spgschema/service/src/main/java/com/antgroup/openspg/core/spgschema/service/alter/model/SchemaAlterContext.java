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

package com.antgroup.openspg.core.spgschema.service.alter.model;

import com.antgroup.openspg.common.model.base.BaseValObj;
import com.antgroup.openspg.common.model.project.Project;
import com.antgroup.openspg.core.spgschema.model.alter.AlterOperationEnum;
import com.antgroup.openspg.core.spgschema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The context of schema altering, used during the lifecycle of once alter.
 */
@Setter
@Getter
@Accessors(chain = true)
public class SchemaAlterContext extends BaseValObj {

    private static final long serialVersionUID = 6094939486899369547L;

    /**
     * The project that deploy new schema.
     */
    private Project project;

    /**
     * The released schema effective online
     */
    private List<BaseSPGType> releasedSchema;

    /**
     * The schema type that altered.
     */
    private List<BaseAdvancedType> alterSchema;

    /**
     * Get alter spg types by alter operation
     *
     * @param alterOperation alter operation
     * @return list of types
     */
    public List<BaseAdvancedType> getAlterTypeByAlterOperation(AlterOperationEnum alterOperation) {
        return alterSchema
            .stream()
            .filter(e -> alterOperation.equals(e.getAlterOperation()))
            .collect(Collectors.toList());
    }
}
