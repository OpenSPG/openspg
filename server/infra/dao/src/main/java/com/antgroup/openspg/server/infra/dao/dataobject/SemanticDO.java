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

public class SemanticDO {
  private Long id;

  private Date gmtCreate;

  private Date gmtModified;

  private String resourceId;

  private String semanticType;

  private String originalResourceId;

  private String resourceType;

  private Integer status;

  private String ruleId;

  private String subjectMetaType;

  private String objectMetaType;

  private String config;

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

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId == null ? null : resourceId.trim();
  }

  public String getSemanticType() {
    return semanticType;
  }

  public void setSemanticType(String semanticType) {
    this.semanticType = semanticType == null ? null : semanticType.trim();
  }

  public String getOriginalResourceId() {
    return originalResourceId;
  }

  public void setOriginalResourceId(String originalResourceId) {
    this.originalResourceId = originalResourceId == null ? null : originalResourceId.trim();
  }

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType == null ? null : resourceType.trim();
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getRuleId() {
    return ruleId;
  }

  public void setRuleId(String ruleId) {
    this.ruleId = ruleId == null ? null : ruleId.trim();
  }

  public String getSubjectMetaType() {
    return subjectMetaType;
  }

  public void setSubjectMetaType(String subjectMetaType) {
    this.subjectMetaType = subjectMetaType == null ? null : subjectMetaType.trim();
  }

  public String getObjectMetaType() {
    return objectMetaType;
  }

  public void setObjectMetaType(String objectMetaType) {
    this.objectMetaType = objectMetaType == null ? null : objectMetaType.trim();
  }

  public String getConfig() {
    return config;
  }

  public void setConfig(String config) {
    this.config = config == null ? null : config.trim();
  }
}
