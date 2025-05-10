package com.antgroup.openspgapp.common.service.project.covertor;

import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspgapp.common.model.project.dto.ProjectDTO;

/* loaded from: com.antgroup.openspgapp-common-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/service/project/covertor/ProjectConvertor.class */
public class ProjectConvertor {
  public static ProjectDTO toProjectDTO(Project project) {
    ProjectDTO projectDTO = new ProjectDTO();
    projectDTO.setId(project.getId());
    projectDTO.setName(project.getName());
    projectDTO.setDescription(project.getDescription());
    projectDTO.setNamespace(project.getNamespace());
    projectDTO.setTenantId(project.getTenantId());
    projectDTO.setConfig(project.getConfig());
    return projectDTO;
  }
}
