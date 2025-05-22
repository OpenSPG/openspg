package com.antgroup.openspgapp.core.reasoner.model;

import com.antgroup.openspg.server.common.model.base.BaseModel;

/* loaded from: core-reasoner-model-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/model/Session.class */
public class Session extends BaseModel {
  private static final long serialVersionUID = -2811168497796448605L;
  private Long id;
  private Long projectId;
  private Long userId;
  private String name;
  private String description;

  public Session(Long id, Long projectId, Long userId, String name, String description) {
    this.id = id;
    this.projectId = projectId;
    this.userId = userId;
    this.name = name;
    this.description = description;
  }

  public Long getId() {
    return this.id;
  }

  public Long getProjectId() {
    return this.projectId;
  }

  public Long getUserId() {
    return this.userId;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
