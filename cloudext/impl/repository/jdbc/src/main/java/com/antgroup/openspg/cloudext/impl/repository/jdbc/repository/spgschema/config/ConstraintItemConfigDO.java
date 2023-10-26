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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.config;

import lombok.Data;


@Data
public class ConstraintItemConfigDO {

    /**
     * 约束id
     */
    private String id;

    /**
     * 约束名称
     */
    private String name;

    /**
     * 约束英文名称
     */
    private String nameZh;

    /**
     * 约束值
     */
    private Object value;
}
