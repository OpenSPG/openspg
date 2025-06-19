package com.antgroup.openspgapp.server.api.facade.dto.reasoner.session;

import com.antgroup.openspg.server.common.model.base.BaseRequest;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/reasoner/session/ListSessionRequest.class */
public class ListSessionRequest extends BaseRequest {
  private static final long serialVersionUID = 2793740486506426124L;
  private Long projectId;
  private Long userId;
  private Integer limit;

  public ListSessionRequest() {}

  public ListSessionRequest(Long projectId, Long userId, Integer limit) {
    this.projectId = projectId;
    this.userId = userId;
    this.limit = limit;
  }

  public Long getProjectId() {
    return this.projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public Long getUserId() {
    return this.userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Integer getLimit() {
    return this.limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }
}
