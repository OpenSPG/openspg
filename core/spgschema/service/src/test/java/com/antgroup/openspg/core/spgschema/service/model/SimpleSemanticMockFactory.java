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

package com.antgroup.openspg.core.spgschema.service.model;

import com.antgroup.openspg.core.spgschema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.spgschema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.spgschema.service.semantic.model.SimpleSemantic;


public class SimpleSemanticMockFactory {

    public static SimpleSemantic mockPredicateSemantic(Long subjectId, Long objectId) {
        return new SimpleSemantic(SPGOntologyEnum.PROPERTY,
            subjectId.toString(),
            objectId.toString(),
            new PredicateIdentifier("p"),
            null,
            null,
            null);
    }
}
