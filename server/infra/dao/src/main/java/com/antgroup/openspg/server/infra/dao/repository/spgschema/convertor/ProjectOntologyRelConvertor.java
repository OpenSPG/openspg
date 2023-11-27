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

package com.antgroup.openspg.server.infra.dao.repository.spgschema.convertor;

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.ProjectOntologyRelDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.enums.ProjectEntityTypeEnum;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.enums.YesOrNoEnum;
import com.antgroup.openspg.core.spgschema.model.alter.AlterStatusEnum;
import com.antgroup.openspg.core.spgschema.model.type.RefSourceEnum;
import com.antgroup.openspg.core.spgschema.service.type.model.ProjectOntologyRel;
import java.util.Date;

public class ProjectOntologyRelConvertor {

  public static ProjectOntologyRelDO toDO(ProjectOntologyRel projectOntology) {
    if (null == projectOntology) {
      return null;
    }

    ProjectOntologyRelDO projectEntityDO = new ProjectOntologyRelDO();
    projectEntityDO.setId(projectOntology.getId());
    projectEntityDO.setGmtCreate(new Date());
    projectEntityDO.setGmtModified(new Date());
    projectEntityDO.setProjectId(projectOntology.getProjectId());
    projectEntityDO.setEntityId(projectOntology.getResourceId());
    projectEntityDO.setType(ProjectEntityTypeEnum.getType(projectOntology.getOntologyTypeEnum()));
    projectEntityDO.setReferenced(
        projectOntology.getRefSourceEnum() == null ? YesOrNoEnum.N.name() : YesOrNoEnum.Y.name());
    projectEntityDO.setRefSource(
        projectOntology.getRefSourceEnum() == null
            ? null
            : projectOntology.getRefSourceEnum().name());
    projectEntityDO.setVersion(projectOntology.getAlterVersion());
    projectEntityDO.setVersionStatus(projectOntology.getAlterStatus().name());
    return projectEntityDO;
  }

  public static ProjectOntologyRel toModel(ProjectOntologyRelDO projectOntologyRelDO) {
    if (null == projectOntologyRelDO) {
      return null;
    }

    return new ProjectOntologyRel(
        projectOntologyRelDO.getId(),
        projectOntologyRelDO.getProjectId(),
        projectOntologyRelDO.getEntityId(),
        ProjectEntityTypeEnum.getOntologyType(projectOntologyRelDO.getType()),
        projectOntologyRelDO.getVersion(),
        AlterStatusEnum.toEnum(projectOntologyRelDO.getVersionStatus()),
        getRefSource(projectOntologyRelDO.getReferenced(), projectOntologyRelDO.getRefSource()));
  }

  private static RefSourceEnum getRefSource(String referenced, String refSource) {
    boolean ref = YesOrNoEnum.isYes(referenced);
    if (!ref) {
      return null;
    }
    if (null == refSource) {
      return RefSourceEnum.PROJECT;
    }
    return RefSourceEnum.toEnum(refSource);
  }
}
