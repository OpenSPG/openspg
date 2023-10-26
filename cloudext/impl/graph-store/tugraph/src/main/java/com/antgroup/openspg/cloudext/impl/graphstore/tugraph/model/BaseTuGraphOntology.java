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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Schema of TuGraph.
 */
@Data
public class BaseTuGraphOntology implements Serializable {

    /**
     * Type
     */
    @JSONField(name = "type")
    protected TypeEnum type;

    /**
     * Label name
     */
    @JSONField(name = "label")
    protected String label;

    /**
     * Properties
     */
    @JSONField(name = "properties")
    protected List<TuGraphProperty> properties;
}
