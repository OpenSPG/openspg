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

package com.antgroup.openspg.core.spgbuilder.engine.logical;


import com.antgroup.openspg.core.spgbuilder.model.pipeline.NodeTypeEnum;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.config.BaseNodeConfig;

import lombok.EqualsAndHashCode;
import lombok.Getter;


@Getter
@EqualsAndHashCode
public abstract class BaseNode<C extends BaseNodeConfig> {

    /**
     * Node id is a unique identifier for nodes and is used for linking nodes together.
     */
    @EqualsAndHashCode.Include
    private final String id;

    /**
     * The name of the node.
     */
    private final String name;

    /**
     * Node type is divided into three categories: source, process, and sink.
     */
    private final NodeTypeEnum type;

    /**
     * Each node needs to define its own configuration.
     */
    private final C nodeConfig;

    public BaseNode(String id, String name, NodeTypeEnum type, C nodeConfig) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.nodeConfig = nodeConfig;
    }
}
