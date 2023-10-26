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

package com.antgroup.openspg.core.spgreasoner.model.service;

/**
 * This class defines the input content types of knowledge reasoning tasks.
 * <p>
 * In actual scenarios, knowledge reasoning can be based on vertices or edges (when there are attributes expressed by
 * logical rules). In addition, the most direct reasoning method is to reason based on a piece of Kgdsl
 */
public enum ReasonerContentTypeEnum {
    /**
     * Vertex-based query or reasoning. The user passes in the id of a vertex. If some attributes of the vertex are
     * defined by logical rules, the logical rules will be calculated first and then returned; If all the attributes of
     * the vertex are defined by non-logical rules, the vertex’s attributes can be returned directly.
     */
    VERTEX,

    /**
     * KGDSL-based reasoning.  The user passes in a piece of KGDSL, and the knowledge reasoning engine will run this
     * piece of KGDSL based on the factual data to get the reasoning result. If this KGDSL refers to other KGDSL
     * definitions, it will execute the dependent KGDSL first, and then run the current KGDSL like a Java method call;
     */
    KGDSL,
    ;
}
