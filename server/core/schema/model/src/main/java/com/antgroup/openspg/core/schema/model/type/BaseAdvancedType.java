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

package com.antgroup.openspg.core.schema.model.type;

import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import java.util.List;

/**
 * The abstract parent class for higher-level types<br>
 *
 * <p>Unlike literal constants, entities in the real world typically possess their own properties or
 * relations that have semantic meaning and can be communicated. For instance, let's consider "Ant
 * Group" as a company. It has properties such as founders, places of registration, and relations
 * such as legal persons and employees. Another example is an Email Address, which is a text string
 * with a specific format. By using the schema type for Email Address, we can determine the pattern
 * of its property value and identify associated users of that Email Address.<br>
 * <br>
 * Advanced types have the following four implementations:
 *
 * <ul>
 *   <li>standard type: {@link StandardType}
 *   <li>entity type: {@link EntityType}
 *   <li>concept type: {@link ConceptType}
 *   <li>event type: {@link EventType}
 * </ul>
 */
public abstract class BaseAdvancedType extends BaseSPGType {

  private static final long serialVersionUID = -50404497281965617L;

  public BaseAdvancedType(
      BasicInfo<SPGTypeIdentifier> basicInfo,
      ParentTypeInfo parentTypeInfo,
      SPGTypeEnum spgTypeEnum,
      List<Property> properties,
      List<Relation> relations,
      SPGTypeAdvancedConfig advancedConfig) {
    super(basicInfo, parentTypeInfo, spgTypeEnum, properties, relations, advancedConfig);
  }
}
