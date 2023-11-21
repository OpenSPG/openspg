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

package com.antgroup.openspg.core.spgschema.service.predicate.model;

import com.antgroup.openspg.core.spgschema.model.BaseSpoTriple;
import com.antgroup.openspg.core.spgschema.model.BasicInfo;
import com.antgroup.openspg.core.spgschema.model.OntologyId;
import com.antgroup.openspg.core.spgschema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.spgschema.model.predicate.EncryptTypeEnum;
import com.antgroup.openspg.core.spgschema.model.type.MultiVersionConfig;

/**
 * Domain model of simple sub property, contains the unique id of spo triple, corresponding to the
 * DO object in the database.
 */
public class SimpleSubProperty extends BaseSpoTriple {

  private static final long serialVersionUID = -3094375554984263917L;

  /** Basic information, such as name, nameZh, desc. */
  private final BasicInfo<PredicateIdentifier> basicInfo;

  /** Unique id of property or relation type. */
  private final OntologyId subjectId;

  /** Unique id of object type. */
  private final OntologyId objectId;

  /** The config of multi version */
  private final MultiVersionConfig multiVersionConfig;

  /** The encrypt type. */
  private final EncryptTypeEnum encryptTypeEnum;

  /** The id of constraint defined on property. */
  private final Long constraintId;

  /** If the sub property is defined on relation type */
  private final boolean fromRelation;

  public SimpleSubProperty(
      BasicInfo<PredicateIdentifier> basicInfo,
      OntologyId subjectId,
      OntologyId objectId,
      MultiVersionConfig multiVersionConfig,
      EncryptTypeEnum encryptTypeEnum,
      Long constraintId,
      boolean fromRelation) {
    this.basicInfo = basicInfo;
    this.subjectId = subjectId;
    this.objectId = objectId;
    this.multiVersionConfig = multiVersionConfig;
    this.encryptTypeEnum = encryptTypeEnum;
    this.constraintId = constraintId;
    this.fromRelation = fromRelation;
  }

  public BasicInfo<PredicateIdentifier> getBasicInfo() {
    return basicInfo;
  }

  public OntologyId getSubjectId() {
    return subjectId;
  }

  public OntologyId getObjectId() {
    return objectId;
  }

  public MultiVersionConfig getMultiVersionConfig() {
    return multiVersionConfig;
  }

  public EncryptTypeEnum getEncryptTypeEnum() {
    return encryptTypeEnum;
  }

  public Long getConstraintId() {
    return constraintId;
  }

  public boolean isFromRelation() {
    return fromRelation;
  }
}
