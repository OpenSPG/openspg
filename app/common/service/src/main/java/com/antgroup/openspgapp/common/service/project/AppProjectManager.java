package com.antgroup.openspgapp.common.service.project;

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspgapp.common.model.project.dto.ProjectDTO;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-common-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/service/project/AppProjectManager.class */
public interface AppProjectManager {
  Paged<ProjectDTO> getProjectList(
      Boolean all,
      Long tenantId,
      String keyword,
      List<Long> projectIdList,
      Integer page,
      Integer size,
      String sort,
      String sortBy);

  boolean deleteProject(Long projectId);
}
