/*
 * Copyright 2023 Ant Group CO., Ltd.
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

public class OntologyDO {
  private Long id;

  private Long originalId;

  private String name;

  private String nameZh;

  private String entityCategory;

  private String layer;

  private String description;

  private String descriptionZh;

  private String status;

  private String withIndex;

  private String scope;

  private Integer version;

  private String versionStatus;

  private Date gmtCreate;

  private Date gmtModified;

  private Long transformerId;

  private String uniqueName;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getOriginalId() {
    return originalId;
  }

  public void setOriginalId(Long originalId) {
    this.originalId = originalId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }

  public String getNameZh() {
    return nameZh;
  }

  public void setNameZh(String nameZh) {
    this.nameZh = nameZh == null ? null : nameZh.trim();
  }

  public String getEntityCategory() {
    return entityCategory;
  }

  public void setEntityCategory(String entityCategory) {
    this.entityCategory = entityCategory == null ? null : entityCategory.trim();
  }

  public String getLayer() {
    return layer;
  }

  public void setLayer(String layer) {
    this.layer = layer == null ? null : layer.trim();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description == null ? null : description.trim();
  }

  public String getDescriptionZh() {
    return descriptionZh;
  }

  public void setDescriptionZh(String descriptionZh) {
    this.descriptionZh = descriptionZh == null ? null : descriptionZh.trim();
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status == null ? null : status.trim();
  }

  public String getWithIndex() {
    return withIndex;
  }

  public void setWithIndex(String withIndex) {
    this.withIndex = withIndex == null ? null : withIndex.trim();
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope == null ? null : scope.trim();
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

  public Long getTransformerId() {
    return transformerId;
  }

  public void setTransformerId(Long transformerId) {
    this.transformerId = transformerId;
  }

  public String getUniqueName() {
    return uniqueName;
  }

  public void setUniqueName(String uniqueName) {
    this.uniqueName = uniqueName == null ? null : uniqueName.trim();
  }
}
