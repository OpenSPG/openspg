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

package com.antgroup.openspg.core.spgschema.model.predicate;

import com.antgroup.openspg.core.spgschema.model.BaseOntology;
import com.antgroup.openspg.core.spgschema.model.BasicInfo;
import com.antgroup.openspg.core.spgschema.model.OntologyId;
import com.antgroup.openspg.core.spgschema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTripleIdentifier;
import com.antgroup.openspg.core.spgschema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeRef;
import com.antgroup.openspg.core.spgschema.model.type.WithBasicInfo;

/** Reference of the property or the relation. */
public class PropertyRef extends BaseOntology implements WithBasicInfo<PredicateIdentifier> {

  private static final long serialVersionUID = -7716635374508722755L;

  /** Reference of the SPG type as the subject. */
  private final SPGTypeRef subjectTypeRef;

  /** Basic information of the property. */
  private final BasicInfo<PredicateIdentifier> basicInfo;

  /** Reference of SPG type as the object. */
  private final SPGTypeRef objectTypeRef;

  /** Ontology type, property or relation. */
  private final SPGOntologyEnum ontologyEnum;

  public PropertyRef(
      SPGTypeRef subjectTypeRef,
      BasicInfo<PredicateIdentifier> basicInfo,
      SPGTypeRef objectTypeRef,
      SPGOntologyEnum ontologyEnum) {
    this.subjectTypeRef = subjectTypeRef;
    this.basicInfo = basicInfo;
    this.objectTypeRef = objectTypeRef;
    this.ontologyEnum = ontologyEnum;
  }

  public PropertyRef(
      SPGTypeRef subjectTypeRef,
      BasicInfo<PredicateIdentifier> basicInfo,
      SPGTypeRef objectTypeRef,
      SPGOntologyEnum ontologyEnum,
      Long projectId,
      OntologyId ontologyId) {
    this(subjectTypeRef, basicInfo, objectTypeRef, ontologyEnum);
    this.setProjectId(projectId);
    this.setOntologyId(ontologyId);
  }

  public SPGTypeRef getSubjectTypeRef() {
    return subjectTypeRef;
  }

  @Override
  public BasicInfo<PredicateIdentifier> getBasicInfo() {
    return basicInfo;
  }

  public SPGTypeRef getObjectTypeRef() {
    return objectTypeRef;
  }

  public SPGOntologyEnum getOntologyType() {
    return ontologyEnum;
  }

  public SPGTripleIdentifier newSpgTripleName() {
    return new SPGTripleIdentifier(
        subjectTypeRef.getBasicInfo().getName(),
        basicInfo.getName(),
        objectTypeRef.getBasicInfo().getName());
  }
}
