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

package com.antgroup.openspg.core.spgbuilder.engine.physical.process;

import com.antgroup.openspg.core.spgbuilder.engine.physical.BasePhysicalNode;
import com.antgroup.openspg.core.spgbuilder.model.record.BaseRecord;

import lombok.Getter;

import java.util.List;

/**
 * Base class of processing nodes, which further processes the data from the upstream nodes, such as knowledge
 * extraction, data mapping, operator execution, and so on. Currently, we support the knowledge extraction processor and
 * the mapping processor.
 */
@Getter
public abstract class BaseProcessor<C> extends BasePhysicalNode {

    /**
     * The configuration of processor node.
     */
    protected final C config;

    public BaseProcessor(String id, String name, C config) {
        super(id, name);
        this.config = config;
    }

    /**
     * Process the input records and output the execution results to the downstream node.
     *
     * @param records : The output results from the upstream node.
     * @return Collection of data that has been processed.
     */
    public abstract List<BaseRecord> process(List<BaseRecord> records);
}
