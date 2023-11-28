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

import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.server.common.service.SequenceRepository;
import com.antgroup.openspg.schema.model.alter.AlterStatusEnum;
import com.antgroup.openspg.schema.model.type.ParentTypeInfo;
import com.antgroup.openspg.server.infra.dao.dataobject.OntologyDOExample;
import com.antgroup.openspg.server.infra.dao.dataobject.OntologyDOWithBLOBs;
import com.antgroup.openspg.server.infra.dao.dataobject.OntologyParentRelDO;
import com.antgroup.openspg.server.infra.dao.dataobject.OntologyParentRelDOExample;
import com.antgroup.openspg.server.infra.dao.mapper.OntologyDOMapper;
import com.antgroup.openspg.server.infra.dao.mapper.OntologyParentRelDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.spgschema.convertor.OntologyParentRelConvertor;
import com.antgroup.openspg.server.infra.dao.repository.spgschema.enums.ValidStatusEnum;
import com.antgroup.openspg.server.schema.core.service.type.repository.OntologyParentRelRepository;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OntologyParentRelRepositoryImpl implements OntologyParentRelRepository {

  @Autowired private OntologyDOMapper ontologyDOMapper;
  @Autowired private OntologyParentRelDOMapper ontologyParentRelDOMapper;
  @Autowired private SequenceRepository sequenceRepository;

  @Override
  public ParentTypeInfo query(Long uniqueId) {
    List<ParentTypeInfo> parentTypeInfos = this.query(Lists.newArrayList(uniqueId));
    return CollectionUtils.isEmpty(parentTypeInfos) ? null : parentTypeInfos.get(0);
  }

  @Override
  public int save(ParentTypeInfo inheritInfo) {
    OntologyParentRelDO ontologyParentRelDO = OntologyParentRelConvertor.toDO(inheritInfo);
    ontologyParentRelDO.setId(sequenceRepository.getSeqIdByTime());
    return ontologyParentRelDOMapper.insert(ontologyParentRelDO);
  }

  @Override
  public int delete(Long entityId) {
    OntologyParentRelDOExample example = new OntologyParentRelDOExample();
    example.createCriteria().andEntityIdEqualTo(entityId);
    return ontologyParentRelDOMapper.deleteByExample(example);
  }

  @Override
  public List<ParentTypeInfo> query(List<Long> uniqueIds) {
    OntologyParentRelDOExample example = new OntologyParentRelDOExample();
    example.createCriteria().andEntityIdIn(uniqueIds);
    List<OntologyParentRelDO> ontologyParentRelDOS =
        ontologyParentRelDOMapper.selectByExample(example);
    if (CollectionUtils.isEmpty(ontologyParentRelDOS)) {
      return new ArrayList<>(0);
    }

    List<Long> parentIds =
        CollectionsUtils.listMap(ontologyParentRelDOS, OntologyParentRelDO::getParentId);
    List<OntologyDOWithBLOBs> parentDOS = null;
    if (CollectionUtils.isNotEmpty(parentIds)) {
      OntologyDOExample example2 = new OntologyDOExample();
      example2
          .createCriteria()
          .andOriginalIdIn(parentIds)
          .andVersionStatusEqualTo(AlterStatusEnum.ONLINE.name())
          .andStatusEqualTo(ValidStatusEnum.VALID.getCode());
      parentDOS = ontologyDOMapper.selectByExampleWithBLOBs(example2);
    }
    return OntologyParentRelConvertor.toModel(ontologyParentRelDOS, parentDOS);
  }
}
