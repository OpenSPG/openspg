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

package com.antgroup.openspg.server.core.schema.service.semantic.impl;

import com.antgroup.openspg.core.schema.model.predicate.PropertyRef;
import com.antgroup.openspg.core.schema.model.semantic.PredicateSemantic;
import com.antgroup.openspg.core.schema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.server.core.schema.service.predicate.repository.PropertyRepository;
import com.antgroup.openspg.server.core.schema.service.semantic.SemanticService;
import com.antgroup.openspg.server.core.schema.service.semantic.convertor.SemanticConvertor;
import com.antgroup.openspg.server.core.schema.service.semantic.model.SimpleSemantic;
import com.antgroup.openspg.server.core.schema.service.semantic.repository.SemanticRepository;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SemanticServiceImpl implements SemanticService {

  @Autowired private SemanticRepository semanticRepository;
  @Autowired private PropertyRepository propertyRepository;

  @Override
  public List<PredicateSemantic> queryBySubjectIds(
      List<Long> subjectIds, SPGOntologyEnum ontologyEnum) {
    if (CollectionUtils.isEmpty(subjectIds)) {
      return Collections.emptyList();
    }

    List<String> subjects = subjectIds.stream().map(Object::toString).collect(Collectors.toList());
    List<SimpleSemantic> semantics = semanticRepository.queryBySubjectId(subjects, ontologyEnum);
    if (CollectionUtils.isEmpty(semantics)) {
      return Collections.emptyList();
    }

    Set<Long> propertyIds = new HashSet<>();
    semantics.forEach(
        e -> {
          propertyIds.add(Long.parseLong(e.getSubjectId()));
          propertyIds.add(Long.parseLong(e.getObjectId()));
        });
    List<PropertyRef> propertyRefs =
        propertyRepository.queryRefByUniqueId(Lists.newArrayList(propertyIds), ontologyEnum);

    return SemanticConvertor.toPredicateSemantic(semantics, propertyRefs, ontologyEnum);
  }

  @Override
  public int saveOrUpdate(PredicateSemantic predicateSemantic) {
    return semanticRepository.saveOrUpdate(SemanticConvertor.toSimpleSemantic(predicateSemantic));
  }

  @Override
  public int delete(PredicateSemantic predicateSemantic) {
    return semanticRepository.deleteBySpo(
        predicateSemantic.getSubjectUniqueId().toString(),
        predicateSemantic.getPredicateIdentifier().getName(),
        predicateSemantic.getObjectUniqueId().toString(),
        predicateSemantic.getOntologyType());
  }
}
