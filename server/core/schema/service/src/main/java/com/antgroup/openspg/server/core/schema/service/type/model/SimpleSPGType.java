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

import com.antgroup.openspg.core.schema.model.BaseOntology;
import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.OntologyId;
import com.antgroup.openspg.core.schema.model.SchemaExtInfo;
import com.antgroup.openspg.core.schema.model.alter.AlterOperationEnum;
import com.antgroup.openspg.core.schema.model.constraint.BaseConstraintItem;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.ConceptLayerConfig;
import com.antgroup.openspg.core.schema.model.type.ConceptTaxonomicConfig;
import com.antgroup.openspg.core.schema.model.type.MultiVersionConfig;
import com.antgroup.openspg.core.schema.model.type.ParentTypeInfo;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import com.antgroup.openspg.core.schema.model.type.VisibleScopeEnum;
import java.util.List;

/** Simple spg type without property types and relation types. */
public class SimpleSPGType extends BaseOntology {

  private static final long serialVersionUID = -1672705361330698624L;

  /** The basic information of type, such as name, Chinese name, description, creator etc. */
  private BasicInfo<SPGTypeIdentifier> basicInfo;

  /** The information of inherit schema type. */
  private ParentTypeInfo parentTypeInfo;

  /**
   * The schema type category.
   *
   * @see SPGTypeEnum
   */
  private SPGTypeEnum spgTypeEnum;

  /** The visible scope of schema type. */
  private VisibleScopeEnum visibleScope;

  /** The operator configuration of spg type, such as link operator, extract operator etc. */
  private List<OperatorConfig> operatorConfigs;

  /** The relation to describe child concept to its parent concept. */
  private ConceptLayerConfig conceptLayerConfig;

  /** The config about taxonomic concept. */
  private ConceptTaxonomicConfig conceptTaxonomicConfig;

  /** The multi version config. */
  private MultiVersionConfig conceptMultiVersionConfig;

  /** If the standardType is spreadable, we can find source node by the standard type node */
  private Boolean spreadable;

  /** The constraint is used to normalize property value that mounted to the StandardType. */
  private List<BaseConstraintItem> constraintItems;

  public SimpleSPGType(OntologyId ontologyId) {
    this.setOntologyId(ontologyId);
  }

  public SimpleSPGType(
      Long projectId,
      OntologyId ontologyId,
      AlterOperationEnum alterOperationEnum,
      SchemaExtInfo schemaExtInfo,
      BasicInfo<SPGTypeIdentifier> basicInfo,
      ParentTypeInfo parentTypeInfo,
      SPGTypeEnum spgTypeEnum,
      VisibleScopeEnum visibleScope,
      List<OperatorConfig> operatorConfigs) {
    this(
        projectId,
        ontologyId,
        alterOperationEnum,
        schemaExtInfo,
        basicInfo,
        parentTypeInfo,
        spgTypeEnum,
        visibleScope,
        operatorConfigs,
        null,
        null,
        null,
        null,
        null);
  }

  public SimpleSPGType(
      Long projectId,
      OntologyId ontologyId,
      AlterOperationEnum alterOperationEnum,
      SchemaExtInfo schemaExtInfo,
      BasicInfo<SPGTypeIdentifier> basicInfo,
      ParentTypeInfo parentTypeInfo,
      SPGTypeEnum spgTypeEnum,
      VisibleScopeEnum visibleScope,
      List<OperatorConfig> operatorConfigs,
      ConceptLayerConfig conceptLayerConfig,
      ConceptTaxonomicConfig conceptTaxonomicConfig,
      MultiVersionConfig multiVersionConfig,
      Boolean spreadable,
      List<BaseConstraintItem> constraintItems) {
    this.setProjectId(projectId);
    this.setOntologyId(ontologyId);
    this.setAlterOperation(alterOperationEnum);
    this.setExtInfo(schemaExtInfo);
    this.basicInfo = basicInfo;
    this.parentTypeInfo = parentTypeInfo;
    this.spgTypeEnum = spgTypeEnum;
    this.visibleScope = visibleScope;
    this.operatorConfigs = operatorConfigs;
    this.conceptLayerConfig = conceptLayerConfig;
    this.conceptTaxonomicConfig = conceptTaxonomicConfig;
    this.conceptMultiVersionConfig = multiVersionConfig;
    this.spreadable = spreadable;
    this.constraintItems = constraintItems;
  }

  public BasicInfo<SPGTypeIdentifier> getBasicInfo() {
    return basicInfo;
  }

  public ParentTypeInfo getParentTypeInfo() {
    return parentTypeInfo;
  }

  public SPGTypeEnum getSpgTypeEnum() {
    return spgTypeEnum;
  }

  public VisibleScopeEnum getVisibleScope() {
    return visibleScope;
  }

  public List<OperatorConfig> getOperatorConfigs() {
    return operatorConfigs;
  }

  public ConceptLayerConfig getConceptLayerConfig() {
    return conceptLayerConfig;
  }

  public ConceptTaxonomicConfig getConceptTaxonomicConfig() {
    return conceptTaxonomicConfig;
  }

  public MultiVersionConfig getConceptMultiVersionConfig() {
    return conceptMultiVersionConfig;
  }

  public Boolean getSpreadable() {
    return spreadable;
  }

  public List<BaseConstraintItem> getConstraintItems() {
    return constraintItems;
  }

  public SPGTypeIdentifier getSpgTypeIdentifier() {
    return basicInfo.getName();
  }

  /**
   * Get reference of the spg type.
   *
   * @return reference object
   */
  public SPGTypeRef toRef() {
    SPGTypeRef spgTypeRef = new SPGTypeRef(this.getBasicInfo(), this.getSpgTypeEnum());
    spgTypeRef.setProjectId(getProjectId());
    spgTypeRef.setOntologyId(getOntologyId());
    return spgTypeRef;
  }

  /**
   * If the spg type is a BasicType or a StandardType
   *
   * @return true or false
   */
  public boolean isBasicType() {
    return SPGTypeEnum.BASIC_TYPE.equals(spgTypeEnum);
  }
}
