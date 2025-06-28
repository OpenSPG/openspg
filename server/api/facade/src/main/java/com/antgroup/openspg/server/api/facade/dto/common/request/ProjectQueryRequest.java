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

package com.antgroup.openspg.server.api.facade.dto.common.request;

import com.antgroup.openspg.server.common.model.base.BaseRequest;
import java.util.List;

public class ProjectQueryRequest extends BaseRequest {

  private Long id;

  private Long tenantId;

  private Long projectId;

  private String name;

  private String namespace;

  private String config;

  private Boolean orderByGmtCreateDesc;

  private List<Long> projectIdList;

  private Boolean isOwner;

  private String visibility;

  public Long getTenantId() {
    return tenantId;
  }

  public Boolean getOwner() {
    return isOwner;
  }

  public void setOwner(Boolean owner) {
    isOwner = owner;
  }

  public ProjectQueryRequest setTenantId(Long tenantId) {
    this.tenantId = tenantId;
    return this;
  }

  public Long getProjectId() {
    return projectId;
  }

  public ProjectQueryRequest setProjectId(Long projectId) {
    this.projectId = projectId;
    return this;
  }

  public String getName() {
    return name;
  }

  public ProjectQueryRequest setName(String name) {
    this.name = name;
    return this;
  }

  public String getNamespace() {
    return namespace;
  }

  public ProjectQueryRequest setNamespace(String namespace) {
    this.namespace = namespace;
    return this;
  }

  public Boolean getOrderByGmtCreateDesc() {
    return orderByGmtCreateDesc;
  }

  public void setOrderByGmtCreateDesc(Boolean orderByGmtCreateDesc) {
    this.orderByGmtCreateDesc = orderByGmtCreateDesc;
  }

  public List<Long> getProjectIdList() {
    return projectIdList;
  }

  public void setProjectIdList(List<Long> projectIdList) {
    this.projectIdList = projectIdList;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getConfig() {
    return config;
  }

  public void setConfig(String config) {
    this.config = config;
  }

  public String getVisibility() {
    return visibility;
  }

  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }
}
