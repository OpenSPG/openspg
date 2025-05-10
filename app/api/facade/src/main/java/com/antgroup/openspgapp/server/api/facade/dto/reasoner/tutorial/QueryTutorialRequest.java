package com.antgroup.openspgapp.server.api.facade.dto.reasoner.tutorial;

import com.antgroup.openspg.server.common.model.base.BaseRequest;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/reasoner/tutorial/QueryTutorialRequest.class */
public class QueryTutorialRequest extends BaseRequest {
  private static final long serialVersionUID = 1297975150638327274L;
  private Long projectId;
  private String keyword;

  public Long getProjectId() {
    return this.projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public String getKeyword() {
    return this.keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }
}
