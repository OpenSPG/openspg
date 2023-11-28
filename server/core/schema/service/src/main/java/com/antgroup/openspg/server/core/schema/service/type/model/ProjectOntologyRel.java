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

package com.antgroup.openspg.server.core.schema.service.type.model;

import com.antgroup.openspg.common.model.base.BaseValObj;
import com.antgroup.openspg.schema.model.alter.AlterStatusEnum;
import com.antgroup.openspg.schema.model.predicate.Relation;
import com.antgroup.openspg.schema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.schema.model.type.BaseSPGType;
import com.antgroup.openspg.schema.model.type.RefSourceEnum;

/**
 * The definition and reference relationship between project and {@link BaseSPGType} or {@link
 * Relation}
 */
public class ProjectOntologyRel extends BaseValObj {

  private static final long serialVersionUID = -5632074481392217864L;

  /** The unique id */
  private final Long id;

  /** The project id */
  private final Long projectId;

  /** The resource id, such as the unique id of entity type or relation type. */
  private final Long resourceId;

  /** The resource type, such as entity type or relation type. */
  private final SPGOntologyEnum ontologyEnum;

  /** The version id of current alternation. */
  private final Integer alterVersion;

  /** The status of current alternation. */
  private final AlterStatusEnum alterStatus;

  /** The place if the resource is referenced from other project. */
  private final RefSourceEnum refSourceEnum;

  public ProjectOntologyRel(
      Long id,
      Long projectId,
      Long resourceId,
      SPGOntologyEnum ontologyEnum,
      Integer alterVersion,
      AlterStatusEnum alterStatus,
      RefSourceEnum refSourceEnum) {
    this.id = id;
    this.projectId = projectId;
    this.resourceId = resourceId;
    this.ontologyEnum = ontologyEnum;
    this.alterVersion = alterVersion;
    this.alterStatus = alterStatus;
    this.refSourceEnum = refSourceEnum;
  }

  public Long getId() {
    return id;
  }

  public Long getProjectId() {
    return projectId;
  }

  public Long getResourceId() {
    return resourceId;
  }

  public SPGOntologyEnum getOntologyTypeEnum() {
    return ontologyEnum;
  }

  public Integer getAlterVersion() {
    return alterVersion;
  }

  public AlterStatusEnum getAlterStatus() {
    return alterStatus;
  }

  public RefSourceEnum getRefSourceEnum() {
    return refSourceEnum;
  }
}
