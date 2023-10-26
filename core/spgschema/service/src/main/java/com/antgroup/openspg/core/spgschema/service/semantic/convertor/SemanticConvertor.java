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

package com.antgroup.openspg.core.spgschema.service.semantic.convertor;

import com.antgroup.openspg.core.spgschema.model.SchemaException;
import com.antgroup.openspg.core.spgschema.model.predicate.PropertyRef;
import com.antgroup.openspg.core.spgschema.model.semantic.PredicateSemantic;
import com.antgroup.openspg.core.spgschema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.spgschema.service.semantic.model.SimpleSemantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class SemanticConvertor {

    public static SimpleSemantic toSimpleSemantic(PredicateSemantic semantic) {
        return new SimpleSemantic(
            semantic.getOntologyType(),
            semantic.getSubjectTypeRef().getUniqueId().toString(),
            semantic.getObjectTypeRef().getUniqueId().toString(),
            semantic.getPredicateIdentifier());
    }

    public static List<PredicateSemantic> toPredicateSemantic(
        List<SimpleSemantic> semantics,
        List<PropertyRef> propertyRefs,
        SPGOntologyEnum ontologyEnum) {
        Map<Long, PropertyRef> propertyMap = propertyRefs.stream()
            .collect(Collectors.toMap(PropertyRef::getUniqueId, Function.identity()));

        List<PredicateSemantic> predicateSemantics = new ArrayList<>();
        for (SimpleSemantic semantic : semantics) {
            Long subjectId = Long.parseLong(semantic.getSubjectId());
            PropertyRef subjectTypeRef = propertyMap.get(subjectId);
            if (null == subjectTypeRef) {
                throw SchemaException.uniqueIdNotExist(subjectId);
            }

            Long objectId = Long.parseLong(semantic.getObjectId());
            PropertyRef objectTypeRef = propertyMap.get(objectId);
            if (null == objectTypeRef) {
                throw SchemaException.uniqueIdNotExist(objectId);
            }

            PredicateSemantic predicateSemantic = new PredicateSemantic(subjectTypeRef,
                semantic.getPredicateIdentifier(), objectTypeRef);
            predicateSemantic.setOntologyType(ontologyEnum);
            predicateSemantics.add(predicateSemantic);
        }
        return predicateSemantics;
    }
}
