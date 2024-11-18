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

package com.antgroup.openspg.server.core.schema.service.predicate.model;

import com.antgroup.openspg.core.schema.model.BaseSpoTriple;
import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.OntologyId;
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.EncryptTypeEnum;
import com.antgroup.openspg.core.schema.model.predicate.IndexTypeEnum;
import com.antgroup.openspg.core.schema.model.predicate.MountedConceptConfig;
import com.antgroup.openspg.core.schema.model.predicate.PropertyGroupEnum;
import com.antgroup.openspg.core.schema.model.semantic.RuleCode;
import com.antgroup.openspg.core.schema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.schema.model.type.MultiVersionConfig;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;

/**
 * Domain model of simple property, contains the unique id of spo triple, corresponding to the DO
 * object in the database.
 */
public class SimpleProperty extends BaseSpoTriple {

  private static final long serialVersionUID = -3094375554984263917L;

  /** Basic information. */
  private final BasicInfo<PredicateIdentifier> basicInfo;

  /** Unique id of subject type. */
  private final OntologyId subjectTypeId;

  /** Unique id of object type. */
  private final OntologyId objectTypeId;

  /** Type of object. */
  private final SPGTypeEnum objectTypeEnum;

  /** The config of multi version */
  private final MultiVersionConfig multiVersionConfig;

  /** The config of mounted concept */
  private final MountedConceptConfig mountedConceptConfig;

  /** The encrypt type. */
  private final EncryptTypeEnum encryptTypeEnum;

  /** The group that property belongs to , since every event must have */
  private final PropertyGroupEnum propertyGroup;

  /** The index type on the property. */
  private IndexTypeEnum indexType;

  /** The id of constraint defined on property. */
  private final Long constraintId;

  /** The logic rule defined on property. */
  private final RuleCode ruleCode;

  /** Ontology type, property or relation. */
  private final SPGOntologyEnum ontologyEnum;

  public SimpleProperty(
      BasicInfo<PredicateIdentifier> basicInfo,
      OntologyId subjectTypeId,
      OntologyId objectTypeId,
      SPGTypeEnum objectTypeEnum,
      MultiVersionConfig multiVersionConfig,
      MountedConceptConfig mountedConceptConfig,
      EncryptTypeEnum encryptTypeEnum,
      PropertyGroupEnum propertyGroup,
      IndexTypeEnum indexType,
      Long constraintId,
      RuleCode ruleCode,
      SPGOntologyEnum ontologyEnum) {
    this.basicInfo = basicInfo;
    this.subjectTypeId = subjectTypeId;
    this.objectTypeId = objectTypeId;
    this.objectTypeEnum = objectTypeEnum;
    this.multiVersionConfig = multiVersionConfig;
    this.mountedConceptConfig = mountedConceptConfig;
    this.encryptTypeEnum = encryptTypeEnum;
    this.propertyGroup = propertyGroup;
    this.indexType = indexType;
    this.constraintId = constraintId;
    this.ruleCode = ruleCode;
    this.ontologyEnum = ontologyEnum;
  }

  public BasicInfo<PredicateIdentifier> getBasicInfo() {
    return basicInfo;
  }

  public OntologyId getSubjectTypeId() {
    return subjectTypeId;
  }

  public OntologyId getObjectTypeId() {
    return objectTypeId;
  }

  public MultiVersionConfig getMultiVersionConfig() {
    return multiVersionConfig;
  }

  public MountedConceptConfig getMountedConceptConfig() {
    return mountedConceptConfig;
  }

  public EncryptTypeEnum getEncryptTypeEnum() {
    return encryptTypeEnum;
  }

  public PropertyGroupEnum getPropertyGroup() {
    return propertyGroup;
  }

  public IndexTypeEnum getIndexType() {
    return indexType;
  }

  public Long getConstraintId() {
    return constraintId;
  }

  public RuleCode getRuleCode() {
    return ruleCode;
  }

  public SPGOntologyEnum getOntologyType() {
    return ontologyEnum;
  }

  public SPGTypeEnum getObjectTypeEnum() {
    return objectTypeEnum;
  }
}
