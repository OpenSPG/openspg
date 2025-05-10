package com.antgroup.openspgapp.common.service.project.impl;

import com.alipay.sofa.common.utils.StringUtil;
import com.antgroup.openspg.common.util.enums.ResourceTagEnum;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.api.facade.dto.common.request.PermissionRequest;
import com.antgroup.openspg.server.api.facade.dto.common.request.ProjectQueryRequest;
import com.antgroup.openspg.server.biz.common.PermissionManager;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspgapp.common.model.project.dto.ProjectDTO;
import com.antgroup.openspgapp.common.service.project.AppProjectManager;
import com.antgroup.openspgapp.common.service.project.covertor.ProjectConvertor;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
/* loaded from: com.antgroup.openspgapp-common-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/service/project/impl/AppProjectManagerImpl.class */
public class AppProjectManagerImpl implements AppProjectManager {

  @Autowired private ProjectManager projectManager;

  @Autowired private PermissionManager permissionManager;

  @Override // com.antgroup.openspgapp.common.service.project.AppProjectManager
  public Paged<ProjectDTO> getProjectList(
      Boolean all,
      Long tenantId,
      String keyword,
      List<Long> projectIdList,
      Integer page,
      Integer size,
      String sort,
      String sortBy) {
    ProjectQueryRequest request = new ProjectQueryRequest();
    request.setTenantId(tenantId);
    request.setName(keyword);
    request.setProjectIdList(projectIdList);
    boolean desc = StringUtil.equalsIgnoreCase("desc", sort);
    request.setOrderByGmtCreateDesc(Boolean.valueOf(desc));
    Paged<Project> paged =
        this.projectManager.queryPaged(request, page.intValue(), size.intValue());
    Paged<ProjectDTO> result = new Paged<>();
    if (CollectionUtils.isNotEmpty(paged.getResults())) {
      List<ProjectDTO> list =
          (List)
              paged.getResults().stream()
                  .map(ProjectConvertor::toProjectDTO)
                  .collect(Collectors.toList());
      result.setResults(list);
    }
    result.setPageIdx(paged.getPageIdx());
    result.setPageSize(paged.getPageSize());
    result.setTotal(paged.getTotal());
    return result;
  }

  @Override // com.antgroup.openspgapp.common.service.project.AppProjectManager
  public boolean deleteProject(Long projectId) {
    Integer data = this.projectManager.deleteById(projectId);
    if (data != null && data.intValue() > 0) {
      PermissionRequest request = new PermissionRequest();
      request.setResourceTag(ResourceTagEnum.PROJECT.name());
      request.setResourceIds(Lists.newArrayList(new Long[] {projectId}));
      Integer i = this.permissionManager.removePermission(request);
      return i != null && i.intValue() > 0;
    }
    return false;
  }
}
