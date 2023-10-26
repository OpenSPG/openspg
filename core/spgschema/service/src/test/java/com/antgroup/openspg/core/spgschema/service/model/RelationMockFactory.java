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

import com.antgroup.openspg.core.spgschema.model.BasicInfo;
import com.antgroup.openspg.core.spgschema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.spgschema.model.predicate.PropertyAdvancedConfig;
import com.antgroup.openspg.core.spgschema.model.predicate.Relation;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeRef;

import com.google.common.collect.Lists;


public class RelationMockFactory {


    public static Relation mockRelation(String subjectName, Long subjectId, String objectName, Long objectId) {
        SPGTypeRef subjectTypeRef = SPGTypeMockFactory.mockSpgTypeRef(subjectName, subjectId);
        BasicInfo<PredicateIdentifier> basicInfo = new BasicInfo<>(
            new PredicateIdentifier("relate"), "相关实体", "desc");
        SPGTypeRef objectTypeRef = SPGTypeMockFactory.mockSpgTypeRef(objectName, objectId);

        PropertyAdvancedConfig advancedConfig = new PropertyAdvancedConfig();
        Relation relation = new Relation(
            basicInfo, subjectTypeRef, objectTypeRef, false, advancedConfig);

        advancedConfig.setSubProperties(SubPropertyMockFactory.mock(relation.toRef()));
        advancedConfig.setSemantics(Lists.newArrayList(
            PredicateSemanticMockFactory.mockInverseOfSemantic(subjectName, subjectId, objectName, objectId)));
        advancedConfig.setLogicalRule(LogicalRuleMockFactory.mockRelationLogicRule());
        return relation;
    }
}
