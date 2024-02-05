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

public class OntologyParentRelDO {
  private Long id;

  private Long entityId;

  private Long parentId;

  private String status;

  private Date gmtCreate;

  private Date gmtModified;

  private String path;

  private String deepInherit;

  private String historyPath;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getEntityId() {
    return entityId;
  }

  public void setEntityId(Long entityId) {
    this.entityId = entityId;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status == null ? null : status.trim();
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

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path == null ? null : path.trim();
  }

  public String getDeepInherit() {
    return deepInherit;
  }

  public void setDeepInherit(String deepInherit) {
    this.deepInherit = deepInherit == null ? null : deepInherit.trim();
  }

  public String getHistoryPath() {
    return historyPath;
  }

  public void setHistoryPath(String historyPath) {
    this.historyPath = historyPath == null ? null : historyPath.trim();
  }
}
