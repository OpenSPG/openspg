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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.common;

import com.antgroup.openspg.api.facade.dto.common.request.ProjectQueryRequest;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.ProjectDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.ProjectDOExample;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.mapper.ProjectDOMapper;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.common.convertor.ProjectConvertor;
import com.antgroup.openspg.common.model.exception.ProjectException;
import com.antgroup.openspg.common.model.project.Project;
import com.antgroup.openspg.common.service.project.ProjectRepository;
import com.antgroup.openspg.common.util.CollectionsUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class ProjectRepositoryImpl implements ProjectRepository {

    @Autowired
    private ProjectDOMapper projectDOMapper;

    @Override
    public Long save(Project project) {
        List<Project> existProjects1 = query(new ProjectQueryRequest().setName(project.getName()));
        if (CollectionUtils.isNotEmpty(existProjects1)) {
            throw ProjectException.projectNameAlreadyExist(project.getName());
        }

        List<Project> existProjects2 = query(new ProjectQueryRequest().setNamespace(project.getNamespace()));
        if (CollectionUtils.isNotEmpty(existProjects2)) {
            throw ProjectException.namespaceAlreadyExist(project.getNamespace());
        }
        ProjectDO projectDO = ProjectConvertor.toDO(project);
        projectDOMapper.insert(projectDO);
        return projectDO.getId();
    }

    @Override
    public Project queryById(Long projectId) {
        ProjectDO projectDO = projectDOMapper.selectByPrimaryKey(projectId);
        return ProjectConvertor.toModel(projectDO);
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

        List<ProjectDO> projectDOS = projectDOMapper.selectByExample(example);
        return CollectionsUtils.listMap(projectDOS, ProjectConvertor::toModel);
    }
}
