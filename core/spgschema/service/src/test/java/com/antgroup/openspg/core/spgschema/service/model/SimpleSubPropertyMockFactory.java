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

package com.antgroup.openspg.core.spgschema.service.model;

import com.antgroup.openspg.core.spgschema.model.BasicInfo;
import com.antgroup.openspg.core.spgschema.model.OntologyId;
import com.antgroup.openspg.core.spgschema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.spgschema.service.predicate.model.SimpleSubProperty;


public class SimpleSubPropertyMockFactory {

    public static SimpleSubProperty mock() {
        BasicInfo<PredicateIdentifier> basicInfo = new BasicInfo<>(
            new PredicateIdentifier("confi"), "置信度", "置信度");

        return new SimpleSubProperty(
            basicInfo, new OntologyId(100L), new OntologyId(1L),
            null, null, null,
            false);
    }
}
