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

package com.antgroup.openspg.biz.common.impl;

import com.antgroup.openspg.api.facade.dto.common.request.ProjectCreateRequest;
import com.antgroup.openspg.api.facade.dto.common.request.ProjectQueryRequest;
import com.antgroup.openspg.biz.common.ProjectManager;
import com.antgroup.openspg.common.model.project.Project;
import com.antgroup.openspg.common.service.project.ProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProjectManagerImpl implements ProjectManager {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public Project create(ProjectCreateRequest request) {
        Project project = new Project(
            null,
            request.getName(),
            request.getDesc(),
            request.getNamespace(),
            request.getTenantId());
        Long projectId = projectRepository.save(project);
        project.setId(projectId);
        return project;
    }

    @Override
    public List<Project> query(ProjectQueryRequest request) {
        return projectRepository.query(request);
    }
}
