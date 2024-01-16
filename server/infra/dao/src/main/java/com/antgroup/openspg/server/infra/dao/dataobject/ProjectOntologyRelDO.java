/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.server.infra.dao.dataobject;

import java.util.Date;

public class ProjectOntologyRelDO {
  private Long id;

  private Date gmtCreate;

  private Date gmtModified;

  private Long projectId;

  private Long entityId;

  private Integer version;

  private String versionStatus;

  private String referenced;

  private String type;

  private String refSource;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public Date getGmtModified() {
    return gmtModified;
  }

  public void setGmtModified(Date gmtModified) {
    this.gmtModified = gmtModified;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public Long getEntityId() {
    return entityId;
  }

  public void setEntityId(Long entityId) {
    this.entityId = entityId;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public String getVersionStatus() {
    return versionStatus;
  }

  public void setVersionStatus(String versionStatus) {
    this.versionStatus = versionStatus == null ? null : versionStatus.trim();
  }

  public String getReferenced() {
    return referenced;
  }

  public void setReferenced(String referenced) {
    this.referenced = referenced == null ? null : referenced.trim();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type == null ? null : type.trim();
  }

  public String getRefSource() {
    return refSource;
  }

  public void setRefSource(String refSource) {
    this.refSource = refSource == null ? null : refSource.trim();
  }
}
