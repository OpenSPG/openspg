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

import com.antgroup.openspg.core.spgschema.model.OntologyId;

/**
 * An interface provides some methods to get id information of ontology.
 */
public interface WithOntologyId {

    /**
     * Get id object of ontology
     *
     * @return id object
     */
    OntologyId getOntologyId();

    /**
     * Get numeric unique id
     *
     * @return id
     */
    default Long getUniqueId() {
        return getOntologyId() == null ? null : getOntologyId().getUniqueId();
    }

    /**
     * Get numeric alter id
     *
     * @return id
     */
    default Long getAlterId() {
        return getOntologyId() == null ? null : getOntologyId().getAlterId();
    }
}
