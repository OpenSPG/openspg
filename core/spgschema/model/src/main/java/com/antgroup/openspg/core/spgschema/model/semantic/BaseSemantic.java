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

package com.antgroup.openspg.core.spgschema.model.semantic;

import com.antgroup.openspg.core.spgschema.model.BaseSpoTriple;

/**
 * The semantics defined on ontology<br>
 * <p>There are not only properties or relations of portraits/facts between ontologies, but also semantic connections,
 * such as equivalence classes, parent-child classes, mutualOf or inverseOf relations, and so on. These semantic
 * relationships can be regarded as the consensus and axioms that exist in reality. Under the SPG framework, we define
 * ontology semantics ({@link BaseSemantic}) to describe these consensus semantics.<br>
 * <br>
 * Ontology semantics can be divided into the following categories according to the different objects of action：
 * <ul>
 *     <li>Represents the semantics between two schema types: such as inheritance semantics between two entity types, {@code <classA subClassOf classB>}</li>
 *     <li>Represents the semantics between two instances: for example, the same semantics of two instances, {@code <instanceA sameAs instanceB>}</li>
 *     <li>Represents the semantics between two properties: for example, property A is a standard property of property B，{@code <propertyA normalizedOf propertyB>}</li>
 *     <li>Represents the semantics between two relations: such as reciprocal semantics between two relations, {@code <relationA inverseOf relationB>}</li>
 * </ul>
 * </p>
 */
public abstract class BaseSemantic extends BaseSpoTriple {

    private static final long serialVersionUID = 1361012430866891880L;

    /**
     * ontology type.
     */
    protected SPGOntologyEnum ontologyEnum;

    public BaseSemantic() {
    }

    public BaseSemantic(SPGOntologyEnum ontologyEnum) {
        this.ontologyEnum = ontologyEnum;
    }

    public SPGOntologyEnum getOntologyType() {
        return ontologyEnum;
    }

    public void setOntologyType(SPGOntologyEnum ontologyEnum) {
        this.ontologyEnum = ontologyEnum;
    }
}
