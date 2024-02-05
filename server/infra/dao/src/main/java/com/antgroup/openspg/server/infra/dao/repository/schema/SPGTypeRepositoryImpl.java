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

package com.antgroup.openspg.server.infra.dao.repository.schema;

import com.antgroup.openspg.core.schema.model.alter.AlterStatusEnum;
import com.antgroup.openspg.core.schema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.schema.model.type.ParentTypeInfo;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.server.core.schema.service.type.model.ProjectOntologyRel;
import com.antgroup.openspg.server.core.schema.service.type.model.SimpleSPGType;
import com.antgroup.openspg.server.core.schema.service.type.repository.OntologyParentRelRepository;
import com.antgroup.openspg.server.core.schema.service.type.repository.ProjectOntologyRelRepository;
import com.antgroup.openspg.server.core.schema.service.type.repository.SPGTypeRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.OntologyDOExample;
import com.antgroup.openspg.server.infra.dao.dataobject.OntologyDOWithBLOBs;
import com.antgroup.openspg.server.infra.dao.mapper.OntologyDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.schema.convertor.OntologyConvertor;
import com.antgroup.openspg.server.infra.dao.repository.schema.enums.EntityCategoryEnum;
import com.antgroup.openspg.server.infra.dao.repository.schema.enums.ValidStatusEnum;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SPGTypeRepositoryImpl implements SPGTypeRepository {

  @Autowired private OntologyDOMapper ontologyDOMapper;
  @Autowired private ProjectOntologyRelRepository projectOntologyRelRepository;
  @Autowired private OntologyParentRelRepository ontologyParentRelRepository;

  @Override
  public int save(SimpleSPGType simpleSpgType) {
    if (simpleSpgType.getParentTypeInfo() != null) {
      ParentTypeInfo inheritInfo = ontologyParentRelRepository.query(simpleSpgType.getUniqueId());
      if (inheritInfo == null) {
        ontologyParentRelRepository.save(simpleSpgType.getParentTypeInfo());
      }
    }

    if (!SPGTypeEnum.BASIC_TYPE.equals(simpleSpgType.getSpgTypeEnum())
        && !SPGTypeEnum.STANDARD_TYPE.equals(simpleSpgType.getSpgTypeEnum())) {
      ProjectOntologyRel projectOntology =
          new ProjectOntologyRel(
              null,
              simpleSpgType.getProjectId(),
              simpleSpgType.getUniqueId(),
              SPGOntologyEnum.TYPE,
              1,
              AlterStatusEnum.ONLINE,
              null);
      projectOntologyRelRepository.save(projectOntology);
    }

    OntologyDOWithBLOBs ontologyDO = OntologyConvertor.toNewDO(simpleSpgType);
    ontologyDO.setGmtCreate(new Date());
    ontologyDO.setGmtModified(new Date());
    return ontologyDOMapper.insert(ontologyDO);
  }

  @Override
  public int update(SimpleSPGType advancedType) {
    OntologyDOWithBLOBs ontologyDO = OntologyConvertor.toUpdateDO(advancedType);
    return ontologyDOMapper.updateByPrimaryKeySelective(ontologyDO);
  }

  @Override
  public int delete(SimpleSPGType advancedType) {
    ontologyParentRelRepository.delete(advancedType.getUniqueId());
    projectOntologyRelRepository.delete(advancedType.getUniqueId(), advancedType.getProjectId());

    OntologyDOExample example = new OntologyDOExample();
    example.createCriteria().andOriginalIdEqualTo(advancedType.getUniqueId());
    return ontologyDOMapper.deleteByExample(example);
  }

  @Override
  public List<SimpleSPGType> queryByProject(Long projectId) {
    List<ProjectOntologyRel> rels = projectOntologyRelRepository.queryByProjectId(projectId);
    if (CollectionUtils.isEmpty(rels)) {
      return Collections.emptyList();
    }

    List<Long> uniqueIds =
        rels.stream().map(ProjectOntologyRel::getResourceId).collect(Collectors.toList());
    List<OntologyDOWithBLOBs> ontologyEntityDOS = this.query(uniqueIds);
    List<ParentTypeInfo> parentTypeInfos = ontologyParentRelRepository.query(uniqueIds);
    return OntologyConvertor.toSpgType(ontologyEntityDOS, rels, parentTypeInfos);
  }

  @Override
  public List<SimpleSPGType> queryAllBasicType() {
    OntologyDOExample example = new OntologyDOExample();
    example
        .createCriteria()
        .andEntityCategoryEqualTo(EntityCategoryEnum.BASIC.name())
        .andVersionStatusEqualTo(AlterStatusEnum.ONLINE.name())
        .andStatusEqualTo(ValidStatusEnum.VALID.getCode());
    List<OntologyDOWithBLOBs> ontologyEntityDOS =
        ontologyDOMapper.selectByExampleWithBLOBs(example);
    return OntologyConvertor.toSpgType(
        ontologyEntityDOS, Collections.emptyList(), Collections.emptyList());
  }

  @Override
  public List<SimpleSPGType> queryAllStandardType() {
    OntologyDOExample example = new OntologyDOExample();
    example
        .createCriteria()
        .andEntityCategoryEqualTo(EntityCategoryEnum.STANDARD.name())
        .andVersionStatusEqualTo(AlterStatusEnum.ONLINE.name())
        .andStatusEqualTo(ValidStatusEnum.VALID.getCode());
    List<OntologyDOWithBLOBs> ontologyEntityDOS =
        ontologyDOMapper.selectByExampleWithBLOBs(example);
    List<Long> uniqueIds =
        ontologyEntityDOS.stream()
            .map(OntologyDOWithBLOBs::getOriginalId)
            .collect(Collectors.toList());
    List<ParentTypeInfo> parentTypeInfos = ontologyParentRelRepository.query(uniqueIds);
    return OntologyConvertor.toSpgType(ontologyEntityDOS, Collections.emptyList(), parentTypeInfos);
  }

  @Override
  public List<SimpleSPGType> queryByUniqueId(List<Long> uniqueIds) {
    if (CollectionUtils.isEmpty(uniqueIds)) {
      return Collections.emptyList();
    }

    List<OntologyDOWithBLOBs> ontologyDOS = this.query(uniqueIds);
    List<ParentTypeInfo> parentTypeInfos = ontologyParentRelRepository.query(uniqueIds);
    List<ProjectOntologyRel> rels =
        projectOntologyRelRepository.queryByOntologyId(uniqueIds, SPGOntologyEnum.TYPE);
    return OntologyConvertor.toSpgType(ontologyDOS, rels, parentTypeInfos);
  }

  @Override
  public SimpleSPGType queryByName(String uniqueName) {
    OntologyDOExample example = new OntologyDOExample();
    example
        .createCriteria()
        .andUniqueNameEqualTo(uniqueName)
        .andVersionStatusEqualTo(AlterStatusEnum.ONLINE.name())
        .andStatusEqualTo(ValidStatusEnum.VALID.getCode());

    List<OntologyDOWithBLOBs> ontologyDOS = ontologyDOMapper.selectByExampleWithBLOBs(example);
    if (CollectionUtils.isEmpty(ontologyDOS)) {
      return null;
    }
    OntologyDOWithBLOBs ontologyDO = ontologyDOS.get(0);
    ProjectOntologyRel projectOntologyRel =
        projectOntologyRelRepository.queryByOntologyId(
            ontologyDO.getOriginalId(), SPGOntologyEnum.TYPE);
    ParentTypeInfo parentTypeInfo = ontologyParentRelRepository.query(ontologyDO.getOriginalId());
    return OntologyConvertor.toSpgType(ontologyDO, projectOntologyRel, parentTypeInfo);
  }

  private List<OntologyDOWithBLOBs> query(List<Long> uniqueIds) {
    OntologyDOExample example = new OntologyDOExample();
    example
        .createCriteria()
        .andOriginalIdIn(uniqueIds)
        .andVersionStatusEqualTo(AlterStatusEnum.ONLINE.name())
        .andStatusEqualTo(ValidStatusEnum.VALID.getCode());
    return ontologyDOMapper.selectByExampleWithBLOBs(example);
  }
}
