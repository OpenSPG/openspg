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

package com.antgroup.openspg.builder.core.physical.invoker.concept.impl;

import com.antgroup.openspg.api.facade.client.ConceptFacade;
import com.antgroup.openspg.api.facade.dto.schema.request.ConceptRequest;
import com.antgroup.openspg.api.http.client.HttpConceptFacade;
import com.antgroup.openspg.core.spgbuilder.engine.physical.invoker.concept.ConceptService;
import com.antgroup.openspg.core.spgbuilder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.BaseSPGRecord;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.semantic.SystemPredicateEnum;
import com.antgroup.openspg.core.spgschema.model.type.ConceptList;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeRef;

public class ConceptServiceImpl implements ConceptService {

  private final ConceptFacade conceptFacade = new HttpConceptFacade();

  @Override
  public ConceptList query(BaseSPGRecord spgRecord) {
    if (!(spgRecord instanceof BaseAdvancedRecord)) {
      return null;
    }

    BaseAdvancedRecord advancedRecord = (BaseAdvancedRecord) spgRecord;
    Property belongToProperty =
        advancedRecord.getSpgType().getPredicateProperty(SystemPredicateEnum.BELONG_TO);
    if (belongToProperty == null) {
      return null;
    }

    SPGTypeRef objectTypeRef = belongToProperty.getObjectTypeRef();
    if (!objectTypeRef.isConceptType()) {
      throw new IllegalStateException();
    }
    return conceptFacade
        .queryConcept(new ConceptRequest().setConceptTypeName(objectTypeRef.getName()))
        .getDataThrowsIfNull(objectTypeRef.getName());
  }
}
