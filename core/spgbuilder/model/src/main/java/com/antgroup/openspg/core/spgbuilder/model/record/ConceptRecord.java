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

package com.antgroup.openspg.core.spgbuilder.model.record;

import com.antgroup.openspg.core.spgschema.model.identifier.ConceptIdentifier;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.type.BaseSPGType;
import com.antgroup.openspg.core.spgschema.model.type.ConceptType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConceptRecord extends BaseAdvancedRecord {

  private static final String NAME = "name";

  private final ConceptType conceptType;

  private final ConceptIdentifier conceptName;

  private final List<SPGPropertyRecord> properties;

  public ConceptRecord(
      ConceptType conceptType, ConceptIdentifier conceptName, List<SPGPropertyRecord> properties) {
    super(SPGRecordTypeEnum.CONCEPT);
    this.conceptName = conceptName;
    this.conceptType = conceptType;
    this.properties = (properties == null ? new ArrayList<>(5) : properties);
    addNameProperty();
  }

  private void addNameProperty() {
    Property property = conceptType.getPropertyMap().get(NAME);
    if (property != null) {
      // 把原来的属性中的name删除，补上从id里面抽取出来的name
      properties.removeIf(record -> NAME.equals(record.getName()));
      SPGPropertyValue spgPropertyValue = new SPGPropertyValue(conceptName.getName());
      this.properties.add(new SPGPropertyRecord(property, spgPropertyValue));
    }
  }

  @Override
  public BaseSPGType getSpgType() {
    return conceptType;
  }

  @Override
  public List<BasePropertyRecord> getProperties() {
    return Collections.unmodifiableList(properties);
  }

  @Override
  public String getId() {
    return conceptName.getId();
  }

  @Override
  public List<SPGPropertyRecord> getSpgProperties() {
    return Collections.unmodifiableList(properties);
  }

  @Override
  public void addSpgProperties(SPGPropertyRecord record) {
    properties.add(record);
  }

  public ConceptType getConceptType() {
    return conceptType;
  }

  public ConceptIdentifier getConceptName() {
    return conceptName;
  }
}
