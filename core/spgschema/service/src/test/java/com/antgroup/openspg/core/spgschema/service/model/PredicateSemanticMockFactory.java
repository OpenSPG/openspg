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

import com.antgroup.openspg.core.spgschema.model.OntologyId;
import com.antgroup.openspg.core.spgschema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.spgschema.model.predicate.PropertyRef;
import com.antgroup.openspg.core.spgschema.model.semantic.PredicateSemantic;
import com.antgroup.openspg.core.spgschema.model.semantic.SystemPredicateEnum;


public class PredicateSemanticMockFactory {

    public static PredicateSemantic mockInverseOfSemantic(String subjectTypeName, Long subjectTypeId,
        String objectTypeName, Long objectTypeId) {
        PropertyRef subjectRef = PropertyMockFactory
            .mockEntityProperty(subjectTypeName, subjectTypeId,
                objectTypeName, objectTypeId).toRef();
        subjectRef.setOntologyId(new OntologyId(MockConstants.ENTITY_PROPERTY_ID));
        PropertyRef objectRef = PropertyMockFactory
            .mockEntityProperty(objectTypeName, objectTypeId,
                subjectTypeName, subjectTypeId).toRef();
        objectRef.setOntologyId(new OntologyId(MockConstants.INVERSE_PROPERTY_ID));

        return new PredicateSemantic(subjectRef, new PredicateIdentifier(SystemPredicateEnum.INVERSE_OF.getName()),
            objectRef);
    }
}
