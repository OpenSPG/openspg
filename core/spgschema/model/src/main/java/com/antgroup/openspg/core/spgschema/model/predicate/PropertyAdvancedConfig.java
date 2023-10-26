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

import com.antgroup.openspg.common.model.base.BaseValObj;
import com.antgroup.openspg.core.spgschema.model.constraint.Constraint;
import com.antgroup.openspg.core.spgschema.model.semantic.LogicalRule;
import com.antgroup.openspg.core.spgschema.model.semantic.PredicateSemantic;
import com.antgroup.openspg.core.spgschema.model.type.MultiVersionConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * The advanced configuration of the property, relation or sub property.
 */
public class PropertyAdvancedConfig extends BaseValObj {

    private static final long serialVersionUID = 8988840015119604226L;

    /**
     * The configuration of the multi version
     */
    private MultiVersionConfig multiVersionConfig;

    /**
     * The configuration of the mounted concept
     */
    private MountedConceptConfig mountedConceptConfig;

    /**
     * The encrypt type.
     */
    private EncryptTypeEnum encryptTypeEnum;

    /**
     * The group that the property belongs to , since every event must have
     */
    private PropertyGroupEnum propertyGroup;

    /**
     * The constraint defined on the property.
     */
    private Constraint constraint;

    /**
     * If the property need to configure index in the storage.
     */
    private boolean withIndex = false;

    /**
     * The list of sub property defined on the property.
     */
    private List<SubProperty> subProperties;

    /**
     * The predicate semantic of the property, such as inverseOf、transitive etc.
     */
    private List<PredicateSemantic> semantics;

    /**
     * The logic rule defined on the property.
     */
    private LogicalRule logicalRule;

    public PropertyAdvancedConfig() {
        subProperties = new ArrayList<>();
        semantics = new ArrayList<>();
    }

    public MultiVersionConfig getMultiVersionConfig() {
        return multiVersionConfig;
    }

    public PropertyAdvancedConfig setMultiVersionConfig(MultiVersionConfig multiVersionConfig) {
        this.multiVersionConfig = multiVersionConfig;
        return this;
    }

    public MountedConceptConfig getMountedConceptConfig() {
        return mountedConceptConfig;
    }

    public PropertyAdvancedConfig setMountedConceptConfig(MountedConceptConfig mountedConceptConfig) {
        this.mountedConceptConfig = mountedConceptConfig;
        return this;
    }

    public EncryptTypeEnum getEncryptTypeEnum() {
        return encryptTypeEnum;
    }

    public PropertyAdvancedConfig setEncryptTypeEnum(EncryptTypeEnum encryptTypeEnum) {
        this.encryptTypeEnum = encryptTypeEnum;
        return this;
    }

    public PropertyGroupEnum getPropertyGroup() {
        return propertyGroup;
    }

    public PropertyAdvancedConfig setPropertyGroup(PropertyGroupEnum propertyGroup) {
        this.propertyGroup = propertyGroup;
        return this;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public PropertyAdvancedConfig setConstraint(Constraint constraint) {
        this.constraint = constraint;
        return this;
    }

    public List<SubProperty> getSubProperties() {
        return subProperties;
    }

    public PropertyAdvancedConfig setSubProperties(List<SubProperty> subProperties) {
        this.subProperties = subProperties;
        return this;
    }

    public List<PredicateSemantic> getSemantics() {
        return semantics;
    }

    public PropertyAdvancedConfig setSemantics(List<PredicateSemantic> semantics) {
        this.semantics = semantics;
        return this;
    }

    public LogicalRule getLogicalRule() {
        return logicalRule;
    }

    public PropertyAdvancedConfig setLogicalRule(LogicalRule logicalRule) {
        this.logicalRule = logicalRule;
        return this;
    }

    public boolean isWithIndex() {
        return withIndex;
    }

    public void setWithIndex(boolean withIndex) {
        this.withIndex = withIndex;
    }
}
