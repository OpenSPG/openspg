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

package com.antgroup.openspg.core.spgschema.service.predicate.impl;

import com.antgroup.openspg.core.spgschema.model.constraint.Constraint;
import com.antgroup.openspg.core.spgschema.model.predicate.PropertyRef;
import com.antgroup.openspg.core.spgschema.model.predicate.SubProperty;
import com.antgroup.openspg.core.spgschema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.spgschema.service.predicate.SubPropertyService;
import com.antgroup.openspg.core.spgschema.service.predicate.convertor.PredicateAssemble;
import com.antgroup.openspg.core.spgschema.service.predicate.convertor.PredicateConvertor;
import com.antgroup.openspg.core.spgschema.service.predicate.model.SimpleSubProperty;
import com.antgroup.openspg.core.spgschema.service.predicate.repository.ConstraintRepository;
import com.antgroup.openspg.core.spgschema.service.predicate.repository.PropertyRepository;
import com.antgroup.openspg.core.spgschema.service.predicate.repository.SubPropertyRepository;
import com.antgroup.openspg.core.spgschema.service.type.model.SimpleSPGType;
import com.antgroup.openspg.core.spgschema.service.type.repository.SPGTypeRepository;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SubPropertyServiceImpl implements SubPropertyService {

    @Autowired
    private SubPropertyRepository subPropertyRepository;
    @Autowired
    private ConstraintRepository constraintRepository;
    @Autowired
    private SPGTypeRepository spgTypeRepository;
    @Autowired
    private PropertyRepository propertyRepository;

    @Override
    public int create(SubProperty subProperty) {
        this.saveOrUpdateConstraint(subProperty);

        int cnt = subPropertyRepository.save(PredicateConvertor.toSimpleSubProperty(subProperty));
        log.info("sub property: {} is saved", subProperty.getName());
        return cnt;
    }

    @Override
    public int update(SubProperty subProperty) {
        if (subProperty.getConstraint() == null) {
            SimpleSubProperty simpleSubProperty = subPropertyRepository.queryByUniqueId(subProperty.getUniqueId());
            if (null != simpleSubProperty && simpleSubProperty.getConstraintId() != null) {
                constraintRepository.deleteById(simpleSubProperty.getConstraintId());
            }
        } else {
            this.saveOrUpdateConstraint(subProperty);
        }

        int cnt = subPropertyRepository.update(PredicateConvertor.toSimpleSubProperty(subProperty));
        log.info("sub property: {} is updated", subProperty.getName());
        return cnt;
    }

    @Override
    public int delete(SubProperty subProperty) {
        if (subProperty.getConstraint() != null) {
            constraintRepository.deleteById(Lists.newArrayList(
                subProperty.getConstraint().getId()));
        }

        int cnt = subPropertyRepository.delete(PredicateConvertor.toSimpleSubProperty(subProperty));
        log.info("sub property: {} is delete", subProperty.getName());
        return cnt;
    }

    @Override
    public List<SubProperty> queryBySubjectId(List<Long> subjectIds, SPGOntologyEnum ontologyEnum) {
        if (CollectionUtils.isEmpty(subjectIds)) {
            return Collections.emptyList();
        }

        List<SimpleSubProperty> simpleSubProperties = subPropertyRepository.queryBySubjectId(
            subjectIds,
            ontologyEnum);
        if (CollectionUtils.isEmpty(simpleSubProperties)) {
            return Collections.emptyList();
        }

        List<Long> constraintIds = simpleSubProperties.stream().map(SimpleSubProperty::getConstraintId)
            .filter(e -> e != null && e > 0).collect(Collectors.toList());
        List<Constraint> constraints = constraintRepository.queryById(constraintIds);

        List<PropertyRef> propertyRefs = propertyRepository.queryRefByUniqueId(subjectIds, ontologyEnum);

        Set<Long> objectIds = simpleSubProperties.stream()
            .map(e -> e.getObjectId().getUniqueId()).collect(Collectors.toSet());
        List<SimpleSPGType> objectTypes = spgTypeRepository.queryByUniqueId(Lists.newArrayList(objectIds));

        return PredicateAssemble.toSubProperty(simpleSubProperties, propertyRefs, objectTypes, constraints);
    }

    private void saveOrUpdateConstraint(SubProperty subProperty) {
        Constraint constraint = subProperty.getConstraint();
        if (constraint != null) {
            constraintRepository.upsert(constraint);
            log.info("constraint of property: {} is saved", subProperty.getName());
        }
    }
}
