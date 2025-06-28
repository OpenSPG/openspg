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

package com.antgroup.openspg.server.common.model.project;

import com.antgroup.openspg.server.common.model.base.BaseModel;

/**
 * Namespace unit for department manager self schema, the schema elements such as entityType or
 * property between Project is isolated.
 */
public class Project extends BaseModel {

  private static final long serialVersionUID = -3046737313733029469L;

  /** Unique id */
  private Long id;

  /** English name */
  private final String name;

  /** Detail description */
  private final String description;

  /** The namespace that isolate entity from different project. */
  private final String namespace;

  /** The visibility of the project */
  private String visibility;

  /** The tenant id that project belong to. */
  private final Long tenantId;

  /** Base configuration for the project dimension. */
  private final String config;

  private String tag;

  public Project(
      Long id,
      String name,
      String description,
      String namespace,
      Long tenantId,
      String config,
      String tag) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.namespace = namespace;
    this.tenantId = tenantId;
    this.config = config;
    this.tag = tag;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Long getTenantId() {
    return tenantId;
  }

  public String getNamespace() {
    return namespace;
  }

  public String getConfig() {
    return config;
  }

  public String getVisibility() {
    return visibility;
  }

  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }
}
