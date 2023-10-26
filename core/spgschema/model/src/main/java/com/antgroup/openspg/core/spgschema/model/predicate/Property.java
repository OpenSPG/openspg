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

import com.antgroup.openspg.core.spgschema.model.BaseSpoTriple;
import com.antgroup.openspg.core.spgschema.model.BasicInfo;
import com.antgroup.openspg.core.spgschema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTripleIdentifier;
import com.antgroup.openspg.core.spgschema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeRef;
import com.antgroup.openspg.core.spgschema.model.type.WithBasicInfo;

/**
 * Class definition of property<br>
 * <p>
 * Property is generally defined on a unary object and is used to describe information about entity portrait features.
 * In the RDF knowledge framework, Property is an SPO triplet structure, and under the SPG framework, Property is not
 * only triplet structure, but also can define extended information such as constraints and sub properties. The object
 * type of an property can not only be a basic type, but also a semantic type such as standard type, concept type or
 * entity type etc. if the object is a semantic type, the property can be used as a relation. Property of a spg type can
 * be inherited from its parent class or defined by itself, ensuring that the property name of an entity type is unique.
 * The value of a property can be dynamically calculated through a KGDSL script, rather than being imported during
 * knowledge processing. We call this property a logical property.
 * </p>
 *
 * <p>
 * Usually the instance of property is called PropertyRecord, and the schema of property is called Property.
 * </p>
 */
public class Property extends BaseSpoTriple
    implements WithBasicInfo<PredicateIdentifier>, WithPropertyAdvancedConfig {

    private static final long serialVersionUID = -1591197068550534180L;

    /**
     * Reference of the SPG type as the subject.
     */
    private SPGTypeRef subjectTypeRef;

    /**
     * Basic information of the property.
     */
    private final BasicInfo<PredicateIdentifier> basicInfo;

    /**
     * Reference of the SPG type as the object.
     */
    private SPGTypeRef objectTypeRef;

    /**
     * If the property is inherited from the parent type.
     */
    private final Boolean inherited;

    /**
     * Advanced configuration of the property.
     */
    private final PropertyAdvancedConfig advancedConfig;

    public Property(
        BasicInfo<PredicateIdentifier> basicInfo,
        SPGTypeRef subjectTypeRef,
        SPGTypeRef objectTypeRef,
        Boolean inherited,
        PropertyAdvancedConfig advancedConfig) {
        this.basicInfo = basicInfo;
        this.subjectTypeRef = subjectTypeRef;
        this.objectTypeRef = objectTypeRef;
        this.inherited = inherited;
        this.advancedConfig = advancedConfig;
    }

    @Override
    public BasicInfo<PredicateIdentifier> getBasicInfo() {
        return basicInfo;
    }

    public SPGTypeRef getSubjectTypeRef() {
        return subjectTypeRef;
    }

    public SPGTypeRef getObjectTypeRef() {
        return objectTypeRef;
    }

    @Override
    public PropertyAdvancedConfig getAdvancedConfig() {
        return advancedConfig;
    }

    public Boolean getInherited() {
        return inherited;
    }

    public void setSubjectTypeRef(SPGTypeRef subjectTypeRef) {
        this.subjectTypeRef = subjectTypeRef;
    }

    public void setObjectTypeRef(SPGTypeRef objectTypeRef) {
        this.objectTypeRef = objectTypeRef;
    }

    public SPGTripleIdentifier getSpgTripleName() {
        return new SPGTripleIdentifier(
            subjectTypeRef.getBasicInfo().getName(),
            basicInfo.getName(),
            objectTypeRef.getBasicInfo().getName()
        );
    }

    public PropertyRef toRef() {
        return new PropertyRef(
            subjectTypeRef,
            basicInfo,
            objectTypeRef,
            SPGOntologyEnum.PROPERTY,
            this.getProjectId(),
            this.getOntologyId()
        );
    }
}
