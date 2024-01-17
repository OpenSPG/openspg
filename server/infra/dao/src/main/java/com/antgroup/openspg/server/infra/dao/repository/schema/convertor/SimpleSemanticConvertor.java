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

package com.antgroup.openspg.server.infra.dao.repository.schema.convertor;

import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.semantic.RuleCode;
import com.antgroup.openspg.core.schema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.server.core.schema.service.semantic.model.SimpleSemantic;
import com.antgroup.openspg.server.infra.dao.dataobject.SemanticDO;

public class SimpleSemanticConvertor {

  public static SemanticDO toDO(SimpleSemantic simpleSemantic) {
    SemanticDO semanticDO = new SemanticDO();
    semanticDO.setResourceId(simpleSemantic.getSubjectId());
    semanticDO.setSemanticType(simpleSemantic.getPredicateIdentifier().getName());
    semanticDO.setOriginalResourceId(simpleSemantic.getObjectId());
    semanticDO.setResourceType(simpleSemantic.getOntologyType().name());
    if (simpleSemantic.getSubjectTypeIdentifier() != null) {
      semanticDO.setSubjectMetaType(simpleSemantic.getSubjectTypeIdentifier().toString());
    }
    if (simpleSemantic.getObjectTypeIdentifier() != null) {
      semanticDO.setObjectMetaType(simpleSemantic.getObjectTypeIdentifier().toString());
    }
    if (simpleSemantic.getRuleCode() != null) {
      semanticDO.setRuleId(simpleSemantic.getRuleCode().getCode());
    }
    return semanticDO;
  }

  public static SimpleSemantic toSimpleSemantic(SemanticDO semanticDO) {
    SPGOntologyEnum ontologyEnum = SPGOntologyEnum.toEnum(semanticDO.getResourceType());
    String subjectId = semanticDO.getResourceId();
    String objectId = semanticDO.getOriginalResourceId();
    SimpleSemantic simpleSemantic =
        new SimpleSemantic(
            ontologyEnum,
            subjectId,
            objectId,
            new PredicateIdentifier(semanticDO.getSemanticType()));
    if (StringUtils.isNotBlank(semanticDO.getSubjectMetaType())) {
      simpleSemantic.setSubjectTypeIdentifier(
          SPGTypeIdentifier.parse(semanticDO.getSubjectMetaType()));
    }
    if (StringUtils.isNotBlank(semanticDO.getObjectMetaType())) {
      simpleSemantic.setObjectTypeIdentifier(
          SPGTypeIdentifier.parse(semanticDO.getObjectMetaType()));
    }
    if (StringUtils.isNotBlank(semanticDO.getRuleId())) {
      simpleSemantic.setRuleCode(new RuleCode(semanticDO.getRuleId()));
    }
    return simpleSemantic;
  }
}
