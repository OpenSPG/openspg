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

package com.antgroup.openspg.server.infra.dao.repository.common;

import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.enums.VisibilityEnum;
import com.antgroup.openspg.server.api.facade.dto.common.request.ProjectQueryRequest;
import com.antgroup.openspg.server.common.model.exception.ProjectException;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.common.service.project.ProjectRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.ProjectDO;
import com.antgroup.openspg.server.infra.dao.dataobject.ProjectDOExample;
import com.antgroup.openspg.server.infra.dao.mapper.ProjectDOMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.ProjectConvertor;
import com.antgroup.openspg.server.infra.dao.repository.schema.enums.ValidStatusEnum;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ProjectRepositoryImpl implements ProjectRepository {

  @Autowired private ProjectDOMapper projectDOMapper;

  @Override
  public Long save(Project project) {
    List<Project> existProjects1 = query(new ProjectQueryRequest().setName(project.getName()));
    if (CollectionUtils.isNotEmpty(existProjects1)) {
      throw ProjectException.projectNameAlreadyExist(project.getName());
    }

    List<Project> existProjects2 =
        query(new ProjectQueryRequest().setNamespace(project.getNamespace()));
    if (CollectionUtils.isNotEmpty(existProjects2)) {
      throw ProjectException.namespaceAlreadyExist(project.getNamespace());
    }
    ProjectDO projectDO = ProjectConvertor.toDO(project);
    projectDO.setGmtModified(new Date());
    projectDO.setGmtCreate(new Date());
    if (StringUtils.isBlank(projectDO.getVisibility())) {
      projectDO.setVisibility(VisibilityEnum.PRIVATE.name());
    }
    projectDOMapper.insert(projectDO);
    return projectDO.getId();
  }

  @Override
  public Project update(Project project) {
    ProjectDO projectDO = ProjectConvertor.toDO(project);
    projectDOMapper.updateByPrimaryKeySelective(projectDO);
    return project;
  }

  @Override
  public Project queryById(Long projectId) {
    ProjectDO projectDO = projectDOMapper.selectByPrimaryKey(projectId);
    return ProjectConvertor.toModel(projectDO);
  }

  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public Integer deleteById(Long projectId) {
    ProjectDO projectDO = projectDOMapper.selectByPrimaryKey(projectId);
    projectDOMapper.deleteFromKgOntologyEntity(projectDO.getNamespace());
    projectDOMapper.deleteFromKgOntologyEntityPropertyRange(projectId);
    projectDOMapper.deleteFromKgProjectEntity(projectId);
    projectDOMapper.deleteFromKgOntologyRelease(projectId);
    projectDOMapper.deleteFromKgReasonSession(projectId);
    projectDOMapper.deleteFromKgReasonTask(projectId);
    projectDOMapper.deleteFromKgReasonTutorial(projectId);
    projectDOMapper.deleteFromKgBuilderJob(projectId);
    projectDOMapper.deleteFromKgResourcePermission(projectId);
    return projectDOMapper.deleteByPrimaryKey(projectId);
  }

  @Override
  public List<Project> query(ProjectQueryRequest request) {
    ProjectDOExample example = new ProjectDOExample();

    ProjectDOExample.Criteria criteria = example.createCriteria();
    if (request.getTenantId() != null) {
      criteria.andBizDomainIdEqualTo(request.getTenantId());
    }
    if (request.getProjectId() != null) {
      criteria.andIdEqualTo(request.getProjectId());
    }
    if (request.getName() != null) {
      criteria.andNameEqualTo(request.getName());
    }
    if (request.getNamespace() != null) {
      criteria.andNamespaceEqualTo(request.getNamespace());
    }
    if (CollectionUtils.isNotEmpty(request.getProjectIdList())) {
      criteria.andIdIn(request.getProjectIdList());
    }

    List<ProjectDO> projectDOS = projectDOMapper.selectByExample(example);
    return CollectionsUtils.listMap(projectDOS, ProjectConvertor::toModel);
  }

  @Override
  public List<Project> queryPageData(ProjectQueryRequest query, int start, int size) {
    int startIndex = (Math.max(start, 1) - 1) * size;
    return projectDOMapper
        .selectByCondition(query, query.getOrderByGmtCreateDesc(), startIndex, size).stream()
        .map(ProjectConvertor::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public Long queryPageCount(ProjectQueryRequest query) {
    return projectDOMapper.selectCountByCondition(query, query.getOrderByGmtCreateDesc());
  }

  @Override
  public Project queryByNamespace(String namespace) {
    ProjectDOExample example = new ProjectDOExample();
    ProjectDOExample.Criteria criteria = example.createCriteria();
    criteria.andNamespaceEqualTo(namespace);
    criteria.andStatusEqualTo(ValidStatusEnum.VALID.name());
    List<ProjectDO> projectDOS = projectDOMapper.selectByExample(example);
    if (CollectionUtils.isEmpty(projectDOS)) {
      return null;
    }
    ProjectDO projectDO = projectDOS.get(0);
    return ProjectConvertor.toModel(projectDO);
  }
}
