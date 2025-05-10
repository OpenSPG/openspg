package com.antgroup.openspgapp.infra.dao.dataobject;

import java.util.Date;

/* loaded from: com.antgroup.openspgapp-infra-dao-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/infra/dao/dataobject/ReasonTutorialDO.class */
public class ReasonTutorialDO {
  private Long id;
  private Long projectId;
  private Date gmtCreate;
  private Date gmtModified;
  private Boolean enable;
  private String name;

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

  public Boolean getEnable() {
    return this.enable;
  }

  public void setEnable(Boolean enable) {
    this.enable = enable;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }
}
