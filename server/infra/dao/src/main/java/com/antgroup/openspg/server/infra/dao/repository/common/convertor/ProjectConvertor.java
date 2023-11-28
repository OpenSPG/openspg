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

package com.antgroup.openspg.server.infra.dao.repository.common.convertor;

import com.antgroup.openspg.server.infra.dao.dataobject.ProjectDO;
import com.antgroup.openspg.common.model.project.Project;

public class ProjectConvertor {

  public static ProjectDO toDO(Project project) {
    ProjectDO projectInfoDO = new ProjectDO();

    projectInfoDO.setId(project.getId());
    projectInfoDO.setName(project.getName());
    projectInfoDO.setDescription(project.getDescription());
    projectInfoDO.setStatus("VALID");
    projectInfoDO.setBizDomainId(project.getTenantId());
    projectInfoDO.setNamespace(project.getNamespace());
    return projectInfoDO;
  }

  public static Project toModel(ProjectDO projectInfoDO) {
    if (null == projectInfoDO) {
      return null;
    }

    return new Project(
        projectInfoDO.getId(),
        projectInfoDO.getName(),
        projectInfoDO.getDescription(),
        projectInfoDO.getNamespace(),
        projectInfoDO.getBizDomainId());
  }
}
