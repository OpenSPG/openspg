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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema.operation;


public enum SchemaAtomicOperationEnum {

    /**
     * Add property of vertex or edge.
     */
    ADD_PROPERTY,

    /**
     * Drop property of vertex or edge
     */
    DROP_PROPERTY,

    /**
     * Create index in property
     */
    CREATE_INDEX,

    /**
     * Drop index in property
     */
    DROP_INDEX,

    /**
     * Set time-to-alive in vertex or edge
     */
    SET_TTL,

    /**
     * Unset time-to-alive in vertex or edge
     */
    UNSET_TTL,

    /**
     * Empty operation
     */
    EMPTY

}
