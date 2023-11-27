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

package com.antgroup.openspg.server.schema.core.model.predicate;

import com.antgroup.openspg.core.spgschema.model.BaseSpoTriple;
import com.antgroup.openspg.core.spgschema.model.BasicInfo;
import com.antgroup.openspg.core.spgschema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.spgschema.model.type.BasicType;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeRef;
import com.antgroup.openspg.core.spgschema.model.type.WithBasicInfo;

/**
 * Class definition of extended information of {@code Property} or {@code Relation} <br>
 *
 * <p>Sub property is attribute of {@link Property} or {@link Relation}. it's used to describe the
 * extended information of {@link Property} or {@link Relation}.<br>
 * the unique key of sub property is {@code <entityTypeId, propertyName>} or {@code <sid,
 * propertyName, versionId>}.<br>
 * <br>
 * To restrict sub property is only used to describe the extended info，the subject of sub property
 * must be the key of property or relation, and the object must be a {@link BasicType}.<br>
 *
 * <p>Usually the instance of sub property is called SubPropertyRecord, and the schema of sub
 * property is called SubProperty.
 */
public class SubProperty extends BaseSpoTriple
    implements WithBasicInfo<PredicateIdentifier>, WithPropertyAdvancedConfig {

  private static final long serialVersionUID = 8295658206130371033L;

  /** Basic information of the sub property. */
  private final BasicInfo<PredicateIdentifier> basicInfo;

  /** Reference to the property or the relation as the subject. */
  private transient PropertyRef subjectTypeRef;

  /** Reference to the SPG type as the object. */
  private final SPGTypeRef objectTypeRef;

  /** Advanced configurations of the sub property. */
  private final PropertyAdvancedConfig advancedConfig;

  public SubProperty(
      BasicInfo<PredicateIdentifier> basicInfo,
      PropertyRef subjectTypeRef,
      SPGTypeRef objectTypeRef,
      PropertyAdvancedConfig advancedConfig) {
    this.basicInfo = basicInfo;
    this.subjectTypeRef = subjectTypeRef;
    this.objectTypeRef = objectTypeRef;
    this.advancedConfig = advancedConfig;
  }

  @Override
  public BasicInfo<PredicateIdentifier> getBasicInfo() {
    return basicInfo;
  }

  public PropertyRef getSubjectTypeRef() {
    return subjectTypeRef;
  }

  public SPGTypeRef getObjectTypeRef() {
    return objectTypeRef;
  }

  @Override
  public PropertyAdvancedConfig getAdvancedConfig() {
    return advancedConfig;
  }

  public void setSubjectTypeRef(PropertyRef subjectTypeRef) {
    this.subjectTypeRef = subjectTypeRef;
  }
}
