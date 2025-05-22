package com.antgroup.openspgapp.server.api.facade.dto.reasoner.session;

import com.antgroup.openspg.server.common.model.base.BaseRequest;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/reasoner/session/CreateSessionRequest.class */
public class CreateSessionRequest extends BaseRequest {
  private static final long serialVersionUID = 288748585428580417L;
  private Long projectId;
  private Long userId;
  private String name;
  private String description;

  public CreateSessionRequest(Long projectId, Long userId, String name, String description) {
    this.projectId = projectId;
    this.userId = userId;
    this.name = name;
    this.description = description;
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

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
