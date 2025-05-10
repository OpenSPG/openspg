package com.antgroup.openspgapp.server.api.facade.dto.reasoner.session;

import com.antgroup.openspg.server.common.model.base.BaseResponse;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/reasoner/session/SessionResponse.class */
public class SessionResponse extends BaseResponse {
  private static final long serialVersionUID = 7549125593003891395L;
  private Long id;
  private Long projectId;
  private Long userId;
  private String name;
  private String description;

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
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
