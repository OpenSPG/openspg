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

package com.antgroup.openspg.server.infra.dao.repository.spgschema;

import com.antgroup.openspg.server.common.service.SequenceRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.SemanticDO;
import com.antgroup.openspg.server.infra.dao.dataobject.SemanticDOExample;
import com.antgroup.openspg.server.infra.dao.mapper.SemanticDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.spgschema.convertor.SimpleSemanticConvertor;
import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.schema.core.service.semantic.model.LogicalCausationQuery;
import com.antgroup.openspg.server.schema.core.service.semantic.model.SimpleSemantic;
import com.antgroup.openspg.server.schema.core.service.semantic.repository.SemanticRepository;
import com.antgroup.openspg.schema.model.semantic.LogicalCausationSemantic;
import com.antgroup.openspg.schema.model.semantic.SPGOntologyEnum;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SemanticRepositoryImpl implements SemanticRepository {

  @Autowired private SequenceRepository sequenceRepository;
  @Autowired private SemanticDOMapper semanticDOMapper;

  @Override
  public int saveOrUpdate(SimpleSemantic semantic) {
    SemanticDO semanticDO = SimpleSemanticConvertor.toDO(semantic);
    if (semanticDO.getId() == null) {
      semanticDO.setId(sequenceRepository.getSeqIdByTime());
      semanticDO.setGmtCreate(new Date());
      semanticDO.setGmtModified(new Date());
      semanticDO.setStatus(1);
      return semanticDOMapper.insert(semanticDO);
    } else {
      semanticDO.setGmtModified(new Date());

      SemanticDOExample example = new SemanticDOExample();
      example
          .createCriteria()
          .andResourceIdEqualTo(semantic.getSubjectId())
          .andSemanticTypeEqualTo(semantic.getPredicateIdentifier().getName())
          .andOriginalResourceIdEqualTo(semantic.getObjectId())
          .andResourceTypeEqualTo(semantic.getOntologyType().name());
      return semanticDOMapper.updateByExampleSelective(semanticDO, example);
    }
  }

  @Override
  public int deleteBySpo(
      String subjectId, String predicateName, String objectId, SPGOntologyEnum ontologyEnum) {
    SemanticDOExample example = new SemanticDOExample();
    SemanticDOExample.Criteria criteria = example.createCriteria();
    if (subjectId != null) {
      criteria.andResourceIdEqualTo(subjectId);
    }
    if (predicateName != null) {
      criteria.andSemanticTypeEqualTo(predicateName);
    }
    if (objectId != null) {
      criteria.andOriginalResourceIdEqualTo(objectId);
    }
    if (ontologyEnum != null) {
      criteria.andResourceTypeEqualTo(ontologyEnum.name());
    }
    return semanticDOMapper.deleteByExample(example);
  }

  @Override
  public int deleteByObject(
      String predicateName, String objectType, String objectId, SPGOntologyEnum ontologyEnum) {
    SemanticDOExample example = new SemanticDOExample();
    SemanticDOExample.Criteria criteria = example.createCriteria();
    if (predicateName != null) {
      criteria.andSemanticTypeEqualTo(predicateName);
    }
    if (objectType != null) {
      criteria.andObjectMetaTypeEqualTo(objectType);
    }
    if (objectId != null) {
      criteria.andOriginalResourceIdEqualTo(objectId);
    }
    if (ontologyEnum != null) {
      criteria.andResourceTypeEqualTo(ontologyEnum.name());
    }
    return semanticDOMapper.deleteByExample(example);
  }

  @Override
  public int deleteConceptSemantic(LogicalCausationSemantic conceptSemantic) {
    SemanticDOExample example = new SemanticDOExample();
    SemanticDOExample.Criteria criteria = example.createCriteria();
    if (conceptSemantic.getSubjectTypeIdentifier() != null) {
      criteria.andSubjectMetaTypeEqualTo(conceptSemantic.getSubjectTypeIdentifier().toString());
    }
    if (conceptSemantic.getSubjectIdentifier() != null) {
      criteria.andResourceIdEqualTo(conceptSemantic.getSubjectIdentifier().getId());
    }
    if (conceptSemantic.getPredicateIdentifier() != null) {
      criteria.andSemanticTypeEqualTo(conceptSemantic.getPredicateIdentifier().getName());
    }
    if (conceptSemantic.getObjectTypeIdentifier() != null) {
      criteria.andObjectMetaTypeEqualTo(conceptSemantic.getObjectTypeIdentifier().toString());
    }
    if (conceptSemantic.getObjectIdentifier() != null) {
      criteria.andOriginalResourceIdEqualTo(conceptSemantic.getObjectIdentifier().getId());
    }
    return semanticDOMapper.deleteByExample(example);
  }

  @Override
  public List<SimpleSemantic> queryConceptSemanticByCond(LogicalCausationQuery query) {
    SemanticDOExample example = new SemanticDOExample();
    SemanticDOExample.Criteria criteria = example.createCriteria();
    criteria.andResourceTypeEqualTo(SPGOntologyEnum.CONCEPT.name());

    if (CollectionUtils.isNotEmpty(query.getSubjectTypeNames())) {
      criteria.andSubjectMetaTypeIn(query.getSubjectTypeNames());
    }
    if (StringUtils.isNotBlank(query.getSubjectName())) {
      criteria.andResourceIdEqualTo(query.getSubjectName());
    }
    if (StringUtils.isNotBlank(query.getPredicateName())) {
      criteria.andSemanticTypeEqualTo(query.getPredicateName());
    }
    if (CollectionUtils.isNotEmpty(query.getObjectTypeNames())) {
      criteria.andObjectMetaTypeIn(query.getObjectTypeNames());
    }
    if (StringUtils.isNotBlank(query.getObjectName())) {
      criteria.andOriginalResourceIdEqualTo(query.getObjectName());
    }
    List<SemanticDO> semanticDOS = semanticDOMapper.selectByExampleWithBLOBs(example);
    return CollectionsUtils.listMap(semanticDOS, SimpleSemanticConvertor::toSimpleSemantic);
  }

  @Override
  public List<SimpleSemantic> queryBySubjectId(
      List<String> subjectIds, SPGOntologyEnum ontologyEnum) {
    SemanticDOExample example = new SemanticDOExample();
    example
        .createCriteria()
        .andResourceTypeEqualTo(ontologyEnum.name())
        .andResourceIdIn(subjectIds);
    List<SemanticDO> semanticDOS = semanticDOMapper.selectByExampleWithBLOBs(example);
    return CollectionsUtils.listMap(semanticDOS, SimpleSemanticConvertor::toSimpleSemantic);
  }
}
