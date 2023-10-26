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

package com.antgroup.openspg.core.spgschema.service.type.convertor;

import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.predicate.Relation;
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType;
import com.antgroup.openspg.core.spgschema.model.type.BasicType;
import com.antgroup.openspg.core.spgschema.model.type.ConceptType;
import com.antgroup.openspg.core.spgschema.model.type.EntityType;
import com.antgroup.openspg.core.spgschema.model.type.EventType;
import com.antgroup.openspg.core.spgschema.model.type.OperatorTypeEnum;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeAdvancedConfig;
import com.antgroup.openspg.core.spgschema.model.type.StandardType;
import com.antgroup.openspg.core.spgschema.service.type.model.OperatorConfig;
import com.antgroup.openspg.core.spgschema.service.type.model.SimpleSPGType;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The convertor between BaseSpgType and SimpleSpgType, provides method to convert BaseSpgType to SimpleSpgType, and
 * method to convert SimpleSpgType to BaseSpgType.
 */
public class SPGTypeConvertor {

    /**
     * convert BaseSpgType to SimpleSpgType.
     *
     * @param baseSpgType spg type to convert
     * @return convert result
     */
    public static SimpleSPGType toSimpleSpgType(BaseSPGType baseSpgType) {
        switch (baseSpgType.getSpgTypeEnum()) {
            case STANDARD_TYPE:
                StandardType standardType = (StandardType) baseSpgType;
                return new SimpleSPGType(
                    baseSpgType.getProjectId(),
                    baseSpgType.getOntologyId(),
                    baseSpgType.getAlterOperation(),
                    baseSpgType.getExtInfo(),
                    baseSpgType.getBasicInfo(),
                    baseSpgType.getParentTypeInfo(),
                    baseSpgType.getSpgTypeEnum(),
                    baseSpgType.getAdvancedConfig().getVisibleScope(),
                    getOperatorKey(baseSpgType.getAdvancedConfig()),
                    null,
                    null,
                    null,
                    standardType.getSpreadable(),
                    standardType.getConstraintItems());
            case CONCEPT_TYPE:
                ConceptType conceptType = (ConceptType) baseSpgType;
                return new SimpleSPGType(
                    baseSpgType.getProjectId(),
                    baseSpgType.getOntologyId(),
                    baseSpgType.getAlterOperation(),
                    baseSpgType.getExtInfo(),
                    baseSpgType.getBasicInfo(),
                    baseSpgType.getParentTypeInfo(),
                    baseSpgType.getSpgTypeEnum(),
                    baseSpgType.getAdvancedConfig().getVisibleScope(),
                    getOperatorKey(baseSpgType.getAdvancedConfig()),
                    conceptType.getConceptLayerConfig(),
                    conceptType.getConceptTaxonomicConfig(),
                    conceptType.getConceptMultiVersionConfig(),
                    null,
                    null);
            default:
                return new SimpleSPGType(
                    baseSpgType.getProjectId(),
                    baseSpgType.getOntologyId(),
                    baseSpgType.getAlterOperation(),
                    baseSpgType.getExtInfo(),
                    baseSpgType.getBasicInfo(),
                    baseSpgType.getParentTypeInfo(),
                    baseSpgType.getSpgTypeEnum(),
                    baseSpgType.getAdvancedConfig().getVisibleScope(),
                    getOperatorKey(baseSpgType.getAdvancedConfig())
                );
        }
    }

    private static List<OperatorConfig> getOperatorKey(SPGTypeAdvancedConfig advancedConfig) {
        List<OperatorConfig> operatorConfigs = new ArrayList<>();
        if (advancedConfig.getLinkOperator() != null) {
            operatorConfigs.add(new OperatorConfig(advancedConfig.getLinkOperator(), OperatorTypeEnum.ENTITY_LINK));
        }
        if (advancedConfig.getFuseOperator() != null) {
            operatorConfigs.add(new OperatorConfig(advancedConfig.getFuseOperator(), OperatorTypeEnum.ENTITY_FUSE));
        }
        if (advancedConfig.getExtractOperator() != null) {
            operatorConfigs.add(new OperatorConfig(
                advancedConfig.getExtractOperator(),
                OperatorTypeEnum.KNOWLEDGE_EXTRACT));
        }
        if (advancedConfig.getNormalizedOperator() != null) {
            operatorConfigs.add(new OperatorConfig(
                advancedConfig.getNormalizedOperator(),
                OperatorTypeEnum.PROPERTY_NORMALIZE));
        }
        return operatorConfigs;
    }

    /**
     * convert batch SimpleSpgType to batch BaseSpgType.
     *
     * @param simpleSpgTypes list of SimpleSpgType
     * @return list of BaseSpgType
     */
    public static List<BaseSPGType> toBaseSpgType(List<SimpleSPGType> simpleSpgTypes) {
        if (CollectionUtils.isEmpty(simpleSpgTypes)) {
            return new ArrayList<>();
        }
        return simpleSpgTypes.stream().map(e -> toBaseSpgType(e,
                new ArrayList<>(), new ArrayList<>()))
            .collect(Collectors.toList());
    }

    public static BaseSPGType toBaseSpgType(SimpleSPGType simpleSpgType) {
        return toBaseSpgType(simpleSpgType, new ArrayList<>(), new ArrayList<>());
    }

    public static BaseSPGType toBaseSpgType(
        SimpleSPGType simpleSpgType,
        List<Property> propertys,
        List<Relation> relations) {
        BaseSPGType baseSpgType;
        switch (simpleSpgType.getSpgTypeEnum()) {
            case BASIC_TYPE:
                baseSpgType = BasicType.from(simpleSpgType.getBasicInfo().getName().toString());
                break;
            case STANDARD_TYPE:
                baseSpgType = new StandardType(simpleSpgType.getBasicInfo(),
                    simpleSpgType.getParentTypeInfo(),
                    propertys, relations,
                    getAdvancedConfig(simpleSpgType),
                    simpleSpgType.getSpreadable(),
                    simpleSpgType.getConstraintItems());
                break;
            case ENTITY_TYPE:
                baseSpgType = new EntityType(simpleSpgType.getBasicInfo(),
                    simpleSpgType.getParentTypeInfo(),
                    propertys, relations,
                    getAdvancedConfig(simpleSpgType)
                );
                break;
            case CONCEPT_TYPE:
                baseSpgType = new ConceptType(simpleSpgType.getBasicInfo(),
                    simpleSpgType.getParentTypeInfo(),
                    propertys, relations,
                    getAdvancedConfig(simpleSpgType),
                    simpleSpgType.getConceptLayerConfig(),
                    simpleSpgType.getConceptTaxonomicConfig(),
                    simpleSpgType.getConceptMultiVersionConfig());
                break;
            case EVENT_TYPE:
                baseSpgType = new EventType(simpleSpgType.getBasicInfo(),
                    simpleSpgType.getParentTypeInfo(),
                    propertys, relations,
                    getAdvancedConfig(simpleSpgType)
                );
                break;
            default:
                throw new IllegalArgumentException("illegal type=" + simpleSpgType.getSpgTypeIdentifier());
        }

        baseSpgType.setProjectId(simpleSpgType.getProjectId());
        baseSpgType.setOntologyId(simpleSpgType.getOntologyId());
        baseSpgType.setAlterOperation(simpleSpgType.getAlterOperation());
        baseSpgType.setExtInfo(simpleSpgType.getExtInfo());
        return baseSpgType;
    }

    private static SPGTypeAdvancedConfig getAdvancedConfig(SimpleSPGType simpleSpgType) {
        SPGTypeAdvancedConfig advancedConfig = new SPGTypeAdvancedConfig();

        advancedConfig.setVisibleScope(simpleSpgType.getVisibleScope());
        if (CollectionUtils.isEmpty(simpleSpgType.getOperatorConfigs())) {
            return advancedConfig;
        }

        for (OperatorConfig operatorConfig : simpleSpgType.getOperatorConfigs()) {
            switch (operatorConfig.getOperatorType()) {
                case ENTITY_LINK:
                    advancedConfig.setLinkOperator(operatorConfig.getOperatorKey());
                    break;
                case ENTITY_FUSE:
                    advancedConfig.setFuseOperator(operatorConfig.getOperatorKey());
                    break;
                case PROPERTY_NORMALIZE:
                    advancedConfig.setNormalizedOperator(operatorConfig.getOperatorKey());
                    break;
                case KNOWLEDGE_EXTRACT:
                    advancedConfig.setExtractOperator(operatorConfig.getOperatorKey());
                    break;
                default:
                    break;
            }
        }
        return advancedConfig;
    }
}
