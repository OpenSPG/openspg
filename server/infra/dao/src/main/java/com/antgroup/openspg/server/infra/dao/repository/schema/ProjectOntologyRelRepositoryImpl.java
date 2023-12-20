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

import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.core.schema.model.alter.AlterStatusEnum;
import com.antgroup.openspg.core.schema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.server.common.service.SequenceRepository;
import com.antgroup.openspg.server.core.schema.service.type.model.ProjectOntologyRel;
import com.antgroup.openspg.server.core.schema.service.type.repository.ProjectOntologyRelRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.ProjectOntologyRelDO;
import com.antgroup.openspg.server.infra.dao.dataobject.ProjectOntologyRelDOExample;
import com.antgroup.openspg.server.infra.dao.mapper.ProjectOntologyRelDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.schema.convertor.ProjectOntologyRelConvertor;
import com.antgroup.openspg.server.infra.dao.repository.schema.enums.ProjectEntityTypeEnum;
import com.antgroup.openspg.server.infra.dao.repository.schema.enums.YesOrNoEnum;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectOntologyRelRepositoryImpl implements ProjectOntologyRelRepository {

  @Autowired private ProjectOntologyRelDOMapper projectOntologyRelDOMapper;
  @Autowired private SequenceRepository sequenceRepository;

  @Override
  public int save(ProjectOntologyRel projectOntologyRel) {
    ProjectOntologyRelDO projectOntologyRelDO =
        ProjectOntologyRelConvertor.toDO(projectOntologyRel);
    projectOntologyRelDO.setId(sequenceRepository.getSeqIdByTime());
    return projectOntologyRelDOMapper.insert(projectOntologyRelDO);
  }

  @Override
  public int delete(Long uniqueId) {
    ProjectOntologyRelDOExample example = new ProjectOntologyRelDOExample();
    example
        .createCriteria()
        .andEntityIdEqualTo(uniqueId)
        .andTypeEqualTo(ProjectEntityTypeEnum.ENTITY_TYPE.name());
    return projectOntologyRelDOMapper.deleteByExample(example);
  }

  @Override
  public List<ProjectOntologyRel> queryByProjectId(Long projectId) {
    ProjectOntologyRelDOExample example = new ProjectOntologyRelDOExample();
    example
        .createCriteria()
        .andProjectIdEqualTo(projectId)
        .andReferencedEqualTo(YesOrNoEnum.N.name())
        .andVersionStatusEqualTo(AlterStatusEnum.ONLINE.name())
        .andTypeEqualTo(ProjectEntityTypeEnum.getType(SPGOntologyEnum.TYPE));
    List<ProjectOntologyRelDO> projectOntologyRelDOS =
        projectOntologyRelDOMapper.selectByExample(example);
    return CollectionsUtils.listMap(projectOntologyRelDOS, ProjectOntologyRelConvertor::toModel);
  }

  @Override
  public ProjectOntologyRel queryByOntologyId(Long uniqueId, SPGOntologyEnum ontologyEnum) {
    List<ProjectOntologyRel> rels =
        this.queryByOntologyId(Lists.newArrayList(uniqueId), ontologyEnum);
    return CollectionUtils.isEmpty(rels) ? null : rels.get(0);
  }

  @Override
  public List<ProjectOntologyRel> queryByOntologyId(
      List<Long> uniqueIds, SPGOntologyEnum ontologyEnum) {
    ProjectOntologyRelDOExample example = new ProjectOntologyRelDOExample();
    example
        .createCriteria()
        .andEntityIdIn(uniqueIds)
        .andTypeEqualTo(ProjectEntityTypeEnum.getType(ontologyEnum))
        .andReferencedEqualTo(YesOrNoEnum.N.name())
        .andVersionStatusEqualTo(AlterStatusEnum.ONLINE.name());
    List<ProjectOntologyRelDO> projectOntologyRelDOS =
        projectOntologyRelDOMapper.selectByExample(example);
    return CollectionsUtils.listMap(projectOntologyRelDOS, ProjectOntologyRelConvertor::toModel);
  }
}
