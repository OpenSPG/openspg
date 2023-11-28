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

package com.antgroup.openspg.common.model.tenant;

import com.antgroup.openspg.common.model.base.BaseModel;

/** Tenant usually stands for a department, or a team, data in different tenant is isolated. */
public class Tenant extends BaseModel {

  private static final long serialVersionUID = -2992994848416221391L;

  /** Unique id */
  private final Long id;

  /** The Chinese name of domain, usually is unique */
  private final String name;

  /** The description of domain */
  private final String description;

  public Tenant(Long id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
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
}
