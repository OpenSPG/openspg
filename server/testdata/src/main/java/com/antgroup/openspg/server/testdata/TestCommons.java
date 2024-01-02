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

package com.antgroup.openspg.server.testdata;

import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.PropertyAdvancedConfig;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ParentTypeInfo;
import java.util.ArrayList;

public class TestCommons {

  public static final ParentTypeInfo THING =
      new ParentTypeInfo(1L, 1L, SPGTypeIdentifier.parse("THING"), new ArrayList<>());

  public static Property newProperty(String propertyName, String desc, BaseSPGType objectType) {
    return new Property(
        new BasicInfo<>(new PredicateIdentifier(propertyName), desc, desc),
        null,
        objectType.toRef(),
        Boolean.FALSE,
        new PropertyAdvancedConfig());
  }

  public static Relation newRelation(String propertyName, String desc, BaseSPGType objectType) {
    return new Relation(
        new BasicInfo<>(new PredicateIdentifier(propertyName), desc, desc),
        null,
        objectType.toRef(),
        Boolean.FALSE,
        new PropertyAdvancedConfig());
  }
}
