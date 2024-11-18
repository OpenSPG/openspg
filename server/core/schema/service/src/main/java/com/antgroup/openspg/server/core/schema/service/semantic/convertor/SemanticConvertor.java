/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.server.core.schema.service.semantic.convertor;

import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.SchemaException;
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.PropertyRef;
import com.antgroup.openspg.core.schema.model.semantic.PredicateSemantic;
import com.antgroup.openspg.core.schema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import com.antgroup.openspg.server.core.schema.service.semantic.model.SimpleSemantic;
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
    Map<Long, PropertyRef> propertyMap =
        propertyRefs.stream()
            .collect(Collectors.toMap(PropertyRef::getUniqueId, Function.identity()));

    // TODO: 2024/3/13 语义关系解析
    List<PredicateSemantic> predicateSemantics = new ArrayList<>();
    for (SimpleSemantic semantic : semantics) {
      Long subjectId = Long.parseLong(semantic.getSubjectId());
      PropertyRef subjectTypeRef = propertyMap.get(subjectId);
      if (null == subjectTypeRef) {
        throw SchemaException.uniqueIdNotExist(subjectId);
      }

      PropertyRef objectTypeRef = null;
      if (SPGOntologyEnum.CONCEPT.equals(ontologyEnum)) {
        objectTypeRef =
            new PropertyRef(
                subjectTypeRef.getSubjectTypeRef(),
                new BasicInfo<>(new PredicateIdentifier(semantic.getObjectId())),
                new SPGTypeRef(
                    new BasicInfo<>(SPGTypeIdentifier.parse(semantic.getObjectId())),
                    SPGTypeEnum.CONCEPT_TYPE),
                SPGOntologyEnum.CONCEPT);
      } else {
        Long objectId = Long.parseLong(semantic.getObjectId());
        objectTypeRef = propertyMap.get(objectId);
        if (null == objectTypeRef) {
          throw SchemaException.uniqueIdNotExist(objectId);
        }
      }

      PredicateSemantic predicateSemantic =
          new PredicateSemantic(subjectTypeRef, semantic.getPredicateIdentifier(), objectTypeRef);
      predicateSemantic.setOntologyType(ontologyEnum);

      predicateSemantic.setRuleCode(semantic.getRuleCode());
      predicateSemantics.add(predicateSemantic);
    }
    return predicateSemantics;
  }
}
