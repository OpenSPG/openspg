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

package com.antgroup.openspg.core.spgschema.model.type;

import com.antgroup.openspg.common.model.base.BaseValObj;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;

/**
 * Configuration of taxon concepts<br>
 * <br>
 * <p>Different from common-sense concept types such as brand, intent; some concept types are dedicated to
 * classifying a certain entity type, it represents the classification method of a certain entity type. For example,
 * crowd is a taxonomic concept type, it is used to classify the entity type of natural person, such as two conceptual
 * instances of high income crowd and low income crowd<br>
 * <br>
 * When creating a taxonomy concept type, it is required to relate an entity type
 * {@link ConceptTaxonomicConfig#taxonomicTypeIdentifier} for taxonomy, By default, system will add a belongTo relation
 * between the entity type and the taxonomy concept type；<br>
 * <br>
 * We can write a DSL rule on the concept instance of the taxonomy meta-concept, which is used to indicate that the
 * belongTo relation is only established when the instance of the classification object satisfies the DSL rule, that is,
 * the instance belongs to the current concept instance。
 * </p>
 */
public class ConceptTaxonomicConfig extends BaseValObj {

    private static final long serialVersionUID = 7789742974797157736L;

    /**
     * The AdvanceType's key that taxonomic by the conceptType.
     */
    private final SPGTypeIdentifier taxonomicTypeIdentifier;

    public ConceptTaxonomicConfig(SPGTypeIdentifier taxonomicTypeIdentifier) {
        this.taxonomicTypeIdentifier = taxonomicTypeIdentifier;
    }

    public SPGTypeIdentifier getTaxonomicTypeIdentifier() {
        return taxonomicTypeIdentifier;
    }
}
