/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.schema.service.type.model;

import com.antgroup.openspg.core.schema.model.type.ExtTypeEnum;

/**
 * @author xcj01388694
 * @version OntologyExt.java, v 0.1 2024年03月06日 上午11:03 xcj01388694
 */
public class OntologyExt {

  private Long id;

  private String resourceId;

  private String resourceType;

  private ExtTypeEnum extType;

  private String field;

  private String config;

  private Integer status;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  public ExtTypeEnum getExtType() {
    return extType;
  }

  public void setExtType(ExtTypeEnum extType) {
    this.extType = extType;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getConfig() {
    return config;
  }

  public void setConfig(String config) {
    this.config = config;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }
}
