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

public class OntologyPropertyDO {
  private Long id;

  private Long domainId;

  private String propertyName;

  private Long rangeId;

  private String propertyNameZh;

  private Long constraintId;

  private String propertyCategory;

  private String mapType;

  private Integer version;

  private String status;

  private Date gmtCreate;

  private Date gmtModified;

  private Long originalId;

  private String storePropertyName;

  private Long transformerId;

  private String propertyDesc;

  private String propertyDescZh;

  private Long projectId;

  private Long originalDomainId;

  private Long originalRangeId;

  private String versionStatus;

  private String relationSource;

  private String direction;

  private String maskType;

  private String multiverConfig;

  private Long propertySource;

  private String propertyConfig;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getDomainId() {
    return domainId;
  }

  public void setDomainId(Long domainId) {
    this.domainId = domainId;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName == null ? null : propertyName.trim();
  }

  public Long getRangeId() {
    return rangeId;
  }

  public void setRangeId(Long rangeId) {
    this.rangeId = rangeId;
  }

  public String getPropertyNameZh() {
    return propertyNameZh;
  }

  public void setPropertyNameZh(String propertyNameZh) {
    this.propertyNameZh = propertyNameZh == null ? null : propertyNameZh.trim();
  }

  public Long getConstraintId() {
    return constraintId;
  }

  public void setConstraintId(Long constraintId) {
    this.constraintId = constraintId;
  }

  public String getPropertyCategory() {
    return propertyCategory;
  }

  public void setPropertyCategory(String propertyCategory) {
    this.propertyCategory = propertyCategory == null ? null : propertyCategory.trim();
  }

  public String getMapType() {
    return mapType;
  }

  public void setMapType(String mapType) {
    this.mapType = mapType == null ? null : mapType.trim();
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
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

  public Long getOriginalId() {
    return originalId;
  }

  public void setOriginalId(Long originalId) {
    this.originalId = originalId;
  }

  public String getStorePropertyName() {
    return storePropertyName;
  }

  public void setStorePropertyName(String storePropertyName) {
    this.storePropertyName = storePropertyName == null ? null : storePropertyName.trim();
  }

  public Long getTransformerId() {
    return transformerId;
  }

  public void setTransformerId(Long transformerId) {
    this.transformerId = transformerId;
  }

  public String getPropertyDesc() {
    return propertyDesc;
  }

  public void setPropertyDesc(String propertyDesc) {
    this.propertyDesc = propertyDesc == null ? null : propertyDesc.trim();
  }

  public String getPropertyDescZh() {
    return propertyDescZh;
  }

  public void setPropertyDescZh(String propertyDescZh) {
    this.propertyDescZh = propertyDescZh == null ? null : propertyDescZh.trim();
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public Long getOriginalDomainId() {
    return originalDomainId;
  }

  public void setOriginalDomainId(Long originalDomainId) {
    this.originalDomainId = originalDomainId;
  }

  public Long getOriginalRangeId() {
    return originalRangeId;
  }

  public void setOriginalRangeId(Long originalRangeId) {
    this.originalRangeId = originalRangeId;
  }

  public String getVersionStatus() {
    return versionStatus;
  }

  public void setVersionStatus(String versionStatus) {
    this.versionStatus = versionStatus == null ? null : versionStatus.trim();
  }

  public String getRelationSource() {
    return relationSource;
  }

  public void setRelationSource(String relationSource) {
    this.relationSource = relationSource == null ? null : relationSource.trim();
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction == null ? null : direction.trim();
  }

  public String getMaskType() {
    return maskType;
  }

  public void setMaskType(String maskType) {
    this.maskType = maskType == null ? null : maskType.trim();
  }

  public String getMultiverConfig() {
    return multiverConfig;
  }

  public void setMultiverConfig(String multiverConfig) {
    this.multiverConfig = multiverConfig == null ? null : multiverConfig.trim();
  }

  public Long getPropertySource() {
    return propertySource;
  }

  public void setPropertySource(Long propertySource) {
    this.propertySource = propertySource;
  }

  public String getPropertyConfig() {
    return propertyConfig;
  }

  public void setPropertyConfig(String propertyConfig) {
    this.propertyConfig = propertyConfig == null ? null : propertyConfig.trim();
  }
}
