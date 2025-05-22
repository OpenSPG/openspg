package com.antgroup.openspgapp.infra.dao.dataobject;

import java.util.Date;

/* loaded from: com.antgroup.openspgapp-infra-dao-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/infra/dao/dataobject/ReasonSessionDO.class */
public class ReasonSessionDO {
  private Long id;
  private Long projectId;
  private Long userId;
  private String name;
  private Date gmtCreate;
  private Date gmtModified;
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
    this.name = name == null ? null : name.trim();
  }

  public Date getGmtCreate() {
    return this.gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public Date getGmtModified() {
    return this.gmtModified;
  }

  public void setGmtModified(Date gmtModified) {
    this.gmtModified = gmtModified;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description == null ? null : description.trim();
  }
}
