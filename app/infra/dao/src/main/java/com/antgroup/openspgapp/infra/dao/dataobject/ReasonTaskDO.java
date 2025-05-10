package com.antgroup.openspgapp.infra.dao.dataobject;

import java.util.Date;

/* loaded from: com.antgroup.openspgapp-infra-dao-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/infra/dao/dataobject/ReasonTaskDO.class */
public class ReasonTaskDO {
  private Long id;
  private Long projectId;
  private Long userId;
  private Long sessionId;
  private Date gmtCreate;
  private Date gmtModified;
  private String mark;
  private String status;

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

  public Long getSessionId() {
    return this.sessionId;
  }

  public void setSessionId(Long sessionId) {
    this.sessionId = sessionId;
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

  public String getMark() {
    return this.mark;
  }

  public void setMark(String mark) {
    this.mark = mark == null ? null : mark.trim();
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(String status) {
    this.status = status == null ? null : status.trim();
  }
}
