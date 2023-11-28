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

package com.antgroup.openspg.schema.model.type;

import com.antgroup.openspg.schema.model.BasicInfo;
import com.antgroup.openspg.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.schema.model.predicate.Property;
import com.antgroup.openspg.schema.model.predicate.Relation;
import java.util.List;

/**
 * Class definition of the Entity. <br>
 *
 * <p>An entity refers to an objective instance that has significant business relevance, such as
 * users, enterprises, merchants, and so on. The entity type provides a definition for the specific
 * type of entity, which typically includes properties describing its characteristics and relations
 * with other entities. When defining entity types, it is essential to specify both the linking
 * operator and the fusing operator.
 */
public class EntityType extends BaseAdvancedType {

  private static final long serialVersionUID = 7548382967010252528L;

  public EntityType(
      BasicInfo<SPGTypeIdentifier> basicInfo,
      ParentTypeInfo parentTypeInfo,
      List<Property> properties,
      List<Relation> relations,
      SPGTypeAdvancedConfig advancedConfig) {
    super(
        basicInfo, parentTypeInfo, SPGTypeEnum.ENTITY_TYPE, properties, relations, advancedConfig);
  }
}
